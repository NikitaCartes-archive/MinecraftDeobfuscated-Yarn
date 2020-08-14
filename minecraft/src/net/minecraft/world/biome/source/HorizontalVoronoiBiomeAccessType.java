package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public enum HorizontalVoronoiBiomeAccessType implements BiomeAccessType {
	INSTANCE;

	@Override
	public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
		return VoronoiBiomeAccessType.INSTANCE.getBiome(seed, x, 0, z, storage);
	}
}
