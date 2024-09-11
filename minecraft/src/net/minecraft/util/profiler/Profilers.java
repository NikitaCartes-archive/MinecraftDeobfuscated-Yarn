package net.minecraft.util.profiler;

import com.mojang.jtracy.TracyClient;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

public final class Profilers {
	private static final ThreadLocal<TracyProfiler> TRACY_PROFILER = ThreadLocal.withInitial(TracyProfiler::new);
	private static final ThreadLocal<Profiler> BUILTIN_PROFILER = new ThreadLocal();
	private static final AtomicInteger ACTIVE_BUILTIN_PROFILER_COUNT = new AtomicInteger();

	private Profilers() {
	}

	public static Profilers.Scoped using(Profiler profiler) {
		activate(profiler);
		return Profilers::deactivate;
	}

	private static void activate(Profiler profiler) {
		if (BUILTIN_PROFILER.get() != null) {
			throw new IllegalStateException("Profiler is already active");
		} else {
			Profiler profiler2 = union(profiler);
			BUILTIN_PROFILER.set(profiler2);
			ACTIVE_BUILTIN_PROFILER_COUNT.incrementAndGet();
			profiler2.startTick();
		}
	}

	private static void deactivate() {
		Profiler profiler = (Profiler)BUILTIN_PROFILER.get();
		if (profiler == null) {
			throw new IllegalStateException("Profiler was not active");
		} else {
			BUILTIN_PROFILER.remove();
			ACTIVE_BUILTIN_PROFILER_COUNT.decrementAndGet();
			profiler.endTick();
		}
	}

	private static Profiler union(Profiler builtinProfiler) {
		return Profiler.union(getDefault(), builtinProfiler);
	}

	public static Profiler get() {
		return ACTIVE_BUILTIN_PROFILER_COUNT.get() == 0
			? getDefault()
			: (Profiler)Objects.requireNonNullElseGet((Profiler)BUILTIN_PROFILER.get(), Profilers::getDefault);
	}

	private static Profiler getDefault() {
		return (Profiler)(TracyClient.isAvailable() ? (Profiler)TRACY_PROFILER.get() : DummyProfiler.INSTANCE);
	}

	public interface Scoped extends AutoCloseable {
		void close();
	}
}
