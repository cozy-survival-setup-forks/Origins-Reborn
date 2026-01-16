package com.starshootercity;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

@SuppressWarnings("UnstableApiUsage")
public class CommandAdapter implements BasicCommand {

    private final @NotNull CommandExecutor executor;
    private final @Nullable TabCompleter completer;
    private final @NotNull String name;
    private final @NotNull Command command;

    public CommandAdapter(@NotNull String name, @NotNull CommandExecutor executor) {
        this.name = name;
        this.executor = executor;
        if (executor instanceof TabCompleter tabCompleter) {
            this.completer = tabCompleter;
        } else this.completer = null;

        this.command = new Command(name) {
            @Override
            public boolean execute(@NotNull CommandSender sender, @NotNull String commandLabel, @NotNull String @NotNull [] args) {
                return executor.onCommand(sender, this, commandLabel, args);
            }
        };
    }

    @Override
    public void execute(CommandSourceStack commandSourceStack, String @NotNull [] args) {
        executor.onCommand(
                commandSourceStack.getSender(),
                command,
                name,
                args
        );
    }

    @Override
    public @NotNull Collection<String> suggest(@NotNull CommandSourceStack commandSourceStack, String @NotNull [] args) {
        if (completer == null) return Collections.emptyList();
        else {
            return Objects.requireNonNullElse(
                    completer.onTabComplete(
                        commandSourceStack.getSender(),
                        command,
                        name,
                        args
            ), Collections.emptyList());
        }
    }
}
