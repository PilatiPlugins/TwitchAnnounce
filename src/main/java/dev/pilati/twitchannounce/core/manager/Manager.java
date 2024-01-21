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

    public abstract void createUpdateManager();
    
    public abstract void disableAnnounccementManager();
    
    public abstract void disableConfigurationManager();
    
    public abstract void disableLoggingManager();
    
    public abstract void disableMetricsManager();
    
    public abstract void disableTimerManager();
    
    public abstract void disableUpdateManager();

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
        createUpdateManager();
    }

    public void enable(){
        LoggingManager.debug("Manager.enable - initAllManagers");
        initAllManagers();
        
        try{
            LoggingManager.debug("Manager.enable - loadConfiguration");
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

        LoggingManager.debug("Manager.enable - registerCommands");
        registerCommands();
        LoggingManager.debug("Manager.enable - initTimers");
        initTimers();
        LoggingManager.debug("Manager.enable - registerEvents");
        registerEvents();
    }

    public void disable() {
        LoggingManager.debug("Manager.disable - cancelTimers");
        cancelTimers();

        LoggingManager.debug("Manager.disable - disable all Managers");
        disableAnnounccementManager();
        disableConfigurationManager();
        disableLoggingManager();
        disableMetricsManager();
        disableTimerManager();
        disableUpdateManager();
    }
}
