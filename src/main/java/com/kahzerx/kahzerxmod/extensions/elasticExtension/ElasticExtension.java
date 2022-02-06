package com.kahzerx.kahzerxmod.extensions.elasticExtension;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.json.jackson.JacksonJsonpMapper;
import co.elastic.clients.transport.ElasticsearchTransport;
import co.elastic.clients.transport.rest_client.RestClientTransport;
import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.kahzerx.kahzerxmod.utils.MarkEnum;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.apache.http.HttpHost;
import org.elasticsearch.client.RestClient;

import java.io.IOException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class ElasticExtension extends GenericExtension implements Extensions {
    private RestClient restClient;
    private ElasticsearchTransport transport;
    private ElasticsearchClient client;
    private boolean isConnected = false;

    public ElasticExtension(ElasticSettings settings) {
        super(settings);
    }

    public boolean isConnected() {
        return isConnected;
    }

    public ElasticsearchClient getClient() {
        return client;
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
        if (extensionSettings().isEnabled()) {
            onExtensionEnabled();
        }
    }

    @Override
    public void onExtensionEnabled() {
        restClient = RestClient.builder(new HttpHost(this.extensionSettings().getHost(), this.extensionSettings().getPort())).build();
        transport = new RestClientTransport(restClient, new JacksonJsonpMapper());
        client = new ElasticsearchClient(transport);
        try {
            client.ping();
            isConnected = true;
        } catch (IOException e) {
            onExtensionDisabled();
        }
    }

    @Override
    public void onExtensionDisabled() {
        restClient = null;
        transport = null;
        client = null;
        isConnected = false;
        extensionSettings().setEnabled(false);
        ExtensionManager.saveSettings();
    }

    @Override
    public ElasticSettings extensionSettings() {
        return (ElasticSettings) this.getSettings();
    }

    @Override
    public void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.
                then(literal("host").
                        then(argument("host", StringArgumentType.string()).
                                executes(context -> {
                                    extensionSettings().setHost(StringArgumentType.getString(context, "host"));
                                    context.getSource().sendFeedback(new LiteralText("[Host] > " + extensionSettings().getHost()), false);
                                    if (extensionSettings().isEnabled()) {
                                        onExtensionDisabled();
                                        context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("Elastic has been disabled"), false);
                                    }
                                    return 1;
                                })).
                        executes(context -> {
                            context.getSource().sendFeedback(new LiteralText("[Host] > " + extensionSettings().getHost()), false);
                            return 1;
                        })).
                then(literal("user").
                        then(argument("user", StringArgumentType.string()).
                                executes(context -> {
                                    extensionSettings().setUser(StringArgumentType.getString(context, "user"));
                                    context.getSource().sendFeedback(new LiteralText("[User] > " + extensionSettings().getHost()), false);
                                    if (extensionSettings().isEnabled()) {
                                        onExtensionDisabled();
                                        context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("Elastic has been disabled"), false);
                                    }
                                    return 1;
                                })).
                        executes(context -> {
                            context.getSource().sendFeedback(new LiteralText("[User] > " + extensionSettings().getHost()), false);
                            return 1;
                        })).
                then(literal("password").
                        then(argument("password", StringArgumentType.string()).
                                executes(context -> {
                                    extensionSettings().setPassword(StringArgumentType.getString(context, "password"));
                                    context.getSource().sendFeedback(new LiteralText("New password set!"), false);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                }))).
                then(literal("port").
                        then(argument("port", IntegerArgumentType.integer(1, 65535)).
                                executes(context -> {
                                    extensionSettings().setPort(IntegerArgumentType.getInteger(context, "port"));
                                    context.getSource().sendFeedback(new LiteralText("[Port] > " + extensionSettings().getPort()), false);
                                    if (extensionSettings().isEnabled()) {
                                        onExtensionDisabled();
                                        context.getSource().sendFeedback(MarkEnum.INFO.appendMessage("Elastic has been disabled"), false);
                                    }
                                    return 1;
                                })).
                        executes(context -> {
                            context.getSource().sendFeedback(new LiteralText("[Port] > " + extensionSettings().getPort()), false);
                            return 1;
                        }));
    }
}