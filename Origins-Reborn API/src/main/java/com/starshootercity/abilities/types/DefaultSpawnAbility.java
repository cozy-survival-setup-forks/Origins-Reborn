package com.starshootercity.abilities.types;

import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

/**
 * An ability that changes which dimension a player will spawn in by default
 */
public interface DefaultSpawnAbility extends Ability {

    /**
     * @return The dimension the player should spawn in
     */
    @Nullable
    World getWorld();
}
