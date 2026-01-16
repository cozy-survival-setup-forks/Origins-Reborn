package com.starshootercity.version;

import org.bukkit.entity.Entity;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.entity.EntityEvent;
import org.jetbrains.annotations.NotNull;

/**
 * An Entity Dismount Event for Origins-Fantasy that works across multiple versions
 */
public class MVEntityDismountEvent extends EntityEvent implements Cancellable {

    public MVEntityDismountEvent(@NotNull Entity what, @NotNull Entity dismounted) {
    }

    public MVEntityDismountEvent(@NotNull Entity what, @NotNull Entity dismounted, boolean isCancellable) {
    }

    @NotNull
    public Entity getDismounted() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {
    }

    public boolean isCancellable() {
        return false;
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
