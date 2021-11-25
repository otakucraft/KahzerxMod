package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class OnlinePlayersMetrics extends AbstractMetric {
    public OnlinePlayersMetrics(String name, String help) {
        super(name, help, "world", "name", "uuid");
    }

    @Override
    public void update(PrometheusExtension extension) {
        for (ServerWorld world : extension.getServer().getWorlds()) {
            for (ServerPlayerEntity player : world.getPlayers()) {
                this.getGauge().labels(
                        world.getRegistryKey().getValue().getPath(),
                        player.getName().getString(),
                        player.getUuidAsString()
                ).set(1);
            }
        }
    }
}
