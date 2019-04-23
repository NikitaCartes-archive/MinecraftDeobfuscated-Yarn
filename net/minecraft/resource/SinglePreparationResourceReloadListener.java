/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

@Environment(value=EnvType.CLIENT)
public abstract class SinglePreparationResourceReloadListener<T>
implements ResourceReloadListener {
    @Override
    public final CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2) {
        return ((CompletableFuture)CompletableFuture.supplyAsync(() -> this.prepare(resourceManager, profiler), executor).thenCompose(synchronizer::whenPrepared)).thenAcceptAsync(object -> this.apply(object, resourceManager, profiler2), executor2);
    }

    protected abstract T prepare(ResourceManager var1, Profiler var2);

    protected abstract void apply(T var1, ResourceManager var2, Profiler var3);
}

