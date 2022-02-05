package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.EntityInstance;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class EntityProfiler extends AbstractProfiler {
    public static List<EntityInstance> entityList = new ArrayList<>();

    @Override
    public void onTick(MinecraftServer server) {
        HashMap<String, Object> entities = new HashMap<>();
        entities.put("entities", entityList);
        this.addResult(server.getTicks(), entities);
        entityList.clear();
    }

    public static void addEntity(Entity e, ServerWorld world) {
        Identifier id = EntityType.getId(e.getType());
        if (id == null) {
            return;
        }
        BlockPos pos = e.getBlockPos();
        entityList.add(new EntityInstance(id.getPath(), world.getRegistryKey().getValue().getPath(), pos.getX(), pos.getY(), pos.getZ()));
    }
}
