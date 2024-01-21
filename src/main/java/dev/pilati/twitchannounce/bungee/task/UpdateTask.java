package dev.pilati.twitchannounce.bungee.task;

import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;
import dev.pilati.twitchannounce.bungee.manager.LoggingManager;

public class UpdateTask extends Thread{
    
    @Override
    public void run() {
        LoggingManager.debug("UpdateTask.run -> updating streamer list");
        AnnouncementManager.updateStreamerList();
    }
}
