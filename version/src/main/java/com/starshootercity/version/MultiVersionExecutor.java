package com.starshootercity.version;

import com.destroystokyo.paper.entity.ai.Goal;
import com.starshootercity.APITarget;
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
import java.util.function.BiPredicate;
import java.util.function.Predicate;

@SuppressWarnings("unused")
@APITarget
public abstract class MultiVersionExecutor extends NMSInvoker {


    public boolean isCustomAttribute(AttributeModifier modifier) {
        return true;
    }

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

    public abstract @Nullable Player getNearestVisiblePlayer(Piglin piglin);

    public abstract void throwItem(Piglin piglin, ItemStack itemStack, Location pos);

    public abstract void damageItem(ItemStack item, int amount, Player player);

    public abstract void startAutoSpinAttack(Player player, int duration, float riptideAttackDamage, ItemStack item);

    public abstract void tridentMove(Player player);

    public abstract void dealThornsDamage(Entity target, int amount, Entity attacker);

    public abstract @NotNull Particle getElderGuardianParticle();

    public abstract @NotNull Particle getWitchParticle();

    public abstract Goal<Mob> getIronGolemAttackGoal(LivingEntity golem, Predicate<Player> hasAbility);

    public abstract void bounce(Player player);

    public abstract @NotNull Particle getHappyVillagerParticle();

    public abstract void playTotemEffect(Player player);

    public abstract @NotNull List<PotionEffect> getDefaultEffects(PotionMeta meta, boolean isLingering);

    public abstract void dropItem(Player player, ItemStack it);

    public abstract void sendEntityData(Player player, Entity entity, byte bytes);

    public abstract void sendResourcePacks(Player player, String pack, Map<?, ResourcePackInfo> extraPacks);

    public abstract Goal<Creeper> getCreeperAfraidGoal(LivingEntity creeper, BiPredicate<LivingEntity, Player> hasAbility);

    public abstract boolean wasTouchingWater(Player player);

    public abstract float getDestroySpeed(ItemStack item, Material block);

    public abstract float getDestroySpeed(Material block);

    public abstract void setNoPhysics(Player player, boolean noPhysics);

    public abstract void sendGamemodeUpdate(Player player, GameMode gameMode, boolean bedrock);

    public abstract void setArrow(Arrow arrow, ItemStack itemStack);

    @Override
    @Deprecated
    public void sendPhasingGamemodeUpdate(Player player, GameMode gameMode) {

    }

    public abstract @Nullable Location getRespawnLocation(Player player);

    public abstract void resetRespawnLocation(Player player);

    public abstract @Nullable AttributeModifier getAttributeModifier(AttributeInstance instance, Key key);

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

    public abstract @Nullable Material getOminousBottle();

    protected abstract @NotNull Attribute armorAttribute();

    protected abstract @NotNull Attribute maxHealthAttribute();

    protected abstract @NotNull Attribute movementSpeedAttribute();

    protected abstract @NotNull Attribute flyingSpeedAttribute();

    protected abstract @NotNull Attribute attackDamageAttribute();

    protected abstract @NotNull Attribute attackKnockbackAttribute();

    protected abstract @NotNull Attribute attackSpeedAttribute();

    protected abstract @NotNull Attribute armorToughnessAttribute();

    protected abstract @NotNull Attribute luckAttribute();

    protected abstract @NotNull Attribute horseJumpStrengthAttribute();

    protected abstract @NotNull Attribute spawnReinforcementsAttribute();

    protected abstract @NotNull Attribute followRangeAttribute();

    protected abstract @NotNull Attribute knockbackResistanceAttribute();

    protected abstract @Nullable Attribute fallDamageMultiplierAttribute();

    protected abstract @Nullable Attribute maxAbsorptionAttribute();

    protected abstract @Nullable Attribute safeFallDistanceAttribute();

    protected abstract @Nullable Attribute scaleAttribute();

    protected abstract @Nullable Attribute stepHeightAttribute();

    protected abstract @Nullable Attribute gravityAttribute();

    protected abstract @Nullable Attribute jumpStrengthAttribute();

    protected abstract @Nullable Attribute burningTimeAttribute();

    protected abstract @Nullable Attribute explosionKnockbackResistanceAttribute();

    protected abstract @Nullable Attribute movementEfficiencyAttribute();

    protected abstract @Nullable Attribute oxygenBonusAttribute();

    protected abstract @Nullable Attribute waterMovementEfficiencyAttribute();

    protected abstract @Nullable Attribute temptRangeAttribute();

    protected abstract @Nullable Attribute blockInteractionRangeAttribute();

    protected abstract @Nullable Attribute entityInteractionRangeAttribute();

    protected abstract @Nullable Attribute blockBreakSpeedAttribute();

    protected abstract @Nullable Attribute miningEfficiencyAttribute();

    protected abstract @Nullable Attribute sneakingSpeedAttribute();

    protected abstract @Nullable Attribute submergedMiningSpeedAttribute();

    protected abstract @Nullable Attribute sweepingDamageRatioAttribute();

    protected abstract @NotNull Enchantment protectionEnchantment();

    protected abstract @NotNull Enchantment fireProtectionEnchantment();

    protected abstract @NotNull Enchantment featherFallingEnchantment();

    protected abstract @NotNull Enchantment blastProtectionEnchantment();

    protected abstract @NotNull Enchantment projectileProtectionEnchantment();

    protected abstract @NotNull Enchantment respirationEnchantment();

    protected abstract @NotNull Enchantment aquaAffinityEnchantment();

    protected abstract @NotNull Enchantment thornsEnchantment();

    protected abstract @NotNull Enchantment depthStriderEnchantment();

    protected abstract @NotNull Enchantment frostWalkerEnchantment();

    protected abstract @NotNull Enchantment bindingCurseEnchantment();

    protected abstract @NotNull Enchantment sharpnessEnchantment();

    protected abstract @NotNull Enchantment smiteEnchantment();

    protected abstract @NotNull Enchantment baneOfArthropodsEnchantment();

    protected abstract @NotNull Enchantment knockbackEnchantment();

    protected abstract @NotNull Enchantment fireAspectEnchantment();

    protected abstract @NotNull Enchantment lootingEnchantment();

    protected abstract @NotNull Enchantment sweepingEdgeEnchantment();

    protected abstract @NotNull Enchantment efficiencyEnchantment();

    protected abstract @NotNull Enchantment silkTouchEnchantment();

    protected abstract @NotNull Enchantment unbreakingEnchantment();

    protected abstract @NotNull Enchantment fortuneEnchantment();

    protected abstract @NotNull Enchantment powerEnchantment();

    protected abstract @NotNull Enchantment punchEnchantment();

    protected abstract @NotNull Enchantment flameEnchantment();

    protected abstract @NotNull Enchantment infinityEnchantment();

    protected abstract @NotNull Enchantment luckOfTheSeaEnchantment();

    protected abstract @NotNull Enchantment lureEnchantment();

    protected abstract @NotNull Enchantment loyaltyEnchantment();

    protected abstract @NotNull Enchantment impalingEnchantment();

    protected abstract @NotNull Enchantment riptideEnchantment();

    protected abstract @NotNull Enchantment channelingEnchantment();

    protected abstract @NotNull Enchantment multishotEnchantment();

    protected abstract @NotNull Enchantment quickChargeEnchantment();

    protected abstract @NotNull Enchantment piercingEnchantment();

    protected abstract @Nullable Enchantment densityEnchantment();

    protected abstract @Nullable Enchantment breachEnchantment();

    protected abstract @Nullable Enchantment windBurstEnchantment();

    protected abstract @NotNull Enchantment mendingEnchantment();

    protected abstract @NotNull Enchantment vanishingCurseEnchantment();

    protected abstract @NotNull Enchantment soulSpeedEnchantment();

    protected abstract @Nullable Enchantment swiftSneakEnchantment();

    public abstract @NotNull ItemMeta setCustomModelData(ItemMeta meta, int cmd);

    public abstract int getConduitRange(Conduit conduit);

    public abstract void setTouchingWater(Player player);

    public abstract boolean boneMeal(Block block, Player player);
    
    protected abstract @NotNull PotionEffectType speedEffect();

    protected abstract @NotNull PotionEffectType slownessEffect();

    protected abstract @NotNull PotionEffectType hasteEffect();

    protected abstract @NotNull PotionEffectType miningFatigueEffect();

    protected abstract @NotNull PotionEffectType strengthEffect();

    protected abstract @NotNull PotionEffectType instantHealthEffect();

    protected abstract @NotNull PotionEffectType instantDamageEffect();

    protected abstract @NotNull PotionEffectType jumpBoostEffect();

    protected abstract @NotNull PotionEffectType nauseaEffect();

    protected abstract @NotNull PotionEffectType regenerationEffect();

    protected abstract @NotNull PotionEffectType resistanceEffect();

    protected abstract @NotNull PotionEffectType fireResistanceEffect();

    protected abstract @NotNull PotionEffectType waterBreathingEffect();

    protected abstract @NotNull PotionEffectType invisibilityEffect();

    protected abstract @NotNull PotionEffectType blindnessEffect();

    protected abstract @NotNull PotionEffectType nightVisionEffect();

    protected abstract @NotNull PotionEffectType hungerEffect();

    protected abstract @NotNull PotionEffectType weaknessEffect();

    protected abstract @NotNull PotionEffectType poisonEffect();

    protected abstract @NotNull PotionEffectType witherEffect();

    protected abstract @NotNull PotionEffectType healthBoostEffect();

    protected abstract @NotNull PotionEffectType absorptionEffect();

    protected abstract @NotNull PotionEffectType saturationEffect();

    protected abstract @NotNull PotionEffectType glowingEffect();

    protected abstract @NotNull PotionEffectType levitationEffect();

    protected abstract @NotNull PotionEffectType luckEffect();

    protected abstract @NotNull PotionEffectType badLuckEffect();

    protected abstract @NotNull PotionEffectType slowFallingEffect();

    protected abstract @NotNull PotionEffectType conduitPowerEffect();

    protected abstract @NotNull PotionEffectType dolphinsGraceEffect();

    protected abstract @NotNull PotionEffectType badOmenEffect();

    protected abstract @NotNull PotionEffectType heroOfTheVillageEffect();

    protected abstract @Nullable PotionEffectType darknessEffect();

    protected abstract @Nullable PotionEffectType trialOmenEffect();

    protected abstract @Nullable PotionEffectType raidOmenEffect();

    protected abstract @Nullable PotionEffectType windChargedEffect();

    protected abstract @Nullable PotionEffectType weavingEffect();

    protected abstract @Nullable PotionEffectType oozingEffect();

    protected abstract @Nullable PotionEffectType infestedEffect();

    public void initialize() {

    }

    // Deprecated code below

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getSweepingDamageRatioAttribute() {
        return sweepingDamageRatioAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getArmorAttribute() {
        return armorAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getArmorToughnessAttribute() {
        return armorToughnessAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getAttackDamageAttribute() {
        return attackDamageAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getAttackKnockbackAttribute() {
        return attackKnockbackAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getAttackSpeedAttribute() {
        return attackSpeedAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getBlockBreakSpeedAttribute() {
        return blockBreakSpeedAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getBlockInteractionRangeAttribute() {
        return blockInteractionRangeAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getBurningTimeAttribute() {
        return burningTimeAttribute();
    }

    @Deprecated(forRemoval = true)
    public @Nullable Attribute getGenericScaleAttribute() {
        return scaleAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getEntityInteractionRangeAttribute() {
        return entityInteractionRangeAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getExplosionKnockbackResistanceAttribute() {
        return explosionKnockbackResistanceAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getFallDamageMultiplierAttribute() {
        return fallDamageMultiplierAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getFlyingSpeedAttribute() {
        return flyingSpeedAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getFollowRangeAttribute() {
        return followRangeAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getGravityAttribute() {
        return gravityAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getHorseJumpStrengthAttribute() {
        return horseJumpStrengthAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getJumpStrengthAttribute() {
        return jumpStrengthAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getKnockbackResistanceAttribute() {
        return knockbackResistanceAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getLuckAttribute() {
        return luckAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getMaxAbsorptionAttribute() {
        return maxAbsorptionAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getMaxHealthAttribute() {
        return maxHealthAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getMiningEfficiencyAttribute() {
        return miningEfficiencyAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getMovementEfficiencyAttribute() {
        return movementEfficiencyAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getMovementSpeedAttribute() {
        return movementSpeedAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getOxygenBonusAttribute() {
        return oxygenBonusAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getSafeFallDistanceAttribute() {
        return safeFallDistanceAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getScaleAttribute() {
        return scaleAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getSneakingSpeedAttribute() {
        return sneakingSpeedAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @NotNull Attribute getSpawnReinforcementsAttribute() {
        return spawnReinforcementsAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getStepHeightAttribute() {
        return stepHeightAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getSubmergedMiningSpeedAttribute() {
        return submergedMiningSpeedAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getTemptRangeAttribute() {
        return temptRangeAttribute();
    }

    @Override
    @Deprecated(forRemoval = true)
    public @Nullable Attribute getWaterMovementEfficiencyAttribute() {
        return waterMovementEfficiencyAttribute();
    }

    @Deprecated(forRemoval = true)
    public @NotNull PotionEffectType getNauseaEffect() {
        return nauseaEffect();
    }

    @Deprecated(forRemoval = true)
    public @NotNull PotionEffectType getMiningFatigueEffect() {
        return miningFatigueEffect();
    }

    @Deprecated(forRemoval = true)
    public @NotNull PotionEffectType getHasteEffect() {
        return hasteEffect();
    }

    @Deprecated(forRemoval = true)
    public @NotNull PotionEffectType getJumpBoostEffect() {
        return jumpBoostEffect();
    }

    @Deprecated(forRemoval = true)
    public @NotNull PotionEffectType getSlownessEffect() {
        return slownessEffect();
    }

    @Deprecated(forRemoval = true)
    public @NotNull PotionEffectType getStrengthEffect() {
        return strengthEffect();
    }

    @Deprecated(forRemoval = true)
    public @NotNull Enchantment getUnbreakingEnchantment() {
        return unbreakingEnchantment();
    }

    @Deprecated(forRemoval = true)
    public @NotNull Enchantment getEfficiencyEnchantment() {
        return efficiencyEnchantment();
    }

    @Deprecated(forRemoval = true)
    public @NotNull Enchantment getRespirationEnchantment() {
        return respirationEnchantment();
    }

    @Deprecated(forRemoval = true)
    public @NotNull Enchantment getAquaAffinityEnchantment() {
        return aquaAffinityEnchantment();
    }

    @Deprecated(forRemoval = true)
    public @NotNull Enchantment getBaneOfArthropodsEnchantment() {
        return baneOfArthropodsEnchantment();
    }
}