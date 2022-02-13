package com.kahzerx.kahzerxmod.extensions.elasticProfilerExtension;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.kahzerx.kahzerxmod.extensions.elasticExtension.ElasticExtension;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;

import java.util.*;

public class ElasticProfilerThread extends Thread {
    private final ElasticExtension elasticExtension;
    private final ElasticProfilerExtension elasticProfilerExtension;
    private boolean running = true;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    public ElasticProfilerThread(String name, ElasticExtension elasticExtension, ElasticProfilerExtension elasticProfilerExtension) {
        super(name);
        this.elasticExtension = elasticExtension;
        this.elasticProfilerExtension = elasticProfilerExtension;
    }

    @Override
    public void run() {
        List<IndexRequest> post = new ArrayList<>();
        while (this.running) {
            if (this.elasticProfilerExtension.queue.isEmpty()) {
                continue;
            }
            try {
                ProfilerResult result = this.elasticProfilerExtension.queue.take();
                Map<String, Object> map = gson.fromJson(gson.toJson(result.data(), result.data().getClass()), new TypeToken<HashMap<String, Object>>() {}.getType());
                map.put("name", result.name());
                map.put("created", result.id());
                IndexRequest indexRequest = new IndexRequest(
                        result.data().getClass().getSimpleName().replace("Instance", "").toLowerCase(Locale.ROOT) + "-alias"
                ).source(map);
                post.add(indexRequest);
                if (post.size() > 3_000) {
                    BulkRequest bulkRequest = new BulkRequest();
                    for (IndexRequest i : post) {
                        bulkRequest.add(i);
                    }
                    elasticExtension.getClient().bulk(bulkRequest, RequestOptions.DEFAULT);
                    post.clear();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop_() {
        this.running = false;
    }
}
