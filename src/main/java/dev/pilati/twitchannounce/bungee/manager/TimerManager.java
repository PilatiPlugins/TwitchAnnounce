package dev.pilati.twitchannounce.bungee.manager;

import java.util.concurrent.TimeUnit;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;
import dev.pilati.twitchannounce.bungee.task.AnnouncementTask;
import dev.pilati.twitchannounce.bungee.task.UpdateTask;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class TimerManager extends dev.pilati.twitchannounce.core.manager.TimerManager {

    private ScheduledTask updateTimer;
    private ScheduledTask announcementTimer;

    public TimerManager(){
        instance = this;
    }

    @Override
    public void initUpdateTimer() {
        long secs = ConfigurationManager.getConfig().getLong("announcement.updateInterval");
        if(secs > 0){
            this.updateTimer = TwitchAnnounce.getInstance().getProxy().getScheduler().schedule(
                TwitchAnnounce.getInstance(), new UpdateTask(), secs, TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void initAnnouncementTimer() {
        long secs = ConfigurationManager.getConfig().getLong("announcement.unmodifiedInterval");
        if(secs > 0){
            this.announcementTimer = TwitchAnnounce.getInstance().getProxy().getScheduler().schedule(
                TwitchAnnounce.getInstance(), new AnnouncementTask(), secs, TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void cancelUpdateTimer() {
        if(updateTimer != null){
            updateTimer.cancel();
        }

        updateTimer = null;
    }
    
    @Override
    public void cancelAnnouncementTimer() {
        if(announcementTimer != null){
            announcementTimer.cancel();
        }
    
        announcementTimer = null;
    }

    public static void disable() {
        instance = null;
    }
    
}
