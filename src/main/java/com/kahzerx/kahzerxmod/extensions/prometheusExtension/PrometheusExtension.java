package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.Extensions;
import com.kahzerx.kahzerxmod.extensions.GenericExtension;
import io.prometheus.client.exporter.HTTPServer;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;

public class PrometheusExtension extends GenericExtension implements Extensions {
    private HTTPServer httpServer;
    private static final Logger LOGGER = LogManager.getLogger();
    private MinecraftServer server = null;
    private final MetricReg metricReg = new MetricReg(this);

    public PrometheusExtension(PrometheusSettings settings) {
        super(settings);
    }

    @Override
    public PrometheusSettings extensionSettings() {
        return (PrometheusSettings) this.getSettings();
    }

    @Override
    public void onServerRun(MinecraftServer minecraftServer) {
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
        }
    }

    private void stopPrometheusEndpoint() {
        if (this.httpServer != null) {
            this.httpServer.close();
        }
        this.metricReg.getTimer().cancel();
    }

    public MinecraftServer getServer() {
        return server;
    }
}
