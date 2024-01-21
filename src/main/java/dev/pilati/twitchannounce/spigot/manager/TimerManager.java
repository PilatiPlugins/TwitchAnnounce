package dev.pilati.twitchannounce.spigot.manager;

import dev.pilati.twitchannounce.spigot.TwitchAnnounce;
import dev.pilati.twitchannounce.spigot.task.AnnouncementTask;
import dev.pilati.twitchannounce.spigot.task.UpdateCheckTask;
import dev.pilati.twitchannounce.spigot.task.UpdateTask;

public class TimerManager extends dev.pilati.twitchannounce.core.manager.TimerManager{

    private UpdateTask updateTimer;
    private AnnouncementTask announcementTimer;
    private UpdateCheckTask updateCheck;

    public TimerManager(){
        instance = this;
    }

    @Override
    public void initUpdateTimer() {
        long secs = 20 * ConfigurationManager.getConfig().getLong("announcement.updateInterval");
        LoggingManager.debug(() -> String.format("TimerManager.initUpdateTimer -> secs: %d", secs));
        if(secs > 0){
            updateTimer = new UpdateTask();
            updateTimer.runTaskTimer(TwitchAnnounce.getInstance(), secs, secs);
        }
    }

    @Override
    public void initAnnouncementTimer() {
        long secs = 20 * ConfigurationManager.getConfig().getLong("announcement.unmodifiedInterval");
        LoggingManager.debug(() -> String.format("TimerManager.initAnnouncementTimer -> secs: %d", secs));
        if(secs > 0){
            announcementTimer = new AnnouncementTask();
            announcementTimer.runTaskTimer(TwitchAnnounce.getInstance(), secs, secs);
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

    protected void initUpdateCheckerTimer() {
        long secs = 86400 * 20; // 24 hours
        LoggingManager.debug(() -> String.format("TimerManager.initUpdateCheckerTimer -> secs: %d", secs));
        updateCheck = new UpdateCheckTask();
        updateCheck.runTaskTimer(TwitchAnnounce.getInstance(), secs, secs);
        updateCheck.run();
    };

    protected void cancelUpdateCheckerTimer() {
        if(updateCheck != null){
            updateCheck.cancel();
            LoggingManager.debug("TimerManager.cancelUpdateCheckerTimer -> updateCheck cancelled");
        }
    
        updateCheck = null;
    };
    
}
