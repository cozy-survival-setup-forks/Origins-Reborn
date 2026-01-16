package com.starshootercity;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class AbstractScheduleManager {
    public abstract void delayedTask(JavaPlugin plugin, Runnable task, long delay);

    public final void delayedTask(JavaPlugin plugin, Runnable task) {
        delayedTask(plugin, task, 1);
    }
}
