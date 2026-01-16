package com.starshootercity.abilities.types;

import org.bukkit.event.Listener;

/**
 * Allows an ability to register as a listener under certain conditions (such as configuration options)
 */
public interface ListenerAbility extends Listener, Ability {

    /**
     * @return Whether the listener should be registered
     */
    boolean shouldRegisterEvents();
}
