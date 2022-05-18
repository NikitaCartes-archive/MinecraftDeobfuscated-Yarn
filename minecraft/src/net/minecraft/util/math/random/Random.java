package net.minecraft.util.math.random;

import io.netty.util.internal.ThreadLocalRandom;

/**
 * A reimplementation of {@link java.util.Random}.
 * 
 * <p>There are four built-in implementations, three based on the classic Java algorithm
 * and one using Xoroshiro128++ algorithm.
 * 
 * <ul>
 * <li>{@link LocalRandom}: Silently breaks when used concurrently. Based on
 * {@link java.util.Random}.</li>
 * <li>{@link CheckedRandom}: Throws when used concurrently. Based on
 * {@link java.util.Random}.</li>
 * <li>{@link ThreadSafeRandom}: Blocks the thread when used concurrently. Based on
 * {@link java.util.Random}.</li>
 * <li>{@link Xoroshiro128PlusPlusRandom}: Silently breaks when used concurrently.
 * Implements the Xoroshiro128++ algorithm.</li>
 * </ul>
 * 
 * @implNote Note that due to MC-239059, this is not an exact reimplementation of
 * the Java random number generator algorithm.
 * 
 * @see java.util.random
 */
public interface Random {
	@Deprecated
	double field_38930 = 2.297;

	/**
	 * {@return a random} The returned random actively detects concurrent usage
	 * and fails on detection.
	 */
	static Random create() {
		return create(RandomSeed.getSeed());
	}

	/**
	 * {@return a random, suitable for multithreaded scenarios}
	 * 
	 * @deprecated This one is hard to ensure deterministic behavior compared
	 * to the non-thread-safe one.
	 */
	@Deprecated
	static Random createThreadSafe() {
		return new ThreadSafeRandom(RandomSeed.getSeed());
	}

	/**
	 * {@return a random with the given {@code seed}} The returned random
	 * actively detects concurrent usage and fails on detection.
	 */
	static Random create(long seed) {
		return new CheckedRandom(seed);
	}

	/**
	 * {@return a random split from the thread local random} Users must ensure
	 * this random is not used concurrently.
	 */
	static Random createLocal() {
		return new LocalRandom(ThreadLocalRandom.current().nextLong());
	}

	Random split();

	RandomSplitter nextSplitter();

	void setSeed(long seed);

	int nextInt();

	int nextInt(int bound);

	default int nextBetween(int min, int max) {
		return this.nextInt(max - min + 1) + min;
	}

	long nextLong();

	boolean nextBoolean();

	float nextFloat();

	double nextDouble();

	double nextGaussian();

	/**
	 * {@return a random {@code double} between {@code mode - deviation} and
	 * {@code mode + deviation} (both inclusive) with mode {@code mode}}
	 * 
	 * <p>Because the return value follows a symmetric triangular distribution,
	 * the distribution's mean and median are equal to {@code mode}.
	 */
	default double nextTriangular(double mode, double deviation) {
		return mode + deviation * (this.nextDouble() - this.nextDouble());
	}

	default void skip(int count) {
		for (int i = 0; i < count; i++) {
			this.nextInt();
		}
	}

	default int nextBetweenExclusive(int min, int max) {
		if (min >= max) {
			throw new IllegalArgumentException("bound - origin is non positive");
		} else {
			return min + this.nextInt(max - min);
		}
	}
}
