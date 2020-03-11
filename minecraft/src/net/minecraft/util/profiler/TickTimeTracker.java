package net.minecraft.util.profiler;

import java.util.function.IntSupplier;
import java.util.function.LongSupplier;

public class TickTimeTracker {
	private final LongSupplier timeGetter;
	private final IntSupplier tickGetter;
	private ReadableProfiler profiler = DummyProfiler.INSTANCE;

	public TickTimeTracker(LongSupplier timeGetter, IntSupplier tickGetter) {
		this.timeGetter = timeGetter;
		this.tickGetter = tickGetter;
	}

	public boolean isActive() {
		return this.profiler != DummyProfiler.INSTANCE;
	}

	public void disable() {
		this.profiler = DummyProfiler.INSTANCE;
	}

	public void enable() {
		this.profiler = new ProfilerSystem(this.timeGetter, this.tickGetter, true);
	}

	public Profiler getProfiler() {
		return this.profiler;
	}

	public ProfileResult getResult() {
		return this.profiler.getResult();
	}
}
