package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public abstract class SinglePreparationResourceReloadListener<T> implements ResourceReloadListener {
	@Override
	public final CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager resourceManager,
		Profiler profiler,
		Profiler profiler2,
		Executor executor,
		Executor executor2
	) {
		return CompletableFuture.supplyAsync(() -> this.prepare(resourceManager, profiler), executor)
			.thenCompose(synchronizer::whenPrepared)
			.thenAcceptAsync(object -> this.apply((T)object, resourceManager, profiler2), executor2);
	}

	protected abstract T prepare(ResourceManager resourceManager, Profiler profiler);

	protected abstract void apply(T object, ResourceManager resourceManager, Profiler profiler);
}
