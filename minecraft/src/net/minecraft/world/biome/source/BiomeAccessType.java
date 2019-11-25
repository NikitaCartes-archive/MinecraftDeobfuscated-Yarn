package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;

public interface BiomeAccessType {
	Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage);
}
