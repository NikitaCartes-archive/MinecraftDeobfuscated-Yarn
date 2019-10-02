package net.minecraft.world.biome;

public enum HorizontalVoronoiBiomeAccessType implements BiomeAccessType {
	INSTANCE;

	@Override
	public Biome getBiome(long l, int i, int j, int k, BiomeAccess.Storage storage) {
		return VoronoiBiomeAccessType.INSTANCE.getBiome(l, i, 0, k, storage);
	}
}
