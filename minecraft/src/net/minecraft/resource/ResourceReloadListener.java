package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public interface ResourceReloadListener {
	CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager resourceManager,
		Profiler profiler,
		Profiler profiler2,
		Executor executor,
		Executor executor2
	);

	public interface Synchronizer {
		<T> CompletableFuture<T> whenPrepared(T object);
	}
}
