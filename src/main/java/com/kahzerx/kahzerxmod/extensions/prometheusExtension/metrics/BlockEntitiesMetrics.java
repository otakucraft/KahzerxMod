package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class BlockEntitiesMetrics extends AbstractMetric {
    public static HashMap<String, HashMap<String, Integer>> worldBlockEntities = new HashMap<>();
    public BlockEntitiesMetrics(String name, String help) {
        super(name, help, "world", "type");
    }

    @Override
    public void update(MinecraftServer server) {
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
