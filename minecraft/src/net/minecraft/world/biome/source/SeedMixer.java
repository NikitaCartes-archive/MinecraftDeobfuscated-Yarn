package net.minecraft.world.biome.source;

public class SeedMixer {
	public static long mixSeed(long seed, long salt) {
		seed *= seed * 6364136223846793005L + 1442695040888963407L;
		return seed + salt;
	}
}
