package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;
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
@APITarget
public interface AttributeModifierAbility extends Ability {

    String OPERATION = "attribute-modifier.operation";
    String EXP = "attribute-modifier.expression";

    /**
     * @return The attribute to modify
     */
    @NotNull Attribute getAttribute();

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
        double total = getAmount(player);

        if (disallowConfiguration()) return total;

        if (total != 0) {
            String modifiedValue = getConfigOption(EXP, ConfigManager.SettingType.STRING);
            if (modifiedValue.equals("x")) return total;
            try {
                return new ExpressionBuilder(modifiedValue).variables("x").build().setVariable("x", total).evaluate();
            } catch (Throwable ignored) {}
        }
        return total;
    }

    default AttributeModifier.@NotNull Operation getActualOperation() {
        if (disallowConfiguration()) return getOperation();

        String op = getConfigOption(OPERATION, ConfigManager.SettingType.STRING).toLowerCase();
        return switch (op) {
            case "add_scalar" -> AttributeModifier.Operation.ADD_SCALAR;
            case "add_number" -> AttributeModifier.Operation.ADD_NUMBER;
            case "multiply_scalar_1" -> AttributeModifier.Operation.MULTIPLY_SCALAR_1;
            default -> getOperation();
        };
    }

    default void setupAttributeConfig() {
        if (disallowConfiguration()) return;
        registerConfigOption(OPERATION, Collections.singletonList("The operation to use ('ADD_SCALAR', 'ADD_NUMBER' or 'MULTIPLY_SCALAR_1')"), ConfigManager.SettingType.STRING, getOperation().toString());
        registerConfigOption(EXP, List.of("A mathematical expression used to modify the attribute, where 'x' is a variable representing the default modifier.", "Example: 'x * 3' will triple the value used in the modifier."), ConfigManager.SettingType.STRING, "x");
    }

    default boolean disallowConfiguration() {
        return false;
    }
}
