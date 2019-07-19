package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private LevelProperties levelProperties;
	private OverworldChunkGeneratorConfig generatorSettings;

	public VanillaLayeredBiomeSourceConfig setLevelProperties(LevelProperties properties) {
		this.levelProperties = properties;
		return this;
	}

	public VanillaLayeredBiomeSourceConfig setGeneratorSettings(OverworldChunkGeneratorConfig generatorSettings) {
		this.generatorSettings = generatorSettings;
		return this;
	}

	public LevelProperties getLevelProperties() {
		return this.levelProperties;
	}

	public OverworldChunkGeneratorConfig getGeneratorSettings() {
		return this.generatorSettings;
	}
}
