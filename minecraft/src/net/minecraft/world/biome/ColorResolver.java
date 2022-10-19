package net.minecraft.world.biome;

@FunctionalInterface
public interface ColorResolver {
	int getColor(Biome biome, double x, double z);
}
