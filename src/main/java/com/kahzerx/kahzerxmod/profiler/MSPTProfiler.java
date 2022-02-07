package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.MSPTInstance;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;

public class MSPTProfiler extends AbstractProfiler {
    @Override
    public void onTick(MinecraftServer server, String id) {
        double MSPT = MathHelper.average(server.lastTickLengths) * 1.0E-6D;

        this.addResult(server.getTicks(), new ProfilerResult("mspt", id, new MSPTInstance(MSPT)));
    }
}
