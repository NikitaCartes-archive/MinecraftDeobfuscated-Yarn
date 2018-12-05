package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorSettings;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private LevelProperties levelProperties;
	private OverworldChunkGeneratorSettings generatorSettings;

	public VanillaLayeredBiomeSourceConfig setLevelProperties(LevelProperties levelProperties) {
		this.levelProperties = levelProperties;
		return this;
	}

	public VanillaLayeredBiomeSourceConfig setGeneratorSettings(OverworldChunkGeneratorSettings overworldChunkGeneratorSettings) {
		this.generatorSettings = overworldChunkGeneratorSettings;
		return this;
	}

	public LevelProperties getLevelProperties() {
		return this.levelProperties;
	}

	public OverworldChunkGeneratorSettings getGeneratorSettings() {
		return this.generatorSettings;
	}
}
