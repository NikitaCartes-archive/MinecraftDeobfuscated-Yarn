package net.minecraft.world.biome;

public class SeedMixer {
	public static long mixSeed(long one, long two) {
		one *= one * 6364136223846793005L + 1442695040888963407L;
		return one + two;
	}
}
