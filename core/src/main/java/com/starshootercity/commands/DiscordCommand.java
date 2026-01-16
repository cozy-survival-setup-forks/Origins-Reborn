package com.starshootercity.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class DiscordCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        sendURL(sender);
        return true;
    }

    public static void sendURL(CommandSender sender) {
        String discordURL = "https://discord.gg/PyH6uGBTFG";
        sender.sendMessage(Component.text().append(Component.text(discordURL).color(NamedTextColor.AQUA).decorate(TextDecoration.UNDERLINED).clickEvent(ClickEvent.openUrl(discordURL))));
        sender.sendMessage(Component.text("Note: This discord server is specifically for help with the Origins-Reborn plugin").color(NamedTextColor.AQUA));
        sender.sendMessage(Component.text("It is NOT for the Minecraft server you are currently playing on").color(NamedTextColor.AQUA));
    }
}
