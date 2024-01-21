package dev.pilati.twitchannounce.bungee.event;

import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;
import dev.pilati.twitchannounce.bungee.util.Player;
import dev.pilati.twitchannounce.core.manager.UpdateManager;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class PlayerEvents implements Listener{

    @EventHandler
    public void playerJoin(PostLoginEvent event){
        Player player = new Player(event.getPlayer());
        AnnouncementManager.announceToPlayer(player);
        UpdateManager.verifyAnnounceToPlayer(player);
    }
}
