package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import net.minecraft.server.MinecraftServer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractProfiler {
    protected Map<Integer, ProfilerResult> results = new LinkedHashMap<>();

    public abstract void onTick(MinecraftServer server, String id);

    public void addResult(int tick, ProfilerResult results) {
        this.results.put(tick, results);
    }

    public void clearResults() {
        if (results.size() > 20) {
            List<Integer> keys = results.keySet().stream().toList();
            List<Integer> lastKeys = keys.subList(keys.size() - 20, keys.size());
            results.keySet().retainAll(lastKeys);
        }
    }

    public Map<Integer, ProfilerResult> getResults() {
        return results;
    }

    public ProfilerResult getResult() {
        List<Integer> keys = results.keySet().stream().toList();
        return results.get(keys.get(keys.size() - 1));
    }
}
