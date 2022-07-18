package dev.pilati.twitchannounce.spigot.util;

import net.md_5.bungee.api.chat.TextComponent;

public class Player extends dev.pilati.twitchannounce.core.util.Player{

    private org.bukkit.entity.Player player;

    public Player(org.bukkit.entity.Player player){
        this.player = player;
    }

    @Override
    public String getName() {
        return player.getName();
    }

    @Override
    public void sendMessage(TextComponent message) {
        player.spigot().sendMessage(message);
    }
    
}
