package com.starshootercity.abilities.types;

import com.destroystokyo.paper.MaterialTags;
import com.destroystokyo.paper.event.server.ServerTickEndEvent;
import com.starshootercity.*;
import com.starshootercity.util.AbilityRegister;
import com.starshootercity.util.ShortcutUtils;
import com.starshootercity.version.MVAttribute;
import com.starshootercity.version.MVBlockDamageAbortEvent;
import com.starshootercity.version.MVEnchantment;
import com.starshootercity.version.MVPotionEffectType;
import org.bukkit.Bukkit;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * An ability modifying the break speed in certain scenarios
 */
@APITarget
public interface BreakSpeedModifierAbility extends Ability {

    /**
     * Gets the 'mining context' for a player, allows the game to treat the player as if they are mining under different conditions
     * @param player The player with the ability
     * @return The mining context for the player
     * @see BlockMiningContext
     */
    BlockMiningContext getMiningContext(Player player);

    /**
     * @param player The player with the ability
     * @return Whether the break speed of this player should be modified with the provided mining context
     */
    boolean shouldActivate(Player player);

    /**
     * The mining context of the player
     * @param heldItem The item to act like the player is holding
     * @param slowDigging The Mining Fatigue status effect to act like the player is having
     * @param fastDigging The Haste status effect to act like the player is having
     * @param conduitPower The Conduit Power status effect to act like the player is having
     * @param underwater Whether the mining speed should act like the player is underwater
     * @param aquaAffinity Whether the mining speed should act like the player has Aqua Affinity
     * @param onGround Whether the mining speed should act like the player is on the ground
     */
    record BlockMiningContext(ItemStack heldItem, @Nullable PotionEffect slowDigging, @Nullable PotionEffect fastDigging, @Nullable PotionEffect conduitPower, boolean underwater, boolean aquaAffinity, boolean onGround) {

        public boolean hasDigSpeed() {
            return fastDigging != null || conduitPower != null;
        }

        public boolean hasDigSlowdown() {
            return slowDigging != null;
        }

        public int getDigSlowdown() {
            if (slowDigging == null) return 0;
            return slowDigging.getAmplifier();
        }

        public int getDigSpeedAmplification() {
            int i = 0;
            int j = 0;
            if (fastDigging != null) {
                i = fastDigging.getAmplifier();
            }
            if (conduitPower != null) {
                j = conduitPower.getAmplifier();
            }
            return Math.max(i, j);
        }
    }

    class BreakSpeedModifierAbilityListener implements Listener {
        Random random = new Random();
        @EventHandler
        public void onBlockDamage(BlockDamageEvent event) {
            if (event.getBlock().getType().getHardness() < 0) return;
            Bukkit.getScheduler().scheduleSyncDelayedTask(OriginsReborn.getInstance(), () -> {
                BreakSpeedModifierAbility speedModifierAbility = null;

                for (BreakSpeedModifierAbility ability : AbilityRegister.breakSpeedModifierAbilities) {
                    if (ability.hasAbility(event.getPlayer())) {
                        if (ability.shouldActivate(event.getPlayer())) {
                            speedModifierAbility = ability;
                            break;
                        }
                    }
                }

                if (speedModifierAbility == null) return;
                AtomicInteger time = new AtomicInteger();
                Entity marker = event.getPlayer().getWorld().spawnEntity(event.getPlayer().getLocation(), EntityType.MARKER);
                BreakSpeedModifierAbility finalSpeedModifierAbility = speedModifierAbility;
                int task = Bukkit.getScheduler().scheduleSyncRepeatingTask(OriginsReborn.getInstance(), () -> {
                    try {
                        BreakSpeedModifierAbility.BlockMiningContext context = finalSpeedModifierAbility.getMiningContext(event.getPlayer());
                        float damage = getBlockDamage(event.getBlock(), context, time.getAndIncrement());
                        if (damage >= 1) {
                            int taskNum = blockbreakingTasks.get(event.getPlayer());
                            cancelTask(taskNum);
                            BlockBreakEvent blockBreakEvent = new ModifiedBlockBreakEvent(event.getBlock(), event.getPlayer());
                            blockBreakEvent.callEvent();
                            ItemStack handItem = event.getPlayer().getInventory().getItemInMainHand();
                            if (isTool(handItem.getType())) {
                                int unbreakingLevel = handItem.getEnchantmentLevel(MVEnchantment.UNBREAKING.get()) + 1;
                                int itemDamage = 0;
                                if (random.nextDouble() <= 1d / unbreakingLevel) {
                                    itemDamage += 1;
                                }
                                if (event.getBlock().getDrops(context.heldItem()).isEmpty()) {
                                    if (random.nextDouble() <= 1d / unbreakingLevel) {
                                        itemDamage += 1;
                                    }
                                }
                                if (handItem.getItemMeta() instanceof Damageable damageable) {
                                    damageable.setDamage(damageable.getDamage() + itemDamage);
                                    if (handItem.getType().getMaxDurability() <= damageable.getDamage()) {
                                        OriginsReborn.getMVE().broadcastSlotBreak(event.getPlayer(), EquipmentSlot.HAND, new ArrayList<>() {{
                                            for (Player player : Bukkit.getOnlinePlayers()) {
                                                if (player.getWorld() != event.getPlayer().getWorld()) continue;
                                                if (player.getLocation().distance(event.getPlayer().getLocation()) < 32) {
                                                    add(player);
                                                }
                                            }
                                        }});
                                        event.getPlayer().getInventory().setItemInMainHand(new ItemStack(Material.AIR));
                                    } else handItem.setItemMeta(damageable);
                                }
                            }
                            if (!blockBreakEvent.isCancelled()) {
                                event.getBlock().breakNaturally(event.getPlayer().getInventory().getItemInMainHand(), true);
                            }
                            return;
                        }
                        OriginsReborn.getMVE().sendBlockDamage(event.getPlayer(), event.getBlock().getLocation(), damage, marker);

                        Block target = event.getPlayer().getTargetBlockExact(8, FluidCollisionMode.NEVER);
                        if (target == null || !target.getLocation().equals(event.getBlock().getLocation())) {
                            int taskNum = blockbreakingTasks.get(event.getPlayer());
                            cancelTask(taskNum);
                        }
                    } catch (NullPointerException e) {
                        int taskNum = blockbreakingTasks.get(event.getPlayer());
                        cancelTask(taskNum);
                    }
                }, 1, 0);
                if (blockbreakingTasks.containsKey(event.getPlayer())) {
                    cancelTask(blockbreakingTasks.get(event.getPlayer()));
                    blockbreakingTasks.remove(event.getPlayer());
                }
                blockbreakingTasks.put(event.getPlayer(), task);
                taskEntityMap.put(task, marker);
                taskBlockMap.put(task, event.getBlock());
                taskPlayerMap.put(task, event.getPlayer());
            });
        }

        private final Map<Integer, Entity> taskEntityMap = new HashMap<>();
        private final Map<Integer, Player> taskPlayerMap = new HashMap<>();
        private final Map<Integer, Block> taskBlockMap = new HashMap<>();
        private final Map<Player, Integer> blockbreakingTasks = new HashMap<>();

        private void cancelTask(int task) {
            Bukkit.getScheduler().cancelTask(task);
            Entity marker = taskEntityMap.get(task);
            Player player = taskPlayerMap.get(task);
            if (player != null && marker != null) {
                OriginsReborn.getMVE().sendBlockDamage(player, taskBlockMap.get(task).getLocation(), 0, marker);
                marker.remove();
            }
            taskEntityMap.remove(task);
            taskBlockMap.remove(task);
            taskPlayerMap.remove(task);
        }

        private static float getBlockDamage(Block block, BreakSpeedModifierAbility.BlockMiningContext context, int time) {
            return (float) (Math.round(getDestroySpeed(context, block.getType()) * time * 1000) / 1000) / (block.getDrops(context.heldItem()).isEmpty() ? 100 : 30);
        }


        public static float getDestroySpeed(BreakSpeedModifierAbility.BlockMiningContext context, Material blockType) {
            float f;
                f = OriginsReborn.getMVE().getDestroySpeed(context.heldItem(), blockType);

            if (f > 1.0F) {
                ItemStack itemstack = context.heldItem();
                int i = itemstack.getEnchantmentLevel(MVEnchantment.EFFICIENCY.get());

                if (i > 0 && itemstack.getType() != Material.AIR) {
                    f += (float) (i * i + 1);
                }
            }

            if (context.hasDigSpeed()) {
                f *= 1.0F + (float) (context.getDigSpeedAmplification() + 1) * 0.2F;
            }

            if (context.hasDigSlowdown()) {
                float f1;
                int digSlowdown = context.getDigSlowdown();
                f1 = switch (digSlowdown) {
                    case 0 -> 0.3F;
                    case 1 -> 0.09F;
                    case 2 -> 0.0027F;
                    default -> 1;
                };
                f *= f1;
            }

            if (context.underwater() && !context.aquaAffinity()) {
                f /= 5.0F;
            }

            if (!context.onGround()) {
                f /= 5.0F;
            }

            float d = OriginsReborn.getMVE().getDestroySpeed(blockType);

            return f / d;
        }

        @EventHandler
        public void onBlockDamage(MVBlockDamageAbortEvent event) {
            if (blockbreakingTasks.containsKey(event.getPlayer())) {
                int taskNum = blockbreakingTasks.get(event.getPlayer());
                cancelTask(taskNum);
            }
        }

        Map<Player, SavedPotionEffect> storedEffects = new HashMap<>();

        @EventHandler
        public void onServerTickEnd(ServerTickEndEvent event) {
            Attribute attribute = MVAttribute.BLOCK_BREAK_SPEED.get();
            for (Player player : Bukkit.getOnlinePlayers()) {
                List<Origin> origins = OriginSwapper.getOrigins(player);
                List<Ability> abilities = new ArrayList<>();
                for (Origin origin : origins) abilities.addAll(origin.getAbilities());
                BreakSpeedModifierAbility speedModifierAbility = null;
                for (Ability ability : abilities) {
                    if (ability instanceof BreakSpeedModifierAbility modifierAbility) {
                        if (modifierAbility.shouldActivate(player)) {
                            speedModifierAbility = modifierAbility;
                            break;
                        }
                    }
                }
                if (speedModifierAbility != null) {
                    if (attribute == null) {
                        PotionEffect effect = player.getPotionEffect(MVPotionEffectType.MINING_FATIGUE.get());
                        boolean ambient = false;
                        boolean showParticles = false;
                        if (effect != null) {
                            ambient = effect.isAmbient();
                            showParticles = effect.hasParticles();
                            if (effect.getAmplifier() != -1) {
                                storedEffects.put(player, new SavedPotionEffect(effect, Bukkit.getCurrentTick()));
                                player.removePotionEffect(MVPotionEffectType.MINING_FATIGUE.get());
                            }
                        }
                        player.addPotionEffect(new PotionEffect(MVPotionEffectType.MINING_FATIGUE.get(), ShortcutUtils.infiniteDuration(), -1, ambient, showParticles));
                    } else {
                        AttributeInstance instance = player.getAttribute(attribute);
                        if (instance == null) continue;
                        if (OriginsReborn.getMVE().getAttributeModifier(instance, key) == null) {
                            OriginsReborn.getMVE().addAttributeModifier(instance, key, "break-speed-modifier", -1, AttributeModifier.Operation.ADD_NUMBER);
                        }
                    }
                } else {
                    if (attribute == null) {
                        if (player.hasPotionEffect(MVPotionEffectType.MINING_FATIGUE.get())) {
                            PotionEffect effect = player.getPotionEffect(MVPotionEffectType.MINING_FATIGUE.get());
                            if (effect != null) {
                                if (effect.getAmplifier() == -1)
                                    player.removePotionEffect(MVPotionEffectType.MINING_FATIGUE.get());
                            }
                        }
                        if (storedEffects.containsKey(player)) {
                            SavedPotionEffect effect = storedEffects.get(player);
                            storedEffects.remove(player);
                            PotionEffect potionEffect = effect.effect();
                            int time = potionEffect.getDuration() - (Bukkit.getCurrentTick() - effect.currentTime());
                            if (time > 0) {
                                player.addPotionEffect(new PotionEffect(
                                        potionEffect.getType(),
                                        time,
                                        potionEffect.getAmplifier(),
                                        potionEffect.isAmbient(),
                                        potionEffect.hasParticles()
                                ));
                            }
                        }
                    } else {
                        AttributeInstance instance = player.getAttribute(attribute);
                        if (instance == null) continue;
                        AttributeModifier attributeModifier = OriginsReborn.getMVE().getAttributeModifier(instance, key);
                        if (attributeModifier == null) continue;
                        instance.removeModifier(attributeModifier);
                    }
                }
            }
        }
    }

    NamespacedKey key = new NamespacedKey(OriginsReborn.getInstance(), "break-speed-modifier");

    private static boolean isTool(Material material) {
        return MaterialTags.PICKAXES.isTagged(material) || MaterialTags.AXES.isTagged(material) || MaterialTags.SWORDS.isTagged(material) || MaterialTags.SHOVELS.isTagged(material) || MaterialTags.HOES.isTagged(material) || material == Material.SHEARS || material == Material.TRIDENT;
    }

    class ModifiedBlockBreakEvent extends BlockBreakEvent {
        public ModifiedBlockBreakEvent(@NotNull Block theBlock, @NotNull Player player) {
            super(theBlock, player);
        }
    }
}
