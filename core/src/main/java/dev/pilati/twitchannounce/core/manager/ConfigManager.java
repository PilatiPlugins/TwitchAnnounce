package dev.pilati.twitchannounce.core.manager;

public abstract class ConfigManager {
    
    public abstract String getConfigString(String key);
    
    public abstract boolean getConfigBoolean(String string);
    
    public abstract String getSettingsString(String string);
    
    public abstract void setSettings(String string, String token);
    
    public abstract void saveSettings();
}
