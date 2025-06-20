package com.hexclient.features;

import com.hexclient.features.modules.*;
import com.hexclient.utils.Logger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Feature manager for HexClient
 * Handles registration and management of all client features and modules
 */
public class FeatureManager {
    
    private final Map<String, Feature> features = new HashMap<>();
    private final Map<FeatureCategory, List<Feature>> categorizedFeatures = new HashMap<>();
    
    public void initializeFeatures() {
        Logger.info("Initializing client features...");
        
        // Combat Features (similar to Wurst, Meteor)
        registerFeature(new AutoCrystal());
        registerFeature(new KillAura());
        registerFeature(new AntiKnockback());
        registerFeature(new AutoTotem());
        registerFeature(new CrystalAura());
        registerFeature(new AutoArmor());
        
        // Advanced Bot-Fighting Features
        registerFeature(new BotPvP());
        registerFeature(new AutoDodge());
        registerFeature(new BotDetector());
        
        // Movement Features
        registerFeature(new Flight());
        registerFeature(new Speed());
        registerFeature(new NoFall());
        registerFeature(new Sprint());
        registerFeature(new AutoWalk());
        registerFeature(new ElytraFly());
        
        // World Features
        registerFeature(new Nuker());
        registerFeature(new AutoMine());
        registerFeature(new Scaffold());
        registerFeature(new AutoBuild());
        registerFeature(new ChestESP());
        registerFeature(new XRay());
        
        // Advanced World Automation
        registerFeature(new AutoBridge());
        registerFeature(new AutoBedBreaker());
        registerFeature(new FastPlace());
        
        // Visual Features (similar to Lunar, Badlion)
        registerFeature(new FullBright());
        registerFeature(new NoWeather());
        registerFeature(new CustomSky());
        registerFeature(new Zoom());
        registerFeature(new FreeCam());
        registerFeature(new ESP());
        
        // Performance Features
        registerFeature(new FPSBoost());
        registerFeature(new NoLag());
        registerFeature(new EntityCulling());
        registerFeature(new ChunkAnimator());
        
        // Misc Features
        registerFeature(new AutoReconnect());
        registerFeature(new ChatFilter());
        registerFeature(new NameProtect());
        registerFeature(new AntiAFK());
        registerFeature(new AutoCollector());
        
        Logger.info("Initialized " + features.size() + " features");
    }
    
    private void registerFeature(Feature feature) {
        features.put(feature.getName().toLowerCase(), feature);
        
        FeatureCategory category = feature.getCategory();
        categorizedFeatures.computeIfAbsent(category, k -> new ArrayList<>()).add(feature);
        
        Logger.debug("Registered feature: " + feature.getName());
    }
    
    public Feature getFeature(String name) {
        return features.get(name.toLowerCase());
    }
    
    public List<Feature> getFeaturesByCategory(FeatureCategory category) {
        return categorizedFeatures.getOrDefault(category, new ArrayList<>());
    }
    
    public List<Feature> getAllFeatures() {
        return new ArrayList<>(features.values());
    }
    
    public void onTick() {
        for (Feature feature : features.values()) {
            if (feature.isEnabled()) {
                try {
                    feature.onTick();
                } catch (Exception e) {
                    Logger.error("Error in feature " + feature.getName(), e);
                }
            }
        }
    }
    
    public void onRender() {
        for (Feature feature : features.values()) {
            if (feature.isEnabled()) {
                try {
                    feature.onRender();
                } catch (Exception e) {
                    Logger.error("Error rendering feature " + feature.getName(), e);
                }
            }
        }
    }
    
    public void toggleFeature(String name) {
        Feature feature = getFeature(name);
        if (feature != null) {
            feature.toggle();
        }
    }
    
    public void enableFeature(String name) {
        Feature feature = getFeature(name);
        if (feature != null) {
            feature.enable();
        }
    }
    
    public void disableFeature(String name) {
        Feature feature = getFeature(name);
        if (feature != null) {
            feature.disable();
        }
    }
}