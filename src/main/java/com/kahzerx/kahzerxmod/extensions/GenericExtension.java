package com.kahzerx.kahzerxmod.extensions;

public class GenericExtension {
    private ExtensionSettings settings;
    public GenericExtension(ExtensionSettings settings) {
        this.settings = settings;
    }

    public ExtensionSettings getSettings() {
        return settings;
    }

    public void setSettings(ExtensionSettings settings) {
        this.settings = settings;
    }
}
