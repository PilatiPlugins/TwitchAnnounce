package dev.pilati.twitchannounce.spigot.event;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.pilati.twitchannounce.core.manager.UpdateManager;
import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;
import dev.pilati.twitchannounce.spigot.util.Player;

public class PlayerEvents implements Listener{
    
    @EventHandler
    public void playerJoin(PlayerJoinEvent event){
        Player player = new Player(event.getPlayer());
        AnnouncementManager.announceToPlayer(player);
        UpdateManager.verifyAnnounceToPlayer(player);
    }
}
