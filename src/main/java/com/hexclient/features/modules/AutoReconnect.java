package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;

public class AutoReconnect extends Feature {
    public AutoReconnect() {
        super("AutoReconnect", "Automatically reconnects when disconnected", FeatureCategory.MISC);
    }
}