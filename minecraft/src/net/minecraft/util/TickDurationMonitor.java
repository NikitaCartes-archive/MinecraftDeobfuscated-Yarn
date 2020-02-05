package net.minecraft.util;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.function.LongSupplier;
import javax.annotation.Nullable;
import net.minecraft.util.profiler.DummyProfiler;
import net.minecraft.util.profiler.ProfileResult;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.profiler.ProfilerSystem;
import net.minecraft.util.profiler.ReadableProfiler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TickDurationMonitor {
	private static final Logger LOGGER = LogManager.getLogger();
	private final LongSupplier timeGetter;
	private final long overtime;
	private int tickCount;
	private final File tickResultsDirectory;
	private ReadableProfiler profiler;

	public Profiler nextProfiler() {
		this.profiler = new ProfilerSystem(this.timeGetter, () -> this.tickCount, false);
		this.tickCount++;
		return this.profiler;
	}

	public void endTick() {
		if (this.profiler != DummyProfiler.INSTANCE) {
			ProfileResult profileResult = this.profiler.getResult();
			this.profiler = DummyProfiler.INSTANCE;
			if (profileResult.getTimeSpan() >= this.overtime) {
				File file = new File(this.tickResultsDirectory, "tick-results-" + new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss").format(new Date()) + ".txt");
				profileResult.save(file);
				LOGGER.info("Recorded long tick -- wrote info to: {}", file.getAbsolutePath());
			}
		}
	}

	@Nullable
	public static TickDurationMonitor create(String name) {
		return null;
	}

	public static Profiler tickProfiler(Profiler profiler, @Nullable TickDurationMonitor monitor) {
		return monitor != null ? Profiler.union(monitor.nextProfiler(), profiler) : profiler;
	}
}
