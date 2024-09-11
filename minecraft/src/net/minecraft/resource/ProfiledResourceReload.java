package net.minecraft.resource;

import com.google.common.base.Stopwatch;
import com.mojang.logging.LogUtils;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.Unit;
import net.minecraft.util.Util;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.Profilers;
import org.slf4j.Logger;

/**
 * An implementation of resource reload that includes an additional profiling
 * summary for each reloader.
 */
public class ProfiledResourceReload extends SimpleResourceReload<ProfiledResourceReload.Summary> {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Stopwatch reloadTimer = Stopwatch.createUnstarted();

	public ProfiledResourceReload(
		ResourceManager manager, List<ResourceReloader> reloaders, Executor prepareExecutor, Executor applyExecutor, CompletableFuture<Unit> initialStage
	) {
		super(
			prepareExecutor,
			applyExecutor,
			manager,
			reloaders,
			(synchronizer, resourceManager, resourceReloader, executor2, executor3) -> {
				AtomicLong atomicLong = new AtomicLong();
				AtomicLong atomicLong2 = new AtomicLong();
				CompletableFuture<Void> completableFuture = resourceReloader.reload(
					synchronizer,
					resourceManager,
					method_64141(executor2, atomicLong, resourceReloader.getName()),
					method_64141(executor3, atomicLong2, resourceReloader.getName())
				);
				return completableFuture.thenApplyAsync(void_ -> {
					LOGGER.debug("Finished reloading {}", resourceReloader.getName());
					return new ProfiledResourceReload.Summary(resourceReloader.getName(), atomicLong, atomicLong2);
				}, applyExecutor);
			},
			initialStage
		);
		this.reloadTimer.start();
		this.applyStageFuture = this.applyStageFuture.thenApplyAsync(this::finish, applyExecutor);
	}

	private static Executor method_64141(Executor executor, AtomicLong atomicLong, String string) {
		return runnable -> executor.execute(() -> {
				Profiler profiler = Profilers.get();
				profiler.push(string);
				long l = Util.getMeasuringTimeNano();
				runnable.run();
				atomicLong.addAndGet(Util.getMeasuringTimeNano() - l);
				profiler.pop();
			});
	}

	private List<ProfiledResourceReload.Summary> finish(List<ProfiledResourceReload.Summary> summaries) {
		this.reloadTimer.stop();
		long l = 0L;
		LOGGER.info("Resource reload finished after {} ms", this.reloadTimer.elapsed(TimeUnit.MILLISECONDS));

		for (ProfiledResourceReload.Summary summary : summaries) {
			long m = TimeUnit.NANOSECONDS.toMillis(summary.prepareTimeMs.get());
			long n = TimeUnit.NANOSECONDS.toMillis(summary.applyTimeMs.get());
			long o = m + n;
			String string = summary.name;
			LOGGER.info("{} took approximately {} ms ({} ms preparing, {} ms applying)", string, o, m, n);
			l += n;
		}

		LOGGER.info("Total blocking time: {} ms", l);
		return summaries;
	}

	/**
	 * The profiling summary for each reloader in the reload.
	 */
	public static record Summary(String name, AtomicLong prepareTimeMs, AtomicLong applyTimeMs) {
	}
}
