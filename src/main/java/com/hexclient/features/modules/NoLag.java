package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;

public class NoLag extends Feature {
    public NoLag() {
        super("NoLag", "Reduces client-side lag", FeatureCategory.PERFORMANCE);
    }
}