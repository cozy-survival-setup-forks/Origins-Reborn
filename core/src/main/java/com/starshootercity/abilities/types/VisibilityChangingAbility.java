package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;
import org.bukkit.entity.Player;

/**
 * An ability changing if a player is visible
 */
@APITarget
public interface VisibilityChangingAbility extends Ability {
    /**
     * @param player The player who has the ability
     * @return Whether the player should be invisible
     */
    boolean isInvisible(Player player);
}
