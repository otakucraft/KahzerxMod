package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

public class OnlinePlayersMetrics extends AbstractMetric {
    public OnlinePlayersMetrics(String name, String help) {
        super(name, help, "world", "name", "uuid");
    }

    @Override
    public void update(MinecraftServer server) {
        for (ServerWorld world : server.getWorlds()) {
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
