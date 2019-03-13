package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelProperties;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private LevelProperties field_9670;
	private OverworldChunkGeneratorConfig field_9669;

	public VanillaLayeredBiomeSourceConfig method_9002(LevelProperties levelProperties) {
		this.field_9670 = levelProperties;
		return this;
	}

	public VanillaLayeredBiomeSourceConfig method_9004(OverworldChunkGeneratorConfig overworldChunkGeneratorConfig) {
		this.field_9669 = overworldChunkGeneratorConfig;
		return this;
	}

	public LevelProperties method_9003() {
		return this.field_9670;
	}

	public OverworldChunkGeneratorConfig method_9005() {
		return this.field_9669;
	}
}
