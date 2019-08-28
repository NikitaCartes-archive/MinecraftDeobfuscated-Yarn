package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private final long field_20619;
	private final LevelGeneratorType field_20620;
	private OverworldChunkGeneratorConfig generatorSettings = new OverworldChunkGeneratorConfig();

	public VanillaLayeredBiomeSourceConfig(LevelProperties levelProperties) {
		this.field_20619 = levelProperties.getSeed();
		this.field_20620 = levelProperties.getGeneratorType();
	}

	public VanillaLayeredBiomeSourceConfig setGeneratorSettings(OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		this.generatorSettings = overworldChunkGeneratorConfig;
		return this;
	}

	public long method_22355() {
		return this.field_20619;
	}

	public LevelGeneratorType method_22356() {
		return this.field_20620;
	}

	public OverworldChunkGeneratorConfig getGeneratorSettings() {
		return this.generatorSettings;
	}
}
