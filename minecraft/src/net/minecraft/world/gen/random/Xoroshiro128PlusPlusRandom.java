package net.minecraft.world.gen.random;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import net.minecraft.util.math.MathHelper;

/**
 * Xoroshiro128++ based pseudo random number generator.
 * 
 * @implNote The actual implementation can be found on {@link Xoroshiro128PlusPlusRandomImpl}.
 */
public class Xoroshiro128PlusPlusRandom implements AbstractRandom {
	private static final float FLOAT_MULTIPLIER = 5.9604645E-8F;
	private static final double DOUBLE_MULTIPLIER = 1.110223E-16F;
	private Xoroshiro128PlusPlusRandomImpl implementation;
	private final GaussianGenerator gaussianGenerator = new GaussianGenerator(this);

	public Xoroshiro128PlusPlusRandom(long seed) {
		this.setSeed(seed);
	}

	public Xoroshiro128PlusPlusRandom(long seedLo, long seedHi) {
		this.implementation = new Xoroshiro128PlusPlusRandomImpl(seedLo, seedHi);
	}

	@Override
	public AbstractRandom derive() {
		return new Xoroshiro128PlusPlusRandom(this.implementation.next(), this.implementation.next());
	}

	@Override
	public net.minecraft.world.gen.random.RandomDeriver createRandomDeriver() {
		return new Xoroshiro128PlusPlusRandom.RandomDeriver(this.implementation.next(), this.implementation.next());
	}

	@Override
	public void setSeed(long l) {
		this.implementation = new Xoroshiro128PlusPlusRandomImpl(RandomSeed.createXoroshiroSeed(l));
	}

	@Override
	public int nextInt() {
		return (int)this.implementation.next();
	}

	@Override
	public int nextInt(int i) {
		if (i <= 0) {
			throw new IllegalArgumentException("Bound must be positive");
		} else {
			long l = Integer.toUnsignedLong(this.nextInt());
			long m = l * (long)i;
			long n = m & 4294967295L;
			if (n < (long)i) {
				for (int j = Integer.remainderUnsigned(~i + 1, i); n < (long)j; n = m & 4294967295L) {
					l = Integer.toUnsignedLong(this.nextInt());
					m = l * (long)i;
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
	 * bits, instead of the lower 53 bits.
	 */
	private long next(int bits) {
		return this.implementation.next() >>> 64 - bits;
	}

	public static class RandomDeriver implements net.minecraft.world.gen.random.RandomDeriver {
		private static final HashFunction MD5_HASHER = Hashing.md5();
		private final long seedLo;
		private final long seedHi;

		public RandomDeriver(long seedLo, long seedHi) {
			this.seedLo = seedLo;
			this.seedHi = seedHi;
		}

		@Override
		public AbstractRandom createRandom(int x, int y, int z) {
			long l = MathHelper.hashCode(x, y, z);
			long m = l ^ this.seedLo;
			return new Xoroshiro128PlusPlusRandom(m, this.seedHi);
		}

		@Override
		public AbstractRandom createRandom(String string) {
			byte[] bs = MD5_HASHER.hashString(string, Charsets.UTF_8).asBytes();
			long l = Longs.fromBytes(bs[0], bs[1], bs[2], bs[3], bs[4], bs[5], bs[6], bs[7]);
			long m = Longs.fromBytes(bs[8], bs[9], bs[10], bs[11], bs[12], bs[13], bs[14], bs[15]);
			return new Xoroshiro128PlusPlusRandom(l ^ this.seedLo, m ^ this.seedHi);
		}

		@VisibleForTesting
		@Override
		public void addDebugInfo(StringBuilder info) {
			info.append("seedLo: ").append(this.seedLo).append(", seedHi: ").append(this.seedHi);
		}
	}
}
