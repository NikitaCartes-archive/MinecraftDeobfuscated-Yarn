package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public interface SynchronousResourceReloadListener extends ResourceReloadListener {
	@Override
	default CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return helper.waitForAll(net.minecraft.util.Void.INSTANCE).thenRunAsync(() -> this.apply(resourceManager), executor2);
	}

	void apply(ResourceManager resourceManager);
}
