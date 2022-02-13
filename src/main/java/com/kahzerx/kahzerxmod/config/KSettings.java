package com.kahzerx.kahzerxmod.config;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

import java.util.List;

public class KSettings {
    private List<ExtensionSettings> settings;

    public KSettings(List<ExtensionSettings> settings) {
        this.settings = settings;
    }

    public List<ExtensionSettings> getSettings() {
        return settings;
    }

    @Override
    public String toString() {
        return "KSettings{" +
                "settings=" + settings +
                '}';
    }
}
