/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloader;
import net.minecraft.util.profiler.Profiler;

/**
 * A base resource reloader implementation that prepares an object in a
 * single call (as opposed to in multiple concurrent tasks) and handles
 * the prepared object in the apply stage.
 * 
 * @param <T> the intermediate object type
 */
public abstract class SinglePreparationResourceReloader<T>
implements ResourceReloader {
    @Override
    public final CompletableFuture<Void> reload(ResourceReloader.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> this.prepare(manager, prepareProfiler), prepareExecutor).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(object -> this.apply(object, manager, applyProfiler), applyExecutor);
    }

    /**
     * Prepares the intermediate object.
     * 
     * <p>This method is called in the prepare executor in a reload.
     * 
     * @return the prepared object
     * 
     * @param manager the resource manager
     * @param profiler the prepare profiler
     */
    protected abstract T prepare(ResourceManager var1, Profiler var2);

    /**
     * Handles the prepared intermediate object.
     * 
     * <p>This method is called in the apply executor, or the game engine, in a
     * reload.
     * 
     * @param prepared the prepared object
     * @param manager the resource manager
     * @param profiler the apply profiler
     */
    protected abstract void apply(T var1, ResourceManager var2, Profiler var3);
}

