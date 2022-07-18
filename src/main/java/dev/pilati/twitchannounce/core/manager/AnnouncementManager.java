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

        if(ConfigurationManager.getConfig().getBoolean("announcement.online")){
            List<Player> onlinePlayers = getInstance().getOnlinePlayers();
            
            onlineStreamers = onlineStreamers.stream().filter(
                streamer -> onlinePlayers.stream().anyMatch(
                    player -> player.getName().equalsIgnoreCase(streamer.minecraftNick)
                )
            ).collect(Collectors.toList());
        }

        return onlineStreamers;
    }

    protected static List<Streamer> sortList(List<Streamer> streamers){
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
            int highest = streamers.stream().max(Comparator.comparing(s -> s.priority)).get().priority;
            return streamers.stream().filter(s -> s.priority == highest).collect(Collectors.toList());
        }
        
        return streamers;
    }

    protected static TextComponent makeMessage(List<Streamer> streamers){
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

        return message;
    }

    protected static List<Streamer> getStreamersInLive(){
        List<Streamer> streamers = filterList(getInstance().streamersInLive);
        streamers = sortList(streamers);
        return streamers;
    }

    protected static void announceToAll(List<Streamer> streamers){
        if(streamers.size() == 0){
            return;
        }

        boolean hideToStreamers = !ConfigurationManager.getConfig().getBoolean("announcement.announceToStreamersInLive");

        TextComponent message = makeMessage(streamers);
        for (Player player : getInstance().getOnlinePlayers()) {

            if(hideToStreamers){
                boolean liveStreamer = getInstance().streamersInLive.stream().anyMatch(s -> s.minecraftNick.equals(player.getName()));
                if(liveStreamer){
                    return;
                }
            }

            player.sendMessage(message);
        }
    }

    public static void announceLiveToAll(){

        new Thread(){
            @Override
            public void run() {
                updateStreamerList(false);
                announceToAll(getStreamersInLive());
            }
        }.start();
    }

    public static void announceToPlayer(Player player){
        if(getStreamersInLive().size() == 0){
            return;
        }

        player.sendMessage(makeMessage(getStreamersInLive()));
    }

    protected static List<Streamer> getAllStreamersInLive(List<Streamer> streamers) throws ParseException, URISyntaxException, IOException{
        if(streamers == null || streamers.size() == 0) {
            return streamers;
        }

        return IntegrationManager.getLiveStreamers(streamers);
    }

    public static void updateStreamerList(){
        new Thread(){
            @Override
            public void run() {
                updateStreamerList(true);
            }
        }.start();
    }

    protected static void updateStreamerList(boolean verifyModified){
        try {
            List<Streamer> streamers = getAllStreamersInLive(getOnlineStreamers());

            if(!verifyModified){
                return;
            }

            verifyModifiedList(streamers);

            getInstance().streamersInLive = streamers;
        } catch (ParseException | URISyntaxException | IOException e) {
            LoggingManager.getLogger().log(Level.SEVERE, "Error while getting live streamers", e);
        }
    }

    protected static void verifyModifiedList(List<Streamer> streamers) {
        String behavior = ConfigurationManager.getConfig().getString("announcement.modifiedBehavior");

        List<Streamer> newStreamers = streamers.stream().filter(
            s -> getInstance().streamersInLive.stream().noneMatch(
                o -> s.twitchUser.equals(o.twitchUser)
            )
        ).collect(Collectors.toList());

        if(newStreamers.size() == 0){
            return;
        }

        if("new".equals(behavior)){
            announceToAll(newStreamers);
            TimerManager.resetAnnouncementTimer();
        }else{
            announceToAll(streamers);
            TimerManager.resetAnnouncementTimer();
        }
    }

    public static int streamersInLiveCount(){
        return getInstance().streamersInLive.size();
    }
}
