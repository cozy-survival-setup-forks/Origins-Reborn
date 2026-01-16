package com.starshootercity;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class ScheduleManager extends AbstractScheduleManager {
    @Override
    public void delayedTask(JavaPlugin plugin, Runnable task, long delay) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(plugin, task, delay);
    }
}
