package net.justempire.discordverificator.listeners;

import net.justempire.discordverificator.DiscordVerificatorPlugin;
import net.justempire.discordverificator.configuration.Configuration;
import net.justempire.discordverificator.exceptions.NoCodesFoundException;
import net.justempire.discordverificator.models.User;
import net.justempire.discordverificator.services.ConfirmationCodeService;
import net.justempire.discordverificator.services.UserManager;
import net.justempire.discordverificator.exceptions.UserNotFoundException;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class JoinListener implements Listener {
    private final UserManager userManager;
    private final DiscordVerificatorPlugin plugin;
    private final Configuration config;
    private final ConfirmationCodeService confirmationCodeService;

    public JoinListener(DiscordVerificatorPlugin plugin, UserManager userManager, ConfirmationCodeService confirmationCodeService) {
        this.userManager = userManager;
        this.plugin = plugin;
        this.config = new Configuration(plugin);
        this.confirmationCodeService = confirmationCodeService;
    }

    @EventHandler
    public void onPlayerJoined(PlayerLoginEvent event) throws UserNotFoundException {
        User user;
        Player player = event.getPlayer();
        String ipAddress = event.getAddress().getHostAddress();

        // Trying to get the user by his linked in-game username
        try { user = userManager.getByMinecraftUsername(player.getName());}
        catch (UserNotFoundException e) {
            // If user wasn't found
            preventJoin(event, config.getMessage("account-not-linked"));
            return;
        }

        // Check if bot is working
        if (!plugin.getDiscordBot().isBotEnabled()) {
            preventJoin(event, config.getMessage("bot-not-working"));
            return;
        }

        // Ensure that no more than the configured amount of seconds has passed since this player last joined
        if (!user.getCurrentAllowedIp().equals(ipAddress)) {
            Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
            Date latestVerificationSent;
            try {
                latestVerificationSent = user.getLastTimeUserReceivedCode(ipAddress);
                long differenceInSeconds = getDifferenceInSeconds(latestVerificationSent, now);
                if (differenceInSeconds < config.codeDelay()) {
                    preventJoin(event, String.format(config.getMessage("wait-until-verification"), config.codeDelay() - differenceInSeconds));
                    return;
                }
            }
            catch (NoCodesFoundException e) {
                userManager.updateLastTimeUserReceivedCode(user.getDiscordId(), ipAddress);
            }
        }

        // If current IP is not equal to IP in database, show verification code to the user
        if (!user.getCurrentAllowedIp().equals(ipAddress)) {
            String code = confirmationCodeService.generateVerificationCode(player.getName(), ipAddress);

            userManager.updateLastTimeUserReceivedCode(user.getDiscordId(), ipAddress);

            event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
            preventJoin(event, String.format(config.getMessage("confirm-with-command"), code));
        }
    }

    private long getDifferenceInSeconds(Date startDate, Date endDate) {
        LocalDateTime startDateTime = startDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        LocalDateTime endDateTime = endDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();

        Duration duration = Duration.between(startDateTime, endDateTime);
        return duration.getSeconds();
    }

    private void preventJoin(PlayerLoginEvent event, String message) {
        event.setResult(PlayerLoginEvent.Result.KICK_OTHER);
        event.setKickMessage(message);
        event.disallow(event.getResult(), message);
    }
}
