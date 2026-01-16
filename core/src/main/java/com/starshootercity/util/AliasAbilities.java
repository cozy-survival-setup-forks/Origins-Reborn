package com.starshootercity.util;

import org.bukkit.Bukkit;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.intellij.lang.annotations.Subst;

import java.io.File;
import java.io.IOException;

public class AliasAbilities {
    public static void initialize(JavaPlugin plugin) {
        File file = new File(plugin.getDataFolder(), "alias-abilities.yml");

        if (!file.exists()) {
            boolean ignored = file.mkdirs();
            plugin.saveResource("alias-abilities.yml", false);
        }

        FileConfiguration config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        // Waits until first tick so all abilities are registered
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            for (@Subst("namespace:value") String key : config.getKeys(false)) {
                //AbilityRegister.registerAbility();
                //AbilityRegister.invertedAbilityMap.get()
                //Key.key(key);
            }
        });
    }
}
