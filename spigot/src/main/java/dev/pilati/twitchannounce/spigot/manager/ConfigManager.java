package dev.pilati.twitchannounce.spigot.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import dev.pilati.twitchannounce.core.Streamer;
import dev.pilati.twitchannounce.spigot.TwitchAnnounce;


public class ConfigManager extends dev.pilati.twitchannounce.core.manager.ConfigManager{
	FileConfiguration settingsConfig;
	File settingsFile;
    private TwitchAnnounce plugin;

    public ConfigManager(TwitchAnnounce plugin) {
        this.plugin = plugin;
    }

	public void loadConfig() {
		settingsFile = new File(plugin.getDataFolder(), "settings.yml");
		if (!settingsFile.exists()) {
			plugin.saveResource("settings.yml", false);
		}

		settingsFile = new File(plugin.getDataFolder(), "settings.yml");
		settingsConfig = YamlConfiguration.loadConfiguration(settingsFile);
	}
    
    public String getMessageConfig(String config) {
        String message = plugin.getConfig().getString(config);

		if(message == null){
			return null;
		}

        return ChatColor.translateAlternateColorCodes('&', message);
    }

    public List<Streamer> getAllStreamers(){
		List<Streamer> streamers = new ArrayList<>();
		
		ConfigurationSection streamersConfig = this.getSettings().getConfigurationSection("settings.streamers");
		if(streamersConfig == null) {
			return streamers;
		}
		
		for(String key : streamersConfig.getKeys(false)) {
			streamers.add(getStreamer(key));
		}
		
		return streamers;
	}

    public Streamer getStreamer(String twitchUser) {
        String key = twitchUser.toLowerCase();
		Streamer streamer = new Streamer();
		streamer.twitchUser = this.getSettings().getString("settings.streamers." + key + ".twitchUser");
		streamer.minecraftNick = this.getSettings().getString("settings.streamers." + key + ".minecraftNick");
		streamer.priority = this.getSettings().getInt("settings.streamers." + key + ".priority");
		
		if(streamer.minecraftNick == null || streamer.minecraftNick.length() == 0) {
			return null;
		}
		
		return streamer;
	}

    public Streamer getStreamerByNick(String minecraftNick){
        return getAllStreamers().stream().filter(s -> s.minecraftNick.equalsIgnoreCase(minecraftNick)).findFirst().orElse(null);
    }

    public void addStreamer(Streamer streamer) {
		String key = streamer.twitchUser.toLowerCase();
		this.getSettings().set("settings.streamers." + key + ".twitchUser", streamer.twitchUser);
		this.getSettings().set("settings.streamers." + key + ".minecraftNick", streamer.minecraftNick);
		this.getSettings().set("settings.streamers." + key + ".priority", streamer.priority);
		this.saveSettings();
	}

    public void removeStreamer(String twitchUser) {
        String key = twitchUser.toLowerCase();
		this.getSettings().set("settings.streamers." + key, null);
		this.saveSettings();
	}

    public void removeStreamerByNick(String minecraftNick) {
        Streamer streamer = getStreamerByNick(minecraftNick);
        if(streamer != null) {
            removeStreamer(streamer.twitchUser);
        }
    }

    public long getAnnounceTimerSeconds() {
        return plugin.getConfig().getLong("announcementInterval");
    }

	public FileConfiguration getSettings() {
		return settingsConfig;
	}

	public void saveSettings() {
		try{
			this.getSettings().save(settingsFile);
		}catch (IOException e){
			plugin.getLogger().log(Level.SEVERE, "error removing streamer", e);
		}
	}

	@Override
	public String getConfigString(String key) {
		return plugin.getConfig().getString(key);
	}

	@Override
	public boolean getConfigBoolean(String string) {
		return plugin.getConfig().getBoolean(string);
	}

	@Override
	public String getSettingsString(String string) {
		return this.getSettings().getString(string);
	}

	@Override
	public void setSettings(String string, String token) {
		this.getSettings().set(string, token);
	}
}
