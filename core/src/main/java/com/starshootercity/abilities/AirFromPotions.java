package com.starshootercity.abilities;

import com.starshootercity.OriginSwapper;
import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.Ability;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class AirFromPotions implements Ability, Listener {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:air_from_potions");
    }

    NamespacedKey dehydrationKey = new NamespacedKey(OriginsReborn.getInstance(), "dehydrating");

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) {
            event.getPlayer().getPersistentDataContainer().set(dehydrationKey, OriginSwapper.BooleanPDT.BOOLEAN, true);
            event.getPlayer().setRemainingAir(Math.min(event.getPlayer().getRemainingAir() + airIncrease, event.getPlayer().getMaximumAir()));
            event.getPlayer().getPersistentDataContainer().set(dehydrationKey, OriginSwapper.BooleanPDT.BOOLEAN, false);
        }
    }

    private int airIncrease;

    @Override
    public void initialize(JavaPlugin plugin) {
        airIncrease = registerConfigOption(plugin, "air_increase", Collections.singletonList("Amount to increase air by when drinking a potion"), ConfigManager.SettingType.INTEGER, 60);
    }
}
