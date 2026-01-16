package com.starshootercity.version;

import com.destroystokyo.paper.entity.ai.Goal;
import io.netty.buffer.Unpooled;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockDestructionPacket;
import net.minecraft.network.protocol.game.ClientboundGameEventPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.entity.ConduitBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Conduit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.craftbukkit.v1_19_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_19_R2.block.CraftConduit;
import org.bukkit.craftbukkit.v1_19_R2.block.data.CraftBlockData;
import org.bukkit.craftbukkit.v1_19_R2.entity.*;
import org.bukkit.craftbukkit.v1_19_R2.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageAbortEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.spigotmc.event.entity.EntityDismountEvent;
import org.spigotmc.event.entity.EntityMountEvent;
import xyz.jpenilla.reflectionremapper.ReflectionRemapper;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.function.Predicate;

public class MVE_V1_19_3 extends MultiVersionExecutor {

    @Override
    public void setTouchingWater(Player player) {
        ((CraftPlayer) player).getHandle().wasTouchingWater = true;
    }

    private final net.minecraft.world.item.ItemStack boneMeal = CraftItemStack.asNMSCopy(new ItemStack(Material.BONE_MEAL));

    @Override
    public boolean boneMeal(Block block, Player player) {
        BlockPos blockPos = new BlockPos(
                block.getX(),
                block.getY(),
                block.getZ()
        );

        BlockHitResult bhr = new BlockHitResult(
                Vec3.atCenterOf(blockPos),
                ((CraftPlayer) player).getHandle().getDirection(),
                blockPos,
                false
        );

        InteractionResult result = BoneMealItem.applyBonemeal(new UseOnContext(
                ((CraftWorld) player.getWorld()).getHandle(),
                ((CraftPlayer) player).getHandle(),
                InteractionHand.MAIN_HAND,
                boneMeal,
                bhr
        ));
        return result == InteractionResult.SUCCESS;
    }

    @Override
    public @Nullable Attribute miningEfficiencyAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute sneakingSpeedAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute submergedMiningSpeedAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute sweepingDamageRatioAttribute() {
        return null;
    }

    @Override
    public @NotNull ItemMeta setCustomModelData(ItemMeta meta, int cmd) {
        meta.setCustomModelData(cmd);
        return meta;
    }

    @Override
    public void initialize() {
        String blocks = ReflectionRemapper.forReobfMappingsInPaperJar().remapFieldName(ConduitBlockEntity.class, "effectBlocks");
        String active = ReflectionRemapper.forReobfMappingsInPaperJar().remapFieldName(ConduitBlockEntity.class, "isActive");
        try {
            effectBlocks = ConduitBlockEntity.class.getDeclaredField(blocks);
            effectBlocks.setAccessible(true);
            isActive = ConduitBlockEntity.class.getDeclaredField(active);
            isActive.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field effectBlocks;
    private static Field isActive;

    @Override
    @SuppressWarnings("unchecked")
    public int getConduitRange(Conduit conduit) {
        try {
            ConduitBlockEntity tileEntity = ((CraftConduit) conduit).getTileEntity();
            boolean active = (boolean) isActive.get(tileEntity);
            if (!active) return 0;
            List<BlockPos> list = (List<BlockPos>) effectBlocks.get(tileEntity);
            int i = list.size();
            return i / 7 * 16;
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public @NotNull Attribute flyingSpeedAttribute() {
        return Attribute.GENERIC_FLYING_SPEED;
    }

    @Override
    public @NotNull Attribute attackKnockbackAttribute() {
        return Attribute.GENERIC_ATTACK_KNOCKBACK;
    }

    @Override
    public @NotNull Attribute attackSpeedAttribute() {
        return Attribute.GENERIC_ATTACK_SPEED;
    }

    @Override
    public @NotNull Attribute armorToughnessAttribute() {
        return Attribute.GENERIC_ARMOR_TOUGHNESS;
    }

    @Override
    public @NotNull Attribute luckAttribute() {
        return Attribute.GENERIC_LUCK;
    }

    @Override
    public @NotNull Attribute horseJumpStrengthAttribute() {
        return Attribute.HORSE_JUMP_STRENGTH;
    }

    @Override
    public @NotNull Attribute spawnReinforcementsAttribute() {
        return Attribute.ZOMBIE_SPAWN_REINFORCEMENTS;
    }

    @Override
    public @NotNull Attribute followRangeAttribute() {
        return Attribute.GENERIC_FOLLOW_RANGE;
    }

    @Override
    public @NotNull Attribute knockbackResistanceAttribute() {
        return Attribute.GENERIC_KNOCKBACK_RESISTANCE;
    }

    @Override
    public @Nullable Attribute fallDamageMultiplierAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute maxAbsorptionAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute safeFallDistanceAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute scaleAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute stepHeightAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute gravityAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute jumpStrengthAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute burningTimeAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute explosionKnockbackResistanceAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute movementEfficiencyAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute oxygenBonusAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute waterMovementEfficiencyAttribute() {
        return null;
    }

    @Override
    public @Nullable Attribute temptRangeAttribute() {
        return null;
    }

    @Override
    public void dealDrowningDamage(LivingEntity entity, int amount) {
        net.minecraft.world.entity.LivingEntity livingEntity = ((CraftLivingEntity) entity).getHandle();
        livingEntity.hurt(DamageSource.DROWN, amount);
    }

    @Override
    public Component applyFont(Component component, Key font) {
        return component.font(font);
    }

    @Override
    public @Nullable Material getOminousBottle() {
        return null;
    }

    @EventHandler
    public void onBlockDamageAbort(BlockDamageAbortEvent event) {
        new MVBlockDamageAbortEvent(event.getPlayer(), event.getBlock(), event.getItemInHand()).callEvent();
    }

    @Override
    public void sendEntityData(Player player, Entity entity, byte bytes) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        net.minecraft.world.entity.Entity target = ((CraftEntity) entity).getHandle();

        List<SynchedEntityData.DataValue<?>> eData = new ArrayList<>();
        eData.add(SynchedEntityData.DataValue.create(new EntityDataAccessor<>(0, EntityDataSerializers.BYTE), bytes));
        ClientboundSetEntityDataPacket metadata = new ClientboundSetEntityDataPacket(target.getId(), eData);
        serverPlayer.connection.send(metadata);
    }

    @Override
    public Goal<@NotNull Creeper> getCreeperAfraidGoal(LivingEntity creeper, BiPredicate<LivingEntity, Player> hasAbility) {
        return new AvoidEntityGoal<>(
                (PathfinderMob) ((CraftEntity) creeper).getHandle(),
                net.minecraft.world.entity.player.Player.class,
                6,
                1,
                1.2,
                livingEntity -> {
                    if (livingEntity.getBukkitEntity() instanceof Player player) {
                        return hasAbility.test(creeper, player);
                    }
                    return false;
                }

        ).asPaperVanillaGoal();
    }

    @Override
    public boolean wasTouchingWater(Player player) {
        return ((CraftPlayer) player).getHandle().wasTouchingWater;
    }

    @Override
    public float getDestroySpeed(ItemStack item, Material block) {
        BlockState b = ((CraftBlockData) block.createBlockData()).getState();
        net.minecraft.world.item.ItemStack handle = CraftItemStack.asNMSCopy(item);
        return handle.getDestroySpeed(b);
    }

    @Override
    public float getDestroySpeed(Material block) {
        return ((CraftBlockData) block.createBlockData()).getState().destroySpeed;
    }

    @Override
    public void setNoPhysics(Player player, boolean noPhysics) {
        ((CraftPlayer) player).getHandle().noPhysics = noPhysics;
    }

    @Override
    public @NotNull Attribute armorAttribute() {
        return Attribute.GENERIC_ARMOR;
    }

    @Override
    public @NotNull Attribute maxHealthAttribute() {
        return Attribute.GENERIC_MAX_HEALTH;
    }

    @Override
    public @NotNull Attribute movementSpeedAttribute() {
        return Attribute.GENERIC_MOVEMENT_SPEED;
    }

    @Override
    public @NotNull Attribute attackDamageAttribute() {
        return Attribute.GENERIC_ATTACK_DAMAGE;
    }

    @Override
    public void sendGamemodeUpdate(Player player, GameMode gameMode, boolean bedrock) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();

        GameType gameType = switch (gameMode) {
            case CREATIVE -> GameType.CREATIVE;
            case SURVIVAL -> GameType.SURVIVAL;
            case ADVENTURE -> GameType.ADVENTURE;
            case SPECTATOR -> GameType.SPECTATOR;
        };

        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        buf.writeEnumSet(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_GAME_MODE), ClientboundPlayerInfoUpdatePacket.Action.class);
        buf.writeCollection(List.of("null"), (buf2, e) -> {
            buf2.writeUUID(serverPlayer.getUUID());
            buf2.writeVarInt(gameType.getId());
        });

        ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(buf);
        serverPlayer.connection.send(packet);

        if (bedrock) {
            ClientboundGameEventPacket eventPacket = new ClientboundGameEventPacket(ClientboundGameEventPacket.CHANGE_GAME_MODE, gameType.getId());
            serverPlayer.connection.send(eventPacket);
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public void sendResourcePacks(Player player, String pack, Map<?, ResourcePackInfo> extraPacks) {
        player.setResourcePack(pack);
    }

    @Override
    public @Nullable Location getRespawnLocation(Player player) {
        return player.getBedSpawnLocation();
    }

    @Override
    public void resetRespawnLocation(Player player) {
        player.setBedSpawnLocation(null);
    }

    @Override
    public @Nullable AttributeModifier getAttributeModifier(AttributeInstance instance, Key key) {
        UUID u = UUID.nameUUIDFromBytes(key.toString().getBytes());
        for (AttributeModifier am : instance.getModifiers()) {
            if (am.getUniqueId().equals(u)) return am;
        }
        return null;
    }

    @Override
    public void addAttributeModifier(AttributeInstance instance, NamespacedKey key, String name, double amount, AttributeModifier.Operation operation) {
        instance.addModifier(new AttributeModifier(UUID.nameUUIDFromBytes(key.toString().getBytes()), name, amount, operation));
    }

    @Override
    public boolean supportsInfiniteDuration() {
        return false;
    }

    @Override
    public boolean isUnderWater(LivingEntity entity) {
        return entity.isUnderWater();
    }

    @Override
    public void knockback(LivingEntity entity, double strength, double x, double z) {
        entity.knockback(strength, x, z);
    }

    @Override
    public Attribute blockInteractionRangeAttribute() {
        return null;
    }

    @Override
    public Attribute entityInteractionRangeAttribute() {
        return null;
    }

    @Override
    public void dealDryOutDamage(LivingEntity entity, int amount) {
        net.minecraft.world.entity.LivingEntity livingEntity = ((CraftLivingEntity) entity).getHandle();
        livingEntity.hurt(DamageSource.DRY_OUT, amount);
    }

    @Override
    public void dealFreezeDamage(LivingEntity entity , int amount) {
        net.minecraft.world.entity.LivingEntity livingEntity = ((CraftLivingEntity) entity).getHandle();
        livingEntity.hurt(DamageSource.FREEZE, amount);
    }

    @Override
    public void setFlyingFallDamage(Player player, TriState state) {
        player.setFlyingFallDamage(state);
    }

    @Override
    public void broadcastSlotBreak(Player player, EquipmentSlot slot, Collection<Player> players) {
        player.broadcastSlotBreak(slot, players);
    }

    @Override
    public void sendBlockDamage(Player player, Location location, float damage, Entity entity) {
        ClientboundBlockDestructionPacket packet = new ClientboundBlockDestructionPacket(entity.getEntityId(), new BlockPos(location.getX(), location.getY(), location.getZ()), (int) (damage*10));
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    @Override
    public Attribute blockBreakSpeedAttribute() {
        return null;
    }

    @Override
    public void setWorldBorderOverlay(Player player, boolean show) {
        if (show) {
            WorldBorder border = Bukkit.createWorldBorder();
            border.setCenter(player.getWorld().getWorldBorder().getCenter());
            border.setSize(player.getWorld().getWorldBorder().getSize());
            border.setWarningDistance((int) (player.getWorld().getWorldBorder().getSize()*2));
            player.setWorldBorder(border);
        } else player.setWorldBorder(null);
    }

    @Override
    public void setComments(FileConfiguration config, String path, List<String> comments) {
        config.setComments(path, comments);
    }

    @Override
    public void dealExplosionDamage(Player player, int amount) {
        ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle();
        serverPlayer.hurt(DamageSource.explosion(null), amount);
    }

    @Override
    public void dealSonicBoomDamage(LivingEntity entity, int amount, Player source) {
        ServerPlayer serverPlayer = ((CraftPlayer) source).getHandle();
        net.minecraft.world.entity.Entity e = ((CraftEntity) entity).getHandle();
        e.hurt(DamageSource.sonicBoom(serverPlayer), amount);
    }

    @Override
    public Goal<@NotNull Villager> getVillagerAfraidGoal(LivingEntity villager, Predicate<Player> hasAbility) {
        return new AvoidEntityGoal<>(
                (PathfinderMob) ((CraftEntity) villager).getHandle(),
                net.minecraft.world.entity.player.Player.class,
                6,
                0.5,
                0.8,
                livingEntity -> {
                    if (livingEntity.getBukkitEntity() instanceof Player player) {
                        return hasAbility.test(player);
                    }
                    return false;
                }
        ).asPaperVanillaGoal();
    }

    @Override
    public void throwItem(Piglin piglin, ItemStack itemStack, Location pos) {
        BehaviorUtils.throwItem(((CraftLivingEntity) piglin).getHandle(), CraftItemStack.asNMSCopy(itemStack), new Vec3(pos.getX(), pos.getY(), pos.getZ()));
    }

    @Override
    public void launchArrow(Entity projectile, Entity entity, float roll, float force, float divergence) {
        ((AbstractProjectile) projectile).getHandle().shootFromRotation(((CraftEntity) entity).getHandle(), entity.getLocation().getPitch(), entity.getLocation().getYaw(), roll, force, divergence);
    }

    @Override
    public void damageItem(ItemStack item, int amount, Player player) {
        item.damage(amount, player);
    }

    @Override
    public void startAutoSpinAttack(Player player, int duration, float riptideAttackDamage, ItemStack item) {
        ((CraftPlayer) player).getHandle().startAutoSpinAttack(duration);
    }

    @Override
    public void tridentMove(Player player) {
        ((CraftPlayer) player).getHandle().move(MoverType.SELF, new Vec3(0.0D, 1.1999999284744263D, 0.0D));
    }

    @Override
    public void transferDamageEvent(LivingEntity entity, EntityDamageEvent event) {
        entity.damage(event.getDamage());
    }

    @Override
    public void boostArrow(Arrow arrow) {
        if (((CraftArrow) arrow).getHandle() instanceof net.minecraft.world.entity.projectile.Arrow a) {
            for (MobEffectInstance instance : PotionUtils.getPotion(a.getPickupItem()).getEffects()) {
                a.addEffect(new MobEffectInstance(instance.getEffect(), instance.getDuration(), instance.getAmplifier() + 1));
            }
        }
    }

    @Override
    public boolean duplicateAllay(LivingEntity entity) {
        if (!(entity instanceof Allay allay)) return false;
        if (allay.getDuplicationCooldown() > 0) return false;
        allay.duplicateAllay();
        ((CraftWorld) allay.getWorld()).getHandle().broadcastEntityEvent(((CraftAllay) allay).getHandle(), (byte) 18);
        return true;
    }

    @EventHandler
    public void onEntityDismount(EntityDismountEvent event) {
        event.setCancelled(!new MVEntityDismountEvent(event.getEntity(), event.getDismounted(), event.isCancellable()).callEvent());
    }

    @EventHandler
    public void onEntityMount(EntityMountEvent event) {
        event.setCancelled(!new MVEntityMountEvent(event.getEntity(), event.getMount()).callEvent());
    }

    @Override
    protected @NotNull PotionEffectType speedEffect() {
        return PotionEffectType.SPEED;
    }

    @Override
    protected @NotNull PotionEffectType slownessEffect() {
        return PotionEffectType.SLOW;
    }

    @Override
    protected @NotNull PotionEffectType hasteEffect() {
        return PotionEffectType.FAST_DIGGING;
    }

    @Override
    protected @NotNull PotionEffectType miningFatigueEffect() {
        return PotionEffectType.SLOW_DIGGING;
    }

    @Override
    protected @NotNull PotionEffectType strengthEffect() {
        return PotionEffectType.INCREASE_DAMAGE;
    }

    @Override
    protected @NotNull PotionEffectType instantHealthEffect() {
        return PotionEffectType.HEAL;
    }

    @Override
    protected @NotNull PotionEffectType instantDamageEffect() {
        return PotionEffectType.HARM;
    }

    @Override
    protected @NotNull PotionEffectType jumpBoostEffect() {
        return PotionEffectType.JUMP;
    }

    @Override
    protected @NotNull PotionEffectType nauseaEffect() {
        return PotionEffectType.CONFUSION;
    }

    @Override
    protected @NotNull PotionEffectType regenerationEffect() {
        return PotionEffectType.REGENERATION;
    }

    @Override
    protected @NotNull PotionEffectType resistanceEffect() {
        return PotionEffectType.DAMAGE_RESISTANCE;
    }

    @Override
    protected @NotNull PotionEffectType fireResistanceEffect() {
        return PotionEffectType.FIRE_RESISTANCE;
    }

    @Override
    protected @NotNull PotionEffectType waterBreathingEffect() {
        return PotionEffectType.WATER_BREATHING;
    }

    @Override
    protected @NotNull PotionEffectType invisibilityEffect() {
        return PotionEffectType.INVISIBILITY;
    }

    @Override
    protected @NotNull PotionEffectType blindnessEffect() {
        return PotionEffectType.BLINDNESS;
    }

    @Override
    protected @NotNull PotionEffectType nightVisionEffect() {
        return PotionEffectType.NIGHT_VISION;
    }

    @Override
    protected @NotNull PotionEffectType hungerEffect() {
        return PotionEffectType.HUNGER;
    }

    @Override
    protected @NotNull PotionEffectType weaknessEffect() {
        return PotionEffectType.WEAKNESS;
    }

    @Override
    protected @NotNull PotionEffectType poisonEffect() {
        return PotionEffectType.POISON;
    }

    @Override
    protected @NotNull PotionEffectType witherEffect() {
        return PotionEffectType.WITHER;
    }

    @Override
    protected @NotNull PotionEffectType healthBoostEffect() {
        return PotionEffectType.HEALTH_BOOST;
    }

    @Override
    protected @NotNull PotionEffectType absorptionEffect() {
        return PotionEffectType.ABSORPTION;
    }

    @Override
    protected @NotNull PotionEffectType saturationEffect() {
        return PotionEffectType.SATURATION;
    }

    @Override
    protected @NotNull PotionEffectType glowingEffect() {
        return PotionEffectType.GLOWING;
    }

    @Override
    protected @NotNull PotionEffectType levitationEffect() {
        return PotionEffectType.LEVITATION;
    }

    @Override
    protected @NotNull PotionEffectType luckEffect() {
        return PotionEffectType.LUCK;
    }

    @Override
    protected @NotNull PotionEffectType badLuckEffect() {
        return PotionEffectType.UNLUCK;
    }

    @Override
    protected @NotNull PotionEffectType slowFallingEffect() {
        return PotionEffectType.SLOW_FALLING;
    }

    @Override
    protected @NotNull PotionEffectType conduitPowerEffect() {
        return PotionEffectType.CONDUIT_POWER;
    }

    @Override
    protected @NotNull PotionEffectType dolphinsGraceEffect() {
        return PotionEffectType.DOLPHINS_GRACE;
    }

    @Override
    protected @NotNull PotionEffectType badOmenEffect() {
        return PotionEffectType.BAD_OMEN;
    }

    @Override
    protected @NotNull PotionEffectType heroOfTheVillageEffect() {
        return PotionEffectType.HERO_OF_THE_VILLAGE;
    }

    @Override
    protected @Nullable PotionEffectType darknessEffect() {
        return PotionEffectType.DARKNESS;
    }

    @Override
    protected @Nullable PotionEffectType trialOmenEffect() {
        return null;
    }

    @Override
    protected @Nullable PotionEffectType raidOmenEffect() {
        return null;
    }

    @Override
    protected @Nullable PotionEffectType windChargedEffect() {
        return null;
    }

    @Override
    protected @Nullable PotionEffectType weavingEffect() {
        return null;
    }

    @Override
    protected @Nullable PotionEffectType oozingEffect() {
        return null;
    }

    @Override
    protected @Nullable PotionEffectType infestedEffect() {
        return null;
    }

    @Override
    protected @NotNull Enchantment protectionEnchantment() {
        return Enchantment.PROTECTION_ENVIRONMENTAL;
    }

    @Override
    protected @NotNull Enchantment fireProtectionEnchantment() {
        return Enchantment.PROTECTION_FIRE;
    }

    @Override
    protected @NotNull Enchantment featherFallingEnchantment() {
        return Enchantment.PROTECTION_FALL;
    }

    @Override
    protected @NotNull Enchantment blastProtectionEnchantment() {
        return Enchantment.PROTECTION_EXPLOSIONS;
    }

    @Override
    protected @NotNull Enchantment projectileProtectionEnchantment() {
        return Enchantment.PROTECTION_PROJECTILE;
    }

    @Override
    protected @NotNull Enchantment respirationEnchantment() {
        return Enchantment.OXYGEN;
    }

    @Override
    protected @NotNull Enchantment aquaAffinityEnchantment() {
        return Enchantment.WATER_WORKER;
    }

    @Override
    protected @NotNull Enchantment thornsEnchantment() {
        return Enchantment.THORNS;
    }

    @Override
    protected @NotNull Enchantment depthStriderEnchantment() {
        return Enchantment.DEPTH_STRIDER;
    }

    @Override
    protected @NotNull Enchantment frostWalkerEnchantment() {
        return Enchantment.FROST_WALKER;
    }

    @Override
    protected @NotNull Enchantment bindingCurseEnchantment() {
        return Enchantment.BINDING_CURSE;
    }

    @Override
    protected @NotNull Enchantment sharpnessEnchantment() {
        return Enchantment.DAMAGE_ALL;
    }

    @Override
    protected @NotNull Enchantment smiteEnchantment() {
        return Enchantment.DAMAGE_UNDEAD;
    }

    @Override
    protected @NotNull Enchantment baneOfArthropodsEnchantment() {
        return Enchantment.DAMAGE_ARTHROPODS;
    }

    @Override
    protected @NotNull Enchantment knockbackEnchantment() {
        return Enchantment.KNOCKBACK;
    }

    @Override
    protected @NotNull Enchantment fireAspectEnchantment() {
        return Enchantment.FIRE_ASPECT;
    }

    @Override
    protected @NotNull Enchantment lootingEnchantment() {
        return Enchantment.LOOT_BONUS_MOBS;
    }

    @Override
    protected @NotNull Enchantment sweepingEdgeEnchantment() {
        return Enchantment.SWEEPING_EDGE;
    }

    @Override
    protected @NotNull Enchantment efficiencyEnchantment() {
        return Enchantment.DIG_SPEED;
    }

    @Override
    protected @NotNull Enchantment silkTouchEnchantment() {
        return Enchantment.SILK_TOUCH;
    }

    @Override
    protected @NotNull Enchantment unbreakingEnchantment() {
        return Enchantment.DURABILITY;
    }

    @Override
    protected @NotNull Enchantment fortuneEnchantment() {
        return Enchantment.LOOT_BONUS_BLOCKS;
    }

    @Override
    protected @NotNull Enchantment powerEnchantment() {
        return Enchantment.ARROW_DAMAGE;
    }

    @Override
    protected @NotNull Enchantment punchEnchantment() {
        return Enchantment.ARROW_KNOCKBACK;
    }

    @Override
    protected @NotNull Enchantment flameEnchantment() {
        return Enchantment.ARROW_FIRE;
    }

    @Override
    protected @NotNull Enchantment infinityEnchantment() {
        return Enchantment.ARROW_INFINITE;
    }

    @Override
    protected @NotNull Enchantment luckOfTheSeaEnchantment() {
        return Enchantment.LUCK;
    }

    @Override
    protected @NotNull Enchantment lureEnchantment() {
        return Enchantment.LURE;
    }

    @Override
    protected @NotNull Enchantment loyaltyEnchantment() {
        return Enchantment.LOYALTY;
    }

    @Override
    protected @NotNull Enchantment impalingEnchantment() {
        return Enchantment.IMPALING;
    }

    @Override
    protected @NotNull Enchantment riptideEnchantment() {
        return Enchantment.RIPTIDE;
    }

    @Override
    protected @NotNull Enchantment channelingEnchantment() {
        return Enchantment.CHANNELING;
    }

    @Override
    protected @NotNull Enchantment multishotEnchantment() {
        return Enchantment.MULTISHOT;
    }

    @Override
    protected @NotNull Enchantment quickChargeEnchantment() {
        return Enchantment.QUICK_CHARGE;
    }

    @Override
    protected @NotNull Enchantment piercingEnchantment() {
        return Enchantment.PIERCING;
    }

    @Override
    protected @Nullable Enchantment densityEnchantment() {
        return null;
    }

    @Override
    protected @Nullable Enchantment breachEnchantment() {
        return null;
    }

    @Override
    protected @Nullable Enchantment windBurstEnchantment() {
        return null;
    }

    @Override
    protected @NotNull Enchantment mendingEnchantment() {
        return Enchantment.MENDING;
    }

    @Override
    protected @NotNull Enchantment vanishingCurseEnchantment() {
        return Enchantment.VANISHING_CURSE;
    }

    @Override
    protected @NotNull Enchantment soulSpeedEnchantment() {
        return Enchantment.SOUL_SPEED;
    }

    @Override
    protected @Nullable Enchantment swiftSneakEnchantment() {
        return Enchantment.SWIFT_SNEAK;
    }

    @Override
    public @Nullable Player getNearestVisiblePlayer(Piglin piglin) {
        Optional<net.minecraft.world.entity.player.Player> optional = ((CraftPiglin) piglin).getHandle().getBrain().getMemory(MemoryModuleType.NEAREST_VISIBLE_PLAYER);
        return optional.map(player -> (Player) player.getBukkitEntity()).orElse(null);
    }

    @Override
    public void dealThornsDamage(Entity target, int amount, Entity attacker) {
        net.minecraft.world.entity.Entity entity = ((CraftEntity) target).getHandle();
        entity.hurt(DamageSource.thorns(((CraftEntity) attacker).getHandle()), amount);
    }

    @Override
    public @NotNull Particle getElderGuardianParticle() {
        return Particle.MOB_APPEARANCE;
    }

    @Override
    public @NotNull Particle getWitchParticle() {
        return Particle.SPELL_WITCH;
    }

    @Override
    public Goal<@NotNull Mob> getIronGolemAttackGoal(LivingEntity golem, Predicate<Player> hasAbility) {
        return new NearestAttackableTargetGoal<>(
                ((CraftMob) golem).getHandle(),
                net.minecraft.world.entity.player.Player.class,
                10,
                true,
                false,
                livingEntity -> {
                    if (livingEntity.getBukkitEntity() instanceof org.bukkit.entity.Player player) {
                        return hasAbility.test(player);
                    } else return false;
                }).asPaperVanillaGoal();
    }

    private final Map<Player, Vec3> lastVec3Map = new HashMap<>();

    @Override
    @SuppressWarnings("deprecation")
    public void bounce(Player player) {
        ServerPlayer p = ((CraftPlayer) player).getHandle();
        if (player.isOnGround()) {
            if (player.getFallDistance() <= 0) return;
            Vec3 dm = lastVec3Map.get(player);
            if (dm != null) {
                player.setVelocity(player.getVelocity().add(new Vector(0, -dm.y, 0)));
            }
        }
        lastVec3Map.put(player, p.getDeltaMovement());
    }

    @Override
    public @NotNull Particle getHappyVillagerParticle() {
        return Particle.VILLAGER_HAPPY;
    }

    @Override
    public void playTotemEffect(Player player) {
        ((CraftWorld) player.getWorld()).getHandle().broadcastEntityEvent(((CraftPlayer) player).getHandle(), (byte) 35);
    }

    @Override
    public @NotNull List<PotionEffect> getDefaultEffects(PotionMeta meta, boolean isLingering) {
        PotionType type = meta.getBasePotionData().getType();
        if (type.getEffectType() == null) return Collections.emptyList();

        if (type.equals(PotionType.TURTLE_MASTER)) {
            float multiplier = (isLingering ? 1 : 0.25f) * (meta.getBasePotionData().isExtended() ? 2 : 1);
            return List.of(
                    new PotionEffect(PotionEffectType.SLOW, (int) (multiplier*400), meta.getBasePotionData().isUpgraded() ? 5 : 4),
                    new PotionEffect(PotionEffectType.DAMAGE_RESISTANCE, (int) (multiplier*400), meta.getBasePotionData().isUpgraded() ? 2 : 3)
            );
        }
        return Collections.singletonList(new PotionEffect(type.getEffectType(), change(getDuration(type), meta.getBasePotionData().isExtended()), meta.getBasePotionData().isUpgraded() ? 1 : 0));
    }

    public int change(int duration, boolean extended) {
        if (!extended) return duration;
        else if (duration == 3600) return 9600;
        else return duration*2;
    }

    public int getDuration(PotionType type) {
        return switch (type) {
            case NIGHT_VISION, INVISIBILITY, JUMP, FIRE_RESISTANCE, SPEED, WATER_BREATHING, STRENGTH -> 3600;
            case SLOWNESS, WEAKNESS, SLOW_FALLING -> 1800;
            case POISON, REGEN -> 900;
            case LUCK -> 6000;
            default -> 1;
        };
    }

    @Override
    public void dropItem(Player player, ItemStack it) {
        ((CraftPlayer) player).getHandle().drop(CraftItemStack.asNMSCopy(it), true);
    }

    @Override
    public void setArrow(Arrow arrow, ItemStack itemStack) {}
}