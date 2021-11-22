package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;

import java.util.HashMap;

public class BlockEntitiesMetrics extends AbstractMetric {
    public static HashMap<String, HashMap<String, Integer>> worldBlockEntities = new HashMap<>();
    public BlockEntitiesMetrics(String name, String help) {
        super(name, help, "world", "type");
    }

    @Override
    public void update(PrometheusExtension extension) {
        worldBlockEntities.forEach((world, blockEntities) -> {
            blockEntities.forEach((beName, count) -> {
                this.getGauge().labels(
                        world,
                        beName
                ).set(count);
            });
        });
    }
}
