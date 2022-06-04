package dev.pilati.twitchannounce.timer;

import org.bukkit.scheduler.BukkitRunnable;

import dev.pilati.twitchannounce.managers.AnnouncementManager;

public class AnnounceTimer extends BukkitRunnable {
    AnnouncementManager manager;

    public AnnounceTimer(AnnouncementManager manager) {
        super();
        this.manager = manager;
    }

    @Override
    public void run() {
        manager.announce();
    }
}