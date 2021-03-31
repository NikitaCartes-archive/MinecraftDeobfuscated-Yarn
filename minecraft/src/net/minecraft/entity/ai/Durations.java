package net.minecraft.entity.ai;

import net.minecraft.util.math.intprovider.UniformIntProvider;

public class Durations {
	public static UniformIntProvider betweenSeconds(int min, int max) {
		return UniformIntProvider.create(min * 20, max * 20);
	}
}
