package dev.pilati.twitchannounce.core.util;

import java.util.Collection;

public abstract class Configuration {

    public abstract String getString(String path);
    public abstract int getInt(String path);
    public abstract long getLong(String path);
    public abstract boolean getBoolean(String path);
    public abstract void set(String path, Object obj);

    public abstract Configuration getConfigurationSection(String path);
    public abstract Collection<String> getKeys(boolean deep);

    public String getMessage(String path){
        String message = getString(path);

		if(message == null){
			return null;
		}

        return translateAlternateColorCodes('&', message);
    }

    protected abstract String translateAlternateColorCodes(char altColorChar, String message);
}
