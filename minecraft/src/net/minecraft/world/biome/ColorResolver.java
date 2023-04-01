package net.minecraft.world.biome;

import net.minecraft.registry.entry.RegistryEntry;

@FunctionalInterface
public interface ColorResolver {
	int getColor(RegistryEntry<Biome> biomeEntry, double x, double z);
}
