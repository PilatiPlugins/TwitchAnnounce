package dev.pilati.twitchannounce.core.manager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;
import org.json.JSONObject;

import dev.pilati.twitchannounce.core.util.Streamer;

import org.json.JSONArray;

public class IntegrationManager {
	
	private static String getToken() throws ParseException, URISyntaxException, IOException {
		String expiresAt = ConfigurationManager.getConfig().getString("settings.twitch.expires_at");
		if(expiresAt == null || expiresAt.isEmpty()){
			return requestToken();
		}
		
		try{
			LocalDateTime expires = LocalDateTime.parse(expiresAt, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
			if (expires.isAfter(LocalDateTime.now())) {
				return ConfigurationManager.getConfig().getString("settings.twitch.token");
			}

		}catch(Exception e){
			LoggingManager.getLogger().log(Level.WARNING, "Error while parsing settings.twitch.expires_at", e);
		}

		return requestToken();
	}

	private static String requestToken() throws URISyntaxException, IOException, ParseException {

		try (CloseableHttpClient client = HttpClients.createDefault();) {
			URIBuilder uri = new URIBuilder("https://id.twitch.tv/oauth2/token");
			uri.addParameter("client_id", ConfigurationManager.getConfig().getString("twitch.cliendId"));
			uri.addParameter("client_secret", ConfigurationManager.getConfig().getString("twitch.clientSecret"));
			uri.addParameter("grant_type", "client_credentials");

			HttpPost post = new HttpPost(uri.build());

			try (CloseableHttpResponse response = client.execute(post)) {
				if (response.getCode() != 200) {
					throw new IOException("Error getting token: " + response.getCode() + " " + response.getReasonPhrase());
				}

				if (response.getEntity() == null) {
					throw new IOException("Error getting token: no response");
				}

				JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
				String token = json.getString("access_token");
				int expiresIn = json.getInt("expires_in");
				saveToken(token, expiresIn);
				return token;
			}
		}
	}

	private static void saveToken(String token, int ExpiresIn) {
		String datetime = DateTimeFormatter.ISO_LOCAL_DATE_TIME.format(LocalDateTime.now().plusSeconds(ExpiresIn));
		ConfigurationManager.getSettings().set("settings.twitch.token", token);
		ConfigurationManager.getSettings().set("settings.twitch.expires_at", datetime);
		ConfigurationManager.getInstance().saveSettings();
	}

	public static List<Streamer> getLiveStreamers(List<Streamer> streamers) throws ParseException, URISyntaxException, IOException{
		String token = getToken();

		try (CloseableHttpClient client = HttpClients.createDefault();){
			URIBuilder uri = new URIBuilder("https://api.twitch.tv/helix/streams");
			
			for(Streamer streamer : streamers){
				uri.addParameter("user_login", streamer.twitchUser);
			}

			HttpGet get = new HttpGet(uri.build());
			get.addHeader("Client-Id",  ConfigurationManager.getConfig().getString("twitch.cliendId"));
			get.addHeader("Authorization", "Bearer " + token);

			try(CloseableHttpResponse response = client.execute(get)){
				if(response.getCode() != 200){
					throw new IOException("Error getting streamers: " + response.getCode() + " " + response.getReasonPhrase());
				}

				if(response.getEntity() == null){
					throw new IOException("Error getting streamers: no response");
				}

				JSONObject json = new JSONObject(EntityUtils.toString(response.getEntity()));
				
				return streamers.stream().filter(s -> verifyStreamerIsInLive(json, s)).collect(Collectors.toList());
			}
		}
	}

	private static boolean verifyStreamerIsInLive(JSONObject json, Streamer streamer) {
		JSONArray data = json.getJSONArray("data");
		
		for(int i = 0; i < data.length(); i++){
			JSONObject twitchStreamer = data.getJSONObject(i);

			if(twitchStreamer.getString("user_login").equalsIgnoreCase(streamer.twitchUser) && "live".equals(twitchStreamer.getString("type"))){
				return true;
			}
		}

		return false;
	}
}
