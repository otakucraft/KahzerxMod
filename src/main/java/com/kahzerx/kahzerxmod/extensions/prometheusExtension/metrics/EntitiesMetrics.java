package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.util.HashMap;

public class EntitiesMetrics extends AbstractMetric {
    public EntitiesMetrics(String name, String help) {
        super(name, help, "world", "group", "type");
    }

    @Override
    public void update(PrometheusExtension extension) {
        for (ServerWorld world : extension.getServer().getWorlds()) {
            HashMap<String, Integer> worldEntities = new HashMap<>();
            world.iterateEntities().forEach(e -> worldEntities.put(
                    Registry.ENTITY_TYPE.getId(e.getType()).getPath(),
                    worldEntities.getOrDefault(Registry.ENTITY_TYPE.getId(e.getType()).getPath(), 0) + 1
            ));
            for (String type : worldEntities.keySet()) {
                this.getGauge().labels(
                        world.getRegistryKey().getValue().getPath(),
                        Registry.ENTITY_TYPE.get(new Identifier(type)).getSpawnGroup().getName(),
                        type
                ).set(worldEntities.get(type));
            }
        }
    }
}
