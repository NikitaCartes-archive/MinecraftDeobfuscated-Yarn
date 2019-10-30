package net.minecraft.world.biome;

public interface BiomeAccessType {
	Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage);
}
