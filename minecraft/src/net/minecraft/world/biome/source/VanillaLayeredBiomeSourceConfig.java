package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private final long seed;
	private final LevelGeneratorType generatorType;
	private OverworldChunkGeneratorConfig generatorSettings = new OverworldChunkGeneratorConfig();

	public VanillaLayeredBiomeSourceConfig(LevelProperties levelProperties) {
		this.seed = levelProperties.getSeed();
		this.generatorType = levelProperties.getGeneratorType();
	}

	public VanillaLayeredBiomeSourceConfig setGeneratorSettings(OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		this.generatorSettings = overworldChunkGeneratorConfig;
		return this;
	}

	public long getSeed() {
		return this.seed;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}

	public OverworldChunkGeneratorConfig getGeneratorSettings() {
		return this.generatorSettings;
	}
}
