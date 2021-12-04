package com.kahzerx.kahzerxmod.extensions.prometheusExtension;

import com.kahzerx.kahzerxmod.ExtensionManager;
import com.kahzerx.kahzerxmod.Extensions;
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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.concurrent.TimeUnit;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class PrometheusExtension extends GenericExtension implements Extensions {
    private HTTPServer httpServer;
    private static final Logger LOGGER = LogManager.getLogger();
    private MinecraftServer server = null;
    private final MetricReg metricReg = new MetricReg(this);

    private long lastTickTime = 0;
    private static final long SEC_IN_NANO = TimeUnit.SECONDS.toNanos(1);
    private static final int TPS = 20;
    private static final int TPS_SAMPLE_INTERVAL = 20;
    private static final BigDecimal TPS_BASE = new BigDecimal(SEC_IN_NANO).multiply(new BigDecimal(TPS_SAMPLE_INTERVAL));
    private final TpsRollingAverage tps5Sec = new TpsRollingAverage(5);
    private final TpsRollingAverage tps10Sec = new TpsRollingAverage(10);
    private final TpsRollingAverage tps1Min = new TpsRollingAverage(60);
    private final TpsRollingAverage tps5Min = new TpsRollingAverage(60 * 5);
    private final TpsRollingAverage tps15Min = new TpsRollingAverage(60 * 15);
    private final TpsRollingAverage[] tpsAverages = {this.tps5Sec, this.tps10Sec, this.tps1Min, this.tps5Min, this.tps15Min};

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
    public void onTick(MinecraftServer server) {
        int currentTick = server.getTicks();
        if (currentTick % TPS_SAMPLE_INTERVAL != 0) {
            return;
        }

        long now = System.nanoTime();

        if (this.lastTickTime == 0) {
            this.lastTickTime = now;
            return;
        }

        long diff = now - this.lastTickTime;
        BigDecimal currentTps = TPS_BASE.divide(new BigDecimal(diff), 30, RoundingMode.HALF_UP);
        BigDecimal total = currentTps.multiply(new BigDecimal(diff));

        for (TpsRollingAverage rollingAverage : this.tpsAverages) {
            rollingAverage.add(currentTps, diff, total);
        }

        this.lastTickTime = now;
    }

    public double tps5Sec() {
        return this.tps5Sec.getAverage();
    }

    public double tps10Sec() {
        return this.tps10Sec.getAverage();
    }

    public double tps1Min() {
        return this.tps1Min.getAverage();
    }

    public double tps5Min() {
        return this.tps5Min.getAverage();
    }

    public double tps15Min() {
        return this.tps15Min.getAverage();
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

    public MinecraftServer getServer() {
        return server;
    }

    @Override
    public void settingsCommand(LiteralArgumentBuilder<ServerCommandSource> builder) {
        builder.
                then(literal("port").
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

    public static final class TpsRollingAverage {
        private final int size;
        private long time;
        private BigDecimal total;
        private int index = 0;
        private final BigDecimal[] samples;
        private final long[] times;

        private TpsRollingAverage(int size) {
            this.size = size;
            this.time = size * SEC_IN_NANO;
            this.total = new BigDecimal(TPS).multiply(new BigDecimal(SEC_IN_NANO)).multiply(new BigDecimal(size));
            this.samples = new BigDecimal[size];
            this.times = new long[size];
            for (int i = 0; i < size; i++) {
                this.samples[i] = new BigDecimal(TPS);
                this.times[i] = SEC_IN_NANO;
            }
        }

        public void add(BigDecimal x, long t, BigDecimal total) {
            this.time -= this.times[this.index];
            this.total = this.total.subtract(this.samples[this.index].multiply(new BigDecimal(this.times[this.index])));
            this.samples[this.index] = x;
            this.times[this.index] = t;
            this.time += t;
            this.total = this.total.add(total);
            if (++this.index == this.size) {
                this.index = 0;
            }
        }

        public double getAverage() {
            return Math.min(this.total.divide(new BigDecimal(this.time), 1, RoundingMode.HALF_UP).doubleValue(), TPS);
        }
    }
}
