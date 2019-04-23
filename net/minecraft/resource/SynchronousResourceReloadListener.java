/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public interface SynchronousResourceReloadListener
extends ResourceReloadListener {
    @Override
    default public CompletableFuture<Void> reload(ResourceReloadListener.Synchronizer synchronizer, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2) {
        return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> this.apply(resourceManager), executor2);
    }

    public void apply(ResourceManager var1);
}

