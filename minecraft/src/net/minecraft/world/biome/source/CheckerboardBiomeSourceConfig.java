package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class CheckerboardBiomeSourceConfig implements BiomeSourceConfig {
	private Biome[] biomes = new Biome[]{Biomes.field_9451};
	private int size = 1;

	public CheckerboardBiomeSourceConfig method_8777(Biome[] biomes) {
		this.biomes = biomes;
		return this;
	}

	public CheckerboardBiomeSourceConfig method_8780(int i) {
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
