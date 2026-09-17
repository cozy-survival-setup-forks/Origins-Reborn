package com.starshootercity.abilities;

import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class NineLives implements VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("origins:nine_lives");
    }

    @Override
    public String description() {
        return "This origin has standard health.";
    }

    @Override
    public String title() {
        return "Nine Lives";
    }
}
