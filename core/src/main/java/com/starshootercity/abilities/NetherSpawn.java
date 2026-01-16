package com.starshootercity.abilities;

import com.starshootercity.abilities.types.DefaultSpawnAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NetherSpawn implements DefaultSpawnAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:nether_spawn");
    }

    @Override
    public @Nullable World getWorld() {
        String nether = ConfigManager.getConfigValue(ConfigManager.Option.NETHER_DIMENSION);
        World world = Bukkit.getWorld(nether);
        if (world == null) {
            for (World w : Bukkit.getWorlds()) {
                if (w.getEnvironment().equals(World.Environment.NETHER)) world = w;
            }
        }
        return world;
    }

    @Override
    public String description() {
        return "Your natural spawn will be in the Nether.";
    }

    @Override
    public String title() {
        return "Nether Inhabitant";
    }
}
