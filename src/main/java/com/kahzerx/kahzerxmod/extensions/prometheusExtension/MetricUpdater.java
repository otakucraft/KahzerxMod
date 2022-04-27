package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.AbstractMetric;
import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.TPSMetric;
import com.kahzerx.kahzerxmod.profiler.AbstractProfiler;
import com.kahzerx.kahzerxmod.profiler.TPSProfiler;

import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

public class MetricUpdater extends TimerTask {
    private final List<AbstractProfiler> profilers;
    private final List<AbstractMetric> metrics = new ArrayList<>();

    public MetricUpdater(List<AbstractProfiler> profilers) {
        this.profilers = profilers;
    }

    @Override
    public void run() {
        TPSProfiler tps = null;
        for (AbstractProfiler profiler : profilers) {
            if (TPSProfiler.class.equals(profiler.getClass())) {
                tps = (TPSProfiler) profiler;
            }
        }
        for (AbstractMetric metric : this.metrics) {
            metric.getGauge().clear();
            try {
                if (metric.getClass().equals(TPSMetric.class)) {
                    metric.update(tps);
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }

    public void addMetric(AbstractMetric metric) {
        this.metrics.add(metric);
    }

    public List<AbstractMetric> getMetrics() {
        return metrics;
    }
}
