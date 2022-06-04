package dev.pilati.twitchannounce;

import org.bukkit.plugin.java.JavaPlugin;

import dev.pilati.twitchannounce.command.TwitchAnnounceCommand;
import dev.pilati.twitchannounce.managers.AnnouncementManager;
import dev.pilati.twitchannounce.managers.ConfigManager;
import dev.pilati.twitchannounce.managers.IntegrationManager;
import dev.pilati.twitchannounce.managers.PermissionManager;

/*
 * Streamer Priorities
 * Link Minecraft Account with live nick
 * 
 * streamer add MinecraftNick TwitchNick [Priority]
 */

public class TwitchAnnounce extends JavaPlugin{
	public PermissionManager permissionManager = new PermissionManager(this);
	public ConfigManager configManager = new ConfigManager(this);
	public IntegrationManager integrationManager = new IntegrationManager(this);
	public AnnouncementManager announcementManager = new AnnouncementManager(this);
	
	@Override
	public void onEnable() {
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
		permissionManager.setupPermissions();
		announcementManager.initTimer();
	}

	@Override
	public void onDisable() {
		announcementManager.cancelTimer();
	}
}
