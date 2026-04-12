package net.justempire.discordverificator.models;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonProperty;
import net.justempire.discordverificator.exceptions.MinecraftUsernameAlreadyLinkedException;
import net.justempire.discordverificator.exceptions.NoCodesFoundException;
import net.justempire.discordverificator.exceptions.NotFoundException;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

@JsonAutoDetect
public class User {
    @JsonProperty("discordId")
    private String discordId;

    @JsonProperty("linkedMinecraftUsernames")
    public List<String> linkedMinecraftUsernames;

    @JsonProperty("latestVerificationsFromIps")
    private List<LastTimeUserReceivedCode> latestVerificationsFromIps;

    @JsonProperty("currentAllowedIp")
    private String currentAllowedIp;

    // Empty constructor for Jackson to serialize/deserialize data from/to JSON
    public User() { }

    public User(String discordUsername, List<String> minecraftUsernames, List<LastTimeUserReceivedCode> latestVerificationsFromIps, String currentAllowedIp) {
        this.discordId = discordUsername;
        this.linkedMinecraftUsernames = new ArrayList<>(minecraftUsernames);
        this.latestVerificationsFromIps = latestVerificationsFromIps;
        this.currentAllowedIp = currentAllowedIp;
    }

    public void setCurrentAllowedIp(String currentAllowedIp) {
        this.currentAllowedIp = currentAllowedIp;
    }

    // Method for updating (adding) the last time user got a code from a certain IP
    public void updateLastTimeUserReceivedCode(String ip) {
        if (latestVerificationsFromIps == null) latestVerificationsFromIps = new ArrayList<>();

        Date now = Date.from(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant());
        for (LastTimeUserReceivedCode verification : latestVerificationsFromIps) {
            if (!ip.equalsIgnoreCase(verification.getIp())) continue;

            verification.setTimeOfReceiving(now);
            return;
        }

        // If not found for specified 'ip' - add new record
        LastTimeUserReceivedCode verificationFromIp = new LastTimeUserReceivedCode(ip, now);
        latestVerificationsFromIps.add(verificationFromIp);
    }

    // Method for clearing all records related to joining the server
    public void clearVerificationHistory() {
        latestVerificationsFromIps = new ArrayList<>();
    }

    // Method for getting the last time the user got a verification code from a certain IP
    // Used to be compared in order to create a delay between generating verification codes
    public Date getLastTimeUserReceivedCode(String ip) throws NoCodesFoundException {
        for (LastTimeUserReceivedCode verification : latestVerificationsFromIps) {
            if (verification.getIp().equalsIgnoreCase(ip))
                return verification.getTimeOfReceiving();
        }

        throw new NoCodesFoundException();
    }

    public String getDiscordId() {
        return discordId;
    }

    public String getCurrentAllowedIp() {
        return currentAllowedIp;
    }

    public boolean isMinecraftUsernameLinked(String username) {
        for (String linkedName : linkedMinecraftUsernames) {
            if (linkedName.equalsIgnoreCase(username)) return true;
        }

        return false;
    }

    public void linkMinecraftUsername(String username) throws MinecraftUsernameAlreadyLinkedException {
        for (String linkedName : linkedMinecraftUsernames) {
            if (linkedName.equalsIgnoreCase(username))
                throw new MinecraftUsernameAlreadyLinkedException();
        }

        linkedMinecraftUsernames.add(username);
    }

    public void unlinkMinecraftUsername(String username) throws NotFoundException {
        Iterator<String> iterator = linkedMinecraftUsernames.iterator();
        while (iterator.hasNext()) {
            String linkedName = iterator.next();
            if (linkedName.equalsIgnoreCase(username)) {
                currentAllowedIp = "";
                iterator.remove();
                return;
            }
        }

        throw new NotFoundException();
    }
}
