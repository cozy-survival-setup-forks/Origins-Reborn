package com.starshootercity.skript.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Getter;
import com.starshootercity.events.PlayerSwapOriginEvent;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("unused")
public class EvtPlayerSwapOrigin extends SkriptEvent {

    static {
        Skript.registerEvent("Origin Swap", EvtPlayerSwapOrigin.class, PlayerSwapOriginEvent.class, "[player] origin (swap|chang(e|ing))");

        EventValues.registerEventValue(PlayerSwapOriginEvent.class, String.class, new Getter<String, PlayerSwapOriginEvent>() {
            @Override
            public @NotNull String get(PlayerSwapOriginEvent event) {
                return "";
            }
        }, 0);
    }

    @Override
    public boolean init(Literal<?> @NotNull [] literals, int i, SkriptParser.@NotNull ParseResult parseResult) {
        return false;
    }

    @Override
    public boolean check(@NotNull Event event) {
        return false;
    }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean b) {
        return "Player ";
    }
}
