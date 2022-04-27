package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.profiler.AbstractProfiler;
import com.kahzerx.kahzerxmod.profiler.TPSProfiler;

public class TPSMetric extends AbstractMetric {
    public TPSMetric(String name, String help, String... labels) {
        super(name, help, "time");
    }

    @Override
    public void update(AbstractProfiler profiler) {
        TPSProfiler p = (TPSProfiler) profiler;
        this.getGauge().labels("5sec").set(p.tps5Sec());
        this.getGauge().labels("10sec").set(p.tps10Sec());
        this.getGauge().labels("1min").set(p.tps1Min());
        this.getGauge().labels("5min").set(p.tps5Min());
        this.getGauge().labels("10min").set(p.tps10Min());
    }
}
