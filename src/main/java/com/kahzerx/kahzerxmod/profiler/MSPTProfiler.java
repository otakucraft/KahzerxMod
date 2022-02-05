package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.MSPTInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;

import java.util.HashMap;

public class MSPTProfiler extends AbstractProfiler {
    @Override
    public void onTick(MinecraftServer server) {
        double MSPT = MathHelper.average(server.lastTickLengths) * 1.0E-6D;

        HashMap<String, Object> mspt = new HashMap<>();
        mspt.put("MSPT", new MSPTInstance(MSPT));
        this.addResult(server.getTicks(), mspt);
    }
}
