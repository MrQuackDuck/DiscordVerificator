package net.justempire.discordverificator.commands;

import net.justempire.discordverificator.DiscordVerificatorPlugin;
import net.justempire.discordverificator.configuration.Configuration;
import net.justempire.discordverificator.services.UserManager;
import net.justempire.discordverificator.exceptions.NotFoundException;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class UnlinkCommand implements CommandExecutor {
    private final Configuration config;
    private final UserManager userManager;

    public UnlinkCommand(DiscordVerificatorPlugin plugin, UserManager userManager) {
        this.config = new Configuration(plugin);
        this.userManager = userManager;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] arguments) {
        if (!commandSender.hasPermission("discordVerificator.unlink")) {
            commandSender.sendMessage(config.getMessage("not-enough-permissions"));
            return true;
        }

        if (arguments.length != 1) {
            commandSender.sendMessage(config.getMessage("invalid-unlink-format"));
            return true;
        }

        try {
            userManager.unlinkUser(arguments[0]);
            commandSender.sendMessage(config.getMessage("successfully-unlinked"));
            return true;
        }
        catch (NotFoundException e) {
            commandSender.sendMessage(config.getMessage("player-was-not-linked"));
            return true;
        }
    }
}
