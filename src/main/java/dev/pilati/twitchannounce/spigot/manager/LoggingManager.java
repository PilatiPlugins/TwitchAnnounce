package dev.pilati.twitchannounce.spigot.manager;

import java.util.logging.Level;

import dev.pilati.twitchannounce.spigot.TwitchAnnounce;

public class LoggingManager extends dev.pilati.twitchannounce.core.manager.LoggingManager{

    public LoggingManager(){
        instance = this;
    }

    @Override
    public void log(Level level, String message, Exception e) {
        TwitchAnnounce.getInstance().getLogger().log(level, message, e);
    }

    @Override
    public void log(Level level, String message) {
        TwitchAnnounce.getInstance().getLogger().log(level, message);
    }

    public static void disable() {
        instance = null;
    }
    
}
