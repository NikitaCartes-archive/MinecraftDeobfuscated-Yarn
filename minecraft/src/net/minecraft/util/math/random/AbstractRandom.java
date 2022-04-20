package net.minecraft.util.math.random;

import io.netty.util.internal.ThreadLocalRandom;

/**
 * A reimplementation of {@link java.util.Random}.
 * 
 * <p>There are four built-in implementations, three based on the classic Java algorithm
 * and one using Xoroshiro128++ algorithm.
 * 
 * <ul>
 * <li>{@link SimpleRandom}: Silently breaks when used concurrently. Based on
 * {@link java.util.Random}.</li>
 * <li>{@link AtomicSimpleRandom}: Throws when used concurrently. Based on
 * {@link java.util.Random}.</li>
 * <li>{@link BlockingSimpleRandom}: Blocks the thread when used concurrently.. Based on
 * {@link java.util.Random}.</li>
 * <li>{@link Xoroshiro128PlusPlusRandom}: Silently breaks when used concurrently.
 * Implements the Xoroshiro128++ algorithm.</li>
 * </ul>
 * 
 * @implNote Note that due to MC-239059, this is not an exact reimplementation of
 * the Java random number generator algorithm.
 */
public interface AbstractRandom {
	@Deprecated
	double field_38930 = 2.297;

	static AbstractRandom createAtomic() {
		return createAtomic(System.nanoTime());
	}

	@Deprecated
	static AbstractRandom createBlocking() {
		return new BlockingSimpleRandom(System.nanoTime());
	}

	static AbstractRandom createAtomic(long seed) {
		return new AtomicSimpleRandom(seed);
	}

	static AbstractRandom create() {
		return new SimpleRandom(ThreadLocalRandom.current().nextLong());
	}

	AbstractRandom derive();

	RandomDeriver createRandomDeriver();

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

	default double nextPredictable(double base, double variance) {
		return base + variance * (this.nextDouble() - this.nextDouble());
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
