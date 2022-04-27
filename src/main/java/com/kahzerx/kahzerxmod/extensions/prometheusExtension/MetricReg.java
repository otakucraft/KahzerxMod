package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.AbstractMetric;
import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.TPSMetric;
import com.kahzerx.kahzerxmod.profiler.AbstractProfiler;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

public class MetricReg {
    private final List<AbstractProfiler> profilers;
    private Timer timer;
    private final List<AbstractMetric> metrics = new ArrayList<>();
    public MetricReg(List<AbstractProfiler> profilers) {
        this.profilers = profilers;
    }

    public void registerMetrics() {
        TPSMetric tpsMetric = new TPSMetric("tps", "Average TPS.");
        tpsMetric.getGauge().register();

        metrics.add(tpsMetric);
    }

    public void runUpdater() {
        if (this.timer != null) {
            this.timer.cancel();
        }
        this.timer = new Timer();
        MetricUpdater metricUpdater = new MetricUpdater(this.profilers);
        for (AbstractMetric m : metrics) {
            metricUpdater.addMetric(m);
        }
        this.timer.schedule(metricUpdater, 1000, 1000);
    }

    public Timer getTimer() {
        return timer;
    }
}
