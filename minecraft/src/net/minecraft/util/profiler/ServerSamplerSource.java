package net.minecraft.util.profiler;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSet.Builder;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.IntStream;
import net.minecraft.util.SystemDetails;
import net.minecraft.util.thread.ExecutorSampling;
import org.slf4j.Logger;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;

public class ServerSamplerSource implements SamplerSource {
	private static final Logger LOGGER = LogUtils.getLogger();
	private final Set<Sampler> samplers = new ObjectOpenHashSet<>();
	private final SamplerFactory factory = new SamplerFactory();

	public ServerSamplerSource(LongSupplier nanoTimeSupplier, boolean includeSystem) {
		this.samplers.add(createTickTimeTracker(nanoTimeSupplier));
		if (includeSystem) {
			this.samplers.addAll(createSystemSamplers());
		}
	}

	public static Set<Sampler> createSystemSamplers() {
		Builder<Sampler> builder = ImmutableSet.builder();

		try {
			ServerSamplerSource.CpuUsageFetcher cpuUsageFetcher = new ServerSamplerSource.CpuUsageFetcher();
			IntStream.range(0, cpuUsageFetcher.logicalProcessorCount)
				.mapToObj(index -> Sampler.create("cpu#" + index, SampleType.CPU, () -> cpuUsageFetcher.getCpuUsage(index)))
				.forEach(builder::add);
		} catch (Throwable var2) {
			LOGGER.warn("Failed to query cpu, no cpu stats will be recorded", var2);
		}

		builder.add(
			Sampler.create("heap MiB", SampleType.JVM, () -> (double)SystemDetails.toMebibytes(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()))
		);
		builder.addAll(ExecutorSampling.INSTANCE.createSamplers());
		return builder.build();
	}

	@Override
	public Set<Sampler> getSamplers(Supplier<ReadableProfiler> profilerSupplier) {
		this.samplers.addAll(this.factory.createSamplers(profilerSupplier));
		return this.samplers;
	}

	public static Sampler createTickTimeTracker(LongSupplier nanoTimeSupplier) {
		Stopwatch stopwatch = Stopwatch.createUnstarted(new Ticker() {
			@Override
			public long read() {
				return nanoTimeSupplier.getAsLong();
			}
		});
		ToDoubleFunction<Stopwatch> toDoubleFunction = watch -> {
			if (watch.isRunning()) {
				watch.stop();
			}

			long l = watch.elapsed(TimeUnit.NANOSECONDS);
			watch.reset();
			return (double)l;
		};
		Sampler.RatioDeviationChecker ratioDeviationChecker = new Sampler.RatioDeviationChecker(2.0F);
		return Sampler.builder("ticktime", SampleType.TICK_LOOP, toDoubleFunction, stopwatch)
			.startAction(Stopwatch::start)
			.deviationChecker(ratioDeviationChecker)
			.build();
	}

	static class CpuUsageFetcher {
		private final SystemInfo systemInfo = new SystemInfo();
		private final CentralProcessor processor = this.systemInfo.getHardware().getProcessor();
		public final int logicalProcessorCount = this.processor.getLogicalProcessorCount();
		private long[][] loadTicks = this.processor.getProcessorCpuLoadTicks();
		private double[] loadBetweenTicks = this.processor.getProcessorCpuLoadBetweenTicks(this.loadTicks);
		private long lastCheckTime;

		public double getCpuUsage(int index) {
			long l = System.currentTimeMillis();
			if (this.lastCheckTime == 0L || this.lastCheckTime + 501L < l) {
				this.loadBetweenTicks = this.processor.getProcessorCpuLoadBetweenTicks(this.loadTicks);
				this.loadTicks = this.processor.getProcessorCpuLoadTicks();
				this.lastCheckTime = l;
			}

			return this.loadBetweenTicks[index] * 100.0;
		}
	}
}
