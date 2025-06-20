package com.hexclient.features;

/**
 * Categories for organizing client features
 */
public enum FeatureCategory {
    COMBAT("Combat", "Combat and PvP related features"),
    MOVEMENT("Movement", "Movement and mobility features"),
    WORLD("World", "World interaction and building features"),
    VISUAL("Visual", "Visual enhancements and ESP features"),
    PERFORMANCE("Performance", "Performance optimization features"),
    MISC("Misc", "Miscellaneous utility features"),
    CLIENT("Client", "Client-specific features and settings");
    
    private final String displayName;
    private final String description;
    
    FeatureCategory(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    @Override
    public String toString() {
        return displayName;
    }
}