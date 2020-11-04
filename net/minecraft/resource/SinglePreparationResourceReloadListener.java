/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

public abstract class SinglePreparationResourceReloadListener<T>
implements ResourceReloadListener {
    @Override
    public final CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager manager, Profiler prepareProfiler, Profiler applyProfiler, Executor prepareExecutor, Executor applyExecutor) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> this.prepare(manager, prepareProfiler), prepareExecutor).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(object -> this.apply(object, manager, applyProfiler), applyExecutor);
    }

    protected abstract T prepare(ResourceManager var1, Profiler var2);

    protected abstract void apply(T var1, ResourceManager var2, Profiler var3);
}

