package com.hexclient.features;

import com.hexclient.core.HexClient;
import com.hexclient.utils.Logger;

/**
 * Base class for all HexClient features/modules
 */
public abstract class Feature {
    
    protected final String name;
    protected final String description;
    protected final FeatureCategory category;
    protected boolean enabled = false;
    
    public Feature(String name, String description, FeatureCategory category) {
        this.name = name;
        this.description = description;
        this.category = category;
    }
    
    /**
     * Called when the feature is enabled
     */
    protected void onEnable() {
        Logger.debug("Enabled feature: " + name);
    }
    
    /**
     * Called when the feature is disabled
     */
    protected void onDisable() {
        Logger.debug("Disabled feature: " + name);
    }
    
    /**
     * Called every client tick when the feature is enabled
     */
    public void onTick() {
        // Override in subclasses
    }
    
    /**
     * Called every render frame when the feature is enabled
     */
    public void onRender() {
        // Override in subclasses
    }
    
    /**
     * Toggle the feature on/off
     */
    public void toggle() {
        if (enabled) {
            disable();
        } else {
            enable();
        }
    }
    
    /**
     * Enable the feature
     */
    public void enable() {
        if (!enabled) {
            enabled = true;
            try {
                onEnable();
            } catch (Exception e) {
                Logger.error("Error enabling feature " + name, e);
                enabled = false;
            }
        }
    }
    
    /**
     * Disable the feature
     */
    public void disable() {
        if (enabled) {
            enabled = false;
            try {
                onDisable();
            } catch (Exception e) {
                Logger.error("Error disabling feature " + name, e);
            }
        }
    }
    
    // Getters
    public String getName() {
        return name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public FeatureCategory getCategory() {
        return category;
    }
    
    public boolean isEnabled() {
        return enabled;
    }
    
    // Utility methods for features
    protected HexClient getClient() {
        return HexClient.getInstance();
    }
}