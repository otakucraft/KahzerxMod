package com.kahzerx.kahzerxmod.extensions.prometheusExtension.metrics;

import com.kahzerx.kahzerxmod.extensions.prometheusExtension.PrometheusExtension;
import net.minecraft.util.math.MathHelper;

public class MSPTMetric extends AbstractMetric {
    public MSPTMetric(String name, String help) {
        super(name, help);
    }

    @Override
    public void update(PrometheusExtension extension) {
        double MSPT = MathHelper.average(extension.getServer().lastTickLengths) * 1.0E-6D;
        this.getGauge().set(MSPT);
    }
}
