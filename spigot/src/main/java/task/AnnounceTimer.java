package task;

import org.bukkit.scheduler.BukkitRunnable;

import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;

public class AnnounceTimer extends BukkitRunnable{
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
