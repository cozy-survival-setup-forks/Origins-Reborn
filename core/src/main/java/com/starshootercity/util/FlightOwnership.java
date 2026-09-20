package com.starshootercity.util;

import com.starshootercity.abilities.types.Ability;
import com.starshootercity.util.config.ConfigManager;
import net.kyori.adventure.key.Key;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerToggleFlightEvent;

/**
 * Lets Origins and other flight plugins share a player. A plugin that controls a player's flight puts the scoreboard
 * tag from misc-settings.external-flight-tag on them. While the tag is there Origins leaves their flight alone, and
 * Origins puts misc-settings.origins-flight-tag on players who have the elytra ability so the other plugin can tell.
 */
public final class FlightOwnership implements Listener {

    private static final Key ELYTRA = Key.key("origins:elytra");

    /** True while another plugin controls the flight of this player. */
    public static boolean ownedByOther(Player player) {
        String tag = ConfigManager.getConfigValue(ConfigManager.Option.EXTERNAL_FLIGHT_TAG);
        return tag != null && !tag.isBlank() && player.getScoreboardTags().contains(tag);
    }

    /** Adds or removes the tag that shows a player has flight from their origin. */
    public static void refreshTag(Player player) {
        String tag = ConfigManager.getConfigValue(ConfigManager.Option.ORIGINS_FLIGHT_TAG);
        if (tag == null || tag.isBlank()) return;

        Ability elytra = AbilityRegister.abilityMap.get(ELYTRA);
        if (elytra != null && elytra.hasAbility(player)) {
            if (!player.getScoreboardTags().contains(tag)) player.addScoreboardTag(tag);
        } else if (player.getScoreboardTags().contains(tag)) {
            player.removeScoreboardTag(tag);
        }
    }

    public static void removeTag(Player player) {
        String tag = ConfigManager.getConfigValue(ConfigManager.Option.ORIGINS_FLIGHT_TAG);
        if (tag != null && !tag.isBlank()) player.removeScoreboardTag(tag);
    }

    /**
     * Runs after the flight handlers of the abilities. Whatever they did, a player whose flight belongs to another
     * plugin gets their flight toggle, and does not glide.
     */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerToggleFlight(PlayerToggleFlightEvent event) {
        Player player = event.getPlayer();
        if (!ownedByOther(player)) return;

        event.setCancelled(false);
        if (player.isGliding()) player.setGliding(false);
        if (!player.getAllowFlight()) player.setAllowFlight(true);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        removeTag(event.getPlayer());
    }
}
