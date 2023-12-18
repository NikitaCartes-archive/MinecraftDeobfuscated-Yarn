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
	 * The equivalent of 1 second, in milliseconds. Is {@code 1000}.
	 */
	public static final long SECOND_IN_MILLIS = TimeUnit.SECONDS.toMillis(1L);
	/**
	 * The equivalent of 1 hour, in seconds. Is {@code 3600}.
	 */
	public static final long HOUR_IN_SECONDS = TimeUnit.HOURS.toSeconds(1L);
	/**
	 * The equivalent of 1 minute, in seconds. Is {@code 60}.
	 */
	public static final int MINUTE_IN_SECONDS = (int)TimeUnit.MINUTES.toSeconds(1L);

	/**
	 * {@return an int provider that provides a time in ticks, between {@code min * 20} and
	 * {@code max * 20} (both inclusive)}
	 */
	public static UniformIntProvider betweenSeconds(int min, int max) {
		return UniformIntProvider.create(min * 20, max * 20);
	}
}
