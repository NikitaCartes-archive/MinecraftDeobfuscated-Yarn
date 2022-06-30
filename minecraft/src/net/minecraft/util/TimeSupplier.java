package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import java.util.function.LongSupplier;

@FunctionalInterface
public interface TimeSupplier {
	long get(TimeUnit timeUnit);

	public interface Nanoseconds extends TimeSupplier, LongSupplier {
		@Override
		default long get(TimeUnit timeUnit) {
			return timeUnit.convert(this.getAsLong(), TimeUnit.NANOSECONDS);
		}
	}
}
