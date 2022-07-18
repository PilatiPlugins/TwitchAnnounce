package dev.pilati.twitchannounce.spigot.util;

import java.util.HashSet;
import java.util.Set;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;

public class Configuration extends dev.pilati.twitchannounce.core.util.Configuration{
    private ConfigurationSection configuration;

    public Configuration(ConfigurationSection configuration){
        this.configuration = configuration;
    }

    @Override
    public String getString(String path) {
        if(configuration == null){
            return null;
        }

        return configuration.getString(path);
    }

    @Override
    public int getInt(String path) {
        if(configuration == null){
            return 0;
        }
        
        return configuration.getInt(path);
    }

    @Override
    public long getLong(String path) {
        if(configuration == null){
            return 0;
        }
        
        return configuration.getLong(path);
    }

    @Override
    public boolean getBoolean(String path) {
        if(configuration == null){
            return false;
        }
        
        return configuration.getBoolean(path);
    }

    @Override
    public void set(String path, Object obj) {
        if(configuration == null){
            return;
        }
        
        configuration.set(path, obj);
    }

    @Override
    public dev.pilati.twitchannounce.core.util.Configuration getConfigurationSection(String path) {
        if(configuration == null){
            return null;
        }
        
        return new Configuration(configuration.getConfigurationSection(path));
    }

    @Override
    public Set<String> getKeys(boolean deep) {
        if(configuration == null){
            return new HashSet<>();
        }
        
        return configuration.getKeys(deep);
    }

    @Override
    protected String translateAlternateColorCodes(char altColorChar, String message) {
        return ChatColor.translateAlternateColorCodes(altColorChar, message);
    }
    
}
