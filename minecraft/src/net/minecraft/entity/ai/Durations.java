package net.minecraft.entity.ai;

import java.util.concurrent.TimeUnit;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class Durations {
	public static final long field_33868 = TimeUnit.SECONDS.toNanos(1L);
	public static final long field_33869 = TimeUnit.MILLISECONDS.toNanos(1L);

	public static UniformIntProvider betweenSeconds(int min, int max) {
		return UniformIntProvider.create(min * 20, max * 20);
	}
}
