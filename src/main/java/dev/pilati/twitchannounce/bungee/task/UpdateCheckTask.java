package dev.pilati.twitchannounce.bungee.task;

import dev.pilati.twitchannounce.core.manager.UpdateManager;

public class UpdateCheckTask extends Thread{
    
    @Override
    public void run() {
        UpdateManager.checkForUpdates();
    }
}
