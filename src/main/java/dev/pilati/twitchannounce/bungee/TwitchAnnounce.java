package dev.pilati.twitchannounce.bungee;

import dev.pilati.twitchannounce.bungee.manager.LoggingManager;
import dev.pilati.twitchannounce.bungee.manager.Manager;
import net.md_5.bungee.api.plugin.Plugin;

public class TwitchAnnounce extends Plugin {
    
    private static TwitchAnnounce instance;
    private Manager manager;

    public static TwitchAnnounce getInstance() {
        return instance;
    }


    @Override
    public void onEnable() {
        instance = this;

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
        instance = null;
    }
}
