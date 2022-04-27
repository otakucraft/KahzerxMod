package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.KahzerxServer;
import com.kahzerx.kahzerxmod.extensions.ExtensionSettings;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.prometheus.client.exporter.HTTPServer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PrometheusExtension extends GenericExtension implements Extensions {
    private HTTPServer httpServer;
    private static final Logger LOGGER = LogManager.getLogger();
    private MinecraftServer server = null;
    private final MetricReg metricReg = new MetricReg(KahzerxServer.profilers);

    public PrometheusExtension(ExtensionSettings settings) {
        super(settings);
    }

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public void onServerStarted(MinecraftServer minecraftServer) {
        this.server = minecraftServer;
        this.metricReg.registerMetrics();
        if (this.getSettings().isEnabled()) {
            this.startPrometheusEndpoint();
        }
    }

    @Override
    public void onServerStop() {
        if (this.getSettings().isEnabled()) {
            this.stopPrometheusEndpoint();
        }
    }

    @Override
    public void onExtensionEnabled() {
        Extensions.super.onExtensionEnabled();
        this.startPrometheusEndpoint();
    }

    @Override
    public void onExtensionDisabled() {
        Extensions.super.onExtensionDisabled();
        this.stopPrometheusEndpoint();
    }

    private void startPrometheusEndpoint() {
        int port = extensionSettings().getPort();
        try {
            this.httpServer = new HTTPServer(port);
            LOGGER.info(String.format("Prometheus listener on %d", port));
            this.metricReg.runUpdater();
        } catch (IOException e) {
            e.printStackTrace();
            this.stopPrometheusEndpoint();
            extensionSettings().setEnabled(false);
            ExtensionManager.saveSettings();
        }
    }

    private void stopPrometheusEndpoint() {
        if (this.httpServer != null) {
            this.httpServer.close();
        }
        if (this.metricReg.getTimer() != null) {
            this.metricReg.getTimer().cancel();
        }
    }

    @Override
    public PrometheusSettings extensionSettings() {
        return (PrometheusSettings) this.getSettings();
    }

    @Override
    public void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.
                then(literal("getPort").
                        executes(context -> {
                            context.getSource().sendFeedback(new LiteralText(String.format("Port: %d", extensionSettings().getPort())), false);
                            return 1;
                        })).
                then(literal("setPort").
                        then(argument("port", IntegerArgumentType.integer(1, 65535)).
                                executes(context -> {
                                    int newPort = IntegerArgumentType.getInteger(context, "port");
                                    context.getSource().sendFeedback(new LiteralText(String.format("New port > %d", newPort)), false);
                                    extensionSettings().setPort(newPort);
                                    ExtensionManager.saveSettings();
                                    return 1;
                                })));
    }
}
