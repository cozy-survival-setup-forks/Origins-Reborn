package com.starshootercity.version;

import com.destroystokyo.paper.entity.ai.Goal;
import com.starshootercity.packetsenders.NMSInvoker;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.util.TriState;
import org.bukkit.*;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeInstance;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.block.Block;
import org.bukkit.block.Conduit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.*;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("unused")
public abstract class MultiVersionExecutor extends NMSInvoker {

    /**
     * Launch an arrow from an entity
     * @param projectile Projectile to be launched
     * @param entity Entity to launch the projectile from
     * @param roll Roll of the projectile
     * @param force Force of the projectile
     * @param divergence Divergence of the projectile
     */
    public abstract void launchArrow(Entity projectile, Entity entity, float roll, float force, float divergence);

    /**
     * Used to damage another entity with the same damage source as specified in the event
     * <br><br>
     * In older versions of Minecraft this may just deal the damage with no specified cause
     * @param entity Entity to damage
     * @param event Damage event
     */
    public abstract void transferDamageEvent(LivingEntity entity, EntityDamageEvent event);

    /**
     * A method used in Origins-Fantasy to boost the status effects on an arrow
     * @param arrow Arrow to boost
     */
    public abstract void boostArrow(Arrow arrow);

    /**
     * A method used in Origins-Fantasy to duplicate an Allay
     * <br><br>
     * Will fail on Minecraft versions where the Allay does not exist
     * @param allay Entity to duplicate (must be an Allay)
     * @return Whether the duplication succeeded
     */
    public abstract boolean duplicateAllay(LivingEntity allay);

    /**
     * Deal explosion damage to a player
     * <br><br>
     * Will be replaced with a more multipurpose damage system in the future
     * @param player Player to damage
     * @param amount Amount of damage to deal
     */
    public abstract void dealExplosionDamage(Player player, int amount);

    /**
     * Deal sonic boom damage to an entity
     * <br><br>
     * Will be replaced with a more multipurpose damage system in the future
     * @param entity Entity to damage
     * @param amount Amount of damage to deal
     */
    public abstract void dealSonicBoomDamage(LivingEntity entity, int amount, Player source);

    public abstract Goal<Villager> getVillagerAfraidGoal(LivingEntity villager, Predicate<Player> hasAbility);

    @Nullable
    public abstract Player getNearestVisiblePlayer(Piglin piglin);

    public abstract void throwItem(Piglin piglin, ItemStack itemStack, Location pos);

    public abstract void damageItem(ItemStack item, int amount, Player player);

    public abstract void startAutoSpinAttack(Player player, int duration, float riptideAttackDamage, ItemStack item);

    public abstract void tridentMove(Player player);

    public abstract void dealThornsDamage(Entity target, int amount, Entity attacker);

    @NotNull
    public abstract Particle getElderGuardianParticle();

    @NotNull
    public abstract Particle getWitchParticle();

    public abstract Goal<Mob> getIronGolemAttackGoal(LivingEntity golem, Predicate<Player> hasAbility);

    public abstract void bounce(Player player);

    @NotNull
    public abstract Particle getHappyVillagerParticle();

    public abstract void playTotemEffect(Player player);

    @NotNull
    public abstract List<PotionEffect> getDefaultEffects(PotionMeta meta, boolean isLingering);

    public abstract void dropItem(Player player, ItemStack it);

    public abstract void sendEntityData(Player player, Entity entity, byte bytes);

    public abstract void sendResourcePacks(Player player, String pack, Map<?, ResourcePackInfo> extraPacks);

    public abstract Goal<Creeper> getCreeperAfraidGoal(LivingEntity creeper, Predicate<Player> hasAbility, Predicate<LivingEntity> hasKey);

    public abstract boolean wasTouchingWater(Player player);

    public abstract float getDestroySpeed(ItemStack item, Material block);

    public abstract float getDestroySpeed(Material block);

    public abstract void setNoPhysics(Player player, boolean noPhysics);

    public abstract void sendGamemodeUpdate(Player player, GameMode gameMode, boolean bedrock);

    @Override
    @Deprecated
    public void sendPhasingGamemodeUpdate(Player player, GameMode gameMode) {
    }

    @Nullable
    public abstract Location getRespawnLocation(Player player);

    public abstract void resetRespawnLocation(Player player);

    @Nullable
    public abstract AttributeModifier getAttributeModifier(AttributeInstance instance, NamespacedKey key);

    public abstract void dealDryOutDamage(LivingEntity entity, int amount);

    public abstract void dealDrowningDamage(LivingEntity entity, int amount);

    public abstract void dealFreezeDamage(LivingEntity entity, int amount);

    public abstract boolean supportsInfiniteDuration();

    public abstract boolean isUnderWater(LivingEntity entity);

    public abstract void knockback(LivingEntity entity, double strength, double x, double z);

    public abstract void setFlyingFallDamage(Player player, TriState state);

    public abstract void broadcastSlotBreak(Player player, EquipmentSlot slot, Collection<Player> players);

    public abstract void sendBlockDamage(Player player, Location location, float damage, Entity entity);

    public abstract void addAttributeModifier(AttributeInstance instance, NamespacedKey key, String name, double amount, AttributeModifier.Operation operation);

    public abstract void setWorldBorderOverlay(Player player, boolean show);

    public abstract void setComments(FileConfiguration config, String path, List<String> comments);

    public abstract Component applyFont(Component component, Key font);

    @Nullable
    public abstract Material getOminousBottle();

    @NotNull
    protected abstract Attribute armorAttribute();

    @NotNull
    protected abstract Attribute maxHealthAttribute();

    @NotNull
    protected abstract Attribute movementSpeedAttribute();

    @NotNull
    protected abstract Attribute flyingSpeedAttribute();

    @NotNull
    protected abstract Attribute attackDamageAttribute();

    @NotNull
    protected abstract Attribute attackKnockbackAttribute();

    @NotNull
    protected abstract Attribute attackSpeedAttribute();

    @NotNull
    protected abstract Attribute armorToughnessAttribute();

    @NotNull
    protected abstract Attribute luckAttribute();

    @NotNull
    protected abstract Attribute horseJumpStrengthAttribute();

    @NotNull
    protected abstract Attribute spawnReinforcementsAttribute();

    @NotNull
    protected abstract Attribute followRangeAttribute();

    @NotNull
    protected abstract Attribute knockbackResistanceAttribute();

    @Nullable
    protected abstract Attribute fallDamageMultiplierAttribute();

    @Nullable
    protected abstract Attribute maxAbsorptionAttribute();

    @Nullable
    protected abstract Attribute safeFallDistanceAttribute();

    @Nullable
    protected abstract Attribute scaleAttribute();

    @Nullable
    protected abstract Attribute stepHeightAttribute();

    @Nullable
    protected abstract Attribute gravityAttribute();

    @Nullable
    protected abstract Attribute jumpStrengthAttribute();

    @Nullable
    protected abstract Attribute burningTimeAttribute();

    @Nullable
    protected abstract Attribute explosionKnockbackResistanceAttribute();

    @Nullable
    protected abstract Attribute movementEfficiencyAttribute();

    @Nullable
    protected abstract Attribute oxygenBonusAttribute();

    @Nullable
    protected abstract Attribute waterMovementEfficiencyAttribute();

    @Nullable
    protected abstract Attribute temptRangeAttribute();

    @Nullable
    protected abstract Attribute blockInteractionRangeAttribute();

    @Nullable
    protected abstract Attribute entityInteractionRangeAttribute();

    @Nullable
    protected abstract Attribute blockBreakSpeedAttribute();

    @Nullable
    protected abstract Attribute miningEfficiencyAttribute();

    @Nullable
    protected abstract Attribute sneakingSpeedAttribute();

    @Nullable
    protected abstract Attribute submergedMiningSpeedAttribute();

    @Nullable
    protected abstract Attribute sweepingDamageRatioAttribute();

    @NotNull
    protected abstract Enchantment protectionEnchantment();

    @NotNull
    protected abstract Enchantment fireProtectionEnchantment();

    @NotNull
    protected abstract Enchantment featherFallingEnchantment();

    @NotNull
    protected abstract Enchantment blastProtectionEnchantment();

    @NotNull
    protected abstract Enchantment projectileProtectionEnchantment();

    @NotNull
    protected abstract Enchantment respirationEnchantment();

    @NotNull
    protected abstract Enchantment aquaAffinityEnchantment();

    @NotNull
    protected abstract Enchantment thornsEnchantment();

    @NotNull
    protected abstract Enchantment depthStriderEnchantment();

    @NotNull
    protected abstract Enchantment frostWalkerEnchantment();

    @NotNull
    protected abstract Enchantment bindingCurseEnchantment();

    @NotNull
    protected abstract Enchantment sharpnessEnchantment();

    @NotNull
    protected abstract Enchantment smiteEnchantment();

    @NotNull
    protected abstract Enchantment baneOfArthropodsEnchantment();

    @NotNull
    protected abstract Enchantment knockbackEnchantment();

    @NotNull
    protected abstract Enchantment fireAspectEnchantment();

    @NotNull
    protected abstract Enchantment lootingEnchantment();

    @NotNull
    protected abstract Enchantment sweepingEdgeEnchantment();

    @NotNull
    protected abstract Enchantment efficiencyEnchantment();

    @NotNull
    protected abstract Enchantment silkTouchEnchantment();

    @NotNull
    protected abstract Enchantment unbreakingEnchantment();

    @NotNull
    protected abstract Enchantment fortuneEnchantment();

    @NotNull
    protected abstract Enchantment powerEnchantment();

    @NotNull
    protected abstract Enchantment punchEnchantment();

    @NotNull
    protected abstract Enchantment flameEnchantment();

    @NotNull
    protected abstract Enchantment infinityEnchantment();

    @NotNull
    protected abstract Enchantment luckOfTheSeaEnchantment();

    @NotNull
    protected abstract Enchantment lureEnchantment();

    @NotNull
    protected abstract Enchantment loyaltyEnchantment();

    @NotNull
    protected abstract Enchantment impalingEnchantment();

    @NotNull
    protected abstract Enchantment riptideEnchantment();

    @NotNull
    protected abstract Enchantment channelingEnchantment();

    @NotNull
    protected abstract Enchantment multishotEnchantment();

    @NotNull
    protected abstract Enchantment quickChargeEnchantment();

    @NotNull
    protected abstract Enchantment piercingEnchantment();

    @Nullable
    protected abstract Enchantment densityEnchantment();

    @Nullable
    protected abstract Enchantment breachEnchantment();

    @Nullable
    protected abstract Enchantment windBurstEnchantment();

    @NotNull
    protected abstract Enchantment mendingEnchantment();

    @NotNull
    protected abstract Enchantment vanishingCurseEnchantment();

    @NotNull
    protected abstract Enchantment soulSpeedEnchantment();

    @Nullable
    protected abstract Enchantment swiftSneakEnchantment();

    @Nullable
    public Enchantment waterProtectionEnchantment() {
        return null;
    }

    public final void registerWaterProtection(Enchantment enchantment) {
    }

    @NotNull
    public abstract ItemMeta setCustomModelData(ItemMeta meta, int cmd);

    public abstract int getConduitRange(Conduit conduit);

    public abstract void setTouchingWater(Player player);

    public abstract boolean boneMeal(Block block, Player player);

    @NotNull
    protected abstract PotionEffectType speedEffect();

    @NotNull
    protected abstract PotionEffectType slownessEffect();

    @NotNull
    protected abstract PotionEffectType hasteEffect();

    @NotNull
    protected abstract PotionEffectType miningFatigueEffect();

    @NotNull
    protected abstract PotionEffectType strengthEffect();

    @NotNull
    protected abstract PotionEffectType instantHealthEffect();

    @NotNull
    protected abstract PotionEffectType instantDamageEffect();

    @NotNull
    protected abstract PotionEffectType jumpBoostEffect();

    @NotNull
    protected abstract PotionEffectType nauseaEffect();

    @NotNull
    protected abstract PotionEffectType regenerationEffect();

    @NotNull
    protected abstract PotionEffectType resistanceEffect();

    @NotNull
    protected abstract PotionEffectType fireResistanceEffect();

    @NotNull
    protected abstract PotionEffectType waterBreathingEffect();

    @NotNull
    protected abstract PotionEffectType invisibilityEffect();

    @NotNull
    protected abstract PotionEffectType blindnessEffect();

    @NotNull
    protected abstract PotionEffectType nightVisionEffect();

    @NotNull
    protected abstract PotionEffectType hungerEffect();

    @NotNull
    protected abstract PotionEffectType weaknessEffect();

    @NotNull
    protected abstract PotionEffectType poisonEffect();

    @NotNull
    protected abstract PotionEffectType witherEffect();

    @NotNull
    protected abstract PotionEffectType healthBoostEffect();

    @NotNull
    protected abstract PotionEffectType absorptionEffect();

    @NotNull
    protected abstract PotionEffectType saturationEffect();

    @NotNull
    protected abstract PotionEffectType glowingEffect();

    @NotNull
    protected abstract PotionEffectType levitationEffect();

    @NotNull
    protected abstract PotionEffectType luckEffect();

    @NotNull
    protected abstract PotionEffectType badLuckEffect();

    @NotNull
    protected abstract PotionEffectType slowFallingEffect();

    @NotNull
    protected abstract PotionEffectType conduitPowerEffect();

    @NotNull
    protected abstract PotionEffectType dolphinsGraceEffect();

    @NotNull
    protected abstract PotionEffectType badOmenEffect();

    @NotNull
    protected abstract PotionEffectType heroOfTheVillageEffect();

    @Nullable
    protected abstract PotionEffectType darknessEffect();

    @Nullable
    protected abstract PotionEffectType trialOmenEffect();

    @Nullable
    protected abstract PotionEffectType raidOmenEffect();

    @Nullable
    protected abstract PotionEffectType windChargedEffect();

    @Nullable
    protected abstract PotionEffectType weavingEffect();

    @Nullable
    protected abstract PotionEffectType oozingEffect();

    @Nullable
    protected abstract PotionEffectType infestedEffect();

    public void initialize() {
    }

    // Deprecated code below
    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getSweepingDamageRatioAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getArmorAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getArmorToughnessAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getAttackDamageAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getAttackKnockbackAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getAttackSpeedAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getBlockBreakSpeedAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getBlockInteractionRangeAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getBurningTimeAttribute() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getGenericScaleAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getEntityInteractionRangeAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getExplosionKnockbackResistanceAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getFallDamageMultiplierAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getFlyingSpeedAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getFollowRangeAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getGravityAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getHorseJumpStrengthAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getJumpStrengthAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getKnockbackResistanceAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getLuckAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getMaxAbsorptionAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getMaxHealthAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getMiningEfficiencyAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getMovementEfficiencyAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getMovementSpeedAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getOxygenBonusAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getSafeFallDistanceAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getScaleAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getSneakingSpeedAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @NotNull
    public Attribute getSpawnReinforcementsAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getStepHeightAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getSubmergedMiningSpeedAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getTemptRangeAttribute() {
        return null;
    }

    @Override
    @Deprecated(forRemoval = true)
    @Nullable
    public Attribute getWaterMovementEfficiencyAttribute() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public PotionEffectType getNauseaEffect() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public PotionEffectType getMiningFatigueEffect() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public PotionEffectType getHasteEffect() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public PotionEffectType getJumpBoostEffect() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public PotionEffectType getSlownessEffect() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public PotionEffectType getStrengthEffect() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public Enchantment getUnbreakingEnchantment() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public Enchantment getEfficiencyEnchantment() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public Enchantment getRespirationEnchantment() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public Enchantment getAquaAffinityEnchantment() {
        return null;
    }

    @Deprecated(forRemoval = true)
    @NotNull
    public Enchantment getBaneOfArthropodsEnchantment() {
        return null;
    }
}
