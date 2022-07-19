package dev.pilati.twitchannounce.core.manager;

public abstract class TimerManager {
    protected static TimerManager instance;

    protected abstract void initUpdateTimer();
    protected abstract void initAnnouncementTimer();
    protected abstract void initUpdateCheckerTimer();
    protected abstract void cancelUpdateTimer();
    protected abstract void cancelAnnouncementTimer();
    protected abstract void cancelUpdateCheckerTimer();


    public static TimerManager getInstance() {
        return instance;
    }

    public static void initTimers() {
        getInstance().initUpdateTimer();
        getInstance().initAnnouncementTimer();
        getInstance().initUpdateCheckerTimer();
    }

    public static void cancelTimers() {
        getInstance().cancelUpdateTimer();
        getInstance().cancelAnnouncementTimer();
        getInstance().cancelUpdateCheckerTimer();
    }

    public static void resetAnnouncementTimer() {
        getInstance().cancelAnnouncementTimer();
        getInstance().initAnnouncementTimer();
    }
}
