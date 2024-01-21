package dev.pilati.twitchannounce.core.manager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.LogManager;
import java.util.stream.Collectors;

import dev.pilati.twitchannounce.core.util.Configuration;
import dev.pilati.twitchannounce.core.util.Streamer;
import dev.pilati.twitchannounce.core.OutdatedException;

public abstract class ConfigurationManager {
    protected static ConfigurationManager instance;
    protected Configuration config;
    protected Configuration settings;
    protected String[] orderBehavior;

    public static ConfigurationManager getInstance() {
        return instance;
    }

    protected abstract Configuration loadConfigWithDefaults() throws IOException;
    protected abstract Configuration loadSettingsWithDefaults() throws IOException;

    public static Configuration getConfig(){
        return getInstance().config;
    }

    public static Configuration getSettings(){
        return getInstance().settings;
    }

    public abstract void saveSettings();

    public static void loadConfiguration() throws OutdatedException, IOException{
        LoggingManager.debug("Loading configuration files...");
        getInstance().config = getInstance().loadConfigWithDefaults();
        getInstance().settings = getInstance().loadSettingsWithDefaults();

        LoggingManager.debug = getConfig().getBoolean("debug");

        handleUpdates();

        getInstance().orderBehavior = ConfigurationManager.getConfig().getString("announcement.orderBehavior").split(",");
        for(int i = 0; i < getInstance().orderBehavior.length; i++){
            getInstance().orderBehavior[i] = String.valueOf(getInstance().orderBehavior[i]).trim().toLowerCase();
        }
    }

    private static void handleUpdates() throws OutdatedException{
        if(!"1.3".equals(getConfig().getString("version"))) {
            throw new OutdatedException("Please backup your config.yml and re-create");
        }
    }

    public static List<Streamer> getAllStreamers(){
		List<Streamer> streamers = new ArrayList<>();
		
		Configuration streamersConfig = getSettings().getConfigurationSection("settings.streamers");
		if(streamersConfig == null) {
			return streamers;
		}
		
		for(String key : streamersConfig.getKeys(false)) {
			streamers.add(getStreamer(key));
		}

        if(LoggingManager.debug){
            LoggingManager.debug("Loaded " + streamers.size() + " streamers, list: " + 
                streamers.stream().map(s -> s.twitchUser).collect(Collectors.joining(", "))
            );
        }
		
		return streamers;
	}

    public static Streamer getStreamer(String twitchUser) {
        LoggingManager.debug(() -> String.format("Getting streamer %s", twitchUser));
        String key = twitchUser.toLowerCase();
		Streamer streamer = new Streamer();
		streamer.twitchUser = getSettings().getString("settings.streamers." + key + ".twitchUser");
		streamer.minecraftNick = getSettings().getString("settings.streamers." + key + ".minecraftNick");
		streamer.priority = getSettings().getInt("settings.streamers." + key + ".priority");
		
		if(streamer.minecraftNick == null || streamer.minecraftNick.length() == 0) {
			return null;
		}
		
		return streamer;
	}

    public static Streamer getStreamerByNick(String minecraftNick){
        return getAllStreamers().stream().filter(s -> s.minecraftNick.equalsIgnoreCase(minecraftNick)).findFirst().orElse(null);
    }

    public static void addStreamer(Streamer streamer) {
		String key = streamer.twitchUser.toLowerCase();
		getSettings().set("settings.streamers." + key + ".twitchUser", streamer.twitchUser);
		getSettings().set("settings.streamers." + key + ".minecraftNick", streamer.minecraftNick);
		getSettings().set("settings.streamers." + key + ".priority", streamer.priority);
		getInstance().saveSettings();
        LoggingManager.debug(() -> String.format("Added streamer %s", streamer.twitchUser));
	}

    public static void removeStreamer(String twitchUser) {
        String key = twitchUser.toLowerCase();
		getSettings().set("settings.streamers." + key, null);
		getInstance().saveSettings();
        LoggingManager.debug(() -> String.format("Removed streamer %s", twitchUser));
	}

    public static void removeStreamerByNick(String minecraftNick) {
        Streamer streamer = getStreamerByNick(minecraftNick);
        if(streamer != null) {
            removeStreamer(streamer.twitchUser);
        }
    }

    public static String[] getOrderBehavior(){
        return getInstance().orderBehavior;
    }
}
