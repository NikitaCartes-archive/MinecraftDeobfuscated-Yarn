package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.profiler.Profiler;

public interface SynchronousResourceReloadListener extends ResourceReloadListener<Void> {
	@Override
	default CompletableFuture<Void> prepare(ResourceManager resourceManager, Profiler profiler) {
		return CompletableFuture.completedFuture(null);
	}

	default void method_18235(ResourceManager resourceManager, Void void_, Profiler profiler) {
		this.reloadResources(resourceManager);
	}

	void reloadResources(ResourceManager resourceManager);
}
