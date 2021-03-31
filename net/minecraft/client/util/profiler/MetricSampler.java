/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.profiler;

import java.util.function.DoubleSupplier;
import net.minecraft.client.util.profiler.Metric;
import net.minecraft.client.util.profiler.SamplingChannel;

public class MetricSampler {
    private final Metric metric;
    private final DoubleSupplier valueSupplier;
    private final SamplingChannel channel;

    public MetricSampler(Metric metric, DoubleSupplier valueSupplier, SamplingChannel channel) {
        this.metric = metric;
        this.valueSupplier = valueSupplier;
        this.channel = channel;
    }

    public Metric getMetric() {
        return this.metric;
    }

    public DoubleSupplier getValueSupplier() {
        return this.valueSupplier;
    }

    public SamplingChannel getChannel() {
        return this.channel;
    }
}

