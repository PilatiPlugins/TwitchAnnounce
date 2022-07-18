package dev.pilati.twitchannounce.bungee.manager;

import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SingleLineChart;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;

public class MetricsManager extends dev.pilati.twitchannounce.core.manager.MetricsManager{
    private Metrics metrics;
    private static MetricsManager instance;

    public MetricsManager(){
        instance = this;
        metrics = new Metrics(TwitchAnnounce.getInstance(), 15545);
    }

    public static void init() {
        instance = new MetricsManager();
        instance.trackedStreamers();
        instance.onlineStreamers();
    }
    
    protected void trackedStreamers(){
        metrics.addCustomChart(new SingleLineChart("tracked_streamers", () -> {
            return ConfigurationManager.getAllStreamers().size();
        }));
    }
    
    protected void onlineStreamers(){
        metrics.addCustomChart(new SingleLineChart("live_streamers", () -> {
            return AnnouncementManager.streamersInLiveCount();
        }));
    }

    public static void disable() {
        instance = null;
    }
}
