package dev.pilati.twitchannounce.spigot;

import org.bukkit.plugin.java.JavaPlugin;

import dev.pilati.twitchannounce.core.manager.IntegrationManager;
import dev.pilati.twitchannounce.spigot.manager.LoggingManager;
import dev.pilati.twitchannounce.spigot.manager.MetricsManager;
import dev.pilati.twitchannounce.spigot.command.TwitchAnnounceCommand;
import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;
import dev.pilati.twitchannounce.spigot.manager.ConfigManager;

public class TwitchAnnounce extends JavaPlugin{
    public ConfigManager configManager;
    public AnnouncementManager announcementManager;
    public IntegrationManager integrationManager;
    public LoggingManager loggingManager;
    public MetricsManager metricsManager;

    @Override
    public void onEnable() {
        configManager = new ConfigManager(this);
        loggingManager = new LoggingManager(this);
        integrationManager = new IntegrationManager(configManager, loggingManager);
        announcementManager = new AnnouncementManager(this);

        this.saveDefaultConfig();

        String clientId = this.getConfig().getString("twitch.cliendId");
		String clientSecret = this.getConfig().getString("twitch.clientSecret");

		if(clientId == null || "TwitchClientIdHere".equals(clientId) || clientSecret == null || "TwitchclientSecretHere".equals(clientSecret)) {
			this.getLogger().severe("Please set the twitch.clientId and twitch.clientSecret in the config.yml");
			this.getServer().getPluginManager().disablePlugin(this);
			return;
		}

        configManager.loadConfig();
        this.getCommand("twitchannounce").setExecutor(new TwitchAnnounceCommand(this));
        announcementManager.initTimer();
        
        metricsManager = new MetricsManager(this);
        metricsManager.init();
    }

    @Override
	public void onDisable() {
		announcementManager.cancelTimer();
	}
}
