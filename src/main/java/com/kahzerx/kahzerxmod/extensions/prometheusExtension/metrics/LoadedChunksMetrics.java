package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;
import net.minecraft.server.world.ServerWorld;

public class LoadedChunksMetrics extends AbstractMetric {
    public LoadedChunksMetrics(String name, String help) {
        super(name, help, "world");
    }

    @Override
    public void update(PrometheusExtension extension) {
        for (ServerWorld world : extension.getServer().getWorlds()) {
            this.getGauge().labels(world.getRegistryKey().getValue().getPath()).set(world.getChunkManager().getLoadedChunkCount());
        }
    }
}
