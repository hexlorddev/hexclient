package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;

public class EntityCulling extends Feature {
    public EntityCulling() {
        super("EntityCulling", "Optimizes entity rendering", FeatureCategory.PERFORMANCE);
    }
}