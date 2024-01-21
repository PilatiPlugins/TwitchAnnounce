package dev.pilati.twitchannounce.bungee.task;

import dev.pilati.twitchannounce.bungee.manager.LoggingManager;
import dev.pilati.twitchannounce.core.manager.UpdateManager;

public class UpdateCheckTask extends Thread{
    
    @Override
    public void run() {
        LoggingManager.debug("UpdateCheckTask.run -> checking for updates");
        UpdateManager.checkForUpdates();
    }
}
