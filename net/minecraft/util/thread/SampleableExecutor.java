/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.thread;

import java.util.List;
import net.minecraft.util.profiler.Sampler;

public interface SampleableExecutor {
    public List<Sampler> createSamplers();
}

