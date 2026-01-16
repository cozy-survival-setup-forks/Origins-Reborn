package com.starshootercity.abilities.types;

import com.starshootercity.util.SkinManager;
import org.bukkit.entity.Player;
import java.awt.image.BufferedImage;

/**
 * Ability that can change a player's skin
 * <br><br>
 * Requires SkinsRestorer to be set up in the config.yml
 * <br><br>
 * Skins will only register if SkinsRestorer is set up or if forceEnabled is true
 * @see SkinChangingAbility#forceEnabled()
 */
public interface SkinChangingAbility extends Ability {

    /**
     * Modifies the player's skin file
     * @param image A BufferedImage of the player's default skin
     * @param player The player who has the ability
     */
    void modifySkin(BufferedImage image, Player player);

    /**
     * The priority of this ability compared to other skin changing abilities
     * <br><br>
     * Determines the order in which multiple skin changing abilities should work
     * @param player The player who has the ability
     * @return The priority of the ability (default: 0)
     */
    default int getPriority(Player player) {
        return 0;
    }

    /**
     * Will force the ability to be enabled even if SkinsRestorer is not
     * <br><br>
     * Skin modification will still not work, however the ability will still register
     * @return If the ability should be force enabled (default: false)
     */
    default boolean forceEnabled() {
        return false;
    }

    /**
     * Whether the skin of a specific player should be modified by this ability if they have it
     * @param player The player who has the ability
     * @return Whether the skin should be modified (default: true)
     */
    default boolean shouldApply(Player player) {
        return false;
    }

    /**
     * Forces a skin update on the player, useful for when the skin set by this ability changes
     * @param player The player to force a skin update on
     */
    default void forceUpdate(Player player) {
    }
}
