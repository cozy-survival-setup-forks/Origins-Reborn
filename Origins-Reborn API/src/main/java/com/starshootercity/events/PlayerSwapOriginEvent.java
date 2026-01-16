package com.starshootercity.events;

import com.starshootercity.Origin;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.player.PlayerEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class PlayerSwapOriginEvent extends PlayerEvent implements Cancellable {

    public PlayerSwapOriginEvent(@NotNull Player who, SwapReason reason, boolean resetPlayer, Origin oldOrigin, Origin newOrigin) {
    }

    @Nullable
    public Origin getNewOrigin() {
        return null;
    }

    @Nullable
    public Origin getOldOrigin() {
        return null;
    }

    public void setNewOrigin(Origin newOrigin) {
    }

    public SwapReason getReason() {
        return null;
    }

    public boolean isResetPlayer() {
        return false;
    }

    public void setResetPlayer(boolean resetPlayer) {
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return null;
    }

    public static HandlerList getHandlerList() {
        return null;
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public void setCancelled(boolean cancel) {
    }

    public enum SwapReason {

        /**
         * Swapped origin with the /origin swap command
         */
        COMMAND("command"),
        /**
         * Swapped origin using an Orb of Origin
         */
        ORB_OF_ORIGIN("orb"),
        /**
         * Swapped origin due to having died and respawned
         */
        DIED("died"),
        /**
         * Swapped origin due to not having one yet
         */
        INITIAL("initial"),
        /**
         * Swapped origin due to another plugin
         */
        PLUGIN("plugin"),
        /**
         * Unknown swap reason
         */
        UNKNOWN("unknown");

        public String getReason() {
            return null;
        }

        public static SwapReason get(String reason) {
            return null;
        }

        SwapReason(String reason) {
        }
    }
}
