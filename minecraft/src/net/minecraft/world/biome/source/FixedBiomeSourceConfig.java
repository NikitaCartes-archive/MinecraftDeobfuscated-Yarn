package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class FixedBiomeSourceConfig implements BiomeSourceConfig {
	private Biome biome = Biomes.field_9451;

	public FixedBiomeSourceConfig setBiome(Biome biome) {
		this.biome = biome;
		return this;
	}

	public Biome getBiome() {
		return this.biome;
	}
}
