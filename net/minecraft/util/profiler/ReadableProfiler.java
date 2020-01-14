/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;

public interface ReadableProfiler
extends Profiler {
    public ProfileResult getResults();
}

