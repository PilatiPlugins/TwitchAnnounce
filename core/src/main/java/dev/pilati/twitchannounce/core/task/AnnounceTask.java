package dev.pilati.twitchannounce.core.task;

import dev.pilati.twitchannounce.core.manager.AnnouncementManager;

public class AnnounceTask extends Thread{
    AnnouncementManager manager;

    public AnnounceTask(AnnouncementManager manager) {
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.announceStreamersInLive();
    }
}
