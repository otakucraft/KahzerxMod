package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics.*;

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

        LoadedChunksMetrics loadedChunksMetrics = new LoadedChunksMetrics("loaded_chunks", "Amount of loaded chunks.");
        loadedChunksMetrics.getGauge().register();

        EntitiesMetrics entitiesMetrics = new EntitiesMetrics("entities", "Amount of entities.");
        entitiesMetrics.getGauge().register();

        BlockEntitiesMetrics blockEntitiesMetrics = new BlockEntitiesMetrics("block_entities", "Amount of block entities");
        blockEntitiesMetrics.getGauge().register();

        metrics.add(msptMetric);
        metrics.add(loadedChunksMetrics);
        metrics.add(entitiesMetrics);
        metrics.add(blockEntitiesMetrics);
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
