package net.minecraft.world.gen.carver;

import java.util.BitSet;
import java.util.Random;
import net.minecraft.world.chunk.Chunk;

public class ConfiguredCarver<WC extends CarverConfig> {
	public final Carver<WC> carver;
	public final WC config;

	public ConfiguredCarver(Carver<WC> carver, WC config) {
		this.carver = carver;
		this.config = config;
	}

	public boolean shouldCarve(Random random, int chunkX, int chunkZ) {
		return this.carver.shouldCarve(random, chunkX, chunkZ, this.config);
	}

	public boolean carve(Chunk chunk, Random random, int seaLevel, int chunkX, int chunkZ, int mainChunkX, int mainChunkY, BitSet mask) {
		return this.carver.carve(chunk, random, seaLevel, chunkX, chunkZ, mainChunkX, mainChunkY, mask, this.config);
	}
}
