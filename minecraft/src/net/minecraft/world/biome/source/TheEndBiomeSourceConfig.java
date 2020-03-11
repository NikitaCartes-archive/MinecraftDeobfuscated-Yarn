package net.minecraft.world.biome.source;

public class TheEndBiomeSourceConfig implements BiomeSourceConfig {
	private final long seed;

	public TheEndBiomeSourceConfig(long l) {
		this.seed = l;
	}

	public long getSeed() {
		return this.seed;
	}
}
