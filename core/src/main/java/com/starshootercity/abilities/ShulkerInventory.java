package com.starshootercity.abilities;

import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.TriggerableAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.events.PlayerLeftClickEvent;
import com.starshootercity.util.CustomGUI;
import com.starshootercity.util.ShortcutUtils;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ShulkerInventory implements Listener, VisibleAbility, TriggerableAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:shulker_inventory");
    }

    @Override
    public String description() {
        return "You have access to an additional 9 slots of inventory, which keep the items on death.";
    }

    @Override
    public String title() {
        return "Hoarder";
    }

    private NamespacedKey inventoryKey;
    private List<NamespacedKey> keys;

    @EventHandler
    public void onPlayerLeftClick(PlayerLeftClickEvent event) {
        if (event.hasBlock()) return;
        if (event.hasItem()) return;
        if (ShortcutUtils.isBedrockPlayer(event.getPlayer().getUniqueId())) {
            runForAbility(event.getPlayer(), this::openInventory);
        }
    }

    private boolean dropItemsOnDeath;

    @Override
    public void initialize(JavaPlugin plugin) {
        registerTranslation("container", "Shulker Inventory");
        inventoryKey = new NamespacedKey(plugin, "shulker_inventory_contents");
        keys = new ArrayList<>();

        for (int i = 0; i < 9; i++) {
            keys.add(new NamespacedKey(plugin, String.valueOf(i)));
        }

        dropItemsOnDeath = registerConfigOption(plugin, "drop_on_death", Collections.singletonList("Drop items in the inventory on death (will not change ability description, for that edit the translations.yml file)"), ConfigManager.SettingType.BOOLEAN, false);

        prepareLegacyInventoryData();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!dropItemsOnDeath) return;
        runForAbility(event.getPlayer(), player -> {
            PersistentDataContainer pdc = getShulkerInventory(player);
            for (NamespacedKey key : pdc.getKeys()) {
                byte[] bytes = pdc.get(key, PersistentDataType.BYTE_ARRAY);
                pdc.remove(key);
                if (bytes == null) continue;
                ItemStack item = ItemStack.deserializeBytes(bytes);
                event.getDrops().add(item);
            }
            setShulkerInventory(player, pdc);
        });
    }

    @Override
    public @NotNull Trigger getTrigger() {
        return Trigger.builder(TriggerType.RIGHT_CLICK_CHESTPLATE, this)
                .build(event -> {
                    event.setCancelled(true);
                    openInventory(event.player());
                });
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getWhoClicked() instanceof Player p) {
            saveInventory(p, event.getInventory());
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent event) {
        if (event.getWhoClicked() instanceof Player p) {
            saveInventory(p, event.getInventory());
        }
    }

    public void saveInventory(Player p, Inventory inventory) {
        runForAbility(p, player -> Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> {
            if (inventory.getHolder() instanceof CustomGUI) {
                PersistentDataContainer container = getShulkerInventory(player);
                for (int i = 0; i < 9; i++) {
                    ItemStack item = inventory.getItem(i);
                    if (item == null || item.getType().isAir()) {
                        container.remove(keys.get(i));
                        continue;
                    }
                    container.set(keys.get(i), PersistentDataType.BYTE_ARRAY, item.serializeAsBytes());
                }
                setShulkerInventory(player, container);
            }
        }));
    }

    public void openInventory(Player player) {
        Inventory inventory = CustomGUI.createInventory(CustomGUI.CustomInventoryType.SHULKER_INVENTORY, InventoryType.DISPENSER, Component.text(translate("container")));
        player.openInventory(inventory);
        PersistentDataContainer container = getShulkerInventory(player);
        for (int i = 0; i < 9; i++) {
            if (!container.has(keys.get(i), PersistentDataType.BYTE_ARRAY)) continue;

            byte[] bytes = container.get(keys.get(i), PersistentDataType.BYTE_ARRAY);

            ItemStack item = ItemStack.deserializeBytes(bytes);
            inventory.setItem(i, item);
        }
    }

    public PersistentDataContainer getShulkerInventory(Player player) {
        PersistentDataContainer container = player.getPersistentDataContainer().get(inventoryKey, PersistentDataType.TAG_CONTAINER);
        if (container == null) container = player.getPersistentDataContainer().getAdapterContext().newPersistentDataContainer();
        return container;
    }

    public void setShulkerInventory(Player player, PersistentDataContainer container) {
        player.getPersistentDataContainer().set(inventoryKey, PersistentDataType.TAG_CONTAINER, container);
    }

    // Legacy inventory data
    private static File inventories;
    private static FileConfiguration inventoriesConfig;
    private boolean supportLegacyInventoryFile;

    private void prepareLegacyInventoryData() {
        inventories = new File(OriginsReborn.getInstance().getDataFolder(), "internals/inventories.yml");
        supportLegacyInventoryFile = inventories.exists();
        if (!supportLegacyInventoryFile) return;
        inventoriesConfig = new YamlConfiguration();
        try {
            inventoriesConfig.load(inventories);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws IOException {
        if (!supportLegacyInventoryFile) return;

        if (!inventoriesConfig.contains(event.getPlayer().getUniqueId().toString())) return;
        PersistentDataContainer container = getShulkerInventory(event.getPlayer());
        for (int i = 0; i < 9; i++) {
            String path = "%s.%s".formatted(event.getPlayer().getUniqueId().toString(), i);
            if (!inventoriesConfig.contains(path)) continue;
            ItemStack item = inventoriesConfig.getItemStack(path);
            if (item == null) continue;
            container.set(keys.get(i), PersistentDataType.BYTE_ARRAY, item.serializeAsBytes());
        }
        setShulkerInventory(event.getPlayer(), container);

        inventoriesConfig.set(event.getPlayer().getUniqueId().toString(), null);
        inventoriesConfig.save(inventories);
        if (inventoriesConfig.getKeys(false).isEmpty()) {
            boolean ignored = inventories.delete();
            supportLegacyInventoryFile = false;
            inventoriesConfig = null;
            inventories = null;
        }
    }
}