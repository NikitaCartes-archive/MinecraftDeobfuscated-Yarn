package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public enum DirectBiomeAccessType implements BiomeAccessType {
	INSTANCE;

	@Override
	public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
		return storage.getBiomeForNoiseGen(BiomeCoords.fromBlock(x), BiomeCoords.fromBlock(y), BiomeCoords.fromBlock(z));
	}
}
