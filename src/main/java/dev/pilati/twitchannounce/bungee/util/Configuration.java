package dev.pilati.twitchannounce.bungee.util;

import java.util.Collection;

import net.md_5.bungee.api.ChatColor;

public class Configuration extends dev.pilati.twitchannounce.core.util.Configuration{

    private net.md_5.bungee.config.Configuration yamlConfiguration;

    public Configuration(net.md_5.bungee.config.Configuration yamlConfiguration) {
        this.yamlConfiguration = yamlConfiguration;
    }

    @Override
    public String getString(String path) {
        return yamlConfiguration.getString(path);
    }

    @Override
    public int getInt(String path) {
        return yamlConfiguration.getInt(path);
    }

    @Override
    public long getLong(String path) {
        return yamlConfiguration.getLong(path);
    }

    @Override
    public boolean getBoolean(String path) {
        return yamlConfiguration.getBoolean(path);
    }

    @Override
    public void set(String path, Object obj) {
        yamlConfiguration.set(path, obj);
    }

    @Override
    public dev.pilati.twitchannounce.core.util.Configuration getConfigurationSection(String path) {
        return new Configuration(yamlConfiguration.getSection(path));
    }

    @Override
    public Collection<String> getKeys(boolean deep) {
        return yamlConfiguration.getKeys();
    }

    @Override
    protected String translateAlternateColorCodes(char altColorChar, String message) {
        return ChatColor.translateAlternateColorCodes(altColorChar, message);
    }
    
}
