package com.starshootercity.util;

import com.starshootercity.OriginsReborn;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.geysermc.api.Geyser;
import org.geysermc.floodgate.api.FloodgateApi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

@SuppressWarnings("unused")
public class ShortcutUtils {

    /**
     * Gives a player some items, dropping them if the player does not have space
     * @param player The player to give the items to
     * @param itemStacks The items to give
     */
    public static void giveItemWithDrops(Player player, ItemStack... itemStacks) {
        for (ItemStack i : player.getInventory().addItem(itemStacks).values()) {
            player.getWorld().dropItemNaturally(player.getLocation(), i);
        }
    }

    /**
     * Gets the living damage source of an event, such as the shooter of the projectile
     * @param event Damage event
     * @return The living damage source (will be null if there is none)
     */
    public static @Nullable LivingEntity getLivingDamageSource(@NotNull EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof Projectile projectile && projectile.getShooter() instanceof LivingEntity entity) return entity;
        else if (event.getDamager() instanceof LivingEntity entity) return entity;
        return null;
    }

    private static final Map<UUID, Boolean> isBedrock = new HashMap<>();

    /**
     * Used to check if a player is on Bedrock Edition, will return false if Geyser is not installed
     * @param uuid UUID of the player to check
     * @return Whether the player is on Bedrock
     */
    public static boolean isBedrockPlayer(UUID uuid) {
        return isBedrock.computeIfAbsent(uuid, ShortcutUtils::checkBedrock);
    }

    private static boolean checkBedrock(UUID uuid) {
        try {
            return FloodgateApi.getInstance().isFloodgatePlayer(uuid);
        } catch (NoClassDefFoundError e) {
            try {
                return Geyser.api().isBedrockPlayer(uuid);
            } catch (NoClassDefFoundError ex) {
                return false;
            }
        }
    }

    public static Component getColored(String f) {
        Component component = Component.empty();
        Iterator<String> iterator = substringsBetween(f, "<", ">").iterator();
        for (String s : f.split("<#\\w{6}>")) {
            if (s.isEmpty()) continue;
            component = component.append(iterator.hasNext() ? Component.text(s).color(TextColor.fromHexString(iterator.next())) : Component.text(s));
        }
        return component;
    }

    public static List<String> substringsBetween(String s, String start, String end) {
        int starti = s.indexOf(start);
        if (starti == -1) return List.of();
        String startPart = s.substring(starti);
        int endi = startPart.indexOf(end);
        if (endi == -1) return List.of();
        List<String> data = new ArrayList<>();
        data.add(startPart.substring(0, endi));
        data.addAll(substringsBetween(startPart.substring(endi), start, end));
        return data;
    }

    /**
     * Checks if a potion effect is infinite
     * @param effect Effect instance to check
     * @return Whether the effect is infinite
     * @see ShortcutUtils#infiniteDuration()
     */
    public static boolean isInfinite(PotionEffect effect) {
        if (OriginsReborn.getMVE().supportsInfiniteDuration()) {
            return (effect.getDuration() == -1);
        } else return (effect.getDuration() >= 20000);
    }

    /**
     * Gives the duration of an infinite potion effect
     * <br><br>
     * On versions without an infinite potion effect this returns a really long effect that will not run out
     * @return Duration of an infinite effect
     * @see ShortcutUtils#isInfinite(PotionEffect)
     */
    public static int infiniteDuration() {
        if (OriginsReborn.getMVE().supportsInfiniteDuration()) {
            return -1;
        } else return 50000;
    }

    public static Inventory getTopInventory(HumanEntity player) {
        try {
            Object view = player.getOpenInventory();
            Method getTopInventory = view.getClass().getMethod("getTopInventory");
            getTopInventory.setAccessible(true);
            return (Inventory) getTopInventory.invoke(view);
        } catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }
}
