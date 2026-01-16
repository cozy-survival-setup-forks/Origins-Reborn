package com.starshootercity.util.hooks;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GSitHook {
    public static boolean enabled = false;

    public static List<UUID> sitting = new ArrayList<>();

    public static void Setup(JavaPlugin plugin) {
        Bukkit.getPluginManager().registerEvents(new GSH(), plugin);
        enabled = true;
    }
}
