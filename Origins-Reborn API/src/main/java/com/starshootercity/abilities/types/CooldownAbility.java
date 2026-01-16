package com.starshootercity.abilities.types;

import com.starshootercity.OriginsReborn;
import com.starshootercity.cooldowns.Cooldowns;
import com.starshootercity.util.config.ConfigManager;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import java.util.Collections;

/**
 * An ability used to create cooldowns that may use the Origins-Reborn cooldown bar system on the HUD
 */
@SuppressWarnings("unused")
public interface CooldownAbility extends Ability {

    String COOLDOWN = null;

    default NamespacedKey getCooldownKey() {
        return null;
    }

    /**
     * Sets the cooldown of this ability to the default amount
     * @param player The player with the ability
     */
    default void setCooldown(Player player) {
    }

    /**
     * Sets the cooldown of this ability to a specified amount
     * @param player The player with the ability
     * @param amount The duration of the cooldown
     */
    default void setCooldown(Player player, int amount) {
    }

    /**
     * Checks if a player is on cooldown for this ability
     * @param player The player with the ability
     * @return Whether this ability is on cooldown
     */
    default boolean hasCooldown(Player player) {
        return false;
    }

    /**
     * @param player The player with the ability
     * @return The current cooldown of this ability
     */
    default long getCooldown(Player player) {
        return 0;
    }

    /**
     * @return CooldownInfo about the ability such as cooldown duration, icon, etc.
     */
    Cooldowns.CooldownInfo getCooldownInfo();

    default void setupCooldownConfig(JavaPlugin instance) {
    }
}
