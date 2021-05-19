package net.minecraft.client.util.profiler;

import com.google.common.base.Stopwatch;
import com.google.common.base.Ticker;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.nio.file.Path;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.LongSupplier;
import java.util.function.ToDoubleFunction;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.chunk.ChunkBuilder;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.MetricSuppliers;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import net.minecraft.util.profiler.TickTimeTracker;

@Environment(EnvType.CLIENT)
public class DebugRecorder implements Recorder {
	public static final int field_32676 = 10;
	@Nullable
	private static Consumer<Path> globalPathConsumer = null;
	private final List<Category> categories = new ObjectArrayList<>();
	private final TickTimeTracker timeTracker;
	private final Executor executor;
	private final ProfilerDumper dumper;
	private final Runnable readAction;
	private final Consumer<Path> pathConsumer;
	private final LongSupplier timeGetter;
	private final List<Sample> samples = new ObjectArrayList<>();
	private final long nanoStartTime;
	private int ticks;
	private ReadableProfiler profiler;
	private volatile boolean pendingRead;

	private DebugRecorder(LongSupplier timeGetter, Executor executor, ProfilerDumper dumper, Runnable readAction, Consumer<Path> completeAction) {
		this.timeGetter = timeGetter;
		this.timeTracker = new TickTimeTracker(timeGetter, () -> this.ticks);
		this.executor = executor;
		this.dumper = dumper;
		this.readAction = readAction;
		this.pathConsumer = globalPathConsumer == null ? completeAction : completeAction.andThen(globalPathConsumer);
		this.nanoStartTime = timeGetter.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
		this.createCategories();
		this.profiler = new ProfilerSystem(this.timeGetter, () -> this.ticks, false);
		this.timeTracker.enable();
	}

	public static DebugRecorder create(LongSupplier timeGetter, Executor executore, ProfilerDumper dumper, Runnable readAction, Consumer<Path> completeAction) {
		return new DebugRecorder(timeGetter, executore, dumper, readAction, completeAction);
	}

	private void createCategories() {
		this.categories
			.add(
				new Category(
					"JVM", SamplingRecorder.create("heap (Mb)", () -> (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0)
				)
			);
		this.categories.add(new Category("Frame times (ms)", this.createFrameTimeSampler(this.timeGetter)));
		this.categories
			.add(
				new Category(
					"Task total durations (ms)",
					this.createFactory("gameRendering").createSampler("root", "gameRenderer"),
					this.createFactory("updateDisplay").createSampler("root", "updateDisplay"),
					this.createFactory("skyRendering").createSampler("root", "gameRenderer", "level", "sky")
				)
			);
		WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
		this.categories
			.add(
				new Category(
					"Rendering chunk dispatching",
					SamplingRecorder.create("totalChunks", worldRenderer, WorldRenderer::getChunkCount),
					SamplingRecorder.create("renderedChunks", worldRenderer, WorldRenderer::getCompletedChunkCount),
					SamplingRecorder.create("lastViewDistance", worldRenderer, WorldRenderer::getViewDistance)
				)
			);
		ChunkBuilder chunkBuilder = worldRenderer.getChunkBuilder();
		this.categories
			.add(
				new Category(
					"Rendering chunk stats",
					SamplingRecorder.create("toUpload", chunkBuilder, ChunkBuilder::getChunksToUpload),
					SamplingRecorder.create("freeBufferCount", chunkBuilder, ChunkBuilder::getFreeBufferCount),
					SamplingRecorder.create("toBatchCount", chunkBuilder, ChunkBuilder::getToBatchCount)
				)
			);
		MetricSuppliers.INSTANCE
			.getSamplers()
			.forEach(
				(channel, metrics) -> {
					List<SamplingRecorder> list = (List<SamplingRecorder>)metrics.stream()
						.map(metricSampler -> SamplingRecorder.create(metricSampler.getMetric(), metricSampler.getValueSupplier()))
						.collect(Collectors.toList());
					this.categories.add(new Category(channel.getName(), list));
				}
			);
	}

	private SamplerFactory createFactory(String name) {
		return new SamplerFactory(name, () -> this.profiler);
	}

	private SamplingRecorder createFrameTimeSampler(LongSupplier timeGetter) {
		Stopwatch stopwatch = Stopwatch.createUnstarted(new Ticker() {
			@Override
			public long read() {
				return timeGetter.getAsLong();
			}
		});
		ToDoubleFunction<Stopwatch> toDoubleFunction = stopwatchx -> {
			if (stopwatchx.isRunning()) {
				stopwatchx.stop();
			}

			long l = stopwatchx.elapsed(TimeUnit.MILLISECONDS);
			stopwatchx.reset();
			return (double)l;
		};
		SamplingRecorder.HighPassValueConsumer highPassValueConsumer = new SamplingRecorder.HighPassValueConsumer(
			0.5F, d -> this.samples.add(new Sample(new Date(), this.ticks, this.profiler.getResult()))
		);
		return SamplingRecorder.create("frametime", toDoubleFunction, stopwatch).startAction(Stopwatch::start).writeAction(highPassValueConsumer).create();
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

		for (Category category : this.categories) {
			category.start();
		}

		this.ticks++;
	}

	@Override
	public void read() {
		this.checkState();
		if (this.ticks != 0) {
			for (Category category : this.categories) {
				category.sample();
			}

			if (!this.pendingRead && this.timeGetter.getAsLong() <= this.nanoStartTime) {
				this.profiler = new ProfilerSystem(this.timeGetter, () -> this.ticks, false);
			} else {
				this.readAction.run();
				this.pendingRead = false;
				this.profiler = DummyProfiler.INSTANCE;
				this.execute();
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

	private void execute() {
		this.executor.execute(() -> {
			Path path = this.dumper.createDump(this.categories, this.samples, this.timeTracker);

			for (Category category : this.categories) {
				category.stop();
			}

			this.categories.clear();
			this.samples.clear();
			this.timeTracker.disable();
			this.pathConsumer.accept(path);
		});
	}

	public static void setGlobalPathConsumer(Consumer<Path> globalPathConsumer) {
		DebugRecorder.globalPathConsumer = globalPathConsumer;
	}
}
