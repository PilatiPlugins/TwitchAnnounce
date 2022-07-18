package dev.pilati.twitchannounce.core.manager;

import java.util.logging.Level;

public abstract class LoggingManager {
    public static LoggingManager instance;
    
    public abstract void log(Level level, String message, Exception e);
    public abstract void log(Level severe, String string);

    public static LoggingManager getLogger(){
        return instance;
    }

}
