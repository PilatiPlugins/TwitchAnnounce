package dev.pilati.twitchannounce.bungee.manager;

import java.util.logging.Level;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;

public class LoggingManager extends dev.pilati.twitchannounce.core.manager.LoggingManager{

    private TwitchAnnounce plugin;

    public LoggingManager(TwitchAnnounce plugin) {
        this.plugin = plugin;
    }

    @Override
    public void log(Level level, String message, Exception e) {
        plugin.getLogger().log(level, message, e);
    }
    
}
