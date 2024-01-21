package dev.pilati.twitchannounce.core.manager;

import java.util.function.Supplier;
import java.util.logging.Level;

public abstract class LoggingManager {
    public static LoggingManager instance;
    public static boolean debug = false;
    
    public abstract void log(Level level, String message, Exception e);
    public abstract void log(Level severe, String string);

    public static LoggingManager getLogger(){
        return instance;
    }

    public static void debug(String message){
        if(debug){
            instance.log(Level.INFO, message);
        }
    }

    public static void debug(String message, Exception e){
        if(debug){
            instance.log(Level.INFO, message, e);
        }
    }
    
    public static void debug(Supplier<String> predicate){
        if(debug){
            instance.log(Level.INFO, predicate.get());
        }
    }
}
