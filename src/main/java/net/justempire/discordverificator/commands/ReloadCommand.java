package net.justempire.discordverificator.commands;

import net.justempire.discordverificator.DiscordVerificatorPlugin;
import net.justempire.discordverificator.configuration.Configuration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class ReloadCommand implements CommandExecutor {
    private final DiscordVerificatorPlugin plugin;
    private final Configuration config;

    public ReloadCommand(DiscordVerificatorPlugin plugin) {
        this.plugin = plugin;
        this.config = new Configuration(plugin);
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] arguments) {
        if (!commandSender.hasPermission("discordVerificator.reload")) {
            commandSender.sendMessage(config.getMessage("not-enough-permissions"));
            return true;
        }

        plugin.reload();
        commandSender.sendMessage(config.getMessage("reloaded"));

        return true;
    }
}
