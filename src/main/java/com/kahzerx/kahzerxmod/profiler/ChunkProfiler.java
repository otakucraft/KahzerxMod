package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.ChunkInstance;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.chunk.WorldChunk;

import java.util.ArrayList;

public class ChunkProfiler extends AbstractProfiler {
    private static final ArrayList<ChunkInstance> chunkList = new ArrayList<>();

    @Override
    public void onTick(MinecraftServer server, String id) {
        chunkList.add(new ChunkInstance("", 0, 0));
        this.addResult(server.getTicks(), new ProfilerResult("loaded_chunks", id, new ArrayList<>(chunkList)));
        chunkList.clear();
    }

    public static void addChunk(WorldChunk chunk) {
        chunkList.add(new ChunkInstance(chunk.getWorld().getRegistryKey().getValue().getPath(), chunk.getPos().getCenterX(), chunk.getPos().getCenterZ()));
    }
}
