package dev.pilati.twitchannounce.core.manager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.logging.Level;

import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.net.URIBuilder;

import dev.pilati.twitchannounce.core.util.Player;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public abstract class UpdateManager {
    public static String latestVersion;
    public static String currentVersion;
    private static final String spigotResourceId = "102816";

    public static void checkForUpdates(){
        LoggingManager.debug("UpdateManager.checkForUpdates - Checking for updates");
        if(currentVersion == null){
            return;
        }

        try(CloseableHttpClient client = HttpClients.createDefault()){
            URIBuilder uri = new URIBuilder("https://api.spigotmc.org/legacy/update.php?resource=" + spigotResourceId);

            HttpGet get = new HttpGet(uri.build());

            try(CloseableHttpResponse response = client.execute(get)){
                if(response.getCode() != 200){
					throw new IOException("Error getting latest version: " + response.getCode() + " " + response.getReasonPhrase());
				}

                if(response.getEntity() == null){
					throw new IOException("Error getting latest version: no response");
				}

                latestVersion = EntityUtils.toString(response.getEntity());

                LoggingManager.debug(() -> String.format("UpdateManager.checkForUpdates - Current version: %s, Latest version: %s", currentVersion, latestVersion));
                if(!currentVersion.equals(latestVersion)){
                    announceUpdate();
                }
            }
        }catch(IOException | URISyntaxException | ParseException ex){

        }
    }

    private static TextComponent makeMessage(){
        LoggingManager.debug("UpdateManager.makeMessage - Making message");
        String message = ConfigurationManager.getConfig().getMessage("messages.update");
        message = message.replace("{version}", latestVersion)
                    .replace("{spigotPage}", "https://www.spigotmc.org/resources/twitchannounce." + spigotResourceId);

        TextComponent cMessage = new TextComponent(message);
        cMessage.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_URL, "https://www.spigotmc.org/resources/twitchannounce." + spigotResourceId));
        return cMessage;
    }

    public static void announceUpdate(){
        LoggingManager.debug("UpdateManager.announceUpdate - Announcing update");
        TextComponent message = makeMessage();
        LoggingManager.getLogger().log(Level.WARNING, message.toPlainText());

        for(Player player : AnnouncementManager.getInstance().getOnlinePlayers()){
            if(player.hasPermission("twitchannounce.update")){
                player.sendMessage(message);
            }
        }
    }

    public static void verifyAnnounceToPlayer(Player player){
        LoggingManager.debug(() -> String.format("UpdateManager.verifyAnnounceToPlayer - Verifying announce to player %s", player.getName()));
        if(!currentVersion.equals(latestVersion)){
            if(player.hasPermission("twitchannounce.update")){
                player.sendMessage(makeMessage());
            }
        }
    }
}
