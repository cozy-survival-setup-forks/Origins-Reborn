package com.starshootercity.abilities;

import com.starshootercity.abilities.types.FlightAllowingAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.commands.FlightToggleCommand;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityToggleGlideEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class Elytra implements FlightAllowingAbility, Listener, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:elytra");
    }

    @Override
    public String description() {
        return "You have Elytra wings without needing to equip any.";
    }

    @Override
    public String title() {
        return "Winged";
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onEntityToggleGlide(EntityToggleGlideEvent event) {
        runForAbility(event.getEntity(), player -> {
            if (!player.isOnGround() && !event.isGliding()) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public boolean canFly(Player player) {
        return true;
    }

    @Override
    public float getFlightSpeed(Player player) {
        return player.getFlySpeed();
    }

    @EventHandler
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        if (FlightToggleCommand.canFly(event.getPlayer())) return;
        runForAbility(event.getPlayer(), player -> {
            if (event.isFlying()) {
                event.setCancelled(true);
                if (player.isGliding() && disableDisengage) return;
                player.setGliding(!player.isGliding());
            }
        });
    }

    @Override
    public @NotNull TriState getFlyingFallDamage(Player player) {
        return TriState.TRUE;
    }

    private boolean disableDisengage;

    @Override
    public void initialize(JavaPlugin plugin) {
        disableDisengage = registerConfigOption(plugin, "disable_disengage_trigger", Collections.singletonList("Disable the ability to disengage the elytra by double tapping again"), ConfigManager.SettingType.BOOLEAN, true);
    }
}
