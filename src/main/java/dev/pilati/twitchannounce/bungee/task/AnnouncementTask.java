package dev.pilati.twitchannounce.bungee.task;

import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;
import dev.pilati.twitchannounce.bungee.manager.LoggingManager;

public class AnnouncementTask extends Thread{
    
    @Override
    public void run() {
        LoggingManager.debug("AnnouncementTask.run -> announcing to all");
        AnnouncementManager.announceLiveToAll();
    }
}
