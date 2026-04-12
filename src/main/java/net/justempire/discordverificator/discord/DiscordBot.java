package net.justempire.discordverificator.discord;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.ReconnectedEvent;
import net.dv8tion.jda.api.events.ShutdownEvent;
import net.dv8tion.jda.api.events.interaction.SlashCommandEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.justempire.discordverificator.DiscordVerificatorPlugin;
import net.justempire.discordverificator.configuration.Configuration;
import net.justempire.discordverificator.exceptions.InvalidCodeException;
import net.justempire.discordverificator.models.UsernameAndIp;
import net.justempire.discordverificator.services.ConfirmationCodeService;
import net.justempire.discordverificator.services.UserManager;
import net.justempire.discordverificator.exceptions.UserNotFoundException;
import org.jetbrains.annotations.NotNull;
import java.util.logging.Logger;

public class DiscordBot extends ListenerAdapter {
    private final Configuration config;
    private final Logger logger;
    private final UserManager userManager;
    private final ConfirmationCodeService confirmationCodeService;

    private boolean botEnabled = false;

    public DiscordBot(DiscordVerificatorPlugin plugin, Logger logger, UserManager repository, ConfirmationCodeService confirmationCodeService) {
        this.config = new Configuration(plugin);
        this.logger = logger;
        this.userManager = repository;
        this.confirmationCodeService = confirmationCodeService;
    }

    @Override
    public void onShutdown(@NotNull ShutdownEvent event) {
        logger.info("Shutting down the bot!");
        botEnabled = false;
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        CommandData commandData = new CommandData("confirm", config.getMessage("confirm-command"));
        commandData.addOption(OptionType.STRING, "code", config.getMessage("verification-code-you-got"));
        event.getJDA().updateCommands().addCommands(commandData).complete();

        botEnabled = true;
        logger.info("Bot started!");
    }

    @Override
    public void onReconnected(@NotNull ReconnectedEvent event) {
        logger.info("Bot reconnected!");
        botEnabled = true;
    }

    public boolean isBotEnabled() {
        return botEnabled;
    }

    @Override
    public void onSlashCommand(@NotNull SlashCommandEvent event) {
        // If command is "confirm"
        if (event.getName().equals("confirm")) onConfirmSlashCommand(event);
    }

    private void onConfirmSlashCommand(@NotNull SlashCommandEvent event) {
        // Getting ID of sender
        String discordId = event.getUser().getId();

        // Getting the code from command arguments (options)
        OptionMapping code = event.getOption("code");

        // If code wasn't provided
        if (code == null) {
            MessageEmbed embed = generateEmbed(config.getMessage("invalid-usage"), config.getMessage("provide-code-please"), 0xF63B2D);
            event.replyEmbeds(embed).setEphemeral(true).complete();
            return;
        }

        // Trying to get code data
        UsernameAndIp codeData;
        try { codeData = confirmationCodeService.getDataByCodeAndRemove(code.getAsString()); }
        catch (InvalidCodeException e) {
            // Telling the user that code is invalid
            MessageEmbed embed = generateEmbed(config.getMessage("invalid-code"), config.getMessage("invalid-code-description"), 0xF63B2D);
            event.replyEmbeds(embed).setEphemeral(true).complete();
            return;
        }

        try {
            // Return if user tries to confirm someone else's code
            if (!userManager.getByMinecraftUsername(codeData.getUsername()).getDiscordId().equals(discordId)) {
                MessageEmbed embed = generateEmbed(config.getMessage("error-occurred"), config.getMessage("its-not-your-account"), 0xF63B2D);
                event.replyEmbeds(embed).setEphemeral(true).complete();
                return;
            }

            // Confirming the code
            confirmIp(discordId, codeData.getIpAddress());
            MessageEmbed embed = generateEmbed(
                    config.getMessage("allowed"),
                    String.format(config.getMessage("allowed-to-join-from-ip"), codeData.getIpAddress()),
                    0x9ACD32);

            event.replyEmbeds(embed).setEphemeral(true).complete();
        }
        catch (UserNotFoundException e) {
            // Send user the message if he was not found
            MessageEmbed embed = generateEmbed(config.getMessage("user-not-found"), config.getMessage("user-not-found-description"), 0xF63B2D);
            event.replyEmbeds(embed).setEphemeral(true).complete();
        }
    }

    private MessageEmbed generateEmbed(String title, String description, int color) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(description);
        builder.setColor(color);

        return builder.build();
    }

    private void confirmIp(String discordId, String ip) throws UserNotFoundException {
        userManager.updateIp(discordId, ip);
    }
}
