/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import net.minecraft.util.profiler.Profiler;

public interface Recorder {
    public void stop();

    public void forceStop();

    public void startTick();

    public boolean isActive();

    public Profiler getProfiler();

    public void endTick();
}

