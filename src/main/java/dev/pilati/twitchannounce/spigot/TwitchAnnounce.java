package dev.pilati.twitchannounce.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import dev.pilati.twitchannounce.spigot.manager.LoggingManager;
import dev.pilati.twitchannounce.spigot.manager.Manager;

public class TwitchAnnounce extends JavaPlugin{
    private static TwitchAnnounce plugin;
    private Manager manager;

    public static TwitchAnnounce getInstance(){
        return plugin;
    }

    @Override
    public void onEnable() {
        plugin = this;

        LoggingManager.debug("TwitchAnnounce.onEnable -> starting all managers");
        manager = new Manager();
        manager.enable();
        LoggingManager.debug("TwitchAnnounce.onEnable -> all managers started");
    }
    
    @Override
    public void onDisable() {
        LoggingManager.debug("TwitchAnnounce.onDisable -> stopping all managers");
        manager.disable();
        manager = null;
    }
}
