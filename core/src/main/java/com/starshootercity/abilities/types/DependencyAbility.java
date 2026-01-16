package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;
import org.bukkit.entity.Player;

/**
 * An ability that can determine whether other abilities are active
 * @see DependantAbility
 */
@APITarget
public interface DependencyAbility extends Ability {

    /**
     * @param player The player with the ability
     * @return Whether the player has this ability state enabled
     */
    boolean isEnabled(Player player);
}
