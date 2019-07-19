package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.Profiler;

public interface SynchronousResourceReloadListener extends ResourceReloadListener {
	@Override
	default CompletableFuture<Void> reload(
		ResourceReloadListener.Synchronizer synchronizer,
		ResourceManager manager,
		Profiler prepareProfiler,
		Profiler applyProfiler,
		Executor prepareExecutor,
		Executor applyExecutor
	) {
		return synchronizer.whenPrepared(Unit.INSTANCE).thenRunAsync(() -> this.apply(manager), applyExecutor);
	}

	void apply(ResourceManager manager);
}
