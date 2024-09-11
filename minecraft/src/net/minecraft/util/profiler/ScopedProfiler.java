package net.minecraft.util.profiler;

import java.util.function.Supplier;
import javax.annotation.Nullable;

public class ScopedProfiler implements AutoCloseable {
	public static final ScopedProfiler DUMMY = new ScopedProfiler(null);
	@Nullable
	private final Profiler wrapped;

	ScopedProfiler(@Nullable Profiler wrapped) {
		this.wrapped = wrapped;
	}

	public ScopedProfiler addLabel(String label) {
		if (this.wrapped != null) {
			this.wrapped.addZoneText(label);
		}

		return this;
	}

	public ScopedProfiler addLabel(Supplier<String> labelSupplier) {
		if (this.wrapped != null) {
			this.wrapped.addZoneText((String)labelSupplier.get());
		}

		return this;
	}

	public ScopedProfiler addValue(long value) {
		if (this.wrapped != null) {
			this.wrapped.addZoneValue(value);
		}

		return this;
	}

	public ScopedProfiler setColor(int color) {
		if (this.wrapped != null) {
			this.wrapped.setZoneColor(color);
		}

		return this;
	}

	public void close() {
		if (this.wrapped != null) {
			this.wrapped.pop();
		}
	}
}
