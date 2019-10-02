package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
	private final BiomeAccess.Storage storage;
	private final long seed;
	private final BiomeAccessType type;

	public BiomeAccess(BiomeAccess.Storage storage, long l, BiomeAccessType biomeAccessType) {
		this.storage = storage;
		this.seed = l;
		this.type = biomeAccessType;
	}

	public BiomeAccess withSource(BiomeSource biomeSource) {
		return new BiomeAccess(biomeSource, this.seed, this.type);
	}

	public Biome getBiome(BlockPos blockPos) {
		return this.type.getBiome(this.seed, blockPos.getX(), blockPos.getY(), blockPos.getZ(), this.storage);
	}

	public interface Storage {
		Biome getStoredBiome(int i, int j, int k);
	}
}
