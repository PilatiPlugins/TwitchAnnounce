package dev.pilati.twitchannounce.spigot.task;

import org.bukkit.scheduler.BukkitRunnable;

import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;
import dev.pilati.twitchannounce.spigot.manager.LoggingManager;

public class UpdateTask extends BukkitRunnable{
    
    @Override
    public void run() {
        LoggingManager.debug("UpdateTask.run -> updating streamer list");
        AnnouncementManager.updateStreamerList();
    }
}
