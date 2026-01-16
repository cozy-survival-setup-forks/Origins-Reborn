package com.starshootercity.version;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@SuppressWarnings("unused")
public class MVBlockDamageAbortEvent extends BlockEvent {

    public MVBlockDamageAbortEvent(@NotNull Player player, @NotNull Block block, @NotNull ItemStack itemInHand) {
    }

    @NotNull
    public Player getPlayer() {
        return null;
    }

    @NotNull
    public ItemStack getItemInHand() {
        return null;
    }

    @NotNull
    public HandlerList getHandlers() {
        return null;
    }

    @NotNull
    public static HandlerList getHandlerList() {
        return null;
    }
}
