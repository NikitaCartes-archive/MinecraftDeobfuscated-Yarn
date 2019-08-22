package net.minecraft.resource;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.DummyProfiler;

public class ResourceReloader<S> implements ResourceReloadMonitor {
	protected final ResourceManager manager;
	protected final CompletableFuture<Unit> prepareStageFuture = new CompletableFuture();
	protected final CompletableFuture<List<S>> applyStageFuture;
	private final Set<ResourceReloadListener> waitingListeners;
	private final int listenerCount;
	private int applyingCount;
	private int appliedCount;
	private final AtomicInteger preparingCount = new AtomicInteger();
	private final AtomicInteger preparedCount = new AtomicInteger();

	public static ResourceReloader<Void> create(
		ResourceManager resourceManager, List<ResourceReloadListener> list, Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture
	) {
		return new ResourceReloader<>(
			executor,
			executor2,
			resourceManager,
			list,
			(synchronizer, resourceManagerx, resourceReloadListener, executor2x, executor3) -> resourceReloadListener.reload(
					synchronizer, resourceManagerx, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, executor, executor3
				),
			completableFuture
		);
	}

	protected ResourceReloader(
		Executor executor,
		Executor executor2,
		ResourceManager resourceManager,
		List<ResourceReloadListener> list,
		ResourceReloader.Factory<S> factory,
		CompletableFuture<Unit> completableFuture
	) {
		this.manager = resourceManager;
		this.listenerCount = list.size();
		this.preparingCount.incrementAndGet();
		completableFuture.thenRun(this.preparedCount::incrementAndGet);
		List<CompletableFuture<S>> list2 = Lists.<CompletableFuture<S>>newArrayList();
		CompletableFuture<?> completableFuture2 = completableFuture;
		this.waitingListeners = Sets.<ResourceReloadListener>newHashSet(list);

		for (final ResourceReloadListener resourceReloadListener : list) {
			final CompletableFuture<?> completableFuture3 = completableFuture2;
			CompletableFuture<S> completableFuture4 = factory.create(new ResourceReloadListener.Synchronizer() {
				@Override
				public <T> CompletableFuture<T> whenPrepared(T object) {
					executor2.execute(() -> {
						ResourceReloader.this.waitingListeners.remove(resourceReloadListener);
						if (ResourceReloader.this.waitingListeners.isEmpty()) {
							ResourceReloader.this.prepareStageFuture.complete(Unit.INSTANCE);
						}
					});
					return ResourceReloader.this.prepareStageFuture.thenCombine(completableFuture3, (unit, object2) -> object);
				}
			}, resourceManager, resourceReloadListener, runnable -> {
				this.preparingCount.incrementAndGet();
				executor.execute(() -> {
					runnable.run();
					this.preparedCount.incrementAndGet();
				});
			}, runnable -> {
				this.applyingCount++;
				executor2.execute(() -> {
					runnable.run();
					this.appliedCount++;
				});
			});
			list2.add(completableFuture4);
			completableFuture2 = completableFuture4;
		}

		this.applyStageFuture = SystemUtil.thenCombine(list2);
	}

	@Override
	public CompletableFuture<Unit> whenComplete() {
		return this.applyStageFuture.thenApply(list -> Unit.INSTANCE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getProgress() {
		int i = this.listenerCount - this.waitingListeners.size();
		float f = (float)(this.preparedCount.get() * 2 + this.appliedCount * 2 + i * 1);
		float g = (float)(this.preparingCount.get() * 2 + this.applyingCount * 2 + this.listenerCount * 1);
		return f / g;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isLoadStageComplete() {
		return this.prepareStageFuture.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isApplyStageComplete() {
		return this.applyStageFuture.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void throwExceptions() {
		if (this.applyStageFuture.isCompletedExceptionally()) {
			this.applyStageFuture.join();
		}
	}

	public interface Factory<S> {
		CompletableFuture<S> create(
			ResourceReloadListener.Synchronizer synchronizer,
			ResourceManager resourceManager,
			ResourceReloadListener resourceReloadListener,
			Executor executor,
			Executor executor2
		);
	}
}
