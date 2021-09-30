package com.kahzerx.kahzerxmod.extensions;

public class ExtensionSettings {
    private String name;
    private boolean enabled;
    private String description;

    public ExtensionSettings(String name, boolean enabled, String description) {
        this.name = name;
        this.enabled = enabled;
        this.description = description;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return "config{" +
                "name='" + name + '\'' +
                ", enabled=" + enabled +
                ", description='" + description + '\'' +
                '}';
    }
}
