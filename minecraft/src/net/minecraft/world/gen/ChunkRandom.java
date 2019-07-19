package net.minecraft.world.gen;

import java.util.Random;

public class ChunkRandom extends Random {
	private int sampleCount;

	public ChunkRandom() {
	}

	public ChunkRandom(long seed) {
		super(seed);
	}

	public void consume(int count) {
		for (int i = 0; i < count; i++) {
			this.next(1);
		}
	}

	protected int next(int bound) {
		this.sampleCount++;
		return super.next(bound);
	}

	public long setSeed(int x, int z) {
		long l = (long)x * 341873128712L + (long)z * 132897987541L;
		this.setSeed(l);
		return l;
	}

	public long setSeed(long worldSeed, int x, int z) {
		this.setSeed(worldSeed);
		long l = this.nextLong() | 1L;
		long m = this.nextLong() | 1L;
		long n = (long)x * l + (long)z * m ^ worldSeed;
		this.setSeed(n);
		return n;
	}

	public long setFeatureSeed(long worldSeed, int index, int step) {
		long l = worldSeed + (long)index + (long)(10000 * step);
		this.setSeed(l);
		return l;
	}

	public long setStructureSeed(long worldSeed, int x, int z) {
		this.setSeed(worldSeed);
		long l = this.nextLong();
		long m = this.nextLong();
		long n = (long)x * l ^ (long)z * m ^ worldSeed;
		this.setSeed(n);
		return n;
	}

	public long setStructureSeed(long worldSeed, int x, int z, int seedModifier) {
		long l = (long)x * 341873128712L + (long)z * 132897987541L + worldSeed + (long)seedModifier;
		this.setSeed(l);
		return l;
	}

	public static Random create(int x, int z, long worldSeed, long localSeed) {
		return new Random(worldSeed + (long)(x * x * 4987142) + (long)(x * 5947611) + (long)(z * z) * 4392871L + (long)(z * 389711) ^ localSeed);
	}
}
