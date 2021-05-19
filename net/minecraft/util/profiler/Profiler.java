/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.minecraft.util.profiler.DummyProfiler;

public interface Profiler {
    public static final String ROOT_NAME = "root";

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
            public void visit(String marker) {
                a.visit(marker);
                b.visit(marker);
            }

            @Override
            public void visit(Supplier<String> markerGetter) {
                a.visit(markerGetter);
                b.visit(markerGetter);
            }
        };
    }
}

