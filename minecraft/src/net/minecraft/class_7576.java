package net.minecraft;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface class_7576 {
	long get(TimeUnit timeUnit);

	public interface class_7577 extends class_7576, LongSupplier {
		@Override
		default long get(TimeUnit timeUnit) {
			return timeUnit.convert(this.getAsLong(), TimeUnit.NANOSECONDS);
		}
	}
}
