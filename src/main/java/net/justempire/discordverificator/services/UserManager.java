package net.justempire.discordverificator.services;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import net.justempire.discordverificator.exceptions.MinecraftUsernameAlreadyLinkedException;
import net.justempire.discordverificator.exceptions.NotFoundException;
import net.justempire.discordverificator.exceptions.UserNotFoundException;
import net.justempire.discordverificator.models.User;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

// Performs actions on users and loads/saves from/to JSON
public class UserManager {
    private List<User> userList = new ArrayList<>();
    private String pathToJson;

    public UserManager(String pathToJson) {
        setUp(pathToJson);
    }

    public User getByDiscordId(String discordId) throws UserNotFoundException {
        for (User user : userList) {
            // Return the user if found
            if (user.getDiscordId().equalsIgnoreCase(discordId))
                return user;
        }

        throw new UserNotFoundException();
    }

    public User getByMinecraftUsername(String minecraftUsername) throws UserNotFoundException {
        for (User user : userList) {
            for (String username : user.linkedMinecraftUsernames) {
                if (minecraftUsername.equalsIgnoreCase(username))
                    return user;
            }
        }

        throw new UserNotFoundException();
    }

    public void updateLastTimeUserReceivedCode(String discordId, String ip) throws UserNotFoundException {
        User user = getByDiscordId(discordId);
        user.updateLastTimeUserReceivedCode(ip);
        saveUsers();
    }

    public void updateIp(String discordId, String newIp) throws UserNotFoundException {
        for (User user : userList) {
            if (!user.getDiscordId().equals(discordId)) continue;

            user.setCurrentAllowedIp(newIp);
            saveUsers();
            return;
        }

        throw new UserNotFoundException();
    }

    public void linkUser(String discordId, String minecraftUsername) throws MinecraftUsernameAlreadyLinkedException {
        for (User user : userList) {
            if (user.isMinecraftUsernameLinked(minecraftUsername)) throw new MinecraftUsernameAlreadyLinkedException();
            if (!user.getDiscordId().equals(discordId)) continue;

            // If user found, link his minecraft account
            user.linkMinecraftUsername(minecraftUsername);

            saveUsers();
            return;
        }

        // If user wasn't found, create one
        User user = new User(discordId, Arrays.asList(minecraftUsername), new ArrayList<>(), "");
        addUser(user);
    }

    private void addUser(User userToAdd) {
        userList.add(userToAdd);
        saveUsers();
    }

    public void unlinkUser(String minecraftUsername) throws NotFoundException {
        for (User user : userList) {
            if (!user.isMinecraftUsernameLinked(minecraftUsername)) continue;

            user.unlinkMinecraftUsername(minecraftUsername);

            saveUsers();
            return;
        }

        throw new NotFoundException();
    }

    private void loadUsers() throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        File source = new File(pathToJson);

        try { userList = objectMapper.readValue(source, new TypeReference<>() {}); }
        catch (FileNotFoundException e) {
            // Create JSON file if it didn't exist
            if (source.createNewFile()) loadUsers();
        }
    }

    private void saveUsers() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, true);
        try { objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(pathToJson), userList); }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    private void setUp(String pathToJson) {
        try {
            this.pathToJson = pathToJson;

            // Trying to load users from JSON
            loadUsers();
        }
        catch (MismatchedInputException e) {
            userList = new ArrayList<>();
            saveUsers();
            try { loadUsers(); }
            catch (IOException ex) { throw new RuntimeException(); }
        }
        catch (IOException e) { throw new RuntimeException(e); }
    }

    public void reload() {
        setUp(pathToJson);
    }

    public void onShutDown() {
        saveUsers();
    }
}
