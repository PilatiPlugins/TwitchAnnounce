package dev.pilati.twitchannounce.core.manager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.hc.core5.http.ParseException;

import dev.pilati.twitchannounce.core.util.Player;
import dev.pilati.twitchannounce.core.util.Streamer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class AnnouncementManager {
    List<Streamer> streamersInLive = new ArrayList<>();
    protected static AnnouncementManager instance;

    public static AnnouncementManager getInstance(){
        return instance;
    }

    protected abstract List<Player> getOnlinePlayers();

    protected static List<Streamer> getOnlineStreamers(){
        List<Streamer> onlineStreamers = ConfigurationManager.getAllStreamers();
        
        if (LoggingManager.debug) {
            LoggingManager.debug("AnnouncementManager.getOnlineStreamers -> count: " + onlineStreamers.size() + 
                ", list: " + onlineStreamers.stream().map(s -> s.twitchUser).collect(Collectors.joining(", ")));
        }

        if(ConfigurationManager.getConfig().getBoolean("announcement.online")){
            LoggingManager.debug("AnnouncementManager.getOnlineStreamers -> filtering online streamers");
            List<Player> onlinePlayers = getInstance().getOnlinePlayers();
            
            onlineStreamers = onlineStreamers.stream().filter(
                streamer -> onlinePlayers.stream().anyMatch(
                    player -> player.getName().equalsIgnoreCase(streamer.minecraftNick)
                )
            ).collect(Collectors.toList());
        }

        if (LoggingManager.debug) {
            LoggingManager.debug("AnnouncementManager.getOnlineStreamers -> count online: " + onlineStreamers.size() + 
                ", list: " + onlineStreamers.stream().map(s -> s.twitchUser).collect(Collectors.joining(", ")));
        }

        return onlineStreamers;
    }

    protected static List<Streamer> sortList(List<Streamer> streamers){
        if(LoggingManager.debug){
            LoggingManager.debug("AnnouncementManager.sortList -> sorting streamers");
            LoggingManager.debug("AnnouncementManager.sortList -> order: " + String.join(", ", ConfigurationManager.getOrderBehavior()));
        }
        return streamers.stream().sorted((s1, s2) -> compare(s1, s2)).collect(Collectors.toList());
    }

    protected static int compare(Streamer s1, Streamer s2){
        int comparation = 0;
        for (String order : ConfigurationManager.getOrderBehavior()) {
            switch(order){
                case "priority":
                    comparation = Integer.compare(s2.priority, s1.priority);
                    break;

                case "reverse_priority":
                    comparation = Integer.compare(s1.priority, s2.priority);
                    break;
                    
                case "random":
                    comparation = (int)(Math.random() * 2) - 1;
                    break;
                    
                case "twitch_alphabetic":
                    comparation = s1.twitchUser.compareTo(s2.twitchUser);
                    break;
                    
                case "mine_alphabetic":
                    comparation = s1.minecraftNick.compareTo(s2.minecraftNick);
                    break;
    
                default:
                    comparation = 0;
            }

            if(comparation != 0){
                return comparation;
            }
        }

        return comparation;
    }

    protected static List<Streamer> filterList(List<Streamer> streamers){
        if( streamers.size() > 0 && ConfigurationManager.getConfig().getBoolean("announcement.highestPriorityOnly")){
            LoggingManager.debug("AnnouncementManager.filterList -> filtering streamers by highest priority");
            int highest = streamers.stream().max(Comparator.comparing(s -> s.priority)).get().priority;
            return streamers.stream().filter(s -> s.priority == highest).collect(Collectors.toList());
        }
        
        return streamers;
    }

    protected static TextComponent makeMessage(List<Streamer> streamers){
        LoggingManager.debug("AnnouncementManager.makeMessage -> making message");
        String streamerConfigMessage = ConfigurationManager.getConfig().getMessage("messages.announcement.streamer");
        TextComponent streamerMessage;
        boolean linkToChannel = ConfigurationManager.getConfig().getBoolean("announcement.linkToChannel");
        String separator = ConfigurationManager.getConfig().getString("messages.announcement.separator");
        int limit = ConfigurationManager.getConfig().getInt("announcement.limit");
        int line = 0;
        
        TextComponent message = new TextComponent();
        
        String header = ConfigurationManager.getConfig().getMessage("messages.announcement.header");
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

        String footer = ConfigurationManager.getConfig().getMessage("messages.announcement.footer");
        if(String.valueOf(footer).length() > 0){
            message.addExtra(footer);
        }

        LoggingManager.debug(() -> String.format("AnnouncementManager.makeMessage -> message: %s", message.toPlainText()));
        return message;
    }

    protected static List<Streamer> getStreamersInLive(){
        if (LoggingManager.debug) {
            LoggingManager.debug("AnnouncementManager.getStreamersInLive -> count: " + getInstance().streamersInLive.size() + 
                ", list: " + getInstance().streamersInLive.stream().map(s -> s.twitchUser).collect(Collectors.joining(", ")));
        }

        List<Streamer> streamers = filterList(getInstance().streamersInLive);

        if (LoggingManager.debug) {
            LoggingManager.debug("AnnouncementManager.getStreamersInLive filtered -> count: " + streamers.size() + 
                ", list: " + streamers.stream().map(s -> s.twitchUser).collect(Collectors.joining(", ")));
        }

        streamers = sortList(streamers);

        if (LoggingManager.debug) {
            LoggingManager.debug("AnnouncementManager.getStreamersInLive sorted -> count: " + streamers.size() + 
                ", list: " + streamers.stream().map(s -> s.twitchUser).collect(Collectors.joining(", ")));
        }

        return streamers;
    }

    protected static void announceToAll(List<Streamer> streamers){
        LoggingManager.debug(() -> String.format("AnnouncementManager.announceToAll -> announcing to all players, size: %d", streamers.size()));
        if(streamers.size() == 0){
            return;
        }

        boolean hideToStreamers = !ConfigurationManager.getConfig().getBoolean("announcement.announceToStreamersInLive");
        LoggingManager.debug(() -> String.format("AnnouncementManager.announceToAll -> hideToStreamers: %s", String.valueOf(hideToStreamers)));

        TextComponent message = makeMessage(streamers);
        for (Player player : getInstance().getOnlinePlayers()) {

            if(hideToStreamers){
                boolean liveStreamer = getInstance().streamersInLive.stream().anyMatch(s -> s.minecraftNick.equals(player.getName()));
                if(liveStreamer){
                    LoggingManager.debug(() -> String.format("AnnouncementManager.announceToAll -> player %s is in live, skipping", player.getName()));
                    return;
                }
            }

            LoggingManager.debug(() -> String.format("AnnouncementManager.announceToAll -> announcing to player ", player.getName()));
            player.sendMessage(message);
        }
    }

    public static void announceLiveToAll(){
        LoggingManager.debug("AnnouncementManager.announceLiveToAll -> announcing live to all players");

        new Thread(){
            @Override
            public void run() {
                LoggingManager.debug("AnnouncementManager.announceLiveToAll -> updating streamers list");
                updateStreamerList(false);
                LoggingManager.debug("AnnouncementManager.announceLiveToAll -> announcing to all players");
                announceToAll(getStreamersInLive());
            }
        }.start();
    }

    public static void announceToPlayer(Player player){
        LoggingManager.debug(() -> String.format("AnnouncementManager.announceToPlayer -> announcing to player %s", player.getName()));
        if(getStreamersInLive().size() == 0){
            return;
        }

        player.sendMessage(makeMessage(getStreamersInLive()));
    }

    protected static List<Streamer> getAllStreamersInLive(List<Streamer> streamers) throws ParseException, URISyntaxException, IOException{
        if(streamers == null || streamers.size() == 0) {
            LoggingManager.debug("AnnouncementManager.getAllStreamersInLive -> no streamers to check");
            return streamers;
        }

        LoggingManager.debug("AnnouncementManager.getAllStreamersInLive -> checking streamers");
        return IntegrationManager.getLiveStreamers(streamers);
    }

    public static void updateStreamerList(){
        LoggingManager.debug("AnnouncementManager.updateStreamerList -> updating streamers list");
        new Thread(){
            @Override
            public void run() {
                LoggingManager.debug("AnnouncementManager.updateStreamerList -> updating streamers list");
                updateStreamerList(true);
            }
        }.start();
    }

    protected static void updateStreamerList(boolean verifyModified){
        try {
            LoggingManager.debug("AnnouncementManager.updateStreamerList -> getting streamers in live");
            List<Streamer> streamers = getAllStreamersInLive(getOnlineStreamers());

            if(verifyModified){
                LoggingManager.debug("AnnouncementManager.updateStreamerList -> verifying modified list");
                verifyModifiedList(streamers);
            }

            getInstance().streamersInLive = streamers;
        } catch (ParseException | URISyntaxException | IOException e) {
            LoggingManager.getLogger().log(Level.SEVERE, "Error while getting live streamers", e);
        }
    }

    protected static void verifyModifiedList(List<Streamer> streamers) {
        String behavior = ConfigurationManager.getConfig().getString("announcement.modifiedBehavior");
        LoggingManager.debug(() -> String.format("AnnouncementManager.verifyModifiedList -> behavior: %s", behavior));

        List<Streamer> newStreamers = streamers.stream().filter(
            s -> getInstance().streamersInLive.stream().noneMatch(
                o -> s.twitchUser.equals(o.twitchUser)
            )
        ).collect(Collectors.toList());

        if(LoggingManager.debug){
            LoggingManager.debug("AnnouncementManager.verifyModifiedList -> new streamers: " + newStreamers.stream().map(s -> s.twitchUser).collect(Collectors.joining(", ")));
        }

        if(newStreamers.size() == 0){
            return;
        }

        if("new".equals(behavior)){
            LoggingManager.debug("AnnouncementManager.verifyModifiedList -> announcing new streamers");
            announceToAll(newStreamers);
            TimerManager.resetAnnouncementTimer();
        }else{
            LoggingManager.debug("AnnouncementManager.verifyModifiedList -> announcing all streamers");
            announceToAll(streamers);
            TimerManager.resetAnnouncementTimer();
        }
    }

    public static int streamersInLiveCount(){
        return getInstance().streamersInLive.size();
    }
}
