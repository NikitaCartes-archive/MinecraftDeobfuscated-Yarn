package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public interface SynchronousResourceReloadListener extends ResourceReloadListener {
	@Override
	default CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager resourceManager,
		Profiler profiler,
		Profiler profiler2,
		Executor executor,
		Executor executor2
	) {
		return synchronizer.whenPrepared(Unit.field_17274).thenRunAsync(() -> this.apply(resourceManager), executor2);
	}

	void apply(ResourceManager resourceManager);
}
