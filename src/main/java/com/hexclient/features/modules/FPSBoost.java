package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;

/**
 * FPSBoost - Performance optimization feature
 * Similar to performance features found in Lunar and Badlion clients
 */
public class FPSBoost extends Feature {
    
    public FPSBoost() {
        super("FPSBoost", "Optimizations to improve FPS", FeatureCategory.PERFORMANCE);
    }
    
    @Override
    protected void onEnable() {
        super.onEnable();
        // Apply performance optimizations
    }
    
    @Override
    protected void onDisable() {
        super.onDisable();
        // Restore default settings
    }
}