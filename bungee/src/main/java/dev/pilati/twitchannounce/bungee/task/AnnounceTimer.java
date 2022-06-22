package dev.pilati.twitchannounce.bungee.task;

import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;

public class AnnounceTimer extends Thread {
    AnnouncementManager manager;

    public AnnounceTimer(AnnouncementManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.announceStreamersInLive();
    }
}
