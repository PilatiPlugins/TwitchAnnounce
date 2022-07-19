package dev.pilati.twitchannounce.bungee.manager;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;
import dev.pilati.twitchannounce.bungee.command.TwitchAnnounceCommand;
import dev.pilati.twitchannounce.bungee.event.PlayerEvents;
import dev.pilati.twitchannounce.core.manager.UpdateManager;

public class Manager extends dev.pilati.twitchannounce.core.manager.Manager {

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
        TwitchAnnounce.getInstance().getProxy().getPluginManager().registerCommand(TwitchAnnounce.getInstance(), new TwitchAnnounceCommand("twitchannounce"));
    }

    @Override
    public void disablePlugin() {
        TwitchAnnounce.getInstance().onDisable();
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
        TwitchAnnounce.getInstance().getProxy().getPluginManager().registerListener(TwitchAnnounce.getInstance(), new PlayerEvents());
    }

    @Override
    public void createUpdateManager() {
        UpdateManager.currentVersion = TwitchAnnounce.getInstance().getDescription().getVersion();
    }

    @Override
    public void disableUpdateManager() {
        
    }
}
