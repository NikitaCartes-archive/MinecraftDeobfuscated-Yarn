/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.profiler;

import java.util.function.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public interface Profiler {
    public void startTick();

    public void endTick();

    public void push(String var1);

    public void push(Supplier<String> var1);

    public void pop();

    public void swap(String var1);

    @Environment(value=EnvType.CLIENT)
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
}

