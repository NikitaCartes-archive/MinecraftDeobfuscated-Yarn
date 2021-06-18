package net.minecraft.util;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class TimeHelper {
	public static final long SECOND_IN_MILLIS = TimeUnit.SECONDS.toNanos(1L);
	public static final long MILLI_IN_NANOS = TimeUnit.MILLISECONDS.toNanos(1L);

	public static UniformIntProvider betweenSeconds(int min, int max) {
		return UniformIntProvider.create(min * 20, max * 20);
	}
}
