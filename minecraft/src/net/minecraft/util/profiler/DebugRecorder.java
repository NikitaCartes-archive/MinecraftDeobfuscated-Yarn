package net.minecraft.util.profiler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Instant;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;

public class DebugRecorder implements Recorder {
	public static final int MAX_DURATION_IN_SECONDS = 10;
	@Nullable
	private static Consumer<Path> globalDumpConsumer = null;
	private final Map<Sampler, List<Deviation>> deviations = new Object2ObjectOpenHashMap<>();
	private final TickTimeTracker timeTracker;
	private final Executor dumpExecutor;
	private final RecordDumper dumper;
	private final Consumer<ProfileResult> resultConsumer;
	private final Consumer<Path> dumpConsumer;
	private final SamplerSource samplerSource;
	private final LongSupplier timeGetter;
	private final long endTime;
	private int ticks;
	private ReadableProfiler profiler;
	private volatile boolean stopping;
	private Set<Sampler> samplers = ImmutableSet.of();

	private DebugRecorder(
		SamplerSource samplerSource,
		LongSupplier timeGetter,
		Executor dumpExecutor,
		RecordDumper dumper,
		Consumer<ProfileResult> resultConsumer,
		Consumer<Path> dumpConsumer
	) {
		this.samplerSource = samplerSource;
		this.timeGetter = timeGetter;
		this.timeTracker = new TickTimeTracker(timeGetter, () -> this.ticks);
		this.dumpExecutor = dumpExecutor;
		this.dumper = dumper;
		this.resultConsumer = resultConsumer;
		this.dumpConsumer = globalDumpConsumer == null ? dumpConsumer : dumpConsumer.andThen(globalDumpConsumer);
		this.endTime = timeGetter.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
		this.profiler = new ProfilerSystem(this.timeGetter, () -> this.ticks, false);
		this.timeTracker.enable();
	}

	public static DebugRecorder of(
		SamplerSource source,
		LongSupplier timeGetter,
		Executor dumpExecutor,
		RecordDumper dumper,
		Consumer<ProfileResult> resultConsumer,
		Consumer<Path> dumpConsumer
	) {
		return new DebugRecorder(source, timeGetter, dumpExecutor, dumper, resultConsumer, dumpConsumer);
	}

	@Override
	public synchronized void stop() {
		if (this.isActive()) {
			this.stopping = true;
		}
	}

	@Override
	public synchronized void forceStop() {
		if (this.isActive()) {
			this.profiler = DummyProfiler.INSTANCE;
			this.resultConsumer.accept(EmptyProfileResult.INSTANCE);
			this.forceStop(this.samplers);
		}
	}

	@Override
	public void startTick() {
		this.checkState();
		this.samplers = this.samplerSource.getSamplers(() -> this.profiler);

		for (Sampler sampler : this.samplers) {
			sampler.start();
		}

		this.ticks++;
	}

	@Override
	public void endTick() {
		this.checkState();
		if (this.ticks != 0) {
			for (Sampler sampler : this.samplers) {
				sampler.sample(this.ticks);
				if (sampler.hasDeviated()) {
					Deviation deviation = new Deviation(Instant.now(), this.ticks, this.profiler.getResult());
					((List)this.deviations.computeIfAbsent(sampler, s -> Lists.newArrayList())).add(deviation);
				}
			}

			if (!this.stopping && this.timeGetter.getAsLong() <= this.endTime) {
				this.profiler = new ProfilerSystem(this.timeGetter, () -> this.ticks, false);
			} else {
				this.stopping = false;
				ProfileResult profileResult = this.timeTracker.getResult();
				this.profiler = DummyProfiler.INSTANCE;
				this.resultConsumer.accept(profileResult);
				this.dump(profileResult);
			}
		}
	}

	@Override
	public boolean isActive() {
		return this.timeTracker.isActive();
	}

	@Override
	public Profiler getProfiler() {
		return Profiler.union(this.timeTracker.getProfiler(), this.profiler);
	}

	private void checkState() {
		if (!this.isActive()) {
			throw new IllegalStateException("Not started!");
		}
	}

	private void dump(ProfileResult result) {
		HashSet<Sampler> hashSet = new HashSet(this.samplers);
		this.dumpExecutor.execute(() -> {
			Path path = this.dumper.createDump(hashSet, this.deviations, result);
			this.forceStop(hashSet);
			this.dumpConsumer.accept(path);
		});
	}

	private void forceStop(Collection<Sampler> samplers) {
		for (Sampler sampler : samplers) {
			sampler.stop();
		}

		this.deviations.clear();
		this.timeTracker.disable();
	}

	public static void setGlobalDumpConsumer(Consumer<Path> consumer) {
		globalDumpConsumer = consumer;
	}
}
