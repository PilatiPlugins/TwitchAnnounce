package dev.pilati.twitchannounce.spigot.task;

import org.bukkit.scheduler.BukkitRunnable;

import dev.pilati.twitchannounce.core.manager.UpdateManager;
import dev.pilati.twitchannounce.spigot.manager.LoggingManager;

public class UpdateCheckTask extends BukkitRunnable{

    @Override
    public void run() {
        LoggingManager.debug("UpdateCheckTask.run -> checking for updates");
        UpdateManager.checkForUpdates();
    }
    
}
