package com.starshootercity.abilities.types;

import com.starshootercity.APITarget;

/**
 * An ability allowing for code to be run asynchronously.
 * <br><br>
 * Warning: Most Bukkit code does not support being run asynchronously, be very careful when using this.
 */
@APITarget
public interface AsyncRepeatingAbility extends Ability {
    /**
     * @return The interval between each time the run() code should be called
     * @see AsyncRepeatingAbility#run()
     */
    int interval();

    /**
     * Code that should be run asynchronously
     * @see AsyncRepeatingAbility#interval()
     */
    void run();

    /**
     * Code that should run when the ability cycle begins
     */
    void start();
}
