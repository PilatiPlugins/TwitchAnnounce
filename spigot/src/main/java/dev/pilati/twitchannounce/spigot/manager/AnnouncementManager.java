package dev.pilati.twitchannounce.spigot.manager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.pilati.twitchannounce.core.Streamer;
import dev.pilati.twitchannounce.core.task.AnnounceTask;
import dev.pilati.twitchannounce.spigot.TwitchAnnounce;
import dev.pilati.twitchannounce.spigot.task.AnnounceTimer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AnnouncementManager extends dev.pilati.twitchannounce.core.manager.AnnouncementManager {
    TwitchAnnounce plugin;
    private AnnounceTimer timer;
    private int lastCount = 0;

    public AnnouncementManager(TwitchAnnounce plugin) {
        this.plugin = plugin;
    }

    public void announce(){
        new AnnounceTask(this).run();
    }

    ////// CALL ASYNCHRONOUSLY
    public List<Streamer> getAllStreamersInLive(List<Streamer> streamers) throws ParseException, URISyntaxException, IOException {
        if(streamers == null || streamers.size() == 0) {
            return streamers;
        }

        List<Streamer> liveStreamers = plugin.integrationManager.getLiveStreamers(streamers);

        if(liveStreamers.isEmpty()){
            return liveStreamers;
        }

        return verifyPriority(liveStreamers);
    }

    public List<Streamer> verifyPriority(List<Streamer> streamers){
        String priorityBehavior = plugin.configManager.getConfigString("priorityBehavior");

        if("highest".equals(priorityBehavior)){
            int highest = streamers.stream().max(Comparator.comparing(s -> s.priority)).get().priority;
            return streamers.stream().filter(s -> s.priority == highest).collect(Collectors.toList());
        }

        if("order".equals(priorityBehavior)){
            return streamers.stream().sorted((s1, s2) -> Integer.compare(s2.priority, s1.priority)).collect(Collectors.toList());
        }

        if("random".equals(priorityBehavior)){
            return streamers.stream().sorted((s1, s2) -> (int)(Math.random() * 2) - 1).collect(Collectors.toList());
        }

        return streamers;
    }

    @Override
    public void announceStreamersInLive() {
        try {
            List<Streamer> streamers = getAllStreamersInLive(getOnlineStreamers());
            lastCount = streamers.size();

            if(streamers.size() > 0){
                announceToAll(streamers);
            }
        } catch (ParseException|URISyntaxException|IOException e) {
            plugin.getLogger().log(Level.SEVERE, "Error while getting live streamers", e);
        }
    }

    public List<Streamer> getOnlineStreamers(){
        List<Streamer> streamers = plugin.configManager.getAllStreamers();
        
        if(plugin.getConfig().getBoolean("online")){
            streamers = streamers.stream().filter(
                            streamer -> Bukkit.getOnlinePlayers().stream().anyMatch(
                                player -> player.getName().equalsIgnoreCase(streamer.minecraftNick))
                            ).collect(Collectors.toList());
        }

        return streamers;
    }

    
    
    private void announceToAll(List<Streamer> streamers) {
        TextComponent message = makeMessage(streamers);
        for(Player player : Bukkit.getOnlinePlayers()){
            player.spigot().sendMessage(message);
        }
    }

    public TextComponent makeMessage(List<Streamer> streamers){
        String streamerConfigMessage = plugin.configManager.getMessageConfig("messages.announcement.streamer");
        TextComponent streamerMessage;
        boolean linkToChannel = plugin.getConfig().getBoolean("linkToChannel");
        String separator = plugin.getConfig().getString("messages.announcement.separator");
        int limit = plugin.getConfig().getInt("limit");
        int line = 0;
        
        TextComponent message = new TextComponent();
        
        String header = plugin.configManager.getMessageConfig("messages.announcement.header");
        if(String.valueOf(header).length() > 0){
            message.addExtra(header);
            message.addExtra("\n");
        }

        for(Streamer streamer : streamers){
            streamerMessage = new TextComponent(streamerConfigMessage.replace("{minecraftNick}", streamer.minecraftNick).replace("{twitchUser}", streamer.twitchUser));
            
            if(linkToChannel){
                streamerMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.twitch.tv/" + streamer.twitchUser));
            }

            if(line > 0){
                message.addExtra(separator);
            }

            message.addExtra(streamerMessage);

            line++;
            if(limit > 0 && line >= limit){
                break;
            }
        }

        String footer = plugin.configManager.getMessageConfig("messages.announcement.footer");
        if(String.valueOf(footer).length() > 0){
            message.addExtra(footer);
        }

        return message;
    }

    public void initTimer() {
        long ticks = plugin.configManager.getAnnounceTimerSeconds() * 20L;
        this.timer = new AnnounceTimer(this);
        timer.runTaskTimer(plugin, ticks, ticks);
    }

    public void cancelTimer() {
        if(timer != null && !timer.isCancelled()){
            timer.cancel();
            timer = null;
        }
    }

    public int getLastCount() {
        return lastCount;
    }
}
