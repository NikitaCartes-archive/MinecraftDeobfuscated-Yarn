/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import com.google.common.collect.ImmutableSet;
import java.util.Set;
import java.util.function.Supplier;
import net.minecraft.client.util.profiler.SamplingChannel;
import net.minecraft.util.profiler.EmptyProfileResult;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.Nullable;

public class DummyProfiler
implements ReadableProfiler {
    public static final DummyProfiler INSTANCE = new DummyProfiler();

    private DummyProfiler() {
    }

    @Override
    public void startTick() {
    }

    @Override
    public void endTick() {
    }

    @Override
    public void push(String location) {
    }

    @Override
    public void push(Supplier<String> locationGetter) {
    }

    @Override
    public void method_37167(SamplingChannel samplingChannel) {
    }

    @Override
    public void pop() {
    }

    @Override
    public void swap(String location) {
    }

    @Override
    public void swap(Supplier<String> locationGetter) {
    }

    @Override
    public void visit(String marker) {
    }

    @Override
    public void visit(Supplier<String> markerGetter) {
    }

    @Override
    public ProfileResult getResult() {
        return EmptyProfileResult.INSTANCE;
    }

    @Override
    @Nullable
    public ProfilerSystem.LocatedInfo getInfo(String name) {
        return null;
    }

    @Override
    public Set<Pair<String, SamplingChannel>> method_37168() {
        return ImmutableSet.of();
    }
}

