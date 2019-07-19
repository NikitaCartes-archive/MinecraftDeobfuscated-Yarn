package net.minecraft.world.biome.source;

public class TheEndBiomeSourceConfig implements BiomeSourceConfig {
	private long seed;

	public TheEndBiomeSourceConfig setSeed(long seed) {
		this.seed = seed;
		return this;
	}

	public long getSeed() {
		return this.seed;
	}
}
