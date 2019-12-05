/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourcePack;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.resource.ResourceReloadMonitor;
import net.minecraft.util.Unit;

public interface ReloadableResourceManager
extends ResourceManager {
    public CompletableFuture<Unit> beginReload(Executor var1, Executor var2, List<ResourcePack> var3, CompletableFuture<Unit> var4);

    @Environment(value=EnvType.CLIENT)
    public ResourceReloadMonitor beginMonitoredReload(Executor var1, Executor var2, CompletableFuture<Unit> var3, List<ResourcePack> var4);

    public void registerListener(ResourceReloadListener var1);
}

