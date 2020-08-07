package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public enum DirectBiomeAccessType implements BiomeAccessType {
	field_24409;

	@Override
	public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
		return storage.getBiomeForNoiseGen(x >> 2, y >> 2, z >> 2);
	}
}
