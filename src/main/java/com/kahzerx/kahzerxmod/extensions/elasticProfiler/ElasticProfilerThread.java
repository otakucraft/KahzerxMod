package com.kahzerx.kahzerxmod.extensions.elasticProfiler;

import com.kahzerx.kahzerxmod.extensions.elasticExtension.ElasticExtension;
import com.kahzerx.kahzerxmod.profiler.AbstractProfiler;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;

public class ElasticProfilerThread extends Thread {
    private final ElasticExtension elasticExtension;
    private final ElasticProfilerExtension elasticProfilerExtension;
    private boolean running = true;

    public ElasticProfilerThread(String name, ElasticExtension elasticExtension, ElasticProfilerExtension elasticProfilerExtension) {
        super(name);
        this.elasticExtension = elasticExtension;
        this.elasticProfilerExtension = elasticProfilerExtension;
    }

    @Override
    public void run() {
        while (this.running) {
            if (this.elasticProfilerExtension.getQueue().isEmpty()) {
                continue;
            }
            try {
                AbstractProfiler profiler = this.elasticProfilerExtension.getQueue().take();
                System.out.println(profiler);
                ProfilerResult res = profiler.getResults();
                System.out.println(res);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void stop_() {
        this.running = false;
    }
}
