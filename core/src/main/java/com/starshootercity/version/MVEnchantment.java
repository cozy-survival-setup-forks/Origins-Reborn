package com.starshootercity.version;

import com.starshootercity.APITarget;
import org.bukkit.enchantments.Enchantment;

/**
 * Allows retrieval of enchantments across versions where the enum names may have changed
 * <br><br>
 * Enchantments will be null on versions where they do not exist
 */
@APITarget
public enum MVEnchantment {
    /**
     * Provides protection against environmental damage
     */
    PROTECTION(MultiVersionExecutor::protectionEnchantment),

    /**
     * Provides protection against water damage
     */
    WATER_PROTECTION(executor -> null),

    /**
     * Provides protection against fire damage
     */
    FIRE_PROTECTION(MultiVersionExecutor::fireProtectionEnchantment),

    /**
     * Provides protection against fall damage
     */
    FEATHER_FALLING(MultiVersionExecutor::featherFallingEnchantment),

    /**
     * Provides protection against explosive damage
     */
    BLAST_PROTECTION(MultiVersionExecutor::blastProtectionEnchantment),

    /**
     * Provides protection against projectile damage
     */
    PROJECTILE_PROTECTION(MultiVersionExecutor::projectileProtectionEnchantment),

    /**
     * Decreases the rate of air loss whilst underwater
     */
    RESPIRATION(MultiVersionExecutor::respirationEnchantment),

    /**
     * Increases the speed at which a player may mine underwater
     */
    AQUA_AFFINITY(MultiVersionExecutor::aquaAffinityEnchantment),

    /**
     * Damages the attacker
     */
    THORNS(MultiVersionExecutor::thornsEnchantment),

    /**
     * Increases walking speed while in water
     */
    DEPTH_STRIDER(MultiVersionExecutor::depthStriderEnchantment),

    /**
     * Freezes any still water adjacent to ice / frost which player is walking on
     */
    FROST_WALKER(MultiVersionExecutor::frostWalkerEnchantment),

    /**
     * Item cannot be removed
     */
    BINDING_CURSE(MultiVersionExecutor::bindingCurseEnchantment),

    /**
     * Increases damage against all targets
     */
    SHARPNESS(MultiVersionExecutor::sharpnessEnchantment),

    /**
     * Increases damage against undead targets
     */
    SMITE(MultiVersionExecutor::smiteEnchantment),

    /**
     * Increases damage against arthropod targets
     */
    BANE_OF_ARTHROPODS(MultiVersionExecutor::baneOfArthropodsEnchantment),

    /**
     * All damage to other targets will knock them back when hit
     */
    KNOCKBACK(MultiVersionExecutor::knockbackEnchantment),

    /**
     * When attacking a target, has a chance to set them on fire
     */
    FIRE_ASPECT(MultiVersionExecutor::fireAspectEnchantment),

    /**
     * Provides a chance of gaining extra loot when killing monsters
     */
    LOOTING(MultiVersionExecutor::lootingEnchantment),

    /**
     * Increases damage against targets when using a sweep attack
     */
    SWEEPING_EDGE(MultiVersionExecutor::sweepingEdgeEnchantment),

    /**
     * Increases the rate at which you mine/dig
     */
    EFFICIENCY(MultiVersionExecutor::efficiencyEnchantment),

    /**
     * Allows blocks to drop themselves instead of fragments (for example,
     * stone instead of cobblestone)
     */
    SILK_TOUCH(MultiVersionExecutor::silkTouchEnchantment),

    /**
     * Decreases the rate at which a tool looses durability
     */
    UNBREAKING(MultiVersionExecutor::unbreakingEnchantment),

    /**
     * Provides a chance of gaining extra loot when destroying blocks
     */
    FORTUNE(MultiVersionExecutor::fortuneEnchantment),

    /**
     * Provides extra damage when shooting arrows from bows
     */
    POWER(MultiVersionExecutor::powerEnchantment),

    /**
     * Provides a knockback when an entity is hit by an arrow from a bow
     */
    PUNCH(MultiVersionExecutor::punchEnchantment),

    /**
     * Sets entities on fire when hit by arrows shot from a bow
     */
    FLAME(MultiVersionExecutor::flameEnchantment),

    /**
     * Provides infinite arrows when shooting a bow
     */
    INFINITY(MultiVersionExecutor::infinityEnchantment),

    /**
     * Decreases odds of catching worthless junk
     */
    LUCK_OF_THE_SEA(MultiVersionExecutor::luckOfTheSeaEnchantment),

    /**
     * Increases rate of fish biting your hook
     */
    LURE(MultiVersionExecutor::lureEnchantment),

    /**
     * Causes a thrown trident to return to the player who threw it
     */
    LOYALTY(MultiVersionExecutor::loyaltyEnchantment),

    /**
     * Deals more damage to mobs that live in the ocean
     */
    IMPALING(MultiVersionExecutor::impalingEnchantment),

    /**
     * When it is rainy, launches the player in the direction their trident is thrown
     */
    RIPTIDE(MultiVersionExecutor::riptideEnchantment),

    /**
     * Strikes lightning when a mob is hit with a trident if conditions are
     * stormy
     */
    CHANNELING(MultiVersionExecutor::channelingEnchantment),

    /**
     * Shoot multiple arrows from crossbows
     */
    MULTISHOT(MultiVersionExecutor::multishotEnchantment),

    /**
     * Charges crossbows quickly
     */
    QUICK_CHARGE(MultiVersionExecutor::quickChargeEnchantment),

    /**
     * Crossbow projectiles pierce entities
     */
    PIERCING(MultiVersionExecutor::piercingEnchantment),

    /**
     * Increases fall damage of maces
     */
    DENSITY(MultiVersionExecutor::densityEnchantment),

    /**
     * Reduces armor effectiveness against maces
     */
    BREACH(MultiVersionExecutor::breachEnchantment),

    /**
     * Emits wind burst upon hitting enemy
     */
    WIND_BURST(MultiVersionExecutor::windBurstEnchantment),

    /**
     * Allows mending the item using experience orbs
     */
    MENDING(MultiVersionExecutor::mendingEnchantment),

    /**
     * Item disappears instead of dropping
     */
    VANISHING_CURSE(MultiVersionExecutor::vanishingCurseEnchantment),

    /**
     * Walk quicker on soul blocks
     */
    SOUL_SPEED(MultiVersionExecutor::soulSpeedEnchantment),

    /**
     * Walk quicker while sneaking
     */
    SWIFT_SNEAK(MultiVersionExecutor::swiftSneakEnchantment);

    private EnchantmentGetter enchantmentGetter;

    public void set(EnchantmentGetter getter) {
        this.enchantmentGetter = getter;
    }

    public Enchantment get() {
        return enchantmentGetter.get(MVAccessor.get());
    }

    MVEnchantment(EnchantmentGetter enchantmentGetter) {
        this.enchantmentGetter = enchantmentGetter;
    }

    public interface EnchantmentGetter {
        Enchantment get(MultiVersionExecutor executor);
    }
}
