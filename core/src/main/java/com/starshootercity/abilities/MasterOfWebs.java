package com.starshootercity.abilities;

import com.destroystokyo.paper.event.entity.EntityKnockbackByEntityEvent;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.CooldownAbility;
import com.starshootercity.abilities.types.FlightAllowingAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.cooldowns.Cooldowns;
import com.starshootercity.util.AbilityRegister;
import net.kyori.adventure.key.Key;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDropItemEvent;
import org.bukkit.event.block.BlockFromToEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareItemCraftEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class MasterOfWebs implements CooldownAbility, FlightAllowingAbility, Listener, VisibleAbility {
    private final Map<Player, List<Entity>> glowingEntities = new HashMap<>();

    private final List<Location> temporaryCobwebs = new ArrayList<>();

    private final Map<Location, BlockState> replacedBlocks = new HashMap<>();

    @EventHandler
    public void onBlockDropItem(BlockDropItemEvent event) {
        if (temporaryCobwebs.contains(event.getBlock().getLocation())) {
            event.setCancelled(true);
            temporaryCobwebs.remove(event.getBlock().getLocation());
        }
    }

    @EventHandler
    public void onBlockFromTo(BlockFromToEvent event) {
        if (temporaryCobwebs.contains(event.getToBlock().getLocation())) {
            event.setCancelled(true);
            event.getToBlock().setType(Material.AIR);
            temporaryCobwebs.remove(event.getToBlock().getLocation());
        }
    }

    private final Map<UUID, Integer> lastKnockback = new HashMap<>();

    @EventHandler
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        runForAbility(event.getDamager(), player -> {
            if (hasCooldown(player)) return;
            if (!event.getEntity().getLocation().getBlock().isSolid()) {
                setCooldown(player);
                Location location = event.getEntity().getLocation().getBlock().getLocation();
                temporaryCobwebs.add(location);
                Block block = location.getBlock();
                BlockState originalState = block.getState();
                replacedBlocks.put(location, originalState);
                block.setType(Material.COBWEB);
                int tick = Bukkit.getCurrentTick();
                UUID uuid = event.getEntity().getUniqueId();
                lastKnockback.put(uuid, tick);
                Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> {
                    if (location.getBlock().getType() == Material.COBWEB && temporaryCobwebs.contains(location)) {
                        temporaryCobwebs.remove(location);
                        if (replacedBlocks.containsKey(location)) {
                            BlockState state = replacedBlocks.remove(location);
                            state.update(true, false);
                        }
                        if (lastKnockback.getOrDefault(uuid, 0) == tick) lastKnockback.remove(uuid);
                    }
                }, 60);
            }
        });
    }

    @EventHandler
    public void onEntityKnockbackByEntity(EntityKnockbackByEntityEvent event) {
        if (lastKnockback.getOrDefault(event.getEntity().getUniqueId(), 0) == Bukkit.getCurrentTick()) {
            event.setCancelled(true);
        }
    }

    private void setCanFly(Player player, boolean setFly) {
        canFly.put(player, setFly);
    }

    private final Map<Player, Boolean> canFly = new HashMap<>();

    @EventHandler
    public void onServerTickEnd(ServerTickEndEvent event) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            runForAbility(player, webMaster -> {
                setCanFly(webMaster, isInCobweb(webMaster));
                List<Entity> entities = webMaster.getNearbyEntities(16, 16, 16);
                entities.removeIf(entity -> !(entity instanceof LivingEntity));
                if (entities.size() > 16) entities = entities.subList(0, 16);
                entities.addAll(Bukkit.getOnlinePlayers());
                entities.removeIf(entity -> entity.getWorld() != webMaster.getWorld());
                entities.removeIf(entity -> entity.getLocation().distance(webMaster.getLocation()) > 16);
                for (Entity entity : entities) {
                    runForAbility(entity, null, () -> {
                        if (entity != webMaster) {
                            if (!glowingEntities.containsKey(webMaster)) {
                                glowingEntities.put(webMaster, new ArrayList<>());
                            }
                            if (isInCobweb(entity)) {
                                if (!glowingEntities.get(webMaster).contains(entity)) {
                                    glowingEntities.get(webMaster).add(entity);
                                }

                                byte data = getData(entity);
                                OriginsReborn.getMVE().sendEntityData(webMaster, entity, data);
                            } else {
                                glowingEntities.get(webMaster).remove(entity);
                                AbilityRegister.updateEntity(webMaster, entity);
                            }
                        }
                    });
                }
            });
        }
    }

    private static byte getData(Entity entity) {
        byte data = 0;
        data |= 0x40;
        if (entity.getFireTicks() > 0) {
            data |= 0x01;
        }
        if (entity instanceof LivingEntity livingEntity) {
            if (livingEntity.isInvisible()) data += 0x20;
        }
        if (entity instanceof Player stuckPlayer) {
            if (stuckPlayer.isSneaking()) {
                data |= 0x02;
            }
            if (stuckPlayer.isSprinting()) {
                data |= 0x08;
            }
            if (stuckPlayer.isSwimming()) {
                data |= 0x10;
            }
            if (stuckPlayer.isGliding()) {
                data |= (byte) 0x80;
            }
        }
        return data;
    }

    public MasterOfWebs() {
        NamespacedKey recipeKey = new NamespacedKey(OriginsReborn.getInstance(), "web-recipe");
        ShapelessRecipe webRecipe = new ShapelessRecipe(recipeKey, new ItemStack(Material.COBWEB));
        if (Bukkit.getRecipe(recipeKey) == null) {
            webRecipe.addIngredient(Material.STRING);
            webRecipe.addIngredient(Material.STRING);
            Bukkit.addRecipe(webRecipe);
        }
    }

    @EventHandler
    public void onPrepareItemCraft(PrepareItemCraftEvent event) {
        if (event.getRecipe() != null) {
            if (event.getRecipe().getResult().getType() == Material.COBWEB) {
                for (HumanEntity entity : event.getInventory().getViewers()) {
                    runForAbility(entity, null, player -> event.getInventory().setResult(null));
                }
            }
        }
    }

    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:master_of_webs");
    }

    @Override
    public String description() {
        return "You navigate cobweb perfectly, and are able to climb in them. When you hit an enemy in melee, they get stuck in cobweb for a while. Non-arthropods stuck in cobweb will be sensed by you. You are able to craft cobweb from string.";
    }

    @Override
    public String title() {
        return "Master of Webs";
    }

    public boolean isInCobweb(Entity entity) {
        for (Block start : Set.of(entity.getLocation().getBlock().getRelative(BlockFace.UP), entity.getLocation().getBlock())) {
            if (start.getType() == Material.COBWEB) return true;
            for (BlockFace face : BlockFace.values()) {
                Block block = start.getRelative(face);
                if (block.getType() != Material.COBWEB) continue;
                if (entity.getBoundingBox().overlaps(block.getBoundingBox())) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean canFly(Player player) {
        return canFly.getOrDefault(player, false);
    }

    @Override
    public boolean forceFly(Player player) {
        return canFly.getOrDefault(player, false);
    }

    @Override
    public float getFlightSpeed(Player player) {
        return 0.04f;
    }

    @Override
    public Cooldowns.CooldownInfo getCooldownInfo() {
        return new Cooldowns.CooldownInfo(120, "web");
    }

    @Override
    public int getPriority() {
        return 2;
    }
}
