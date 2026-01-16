package com.starshootercity.util;

import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

public class CustomGUI implements InventoryHolder {

    private final CustomInventoryType inventoryType;
    private final Inventory inventory;

    private CustomGUI(CustomInventoryType inventoryType, InventoryType itype, Component title) {
        this.inventory = Bukkit.createInventory(this, itype, title);
        this.inventoryType = inventoryType;
    }

    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public CustomInventoryType getInventoryType() {
        return inventoryType;
    }

    public enum CustomInventoryType{
        SHULKER_INVENTORY
    }

    public static Inventory createInventory(CustomInventoryType inventoryType, InventoryType itype, Component title) {
        return new CustomGUI(inventoryType, itype, title).getInventory();
    }
}
