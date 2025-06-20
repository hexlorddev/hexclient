package com.hexclient.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.hexclient.utils.Logger;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuration manager for HexClient
 * Handles saving and loading of client settings
 */
public class ConfigManager {
    
    private static final String CONFIG_DIR = "hexclient";
    private static final String CONFIG_FILE = "config.json";
    
    private final Gson gson;
    private final File configDir;
    private final File configFile;
    
    private HexClientConfig config;
    
    public ConfigManager() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        this.configDir = new File(FabricLoader.getInstance().getConfigDir().toFile(), CONFIG_DIR);
        this.configFile = new File(configDir, CONFIG_FILE);
        
        // Create config directory if it doesn't exist
        if (!configDir.exists()) {
            configDir.mkdirs();
        }
        
        loadConfig();
    }
    
    public void loadConfig() {
        if (!configFile.exists()) {
            Logger.info("Config file not found, creating default configuration");
            config = new HexClientConfig();
            saveConfig();
            return;
        }
        
        try (FileReader reader = new FileReader(configFile)) {
            config = gson.fromJson(reader, HexClientConfig.class);
            if (config == null) {
                config = new HexClientConfig();
            }
            Logger.info("Configuration loaded successfully");
        } catch (IOException e) {
            Logger.error("Failed to load configuration", e);
            config = new HexClientConfig();
        }
    }
    
    public void saveConfig() {
        try (FileWriter writer = new FileWriter(configFile)) {
            gson.toJson(config, writer);
            Logger.info("Configuration saved successfully");
        } catch (IOException e) {
            Logger.error("Failed to save configuration", e);
        }
    }
    
    public HexClientConfig getConfig() {
        return config;
    }
    
    /**
     * Main configuration class for HexClient
     */
    public static class HexClientConfig {
        public GeneralSettings general = new GeneralSettings();
        public GuiSettings gui = new GuiSettings();
        public PerformanceSettings performance = new PerformanceSettings();
        public CombatSettings combat = new CombatSettings();
        public Map<String, Object> moduleSettings = new HashMap<>();
        
        public static class GeneralSettings {
            public boolean enableClientBranding = true;
            public boolean showWelcomeMessage = true;
            public boolean enableUpdates = true;
            public String theme = "dark";
        }
        
        public static class GuiSettings {
            public boolean enableAnimations = true;
            public boolean enableBlur = true;
            public float guiScale = 1.0f;
            public int primaryColor = 0x6A5ACD; // Slate Blue
            public int accentColor = 0xFF6B35;  // Orange
            public boolean rainbowMode = false;
        }
        
        public static class PerformanceSettings {
            public boolean enableOptimizations = true;
            public boolean reducedAnimations = false;
            public int maxFps = 240;
            public boolean enableVsync = false;
        }
        
        public static class CombatSettings {
            public boolean enableCombatFeatures = true;
            public boolean showCombatInfo = true;
            public boolean enableReach = false;
            public double reachDistance = 3.0;
        }
    }
}