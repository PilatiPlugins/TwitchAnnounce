package dev.pilati.twitchannounce.spigot.task;

import org.bukkit.scheduler.BukkitRunnable;

import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;
import dev.pilati.twitchannounce.spigot.manager.LoggingManager;

public class AnnouncementTask extends BukkitRunnable{
    
    @Override
    public void run() {
        LoggingManager.debug("AnnouncementTask.run -> announcing to all");
        AnnouncementManager.announceLiveToAll();
    }
}
