package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;
import com.starshootercity.abilities.PlaceholderDependencyAbility;
import com.starshootercity.util.AbilityRegister;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

/**
 * An ability that depends on the state of another ability to be considered active
 * @see DependencyAbility
 */
@APITarget
public interface DependantAbility extends Ability {

    /**
     * @return The key of the DependencyAbility required for this ability
     * @see DependencyAbility
     */
    @NotNull Key getDependencyKey();

    default @NotNull DependencyAbility getDependency() {
        return AbilityRegister.dependencyAbilityMap.getOrDefault(getDependencyKey(), new PlaceholderDependencyAbility());
    }

    /**
     * @return The dependency type
     */
    default DependencyType getDependencyType() {
        return DependencyType.REGULAR;
    }

    enum DependencyType {
        /**Ability enables when the DependencyAbility state is true*/
        REGULAR,
        /**Ability enables when the DependencyAbility state is false*/
        INVERSE
    }
}
