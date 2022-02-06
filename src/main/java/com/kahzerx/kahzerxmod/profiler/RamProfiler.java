package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import com.kahzerx.kahzerxmod.profiler.instances.RamInstance;
import net.minecraft.server.MinecraftServer;

import java.util.HashMap;

public class RamProfiler extends AbstractProfiler {
    @Override
    public void onTick(MinecraftServer server) {
        Runtime runtime = Runtime.getRuntime();
        long maxMemory = runtime.maxMemory() / 1024 / 1024 / 1024;
        long allocatedMemory = runtime.totalMemory() / 1024 / 1024 / 1024;
        long freeMemory = runtime.freeMemory() / 1024 / 1024 / 1024;

        this.addResult(server.getTicks(), new ProfilerResult("RAM", new RamInstance((double) maxMemory, (double) allocatedMemory, (double) freeMemory)));
    }
}
