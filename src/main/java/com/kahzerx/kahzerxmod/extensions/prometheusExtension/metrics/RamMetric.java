package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;

public class RamMetric extends AbstractMetric {
    public RamMetric(String name, String help) {
        super(name, help, "ram");
    }

    @Override
    public void update(PrometheusExtension extension) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024;
        long allocatedMemory = runtime.totalMemory() / 1024;
        long freeMemory = runtime.freeMemory() / 1024;
        this.getGauge().labels("used").set(allocatedMemory - freeMemory);
        this.getGauge().labels("max").set(maxMemory);
    }
}
