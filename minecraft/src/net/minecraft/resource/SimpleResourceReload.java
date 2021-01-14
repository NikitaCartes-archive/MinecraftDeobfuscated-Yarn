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
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.DummyProfiler;

/**
 * A simple implementation of resource reload.
 * 
 * @param <S> the result type for each reloader in the reload
 */
public class SimpleResourceReload<S> implements ResourceReload {
	protected final ResourceManager manager;
	protected final CompletableFuture<Unit> prepareStageFuture = new CompletableFuture();
	protected final CompletableFuture<List<S>> applyStageFuture;
	private final Set<ResourceReloader> waitingReloaders;
	private final int reloaderCount;
	private int toApplyCount;
	private int appliedCount;
	private final AtomicInteger toPrepareCount = new AtomicInteger();
	private final AtomicInteger preparedCount = new AtomicInteger();

	/**
	 * Creates a simple resource reload without additional results.
	 */
	public static SimpleResourceReload<Void> create(
		ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage
	) {
		return new SimpleResourceReload<>(
			prepareExecutor,
			applyExecutor,
			manager,
			reloaders,
			(synchronizer, resourceManager, resourceReloader, executor2, executor3) -> resourceReloader.reload(
					synchronizer, resourceManager, DummyProfiler.INSTANCE, DummyProfiler.INSTANCE, prepareExecutor, executor3
				),
			initialStage
		);
	}

	protected SimpleResourceReload(
		Executor prepareExecutor,
		Executor applyExecutor,
		ResourceManager manager,
		List<ResourceReloader> reloaders,
		SimpleResourceReload.Factory<S> factory,
		CompletableFuture<Unit> initialStage
	) {
		this.manager = manager;
		this.reloaderCount = reloaders.size();
		this.toPrepareCount.incrementAndGet();
		initialStage.thenRun(this.preparedCount::incrementAndGet);
		List<CompletableFuture<S>> list = Lists.<CompletableFuture<S>>newArrayList();
		CompletableFuture<?> completableFuture = initialStage;
		this.waitingReloaders = Sets.<ResourceReloader>newHashSet(reloaders);

		for (final ResourceReloader resourceReloader : reloaders) {
			final CompletableFuture<?> completableFuture2 = completableFuture;
			CompletableFuture<S> completableFuture3 = factory.create(new ResourceReloader.Synchronizer() {
				@Override
				public <T> CompletableFuture<T> whenPrepared(T preparedObject) {
					applyExecutor.execute(() -> {
						SimpleResourceReload.this.waitingReloaders.remove(resourceReloader);
						if (SimpleResourceReload.this.waitingReloaders.isEmpty()) {
							SimpleResourceReload.this.prepareStageFuture.complete(Unit.INSTANCE);
						}
					});
					return SimpleResourceReload.this.prepareStageFuture.thenCombine(completableFuture2, (unit, object2) -> preparedObject);
				}
			}, manager, resourceReloader, runnable -> {
				this.toPrepareCount.incrementAndGet();
				prepareExecutor.execute(() -> {
					runnable.run();
					this.preparedCount.incrementAndGet();
				});
			}, runnable -> {
				this.toApplyCount++;
				applyExecutor.execute(() -> {
					runnable.run();
					this.appliedCount++;
				});
			});
			list.add(completableFuture3);
			completableFuture = completableFuture3;
		}

		this.applyStageFuture = Util.combine(list);
	}

	@Override
	public CompletableFuture<Unit> whenComplete() {
		return this.applyStageFuture.thenApply(list -> Unit.INSTANCE);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public float getProgress() {
		int i = this.reloaderCount - this.waitingReloaders.size();
		float f = (float)(this.preparedCount.get() * 2 + this.appliedCount * 2 + i * 1);
		float g = (float)(this.toPrepareCount.get() * 2 + this.toApplyCount * 2 + this.reloaderCount * 1);
		return f / g;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isPrepareStageComplete() {
		return this.prepareStageFuture.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean isComplete() {
		return this.applyStageFuture.isDone();
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void throwException() {
		if (this.applyStageFuture.isCompletedExceptionally()) {
			this.applyStageFuture.join();
		}
	}

	public interface Factory<S> {
		CompletableFuture<S> create(
			ResourceReloader.Synchronizer helper, ResourceManager manager, ResourceReloader listener, Executor prepareExecutor, Executor applyExecutor
		);
	}
}
