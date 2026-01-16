package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;
import net.kyori.adventure.util.TriState;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * An ability allowing players to fly
 */
@APITarget
public interface FlightAllowingAbility extends Ability {

    /**
     * @param player The player with the ability
     * @return Whether the player should be able to fly
     */
    boolean canFly(Player player);

    /**
     * @param player The player with the ability
     * @return The flight speed the player should have
     */
    float getFlightSpeed(Player player);

    /**
     * @param player The player with the ability
     * @return Whether the player should be forced to fly
     */
    default boolean forceFly(Player player) {
        return false;
    }

    /**
     * @param player The player with the ability
     * @return A TriState representing whether the player should take fall damage
     */
    default @NotNull TriState getFlyingFallDamage(Player player) {
        return TriState.FALSE;
    }

    /**
     * @return The priority of this ability, higher priority abilities are checked first
     */
    default int getPriority() {
        return 1;
    }
}
