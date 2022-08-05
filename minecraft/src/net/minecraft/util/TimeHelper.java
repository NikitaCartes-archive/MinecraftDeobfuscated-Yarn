package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.intprovider.UniformIntProvider;

/**
 * A class holding utility methods related to time and durations.
 */
public class TimeHelper {
	/**
	 * The equivalent of 1 second, in nanoseconds. Is {@code 1000000000}.
	 */
	public static final long SECOND_IN_NANOS = TimeUnit.SECONDS.toNanos(1L);
	/**
	 * The equivalent of 1 millisecond, in nanoseconds. Is {@code 1000000}.
	 */
	public static final long MILLI_IN_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);

	/**
	 * {@return an int provider that provides a time in ticks, between {@code min * 20} and
	 * {@code max * 20} (both inclusive)}
	 */
	public static UniformIntProvider betweenSeconds(int min, int max) {
		return UniformIntProvider.create(min * 20, max * 20);
	}
}
