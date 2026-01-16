package com.starshootercity.abilities;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.abilities.types.DependencyAbility;
import com.starshootercity.abilities.types.TriggerableAbility;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class Phantomize implements DependencyAbility, Listener, TriggerableAbility {
    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (isEnabled(player) && player.getFoodLevel() <= foodLevel) {
                phantomizedPlayers.put(player.getUniqueId(), false);
                PhantomizeToggleEvent phantomizeToggleEvent = new PhantomizeToggleEvent(player, false);
                phantomizeToggleEvent.callEvent();
            }
        }
    }

    private int foodLevel;
    private boolean disableOnDamage;

    @Override
    public void initialize(JavaPlugin plugin) {
        foodLevel = registerConfigOption(plugin, "minimum_food_level", Collections.singletonList("The food level at which you can no longer enter Phantom Form"), ConfigManager.SettingType.INTEGER, 6);
        disableOnDamage = registerConfigOption(plugin, "disable_on_damage", Collections.singletonList("Whether to disable Phantom Form when taking damage"), ConfigManager.SettingType.BOOLEAN, false);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (!disableOnDamage) return;
        if (event.getCause().equals(EntityDamageEvent.DamageCause.SUFFOCATION)) return;
        runForAbility(event.getEntity(), player -> {
            if (phantomizedPlayers.getOrDefault(player.getUniqueId(), false)) {
                setPhasing(player, false);
            }
        });
    }

    private void setPhasing(Player player, boolean enabling) {
        phantomizedPlayers.put(player.getUniqueId(), enabling);
        PhantomizeToggleEvent phantomizeToggleEvent = new PhantomizeToggleEvent(player, enabling);
        phantomizeToggleEvent.callEvent();
    }

    private final Map<UUID, Boolean> phantomizedPlayers = new HashMap<>();

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:phantomize");
    }

    @Override
    public boolean isEnabled(Player player) {
        return phantomizedPlayers.getOrDefault(player.getUniqueId(), false);
    }

    @Override
    public @NotNull Trigger getTrigger() {
        return Trigger.builder(
                TriggerType.LEFT_CLICK,
                this
        ).addConditions(Condition.EMPTY_HAND, Condition.NO_BLOCK)
                .build(event -> {
                    Player player = event.player();
                    if (player.getFoodLevel() <= 6) return;
                    boolean enabling = !phantomizedPlayers.getOrDefault(player.getUniqueId(), false);
                    setPhasing(player, enabling);
                });
    }

    @SuppressWarnings("unused")
    public static class PhantomizeToggleEvent extends PlayerEvent {
        private final boolean enabling;

        public PhantomizeToggleEvent(Player who, boolean enabling) {
            super(who);
            this.enabling = enabling;
        }

        public boolean isEnabling() {
            return enabling;
        }

        private static final HandlerList HANDLERS = new HandlerList();

        @Override
        public @NotNull HandlerList getHandlers() {
            return HANDLERS;
        }

        public static HandlerList getHandlerList() {
            return HANDLERS;
        }
    }
}
