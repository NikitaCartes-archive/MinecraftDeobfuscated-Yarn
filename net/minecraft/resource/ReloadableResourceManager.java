/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;

public interface ReloadableResourceManager
extends ResourceManager {
    default public CompletableFuture<Unit> beginReload(Executor prepareExecutor, Executor applyExecutor, List<ResourcePack> packs, CompletableFuture<Unit> initialStage) {
        return this.beginMonitoredReload(prepareExecutor, applyExecutor, initialStage, packs).whenComplete();
    }

    public ResourceReloadMonitor beginMonitoredReload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<ResourcePack> var4);

    public void registerListener(ResourceReloadListener var1);
}

