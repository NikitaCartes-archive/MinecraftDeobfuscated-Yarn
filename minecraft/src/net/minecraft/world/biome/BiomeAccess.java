package net.minecraft.world.biome;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.source.BiomeSource;

public class BiomeAccess {
	private final BiomeAccess.Storage storage;
	private final long seed;
	private final BiomeAccessType type;

	public BiomeAccess(BiomeAccess.Storage storage, long seed, BiomeAccessType type) {
		this.storage = storage;
		this.seed = seed;
		this.type = type;
	}

	public BiomeAccess withSource(BiomeSource source) {
		return new BiomeAccess(source, this.seed, this.type);
	}

	public Biome getBiome(BlockPos pos) {
		return this.type.getBiome(this.seed, pos.getX(), pos.getY(), pos.getZ(), this.storage);
	}

	public interface Storage {
		Biome getStoredBiome(int biomeX, int biomeY, int biomeZ);
	}
}
