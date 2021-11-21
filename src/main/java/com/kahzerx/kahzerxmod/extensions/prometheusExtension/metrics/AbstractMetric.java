package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import io.prometheus.client.Gauge;
import net.minecraft.server.MinecraftServer;

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

    public abstract void update(MinecraftServer server);

    public Gauge getGauge() {
        return gauge;
    }

    public String getName() {
        return name;
    }
}
