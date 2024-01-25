package dev.pilati.twitchannounce.bungee.manager;

import java.util.concurrent.TimeUnit;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;
import dev.pilati.twitchannounce.bungee.task.AnnouncementTask;
import dev.pilati.twitchannounce.bungee.task.UpdateCheckTask;
import dev.pilati.twitchannounce.bungee.task.UpdateTask;
import dev.pilati.twitchannounce.core.manager.UpdateManager;
import net.md_5.bungee.api.scheduler.ScheduledTask;

public class TimerManager extends dev.pilati.twitchannounce.core.manager.TimerManager {

    private ScheduledTask updateTimer;
    private ScheduledTask announcementTimer;
    private ScheduledTask updateCheckTimer;

    public TimerManager(){
        instance = this;
    }

    @Override
    public void initUpdateTimer() {
        long secs = ConfigurationManager.getConfig().getLong("announcement.updateInterval");
        LoggingManager.debug(() -> String.format("TimerManager.initUpdateTimer -> secs: %d", secs));
        if(secs > 0){
            this.updateTimer = TwitchAnnounce.getInstance().getProxy().getScheduler().schedule(
                TwitchAnnounce.getInstance(), new UpdateTask(), 1, secs, TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void initAnnouncementTimer() {
        long secs = ConfigurationManager.getConfig().getLong("announcement.unmodifiedInterval");
        LoggingManager.debug(() -> String.format("TimerManager.initAnnouncementTimer -> secs: %d", secs));
        if(secs > 0){
            this.announcementTimer = TwitchAnnounce.getInstance().getProxy().getScheduler().schedule(
                TwitchAnnounce.getInstance(), new AnnouncementTask(), 1, secs, TimeUnit.SECONDS
            );
        }
    }

    @Override
    public void cancelUpdateTimer() {
        if(updateTimer != null){
            updateTimer.cancel();
            LoggingManager.debug("TimerManager.cancelUpdateTimer -> updateTimer cancelled");
        }

        updateTimer = null;
    }
    
    @Override
    public void cancelAnnouncementTimer() {
        if(announcementTimer != null){
            announcementTimer.cancel();
            LoggingManager.debug("TimerManager.cancelAnnouncementTimer -> announcementTimer cancelled");
        }
    
        announcementTimer = null;
    }

    public static void disable() {
        instance = null;
    }

    @Override
    protected void initUpdateCheckerTimer() {
        long secs = 86400 * 20; // 24 hours
        LoggingManager.debug(() -> String.format("TimerManager.initUpdateCheckerTimer -> secs: %d", secs));
        if(secs > 0){
            this.updateCheckTimer = TwitchAnnounce.getInstance().getProxy().getScheduler().schedule(
                TwitchAnnounce.getInstance(), new UpdateCheckTask(), 1, secs, TimeUnit.SECONDS
            );

            UpdateManager.checkForUpdates();
        }
    }

    @Override
    protected void cancelUpdateCheckerTimer() {
        if(updateCheckTimer != null){
            updateCheckTimer.cancel();
            LoggingManager.debug("TimerManager.cancelUpdateCheckerTimer -> updateCheckTimer cancelled");
        }

        updateCheckTimer = null;
    }
    
}
