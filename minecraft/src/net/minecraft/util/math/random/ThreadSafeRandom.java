package net.minecraft.util.math.random;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A random that can be shared by multiple threads safely.
 */
@Deprecated
public class ThreadSafeRandom implements BaseRandom {
	private static final int INT_BITS = 48;
	private static final long SEED_MASK = 281474976710655L;
	private static final long MULTIPLIER = 25214903917L;
	private static final long INCREMENT = 11L;
	private final AtomicLong seed = new AtomicLong();
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public ThreadSafeRandom(long seed) {
		this.setSeed(seed);
	}

	@Override
	public Random split() {
		return new ThreadSafeRandom(this.nextLong());
	}

	@Override
	public RandomSplitter nextSplitter() {
		return new CheckedRandom.Splitter(this.nextLong());
	}

	@Override
	public void setSeed(long seed) {
		this.seed.set((seed ^ 25214903917L) & 281474976710655L);
	}

	@Override
	public int next(int bits) {
		long l;
		long m;
		do {
			l = this.seed.get();
			m = l * 25214903917L + 11L & 281474976710655L;
		} while (!this.seed.compareAndSet(l, m));

		return (int)(m >>> 48 - bits);
	}

	@Override
	public double nextGaussian() {
		return this.gaussianGenerator.next();
	}
}
