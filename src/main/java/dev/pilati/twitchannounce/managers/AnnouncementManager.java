package dev.pilati.twitchannounce.managers;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import dev.pilati.twitchannounce.Streamer;
import dev.pilati.twitchannounce.TwitchAnnounce;
import dev.pilati.twitchannounce.timer.AnnounceTimer;
import dev.pilati.twitchannounce.task.AnnounceTask;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class AnnouncementManager {
    TwitchAnnounce plugin;
    private AnnounceTimer timer;
    private int errorsInSequence = 0;

    public AnnouncementManager(TwitchAnnounce plugin) {
        this.plugin = plugin;
    }
    
    public void initTimer(){
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

    public void announce(){
        new AnnounceTask(plugin).run();
    }

    ////// CALL ASYNCHRONOUSLY
    public void announceStreamersInLive() {
        List<Streamer> liveStreamers = getOnlineStreamers();

        if(liveStreamers == null || liveStreamers.size() == 0) {
            return;
        }

        try{
            liveStreamers = plugin.integrationManager.getLiveStreamers(liveStreamers);
        }catch(URISyntaxException | ParseException | IOException ex){
            plugin.getLogger().log(Level.SEVERE, "Error while getting live streamers", ex);
            errorsInSequence++;

            if(errorsInSequence > 10){
                plugin.getLogger().log(Level.SEVERE, "Too many errors in a row, disabling plugin");
                Bukkit.getPluginManager().disablePlugin(plugin);
            }

            return;
        }

        errorsInSequence = 0;
        if(liveStreamers.isEmpty()){
            return;
        }

        liveStreamers = verifyPriority(liveStreamers);

        TextComponent message = makeMessage(liveStreamers);
        for(Player player : Bukkit.getOnlinePlayers()){
            player.spigot().sendMessage(message);
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

    public List<Streamer> verifyPriority(List<Streamer> streamers){
        String priorityBehavior = plugin.getConfig().getString("priorityBehavior");

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
}
