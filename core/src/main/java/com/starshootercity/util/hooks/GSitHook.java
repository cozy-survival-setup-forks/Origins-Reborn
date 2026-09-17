package com.starshootercity.util.hooks;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class GSitHook {
    public static boolean enabled = false;

    public static List<UUID> sitting = new ArrayList<>();

    public static void Setup(JavaPlugin plugin) {
        registerPoseEvent(plugin, "dev.geco.gsit.api.event.PlayerPoseEvent", true);
        registerPoseEvent(plugin, "dev.geco.gsit.api.event.PlayerStopPoseEvent", false);
        enabled = true;
    }

    @SuppressWarnings("unchecked")
    private static void registerPoseEvent(JavaPlugin plugin, String className, boolean seated) {
        try {
            Class<?> eventType = Class.forName(className);
            if (!Event.class.isAssignableFrom(eventType)) return;
            Listener listener = new Listener() {};
            Bukkit.getPluginManager().registerEvent((Class<? extends Event>) eventType, listener, EventPriority.MONITOR, (ignored, event) -> {
                try {
                    Object player = event.getClass().getMethod("getPlayer").invoke(event);
                    if (player instanceof Player target) {
                        if (seated) sitting.add(target.getUniqueId());
                        else sitting.remove(target.getUniqueId());
                    }
                } catch (ReflectiveOperationException ignoredException) {
                }
            }, plugin, true);
        } catch (ClassNotFoundException ignored) {
        }
    }
}
