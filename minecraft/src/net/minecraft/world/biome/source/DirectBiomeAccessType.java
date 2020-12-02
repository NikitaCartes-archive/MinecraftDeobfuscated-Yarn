package net.minecraft.world.biome.source;

import net.minecraft.class_5742;
import net.minecraft.world.biome.Biome;

public enum DirectBiomeAccessType implements BiomeAccessType {
	INSTANCE;

	@Override
	public Biome getBiome(long seed, int x, int y, int z, BiomeAccess.Storage storage) {
		return storage.getBiomeForNoiseGen(class_5742.method_33100(x), class_5742.method_33100(y), class_5742.method_33100(z));
	}
}
