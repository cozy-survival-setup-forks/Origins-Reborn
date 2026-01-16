package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.Ability;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class DamageFromPotions implements Ability, Listener {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:damage_from_potions");
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        runForAbility(event.getPlayer(), player -> {
            if (event.getItem().getType() == Material.POTION) {
                OriginsReborn.getMVE().dealFreezeDamage(player, damageAmount);
            }
        });
    }

    private int damageAmount;

    @Override
    public void initialize(JavaPlugin plugin) {
        damageAmount = registerConfigOption(plugin, "damage_amount", Collections.singletonList("Amount of damage the player should take when drinking a potion"), ConfigManager.SettingType.INTEGER, 2);
    }
}
