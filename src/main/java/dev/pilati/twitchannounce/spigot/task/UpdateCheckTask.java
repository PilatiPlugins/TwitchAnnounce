package dev.pilati.twitchannounce.spigot.task;

import org.bukkit.scheduler.BukkitRunnable;

import dev.pilati.twitchannounce.core.manager.UpdateManager;

public class UpdateCheckTask extends BukkitRunnable{

    @Override
    public void run() {
        UpdateManager.checkForUpdates();
    }
    
}
