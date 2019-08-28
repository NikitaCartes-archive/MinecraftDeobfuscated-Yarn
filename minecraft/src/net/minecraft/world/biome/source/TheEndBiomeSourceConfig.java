package net.minecraft.world.biome.source;

import net.minecraft.world.level.LevelProperties;

public class TheEndBiomeSourceConfig implements BiomeSourceConfig {
	private final long seed;

	public TheEndBiomeSourceConfig(LevelProperties levelProperties) {
		this.seed = levelProperties.getSeed();
	}

	public long getSeed() {
		return this.seed;
	}
}
