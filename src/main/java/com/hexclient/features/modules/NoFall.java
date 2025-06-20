package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;

public class NoFall extends Feature {
    public NoFall() {
        super("NoFall", "Prevents fall damage", FeatureCategory.MOVEMENT);
    }
}