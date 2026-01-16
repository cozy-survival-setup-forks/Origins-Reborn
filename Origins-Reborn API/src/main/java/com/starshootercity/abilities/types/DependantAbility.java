package com.starshootercity.abilities.types;

import com.starshootercity.abilities.PlaceholderDependencyAbility;
import com.starshootercity.util.AbilityRegister;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * An ability that depends on the state of another ability to be considered active
 * @see DependencyAbility
 */
public interface DependantAbility extends Ability {

    /**
     * @return The key of the DependencyAbility required for this ability
     * @see DependencyAbility
     */
    @NotNull
    Key getDependencyKey();

    @NotNull
    default DependencyAbility getDependency() {
        return null;
    }

    /**
     * @return The dependency type
     */
    default DependencyType getDependencyType() {
        return null;
    }

    enum DependencyType {

        /**
         * Ability enables when the DependencyAbility state is true
         */
        REGULAR,
        /**
         * Ability enables when the DependencyAbility state is false
         */
        INVERSE
    }
}
