package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.profiler.Profiler;

public interface ResourceReloadListener {
	CompletableFuture<Void> method_18222(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	);

	public interface Helper {
		<T> CompletableFuture<T> waitForAll(T object);
	}
}
