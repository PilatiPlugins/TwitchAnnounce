package dev.pilati.twitchannounce.core.manager;

import java.util.logging.Level;

public abstract class LoggingManager {

    public abstract void log(Level level, String message, Exception e);
    
}
