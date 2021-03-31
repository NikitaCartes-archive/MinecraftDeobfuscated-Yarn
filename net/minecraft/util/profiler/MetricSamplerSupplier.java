/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.List;
import net.minecraft.client.util.profiler.MetricSampler;

public interface MetricSamplerSupplier {
    public List<MetricSampler> getSamplers();
}

