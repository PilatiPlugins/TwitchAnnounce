package dev.pilati.twitchannounce.core.manager;

import java.io.IOException;
import java.util.logging.Level;

import dev.pilati.twitchannounce.core.OutdatedException;

public abstract class Manager {

    public abstract void createAnnounccementManager();

    public abstract void createConfigurationManager();

    public abstract void createLoggingManager();

    public abstract void createMetricsManager();

    public abstract void createTimerManager();

    public abstract void disableAnnounccementManager();

    public abstract void disableConfigurationManager();

    public abstract void disableLoggingManager();

    public abstract void disableMetricsManager();

    public abstract void disableTimerManager();

    public abstract void registerCommands();

    public abstract void initTimers();

    public abstract void cancelTimers();

    public abstract void disablePlugin();
    
    public abstract void registerEvents();

    protected void initAllManagers() {
        createAnnounccementManager();
        createConfigurationManager();
        createLoggingManager();
        createMetricsManager();
        createTimerManager();
    }

    public void enable(){
        initAllManagers();

        try{
            ConfigurationManager.loadConfiguration();

            String clientId = ConfigurationManager.getConfig().getString("twitch.cliendId");
            String clientSecret = ConfigurationManager.getConfig().getString("twitch.clientSecret");

            if(clientId == null || "TwitchClientIdHere".equals(clientId) || clientSecret == null || "TwitchclientSecretHere".equals(clientSecret)) {
                LoggingManager.getLogger().log(Level.SEVERE, "Please set the twitch.clientId and twitch.clientSecret in the config.yml");
                disablePlugin();
                return;
            }
            
        }catch(IOException ex){
            LoggingManager.getLogger().log(Level.SEVERE, "Error loading configurations", ex);
            disablePlugin();
            return;
        } catch (OutdatedException e) {
            LoggingManager.getLogger().log(Level.SEVERE, "config.yml is outdated. Please backup existing, delete and re-create");
        }

        registerCommands();
        initTimers();
        registerEvents();
    }

    public void disable() {
        cancelTimers();

        disableAnnounccementManager();
        disableConfigurationManager();
        disableLoggingManager();
        disableMetricsManager();
        disableTimerManager();
    }
}
