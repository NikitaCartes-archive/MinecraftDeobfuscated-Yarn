package net.minecraft.util.math.random;

import com.mojang.serialization.Codec;
import java.util.stream.LongStream;
import net.minecraft.util.Util;

/**
 * Implementation of Xoroshiro128++ pseudo-random number generator.
 * 
 * @apiNote For regular usage, {@link Xoroshiro128PlusPlusRandom} should be used instead.
 * 
 * @see <a href="https://prng.di.unimi.it/xoroshiro128plusplus.c">Official
 * implementation released under public domain</a>
 */
public class Xoroshiro128PlusPlusRandomImpl {
	private long seedLo;
	private long seedHi;
	public static final Codec<Xoroshiro128PlusPlusRandomImpl> CODEC = Codec.LONG_STREAM
		.comapFlatMap(
			stream -> Util.decodeFixedLengthArray(stream, 2).map(seeds -> new Xoroshiro128PlusPlusRandomImpl(seeds[0], seeds[1])),
			random -> LongStream.of(new long[]{random.seedLo, random.seedHi})
		);

	public Xoroshiro128PlusPlusRandomImpl(RandomSeed.XoroshiroSeed seed) {
		this(seed.seedLo(), seed.seedHi());
	}

	public Xoroshiro128PlusPlusRandomImpl(long seedLo, long seedHi) {
		this.seedLo = seedLo;
		this.seedHi = seedHi;
		if ((this.seedLo | this.seedHi) == 0L) {
			this.seedLo = -7046029254386353131L;
			this.seedHi = 7640891576956012809L;
		}
	}

	public long next() {
		long l = this.seedLo;
		long m = this.seedHi;
		long n = Long.rotateLeft(l + m, 17) + l;
		m ^= l;
		this.seedLo = Long.rotateLeft(l, 49) ^ m ^ m << 21;
		this.seedHi = Long.rotateLeft(m, 28);
		return n;
	}
}
