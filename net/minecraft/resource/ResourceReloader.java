/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.profiler.Profiler;

/**
 * A resource reloader performs actual reloading in its {@linkplain #reload
 * reload} in a reloadable resource manager it is registered to.
 * 
 * @see ReloadableResourceManager
 * @see SinglePreparationResourceReloader SinglePreparationResourceReloader
 * (completes preparation in one method)
 * @see SynchronousResourceReloader SynchronousResourceReloader
 * (performs all reloading in the apply executor)
 */
public interface ResourceReloader {
    /**
     * Performs a reload. Returns a future that is completed when the reload
     * is completed.
     * 
     * <p>In a reload, there is a prepare stage and an apply stage. For the
     * prepare stage, you should create completable futures with {@linkplain
     * CompletableFuture#supplyAsync(Supplier, Executor)
     * CompletableFuture.supplyAsync(..., prepareExecutor)}
     * to ensure the prepare actions are done with the prepare executor. Then,
     * you should have a completable future for all the prepared actions, and
     * call {@linkplain CompletableFuture#thenCompose(Function)
     * combinedPrepare.thenCompose(synchronizer::waitFor)}
     * to notify the {@code synchronizer}. Finally, you should run {@linkplain
     * CompletableFuture#thenAcceptAsync(Consumer, Executor)
     * CompletableFuture.thenAcceptAsync(..., applyExecutor)} for apply actions.
     * In the end, returns the result of {@code thenAcceptAsync}.
     * 
     * @return a future for the reload
     * @see ReloadableResourceManager#reload(Executor, Executor,
     * CompletableFuture, List)
     * 
     * @param prepareProfiler the profiler for prepare stage
     * @param applyProfiler the profiler for apply stage
     * @param prepareExecutor the executor for prepare stage
     * @param applyExecutor the executor for apply stage
     * @param synchronizer the synchronizer
     * @param manager the resource manager
     */
    public CompletableFuture<Void> reload(Synchronizer var1, ResourceManager var2, Profiler var3, Profiler var4, Executor var5, Executor var6);

    /**
     * Returns a user-friendly name for logging.
     */
    default public String getName() {
        return this.getClass().getSimpleName();
    }

    public static interface Synchronizer {
        public <T> CompletableFuture<T> whenPrepared(T var1);
    }
}

