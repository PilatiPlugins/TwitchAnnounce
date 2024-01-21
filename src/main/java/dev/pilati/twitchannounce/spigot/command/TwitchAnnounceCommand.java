package dev.pilati.twitchannounce.spigot.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.util.StringUtil;

import dev.pilati.twitchannounce.spigot.manager.AnnouncementManager;
import dev.pilati.twitchannounce.core.manager.LoggingManager;
import dev.pilati.twitchannounce.core.util.Streamer;
import dev.pilati.twitchannounce.spigot.manager.ConfigurationManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class TwitchAnnounceCommand  implements CommandExecutor, TabCompleter{
	private static final String[] COMMANDS = { "announce", "add", "list", "remove", "help", "debug" };

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if(args.length == 0 || "help".equalsIgnoreCase(args[0])) {
			this.showHelp(sender);
			return true;
		}
		
		if("add".equalsIgnoreCase(args[0])) {
			addStreamer(sender, args);
			return true;
		}
		
		if("list".equalsIgnoreCase(args[0])) {
			listStreamers(sender);
			return true;
		}
		
		if("remove".equalsIgnoreCase(args[0])) {
			removeStreamer(sender, args);
			return true;
		}
		
		if("announce".equalsIgnoreCase(args[0])) {
			announceNow(sender);
			return true;
		}
		
		if("debug".equalsIgnoreCase(args[0])) {
			toggleDebug(sender);
			return true;
		}
		
		this.showHelp(sender);
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
		List<String> list = new ArrayList<>();

		if(args.length == 0 || (args.length == 1 && !Arrays.asList(COMMANDS).contains(args[0]))){
			StringUtil.copyPartialMatches(args[0], Arrays.asList(COMMANDS), list);
			return list;
		}
		
		if(args.length == 2) {

			if("add".equalsIgnoreCase(args[0]) || "remove".equalsIgnoreCase(args[0])){
				list.add("{twitch_username}");
			}

			return list;
		}
		
		if(args.length == 3) {
			if("add".equalsIgnoreCase(args[0])){
				list.add("{minecraft_nick}");
				list.addAll(Bukkit.getOnlinePlayers().stream().map(Player::getDisplayName).collect(Collectors.toList()));
			}

			return list;
		}
		
		if(args.length == 4) {
			if("add".equalsIgnoreCase(args[0])){
				list.add("{priority}");
			}

			return list;
		}

		return list;
	}

	private void removeStreamer(CommandSender sender, String[] args) {
		if(!sender.hasPermission("twitchannounce.remove")) {
			String message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
			sender.sendMessage(message.replace("{permission}", "[twitchannounce.remove]"));
			return;
		}
		
		if(args.length < 2) {
			sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.invalidCommand"));
			return;
		}
		
		ConfigurationManager.removeStreamer(args[1].toLowerCase());
		String message = ConfigurationManager.getConfig().getMessage("messages.removedStreamer");
		sender.sendMessage(message.replace("{twitchUser}", args[1]));
	}
	
	public void listStreamers(CommandSender sender) {
		if(!sender.hasPermission("twitchannounce.list")) {
			String message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
			sender.sendMessage(message.replace("{permission}", "[twitchannounce.list]"));
			return;
		}
		
		boolean remove = (sender instanceof Player) && sender.hasPermission("twitchannounce.remove");
		List<Streamer> streamers = ConfigurationManager.getAllStreamers();
		
		if(streamers.size() == 0){
			sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.streamerList.empty"));
			return;
		}

		sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.streamerList.start"));
		
		for(int i = 0; i < streamers.size(); i++) {
			
			Streamer streamer = streamers.get(i);
			TextComponent message = new TextComponent();
			
			if(remove) {
				TextComponent link = new TextComponent(ChatColor.RED + "[X] ");
				link.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/twitchannounce remove " + streamer.twitchUser));
				message.addExtra(link);
			}
			
			String streamerMessage = ConfigurationManager.getConfig()
										   .getMessage("messages.streamerList.streamer")
										   .replace("{minecraftNick}", streamer.minecraftNick)
										   .replace("{twitchUser}", streamer.twitchUser)
										   .replace("{priority}", streamer.priority.toString());
			message.addExtra(streamerMessage);
			sender.spigot().sendMessage(message);
		}
		
		sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.streamerList.end"));
	}

	private void showHelp(CommandSender sender) {
		ArrayList<String> messages = new ArrayList<>();

		if(sender.hasPermission("twitchannounce.list")) {
			messages.add(ConfigurationManager.getConfig().getMessage("messages.help.list"));
		}
		
		if(sender.hasPermission("twitchannounce.add")) {
			messages.add(ConfigurationManager.getConfig().getMessage("messages.help.add"));
		}
		
		if(sender.hasPermission("twitchannounce.remove")) {
			messages.add(ConfigurationManager.getConfig().getMessage("messages.help.remove"));
		}
		
		if(sender.hasPermission("twitchannounce.announce")) {
			messages.add(ConfigurationManager.getConfig().getMessage("messages.help.announce"));
		}
		
		if(messages.size() == 0){
			sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.help.none"));
			return;
		}

		sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.help.header"));
		for(String message : messages) {
			sender.sendMessage(message);
		}
		sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.help.footer"));
	}

	private void addStreamer(CommandSender sender, String[] args) {
		if(!sender.hasPermission("twitchannounce.add")) {
			String message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
			sender.sendMessage(message.replace("{permission}", "[twitchannounce.add]"));
			return;
		}
		
		if(args.length < 3) {
			sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.invalidCommand"));
			return;
		}
		
		Streamer streamer = new Streamer();
		streamer.twitchUser = args[1];
		streamer.minecraftNick = args[2];
		if(args.length > 3) {
			streamer.priority = Integer.valueOf(args[3]);
		}
		
		ConfigurationManager.addStreamer(streamer);
		sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.addedStreamer"));
	}
	
	private void announceNow(CommandSender sender) {
		if(!sender.hasPermission("twitchannounce.announce")) {
			String message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
			sender.sendMessage(message.replace("{permission}", "[twitchannounce.announce]"));
			return;
		}

		sender.sendMessage(ConfigurationManager.getConfig().getMessage("messages.announced"));
		AnnouncementManager.announceLiveToAll();
	}

	private void toggleDebug(CommandSender sender) {
		if(!sender.hasPermission("twitchannounce.debug")) {
			String message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
			sender.sendMessage(message.replace("{permission}", "[twitchannounce.debug]"));
			return;
		}

		LoggingManager.debug = !LoggingManager.debug;
		String message = ConfigurationManager.getConfig().getMessage("messages.debug");
		sender.sendMessage(message.replace("{state}", LoggingManager.debug ? "enabled" : "disabled"));
	}
}