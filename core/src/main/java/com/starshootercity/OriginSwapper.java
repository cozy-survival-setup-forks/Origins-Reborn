package com.starshootercity;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.abilities.types.Ability;
import com.starshootercity.abilities.types.AttributeModifierAbility;
import com.starshootercity.abilities.types.DefaultSpawnAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.commands.OriginCommand;
import com.starshootercity.events.PlayerSwapOriginEvent;
import com.starshootercity.geysermc.GeyserSwapper;
import com.starshootercity.util.AbilityRegister;
import com.starshootercity.util.Pair;
import com.starshootercity.util.VaultHook;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.version.MVAccessor;
import com.starshootercity.version.MVAttribute;
import fr.xephi.authme.api.v3.AuthMeApi;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.*;
import com.starshootercity.util.ShortcutUtils;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityPotionEffectEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class OriginSwapper implements Listener {
    private final static NamespacedKey displayKey = new NamespacedKey(OriginsReborn.getInstance(), "displayed-item");
    private final static NamespacedKey layerKey = new NamespacedKey(OriginsReborn.getInstance(), "layer");
    private final static NamespacedKey confirmKey = new NamespacedKey(OriginsReborn.getInstance(), "confirm-select");
    private final static NamespacedKey costsCurrencyKey = new NamespacedKey(OriginsReborn.getInstance(), "costs-currency");
    private final static NamespacedKey originKey = new NamespacedKey(OriginsReborn.getInstance(), "origin-name");
    private final static NamespacedKey swapTypeKey = new NamespacedKey(OriginsReborn.getInstance(), "swap-type");
    private final static NamespacedKey pageSetKey = new NamespacedKey(OriginsReborn.getInstance(), "page-set");
    private final static NamespacedKey pageScrollKey = new NamespacedKey(OriginsReborn.getInstance(), "page-scroll");
    private final static NamespacedKey costKey = new NamespacedKey(OriginsReborn.getInstance(), "enable-cost");
    private final static NamespacedKey displayOnlyKey = new NamespacedKey(OriginsReborn.getInstance(), "display-only");
    private final static NamespacedKey closeKey = new NamespacedKey(OriginsReborn.getInstance(), "close");
    public final static NamespacedKey awaitingOriginChangeKey = new NamespacedKey(OriginsReborn.getInstance(), "awaiting_origin_change");
    private final static Random random = new Random();

    private final static Map<UUID, Map<String, Origin>> origins = new HashMap<>();

    public static String getInverse(String string) {
        StringBuilder result = new StringBuilder();
        for (char c : string.toCharArray()) {
            result.append(getInverse(c));
        }
        return result.toString();
    }

    public static void openOriginSwapper(Player player, PlayerSwapOriginEvent.SwapReason reason, int slot, int scrollAmount, String layer) {
        openOriginSwapper(player, reason, slot, scrollAmount, false, false, layer);
    }

    public static void openOriginSwapper(Player player, PlayerSwapOriginEvent.SwapReason reason, int slot, int scrollAmount, boolean cost, String layer) {
        openOriginSwapper(player, reason, slot, scrollAmount, cost, false, layer);
    }

    public static void openOriginSwapper(Player player, PlayerSwapOriginEvent.SwapReason reason, int slot, int scrollAmount, boolean cost, boolean displayOnly, String layer) {
        if (shouldDisallowSelection(player, reason)) return;
        lastSwapReasons.put(player, reason);
        boolean enableRandom = OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.random-option.enabled");
        if (GeyserSwapper.checkBedrockSwap(player, reason, cost, displayOnly, layer)) {
            if (AddonLoader.getOrigins(layer).isEmpty()) return;
            List<Origin> origins = new ArrayList<>(AddonLoader.getOrigins(layer));
            if (!displayOnly) origins.removeIf(origin -> origin.isUnchoosable(player) || origin.hasPermission() && !player.hasPermission(origin.getPermission()));
            while (slot > origins.size() || slot == origins.size() && !enableRandom) {
                slot -= origins.size() + (enableRandom ? 1 : 0);
            }
            while (slot < 0) {
                slot += origins.size() + (enableRandom ? 1 : 0);
            }
            ItemStack icon;
            String name;
            String nameForDisplay;
            char impact;
            int amount = ConfigManager.getConfigValue(ConfigManager.Option.SWAP_COMMAND_VAULT_DEFAULT_COST);
            LineData data;
            if (slot == origins.size()) {
                List<String> excludedOrigins = ConfigManager.getConfigValue(ConfigManager.Option.ORIGIN_SELECTION_RANDOM_OPTION_EXCLUDE);
                List<String> excludedOriginNames = new ArrayList<>();
                for (String s : excludedOrigins) {
                    Origin origin = AddonLoader.getOriginByFilename(s);
                    if (origin == null) continue;
                    excludedOriginNames.add(origin.getName());
                }
                icon = OrbOfOrigin.orb.clone();
                name = "random";
                nameForDisplay = Translator.translate(randomName);
                impact = '\uE002';
                StringBuilder names = new StringBuilder("%s\n\n".formatted(Translator.translate(randomDescription)));
                for (Origin origin : AddonLoader.getOrigins(layer)) {
                    if (origin.isUnchoosable(player, true)) continue;
                    if (excludedOriginNames.contains(origin.getName())) continue;
                    names.append(origin.getName()).append("\n");
                }
                data = new LineData(LineData.makeLineFor(
                        names.toString(),
                        LineData.LineComponent.LineType.DESCRIPTION
                ));
            } else {
                Origin origin = origins.get(slot);
                icon = origin.getIcon();
                name = origin.getName();
                nameForDisplay = origin.getNameForDisplay();
                impact = origin.getImpact();
                data = new LineData(origin);
                if (origin.getCost() != null) {
                    amount = origin.getCost();
                }
            }
            StringBuilder compressedName = new StringBuilder("\uF001");
            for (char c : nameForDisplay.toCharArray()) {
                if (c == ' ') c = '\uEFFF';
                compressedName.append(c);
                compressedName.append('\uF000');
            }
            Component background = applyFont(ShortcutUtils.getColored(ConfigManager.getConfigValue(ConfigManager.Option.ORIGIN_SELECTION_SCREEN_TITLE_BACKGROUND)), Key.key("minecraft:default"));
            Component component = applyFont(Component.text("\uF000\uE000\uF001\uE001\uF002" + impact),
                    Key.key("minecraft:origin_selector"))
                    .color(NamedTextColor.WHITE)
                    .append(background)
                    .append(applyFont(Component.text(compressedName.toString()),
                            Key.key("minecraft:origin_title_text")
                    ).color(NamedTextColor.WHITE))
                    .append(applyFont(Component.text(getInverse(nameForDisplay) + "\uF000"),
                            Key.key("minecraft:reverse_text")
                    ).color(NamedTextColor.WHITE));
            for (Component c : data.getLines(scrollAmount)) {
                component = component.append(c);
            }
            Component prefix = applyFont(ShortcutUtils.getColored(ConfigManager.getConfigValue(ConfigManager.Option.ORIGIN_SELECTION_SCREEN_TITLE_PREFIX)), Key.key("minecraft:default"));
            Component suffix = applyFont(ShortcutUtils.getColored(ConfigManager.getConfigValue(ConfigManager.Option.ORIGIN_SELECTION_SCREEN_TITLE_SUFFIX)), Key.key("minecraft:default"));
            Inventory swapperInventory = Bukkit.createInventory(null, 54,
                    prefix.append(component).append(suffix)
            );
            ItemMeta meta = icon.getItemMeta();
            meta.getPersistentDataContainer().set(originKey, PersistentDataType.STRING, name.toLowerCase());
            if (meta instanceof SkullMeta skullMeta) {
                skullMeta.setOwningPlayer(player);
            }
            meta.getPersistentDataContainer().set(displayKey, BooleanPDT.BOOLEAN, true);
            meta.getPersistentDataContainer().set(swapTypeKey, PersistentDataType.STRING, reason.getReason());
            meta.getPersistentDataContainer().set(layerKey, PersistentDataType.STRING, layer);
            icon.setItemMeta(meta);
            swapperInventory.setItem(1, icon);
            ItemStack confirm = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemStack invisibleConfirm = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemMeta confirmMeta = confirm.getItemMeta();
            ItemMeta invisibleConfirmMeta = invisibleConfirm.getItemMeta();

            confirmMeta.displayName(Component.text(Translator.translate(confirmGUI))
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            confirmMeta = OriginsReborn.getMVE().setCustomModelData(confirmMeta, 5);
            if (!displayOnly) confirmMeta.getPersistentDataContainer().set(confirmKey, BooleanPDT.BOOLEAN, true);
            else confirmMeta.getPersistentDataContainer().set(closeKey, BooleanPDT.BOOLEAN, true);

            invisibleConfirmMeta.displayName(Component.text(Translator.translate(confirmGUI))
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            invisibleConfirmMeta = OriginsReborn.getMVE().setCustomModelData(invisibleConfirmMeta, 6);
            if (!displayOnly) invisibleConfirmMeta.getPersistentDataContainer().set(confirmKey, PersistentDataType.INTEGER, 1);
            else invisibleConfirmMeta.getPersistentDataContainer().set(closeKey, BooleanPDT.BOOLEAN, true);

            if (OriginsReborn.getInstance().isVaultEnabled() && amount != 0 && cost && !player.hasPermission(OriginsReborn.getInstance().getConfig().getString("swap-command.vault.bypass-permission", "originsreborn.costbypass"))) {
                boolean go = true;
                if (ConfigManager.getConfigValue(ConfigManager.Option.SWAP_COMMAND_VAULT_PERMANENT_PURCHASES)) {
                    go = !getUsedOriginFileConfiguration().getStringList(player.getUniqueId().toString()).contains(name.toLowerCase());
                }
                if (go) {
                    String symbol = OriginsReborn.getInstance().getConfig().getString("swap-command.vault.currency-symbol", "$");
                    List<Component> costsCurrency = List.of(
                            Component.text((VaultHook.has(player, amount) ? "This will cost %s%s of your balance!" : "You need at least %s%s in your balance to do this!").formatted(symbol, amount))
                    );
                    confirmMeta.lore(costsCurrency);
                    invisibleConfirmMeta.lore(costsCurrency);
                    confirmMeta.getPersistentDataContainer().set(costsCurrencyKey, PersistentDataType.INTEGER, amount);
                    invisibleConfirmMeta.getPersistentDataContainer().set(costsCurrencyKey, PersistentDataType.INTEGER, amount);
                }
            }

            ItemStack up = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemStack down = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
            ItemMeta upMeta = up.getItemMeta();
            ItemMeta downMeta = down.getItemMeta();

            int scrollSize = OriginsReborn.getInstance().getConfig().getInt("origin-selection.scroll-amount", 1);

            upMeta.displayName(Component.text(Translator.translate(upGUI))
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            if (scrollAmount != 0) {
                upMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot);
                upMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, Math.max(scrollAmount - scrollSize, 0));
            }
            upMeta = OriginsReborn.getMVE().setCustomModelData(upMeta, 3 + (scrollAmount == 0 ? 6 : 0));
            upMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
            upMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, displayOnly);


            int size = data.lines.size() - scrollAmount - 6;
            boolean canGoDown = size > 0;

            downMeta.displayName(Component.text(Translator.translate(downGUI))
                    .color(NamedTextColor.WHITE)
                    .decoration(TextDecoration.ITALIC, false));
            if (canGoDown) {
                downMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot);
                downMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, Math.min(scrollAmount + scrollSize, scrollAmount + size));
            }
            downMeta = OriginsReborn.getMVE().setCustomModelData(downMeta, 4 + (!canGoDown ? 6 : 0));
            downMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
            downMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, displayOnly);


            up.setItemMeta(upMeta);
            down.setItemMeta(downMeta);
            swapperInventory.setItem(52, up);
            swapperInventory.setItem(53, down);


            if (!displayOnly) {
                ItemStack left = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                ItemStack right = new ItemStack(Material.LIGHT_GRAY_STAINED_GLASS_PANE);
                ItemMeta leftMeta = left.getItemMeta();
                ItemMeta rightMeta = right.getItemMeta();

                leftMeta.displayName(Component.text(Translator.translate(prevGUI))
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
                leftMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot - 1);
                leftMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, 0);
                leftMeta = OriginsReborn.getMVE().setCustomModelData(leftMeta, 1);
                leftMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
                leftMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, false);

                rightMeta.displayName(Component.text(Translator.translate(nextGUI))
                        .color(NamedTextColor.WHITE)
                        .decoration(TextDecoration.ITALIC, false));
                rightMeta.getPersistentDataContainer().set(pageSetKey, PersistentDataType.INTEGER, slot + 1);
                rightMeta.getPersistentDataContainer().set(pageScrollKey, PersistentDataType.INTEGER, 0);
                rightMeta = OriginsReborn.getMVE().setCustomModelData(rightMeta, 2);
                rightMeta.getPersistentDataContainer().set(costKey, BooleanPDT.BOOLEAN, cost);
                rightMeta.getPersistentDataContainer().set(displayOnlyKey, BooleanPDT.BOOLEAN, false);


                left.setItemMeta(leftMeta);
                right.setItemMeta(rightMeta);

                swapperInventory.setItem(47, left);
                swapperInventory.setItem(51, right);
            }

            confirm.setItemMeta(confirmMeta);
            invisibleConfirm.setItemMeta(invisibleConfirmMeta);
            swapperInventory.setItem(48, confirm);
            swapperInventory.setItem(49, invisibleConfirm);
            swapperInventory.setItem(50, invisibleConfirm);
            player.openInventory(swapperInventory);
        }
    }

    public static Component applyFont(Component component, Key font) {
        return OriginsReborn.getMVE().applyFont(component, font);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        ItemStack item = ShortcutUtils.getTopInventory(event.getWhoClicked()).getItem(1);
        if (item != null) {
            if (item.getItemMeta() == null) return;
            if (item.getItemMeta().getPersistentDataContainer().has(displayKey, BooleanPDT.BOOLEAN)) {
                event.setCancelled(true);
            }
            String layer = item.getItemMeta().getPersistentDataContainer().getOrDefault(layerKey, PersistentDataType.STRING, "origin");
            if (event.getWhoClicked() instanceof Player player) {
                ItemStack currentItem = event.getCurrentItem();
                if (currentItem == null || currentItem.getItemMeta() == null) return;
                Integer page = currentItem.getItemMeta().getPersistentDataContainer().get(pageSetKey, PersistentDataType.INTEGER);
                if (page != null) {
                    boolean cost = currentItem.getItemMeta().getPersistentDataContainer().getOrDefault(costKey, BooleanPDT.BOOLEAN, false);
                    boolean allowUnchoosable = currentItem.getItemMeta().getPersistentDataContainer().getOrDefault(displayOnlyKey, BooleanPDT.BOOLEAN, false);
                    Integer scroll = currentItem.getItemMeta().getPersistentDataContainer().get(pageScrollKey, PersistentDataType.INTEGER);
                    if (scroll == null) return;
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                    openOriginSwapper(player, getReason(item), page, scroll, cost, allowUnchoosable, layer);
                }
                if (currentItem.getItemMeta().getPersistentDataContainer().has(confirmKey, PersistentDataType.INTEGER)) {
                    if (ConfigManager.getConfigValue(ConfigManager.Option.ORIGIN_SELECTION_CONFIRM_SELECTION)) {
                        if (currentItem.getItemMeta().getPersistentDataContainer().getOrDefault(confirmKey, PersistentDataType.INTEGER, 1) == 1) {
                            ItemMeta meta = currentItem.getItemMeta();
                            meta.getPersistentDataContainer().set(confirmKey, PersistentDataType.INTEGER, 2);
                            currentItem.setItemMeta(meta);
                            player.sendMessage(Component.text(Translator.translate(confirmSwapGUI)).color(NamedTextColor.RED));
                            return;
                        }
                    }
                    String originName = item.getItemMeta().getPersistentDataContainer().get(originKey, PersistentDataType.STRING);
                    if (originName == null) return;
                    Origin origin;
                    if (originName.equalsIgnoreCase("random")) {
                        List<String> excludedOrigins = ConfigManager.getConfigValue(ConfigManager.Option.ORIGIN_SELECTION_RANDOM_OPTION_EXCLUDE);
                        List<Origin> origins = new ArrayList<>(AddonLoader.getOrigins(layer));
                        origins.removeIf(origin1 -> excludedOrigins.contains(origin1.getName()));
                        origins.removeIf(origin1 -> origin1.isUnchoosable(player, true));
                        if (origins.isEmpty()) {
                            origin = AddonLoader.getFirstOrigin(layer);
                        } else {
                            origin = origins.get(random.nextInt(origins.size()));
                        }
                    } else {
                        origin = AddonLoader.getOrigin(originName);
                    }

                    int amount = OriginsReborn.getInstance().getConfig().getInt("swap-command.vault.cost", 1000);
                    if (!player.hasPermission(OriginsReborn.getInstance().getConfig().getString("swap-command.vault.bypass-permission", "originsreborn.costbypass")) && currentItem.getItemMeta().getPersistentDataContainer().has(costsCurrencyKey, PersistentDataType.INTEGER)) {
                        boolean bypass = false;
                        if (ConfigManager.getConfigValue(ConfigManager.Option.SWAP_COMMAND_VAULT_PERMANENT_PURCHASES)) {
                            if (usedOriginFileConfiguration.getStringList(event.getWhoClicked().getUniqueId().toString()).contains(origin.getName().toLowerCase())) bypass = true;
                        }
                        if (!bypass) {
                            amount = currentItem.getItemMeta().getPersistentDataContainer().getOrDefault(costsCurrencyKey, PersistentDataType.INTEGER, amount);
                            if (!VaultHook.has(player, amount)) {
                                return;
                            } else {
                                VaultHook.withdraw(player, amount);
                            }
                        }
                    }
                    PlayerSwapOriginEvent.SwapReason reason = getReason(item);
                    player.playSound(player.getLocation(), Sound.UI_BUTTON_CLICK, SoundCategory.MASTER, 1, 1);
                    player.closeInventory();

                    if (reason == PlayerSwapOriginEvent.SwapReason.ORB_OF_ORIGIN) orbCooldown.put(player, System.currentTimeMillis());
                    boolean resetPlayer = shouldResetPlayer(reason);
                    setOrigin(player, origin, reason, resetPlayer, layer);
                    if (reason == PlayerSwapOriginEvent.SwapReason.COMMAND) {
                        OriginsReborn.getCooldowns().setCooldown(player, OriginCommand.key, ConfigManager.getConfigValue(ConfigManager.Option.SWAP_COMMAND_COOLDOWN));
                    }
                } else if (currentItem.getItemMeta().getPersistentDataContainer().has(closeKey, BooleanPDT.BOOLEAN)) event.getWhoClicked().closeInventory();
            }
        }
    }

    public static boolean shouldResetPlayer(PlayerSwapOriginEvent.SwapReason reason) {
        return switch (reason) {
            case COMMAND -> OriginsReborn.getInstance().getConfig().getBoolean("swap-command.reset-player");
            case ORB_OF_ORIGIN -> OriginsReborn.getInstance().getConfig().getBoolean("orb-of-origin.reset-player");
            default -> false;
        };
    }

    public static int getWidth(String s) {
        int result = 0;
        for (char c : s.toCharArray()) {
            result += Translator.getWidth(c);
        }
        return result;
    }

    public static String getInverse(char c) {
        return switch (Translator.getWidth(c)) {
            case 0 -> "";
            case 2 -> "\uF001";
            case 3 -> "\uF002";
            case 4 -> "\uF003";
            case 5 -> "\uF004";
            case 6 -> "\uF005";
            case 7 -> "\uF006";
            case 8 -> "\uF007";
            case 9 -> "\uF008";
            case 10 -> "\uF009";
            case 11 -> "\uF008\uF001";
            case 12 -> "\uF009\uF001";
            case 13 -> "\uF009\uF002";
            case 14 -> "\uF009\uF003";
            case 15 -> "\uF009\uF004";
            case 16 -> "\uF009\uF005";
            case 17 -> "\uF009\uF006";
            default -> throw new IllegalStateException("Unexpected value: " + c);
        };
    }

    public static Map<Player, Long> orbCooldown = new HashMap<>();

    public static void resetPlayer(Player player, boolean full) {

        if (!Bukkit.isPrimaryThread()) {

            Bukkit.getLogger().severe("Origins-Reborn detected an attempt to reset a player on a non-primary thread! Report this and the thread dump below to the Plugin Authors.");
            Thread.dumpStack();

            Bukkit.getScheduler().runTask(OriginsReborn.getInstance(), () -> { // Safely handles the reset.
                resetPlayer(player, full);
            });

            return;
        }

        resetAttributes(player);
        player.closeInventory();
        OriginsReborn.getMVE().setWorldBorderOverlay(player, false);
        player.setCooldown(Material.SHIELD, 0);
        player.setAllowFlight(false);
        player.setFlying(false);
        for (Player otherPlayer : Bukkit.getOnlinePlayers()) {
            AbilityRegister.updateEntity(player, otherPlayer);
        }
        for (PotionEffect effect : player.getActivePotionEffects()) {
            if (effect.getAmplifier() == -1 || ShortcutUtils.isInfinite(effect)) player.removePotionEffect(effect.getType());
        }
        if (!full) return;
        player.getInventory().clear();
        player.getEnderChest().clear();
        player.setSaturation(5);
        player.setFallDistance(0);
        player.setRemainingAir(player.getMaximumAir());
        player.setFoodLevel(20);
        player.setFireTicks(0);
        player.setHealth(getMaxHealth(player));
        for (PotionEffect effect : player.getActivePotionEffects()) {
            player.removePotionEffect(effect.getType());
        }
        World world = getRespawnWorld(getOrigins(player));
        player.teleport(world.getSpawnLocation());
        OriginsReborn.getMVE().resetRespawnLocation(player);
    }

    public static @NotNull World getRespawnWorld(@NotNull List<Origin> origin) {
        List<Ability> abilities = new ArrayList<>();
        for (Origin o : origin) abilities.addAll(o.getAbilities());
        for (Ability ability : abilities) {
            if (ability instanceof DefaultSpawnAbility defaultSpawnAbility) {
                World world = defaultSpawnAbility.getWorld();
                if (world != null) return world;
            }
        }
        String overworld = OriginsReborn.getInstance().getConfig().getString("worlds.world");
        if (overworld == null) {
            overworld = "world";
            OriginsReborn.getInstance().getConfig().set("worlds.world", "world");
            OriginsReborn.getInstance().saveConfig();
        }
        World world = Bukkit.getWorld(overworld);
        if (world == null) return Bukkit.getWorlds().get(0);
        return world;
    }

    public static double getMaxHealth(Player player) {
        applyAttributeChanges(player);
        AttributeInstance instance = player.getAttribute(MVAttribute.MAX_HEALTH.get());
        if (instance == null) return 20;
        return instance.getValue();
    }

    public static void applyAttributeChanges(Player player) {
        for (Pair<AttributeModifierAbility, NamespacedKey> abilityKeyPair : AbilityRegister.attributeModifierAbilities) {
            AttributeModifierAbility ability = abilityKeyPair.left();

            AttributeInstance instance;
            try {
                instance = player.getAttribute(ability.getAttribute());
            } catch (IllegalArgumentException e) {
                continue;
            }
            if (instance == null) continue;

            if (ability.hasAbility(player)) {
                AttributeModifier modifier = OriginsReborn.getMVE().getAttributeModifier(instance, abilityKeyPair.right());
                if (modifier != null) {
                    if (modifier.getAmount() == ability.getTotalAmount(player)) {
                        continue;
                    } else instance.removeModifier(modifier);
                }
                OriginsReborn.getMVE().addAttributeModifier(instance, abilityKeyPair.right(), ability.getRegisteredKey().asString(), ability.getTotalAmount(player), ability.getActualOperation());
            } else {
                AttributeModifier am = OriginsReborn.getMVE().getAttributeModifier(instance, abilityKeyPair.right());
                if (am != null) instance.removeModifier(am);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        loadOrigins(event.getPlayer());
        resetAttributes(event.getPlayer());
        lastJoinedTick.put(event.getPlayer(), Bukkit.getCurrentTick());
        for (String layer : AddonLoader.layers) {
            if (ShortcutUtils.getTopInventory(event.getPlayer()).getType() == InventoryType.CHEST) {
                continue;
            }
            Origin origin = getOrigin(event.getPlayer(), layer);
            if (origin != null) {
                if (origin.getTeam() == null) continue;
                origin.getTeam().addPlayer(event.getPlayer());
            } else {
                if (event.getPlayer().getPersistentDataContainer().has(awaitingOriginChangeKey)) return;

                Origin def = AddonLoader.getDefaultOrigin(layer);
                if (def != null) {
                    setOrigin(event.getPlayer(), def, PlayerSwapOriginEvent.SwapReason.INITIAL, false, layer);
                } else if (OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.randomise.%s".formatted(layer))) {
                    selectRandomOrigin(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, layer);
                } else if (ShortcutUtils.isBedrockPlayer(event.getPlayer().getUniqueId())) {
                    Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> GeyserSwapper.openOriginSwapper(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, false, false, layer), OriginsReborn.getInstance().getConfig().getInt("geyser.join-form-delay", 20));
                } else {
                    openOriginSwapper(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, 0, 0, layer);
                }
            }
        }
    }

    public static void resetAttributes(Player player) {
        final double[] health = {player.getHealth()};
        for (MVAttribute attribute : MVAttribute.values()) {
            if (attribute.get() == null) continue;
            AttributeInstance instance = player.getAttribute(attribute.get());
            if (instance == null) continue;
            for (AttributeModifier modifier : instance.getModifiers()) {
                if (!MVAccessor.get().isCustomAttribute(modifier)) continue;
                instance.removeModifier(modifier);
            }
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> {
            AttributeInstance mh = player.getAttribute(MVAttribute.MAX_HEALTH.get());
            if (mh == null) return;
            double maxHealth = mh.getValue();
            health[0] = Math.min(maxHealth, health[0]);
            player.setHealth(health[0]);
        }, 10);
    }

    private static final Map<Player, PlayerSwapOriginEvent.SwapReason> lastSwapReasons = new HashMap<>();

    private static final Map<Player, Integer> lastJoinedTick = new HashMap<>();

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player player = event.getPlayer();
        if (shouldDisallowSelection(player, lastSwapReasons.getOrDefault(player, PlayerSwapOriginEvent.SwapReason.INITIAL))) {
            AbilityRegister.FlyData fd = AbilityRegister.flyData(player, true);
            if (fd.canFly() != player.getAllowFlight()) player.setAllowFlight(fd.canFly());
            if (fd.canFly() && fd.forceFly()) player.setFlying(true);
            AbilityRegister.updateFlight(player, true);
            resetAttributes(player);
        }
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        if (event.getTickNumber() % ConfigManager.getConfigValue(ConfigManager.Option.REFRESH_RATE) != 0) return;
        for (Player player : Bukkit.getOnlinePlayers()) {
            int delay = OriginsReborn.getInstance().getConfig().getInt("origin-selection.delay-before-required", 0);
            if (!lastJoinedTick.containsKey(player)) lastJoinedTick.put(player, event.getTickNumber());
            if (Bukkit.getCurrentTick() - delay < lastJoinedTick.get(player)) continue;
            if (shouldDisallowSelection(player, lastSwapReasons.getOrDefault(player, PlayerSwapOriginEvent.SwapReason.INITIAL))) {
                continue;
            }
            if (!ConfigManager.getConfigValue(ConfigManager.Option.DISABLE_FLIGHT_STUFF)) {
                AbilityRegister.FlyData fd = AbilityRegister.flyData(player, false);
                if (fd.canFly() != player.getAllowFlight()) player.setAllowFlight(fd.canFly());
                if (fd.canFly() && fd.forceFly()) player.setFlying(true);
                AbilityRegister.updateFlight(player, false);
            }
            boolean invis = AbilityRegister.isInvisible(player, invisible.computeIfAbsent(player, p -> p.hasPotionEffect(PotionEffectType.INVISIBILITY)));
            if (invis != player.isInvisible()) player.setInvisible(invis);
            applyAttributeChanges(player);
            String layer = AddonLoader.getFirstUnselectedLayer(player);
            if (layer == null) continue;
            if (ShortcutUtils.getTopInventory(player).getType() != InventoryType.CHEST) {
                if (!OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.randomise.%s".formatted(layer)) && !ShortcutUtils.isBedrockPlayer(player.getUniqueId())) {
                    openOriginSwapper(player, lastSwapReasons.getOrDefault(player, PlayerSwapOriginEvent.SwapReason.INITIAL), 0, 0, layer);
                }
            }
        }
    }

    private static final Map<Player, Boolean> invisible = new HashMap<>();

    @EventHandler
    public void onEntityPotionEffect(EntityPotionEffectEvent event) {
        if (!event.getModifiedType().equals(PotionEffectType.INVISIBILITY)) return;
        if (!(event.getEntity() instanceof Player player)) return;
        invisible.put(player, event.getNewEffect() != null);
    }

    @EventHandler
    public void onPlayerDropItem(PlayerDropItemEvent event) {
        if (hasNotSelectedAllOrigins(event.getPlayer())) event.setCancelled(true);
    }

    public static boolean shouldDisallowSelection(Player player, PlayerSwapOriginEvent.SwapReason reason) {
        try {
            if (!AuthMeApi.getInstance().isAuthenticated(player)) return true;
        } catch (NoClassDefFoundError ignored) {}
        String worldId = player.getWorld().getName();
        return !AddonLoader.shouldOpenSwapMenu(player, reason) || OriginsReborn.getInstance().getConfig().getStringList("worlds.disabled-worlds").contains(worldId);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.getEntity() instanceof Player player) {
            if (invulnerableMode.equalsIgnoreCase("INITIAL") && hasNotSelectedAllOrigins(player)) event.setCancelled(true);
            else if (invulnerableMode.equalsIgnoreCase("ON")) {
                ItemStack item = ShortcutUtils.getTopInventory(player).getItem(1);
                if (item != null && item.getItemMeta() != null) {
                    if (item.getItemMeta().getPersistentDataContainer().has(originKey, PersistentDataType.STRING)) event.setCancelled(true);
                }
            }
        }
    }

    public boolean hasNotSelectedAllOrigins(Player player) {
        for (String layer : AddonLoader.layers) {
            if (getOrigin(player, layer) == null) return true;
        }
        return false;
    }

    @EventHandler
    public void onPlayerSwapOrigin(PlayerSwapOriginEvent event) {
        if (event.getNewOrigin() == null) return;
        if (!OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.auto-spawn-teleport")) return;
        if (event.getReason() == PlayerSwapOriginEvent.SwapReason.INITIAL || event.getReason() == PlayerSwapOriginEvent.SwapReason.DIED) {
            Location loc = OriginsReborn.getMVE().getRespawnLocation(event.getPlayer());
            event.getPlayer().teleport(Objects.requireNonNullElseGet(loc, () -> getRespawnWorld(Collections.singletonList(event.getNewOrigin())).getSpawnLocation()));
        }
    }

    public static void selectRandomOrigin(Player player, PlayerSwapOriginEvent.SwapReason reason, String layer) {
        Origin origin = AddonLoader.getRandomOrigin(layer);
        setOrigin(player, origin, reason, shouldResetPlayer(reason), layer);
        openOriginSwapper(player, reason, AddonLoader.getOrigins(layer).indexOf(origin), 0, false, true, layer);
    }

    private final Map<Player, Set<PlayerRespawnEvent.RespawnFlag>> lastRespawnReasons = new HashMap<>();

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent event) {
        if (OriginsReborn.getMVE().getRespawnLocation(event.getPlayer()) == null) {
            World world = getRespawnWorld(getOrigins(event.getPlayer()));
            event.setRespawnLocation(world.getSpawnLocation());
        }

        lastRespawnReasons.put(event.getPlayer(), event.getRespawnFlags());
    }

    @EventHandler
    public void onPlayerPostRespawn(PlayerPostRespawnEvent event) {
        if (lastRespawnReasons.get(event.getPlayer()).contains(PlayerRespawnEvent.RespawnFlag.END_PORTAL)) return;
        FileConfiguration config = OriginsReborn.getInstance().getConfig();
        if (config.getBoolean("origin-selection.death-origin-change")) {
            for (String layer : AddonLoader.layers) {
                setOrigin(event.getPlayer(), null, PlayerSwapOriginEvent.SwapReason.DIED, false, layer);
                if (OriginsReborn.getInstance().getConfig().getBoolean("origin-selection.randomise.%s".formatted(layer))) {
                    selectRandomOrigin(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, layer);
                } else openOriginSwapper(event.getPlayer(), PlayerSwapOriginEvent.SwapReason.INITIAL, 0, 0, layer);
            }
        }

        resetAttributes(event.getPlayer());
        applyAttributeChanges(event.getPlayer());
    }

    public PlayerSwapOriginEvent.SwapReason getReason(ItemStack icon) {
        return PlayerSwapOriginEvent.SwapReason.get(icon.getItemMeta().getPersistentDataContainer().get(swapTypeKey, PersistentDataType.STRING));
    }

    public static @Nullable Origin getOrigin(Player player, String layer) {

        if (!origins.containsKey(player.getUniqueId())) {

            if (!Bukkit.isPrimaryThread()) {
                return null;
            }

            loadOrigins(player);
        }
        return origins.get(player.getUniqueId()).get(layer);
    }

    public static @Nullable Origin getStoredOrigin(UUID player, String layer) {
        String oldOrigin = originFileConfiguration.getString(player.toString(), "null");
        if (!oldOrigin.equals("null") && layer.equals("origin")) {
            if (!oldOrigin.contains("MemorySection")) {
                originFileConfiguration.set(player + "." + layer, oldOrigin);
                saveOrigins();
            }
        }
        String name = originFileConfiguration.getString(player + "." + layer, "null");
        return AddonLoader.getOrigin(name);
    }

    public static void loadOrigins(Player player) {
        player.getPersistentDataContainer().remove(originKey);

        Map<String, Origin> playerOrigins = new HashMap<>();
        origins.put(player.getUniqueId(), playerOrigins);

        for (String layer : AddonLoader.layers) {
            Origin origin = getStoredOrigin(player.getUniqueId(), layer);
            if (origin == null) {
                if (player.getPersistentDataContainer().has(awaitingOriginChangeKey)) continue;
                if (AddonLoader.getDefaultOrigin(layer) != null) {
                    setOrigin(player, AddonLoader.getDefaultOrigin(layer), PlayerSwapOriginEvent.SwapReason.INITIAL, false, layer);
                }
                continue;
            }
            playerOrigins.put(layer, origin);
        }
    }

    public static List<Origin> getOrigins(Player player) {
        List<Origin> origins = new ArrayList<>();
        for (String layer : AddonLoader.layers) {
            Origin o = getOrigin(player, layer);
            if (o != null) origins.add(o);
        }
        return origins;
    }

    public static FileConfiguration getUsedOriginFileConfiguration() {
        return usedOriginFileConfiguration;
    }

    public static void setOrigin(Player player, @Nullable Origin origin, PlayerSwapOriginEvent.SwapReason reason, boolean resetPlayer, String layer) {
        PlayerSwapOriginEvent swapOriginEvent = new PlayerSwapOriginEvent(player, reason, resetPlayer, getOrigin(player, layer), origin);
        if (!swapOriginEvent.callEvent()) return;
        if (swapOriginEvent.getNewOrigin() == null) {
            originFileConfiguration.set(player.getUniqueId() + "." + layer, null);
            saveOrigins();
            resetPlayer(player, swapOriginEvent.isResetPlayer());
            executeCommands(player, swapOriginEvent.getNewOrigin());
            loadOrigins(player);
            return;
        }
        player.getPersistentDataContainer().remove(awaitingOriginChangeKey);
        if (swapOriginEvent.getNewOrigin().getTeam() != null) {
            swapOriginEvent.getNewOrigin().getTeam().addPlayer(player);
        }
        OriginsReborn.getCooldowns().resetCooldowns(player);
        originFileConfiguration.set(player.getUniqueId() + "." + layer, swapOriginEvent.getNewOrigin().getName().toLowerCase());
        saveOrigins();
        List<String> usedOrigins = new ArrayList<>(usedOriginFileConfiguration.getStringList(player.getUniqueId().toString()));
        usedOrigins.add(swapOriginEvent.getNewOrigin().getName().toLowerCase());
        usedOriginFileConfiguration.set(player.getUniqueId().toString(), usedOrigins);
        saveUsedOrigins();
        resetPlayer(player, swapOriginEvent.isResetPlayer());
        executeCommands(player, swapOriginEvent.getNewOrigin());
        loadOrigins(player);
    }

    public static void executeCommands(Player player, Origin origin) {
        if (origin == null) return;
        String name = "default";
        if (OriginsReborn.getInstance().getConfig().contains("commands-on-origin.%s".formatted(name))) {
            for (String s : OriginsReborn.getInstance().getConfig().getStringList("commands-on-origin.%s".formatted(name))) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString()));
            }
        }

        name = origin.getName().replace(" ", "_").toLowerCase();
        if (OriginsReborn.getInstance().getConfig().contains("commands-on-origin.%s".formatted(name))) {
            for (String s : OriginsReborn.getInstance().getConfig().getStringList("commands-on-origin.%s".formatted(name))) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), s.replace("%player%", player.getName()).replace("%uuid%", player.getUniqueId().toString()));
            }
        }
    }

    private static File originFile;
    private static FileConfiguration originFileConfiguration;

    private static File usedOriginFile;
    private static FileConfiguration usedOriginFileConfiguration;

    public static FileConfiguration getOriginFileConfiguration() {
        return originFileConfiguration;
    }

    private final String invulnerableMode;

    private static final String randomName = "random-origin.name";
    private static final String randomDescription = "random-origin.description";
    private static final String nextGUI = "gui.next";
    private static final String prevGUI = "gui.previous";
    private static final String confirmGUI = "gui.confirm";
    private static final String upGUI = "gui.up";
    private static final String downGUI = "gui.down";
    private static final String confirmSwapGUI = "gui.confirm_swap";

    public OriginSwapper() {
        Translator.registerTranslation(randomName, "Random");
        Translator.registerTranslation(randomDescription, "You'll be assigned one of the following:");
        Translator.registerTranslation(nextGUI, "Next Origin");
        Translator.registerTranslation(prevGUI, "Previous Origin");
        Translator.registerTranslation(upGUI, "Up");
        Translator.registerTranslation(downGUI, "Down");
        Translator.registerTranslation(confirmGUI, "Confirm");
        Translator.registerTranslation(confirmSwapGUI, "Press \"Confirm\" again to confirm your selection");

        invulnerableMode = OriginsReborn.getInstance().getConfig().getString("origin-selection.invulnerable-mode", "OFF");

        originFile = new File(OriginsReborn.getInstance().getDataFolder(), "internals/selected-origins.yml");
        if (!originFile.exists()) {
            boolean ignored = originFile.getParentFile().mkdirs();
            OriginsReborn.getInstance().saveResource("internals/selected-origins.yml", false);
        }
        originFileConfiguration = new YamlConfiguration();
        try {
            originFileConfiguration.load(originFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }

        usedOriginFile = new File(OriginsReborn.getInstance().getDataFolder(), "internals/used-origins.yml");
        if (!usedOriginFile.exists()) {
            boolean ignored = usedOriginFile.getParentFile().mkdirs();
            OriginsReborn.getInstance().saveResource("internals/used-origins.yml", false);
        }
        usedOriginFileConfiguration = new YamlConfiguration();
        try {
            usedOriginFileConfiguration.load(usedOriginFile);
        } catch (IOException | InvalidConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveOrigins() {
        new Thread(() -> {
            try {
                originFileConfiguration.save(originFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static void saveUsedOrigins() {
        new Thread(() -> {
            try {
                usedOriginFileConfiguration.save(usedOriginFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public static class LineData {
        public static List<LineComponent> makeLineFor(String text, LineComponent.LineType type) {
            StringBuilder result = new StringBuilder();
            StringBuilder rawResult = new StringBuilder();
            List<LineComponent> list = new ArrayList<>();
            List<String> splitLines = new ArrayList<>(Arrays.stream(text.split("\n", 2)).toList());
            StringBuilder otherPart = new StringBuilder();
            String firstLine = splitLines.remove(0);
            if (firstLine.contains(" ") && getWidth(firstLine) > 140) {
                List<String> split = new ArrayList<>(Arrays.stream(firstLine.split(" ")).toList());
                StringBuilder firstPart = new StringBuilder(split.get(0));
                split.remove(0);
                boolean canAdd = true;
                for (String s : split) {
                    if (canAdd && getWidth(firstPart + " " + s) <= 140) {
                        firstPart.append(" ");
                        firstPart.append(s);
                    } else {
                        canAdd = false;
                        if (!otherPart.isEmpty()) otherPart.append(" ");
                        otherPart.append(s);
                    }
                }
                firstLine = firstPart.toString();
            }
            for (String s : splitLines) {
                if (!otherPart.isEmpty()) otherPart.append("\n");
                otherPart.append(s);
            }
            if (type == LineComponent.LineType.DESCRIPTION) firstLine = '\uF00A' + firstLine;
            for (char c : firstLine.toCharArray()) {
                result.append(c);
                rawResult.append(c == '\uF00A' ? "" : c);
                result.append('\uF000');
            }
            rawResult.append(' ');
            String finalText = firstLine;
            list.add(new LineComponent(
                    Component.text(result.toString().replace(" ", "\uEFFF"))
                            .color(type == LineComponent.LineType.TITLE ? NamedTextColor.WHITE : TextColor.fromHexString("#CACACA"))
                            .append(Component.text(getInverse(finalText))),
                    type,
                    rawResult.toString()
            ));
            if (!otherPart.isEmpty()) {
                list.addAll(makeLineFor(otherPart.toString(), type));
            }
            return list;
        }
        public static class LineComponent {
            public enum LineType {
                TITLE,
                DESCRIPTION
            }
            private final Component component;
            private final LineType type;
            private final String rawText;
            private final boolean empty;

            public boolean isEmpty() {
                return empty;
            }

            public LineType getType() {
                return type;
            }

            public LineComponent(Component component, LineType type, String rawText) {
                this.component = component;
                this.type = type;
                this.rawText = rawText;
                this.empty = false;
            }

            public LineComponent() {
                this.type = LineType.DESCRIPTION;
                this.component = Component.empty();
                this.rawText = "";
                this.empty = true;
            }

            public String getRawText() {
                return rawText;
            }

            public Component getComponent(int lineNumber) {
                @Subst("minecraft:text_line_0") String formatted = "minecraft:%stext_line_%s".formatted(type == LineType.DESCRIPTION ? "" : "title_", lineNumber);
                return applyFont(component, Key.key(formatted));
            }
        }
        private final List<LineComponent> lines;

        public LineData(Origin origin) {
            lines = new ArrayList<>();
            lines.addAll(makeLineFor(origin.getDescription(), LineComponent.LineType.DESCRIPTION));
            List<VisibleAbility> visibleAbilities = origin.getVisibleAbilities();
            int size = visibleAbilities.size();
            int count = 0;
            if (size > 0) lines.add(new LineComponent());
            for (VisibleAbility visibleAbility : visibleAbilities) {
                count++;
                lines.addAll(visibleAbility.getTitle());
                lines.addAll(visibleAbility.getDescription());
                if (count < size) lines.add(new LineComponent());
            }
        }
        public LineData(List<LineComponent> lines) {
            this.lines = lines;
        }

        public List<Component> getLines(int startingPoint) {
            List<Component> resultLines = new ArrayList<>();
            for (int i = startingPoint; i < startingPoint + 6 && i < lines.size(); i++) {
                resultLines.add(lines.get(i).getComponent(i - startingPoint));
            }
            return resultLines;
        }

        public List<LineComponent> getRawLines() {
            return lines;
        }
    }

    public static class BooleanPDT implements PersistentDataType<Byte, Boolean> {
        public static BooleanPDT BOOLEAN = new BooleanPDT();

        @Override
        public @NotNull Class<Byte> getPrimitiveType() {
            return Byte.class;
        }

        @Override
        public @NotNull Class<Boolean> getComplexType() {
            return Boolean.class;
        }

        @Override
        public @NotNull Byte toPrimitive(@NotNull Boolean aBoolean, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return (byte) (aBoolean ? 1 : 0);
        }

        @Override
        public @NotNull Boolean fromPrimitive(@NotNull Byte aByte, @NotNull PersistentDataAdapterContext persistentDataAdapterContext) {
            return aByte >= 1;
        }
    }
}
