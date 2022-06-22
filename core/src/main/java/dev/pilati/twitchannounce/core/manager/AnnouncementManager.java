package dev.pilati.twitchannounce.core.manager;


public abstract class AnnouncementManager {

    // public AnnouncementManager(TwitchAnnounce plugin) {
    //     this.plugin = plugin;
    // }
    
    // public void initTimer(){
    //     long ticks = plugin.configManager.getAnnounceTimerSeconds() * 20L;
    //     this.timer = new AnnounceTimer(this);
    //     timer.runTaskTimer(plugin, ticks, ticks);
    // }

    // public void cancelTimer() {
    //     if(timer != null && !timer.isCancelled()){
    //         timer.cancel();
    //         timer = null;
    //     }
    // }

    public abstract void announceStreamersInLive();
}
