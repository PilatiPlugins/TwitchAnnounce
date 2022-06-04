package dev.pilati.twitchannounce.managers;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.RegisteredServiceProvider;

import dev.pilati.twitchannounce.TwitchAnnounce;
import net.milkbowl.vault.permission.Permission;

public class PermissionManager {
	TwitchAnnounce plugin;
	private Permission permission;

	public PermissionManager(TwitchAnnounce plugin) {
		this.plugin = plugin;
	}

	public boolean hasPermission(CommandSender sender, String permissionKey) {
		if(permission != null) {
			return permission.has(sender, permissionKey);
		}
		
		return sender.hasPermission(permissionKey);
	}
	
	public void setupPermissions() {
		if(plugin.getServer().getPluginManager().isPluginEnabled("Vault")) {
			RegisteredServiceProvider<Permission> rsp = plugin.getServer().getServicesManager().getRegistration(Permission.class);
			permission = rsp.getProvider();
		}
	}
}
