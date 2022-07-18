package dev.pilati.twitchannounce.bungee.command;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import dev.pilati.twitchannounce.bungee.TwitchAnnounce;
import dev.pilati.twitchannounce.bungee.manager.ConfigurationManager;
import dev.pilati.twitchannounce.core.util.Streamer;
import dev.pilati.twitchannounce.bungee.manager.AnnouncementManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class TwitchAnnounceCommand extends Command implements TabExecutor {
    private static final String[] COMMANDS = { "announce", "add", "list", "remove", "help" };

    public TwitchAnnounceCommand(String name) {
        super(name);
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(args.length == 0 || "help".equalsIgnoreCase(args[0])) {
			this.showHelp(sender);
			return;
		}
		
		if("add".equalsIgnoreCase(args[0])) {
			addStreamer(sender, args);
			return;
		}
		
		if("list".equalsIgnoreCase(args[0])) {
			listStreamers(sender);
			return;
		}
		
		if("remove".equalsIgnoreCase(args[0])) {
			removeStreamer(sender, args);
			return;
		}
		
		if("announce".equalsIgnoreCase(args[0])) {
			announceNow(sender);
			return;
		}
		
		this.showHelp(sender);
		return;
    }

	@Override
	public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
		List<String> list = new ArrayList<String>();

		if(args.length == 0 || (args.length == 1 && !Arrays.asList(COMMANDS).contains(args[0]))){


			copyPartialMatches(args[0], Arrays.asList(COMMANDS), list);
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
				list.addAll(TwitchAnnounce.getInstance().getProxy().getPlayers().stream().map(ProxiedPlayer::getDisplayName).collect(Collectors.toList()));
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

    private void copyPartialMatches(String token, List<String> originals, List<String> list) {
		for (String string : originals) {
			if (string.regionMatches(true, 0, token, 0, token.length())) {
				list.add(string);
			}
		}
	}

	private void showHelp(CommandSender sender) {
        String message;
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
            message = ConfigurationManager.getConfig().getMessage("messages.help.none");
            sender.sendMessage(new TextComponent(message));
			return;
		}

        message = ConfigurationManager.getConfig().getMessage("messages.help.header");
		sender.sendMessage(new TextComponent(message));
		for(String msg : messages) {
			sender.sendMessage(new TextComponent(msg));
		}

        message = ConfigurationManager.getConfig().getMessage("messages.help.footer");
		sender.sendMessage(new TextComponent(message));
	}

    private void addStreamer(CommandSender sender, String[] args) {
        String message;
		if(!sender.hasPermission("twitchannounce.add")) {
			message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
            message = message.replace("{permission}", "[twitchannounce.add]");
			sender.sendMessage(new TextComponent(message));
			return;
		}
		
		if(args.length < 3) {
            message = ConfigurationManager.getConfig().getMessage("messages.invalidCommand");
			sender.sendMessage(new TextComponent(message));
			return;
		}
		
		Streamer streamer = new Streamer();
		streamer.twitchUser = args[1];
		streamer.minecraftNick = args[2];
		if(args.length > 3) {
			streamer.priority = Integer.valueOf(args[3]);
		}
		
		ConfigurationManager.addStreamer(streamer);
        message = ConfigurationManager.getConfig().getMessage("messages.addedStreamer");
		sender.sendMessage(new TextComponent(message));
	}

    public void listStreamers(CommandSender sender) {
        String message;
		if(!sender.hasPermission("twitchannounce.list")) {
			message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
            message = message.replace("{permission}", "[twitchannounce.list]");
			sender.sendMessage(new TextComponent(message));
			return;
		}

		boolean remove = (sender instanceof ProxiedPlayer) && sender.hasPermission("twitchannounce.remove");
		List<Streamer> streamers = ConfigurationManager.getAllStreamers();
		
		if(streamers.size() == 0){
            message = ConfigurationManager.getConfig().getMessage("messages.streamerList.empty");
			sender.sendMessage(new TextComponent(message));
			return;
		}

        message = ConfigurationManager.getConfig().getMessage("messages.streamerList.start");
		sender.sendMessage(new TextComponent(message));
		
		for(int i = 0; i < streamers.size(); i++) {
			
			Streamer streamer = streamers.get(i);
			TextComponent componentMessage = new TextComponent();
			
			if(remove) {
				TextComponent link = new TextComponent(ChatColor.RED + "[X] ");
				link.setClickEvent(new ClickEvent(ClickEvent.Action.SUGGEST_COMMAND, "/twitchannounce remove " + streamer.twitchUser));
				componentMessage.addExtra(link);
			}
			
			String streamerMessage = ConfigurationManager.getConfig()
										   .getMessage("messages.streamerList.streamer")
										   .replace("{minecraftNick}", streamer.minecraftNick)
										   .replace("{twitchUser}", streamer.twitchUser)
										   .replace("{priority}", streamer.priority.toString());
            componentMessage.addExtra(streamerMessage);
			sender.sendMessage(componentMessage);
		}
		
        message = ConfigurationManager.getConfig().getMessage("messages.streamerList.end");
		sender.sendMessage(new TextComponent(message));
	}

    private void removeStreamer(CommandSender sender, String[] args) {
        String message;
		if(!sender.hasPermission("twitchannounce.remove")) {
			message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
            message = message.replace("{permission}", "[twitchannounce.remove]");
			sender.sendMessage(new TextComponent(message));
			return;
		}
		
		if(args.length < 2) {
            message = ConfigurationManager.getConfig().getMessage("messages.invalidCommand");
			sender.sendMessage(new TextComponent(message));
			return;
		}
		
		ConfigurationManager.removeStreamer(args[1].toLowerCase());
		message = ConfigurationManager.getConfig().getMessage("messages.removedStreamer");
        message = message.replace("{twitchUser}", args[1]);
		sender.sendMessage(new TextComponent(message));
	}

    private void announceNow(CommandSender sender) {
        String message;
		if(!sender.hasPermission("twitchannounce.announce")) {
			message = ConfigurationManager.getConfig().getMessage("messages.nopermission");
            message = message.replace("{permission}", "[twitchannounce.announce]");
			sender.sendMessage(new TextComponent(message));
			return;
		}

        message = ConfigurationManager.getConfig().getMessage("messages.announced");
		sender.sendMessage(new TextComponent(message));
		AnnouncementManager.announceLiveToAll();
	}
}
