package net.minecraft.world.biome.source;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class FixedBiomeSourceConfig implements BiomeSourceConfig {
	private Biome field_9485 = Biomes.field_9451;

	public FixedBiomeSourceConfig method_8782(Biome biome) {
		this.field_9485 = biome;
		return this;
	}

	public Biome method_8781() {
		return this.field_9485;
	}
}
