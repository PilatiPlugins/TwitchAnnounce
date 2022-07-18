package dev.pilati.twitchannounce.spigot.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;
import dev.pilati.twitchannounce.spigot.util.Player;

public class PlayerEvents implements Listener{
    
    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        AnnouncementManager.announceToPlayer(new Player(event.getPlayer()));
    }
}
