package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;

public class TPSMetric extends AbstractMetric {
    public TPSMetric(String name, String help) {
        super(name, help, "time");
    }

    @Override
    public void update(PrometheusExtension extension) {
        this.getGauge().labels("5sec").set(extension.tps5Sec());
        this.getGauge().labels("10sec").set(extension.tps10Sec());
        this.getGauge().labels("1min").set(extension.tps1Min());
        this.getGauge().labels("5min").set(extension.tps5Min());
        this.getGauge().labels("15min").set(extension.tps15Min());
    }
}
