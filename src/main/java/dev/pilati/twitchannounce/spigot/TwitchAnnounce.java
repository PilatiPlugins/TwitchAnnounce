package dev.pilati.twitchannounce.spigot;

import org.bukkit.plugin.java.JavaPlugin;

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

        manager = new Manager();
        manager.enable();
    }
    
    @Override
    public void onDisable() {
        manager.disable();
        manager = null;
    }
}
