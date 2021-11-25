package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import java.util.List;

public class PrometheusJsonSettings {
    private List<PrometheusSettings> settings;

    public PrometheusJsonSettings(List<PrometheusSettings> settings) {
        this.settings = settings;
    }

    public List<PrometheusSettings> getSettings() {
        return settings;
    }
}
