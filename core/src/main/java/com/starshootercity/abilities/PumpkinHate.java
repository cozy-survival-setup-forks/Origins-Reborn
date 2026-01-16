package com.starshootercity.abilities;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.events.PlayerSwapOriginEvent;
import com.starshootercity.util.AbilityRegister;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.version.MVPotionEffectType;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class PumpkinHate implements Listener, VisibleAbility {
    private final Map<Player, List<Player>> ignoringPlayers = new HashMap<>();
    private final Set<Player> pumpkinHaters = new HashSet<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        runForAbility(event.getPlayer(), pumpkinHaters::add);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        pumpkinHaters.remove(event.getPlayer());
    }

    @EventHandler
    public void onPlayerSwapOrigin(PlayerSwapOriginEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> runForAbility(event.getPlayer(), pumpkinHaters::add, pumpkinHaters::remove));
    }

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player pumpkinHater : pumpkinHaters) {
            for (Player pumpkinWearer : pumpkinHater.getLocation().getNearbyPlayers(64)) {
                if (pumpkinWearer == pumpkinHater) continue;

                List<Player> ignoring = ignoringPlayers.computeIfAbsent(pumpkinHater, p -> new ArrayList<>());
                
                ItemStack helmet = pumpkinWearer.getInventory().getHelmet();
                if (helmet != null && helmet.getType() == Material.CARVED_PUMPKIN) {
                    if (ignoring.contains(pumpkinWearer)) continue;
                    ignoring.add(pumpkinWearer);

                    byte data = getData(pumpkinWearer);

                    OriginsReborn.getMVE().sendEntityData(pumpkinHater, pumpkinWearer, data);

                    for (EquipmentSlot equipmentSlot : EquipmentSlot.values()) {
                        pumpkinHater.sendEquipmentChange(pumpkinWearer, equipmentSlot, new ItemStack(Material.AIR));
                    }
                } else if (ignoring.contains(pumpkinWearer)) {
                    ignoring.remove(pumpkinWearer);
                    AbilityRegister.updateEntity(pumpkinHater, pumpkinWearer);
                }
            }
        }
    }

    private static byte getData(Player pumpkinWearer) {
        byte data = 0x20;
        if (pumpkinWearer.getFireTicks() > 0) {
            data += 0x01;
        }
        if (pumpkinWearer.isSneaking()) {
            data += 0x02;
        }
        if (pumpkinWearer.isSprinting()) {
            data += 0x08;
        }
        if (pumpkinWearer.isSwimming()) {
            data += 0x10;
        }
        if (pumpkinWearer.isGlowing()) {
            data += 0x40;
        }
        if (pumpkinWearer.isGliding()) {
            data += (byte) 0x80;
        }
        return data;
    }

    private boolean poison;

    @Override
    public void initialize(JavaPlugin plugin) {
        poison = registerConfigOption(plugin, "consume_and_poison", Collections.singletonList("Poison the player when consuming pumpkin pie rather than preventing them from eating it"), ConfigManager.SettingType.BOOLEAN, false);
    }

    @EventHandler
    public void onPlayerItemConsume(PlayerItemConsumeEvent event) {
        runForAbility(event.getPlayer(), player -> {
            if (event.getItem().getType() == Material.PUMPKIN_PIE) {
                event.setCancelled(true);
                event.getItem().setAmount(event.getItem().getAmount() - 1);
                if (poison) {
                    player.addPotionEffect(new PotionEffect(MVPotionEffectType.HUNGER.get(), 300, 2, false, true));
                    player.addPotionEffect(new PotionEffect(MVPotionEffectType.NAUSEA.get(), 300, 1, false, true));
                    player.addPotionEffect(new PotionEffect(MVPotionEffectType.POISON.get(), 1200, 1, false, true));
                }
            }
        });
    }

    @Override
    public String description() {
        return "You are afraid of pumpkins. For a good reason.";
    }

    @Override
    public String title() {
        return "Scared of Gourds";
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:pumpkin_hate");
    }
}