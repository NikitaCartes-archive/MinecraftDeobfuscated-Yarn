package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private LevelProperties levelProperties;
	private OverworldChunkGeneratorConfig field_9669;

	public VanillaLayeredBiomeSourceConfig setLevelProperties(LevelProperties levelProperties) {
		this.levelProperties = levelProperties;
		return this;
	}

	public VanillaLayeredBiomeSourceConfig method_9004(OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		this.field_9669 = overworldChunkGeneratorConfig;
		return this;
	}

	public LevelProperties getLevelProperties() {
		return this.levelProperties;
	}

	public OverworldChunkGeneratorConfig method_9005() {
		return this.field_9669;
	}
}
