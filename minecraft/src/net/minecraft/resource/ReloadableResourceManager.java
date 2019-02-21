package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Void;

public interface ReloadableResourceManager extends ResourceManager {
	CompletableFuture<Void> beginReload(Executor executor, Executor executor2, List<ResourcePack> list, CompletableFuture<Void> completableFuture);

	@Environment(EnvType.CLIENT)
	ResourceReloadMonitor beginInitialMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Void> completableFuture);

	@Environment(EnvType.CLIENT)
	ResourceReloadMonitor beginMonitoredReload(Executor executor, Executor executor2, CompletableFuture<Void> completableFuture, List<ResourcePack> list);

	void registerListener(ResourceReloadListener resourceReloadListener);
}
