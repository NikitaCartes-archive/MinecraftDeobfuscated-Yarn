package net.minecraft.resource;

import com.google.common.base.Stopwatch;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.Unit;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.ProfilerSystem;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ProfilingResourceReloader extends ResourceReloader<ProfilingResourceReloader.Summary> {
	private static final Logger LOGGER = LogManager.getLogger();
	private final Stopwatch reloadTimer = Stopwatch.createUnstarted();

	public ProfilingResourceReloader(
		ResourceManager resourceManager, List<ResourceReloadListener> list, Executor executor, Executor executor2, CompletableFuture<Unit> completableFuture
	) {
		super(
			executor,
			executor2,
			resourceManager,
			list,
			(synchronizer, resourceManagerx, resourceReloadListener, executor2x, executor3) -> {
				AtomicLong atomicLong = new AtomicLong();
				AtomicLong atomicLong2 = new AtomicLong();
				ProfilerSystem profilerSystem = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), () -> 0);
				ProfilerSystem profilerSystem2 = new ProfilerSystem(SystemUtil.getMeasuringTimeNano(), () -> 0);
				CompletableFuture<Void> completableFuturex = resourceReloadListener.reload(
					synchronizer, resourceManagerx, profilerSystem, profilerSystem2, runnable -> executor2x.execute(() -> {
							long l = SystemUtil.getMeasuringTimeNano();
							runnable.run();
							atomicLong.addAndGet(SystemUtil.getMeasuringTimeNano() - l);
						}), runnable -> executor3.execute(() -> {
							long l = SystemUtil.getMeasuringTimeNano();
							runnable.run();
							atomicLong2.addAndGet(SystemUtil.getMeasuringTimeNano() - l);
						})
				);
				return completableFuturex.thenApplyAsync(
					void_ -> new ProfilingResourceReloader.Summary(
							resourceReloadListener.getClass().getSimpleName(), profilerSystem.getResults(), profilerSystem2.getResults(), atomicLong, atomicLong2
						),
					executor2
				);
			},
			completableFuture
		);
		this.reloadTimer.start();
		this.applyStageFuture.thenAcceptAsync(this::finish, executor2);
	}

	private void finish(List<ProfilingResourceReloader.Summary> list) {
		this.reloadTimer.stop();
		int i = 0;
		LOGGER.info("Resource reload finished after " + this.reloadTimer.elapsed(TimeUnit.MILLISECONDS) + " ms");

		for (ProfilingResourceReloader.Summary summary : list) {
			ProfileResult profileResult = summary.prepareProfile;
			ProfileResult profileResult2 = summary.applyProfile;
			int j = (int)((double)summary.prepareTimeMs.get() / 1000000.0);
			int k = (int)((double)summary.applyTimeMs.get() / 1000000.0);
			int l = j + k;
			String string = summary.name;
			LOGGER.info(string + " took approximately " + l + " ms (" + j + " ms preparing, " + k + " ms applying)");
			String string2 = profileResult.getTimingTreeString();
			if (string2.length() > 0) {
				LOGGER.debug(string + " preparations:\n" + string2);
			}

			String string3 = profileResult2.getTimingTreeString();
			if (string3.length() > 0) {
				LOGGER.debug(string + " reload:\n" + string3);
			}

			LOGGER.info("----------");
			i += k;
		}

		LOGGER.info("Total blocking time: " + i + " ms");
	}

	public static class Summary {
		private final String name;
		private final ProfileResult prepareProfile;
		private final ProfileResult applyProfile;
		private final AtomicLong prepareTimeMs;
		private final AtomicLong applyTimeMs;

		private Summary(String string, ProfileResult profileResult, ProfileResult profileResult2, AtomicLong atomicLong, AtomicLong atomicLong2) {
			this.name = string;
			this.prepareProfile = profileResult;
			this.applyProfile = profileResult2;
			this.prepareTimeMs = atomicLong;
			this.applyTimeMs = atomicLong2;
		}
	}
}
