package com.starshootercity.events;

import com.starshootercity.OriginsReborn;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class PlayerLeftClickEvent extends PlayerEvent {

    public PlayerLeftClickEvent(PlayerInteractEvent event) {
    }

    @Nullable
    public Location getInteractionPoint() {
        return null;
    }

    public boolean hasBlock() {
        return false;
    }

    public boolean hasItem() {
        return false;
    }

    @Nullable
    public ItemStack getItem() {
        return null;
    }

    @NotNull
    public Material getMaterial() {
        return null;
    }

    @Nullable
    public Block getClickedBlock() {
        return null;
    }

    @Nullable
    public BlockFace getBlockFace() {
        return null;
    }

    @Override
    @NotNull
    public HandlerList getHandlers() {
        return null;
    }

    public static HandlerList getHandlerList() {
        return null;
    }

    public static class PlayerLeftClickEventListener implements Listener {

        Map<Player, Integer> lastInteractionTickMap = null;

        @EventHandler
        public void onPlayerInteract(PlayerInteractEvent event) {
        }

        @EventHandler
        public void onPlayerDropItem(PlayerDropItemEvent event) {
        }

        @EventHandler
        public void onBlockBreak(BlockBreakEvent event) {
        }
    }
}
