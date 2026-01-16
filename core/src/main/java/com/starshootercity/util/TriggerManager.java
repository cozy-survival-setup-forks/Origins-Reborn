package com.starshootercity.util;

import com.destroystokyo.paper.event.player.PlayerJumpEvent;
import com.starshootercity.abilities.types.TriggerableAbility;
import com.starshootercity.events.PlayerLeftClickEvent;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.event.player.PlayerToggleSprintEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Predicate;

public class TriggerManager implements Listener {

    private static final TriggerManager instance = new TriggerManager();

    public static TriggerManager getInstance() {
        return instance;
    }

    private final Map<TriggerableAbility.TriggerType, Set<TriggerableAbility.Trigger>> triggerableAbilities = new HashMap<>();

    public void register(TriggerableAbility ability) {
        triggerableAbilities.computeIfAbsent(ability.getTrigger().getType(), k -> new HashSet<>())
                .add(ability.getTrigger());
    }

    public void callTriggerEvent(TriggerEvent event, TriggerableAbility.TriggerType type, @Nullable Cancellable cancellable) {
        for (TriggerableAbility.Trigger trigger : triggerableAbilities.getOrDefault(type, Collections.emptySet())) {
            trigger.run(event);
        }
        if (cancellable != null && event.isCancelled()) cancellable.setCancelled(true);
    }

    @EventHandler
    public void onPlayerLeftClick(PlayerLeftClickEvent event) {
        TriggerEvent triggerEvent = new TriggerEvent(event.getPlayer(), event.getItem(), event.getClickedBlock());
        callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.LEFT_CLICK, null);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!event.getAction().isRightClick()) return;
        TriggerEvent triggerEvent = new TriggerEvent(event.getPlayer(), event.getItem(), event.getClickedBlock());
       callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.RIGHT_CLICK, event);
    }

    @EventHandler
    public void onPlayerSwapHandItems(PlayerSwapHandItemsEvent event) {
        TriggerEvent triggerEvent = new TriggerEvent(event.getPlayer(), event.getMainHandItem(), null);
        callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.OFFHAND_SWAP, event);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player p) {
            if (event.isRightClick()) {
                if (event.getSlotType() == InventoryType.SlotType.ARMOR) {
                    if (event.getSlot() == 38) {
                        TriggerEvent triggerEvent = new TriggerEvent(p, event.getCurrentItem(), null);
                        callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.RIGHT_CLICK_CHESTPLATE, event);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerJump(PlayerJumpEvent event) {
        TriggerEvent triggerEvent = new TriggerEvent(event.getPlayer(), null, null);
        callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.JUMP, event);
    }

    @EventHandler
    public void onPlayerToggleSprint(PlayerToggleSprintEvent event) {
        TriggerEvent triggerEvent = new TriggerEvent(event.getPlayer(), null, null);
        if (event.isSprinting()) {
            callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.SPRINT_ON, event);
        } else callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.SPRINT_OFF, event);
        callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.SPRINT_TOGGLE, event);
    }

    @EventHandler
    public void onPlayerToggleSneak(PlayerToggleSneakEvent event) {
        TriggerEvent triggerEvent = new TriggerEvent(event.getPlayer(), null, null);
        callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.SNEAK_TOGGLE, event);

        if (!event.isSneaking()) {
            callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.SNEAK_OFF, event);
            return;
        }

        callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.SNEAK_ON, event);

        if (lastInteractionTickMap.getOrDefault(event.getPlayer(), 0) - Bukkit.getCurrentTick() > 0) {
            callTriggerEvent(triggerEvent, TriggerableAbility.TriggerType.DOUBLE_TAP_SNEAK, event);
            lastInteractionTickMap.remove(event.getPlayer());
            return;
        }
        lastInteractionTickMap.put(event.getPlayer(), Bukkit.getCurrentTick() + 5);
    }

    private final Map<Player, Integer> lastInteractionTickMap = new HashMap<>();

    public static final class TriggerEvent {
        private final Player player;
        private final @Nullable ItemStack item;
        private final @Nullable Block block;
        private boolean cancelled;

        public TriggerEvent(Player player, @Nullable ItemStack item, @Nullable Block block) {
            this.player = player;
            this.item = item;
            this.block = block;
            this.cancelled = false;
        }

        public void setCancelled(boolean cancelled) {
            this.cancelled = cancelled;
        }

        public boolean isCancelled() {
            return cancelled;
        }

        public boolean hasBlock() {
            return block != null;
        }

        public boolean itemType(Predicate<Material> predicate) {
            if (item != null) return predicate.test(item.getType());
            return predicate.test(player.getInventory().getItemInMainHand().getType());
        }

        public Player player() {
            return player;
        }

        public @Nullable ItemStack item() {
            return item;
        }

        public @Nullable Block block() {
            return block;
        }
    }
}
