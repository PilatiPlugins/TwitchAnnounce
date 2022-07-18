package dev.pilati.twitchannounce.bungee.util;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class Player extends dev.pilati.twitchannounce.core.util.Player{

    private ProxiedPlayer player;

    public Player(ProxiedPlayer player){
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public void sendMessage(TextComponent message) {
        player.sendMessage(message);
    }
}
