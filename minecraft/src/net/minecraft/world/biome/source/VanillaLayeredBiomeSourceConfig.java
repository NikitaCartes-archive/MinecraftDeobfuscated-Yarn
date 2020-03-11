package net.minecraft.world.biome.source;

import net.minecraft.world.gen.chunk.OverworldChunkGeneratorConfig;
import net.minecraft.world.level.LevelGeneratorType;

public class VanillaLayeredBiomeSourceConfig implements BiomeSourceConfig {
	private final long seed;
	private LevelGeneratorType generatorType = LevelGeneratorType.DEFAULT;
	private OverworldChunkGeneratorConfig generatorConfig = new OverworldChunkGeneratorConfig();

	public VanillaLayeredBiomeSourceConfig(long seed) {
		this.seed = seed;
	}

	public VanillaLayeredBiomeSourceConfig setGeneratorType(LevelGeneratorType generatorType) {
		this.generatorType = generatorType;
		return this;
	}

	public VanillaLayeredBiomeSourceConfig setGeneratorConfig(OverworldChunkGeneratorConfig generatorConfig) {
		this.generatorConfig = generatorConfig;
		return this;
	}

	public long getSeed() {
		return this.seed;
	}

	public LevelGeneratorType getGeneratorType() {
		return this.generatorType;
	}

	public OverworldChunkGeneratorConfig getGeneratorConfig() {
		return this.generatorConfig;
	}
}
