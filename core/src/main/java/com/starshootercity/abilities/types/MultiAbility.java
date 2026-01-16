package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;

import java.util.List;

/**
 * An ability that allows multiple abilities to be applied with one key
 * <br><br>
 * Useful if you need to do something like modify multiple attributes with one ability
 */
@APITarget
public interface MultiAbility extends Ability {

    /**
     * @return The abilities a player with this Multi Ability should have, these abilities should also be registered
     */
    List<Ability> getAbilities();
}
