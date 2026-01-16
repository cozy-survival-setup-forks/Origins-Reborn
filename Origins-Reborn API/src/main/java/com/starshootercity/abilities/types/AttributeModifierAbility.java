package com.starshootercity.abilities.types;

import com.starshootercity.util.config.ConfigManager;
import net.objecthunter.exp4j.ExpressionBuilder;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.Collections;
import java.util.List;

/**
 * An ability that modifies an attribute
 * <br><br>
 * Abilities using this can be configured by users in the ability-config.yml
 */
public interface AttributeModifierAbility extends Ability {

    String OPERATION = null;

    String EXP = null;

    /**
     * @return The attribute to modify
     */
    @NotNull
    Attribute getAttribute();

    /**
     * @param player The player with the ability
     * @return The amount to modify the attribute by (the effect of this depends on the operation)
     * @see AttributeModifierAbility#getOperation()
     */
    double getAmount(Player player);

    /**
     * @return The operation to use to modify the ability
     * @see org.bukkit.attribute.AttributeModifier.Operation
     */
    AttributeModifier.@NotNull Operation getOperation();

    default double getTotalAmount(Player player) {
        return 0;
    }

    default AttributeModifier.@NotNull Operation getActualOperation() {
        return null;
    }

    default void setupAttributeConfig() {
    }
}
