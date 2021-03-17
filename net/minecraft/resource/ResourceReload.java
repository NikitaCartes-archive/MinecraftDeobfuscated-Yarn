/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Unit;

/**
 * Represents a resource reload.
 * 
 * @see ReloadableResourceManager#reload(java.util.concurrent.Executor,
 * java.util.concurrent.Executor, CompletableFuture, java.util.List)
 */
public interface ResourceReload {
    /**
     * Returns a future for the reload. The returned future is completed when
     * the reload completes.
     */
    public CompletableFuture<Unit> whenComplete();

    /**
     * Returns a fraction between 0 and 1 indicating the progress of this
     * reload.
     */
    @Environment(value=EnvType.CLIENT)
    public float getProgress();

    /**
     * Returns if this reload has completed, either normally or abnormally.
     */
    @Environment(value=EnvType.CLIENT)
    public boolean isComplete();

    /**
     * Throws an unchecked exception from this reload, if there is any. Does
     * nothing if the reload has not completed or terminated.
     */
    @Environment(value=EnvType.CLIENT)
    public void throwException();
}

