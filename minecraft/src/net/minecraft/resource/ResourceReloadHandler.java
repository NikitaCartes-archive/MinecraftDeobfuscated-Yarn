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

public class ResourceReloadHandler<S> implements ResourceReloadMonitor {
	protected final ResourceManager resourceManager;
	protected final CompletableFuture<Void> loadStageFuture = new CompletableFuture();
	protected final CompletableFuture<List<S>> applyStageFuture;
	private final Set<ResourceReloadListener> loadStageLoaders;
	private final int listenerCount;
	private int field_18046;
	private int field_18047;
	private final AtomicInteger field_18048 = new AtomicInteger();
	private final AtomicInteger field_18049 = new AtomicInteger();

	public static ResourceReloadHandler<java.lang.Void> create(
		ResourceManager resourceManager, List<ResourceReloadListener> list, Executor executor, Executor executor2, CompletableFuture<Void> completableFuture
	) {
		return new ResourceReloadHandler<>(
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

	protected ResourceReloadHandler(
		Executor executor,
		Executor executor2,
		ResourceManager resourceManager,
		List<ResourceReloadListener> list,
		ResourceReloadHandler.class_4047<S> arg,
		CompletableFuture<Void> completableFuture
	) {
		this.resourceManager = resourceManager;
		this.listenerCount = list.size();
		this.field_18048.incrementAndGet();
		completableFuture.thenRun(this.field_18049::incrementAndGet);
		List<CompletableFuture<S>> list2 = new ArrayList();
		CompletableFuture<?> completableFuture2 = completableFuture;
		this.loadStageLoaders = Sets.<ResourceReloadListener>newHashSet(list);

		for (final ResourceReloadListener resourceReloadListener : list) {
			final CompletableFuture<?> completableFuture3 = completableFuture2;
			CompletableFuture<S> completableFuture4 = arg.create(new ResourceReloadListener.Helper() {
				@Override
				public <T> CompletableFuture<T> waitForAll(T object) {
					executor2.execute(() -> {
						ResourceReloadHandler.this.loadStageLoaders.remove(resourceReloadListener);
						if (ResourceReloadHandler.this.loadStageLoaders.isEmpty()) {
							ResourceReloadHandler.this.loadStageFuture.complete(Void.INSTANCE);
						}
					});
					return ResourceReloadHandler.this.loadStageFuture.thenCombine(completableFuture3, (void_, object2) -> object);
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

		this.applyStageFuture = SystemUtil.thenCombine(list2);
	}

	@Override
	public CompletableFuture<Void> whenComplete() {
		return this.applyStageFuture.thenApply(list -> Void.INSTANCE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getProgress() {
		int i = this.listenerCount - this.loadStageLoaders.size();
		float f = (float)(this.field_18049.get() * 2 + this.field_18047 * 2 + i * 1);
		float g = (float)(this.field_18048.get() * 2 + this.field_18046 * 2 + this.listenerCount * 1);
		return f / g;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isLoadStageComplete() {
		return this.loadStageFuture.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isApplyStageComplete() {
		return this.applyStageFuture.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void method_18849() {
		if (this.applyStageFuture.isCompletedExceptionally()) {
			this.applyStageFuture.join();
		}
	}

	public interface class_4047<S> {
		CompletableFuture<S> create(
			ResourceReloadListener.Helper helper, ResourceManager resourceManager, ResourceReloadListener resourceReloadListener, Executor executor, Executor executor2
		);
	}
}
