package net.minecraft.util.math.random;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.serialization.Codec;
import net.minecraft.util.math.MathHelper;

/**
 * Xoroshiro128++ based pseudo random number generator.
 * 
 * @implNote The actual implementation can be found on {@link Xoroshiro128PlusPlusRandomImpl}.
 */
public class Xoroshiro128PlusPlusRandom implements Random {
	private static final float FLOAT_MULTIPLIER = 5.9604645E-8F;
	private static final double DOUBLE_MULTIPLIER = 1.110223E-16F;
	public static final Codec<Xoroshiro128PlusPlusRandom> CODEC = Xoroshiro128PlusPlusRandomImpl.CODEC
		.xmap(implementation -> new Xoroshiro128PlusPlusRandom(implementation), random -> random.implementation);
	private Xoroshiro128PlusPlusRandomImpl implementation;
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public Xoroshiro128PlusPlusRandom(long seed) {
		this.implementation = new Xoroshiro128PlusPlusRandomImpl(RandomSeed.createXoroshiroSeed(seed));
	}

	public Xoroshiro128PlusPlusRandom(RandomSeed.XoroshiroSeed seed) {
		this.implementation = new Xoroshiro128PlusPlusRandomImpl(seed);
	}

	public Xoroshiro128PlusPlusRandom(long seedLo, long seedHi) {
		this.implementation = new Xoroshiro128PlusPlusRandomImpl(seedLo, seedHi);
	}

	private Xoroshiro128PlusPlusRandom(Xoroshiro128PlusPlusRandomImpl implementation) {
		this.implementation = implementation;
	}

	@Override
	public Random split() {
		return new Xoroshiro128PlusPlusRandom(this.implementation.next(), this.implementation.next());
	}

	@Override
	public RandomSplitter nextSplitter() {
		return new Xoroshiro128PlusPlusRandom.Splitter(this.implementation.next(), this.implementation.next());
	}

	@Override
	public void setSeed(long seed) {
		this.implementation = new Xoroshiro128PlusPlusRandomImpl(RandomSeed.createXoroshiroSeed(seed));
		this.gaussianGenerator.reset();
	}

	@Override
	public int nextInt() {
		return (int)this.implementation.next();
	}

	@Override
	public int nextInt(int bound) {
		if (bound <= 0) {
			throw new IllegalArgumentException("Bound must be positive");
		} else {
			long l = Integer.toUnsignedLong(this.nextInt());
			long m = l * (long)bound;
			long n = m & 4294967295L;
			if (n < (long)bound) {
				for (int i = Integer.remainderUnsigned(~bound + 1, bound); n < (long)i; n = m & 4294967295L) {
					l = Integer.toUnsignedLong(this.nextInt());
					m = l * (long)bound;
				}
			}

			long o = m >> 32;
			return (int)o;
		}
	}

	@Override
	public long nextLong() {
		return this.implementation.next();
	}

	@Override
	public boolean nextBoolean() {
		return (this.implementation.next() & 1L) != 0L;
	}

	@Override
	public float nextFloat() {
		return (float)this.next(24) * 5.9604645E-8F;
	}

	@Override
	public double nextDouble() {
		return (double)this.next(53) * 1.110223E-16F;
	}

	@Override
	public double nextGaussian() {
		return this.gaussianGenerator.next();
	}

	@Override
	public void skip(int count) {
		for (int i = 0; i < count; i++) {
			this.implementation.next();
		}
	}

	/**
	 * {@return {@code bits} upper bits of random value}
	 * 
	 * @implNote In Xoroshiro128++, the lower bits have to be discarded in order
	 * to ensure proper randomness. For example, to obtain a double, the upper 53
	 * bits should be used instead of the lower 53 bits.
	 */
	private long next(int bits) {
		return this.implementation.next() >>> 64 - bits;
	}

	public static class Splitter implements RandomSplitter {
		private final long seedLo;
		private final long seedHi;

		public Splitter(long seedLo, long seedHi) {
			this.seedLo = seedLo;
			this.seedHi = seedHi;
		}

		@Override
		public Random split(int x, int y, int z) {
			long l = MathHelper.hashCode(x, y, z);
			long m = l ^ this.seedLo;
			return new Xoroshiro128PlusPlusRandom(m, this.seedHi);
		}

		@Override
		public Random split(String seed) {
			RandomSeed.XoroshiroSeed xoroshiroSeed = RandomSeed.createXoroshiroSeed(seed);
			return new Xoroshiro128PlusPlusRandom(xoroshiroSeed.split(this.seedLo, this.seedHi));
		}

		@Override
		public Random split(long seed) {
			return new Xoroshiro128PlusPlusRandom(seed ^ this.seedLo, seed ^ this.seedHi);
		}

		@VisibleForTesting
		@Override
		public void addDebugInfo(StringBuilder info) {
			info.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
		}
	}
}
