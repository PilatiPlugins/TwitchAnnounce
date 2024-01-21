package dev.pilati.twitchannounce.spigot.manager;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;

import org.bukkit.configuration.file.YamlConfiguration;

import dev.pilati.twitchannounce.spigot.TwitchAnnounce;
import dev.pilati.twitchannounce.spigot.util.Configuration;

public class ConfigurationManager extends dev.pilati.twitchannounce.core.manager.ConfigurationManager {
    private YamlConfiguration settingsConfiguration;
	private YamlConfiguration configConfiguration;

	public ConfigurationManager(){
		instance = this;
    }

	@Override
    public Configuration loadConfigWithDefaults(){
		LoggingManager.debug("ConfigurationManager.loadConfigWithDefaults -> loading config.yml");
        File configFile = new File(TwitchAnnounce.getInstance().getDataFolder(), "config.yml");
		if (!configFile.exists()) {
			LoggingManager.debug("ConfigurationManager.loadConfigWithDefaults -> config.yml not found, saving default");
			TwitchAnnounce.getInstance().saveResource("config.yml", false);
		}

		configFile = new File(TwitchAnnounce.getInstance().getDataFolder(), "config.yml");
		configConfiguration = YamlConfiguration.loadConfiguration(configFile);
		return new Configuration(configConfiguration);
    }

	@Override
    public Configuration loadSettingsWithDefaults(){
		LoggingManager.debug("ConfigurationManager.loadSettingsWithDefaults -> loading settings.yml");
        File settingsFile = new File(TwitchAnnounce.getInstance().getDataFolder(), "settings.yml");
		if (!settingsFile.exists()) {
			LoggingManager.debug("ConfigurationManager.loadSettingsWithDefaults -> settings.yml not found, saving default");
			TwitchAnnounce.getInstance().saveResource("settings.yml", false);
		}

		settingsFile = new File(TwitchAnnounce.getInstance().getDataFolder(), "settings.yml");
		settingsConfiguration = YamlConfiguration.loadConfiguration(settingsFile);
		return new Configuration(settingsConfiguration);
    }

	@Override
	public void saveSettings() {
		try{
			settingsConfiguration.save(new File(TwitchAnnounce.getInstance().getDataFolder(), "settings.yml"));
			LoggingManager.debug("ConfigurationManager.saveSettings -> settings.yml saved");
		}catch (IOException e){
			LoggingManager.getLogger().log(Level.SEVERE, "error removing streamer", e);
		}
	}

    public static void disable() {
		instance = null;
    }
}
