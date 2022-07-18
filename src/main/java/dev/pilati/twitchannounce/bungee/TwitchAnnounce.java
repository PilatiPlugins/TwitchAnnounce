package dev.pilati.twitchannounce.bungee;

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

        manager = new Manager();
        manager.enable();
    }

    @Override
    public void onDisable() {
        manager.disable();
        manager = null;
        instance = null;
    }
}
