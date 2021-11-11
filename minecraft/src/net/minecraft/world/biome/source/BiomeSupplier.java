package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.source.util.MultiNoiseUtil;

public interface BiomeSupplier {
	Biome getBiome(int x, int y, int z, MultiNoiseUtil.MultiNoiseSampler noise);
}
