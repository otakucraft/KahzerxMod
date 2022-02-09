package com.kahzerx.kahzerxmod.extensions.elasticProfilerExtension;

import co.elastic.clients.elasticsearch._types.Time;
import co.elastic.clients.elasticsearch.indices.RolloverRequest;
import co.elastic.clients.json.JsonData;
import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.KahzerxServer;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.extensions.elasticExtension.ElasticExtension;
import com.kahzerx.kahzerxmod.profiler.AbstractProfiler;
import com.kahzerx.kahzerxmod.profiler.instances.ProfilerResult;
import net.minecraft.server.MinecraftServer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

public class ElasticProfilerExtension extends GenericExtension implements Extensions {
    private final ElasticExtension elasticExtension;
    public final ArrayBlockingQueue<ProfilerResult> queue = new ArrayBlockingQueue<>(10_000);
    private ElasticProfilerThread profilerThread;

    public ElasticProfilerExtension(ExtensionSettings settings, ElasticExtension elasticExtension) {
        super(settings);
        this.elasticExtension = elasticExtension;
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (this.getSettings().isEnabled() && elasticExtension.extensionSettings().isEnabled()) {
            createMappings();
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
        if (server.getTicks() % 20 != 0) {
            return;
        }
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
        createMappings();
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
        try {
            if (queue.remainingCapacity() > 0) {
                if (profiler.getResults().size() > 0) {
                    ProfilerResult res = profiler.getResult();
                    if (res.data().getClass() == ArrayList.class) {
                        for (Object r : (ArrayList) res.data()) {
                            queue.put(new ProfilerResult(res.name(), res.id(), r));
                        }
                    } else {
                        queue.put(new ProfilerResult(res.name(), res.id(), res.data()));
                    }
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private void createMappings() {
        try {
            elasticExtension.getClient().ingest().putPipeline(pipeline -> pipeline.
                    id("ingest-time").
                    description("current-time").
                    processors(processors -> processors.
                            set(processor -> processor.
                                    field("timestamp").
                                    value(JsonData.of("{{_ingest.timestamp}}")))));

            elasticExtension.getClient().ilm().putLifecycle(lifecycleRequest -> lifecycleRequest.
                    name("profiler-policy").
                    policy(policy -> policy.
                            phases(phases -> phases.
                                    hot(phase -> phase.actions(JsonData.of(Map.of("rollover", Map.of("max_primary_shard_size", "5gb"))))).
                                    delete(phase -> phase.minAge(Time.of(time -> time.time("30d"))).actions(JsonData.of(Map.of("delete", Map.of())))))));

            elasticExtension.getClient().indices().create(create -> create.
                    index("chunk1").
                    aliases("chunk1", al -> al.filter(q -> q.term(t -> t.field("name").value(v -> v.stringValue("loaded_chunks"))))).
                    settings(settings -> settings.
                            numberOfShards("1").
                            numberOfReplicas("0").
                            lifecycleName("profiler-policy").
                            defaultPipeline("ingest-time")).
                    mappings(mapping -> mapping.
                            properties("created", p -> p.date(dateProperty -> dateProperty.format("yyyy-MM-dd HH:mm:ss.SSSSSS"))).
                            properties("dimension", p -> p.keyword(key -> key)).
                            properties("name", p -> p.keyword(key -> key)).
                            properties("posX", p -> p.float_(fl -> fl)).
                            properties("posZ", p -> p.float_(fl -> fl))));
//
//            PutIndexTemplateRequest chunkTemplate = new PutIndexTemplateRequest("chunk1");
//            chunkTemplate.patterns(List.of("chunk-*"));
//            chunkTemplate.alias(new Alias("chunk1").filter(QueryBuilders.termQuery("name", "loaded_chunks")));
//            elasticExtension.getClient().indices().putTemplate(chunkTemplate, RequestOptions.DEFAULT);
//            CreateIndexRequest createChunkIndex = new CreateIndexRequest("chunk-000001");
//            createChunkIndex.alias(new Alias("chunk-alias").writeIndex(true));
//            try {
//                elasticExtension.getClient().indices().create(createChunkIndex, RequestOptions.DEFAULT);
//            } catch (ElasticsearchStatusException ignored) { }
//
//            PutIndexTemplateRequest blockEntityTemplate = new PutIndexTemplateRequest("blockentity1");
//            blockEntityTemplate.patterns(List.of("blockentity-*"));
//            blockEntityTemplate.settings(Settings.builder().
//                    put("index.number_of_shards", 1).
//                    put("index.number_of_replicas", 0).
//                    put("index.lifecycle.name", "profiler-policy").
//                    put("index.lifecycle.rollover_alias", "blockentity-alias").
//                    put("index.default_pipeline", "add-current-time").
//                    put("index.max_inner_result_window", 100000).
//                    put("index.mapping.total_fields.limit", 100000).
//                    put("index.mapping.nested_objects.limit", 100000));
//            blockEntityTemplate.mapping("""
//                {
//                    "properties": {
//                        "created": {
//                            "type": "date",
//                            "format": "yyyy-MM-dd HH:mm:ss.SSSSSS"
//                        },
//                        "name": {
//                            "type": "keyword"
//                        },
//                        "blockEntityName": {
//                            "type": "keyword"
//                        },
//                        "dimension": {
//                            "type": "keyword"
//                        },
//                        "posX": {
//                            "type": "float"
//                        },
//                        "posY": {
//                            "type": "float"
//                        },
//                        "posZ": {
//                            "type": "float"
//                        }
//                    }
//                }
//                """, XContentType.JSON);
//            blockEntityTemplate.alias(new Alias("blockentity1").filter(QueryBuilders.termQuery("name", "block_entities")));
//            elasticExtension.getClient().indices().putTemplate(blockEntityTemplate, RequestOptions.DEFAULT);
//            CreateIndexRequest createBlockEntityIndex = new CreateIndexRequest("blockentity-000001");
//            createBlockEntityIndex.alias(new Alias("blockentity-alias").writeIndex(true));
//            try {
//                elasticExtension.getClient().indices().create(createBlockEntityIndex, RequestOptions.DEFAULT);
//            } catch (ElasticsearchStatusException ignored) { }
//
//            PutIndexTemplateRequest entityTemplate = new PutIndexTemplateRequest("entity1");
//            entityTemplate.patterns(List.of("entity-*"));
//            entityTemplate.settings(Settings.builder().
//                    put("index.number_of_shards", 1).
//                    put("index.number_of_replicas", 0).
//                    put("index.lifecycle.name", "profiler-policy").
//                    put("index.lifecycle.rollover_alias", "entity-alias").
//                    put("index.default_pipeline", "add-current-time").
//                    put("index.max_inner_result_window", 100000).
//                    put("index.mapping.total_fields.limit", 100000).
//                    put("index.mapping.nested_objects.limit", 100000));
//            entityTemplate.mapping("""
//                {
//                    "properties": {
//                        "created": {
//                            "type": "date",
//                            "format": "yyyy-MM-dd HH:mm:ss.SSSSSS"
//                        },
//                        "name": {
//                            "type": "keyword"
//                        },
//                        "entityName": {
//                            "type": "keyword"
//                        },
//                        "dimension": {
//                            "type": "keyword"
//                        },
//                        "posX": {
//                            "type": "float"
//                        },
//                        "posY": {
//                            "type": "float"
//                        },
//                        "posZ": {
//                            "type": "float"
//                        }
//                    }
//                }
//                """, XContentType.JSON);
//            entityTemplate.alias(new Alias("entity1").filter(QueryBuilders.termQuery("name", "entities")));
//            elasticExtension.getClient().indices().putTemplate(entityTemplate, RequestOptions.DEFAULT);
//            CreateIndexRequest createEntityIndex = new CreateIndexRequest("entity-000001");
//            createEntityIndex.alias(new Alias("entity-alias").writeIndex(true));
//            try {
//                elasticExtension.getClient().indices().create(createEntityIndex, RequestOptions.DEFAULT);
//            } catch (ElasticsearchStatusException ignored) { }
//
//            PutIndexTemplateRequest msptTemplate = new PutIndexTemplateRequest("mspt1");
//            msptTemplate.patterns(List.of("mspt-*"));
//            msptTemplate.settings(Settings.builder().
//                    put("index.number_of_shards", 1).
//                    put("index.number_of_replicas", 0).
//                    put("index.lifecycle.name", "profiler-policy").
//                    put("index.lifecycle.rollover_alias", "mspt-alias").
//                    put("index.default_pipeline", "add-current-time").
//                    put("index.max_inner_result_window", 100000).
//                    put("index.mapping.total_fields.limit", 100000).
//                    put("index.mapping.nested_objects.limit", 100000));
//            msptTemplate.mapping("""
//                {
//                    "properties": {
//                        "created": {
//                            "type": "date",
//                            "format": "yyyy-MM-dd HH:mm:ss.SSSSSS"
//                        },
//                        "name": {
//                            "type": "keyword"
//                        },
//                        "ms": {
//                            "type": "float"
//                        }
//                    }
//                }
//                """, XContentType.JSON);
//            msptTemplate.alias(new Alias("mspt1").filter(QueryBuilders.termQuery("name", "mspt")));
//            elasticExtension.getClient().indices().putTemplate(msptTemplate, RequestOptions.DEFAULT);
//            CreateIndexRequest createMsptIndex = new CreateIndexRequest("mspt-000001");
//            createMsptIndex.alias(new Alias("mspt-alias").writeIndex(true));
//            try {
//                elasticExtension.getClient().indices().create(createMsptIndex, RequestOptions.DEFAULT);
//            } catch (ElasticsearchStatusException ignored) { }
//
//            PutIndexTemplateRequest playersTemplate = new PutIndexTemplateRequest("players1");
//            playersTemplate.patterns(List.of("players-*"));
//            playersTemplate.settings(Settings.builder().
//                    put("index.number_of_shards", 1).
//                    put("index.number_of_replicas", 0).
//                    put("index.lifecycle.name", "profiler-policy").
//                    put("index.lifecycle.rollover_alias", "players-alias").
//                    put("index.default_pipeline", "add-current-time").
//                    put("index.max_inner_result_window", 100000).
//                    put("index.mapping.total_fields.limit", 100000).
//                    put("index.mapping.nested_objects.limit", 100000));
//            playersTemplate.mapping("""
//                {
//                    "properties": {
//                        "created": {
//                            "type": "date",
//                            "format": "yyyy-MM-dd HH:mm:ss.SSSSSS"
//                        },
//                        "name": {
//                            "type": "keyword"
//                        },
//                        "playerName": {
//                            "type": "keyword"
//                        },
//                        "uuid": {
//                            "type": "keyword"
//                        },
//                        "dimension": {
//                            "type": "keyword"
//                        },
//                        "posX": {
//                            "type": "float"
//                        },
//                        "posY": {
//                            "type": "float"
//                        },
//                        "posZ": {
//                            "type": "float"
//                        }
//                    }
//                }
//                """, XContentType.JSON);
//            playersTemplate.alias(new Alias("players1").filter(QueryBuilders.termQuery("name", "online_players")));
//            elasticExtension.getClient().indices().putTemplate(playersTemplate, RequestOptions.DEFAULT);
//            CreateIndexRequest createPlayersIndex = new CreateIndexRequest("players-000001");
//            createPlayersIndex.alias(new Alias("players-alias").writeIndex(true));
//            try {
//                elasticExtension.getClient().indices().create(createPlayersIndex, RequestOptions.DEFAULT);
//            } catch (ElasticsearchStatusException ignored) { }
//
//            PutIndexTemplateRequest ramTemplate = new PutIndexTemplateRequest("ram1");
//            ramTemplate.patterns(List.of("ram-*"));
//            ramTemplate.settings(Settings.builder().
//                    put("index.number_of_shards", 1).
//                    put("index.number_of_replicas", 0).
//                    put("index.lifecycle.name", "profiler-policy").
//                    put("index.lifecycle.rollover_alias", "ram-alias").
//                    put("index.default_pipeline", "add-current-time").
//                    put("index.max_inner_result_window", 100000).
//                    put("index.mapping.total_fields.limit", 100000).
//                    put("index.mapping.nested_objects.limit", 100000));
//            ramTemplate.mapping("""
//                {
//                    "properties": {
//                        "created": {
//                            "type": "date",
//                            "format": "yyyy-MM-dd HH:mm:ss.SSSSSS"
//                        },
//                        "name": {
//                            "type": "keyword"
//                        },
//                        "max": {
//                            "type": "float"
//                        },
//                        "allocated": {
//                            "type": "float"
//                        },
//                        "free": {
//                            "type": "float"
//                        }
//                    }
//                }
//                """, XContentType.JSON);
//            ramTemplate.alias(new Alias("ram1").filter(QueryBuilders.termQuery("name", "ram")));
//            elasticExtension.getClient().indices().putTemplate(ramTemplate, RequestOptions.DEFAULT);
//            CreateIndexRequest createRamIndex = new CreateIndexRequest("ram-000001");
//            createRamIndex.alias(new Alias("ram-alias").writeIndex(true));
//            try {
//                elasticExtension.getClient().indices().create(createRamIndex, RequestOptions.DEFAULT);
//            } catch (ElasticsearchStatusException ignored) { }
//
//            PutIndexTemplateRequest tpsTemplate = new PutIndexTemplateRequest("tps1");
//            tpsTemplate.patterns(List.of("tps-*"));
//            tpsTemplate.settings(Settings.builder().
//                    put("index.number_of_shards", 1).
//                    put("index.number_of_replicas", 0).
//                    put("index.lifecycle.name", "profiler-policy").
//                    put("index.lifecycle.rollover_alias", "tps-alias").
//                    put("index.default_pipeline", "add-current-time").
//                    put("index.max_inner_result_window", 100000).
//                    put("index.mapping.total_fields.limit", 100000).
//                    put("index.mapping.nested_objects.limit", 100000));
//            tpsTemplate.mapping("""
//                {
//                    "properties": {
//                        "created": {
//                            "type": "date",
//                            "format": "yyyy-MM-dd HH:mm:ss.SSSSSS"
//                        },
//                        "name": {
//                            "type": "keyword"
//                        },
//                        "tps5sec": {
//                            "type": "float"
//                        },
//                        "tps10sec": {
//                            "type": "float"
//                        },
//                        "tps1min": {
//                            "type": "float"
//                        },
//                        "tps5min": {
//                            "type": "float"
//                        },
//                        "tps10min": {
//                            "type": "float"
//                        }
//                    }
//                }
//                """, XContentType.JSON);
//            tpsTemplate.alias(new Alias("tps1").filter(QueryBuilders.termQuery("name", "tps")));
//            elasticExtension.getClient().indices().putTemplate(tpsTemplate, RequestOptions.DEFAULT);
//            CreateIndexRequest createTpsIndex = new CreateIndexRequest("tps-000001");
//            createTpsIndex.alias(new Alias("tps-alias").writeIndex(true));
//            try {
//                elasticExtension.getClient().indices().create(createTpsIndex, RequestOptions.DEFAULT);
//            } catch (ElasticsearchStatusException ignored) { }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public ExtensionSettings extensionSettings() {
        return this.getSettings();
    }
}
