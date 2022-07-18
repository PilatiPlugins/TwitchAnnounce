package dev.pilati.twitchannounce.bungee.manager;

import java.util.List;
import java.util.stream.Collectors;
import dev.pilati.twitchannounce.bungee.util.Player;
import net.md_5.bungee.api.ProxyServer;

public class AnnouncementManager extends dev.pilati.twitchannounce.core.manager.AnnouncementManager{

    public AnnouncementManager(){
        instance = this;
    }

    @Override
    public List<dev.pilati.twitchannounce.core.util.Player> getOnlinePlayers() {
        return ProxyServer.getInstance().getPlayers().stream().map(p -> new Player(p)).collect(Collectors.toList());
    }

    public static void disable() {
        instance = null;
    }
}
