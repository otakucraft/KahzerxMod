package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.EntityInstance;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;

public class EntityProfiler extends AbstractProfiler {
    private static final ArrayList<EntityInstance> entityList = new ArrayList<>();

    @Override
    public void onTick(MinecraftServer server, String id) {
        entityList.add(new EntityInstance("", "", 0.0D, 0.0D, 0.0D));
        this.addResult(server.getTicks(), new ProfilerResult("entities", id, new ArrayList<>(entityList)));
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
