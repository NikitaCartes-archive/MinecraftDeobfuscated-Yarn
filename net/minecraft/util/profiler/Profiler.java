/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.SampleType;

public interface Profiler {
    public static final String ROOT_NAME = "root";

    public void startTick();

    public void endTick();

    public void push(String var1);

    public void push(Supplier<String> var1);

    public void pop();

    public void swap(String var1);

    public void swap(Supplier<String> var1);

    public void markSampleType(SampleType var1);

    default public void visit(String marker) {
        this.visit(marker, 1);
    }

    /**
     * Increment the visit count for a marker.
     * 
     * <p>This is useful to keep track of number of calls made to performance-
     * wise expensive methods.
     * 
     * @param marker a unique marker
     */
    public void visit(String var1, int var2);

    default public void visit(Supplier<String> markerGetter) {
        this.visit(markerGetter, 1);
    }

    /**
     * Increment the visit count for a marker.
     * 
     * <p>This is useful to keep track of number of calls made to performance-
     * wise expensive methods.
     * 
     * <p>This method is preferred if getting the marker is costly; the
     * supplier won't be called if the profiler is disabled.
     * 
     * @param markerGetter the getter for a unique marker
     */
    public void visit(Supplier<String> var1, int var2);

    public static Profiler union(final Profiler a, final Profiler b) {
        if (a == DummyProfiler.INSTANCE) {
            return b;
        }
        if (b == DummyProfiler.INSTANCE) {
            return a;
        }
        return new Profiler(){

            @Override
            public void startTick() {
                a.startTick();
                b.startTick();
            }

            @Override
            public void endTick() {
                a.endTick();
                b.endTick();
            }

            @Override
            public void push(String location) {
                a.push(location);
                b.push(location);
            }

            @Override
            public void push(Supplier<String> locationGetter) {
                a.push(locationGetter);
                b.push(locationGetter);
            }

            @Override
            public void markSampleType(SampleType type) {
                a.markSampleType(type);
                b.markSampleType(type);
            }

            @Override
            public void pop() {
                a.pop();
                b.pop();
            }

            @Override
            public void swap(String location) {
                a.swap(location);
                b.swap(location);
            }

            @Override
            public void swap(Supplier<String> locationGetter) {
                a.swap(locationGetter);
                b.swap(locationGetter);
            }

            @Override
            public void visit(String marker, int i) {
                a.visit(marker, i);
                b.visit(marker, i);
            }

            @Override
            public void visit(Supplier<String> markerGetter, int i) {
                a.visit(markerGetter, i);
                b.visit(markerGetter, i);
            }
        };
    }
}

