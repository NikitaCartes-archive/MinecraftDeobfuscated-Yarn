package net.minecraft.world.biome;

import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class WinterDropBuiltinBiomes {
	public static final RegistryKey<Biome> PALE_GARDEN = keyOf("pale_garden");

	public static RegistryKey<Biome> keyOf(String id) {
		return RegistryKey.of(RegistryKeys.BIOME, Identifier.ofVanilla(id));
	}

	public static void register(Registerable<Biome> biomeRegisterable, String id, Biome biome) {
		biomeRegisterable.register(keyOf(id), biome);
	}

	public static void bootstrap(Registerable<Biome> biomeRegisterable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = biomeRegisterable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntryLookup<ConfiguredCarver<?>> registryEntryLookup2 = biomeRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER);
		biomeRegisterable.register(PALE_GARDEN, OverworldBiomeCreator.createDenseForest(registryEntryLookup, registryEntryLookup2, true));
	}
}
