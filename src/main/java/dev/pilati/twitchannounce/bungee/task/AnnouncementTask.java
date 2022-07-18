package dev.pilati.twitchannounce.bungee.task;

import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;

public class AnnouncementTask extends Thread{
    
    @Override
    public void run() {
        AnnouncementManager.announceLiveToAll();
    }
}
