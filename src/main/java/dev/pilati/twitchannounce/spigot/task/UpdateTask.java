package dev.pilati.twitchannounce.spigot.task;

import org.bukkit.scheduler.BukkitRunnable;

import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;

public class UpdateTask extends BukkitRunnable{
    
    @Override
    public void run() {
        AnnouncementManager.updateStreamerList();
    }
}
