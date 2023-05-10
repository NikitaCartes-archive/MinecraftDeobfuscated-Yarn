package net.minecraft.util.math.random;

import java.util.function.LongFunction;

public class ChunkRandom extends CheckedRandom {
	private final Random baseRandom;
	private int sampleCount;

	public ChunkRandom(Random baseRandom) {
		super(0L);
		this.baseRandom = baseRandom;
	}

	public int getSampleCount() {
		return this.sampleCount;
	}

	@Override
	public Random split() {
		return this.baseRandom.split();
	}

	@Override
	public RandomSplitter nextSplitter() {
		return this.baseRandom.nextSplitter();
	}

	@Override
	public int next(int bits) {
		this.sampleCount++;
		return this.baseRandom instanceof CheckedRandom checkedRandom ? checkedRandom.next(bits) : (int)(this.baseRandom.nextLong() >>> 64 - bits);
	}

	@Override
	public synchronized void setSeed(long seed) {
		if (this.baseRandom != null) {
			this.baseRandom.setSeed(seed);
		}
	}

	/**
	 * Seeds the randomizer to create population features such as decorators and animals.
	 * 
	 * <p>This method takes in the world seed and the negative-most block coordinates of the
	 * chunk. The coordinate pair provided is equivalent to (chunkX * 16, chunkZ * 16). The
	 * three values are mixed together through some layers of hashing to produce the
	 * population seed.
	 * 
	 * <p>This function has been proved to be reversible through some exploitation of the underlying
	 * nextLong() weaknesses. It is also important to remember that since setSeed()
	 * truncates the 16 upper bits of world seed, only the 48 lowest bits affect the population
	 * seed output.
	 */
	public long setPopulationSeed(long worldSeed, int blockX, int blockZ) {
		this.setSeed(worldSeed);
		long l = this.nextLong() | 1L;
		long m = this.nextLong() | 1L;
		long n = (long)blockX * l + (long)blockZ * m ^ worldSeed;
		this.setSeed(n);
		return n;
	}

	/**
	 * Seeds the randomizer to generate a given feature.
	 * 
	 * The salt, in the form of {@code index + 10000 * step} assures that each feature is seeded
	 * differently, making the decoration feel more random. Even though it does a good job
	 * at doing so, many entropy issues arise from the salt being so small and result in
	 * weird alignments between features that have an index close apart.
	 * 
	 * @param index the index of the feature in the feature list
	 * @param step the generation step's ordinal for this feature
	 * @param populationSeed the population seed computed in {@link #setPopulationSeed(long, int, int)}
	 */
	public void setDecoratorSeed(long populationSeed, int index, int step) {
		long l = populationSeed + (long)index + (long)(10000 * step);
		this.setSeed(l);
	}

	/**
	 * Seeds the randomizer to generate larger features such as caves, ravines, mineshafts
	 * and strongholds. It is also used to initiate structure start behavior such as rotation.
	 * 
	 * <p>Similar to the population seed, only the 48 lowest bits of the world seed affect the
	 * output since it the upper 16 bits are truncated in the setSeed() call.
	 */
	public void setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
		this.setSeed(worldSeed);
		long l = this.nextLong();
		long m = this.nextLong();
		long n = (long)chunkX * l ^ (long)chunkZ * m ^ worldSeed;
		this.setSeed(n);
	}

	/**
	 * Seeds the randomizer to determine the start position of structure features such as
	 * temples, monuments and buried treasures within a region.
	 * 
	 * <p>The region coordinates pair corresponds to the coordinates of the region the seeded
	 * chunk lies in. For example, a swamp hut region is 32 by 32 chunks meaning that all
	 * chunks that lie within that region get seeded the same way.
	 * 
	 * <p>Similarly, the upper 16 bits of world seed also do not affect the region seed because
	 * they get truncated in the setSeed() call.
	 */
	public void setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
		long l = (long)regionX * 341873128712L + (long)regionZ * 132897987541L + worldSeed + (long)salt;
		this.setSeed(l);
	}

	public static Random getSlimeRandom(int chunkX, int chunkZ, long worldSeed, long scrambler) {
		return Random.create(
			worldSeed + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611) + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ scrambler
		);
	}

	public static enum RandomProvider {
		LEGACY(CheckedRandom::new),
		XOROSHIRO(Xoroshiro128PlusPlusRandom::new);

		private final LongFunction<Random> provider;

		private RandomProvider(LongFunction<Random> provider) {
			this.provider = provider;
		}

		public Random create(long seed) {
			return (Random)this.provider.apply(seed);
		}
	}
}
