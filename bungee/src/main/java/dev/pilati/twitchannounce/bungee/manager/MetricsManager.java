package dev.pilati.twitchannounce.bungee.manager;

import org.bstats.bungeecord.Metrics;
import org.bstats.charts.SingleLineChart;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;

public class MetricsManager {
    private TwitchAnnounce plugin;
    private Metrics metrics;

    public MetricsManager(TwitchAnnounce plugin) {
        this.plugin = plugin;
        metrics = new Metrics(plugin, 15547);
    }

    public void init() {
        trackedStreamers();
        onlineStreamers();
    }
    
    private void trackedStreamers(){
        metrics.addCustomChart(new SingleLineChart("tracked_streamers", () -> {
            return plugin.configManager.getAllStreamers().size();
        }));
    }
    
    public void onlineStreamers(){
        metrics.addCustomChart(new SingleLineChart("live_streamers", () -> {
            return plugin.announcementManager.getLastCount();
        }));
    }
}
