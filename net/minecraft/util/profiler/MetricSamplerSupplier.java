/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.List;
import net.minecraft.client.util.profiler.SamplingRecorder;

public interface MetricSamplerSupplier {
    public List<SamplingRecorder> getSamplers();
}

