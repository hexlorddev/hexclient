package com.hexclient.core;

import com.hexclient.config.ConfigManager;
import com.hexclient.features.FeatureManager;
import com.hexclient.gui.GuiManager;
import com.hexclient.utils.Logger;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

/**
 * HexClient - Advanced Minecraft Client
 * Combines the best features from popular clients with superior UI and performance
 */
public class HexClient implements ClientModInitializer {
    
    public static final String MOD_ID = "hexclient";
    public static final String MOD_NAME = "HexClient";
    public static final String VERSION = "1.0.0";
    
    private static HexClient instance;
    
    // Core managers
    private ConfigManager configManager;
    private FeatureManager featureManager;
    private GuiManager guiManager;
    
    // Key bindings
    private KeyBinding toggleGuiKey;
    private KeyBinding toggleClickGuiKey;
    private KeyBinding toggleHudKey;
    
    @Override
    public void onInitializeClient() {
        instance = this;
        
        Logger.info("Initializing " + MOD_NAME + " v" + VERSION);
        
        // Initialize core managers
        this.configManager = new ConfigManager();
        this.featureManager = new FeatureManager();
        this.guiManager = new GuiManager();
        
        // Setup key bindings
        setupKeyBindings();
        
        // Register event handlers
        registerEvents();
        
        // Initialize features
        featureManager.initializeFeatures();
        
        Logger.info(MOD_NAME + " initialization complete!");
    }
    
    private void setupKeyBindings() {
        toggleGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hexclient.toggle_gui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_SHIFT,
            "category.hexclient.general"
        ));
        
        toggleClickGuiKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hexclient.toggle_clickgui",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_RIGHT_CONTROL,
            "category.hexclient.general"
        ));
        
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
            "key.hexclient.toggle_hud",
            InputUtil.Type.KEYSYM,
            GLFW.GLFW_KEY_H,
            "category.hexclient.general"
        ));
    }
    
    private void registerEvents() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) return;
            
            // Handle key presses
            while (toggleGuiKey.wasPressed()) {
                guiManager.toggleMainGui();
            }
            
            while (toggleClickGuiKey.wasPressed()) {
                guiManager.toggleClickGui();
            }
            
            while (toggleHudKey.wasPressed()) {
                guiManager.toggleHud();
            }
            
            // Update features
            featureManager.onTick();
        });
    }
    
    public static HexClient getInstance() {
        return instance;
    }
    
    public ConfigManager getConfigManager() {
        return configManager;
    }
    
    public FeatureManager getFeatureManager() {
        return featureManager;
    }
    
    public GuiManager getGuiManager() {
        return guiManager;
    }
}