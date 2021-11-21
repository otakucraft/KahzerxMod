package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.MathHelper;

public class MSPTMetric extends AbstractMetric {
    public MSPTMetric(String name, String help, String... labels) {
        super(name, help, labels);
    }

    @Override
    public void update(MinecraftServer server) {
        double MSPT = MathHelper.average(server.lastTickLengths) * 1.0E-6D;
        this.getGauge().set(MSPT);
    }
}
