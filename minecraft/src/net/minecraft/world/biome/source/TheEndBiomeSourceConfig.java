package net.minecraft.world.biome.source;

public class TheEndBiomeSourceConfig implements BiomeSourceConfig {
	private long seed;

	public TheEndBiomeSourceConfig setSeed(long l) {
		this.seed = l;
		return this;
	}

	public long getSeed() {
		return this.seed;
	}
}
