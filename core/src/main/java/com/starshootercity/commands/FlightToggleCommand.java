package com.starshootercity.commands;

import com.starshootercity.OriginsReborn;
import com.starshootercity.util.WorldGuardHook;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

public class FlightToggleCommand implements CommandExecutor {

    private static final Map<Player, Boolean> canFly = new HashMap<>();

    public static boolean canFly(Player player) {
        if (OriginsReborn.isWorldGuardHookInitialized()) {
            if (WorldGuardHook.isFlightDisabled(player.getLocation())) return false;
        }
        return canFly.getOrDefault(player, false);
    }

    public static void setCanFly(Player player, boolean b) {
        canFly.put(player, b);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (sender instanceof Player player) {
            if (canFly(player)) {
                setCanFly(player, false);
                player.sendMessage(Component.text("Disabled flight").color(NamedTextColor.AQUA));
            } else {
                setCanFly(player, true);
                player.sendMessage(Component.text("Enabled flight").color(NamedTextColor.AQUA));
            }
        } else sender.sendMessage(Component.text("This command can only be used by players!").color(NamedTextColor.RED));
        return true;
    }
}
