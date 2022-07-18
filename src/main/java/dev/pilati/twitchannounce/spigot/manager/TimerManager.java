package dev.pilati.twitchannounce.spigot.manager;

import dev.pilati.twitchannounce.spigot.TwitchAnnounce;
import dev.pilati.twitchannounce.spigot.task.AnnouncementTask;
import dev.pilati.twitchannounce.spigot.task.UpdateTask;

public class TimerManager extends dev.pilati.twitchannounce.core.manager.TimerManager{

    private UpdateTask updateTimer;
    private AnnouncementTask announcementTimer;

    public TimerManager(){
        instance = this;
    }

    @Override
    public void initUpdateTimer() {
        long secs = 20 * ConfigurationManager.getConfig().getLong("announcement.updateInterval");
        if(secs > 0){
            updateTimer = new UpdateTask();
            updateTimer.runTaskTimer(TwitchAnnounce.getInstance(), secs, secs);
        }
    }

    @Override
    public void initAnnouncementTimer() {
        long secs = 20 * ConfigurationManager.getConfig().getLong("announcement.unmodifiedInterval");
        if(secs > 0){
            announcementTimer = new AnnouncementTask();
            announcementTimer.runTaskTimer(TwitchAnnounce.getInstance(), secs, secs);
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
