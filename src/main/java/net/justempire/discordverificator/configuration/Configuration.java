package net.justempire.discordverificator.configuration;

import org.bukkit.plugin.java.JavaPlugin;

public class Configuration extends MessageConfigurationBase {
    public Configuration(JavaPlugin plugin) {
        super(plugin, "messages");
    }

    public String token() {
        return getString("token");
    }

    public long codeDelay() {
        return getLong("codeDelay");
    }
}