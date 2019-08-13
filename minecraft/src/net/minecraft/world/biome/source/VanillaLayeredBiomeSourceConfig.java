package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private LevelProperties levelProperties;
	private OverworldChunkGeneratorConfig generatorSettings;

	public VanillaLayeredBiomeSourceConfig setLevelProperties(LevelProperties levelProperties) {
		this.levelProperties = levelProperties;
		return this;
	}

	public VanillaLayeredBiomeSourceConfig setGeneratorSettings(OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		this.generatorSettings = overworldChunkGeneratorConfig;
		return this;
	}

	public LevelProperties getLevelProperties() {
		return this.levelProperties;
	}

	public OverworldChunkGeneratorConfig getGeneratorSettings() {
		return this.generatorSettings;
	}
}
