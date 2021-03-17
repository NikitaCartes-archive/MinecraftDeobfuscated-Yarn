package net.minecraft;

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
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import net.minecraft.util.profiler.TickTimeTracker;

@Environment(EnvType.CLIENT)
public class class_5961 implements class_5962 {
	@Nullable
	private static Consumer<Path> field_29579 = null;
	private final List<class_5969> field_29580 = new ObjectArrayList<>();
	private final TickTimeTracker field_29581;
	private final Executor field_29582;
	private final class_5971 field_29583;
	private final Runnable field_29584;
	private final Consumer<Path> field_29585;
	private final LongSupplier field_29586;
	private final List<class_5964> field_29587 = new ObjectArrayList<>();
	private final long field_29588;
	private int field_29589;
	private ReadableProfiler field_29590;
	private volatile boolean field_29591;

	private class_5961(LongSupplier longSupplier, Executor executor, class_5971 arg, Runnable runnable, Consumer<Path> consumer) {
		this.field_29586 = longSupplier;
		this.field_29581 = new TickTimeTracker(longSupplier, () -> this.field_29589);
		this.field_29582 = executor;
		this.field_29583 = arg;
		this.field_29584 = runnable;
		this.field_29585 = field_29579 == null ? consumer : consumer.andThen(field_29579);
		this.field_29588 = longSupplier.getAsLong() + TimeUnit.NANOSECONDS.convert(10L, TimeUnit.SECONDS);
		this.method_34761();
		this.field_29590 = new ProfilerSystem(this.field_29586, () -> this.field_29589, false);
		this.field_29581.enable();
	}

	public static class_5961 method_34760(LongSupplier longSupplier, Executor executor, class_5971 arg, Runnable runnable, Consumer<Path> consumer) {
		return new class_5961(longSupplier, executor, arg, runnable, consumer);
	}

	private void method_34761() {
		this.field_29580
			.add(
				new class_5969(
					"JVM", class_5965.method_34778("heap (Mb)", () -> (double)(Runtime.getRuntime().totalMemory() - Runtime.getRuntime().freeMemory()) / 1048576.0)
				)
			);
		this.field_29580.add(new class_5969("Frame times (ms)", this.method_34759(this.field_29586)));
		this.field_29580
			.add(
				new class_5969(
					"Task total durations (ms)",
					this.method_34758("gameRendering").method_34799("root", "gameRenderer"),
					this.method_34758("updateDisplay").method_34799("root", "updateDisplay"),
					this.method_34758("skyRendering").method_34799("root", "gameRenderer", "level", "sky")
				)
			);
		WorldRenderer worldRenderer = MinecraftClient.getInstance().worldRenderer;
		this.field_29580
			.add(
				new class_5969(
					"Rendering chunk dispatching",
					class_5965.method_34777("totalChunks", worldRenderer, WorldRenderer::method_34811),
					class_5965.method_34777("renderedChunks", worldRenderer, WorldRenderer::getCompletedChunkCount),
					class_5965.method_34777("lastViewDistance", worldRenderer, WorldRenderer::method_34812)
				)
			);
		ChunkBuilder chunkBuilder = worldRenderer.method_34810();
		this.field_29580
			.add(
				new class_5969(
					"Rendering chunk stats",
					class_5965.method_34777("toUpload", chunkBuilder, ChunkBuilder::method_34846),
					class_5965.method_34777("freeBufferCount", chunkBuilder, ChunkBuilder::method_34847),
					class_5965.method_34777("toBatchCount", chunkBuilder, ChunkBuilder::method_34845)
				)
			);
		class_5950.field_29555
			.method_34701()
			.forEach(
				(arg, list) -> {
					List<class_5965> list2 = (List<class_5965>)list.stream()
						.map(argx -> class_5965.method_34776(argx.method_34697(), argx.method_34698()))
						.collect(Collectors.toList());
					this.field_29580.add(new class_5969(arg.method_34700(), list2));
				}
			);
	}

	private class_5970 method_34758(String string) {
		return new class_5970(string, () -> this.field_29590);
	}

	private class_5965 method_34759(LongSupplier longSupplier) {
		Stopwatch stopwatch = Stopwatch.createUnstarted(new Ticker() {
			@Override
			public long read() {
				return longSupplier.getAsLong();
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
		class_5965.class_5968 lv = new class_5965.class_5968(
			0.5F, d -> this.field_29587.add(new class_5964(new Date(), this.field_29589, this.field_29590.getResult()))
		);
		return class_5965.method_34779("frametime", toDoubleFunction, stopwatch).method_34789(Stopwatch::start).method_34788(lv).method_34787();
	}

	@Override
	public synchronized void method_34770() {
		if (this.method_34773()) {
			this.field_29591 = true;
		}
	}

	@Override
	public void method_34771() {
		this.method_34762();

		for (class_5969 lv : this.field_29580) {
			lv.method_34794();
		}

		this.field_29589++;
	}

	@Override
	public void method_34772() {
		this.method_34762();
		if (this.field_29589 != 0) {
			for (class_5969 lv : this.field_29580) {
				lv.method_34793();
			}

			if (!this.field_29591 && this.field_29586.getAsLong() <= this.field_29588) {
				this.field_29590 = new ProfilerSystem(this.field_29586, () -> this.field_29589, false);
			} else {
				this.field_29584.run();
				this.field_29591 = false;
				this.field_29590 = DummyProfiler.INSTANCE;
				this.method_34763();
			}
		}
	}

	@Override
	public boolean method_34773() {
		return this.field_29581.isActive();
	}

	@Override
	public Profiler method_34774() {
		return Profiler.union(this.field_29581.getProfiler(), this.field_29590);
	}

	private void method_34762() {
		if (!this.method_34773()) {
			throw new IllegalStateException("Not started!");
		}
	}

	private void method_34763() {
		this.field_29582.execute(() -> {
			Path path = this.field_29583.method_34807(this.field_29580, this.field_29587, this.field_29581);

			for (class_5969 lv : this.field_29580) {
				lv.method_34795();
			}

			this.field_29580.clear();
			this.field_29587.clear();
			this.field_29581.disable();
			this.field_29585.accept(path);
		});
	}
}
