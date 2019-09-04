package net.minecraft.world.biome.source;

import com.google.common.collect.ImmutableSet;
import net.minecraft.world.biome.Biome;

public class CheckerboardBiomeSource extends BiomeSource {
	private final Biome[] biomes;
	private final int gridSize;

	public CheckerboardBiomeSource(CheckerboardBiomeSourceConfig checkerboardBiomeSourceConfig) {
		super(ImmutableSet.copyOf(checkerboardBiomeSourceConfig.getBiomes()));
		this.biomes = checkerboardBiomeSourceConfig.getBiomes();
		this.gridSize = checkerboardBiomeSourceConfig.getSize() + 2;
	}

	@Override
	public Biome getBiome(int i, int j, int k) {
		return this.biomes[Math.abs(((i >> this.gridSize) + (k >> this.gridSize)) % this.biomes.length)];
	}
}
