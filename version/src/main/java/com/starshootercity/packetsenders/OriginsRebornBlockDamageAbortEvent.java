package com.starshootercity.packetsenders;

import com.starshootercity.version.MVBlockDamageAbortEvent;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

@Deprecated(forRemoval = true)
public class OriginsRebornBlockDamageAbortEvent extends MVBlockDamageAbortEvent {
    public OriginsRebornBlockDamageAbortEvent(@NotNull Player player, @NotNull Block block, @NotNull ItemStack itemInHand) {
        super(player, block, itemInHand);
    }
}
