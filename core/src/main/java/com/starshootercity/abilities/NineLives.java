package com.starshootercity.abilities;

import com.starshootercity.abilities.types.AttributeModifierAbility;
import com.starshootercity.abilities.types.VisibleAbility;
import com.starshootercity.version.MVAttribute;
import net.kyori.adventure.key.Key;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class NineLives implements AttributeModifierAbility, VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:nine_lives");
    }

    @Override
    public String description() {
        return "You have 1 less heart of health than humans.";
    }

    @Override
    public String title() {
        return "Nine Lives";
    }

    @Override
    public @NotNull Attribute getAttribute() {
        return MVAttribute.MAX_HEALTH.get();
    }

    @Override
    public double getAmount(Player player) {
        return -2;
    }

    @Override
    public AttributeModifier.@NotNull Operation getOperation() {
        return AttributeModifier.Operation.ADD_NUMBER;
    }
}
