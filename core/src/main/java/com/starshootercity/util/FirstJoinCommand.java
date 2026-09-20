package com.starshootercity.util;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.config.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.persistence.PersistentDataType;

/**
 * Runs a command for a player the first time they join, such as one that opens an origin menu. The command is set in
 * first-join.command and does nothing while it is empty.
 */
public final class FirstJoinCommand implements Listener {

    private final NamespacedKey doneKey = new NamespacedKey(OriginsReborn.getInstance(), "first_join_command_done");

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        String command = ConfigManager.getConfigValue(ConfigManager.Option.FIRST_JOIN_COMMAND);
        if (command == null || command.isBlank()) return;

        Player player = event.getPlayer();
        if (player.getPersistentDataContainer().has(doneKey, PersistentDataType.BYTE)) return;

        long delay = Math.max(1, ConfigManager.getConfigValue(ConfigManager.Option.FIRST_JOIN_DELAY));
        Bukkit.getScheduler().runTaskLater(OriginsReborn.getInstance(), () -> {
            if (!player.isOnline()) return;

            // Marked before the command runs, so joining again can never open it twice.
            player.getPersistentDataContainer().set(doneKey, PersistentDataType.BYTE, (byte) 1);
            String line = command.trim();
            player.performCommand(line.startsWith("/") ? line.substring(1) : line);
        }, delay);
    }
}
