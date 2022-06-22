package dev.pilati.twitchannounce.bungee.manager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;
import dev.pilati.twitchannounce.bungee.task.AnnounceTimer;
import dev.pilati.twitchannounce.core.Streamer;
import dev.pilati.twitchannounce.core.task.AnnounceTask;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class AnnouncementManager extends dev.pilati.twitchannounce.core.manager.AnnouncementManager {

    private TwitchAnnounce plugin;
    private ScheduledTask timer;
    private int lastCount = 0;

    public AnnouncementManager(TwitchAnnounce plugin) {
        this.plugin = plugin;
    }
    
    public void announce() {
        new AnnounceTask(this).run();
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
        
        if(plugin.configManager.getConfigBoolean("online")){
            streamers = streamers.stream().filter(
                streamer -> plugin.getProxy().getPlayers().stream().anyMatch(
                    player -> player.getName().equalsIgnoreCase(streamer.minecraftNick)
                )
            ).collect(Collectors.toList());
        }

        return streamers;
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

    private void announceToAll(List<Streamer> streamers) {
        TextComponent message = makeMessage(streamers);
        for(ProxiedPlayer player : plugin.getProxy().getPlayers()){
            player.sendMessage(message);
        }
    }

    public TextComponent makeMessage(List<Streamer> streamers){
        String streamerConfigMessage = plugin.configManager.getMessageConfig("messages.announcement.streamer");
        TextComponent streamerMessage;
        boolean linkToChannel = plugin.configManager.getConfigBoolean("linkToChannel");
        String separator = plugin.configManager.getConfigString("messages.announcement.separator");
        int limit = plugin.configManager.getConfigInt("limit");
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
        long secs = plugin.configManager.getAnnounceTimerSeconds();
        this.timer = plugin.getProxy().getScheduler().schedule(plugin, new AnnounceTimer(this), secs, secs, TimeUnit.SECONDS);
    }

    public void cancelTimer() {
        if(timer != null){
            plugin.getProxy().getScheduler().cancel(this.timer);
            timer = null;
        }
    }

    public int getLastCount() {
        return lastCount;
    }
}
