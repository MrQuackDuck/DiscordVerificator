package net.justempire.discordverificator;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.utils.ChunkingFilter;
import net.justempire.discordverificator.commands.LinkCommand;
import net.justempire.discordverificator.commands.ReloadCommand;
import net.justempire.discordverificator.commands.UnlinkCommand;
import net.justempire.discordverificator.configuration.Configuration;
import net.justempire.discordverificator.discord.DiscordBot;
import net.justempire.discordverificator.listeners.JoinListener;
import net.justempire.discordverificator.services.ConfirmationCodeService;
import net.justempire.discordverificator.services.UserManager;
import net.justempire.discordverificator.utils.MessageColorizer;
import org.bukkit.plugin.java.JavaPlugin;

import javax.security.auth.login.LoginException;
import java.util.logging.Logger;

public class DiscordVerificatorPlugin extends JavaPlugin {
    private Logger logger;
    private UserManager userManager;
    private ConfirmationCodeService confirmationCodeService;
    private DiscordBot discordBot;
    private final Configuration config = new Configuration(this);

    private JDA currentJDA;

    @Override
    public void onEnable() {
        // Creating a config if it doesn't exist
        saveDefaultConfig();

        // Setting up the logger
        logger = this.getLogger();

        // Setting up services
        userManager = new UserManager(String.format("%s/users.json", getDataFolder()));
        confirmationCodeService = new ConfirmationCodeService();

        // Setting up the bot
        setupBot();

        // Setting up listeners
        getServer().getPluginManager().registerEvents(new JoinListener(this, userManager, confirmationCodeService), this);

        // Setting up commands
        LinkCommand linkCommand = new LinkCommand(this, userManager);
        getCommand("link").setExecutor(linkCommand);

        UnlinkCommand unlinkCommand = new UnlinkCommand(this, userManager);
        getCommand("unlink").setExecutor(unlinkCommand);

        ReloadCommand reloadCommand = new ReloadCommand(this);
        getCommand("dvreload").setExecutor(reloadCommand);

        logger.info("Enabled successfully!");
    }

    @Override
    public void onDisable() {
        userManager.onShutDown();
        if (currentJDA != null) currentJDA.shutdown();
        logger.info("Shutting down!");
    }

    public DiscordBot getDiscordBot() {
        return discordBot;
    }

    // Setting up Discord bot
    private void setupBot() {
        String token = config.token();
        DiscordBot bot = new DiscordBot(this, logger, userManager, confirmationCodeService);

        try {
            this.currentJDA = JDABuilder.createLight(token)
                    .addEventListeners(bot)
                    .setAutoReconnect(true)
                    .setChunkingFilter(ChunkingFilter.ALL)
                    .setStatus(OnlineStatus.ONLINE)
                    .build();
        }
        catch (LoginException e)
        { logger.severe(MessageColorizer.colorize("Wrong discord bot token provided!")); }

        this.discordBot = bot;
    }

    public void reload() {
        // Trying to shut down the bot
        try { currentJDA.shutdownNow(); }
        catch (Exception ignored) { }

        // Reloading the config
        reloadConfig();

        // Reloading JSON file where users are stored
        userManager.reload();

        // Starting the bot
        setupBot();
    }
}

