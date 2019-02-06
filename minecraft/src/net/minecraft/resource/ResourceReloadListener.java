package net.minecraft.resource;

import java.util.concurrent.CompletableFuture;
import net.minecraft.util.profiler.Profiler;

public interface ResourceReloadListener<T> {
	CompletableFuture<T> prepare(ResourceManager resourceManager, Profiler profiler);

	void apply(ResourceManager resourceManager, T object, Profiler profiler);
}
