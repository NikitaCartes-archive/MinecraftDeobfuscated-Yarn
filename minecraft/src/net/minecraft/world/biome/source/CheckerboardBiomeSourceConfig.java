package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class CheckerboardBiomeSourceConfig implements BiomeSourceConfig {
	private Biome[] field_9483 = new Biome[]{Biomes.field_9451};
	private int field_9482 = 1;

	public CheckerboardBiomeSourceConfig method_8777(Biome[] biomes) {
		this.field_9483 = biomes;
		return this;
	}

	public CheckerboardBiomeSourceConfig method_8780(int i) {
		this.field_9482 = i;
		return this;
	}

	public Biome[] method_8779() {
		return this.field_9483;
	}

	public int method_8778() {
		return this.field_9482;
	}
}
