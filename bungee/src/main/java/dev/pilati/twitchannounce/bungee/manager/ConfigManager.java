package dev.pilati.twitchannounce.bungee.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;
import dev.pilati.twitchannounce.core.Streamer;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigManager extends dev.pilati.twitchannounce.core.manager.ConfigManager {
    TwitchAnnounce plugin;
    private Configuration config;
    private Configuration settings;


    public ConfigManager(TwitchAnnounce plugin) {
        this.plugin = plugin;
    }

    public void saveDefaultConfig(String fileName) throws IOException{
        if(!plugin.getDataFolder().exists()){
            plugin.getDataFolder().mkdirs();
        }
    
        File configFile = new File(plugin.getDataFolder(), fileName);
    
        if(!configFile.exists()){
            FileOutputStream outputStream = new FileOutputStream(configFile);
            InputStream in = plugin.getResourceAsStream(fileName);
            in.transferTo(outputStream);
        }
    }

    public void loadConfig() throws IOException{
        this.saveDefaultConfig("config.yml");
        config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "config.yml"));
    }
    
    public void loadSettings() throws IOException{
        this.saveDefaultConfig("settings.yml");
        settings = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(plugin.getDataFolder(), "settings.yml"));
    }

    @Override
    public String getConfigString(String key) {
        return config.getString(key);
    }

    @Override
    public boolean getConfigBoolean(String key) {
        return config.getBoolean(key);
    }

    @Override
    public String getSettingsString(String key) {
        return settings.getString(key);
    }

    @Override
    public void setSettings(String string, String token) {
        settings.set(string, token);    
    }

    @Override
    public void saveSettings() {
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(settings, new File(plugin.getDataFolder(), "settings.yml"));
        }catch(IOException e){
            plugin.getLogger().log(Level.SEVERE, "Error saving settings.yml", e);
            plugin.forceDisable();
        }
    }

    public String getMessageConfig(String key){
        String message = config.getString(key);

		if(message == null){
			return null;
		}

        return ChatColor.translateAlternateColorCodes('&', message);
        
    }

    public void addStreamer(Streamer streamer) {
		String key = streamer.twitchUser.toLowerCase();
		settings.set("settings.streamers." + key + ".twitchUser", streamer.twitchUser);
		settings.set("settings.streamers." + key + ".minecraftNick", streamer.minecraftNick);
		settings.set("settings.streamers." + key + ".priority", streamer.priority);
		this.saveSettings();
	}

    public List<Streamer> getAllStreamers(){
		List<Streamer> streamers = new ArrayList<>();
		
		Configuration streamersConfig = settings.getSection("settings.streamers");
		if(streamersConfig == null) {
			return streamers;
		}
		
		for(String key : streamersConfig.getKeys()) {
			streamers.add(getStreamer(key));
		}
		
		return streamers;
	}

    public Streamer getStreamer(String twitchUser) {
        String key = twitchUser.toLowerCase();
		Streamer streamer = new Streamer();
		streamer.twitchUser = settings.getString("settings.streamers." + key + ".twitchUser");
		streamer.minecraftNick = settings.getString("settings.streamers." + key + ".minecraftNick");
		streamer.priority = settings.getInt("settings.streamers." + key + ".priority");
		
		if(streamer.minecraftNick == null || streamer.minecraftNick.length() == 0) {
			return null;
		}
		
		return streamer;
	}

    public void removeStreamer(String twitchUser) {
        String key = twitchUser.toLowerCase();
		settings.set("settings.streamers." + key, null);
		this.saveSettings();
	}

    public int getConfigInt(String key) {
        return config.getInt(key);
    }

    public long getAnnounceTimerSeconds() {
        return config.getLong("announcementInterval");
    }
    
}
