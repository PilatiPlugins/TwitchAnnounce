package dev.pilati.twitchannounce;

public class Streamer{
	
	public String twitchUser;
	public String minecraftNick;
	public Integer priority = 0;
	public Boolean online = null;
	
	public Streamer() {
	}
	
	public Streamer(String twitchUser, String minecraftNick) {
		this.twitchUser = twitchUser;
		this.minecraftNick = minecraftNick;
	}
	
	public Streamer(String twitchUser, String minecraftNick, Integer priority) {
		this.twitchUser = twitchUser;
		this.minecraftNick = minecraftNick;
		this.priority = priority;
	}
}
