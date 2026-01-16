package com.starshootercity;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class FoliaScheduleManager extends AbstractScheduleManager {
    @Override
    public void delayedTask(JavaPlugin plugin, Runnable task, long delay) {
        Bukkit.getGlobalRegionScheduler().runDelayed(plugin, scheduledTask -> task.run(), delay);
    }
}
