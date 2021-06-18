/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Recorder;

public class DummyRecorder
implements Recorder {
    public static final Recorder INSTANCE = new DummyRecorder();

    @Override
    public void stop() {
    }

    @Override
    public void startTick() {
    }

    @Override
    public boolean isActive() {
        return false;
    }

    @Override
    public Profiler getProfiler() {
        return DummyProfiler.INSTANCE;
    }

    @Override
    public void endTick() {
    }
}

