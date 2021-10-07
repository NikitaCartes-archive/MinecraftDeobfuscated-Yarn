package net.minecraft.world.gen.random;

import com.google.common.annotations.VisibleForTesting;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSeed {
	public static final long XOROSHIRO64_SEED_LO_FALLBACK = -7046029254386353131L;
	public static final long XOROSHIRO64_SEED_HI_FALLBACK = 7640891576956012809L;
	private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

	@VisibleForTesting
	public static long nextSplitMix64Int(long seed) {
		seed = (seed ^ seed >>> 30) * -4658895280553007687L;
		seed = (seed ^ seed >>> 27) * -7723592293110705685L;
		return seed ^ seed >>> 31;
	}

	public static RandomSeed.XoroshiroSeed createXoroshiroSeed(long seed) {
		long l = seed ^ 7640891576956012809L;
		long m = l + -7046029254386353131L;
		return new RandomSeed.XoroshiroSeed(nextSplitMix64Int(l), nextSplitMix64Int(m));
	}

	/**
	 * {@return the seed calculated using {@link SEED_UNIQUIFIER} and the system time}
	 */
	public static long getSeed() {
		return SEED_UNIQUIFIER.updateAndGet(seedUniquifier -> seedUniquifier * 1181783497276652981L) ^ System.nanoTime();
	}

	public static record XoroshiroSeed() {
		private final long seedLo;
		private final long seedHi;

		public XoroshiroSeed(long l, long m) {
			this.seedLo = l;
			this.seedHi = m;
		}
	}
}
