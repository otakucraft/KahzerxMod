package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;

public class PrometheusSettings extends ExtensionSettings {
    private int port;
    public PrometheusSettings(String name, boolean enabled, String description, int port) {
        super(name, enabled, description);
        this.port = port;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }
}
