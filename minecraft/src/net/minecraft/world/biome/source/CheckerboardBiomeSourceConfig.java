package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.level.LevelProperties;

public class CheckerboardBiomeSourceConfig implements BiomeSourceConfig {
	private Biome[] biomes = new Biome[]{Biomes.PLAINS};
	private int size = 1;

	public CheckerboardBiomeSourceConfig(LevelProperties levelProperties) {
	}

	public CheckerboardBiomeSourceConfig setBiomes(Biome[] biomes) {
		this.biomes = biomes;
		return this;
	}

	public CheckerboardBiomeSourceConfig setSize(int i) {
		this.size = i;
		return this;
	}

	public Biome[] getBiomes() {
		return this.biomes;
	}

	public int getSize() {
		return this.size;
	}
}
