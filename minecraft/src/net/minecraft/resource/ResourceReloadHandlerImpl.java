package net.minecraft.resource;

import com.google.common.collect.Sets;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Void;
import net.minecraft.util.profiler.DummyProfiler;

public class ResourceReloadHandlerImpl<S> implements ResourceReloadHandler {
	protected final ResourceManager resourceManager;
	protected final CompletableFuture<Void> allLoadedFuture = new CompletableFuture();
	protected final CompletableFuture<List<S>> completionFuture;
	private final Set<ResourceReloadListener> loadingLoaders;
	private final int listenerCount;
	private int field_18046;
	private int field_18047;
	private final AtomicInteger field_18048 = new AtomicInteger();
	private final AtomicInteger field_18049 = new AtomicInteger();

	public static ResourceReloadHandlerImpl<java.lang.Void> create(
		ResourceManager resourceManager, List<ResourceReloadListener> list, Executor executor, Executor executor2, CompletableFuture<Void> completableFuture
	) {
		return new ResourceReloadHandlerImpl<>(
			executor,
			executor2,
			resourceManager,
			list,
			(helper, resourceManagerx, resourceReloadListener, executor2x, executor3) -> resourceReloadListener.apply(
					helper, resourceManagerx, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, executor, executor3
				),
			completableFuture
		);
	}

	protected ResourceReloadHandlerImpl(
		Executor executor,
		Executor executor2,
		ResourceManager resourceManager,
		List<ResourceReloadListener> list,
		ResourceReloadHandlerImpl.class_4047<S> arg,
		CompletableFuture<Void> completableFuture
	) {
		this.resourceManager = resourceManager;
		this.listenerCount = list.size();
		this.field_18048.incrementAndGet();
		completableFuture.thenRun(this.field_18049::incrementAndGet);
		List<CompletableFuture<S>> list2 = new ArrayList();
		CompletableFuture<?> completableFuture2 = completableFuture;
		this.loadingLoaders = Sets.<ResourceReloadListener>newHashSet(list);

		for (final ResourceReloadListener resourceReloadListener : list) {
			final CompletableFuture<?> completableFuture3 = completableFuture2;
			CompletableFuture<S> completableFuture4 = arg.create(new ResourceReloadListener.Helper() {
				@Override
				public <T> CompletableFuture<T> waitForAll(T object) {
					executor2.execute(() -> {
						ResourceReloadHandlerImpl.this.loadingLoaders.remove(resourceReloadListener);
						if (ResourceReloadHandlerImpl.this.loadingLoaders.isEmpty()) {
							ResourceReloadHandlerImpl.this.allLoadedFuture.complete(Void.INSTANCE);
						}
					});
					return ResourceReloadHandlerImpl.this.allLoadedFuture.thenCombine(completableFuture3, (void_, object2) -> object);
				}
			}, resourceManager, resourceReloadListener, runnable -> {
				this.field_18048.incrementAndGet();
				executor.execute(() -> {
					runnable.run();
					this.field_18049.incrementAndGet();
				});
			}, runnable -> {
				this.field_18046++;
				executor2.execute(() -> {
					runnable.run();
					this.field_18047++;
				});
			});
			list2.add(completableFuture4);
			completableFuture2 = completableFuture4;
		}

		this.completionFuture = SystemUtil.thenCombine(list2);
	}

	@Override
	public CompletableFuture<Void> whenComplete() {
		return this.completionFuture.thenApply(list -> Void.INSTANCE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getProgress() {
		int i = this.listenerCount - this.loadingLoaders.size();
		float f = (float)(this.field_18049.get() * 2 + this.field_18047 * 2 + i * 1);
		float g = (float)(this.field_18048.get() * 2 + this.field_18046 * 2 + this.listenerCount * 1);
		return f / g;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_18786() {
		return this.allLoadedFuture.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean method_18787() {
		return this.completionFuture.isDone();
	}

	public interface class_4047<S> {
		CompletableFuture<S> create(
			ResourceReloadListener.Helper helper, ResourceManager resourceManager, ResourceReloadListener resourceReloadListener, Executor executor, Executor executor2
		);
	}
}
