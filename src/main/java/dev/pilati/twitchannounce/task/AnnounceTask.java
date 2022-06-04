package dev.pilati.twitchannounce.task;

import dev.pilati.twitchannounce.TwitchAnnounce;

public class AnnounceTask extends Thread{
    TwitchAnnounce plugin;

    public AnnounceTask(TwitchAnnounce plugin) {
        this.plugin = plugin;
    }

    @Override
    public void run() {
        plugin.announcementManager.announceStreamersInLive();
    }
}
