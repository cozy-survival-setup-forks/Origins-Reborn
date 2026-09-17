package com.starshootercity.abilities;

import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class Vegetarian implements Listener, VisibleAbility {
    private List<Material> meat;

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        if (event.getItem().getType() == Material.POTION) return;
        runForAbility(event.getPlayer(), player -> {
            if (meat.contains(event.getItem().getType())) {
                event.setCancelled(true);

                if (poison) {
                    consumeItem(player, event.getHand());
                    player.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 300, 1, false, true));
                }
            }
        });
    }

    private void consumeItem(Player player, EquipmentSlot hand) {
        ItemStack held = hand == EquipmentSlot.OFF_HAND
                ? player.getInventory().getItemInOffHand()
                : player.getInventory().getItemInMainHand();
        ItemStack remaining = held.clone();
        remaining.setAmount(Math.max(0, held.getAmount() - 1));
        if (hand == EquipmentSlot.OFF_HAND) player.getInventory().setItemInOffHand(remaining);
        else player.getInventory().setItemInMainHand(remaining);
    }

    @Override
    public String description() {
        return "You can't digest any meat.";
    }

    @Override
    public String title() {
        return "Vegetarian";
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:vegetarian");
    }

    private boolean poison;

    @Override
    public void initialize(JavaPlugin plugin) {
        poison = registerConfigOption(plugin, "consume_and_poison", Collections.singletonList("Poison the player when consuming meat rather than preventing them from eating it"), ConfigManager.SettingType.BOOLEAN, false);

        String meatContents = "meat";
        meat = registerConfigOption(plugin, meatContents, Collections.singletonList("Items that count as meat"), ConfigManager.SettingType.MATERIAL_LIST, List.of(
                Material.PORKCHOP,
                Material.COOKED_PORKCHOP,
                Material.BEEF,
                Material.COOKED_BEEF,
                Material.CHICKEN,
                Material.COOKED_CHICKEN,
                Material.RABBIT,
                Material.COOKED_RABBIT,
                Material.MUTTON,
                Material.COOKED_MUTTON,
                Material.RABBIT_STEW,
                Material.COD,
                Material.COOKED_COD,
                Material.TROPICAL_FISH,
                Material.SALMON,
                Material.COOKED_SALMON,
                Material.PUFFERFISH,
                Material.ROTTEN_FLESH,
                Material.SPIDER_EYE
        ));
    }
}
