package com.kahzerx.kahzerxmod.profiler;

import com.kahzerx.kahzerxmod.profiler.instances.TPSInstance;
import net.minecraft.server.MinecraftServer;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

public class TPSProfiler extends AbstractProfiler {
    private long lastTickTime = 0;
    private static final long SEC_IN_NANO = TimeUnit.SECONDS.toNanos(1);
    private static final int TPS = 20;
    private static final int TPS_SAMPLE_INTERVAL = 20;
    private static final BigDecimal TPS_BASE = new BigDecimal(SEC_IN_NANO).multiply(new BigDecimal(TPS_SAMPLE_INTERVAL));
    private final TpsRollingAverage tps5Sec = new TpsRollingAverage(20 * 5);
    private final TpsRollingAverage tps10Sec = new TpsRollingAverage(20 * 10);
    private final TpsRollingAverage tps1Min = new TpsRollingAverage(20 * 60);
    private final TpsRollingAverage tps5Min = new TpsRollingAverage(20 * 60 * 5);
    private final TpsRollingAverage tps10Min = new TpsRollingAverage(20 * 60 * 10);
    private final TpsRollingAverage[] tpsAverages = {this.tps5Sec, this.tps10Sec, this.tps1Min, this.tps5Min, this.tps10Min};

    @Override
    public void onTick(MinecraftServer server) {
        HashMap<String, Object> tick = new HashMap<>();
        long now = System.nanoTime();

        if (this.lastTickTime == 0) {
            tick.put("TPS", new TPSInstance(20.0D, 20.0D, 20.0D, 20.0D, 20.0D));
            this.addResult(server.getTicks(), tick);
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

        tick.put("TPS", new TPSInstance(tps5Sec(), tps10Sec(), tps1Min(), tps5Min(), tps10Min()));
        this.addResult(server.getTicks(), tick);
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

    public double tps10Min() {
        return this.tps10Min.getAverage();
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