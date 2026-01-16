package com.starshootercity.version;

import com.starshootercity.APITarget;
import org.bukkit.potion.PotionEffectType;

/**
 * Allows retrieval of status effects across versions where the enum names may have changed
 * <br><br>
 * Status effects will be null on versions where they do not exist
 */
@APITarget
public enum MVPotionEffectType {

    /**
     * Increases movement speed.
     */
    SPEED(MultiVersionExecutor::speedEffect),

    /**
     * Decreases movement speed.
     */
    SLOWNESS(MultiVersionExecutor::slownessEffect),

    /**
     * Increases dig speed.
     */
    HASTE(MultiVersionExecutor::hasteEffect),

    /**
     * Decreases dig speed.
     */
    MINING_FATIGUE(MultiVersionExecutor::miningFatigueEffect),

    /**
     * Increases damage dealt.
     */
    STRENGTH(MultiVersionExecutor::strengthEffect),

    /**
     * Heals an entity.
     */
    INSTANT_HEALTH(MultiVersionExecutor::instantHealthEffect),

    /**
     * Hurts an entity.
     */
    INSTANT_DAMAGE(MultiVersionExecutor::instantDamageEffect),

    /**
     * Increases jump height.
     */
    JUMP_BOOST(MultiVersionExecutor::jumpBoostEffect),

    /**
     * Warps vision on the client.
     */
    NAUSEA(MultiVersionExecutor::nauseaEffect),

    /**
     * Regenerates health.
     */
    REGENERATION(MultiVersionExecutor::regenerationEffect),

    /**
     * Decreases damage dealt to an entity.
     */
    RESISTANCE(MultiVersionExecutor::resistanceEffect),

    /**
     * Stops fire damage.
     */
    FIRE_RESISTANCE(MultiVersionExecutor::fireResistanceEffect),

    /**
     * Allows breathing underwater.
     */
    WATER_BREATHING(MultiVersionExecutor::waterBreathingEffect),

    /**
     * Grants invisibility.
     */
    INVISIBILITY(MultiVersionExecutor::invisibilityEffect),

    /**
     * Blinds an entity.
     */
    BLINDNESS(MultiVersionExecutor::blindnessEffect),

    /**
     * Allows an entity to see in the dark.
     */
    NIGHT_VISION(MultiVersionExecutor::nightVisionEffect),

    /**
     * Increases hunger.
     */
    HUNGER(MultiVersionExecutor::hungerEffect),

    /**
     * Decreases damage dealt by an entity.
     */
    WEAKNESS(MultiVersionExecutor::weaknessEffect),

    /**
     * Deals damage to an entity over time.
     */
    POISON(MultiVersionExecutor::poisonEffect),

    /**
     * Deals damage to an entity over time and gives the health to the
     * shooter.
     */
    WITHER(MultiVersionExecutor::witherEffect),

    /**
     * Increases the maximum health of an entity.
     */
    HEALTH_BOOST(MultiVersionExecutor::healthBoostEffect),

    /**
     * Increases the maximum health of an entity with health that cannot be
     * regenerated, but is refilled every 30 seconds.
     */
    ABSORPTION(MultiVersionExecutor::absorptionEffect),

    /**
     * Increases the food level of an entity each tick.
     */
    SATURATION(MultiVersionExecutor::saturationEffect),

    /**
     * Outlines the entity so that it can be seen from afar.
     */
    GLOWING(MultiVersionExecutor::glowingEffect),

    /**
     * Causes the entity to float into the air.
     */
    LEVITATION(MultiVersionExecutor::levitationEffect),

    /**
     * Loot table luck.
     */
    LUCK(MultiVersionExecutor::luckEffect),

    /**
     * Loot table unluck.
     */
    UNLUCK(MultiVersionExecutor::badLuckEffect),

    /**
     * Slows entity fall rate.
     */
    SLOW_FALLING(MultiVersionExecutor::slowFallingEffect),

    /**
     * Effects granted by a nearby conduit. Includes enhanced underwater abilities.
     */
    CONDUIT_POWER(MultiVersionExecutor::conduitPowerEffect),

    /**
     * Increses underwater movement speed.<br>
     * Squee'ek uh'k kk'kkkk squeek eee'eek.
     */
    DOLPHINS_GRACE(MultiVersionExecutor::dolphinsGraceEffect),

    /**
     * Triggers an ominous event when the player enters a village or trial chambers.<br>
     * oof.
     */
    BAD_OMEN(MultiVersionExecutor::badOmenEffect),

    /**
     * Reduces the cost of villager trades.<br>
     * \o/.
     */
    HERO_OF_THE_VILLAGE(MultiVersionExecutor::heroOfTheVillageEffect),

    /**
     * Causes the player's vision to dim occasionally.
     */
    DARKNESS(MultiVersionExecutor::darknessEffect),

    /**
     * Causes trial spawners to become ominous.
     */
    TRIAL_OMEN(MultiVersionExecutor::trialOmenEffect),

    /**
     * Triggers a raid when a player enters a village.
     */
    RAID_OMEN(MultiVersionExecutor::raidOmenEffect),

    /**
     * Emits a wind burst upon death.
     */
    WIND_CHARGED(MultiVersionExecutor::windChargedEffect),

    /**
     * Creates cobwebs upon death.
     */
    WEAVING(MultiVersionExecutor::weavingEffect),

    /**
     * Causes slimes to spawn upon death.
     */
    OOZING(MultiVersionExecutor::oozingEffect),

    /**
     * Chance of spawning silverfish when hurt.
     */
    INFESTED(MultiVersionExecutor::infestedEffect);

    private final PotionEffectTypeGetter potionEffectTypeGetter;

    public PotionEffectType get() {
        return potionEffectTypeGetter.get(MVAccessor.get());
    }

    MVPotionEffectType(PotionEffectTypeGetter potionEffectTypeGetter) {
        this.potionEffectTypeGetter = potionEffectTypeGetter;
    }

    interface PotionEffectTypeGetter {
        PotionEffectType get(MultiVersionExecutor executor);
    }
}
