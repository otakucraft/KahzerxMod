package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;
import io.prometheus.client.Gauge;

public abstract class AbstractMetric {
    private final Gauge gauge;
    private final String name;

    public AbstractMetric(String name, String help, String... labels) {
        this.name = name;
        this.gauge = new Gauge.Builder().
                name(String.format("minecraft_%s", name)).
                help(help).
                labelNames(labels).
                create();
    }

    public abstract void update(PrometheusExtension extension);

    public Gauge getGauge() {
        return gauge;
    }

    public String getName() {
        return name;
    }
}
