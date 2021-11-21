package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.MSPTMetric;

import java.util.Timer;

public class MetricReg {
    private final PrometheusExtension extension;
    private final Timer timer;
    public MetricReg(PrometheusExtension prometheusExtension) {
        this.extension = prometheusExtension;
        this.timer = new Timer();
    }

    public void runUpdater() {
        MetricUpdater metricUpdater = new MetricUpdater(this.extension);

        MSPTMetric msptMetric = new MSPTMetric("tps", "Current TPS on server.");
        msptMetric.getGauge().register();
        metricUpdater.addMetric(msptMetric);

        this.timer.schedule(metricUpdater, 1000, 1000);
    }

    public Timer getTimer() {
        return timer;
    }
}
