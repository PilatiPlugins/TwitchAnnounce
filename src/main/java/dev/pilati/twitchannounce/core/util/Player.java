package dev.pilati.twitchannounce.core.util;

import net.md_5.bungee.api.chat.TextComponent;

public abstract class Player {
    public abstract String getName();
    public abstract void sendMessage(TextComponent message);
    public abstract boolean hasPermission(String permission);
}
