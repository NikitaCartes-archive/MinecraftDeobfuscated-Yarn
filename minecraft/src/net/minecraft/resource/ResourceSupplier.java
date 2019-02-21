package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class ResourceSupplier<T> implements ResourceReloadListener {
	@Override
	public final CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return CompletableFuture.supplyAsync(() -> this.apply(resourceManager, profiler), executor)
			.thenCompose(helper::waitForAll)
			.thenAcceptAsync(object -> this.accept((T)object, resourceManager, profiler2), executor2);
	}

	protected abstract T apply(ResourceManager resourceManager, Profiler profiler);

	protected abstract void accept(T object, ResourceManager resourceManager, Profiler profiler);
}
