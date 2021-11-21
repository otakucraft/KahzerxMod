package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.AbstractMetric;
import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.LoadedChunksMetrics;
import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.MSPTMetric;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MetricReg {
    private final PrometheusExtension extension;
    private Timer timer;
    private final List<AbstractMetric> metrics = new ArrayList<>();
    public MetricReg(PrometheusExtension prometheusExtension) {
        this.extension = prometheusExtension;
    }

    public void registerMetrics() {
        MSPTMetric msptMetric = new MSPTMetric("mspt", "Current MSPT on server.");
        msptMetric.getGauge().register();

        LoadedChunksMetrics loadedChunksMetrics = new LoadedChunksMetrics("loaded_chunks", "amount of loaded chunks.");
        loadedChunksMetrics.getGauge().register();

        metrics.add(msptMetric);
        metrics.add(loadedChunksMetrics);
    }

    public void runUpdater() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer();
        MetricUpdater metricUpdater = new MetricUpdater(this.extension);
        for (AbstractMetric m : metrics) {
            metricUpdater.addMetric(m);
        }
        this.timer.schedule(metricUpdater, 1000, 1000);
    }

    public Timer getTimer() {
        return timer;
    }
}
