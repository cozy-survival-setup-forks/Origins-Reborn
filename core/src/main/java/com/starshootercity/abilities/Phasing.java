package com.starshootercity.abilities;

import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.BreakSpeedModifierAbility;
import com.starshootercity.abilities.types.DependantAbility;
import com.starshootercity.abilities.types.FlightAllowingAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.util.ShortcutUtils;
import com.starshootercity.util.config.ConfigManager;
import com.starshootercity.version.MVEnchantment;
import com.starshootercity.version.MVPotionEffectType;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class Phasing implements DependantAbility, FlightAllowingAbility, BreakSpeedModifierAbility, Listener, VisibleAbility {

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:phasing");
    }

    @Override
    public @NotNull Key getDependencyKey() {
        return Key.key("origins:phantomize");
    }

    @Override
    public String description() {
        return "While phantomized, you can walk through solid material, except Obsidian.";
    }

    @Override
    public String title() {
        return "Phasing";
    }

    @EventHandler
    @SuppressWarnings("deprecation")
    public void onServerTick(ServerTickEndEvent event) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            runForAbility(p, player -> {
                boolean begins = player.isSneaking() && player.isOnGround() && !impassable.contains(player.getLocation().getBlock().getRelative(BlockFace.DOWN).getType());
                setPhasing(player, begins || isInBlock(player), begins);
                OriginsReborn.getMVE().setNoPhysics(player, player.getGameMode() == GameMode.SPECTATOR || isPhasing.computeIfAbsent(player, pl -> false));
                if (isPhasing.computeIfAbsent(player, pl -> false)) {
                    player.setFallDistance(0);
                    if (player.getAllowFlight()) player.setFlying(true);
                }
            }, player -> {
                if (isPhasing.computeIfAbsent(player, pl -> false)) setPhasing(player, false, false);
            });
        }
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        if (isPhasing.computeIfAbsent(event.getPlayer(), pl -> false) && (isInBlock(event.getTo(), block -> impassable.contains(block.getType())))) {
            event.setCancelled(true);
        }
    }

    private boolean giveBlindness;
    private float phasingSpeed;

    @Override
    public void initialize(JavaPlugin plugin) {
        impassable = registerConfigOption(plugin, "impassable_blocks", Collections.singletonList("Blocks you cannot pass through when phasing"), ConfigManager.SettingType.MATERIAL_LIST, List.of(
                Material.OBSIDIAN,
                Material.CRYING_OBSIDIAN,
                Material.BEDROCK
        ));
        giveBlindness = registerConfigOption(plugin, "give_blindness", Collections.singletonList("Apply Blindness when Phasing"), ConfigManager.SettingType.BOOLEAN, true);
        phasingSpeed = registerConfigOption(plugin, "phasing_speed", Collections.singletonList("Speed at which the player should move when phasing"), ConfigManager.SettingType.FLOAT, 0.1f);
    }

    private List<Material> impassable;

    public boolean isInBlock(Entity entity) {
        return isInBlock(entity.getLocation(), block -> block.getType().isSolid() && !impassable.contains(block.getType()));
    }

    public boolean isInBlock(Location location, Predicate<Block> predicate) {
        boolean isInsideBlock = false;
        for (Location currentLocation : List.of(location.clone().add(0, 1, 0), location.clone())) {
            List<Double> values = List.of(0.4, -0.4);
            for (double x : values) {
                for (double z : values) {
                    if (predicate.test(currentLocation.clone().add(x, 0, z).getBlock())) {
                        isInsideBlock = true;
                        break;
                    }
                }
                if (isInsideBlock) break;
            }
            if (isInsideBlock) break;
        }
        return isInsideBlock;
    }

    private final Map<Player, Boolean> isPhasing = new HashMap<>();

    @Override
    public boolean canFly(Player player) {
        return getDependency().isEnabled(player) && isPhasing.computeIfAbsent(player, pl -> false);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        runForAbility(event.getEntity(), player -> {
            if (!isPhasing.getOrDefault(player, false)) return;
            if (event.getCause() == EntityDamageEvent.DamageCause.SUFFOCATION) {
                event.setCancelled(true);
            }
        });
    }

    @Override
    public float getFlightSpeed(Player player) {
        return phasingSpeed;
    }

    @Override
    public BlockMiningContext getMiningContext(Player player) {
        ItemStack helmet = player.getInventory().getHelmet();
        boolean aquaAffinity = false;
        if (helmet != null) {
            aquaAffinity = helmet.containsEnchantment(MVEnchantment.AQUA_AFFINITY.get());
        }
        return new BlockMiningContext(
                player.getInventory().getItemInMainHand(),
                player.getPotionEffect(MVPotionEffectType.HASTE.get()),
                player.getPotionEffect(MVPotionEffectType.MINING_FATIGUE.get()),
                player.getPotionEffect(MVPotionEffectType.CONDUIT_POWER.get()),
                OriginsReborn.getMVE().isUnderWater(player),
                aquaAffinity,
                true
        );
    }

    @Override
    public boolean shouldActivate(Player player) {
        return getDependency().isEnabled(player) && isPhasing.computeIfAbsent(player, pl -> false);
    }

    private void setPhasing(Player player, boolean enabled, boolean begins) {
        enabled = hasAbility(player) && enabled;
        Block block = player.getEyeLocation().getBlock();
        if (block.getType().isCollidable() && enabled) {
            if (giveBlindness) player.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, ShortcutUtils.infiniteDuration(), 0, false, false));
        } else {
            PotionEffect pot = player.getPotionEffect(PotionEffectType.BLINDNESS);
            if (pot != null && ShortcutUtils.isInfinite(pot)) player.removePotionEffect(PotionEffectType.BLINDNESS);
        }
        if (isPhasing.computeIfAbsent(player, pl -> false) == enabled) return;
        Vector vector = player.getVelocity();
        GameMode gameMode = enabled ? GameMode.SPECTATOR : player.getGameMode();
        if (ShortcutUtils.isBedrockPlayer(player.getUniqueId())) {
            if (begins) player.teleport(player.getLocation().subtract(0, 1, 0));
            OriginsReborn.getMVE().sendGamemodeUpdate(player, gameMode, true);
        } else OriginsReborn.getMVE().sendGamemodeUpdate(player, gameMode, false);
        Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> player.setVelocity(vector));
        isPhasing.put(player, enabled);
    }
}
