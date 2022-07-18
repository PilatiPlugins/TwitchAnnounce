package dev.pilati.twitchannounce.spigot.manager;

import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;

import dev.pilati.twitchannounce.spigot.util.Player;

public class AnnouncementManager extends dev.pilati.twitchannounce.core.manager.AnnouncementManager{

    public AnnouncementManager(){
        instance = this;
    }

    @Override
    public List<dev.pilati.twitchannounce.core.util.Player> getOnlinePlayers() {
        return Bukkit.getOnlinePlayers().stream().map(p -> new Player(p)).collect(Collectors.toList());
    }

    public static void disable() {
        instance = null;
    }
}
