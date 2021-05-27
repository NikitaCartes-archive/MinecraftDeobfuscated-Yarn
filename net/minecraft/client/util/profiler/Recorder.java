/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.profiler;

import net.minecraft.util.profiler.Profiler;

public interface Recorder {
    public void sample();

    public void start();

    public boolean isActive();

    public Profiler getProfiler();

    public void read();
}

