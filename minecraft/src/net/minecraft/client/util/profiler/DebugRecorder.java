package net.minecraft.client.util.profiler;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import java.nio.file.Path;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.class_6400;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import net.minecraft.util.profiler.TickTimeTracker;

public class DebugRecorder implements Recorder {
	public static final int field_32676 = 10;
	@Nullable
	private static Consumer<Path> globalPathConsumer = null;
	private final Map<SamplingRecorder, List<Sample>> field_33891 = new Object2ObjectOpenHashMap<>();
	private final TickTimeTracker timeTracker;
	private final Executor executor;
	private final ProfilerDumper dumper;
	private final Consumer<ProfileResult> readAction;
	private final Consumer<Path> pathConsumer;
	private final class_6400 field_33892;
	private final LongSupplier timeGetter;
	private final long nanoStartTime;
	private int ticks;
	private ReadableProfiler profiler;
	private volatile boolean pendingRead;
	private Set<SamplingRecorder> field_33893 = ImmutableSet.of();

	private DebugRecorder(
		class_6400 arg, LongSupplier longSupplier, Executor executor, ProfilerDumper profilerDumper, Consumer<ProfileResult> consumer, Consumer<Path> consumer2
	) {
		this.field_33892 = arg;
		this.timeGetter = longSupplier;
		this.timeTracker = new TickTimeTracker(longSupplier, () -> this.ticks);
		this.executor = executor;
		this.dumper = profilerDumper;
		this.readAction = consumer;
		this.pathConsumer = globalPathConsumer == null ? consumer2 : consumer2.andThen(globalPathConsumer);
		this.nanoStartTime = longSupplier.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
		this.profiler = new ProfilerSystem(this.timeGetter, () -> this.ticks, false);
		this.timeTracker.enable();
	}

	public static DebugRecorder method_37191(
		class_6400 arg, LongSupplier longSupplier, Executor executor, ProfilerDumper profilerDumper, Consumer<ProfileResult> consumer, Consumer<Path> consumer2
	) {
		return new DebugRecorder(arg, longSupplier, executor, profilerDumper, consumer, consumer2);
	}

	@Override
	public synchronized void sample() {
		if (this.isActive()) {
			this.pendingRead = true;
		}
	}

	@Override
	public void start() {
		this.checkState();
		this.field_33893 = this.field_33892.method_37189(() -> this.profiler);

		for (SamplingRecorder samplingRecorder : this.field_33893) {
			samplingRecorder.start();
		}

		this.ticks++;
	}

	@Override
	public void read() {
		this.checkState();
		if (this.ticks != 0) {
			for (SamplingRecorder samplingRecorder : this.field_33893) {
				samplingRecorder.sample(this.ticks);
				if (samplingRecorder.method_37174()) {
					Sample sample = new Sample(Instant.now(), this.ticks, this.profiler.getResult());
					((List)this.field_33891.computeIfAbsent(samplingRecorder, samplingRecorderx -> Lists.newArrayList())).add(sample);
				}
			}

			if (!this.pendingRead && this.timeGetter.getAsLong() <= this.nanoStartTime) {
				this.profiler = new ProfilerSystem(this.timeGetter, () -> this.ticks, false);
			} else {
				this.pendingRead = false;
				this.profiler = DummyProfiler.INSTANCE;
				ProfileResult profileResult = this.timeTracker.getResult();
				this.readAction.accept(profileResult);
				this.execute(profileResult);
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

	private void execute(ProfileResult profileResult) {
		HashSet<SamplingRecorder> hashSet = new HashSet(this.field_33893);
		this.executor.execute(() -> {
			Path path = this.dumper.createDump(hashSet, this.field_33891, profileResult);

			for (SamplingRecorder samplingRecorder : hashSet) {
				samplingRecorder.stop();
			}

			this.field_33891.clear();
			this.timeTracker.disable();
			this.pathConsumer.accept(path);
		});
	}

	public static void setGlobalPathConsumer(Consumer<Path> globalPathConsumer) {
		DebugRecorder.globalPathConsumer = globalPathConsumer;
	}
}
