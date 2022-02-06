package com.kahzerx.kahzerxmod.extensions.elasticProfiler;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.KahzerxServer;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.elasticExtension.ElasticExtension;
import com.kahzerx.kahzerxmod.profiler.AbstractProfiler;
import net.minecraft.server.MinecraftServer;

import java.util.concurrent.ArrayBlockingQueue;

public class ElasticProfilerExtension extends GenericExtension implements Extensions {
    private final ElasticExtension elasticExtension;
    private final ArrayBlockingQueue<AbstractProfiler> queue = new ArrayBlockingQueue<>(10_000);
    private ElasticProfilerThread profilerThread;

    public ElasticProfilerExtension(ExtensionSettings settings, ElasticExtension elasticExtension) {
        super(settings);
        this.elasticExtension = elasticExtension;
    }

    public ArrayBlockingQueue<AbstractProfiler> getQueue() {
        return queue;
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (this.getSettings().isEnabled() && elasticExtension.extensionSettings().isEnabled()) {
            profilerThread = new ElasticProfilerThread("ELASTIC_PROFILER", elasticExtension, this);
            profilerThread.start();
        }
    }

    @Override
    public void onServerStop() {
        if (this.getSettings().isEnabled() && elasticExtension.extensionSettings().isEnabled()) {
            try {
                profilerThread.stop_();
                profilerThread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onTick(MinecraftServer server) {
        if (!this.extensionSettings().isEnabled()) {
            return;
        }
        if (this.extensionSettings().isEnabled() && !elasticExtension.extensionSettings().isEnabled()) {
            this.extensionSettings().setEnabled(false);
            ExtensionManager.saveSettings();
            onExtensionDisabled();
            return;
        }
        KahzerxServer.profilers.forEach(this::enqueue);
    }

    @Override
    public void onExtensionEnabled() {
        queue.clear();
        if (profilerThread != null && profilerThread.isAlive()) {
            return;
        }
        profilerThread = new ElasticProfilerThread("ELASTIC_PROFILER", elasticExtension, this);
        profilerThread.start();
    }

    @Override
    public void onExtensionDisabled() {
        queue.clear();
        if (profilerThread == null || !profilerThread.isAlive()) {
            return;
        }
        profilerThread.stop_();
    }

    private void enqueue(AbstractProfiler profiler) {
        if (queue.remainingCapacity() > 0) {
            try {
                queue.put(profiler);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }
}
