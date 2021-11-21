package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;

public class LoadedChunksMetrics extends AbstractMetric {
    public LoadedChunksMetrics(String name, String help) {
        super(name, help, "world");
    }

    @Override
    public void update(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
            this.getGauge().labels(world.getRegistryKey().getValue().getPath()).set(world.getChunkManager().getLoadedChunkCount());
        }
    }
}
