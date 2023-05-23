package net.minecraft.util.math.random;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Charsets;
import com.google.common.hash.HashFunction;
import com.google.common.hash.Hashing;
import com.google.common.primitives.Longs;
import java.util.concurrent.atomic.AtomicLong;

public final class RandomSeed {
	public static final long GOLDEN_RATIO_64 = -7046029254386353131L;
	public static final long SILVER_RATIO_64 = 7640891576956012809L;
	private static final HashFunction MD5_HASH = Hashing.md5();
	private static final AtomicLong SEED_UNIQUIFIER = new AtomicLong(8682522807148012L);

	@VisibleForTesting
	public static long mixStafford13(long seed) {
		seed = (seed ^ seed >>> 30) * -4658895280553007687L;
		seed = (seed ^ seed >>> 27) * -7723592293110705685L;
		return seed ^ seed >>> 31;
	}

	public static RandomSeed.XoroshiroSeed createUnmixedXoroshiroSeed(long seed) {
		long l = seed ^ 7640891576956012809L;
		long m = l + -7046029254386353131L;
		return new RandomSeed.XoroshiroSeed(l, m);
	}

	public static RandomSeed.XoroshiroSeed createXoroshiroSeed(long seed) {
		return createUnmixedXoroshiroSeed(seed).mix();
	}

	public static RandomSeed.XoroshiroSeed createXoroshiroSeed(String seed) {
		byte[] bs = MD5_HASH.hashString(seed, Charsets.UTF_8).asBytes();
		long l = Longs.fromBytes(bs[0], bs[1], bs[2], bs[3], bs[4], bs[5], bs[6], bs[7]);
		long m = Longs.fromBytes(bs[8], bs[9], bs[10], bs[11], bs[12], bs[13], bs[14], bs[15]);
		return new RandomSeed.XoroshiroSeed(l, m);
	}

	/**
	 * {@return the seed calculated using {@link SEED_UNIQUIFIER} and the system time}
	 */
	public static long getSeed() {
		return SEED_UNIQUIFIER.updateAndGet(seedUniquifier -> seedUniquifier * 1181783497276652981L) ^ System.nanoTime();
	}

	public static record XoroshiroSeed(long seedLo, long seedHi) {
		public RandomSeed.XoroshiroSeed split(long seedLo, long seedHi) {
			return new RandomSeed.XoroshiroSeed(this.seedLo ^ seedLo, this.seedHi ^ seedHi);
		}

		public RandomSeed.XoroshiroSeed split(RandomSeed.XoroshiroSeed seed) {
			return this.split(seed.seedLo, seed.seedHi);
		}

		public RandomSeed.XoroshiroSeed mix() {
			return new RandomSeed.XoroshiroSeed(RandomSeed.mixStafford13(this.seedLo), RandomSeed.mixStafford13(this.seedHi));
		}
	}
}
