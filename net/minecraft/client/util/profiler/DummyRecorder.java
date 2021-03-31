/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.profiler;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.profiler.Recorder;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public class DummyRecorder
implements Recorder {
    public static final Recorder INSTANCE = new DummyRecorder();

    @Override
    public void sample() {
    }

    @Override
    public void start() {
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
    public void read() {
    }
}

