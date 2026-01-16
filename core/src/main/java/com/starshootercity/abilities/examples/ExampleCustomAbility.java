package com.starshootercity.abilities.examples;

import com.starshootercity.abilities.types.VisibleAbility;
import net.kyori.adventure.key.Key;
import org.jetbrains.annotations.NotNull;

public class ExampleCustomAbility implements VisibleAbility {
    @Override
    public @NotNull Key getKey() {
        return Key.key("my_cool_addon", "my_ability");
    }

    @Override
    public String description() {
        return "My Description";
    }

    @Override
    public String title() {
        return "My Title";
    }
}
