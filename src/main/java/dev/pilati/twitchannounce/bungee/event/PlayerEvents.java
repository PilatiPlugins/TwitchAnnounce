package dev.pilati.twitchannounce.bungee.event;

import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;
import dev.pilati.twitchannounce.bungee.util.Player;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerEvents implements Listener{

    @EventHandler
    public void playerJoin(PostLoginEvent event){
        AnnouncementManager.announceToPlayer(new Player(event.getPlayer()));
    }
    
}
