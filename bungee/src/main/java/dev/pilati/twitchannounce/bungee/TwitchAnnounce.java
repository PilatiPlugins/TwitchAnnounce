package dev.pilati.twitchannounce.bungee;

import java.io.IOException;

import dev.pilati.twitchannounce.bungee.command.TwitchAnnounceCommand;
import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;
import dev.pilati.twitchannounce.bungee.manager.ConfigManager;
import dev.pilati.twitchannounce.bungee.manager.LoggingManager;
import dev.pilati.twitchannounce.core.manager.IntegrationManager;
import net.md_5.bungee.api.plugin.Plugin;

public class TwitchAnnounce extends Plugin{
    public ConfigManager configManager;
    public AnnouncementManager announcementManager;
    public LoggingManager loggingManager;
    public IntegrationManager integrationManager;

    @Override
    public void onEnable(){
        try {
            configManager = new ConfigManager(this);
            configManager.loadConfig();

            announcementManager = new AnnouncementManager(this);
            loggingManager = new LoggingManager(this);
            integrationManager = new IntegrationManager(configManager, loggingManager);

            String clientId = configManager.getConfigString("twitch.cliendId");
            String clientSecret = configManager.getConfigString("twitch.clientSecret");

            if(clientId == null || "TwitchClientIdHere".equals(clientId) || clientSecret == null || "TwitchclientSecretHere".equals(clientSecret)) {
                this.getLogger().severe("Please set the twitch.clientId and twitch.clientSecret in the config.yml");
                this.forceDisable();
                return;
            }

            configManager.loadSettings();
            getProxy().getPluginManager().registerCommand(this, new TwitchAnnounceCommand("twitchannounce", this));
            announcementManager.initTimer();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
	public void onDisable() {
		announcementManager.cancelTimer();
	}

    public void forceDisable() {
        this.onDisable();

        while(this.getLogger().getHandlers().length > 0){
            this.getLogger().getHandlers()[0].close();
        }

        this.getProxy().getPluginManager().unregisterListeners(this);
        this.getProxy().getPluginManager().unregisterCommands(this);
        this.getProxy().getScheduler().cancel(this);
        this.getExecutorService().shutdownNow();
    }
    
}
