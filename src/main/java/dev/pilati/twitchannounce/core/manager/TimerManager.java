package dev.pilati.twitchannounce.core.manager;

public abstract class TimerManager {
    protected static TimerManager instance;

    protected abstract void initUpdateTimer();
    protected abstract void initAnnouncementTimer();
    protected abstract void cancelUpdateTimer();
    protected abstract void cancelAnnouncementTimer();

    public static TimerManager getInstance() {
        return instance;
    }

    public static void initTimers() {
        getInstance().initUpdateTimer();
        getInstance().initAnnouncementTimer();
    }

    public static void cancelTimers() {
        getInstance().cancelUpdateTimer();
        getInstance().cancelAnnouncementTimer();
    }

    public static void resetAnnouncementTimer() {
        getInstance().cancelAnnouncementTimer();
        getInstance().initAnnouncementTimer();
    }
}
