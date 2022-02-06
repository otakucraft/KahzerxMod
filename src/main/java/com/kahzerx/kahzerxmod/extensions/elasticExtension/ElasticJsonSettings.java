package com.kahzerx.kahzerxmod.extensions.elasticExtension;

import java.util.List;

public class ElasticJsonSettings {
    private List<ElasticSettings> settings;
    public ElasticJsonSettings(List<ElasticSettings> settings) {
        this.settings = settings;
    }

    public List<ElasticSettings> getSettings() {
        return settings;
    }
}
