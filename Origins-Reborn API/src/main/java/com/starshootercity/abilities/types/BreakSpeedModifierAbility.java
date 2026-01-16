package com.starshootercity.abilities.types;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.*;
import com.starshootercity.version.MVAttribute;
import com.starshootercity.version.MVBlockDamageAbortEvent;
import com.starshootercity.util.AbilityRegister;
import com.starshootercity.util.ShortcutUtils;
import com.starshootercity.version.MVEnchantment;
import com.starshootercity.version.MVPotionEffectType;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An ability modifying the break speed in certain scenarios
 */
public interface BreakSpeedModifierAbility extends Ability {

    /**
     * Gets the 'mining context' for a player, allows the game to treat the player as if they are mining under different conditions
     * @param player The player with the ability
     * @return The mining context for the player
     * @see BlockMiningContext
     */
    BlockMiningContext getMiningContext(Player player);

    /**
     * @param player The player with the ability
     * @return Whether the break speed of this player should be modified with the provided mining context
     */
    boolean shouldActivate(Player player);

    /**
     * The mining context of the player
     * @param heldItem The item to act like the player is holding
     * @param slowDigging The Mining Fatigue status effect to act like the player is having
     * @param fastDigging The Haste status effect to act like the player is having
     * @param conduitPower The Conduit Power status effect to act like the player is having
     * @param underwater Whether the mining speed should act like the player is underwater
     * @param aquaAffinity Whether the mining speed should act like the player has Aqua Affinity
     * @param onGround Whether the mining speed should act like the player is on the ground
     */
    record BlockMiningContext(ItemStack heldItem, @Nullable PotionEffect slowDigging, @Nullable PotionEffect fastDigging, @Nullable PotionEffect conduitPower, boolean underwater, boolean aquaAffinity, boolean onGround) {

        public boolean hasDigSpeed() {
            return false;
        }

        public boolean hasDigSlowdown() {
            return false;
        }

        public int getDigSlowdown() {
            return 0;
        }

        public int getDigSpeedAmplification() {
            return 0;
        }
    }

    class BreakSpeedModifierAbilityListener implements Listener {

        Random random = null;

        @EventHandler
        public void onBlockDamage(BlockDamageEvent event) {
        }

        public static float getDestroySpeed(BreakSpeedModifierAbility.BlockMiningContext context, Material blockType) {
            return 0;
        }

        @EventHandler
        public void onBlockDamage(MVBlockDamageAbortEvent event) {
        }

        Map<Player, SavedPotionEffect> storedEffects = null;

        @EventHandler
        public void onServerTickEnd(ServerTickEndEvent event) {
        }
    }

    NamespacedKey key = null;

    class ModifiedBlockBreakEvent extends BlockBreakEvent {

        public ModifiedBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
        }
    }
}
