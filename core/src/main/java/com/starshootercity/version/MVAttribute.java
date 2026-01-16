package com.starshootercity.version;

import com.starshootercity.APITarget;
import org.bukkit.attribute.Attribute;

/**
 * Allows retrieval of attributes across versions where the enum names may have changed
 * <br><br>
 * Attributes will be null on versions where they do not exist
 */
@APITarget
public enum MVAttribute {

    /**
     * Maximum health of an Entity.
     */
    MAX_HEALTH(MultiVersionExecutor::maxHealthAttribute),
    /**
     * Range at which an Entity will follow others.
     */
    FOLLOW_RANGE(MultiVersionExecutor::followRangeAttribute),
    /**
     * Resistance of an Entity to knockback.
     */
    KNOCKBACK_RESISTANCE(MultiVersionExecutor::knockbackResistanceAttribute),
    /**
     * Movement speed of an Entity.
     */
    MOVEMENT_SPEED(MultiVersionExecutor::movementSpeedAttribute),
    /**
     * Flying speed of an Entity.
     */
    FLYING_SPEED(MultiVersionExecutor::flyingSpeedAttribute),
    /**
     * Attack damage of an Entity.
     */
    ATTACK_DAMAGE(MultiVersionExecutor::attackDamageAttribute),
    /**
     * Attack knockback of an Entity.
     */
    ATTACK_KNOCKBACK(MultiVersionExecutor::attackKnockbackAttribute),
    /**
     * Attack speed of an Entity.
     */
    ATTACK_SPEED(MultiVersionExecutor::attackSpeedAttribute),
    /**
     * Armor bonus of an Entity.
     */
    ARMOR(MultiVersionExecutor::armorAttribute),
    /**
     * Armor durability bonus of an Entity.
     */
    ARMOR_TOUGHNESS(MultiVersionExecutor::armorToughnessAttribute),
    /**
     * The fall damage multiplier of an Entity.
     */
    FALL_DAMAGE_MULTIPLIER(MultiVersionExecutor::fallDamageMultiplierAttribute),
    /**
     * Luck bonus of an Entity.
     */
    LUCK(MultiVersionExecutor::luckAttribute),
    /**
     * Maximum absorption of an Entity.
     */
    MAX_ABSORPTION(MultiVersionExecutor::maxAbsorptionAttribute),
    /**
     * The distance which an Entity can fall without damage.
     */
    SAFE_FALL_DISTANCE(MultiVersionExecutor::safeFallDistanceAttribute),
    /**
     * The relative scale of an Entity.
     */
    SCALE(MultiVersionExecutor::scaleAttribute),
    /**
     * The height which an Entity can walk over.
     */
    STEP_HEIGHT(MultiVersionExecutor::stepHeightAttribute),
    /**
     * The gravity applied to an Entity.
     */
    GRAVITY(MultiVersionExecutor::gravityAttribute),
    /**
     * Strength with which an Entity will jump.
     */
    JUMP_STRENGTH(MultiVersionExecutor::jumpStrengthAttribute),
    /**
     * How long an entity remains burning after ignition.
     */
    BURNING_TIME(MultiVersionExecutor::burningTimeAttribute),
    /**
     * Resistance to knockback from explosions.
     */
    EXPLOSION_KNOCKBACK_RESISTANCE(MultiVersionExecutor::explosionKnockbackResistanceAttribute),
    /**
     * Movement speed through difficult terrain.
     */
    MOVEMENT_EFFICIENCY(MultiVersionExecutor::movementEfficiencyAttribute),
    /**
     * Oxygen use underwater.
     */
    OXYGEN_BONUS(MultiVersionExecutor::oxygenBonusAttribute),
    /**
     * Movement speed through water.
     */
    WATER_MOVEMENT_EFFICIENCY(MultiVersionExecutor::waterMovementEfficiencyAttribute),
    /**
     * Range at which mobs will be tempted by items.
     */
    TEMPT_RANGE(MultiVersionExecutor::temptRangeAttribute),
    /**
     * The block reach distance of a Player.
     */
    BLOCK_INTERACTION_RANGE(MultiVersionExecutor::blockInteractionRangeAttribute),
    /**
     * The entity reach distance of a Player.
     */
    ENTITY_INTERACTION_RANGE(MultiVersionExecutor::entityInteractionRangeAttribute),
    /**
     * Block break speed of a Player.
     */
    BLOCK_BREAK_SPEED(MultiVersionExecutor::blockBreakSpeedAttribute),
    /**
     * Mining speed for correct tools.
     */
    MINING_EFFICIENCY(MultiVersionExecutor::miningEfficiencyAttribute),
    /**
     * Sneaking speed.
     */
    SNEAKING_SPEED(MultiVersionExecutor::sneakingSpeedAttribute),
    /**
     * Underwater mining speed.
     */
    SUBMERGED_MINING_SPEED(MultiVersionExecutor::submergedMiningSpeedAttribute),
    /**
     * Sweeping damage.
     */
    SWEEPING_DAMAGE_RATIO(MultiVersionExecutor::sweepingDamageRatioAttribute),
    /**
     * Chance of a zombie to spawn reinforcements.
     */
    SPAWN_REINFORCEMENTS(MultiVersionExecutor::spawnReinforcementsAttribute);

    private final AttributeGetter attributeGetter;

    public Attribute get() {
        return attributeGetter.get(MVAccessor.get());
    }

    MVAttribute(AttributeGetter attributeGetter) {
        this.attributeGetter = attributeGetter;
    }

    interface AttributeGetter {
        Attribute get(MultiVersionExecutor executor);
    }
}
