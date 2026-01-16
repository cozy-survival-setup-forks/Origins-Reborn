package com.starshootercity.version;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An Entity Mount Event for Origins-Fantasy that works across multiple versions
 */
public class MVEntityMountEvent extends EntityEvent implements Cancellable {

    public MVEntityMountEvent(@NotNull Entity what, @NotNull Entity mount) {
    }

    @NotNull
    public Entity getMount() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {
    }

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return null;
    }

    @SuppressWarnings("unused")
    @NotNull
    public static HandlerList getHandlerList() {
        return null;
    }
}
