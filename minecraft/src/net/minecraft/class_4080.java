package net.minecraft;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceReloadListener;
import net.minecraft.util.profiler.Profiler;

@Environment(EnvType.CLIENT)
public abstract class class_4080<T> implements ResourceReloadListener {
	@Override
	public final CompletableFuture<Void> apply(
		ResourceReloadListener.Helper helper, ResourceManager resourceManager, Profiler profiler, Profiler profiler2, Executor executor, Executor executor2
	) {
		return CompletableFuture.supplyAsync(() -> this.method_18789(resourceManager, profiler), executor)
			.thenCompose(helper::waitForAll)
			.thenAcceptAsync(object -> this.method_18788((T)object, resourceManager, profiler2), executor2);
	}

	protected abstract T method_18789(ResourceManager resourceManager, Profiler profiler);

	protected abstract void method_18788(T object, ResourceManager resourceManager, Profiler profiler);
}
