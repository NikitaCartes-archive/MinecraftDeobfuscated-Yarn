/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.minecraft.util.profiler.DummyProfiler;

public interface Profiler {
    public static final String field_29925 = "root";

    public void startTick();

    public void endTick();

    public void push(String var1);

    public void push(Supplier<String> var1);

    public void pop();

    public void swap(String var1);

    public void swap(Supplier<String> var1);

    /**
     * Increment the visit count for a marker.
     * 
     * <p>This is useful to keep track of number of calls made to performance-
     * wise expensive methods.
     * 
     * @param marker a unique marker
     */
    public void visit(String var1);

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
    public void visit(Supplier<String> var1);

    public static Profiler union(final Profiler profiler, final Profiler profiler2) {
        if (profiler == DummyProfiler.INSTANCE) {
            return profiler2;
        }
        if (profiler2 == DummyProfiler.INSTANCE) {
            return profiler;
        }
        return new Profiler(){

            @Override
            public void startTick() {
                profiler.startTick();
                profiler2.startTick();
            }

            @Override
            public void endTick() {
                profiler.endTick();
                profiler2.endTick();
            }

            @Override
            public void push(String location) {
                profiler.push(location);
                profiler2.push(location);
            }

            @Override
            public void push(Supplier<String> locationGetter) {
                profiler.push(locationGetter);
                profiler2.push(locationGetter);
            }

            @Override
            public void pop() {
                profiler.pop();
                profiler2.pop();
            }

            @Override
            public void swap(String location) {
                profiler.swap(location);
                profiler2.swap(location);
            }

            @Override
            public void swap(Supplier<String> locationGetter) {
                profiler.swap(locationGetter);
                profiler2.swap(locationGetter);
            }

            @Override
            public void visit(String marker) {
                profiler.visit(marker);
                profiler2.visit(marker);
            }

            @Override
            public void visit(Supplier<String> markerGetter) {
                profiler.visit(markerGetter);
                profiler2.visit(markerGetter);
            }
        };
    }
}

