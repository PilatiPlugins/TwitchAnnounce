package dev.pilati.twitchannounce.spigot.manager;

import dev.pilati.twitchannounce.core.manager.UpdateManager;
import dev.pilati.twitchannounce.spigot.TwitchAnnounce;
import dev.pilati.twitchannounce.spigot.command.TwitchAnnounceCommand;
import dev.pilati.twitchannounce.spigot.event.PlayerEvents;

public class Manager extends dev.pilati.twitchannounce.core.manager.Manager{

    @Override
    public void createAnnounccementManager() {
        new AnnouncementManager();
    }

    @Override
    public void createConfigurationManager() {
        new ConfigurationManager();
    }

    @Override
    public void createLoggingManager() {
        new LoggingManager();
    }

    @Override
    public void createMetricsManager() {
        MetricsManager.init();
    }

    @Override
    public void createTimerManager() {
        new TimerManager();
    }
        

    @Override
    public void registerCommands() {
        TwitchAnnounce.getInstance().getCommand("twitchannounce").setExecutor(new TwitchAnnounceCommand());
    }

    @Override
    public void disablePlugin() {
        TwitchAnnounce.getInstance().getServer().getPluginManager().disablePlugin(TwitchAnnounce.getInstance());
    }

    @Override
    public void cancelTimers() {
        TimerManager.cancelTimers();
    }

    @Override
    public void initTimers() {
        TimerManager.initTimers();
    }

    @Override
    public void disableAnnounccementManager() {
        AnnouncementManager.disable();
    }

    @Override
    public void disableConfigurationManager() {
        ConfigurationManager.disable();
    }

    @Override
    public void disableLoggingManager() {
        LoggingManager.disable();
    }

    @Override
    public void disableMetricsManager() {
        MetricsManager.disable();
    }

    @Override
    public void disableTimerManager() {
        TimerManager.disable();
    }

    @Override
    public void registerEvents() {
        TwitchAnnounce.getInstance().getServer().getPluginManager().registerEvents(new PlayerEvents(), TwitchAnnounce.getInstance());
    }

    @Override
    public void createUpdateManager() {
        UpdateManager.currentVersion = TwitchAnnounce.getInstance().getDescription().getVersion();
    }

    @Override
    public void disableUpdateManager() {
    }
}
