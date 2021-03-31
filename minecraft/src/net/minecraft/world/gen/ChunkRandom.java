package net.minecraft.world.gen;

import java.util.Random;

public class ChunkRandom extends Random implements WorldGenRandom {
	private int sampleCount;

	public ChunkRandom() {
	}

	public ChunkRandom(long seed) {
		super(seed);
	}

	public int method_35335() {
		return this.sampleCount;
	}

	public int next(int count) {
		this.sampleCount++;
		return super.next(count);
	}

	/**
	 * Seeds the randomizer to generate the surface terrain blocks (such as grass, sand, etc.)
	 * and the bedrock patterns.
	 * 
	 * <p>Note that the terrain seed does not depend on the world seed and only gets affected by
	 * chunk coordinates.</p>
	 */
	public long setTerrainSeed(int chunkX, int chunkZ) {
		long l = (long)chunkX * 341873128712L + (long)chunkZ * 132897987541L;
		this.setSeed(l);
		return l;
	}

	/**
	 * Seeds the randomizer to create population features such as decorators and animals.
	 * 
	 * <p>This method takes in the world seed and the negative-most block coordinates of the
	 * chunk. The coordinate pair provided is equivalent to (chunkX * 16, chunkZ * 16). The
	 * three values are mixed together through some layers of hashing to produce the
	 * population seed.</p>
	 * 
	 * <p>This function has been proved to be reversible through some exploitation of the underlying
	 * nextLong() weaknesses. It is also important to remember that since setSeed()
	 * truncates the 16 upper bits of world seed, only the 48 lowest bits affect the population
	 * seed output.</p>
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
	 * @param populationSeed The population seed computed in setPopulationSeed().
	 * @param index The index of the feature in the feature list.
	 * @param step The generation step's ordinal for this feature.
	 */
	public long setDecoratorSeed(long populationSeed, int index, int step) {
		long l = populationSeed + (long)index + (long)(10000 * step);
		this.setSeed(l);
		return l;
	}

	/**
	 * Seeds the randomizer to generate larger features such as caves, ravines, mineshafts
	 * and strongholds. It is also used to initiate structure start behavior such as rotation.
	 * 
	 * <p>Similar to the population seed, only the 48 lowest bits of the world seed affect the
	 * output since it the upper 16 bits are truncated in the setSeed() call.</p>
	 */
	public long setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
		this.setSeed(worldSeed);
		long l = this.nextLong();
		long m = this.nextLong();
		long n = (long)chunkX * l ^ (long)chunkZ * m ^ worldSeed;
		this.setSeed(n);
		return n;
	}

	public long setGrimstoneSeed(long worldSeed, int x, int y, int z) {
		this.setSeed(worldSeed);
		long l = this.nextLong();
		long m = this.nextLong();
		long n = this.nextLong();
		long o = (long)x * l ^ (long)y * m ^ (long)z * n ^ worldSeed;
		this.setSeed(o);
		return o;
	}

	/**
	 * Seeds the randomizer to determine the start position of structure features such as
	 * temples, monuments and buried treasures within a region.
	 * 
	 * <p>The region coordinates pair corresponds to the coordinates of the region the seeded
	 * chunk lies in. For example, a swamp hut region is 32 by 32 chunks meaning that all
	 * chunks that lie within that region get seeded the same way.</p>
	 * 
	 * <p>Similarly, the upper 16 bits of world seed also do not affect the region seed because
	 * they get truncated in the setSeed() call.</p>
	 */
	public long setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
		long l = (long)regionX * 341873128712L + (long)regionZ * 132897987541L + worldSeed + (long)salt;
		this.setSeed(l);
		return l;
	}

	public static Random getSlimeRandom(int chunkX, int chunkZ, long worldSeed, long scrambler) {
		return new Random(
			worldSeed + (long)(chunkX * chunkX * 4987142) + (long)(chunkX * 5947611) + (long)(chunkZ * chunkZ) * 4392871L + (long)(chunkZ * 389711) ^ scrambler
		);
	}
}
