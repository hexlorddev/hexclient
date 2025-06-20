package com.hexclient.features.modules;

import com.hexclient.features.Feature;
import com.hexclient.features.FeatureCategory;

public class NoWeather extends Feature {
    public NoWeather() {
        super("NoWeather", "Removes rain and snow effects", FeatureCategory.VISUAL);
    }
}