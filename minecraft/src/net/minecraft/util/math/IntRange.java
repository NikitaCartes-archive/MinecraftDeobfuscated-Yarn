package net.minecraft.util.math;

import java.util.Random;

/**
 * An integer range, inclusive on both ends. Used to choose a random
 * duration of memory.
 */
public class IntRange {
	private final int min;
	private final int max;

	public IntRange(int min, int max) {
		if (max < min) {
			throw new IllegalArgumentException("max must be >= minInclusive! Given minInclusive: " + min + ", Given max: " + max);
		} else {
			this.min = min;
			this.max = max;
		}
	}

	public static IntRange between(int min, int max) {
		return new IntRange(min, max);
	}

	public int choose(Random random) {
		return MathHelper.nextInt(random, this.min, this.max);
	}

	public int getMin() {
		return this.min;
	}

	public int getMax() {
		return this.max;
	}

	public String toString() {
		return "IntRange[" + this.min + "-" + this.max + "]";
	}
}
