package net.minecraft.resource;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Void;

public interface ReloadableResourceManager extends ResourceManager {
	CompletableFuture<Void> reload(Executor executor, Executor executor2, List<ResourcePack> list, CompletableFuture<Void> completableFuture);

	@Environment(EnvType.CLIENT)
	ResourceReloadHandler createReloadHandler(Executor executor, Executor executor2, CompletableFuture<Void> completableFuture);

	void registerListener(ResourceReloadListener resourceReloadListener);
}
