package com.starshootercity.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.starshootercity.OriginsReborn;
import com.starshootercity.abilities.types.Ability;
import com.starshootercity.skript.ComplexSkriptAbility;
import com.starshootercity.skript.BasicSkriptAbility;
import com.starshootercity.skript.NamedSkriptAbility;
import com.starshootercity.util.AbilityRegister;
import net.kyori.adventure.key.Key;
import org.bukkit.event.Event;
import org.intellij.lang.annotations.Subst;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
public class EffAbility extends Effect {

    static {
        Skript.registerEffect(EffAbility.class, "(register|create) [(a new|an|a)] (ability|power) with (key|id) %string% [[and] (title|name) %-string% [and] description %-string%]");
    }

    @Override
    protected void execute(@NotNull Event event) {
        if (ability == null) return;
        @Subst("skript:ability") String key = ability.getSingle(event);
        if (key == null) return;
        if ((title == null) != (description == null)) return;
        Ability a;
        List<Ability> abilities = new ArrayList<>();

        if (title != null) {
            String titl = title.getSingle(event);
            String desc = description.getSingle(event);
            Key displayKey = Key.key(key + "_display");
            NamedSkriptAbility nsa = new NamedSkriptAbility(displayKey, titl, desc);
            AbilityRegister.registerAbility(nsa, displayKey, Skript.getInstance());
            abilities.add(nsa);
        }

        Key abilityKey = Key.key(key);
        if (abilities.isEmpty()) {
            a = new BasicSkriptAbility(abilityKey);
        } else {
            a = new ComplexSkriptAbility(
                    abilityKey,
                    abilities
            );
        }

        OriginsReborn.usesSkriptAbilities = true;
        AbilityRegister.registerAbility(
                a,
                abilityKey,
                Skript.getInstance()
        );
    }

    private Expression<String> ability;
    private Expression<String> title;
    private Expression<String> description;

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) {
        if (event == null) return "Ability register effect";
        if ((title != null) && (description != null)) {
            return "Ability register effect with expression ability\">: " + ability.toString(event, debug) + " and title\">: " + title.toString(event, debug) + " and description\">: " + description.toString(event, debug);
        }
        return "Ability register effect with expression ability\">: " + ability.toString(event, debug);
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean init(Expression<?> @NotNull [] expressions, int matchedPattern, @NotNull Kleenean isDelayed, SkriptParser.@NotNull ParseResult parseResult) {
        this.ability = (Expression<String>) expressions[0];
        this.title = (Expression<String>) expressions[1];
        this.description = (Expression<String>) expressions[2];
        return true;
    }
}
