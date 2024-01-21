package dev.pilati.twitchannounce.bungee.manager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.logging.Level;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;
import dev.pilati.twitchannounce.bungee.util.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public class ConfigurationManager extends dev.pilati.twitchannounce.core.manager.ConfigurationManager{

    private net.md_5.bungee.config.Configuration configConfiguration;
    private net.md_5.bungee.config.Configuration settingsConfiguration;

    public ConfigurationManager(){
		instance = this;
    }
    
    public void saveDefaultConfig(String fileName) throws IOException{
        LoggingManager.debug(() -> String.format("ConfigurationManager.saveDefaultConfig -> fileName: %s", fileName));
        if(!TwitchAnnounce.getInstance().getDataFolder().exists()){
            TwitchAnnounce.getInstance().getDataFolder().mkdirs();
        }
        
        File configFile = new File(TwitchAnnounce.getInstance().getDataFolder(), fileName);
        
        if(!configFile.exists()){
            LoggingManager.debug("Config file does not exist, creating it now");
            FileOutputStream outputStream = new FileOutputStream(configFile);
            InputStream inputStream = TwitchAnnounce.getInstance().getResourceAsStream(fileName);
            copyFile(inputStream, outputStream);
        }
    }

    @Override
    public Configuration loadConfigWithDefaults() throws IOException{
        this.saveDefaultConfig("config.yml");
        configConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(TwitchAnnounce.getInstance().getDataFolder(), "config.yml"));
        return new Configuration(configConfiguration);
    }
    
    @Override
    public Configuration loadSettingsWithDefaults() throws IOException{
        this.saveDefaultConfig("settings.yml");
        settingsConfiguration = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(TwitchAnnounce.getInstance().getDataFolder(), "settings.yml"));
        return new Configuration(settingsConfiguration);
    }

    @Override
    public void saveSettings() {
        try{
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(settingsConfiguration, new File(TwitchAnnounce.getInstance().getDataFolder(), "settings.yml"));
            LoggingManager.debug("ConfigurationManager.saveSettings -> settings.yml saved");
        }catch(IOException e){
            TwitchAnnounce.getInstance().getLogger().log(Level.SEVERE, "Error saving settings.yml", e);
            TwitchAnnounce.getInstance().onDisable();
        }
    }

    public static void disable() {
        instance = null;
    }

    public void copyFile(InputStream inputStream, FileOutputStream outputStream) throws IOException {
        byte[] buffer = new byte[1024];
        int length;
        while((length = inputStream.read(buffer)) > 0) {
            outputStream.write(buffer, 0, length);
        }
        inputStream.close();
        outputStream.close();
    }
}
