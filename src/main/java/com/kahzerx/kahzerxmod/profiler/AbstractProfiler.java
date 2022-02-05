package com.kahzerx.kahzerxmod.profiler;

import net.minecraft.server.MinecraftServer;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractProfiler {
    protected Map<Integer, Map<String, Object>> results = new LinkedHashMap<>();

    public abstract void onTick(MinecraftServer server);

    public void addResult(int tick, Map<String, Object> results) {
        this.results.put(tick, results);
    }

    public void clearResults() {
        if (results.size() > 20) {
            List<Integer> keys = results.keySet().stream().toList();
            List<Integer> lastKeys = keys.subList(keys.size() - 20, keys.size());
            results.keySet().retainAll(lastKeys);
        }
    }
}
