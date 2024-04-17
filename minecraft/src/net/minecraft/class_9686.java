package net.minecraft;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnGroup;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BiomeKeys;
import net.minecraft.world.biome.OverworldBiomeCreator;
import net.minecraft.world.biome.SpawnSettings;
import net.minecraft.world.gen.carver.ConfiguredCarver;
import net.minecraft.world.gen.feature.PlacedFeature;

public class class_9686 {
	public static void method_59883(Registerable<Biome> registerable) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = registerable.getRegistryLookup(RegistryKeys.PLACED_FEATURE);
		RegistryEntryLookup<ConfiguredCarver<?>> registryEntryLookup2 = registerable.getRegistryLookup(RegistryKeys.CONFIGURED_CARVER);
		SpawnSettings.SpawnEntry spawnEntry = new SpawnSettings.SpawnEntry(EntityType.BOGGED, 50, 4, 4);
		registerable.register(
			BiomeKeys.MANGROVE_SWAMP,
			OverworldBiomeCreator.createMangroveSwamp(registryEntryLookup, registryEntryLookup2, builder -> builder.spawn(SpawnGroup.MONSTER, spawnEntry))
		);
		registerable.register(
			BiomeKeys.SWAMP, OverworldBiomeCreator.createSwamp(registryEntryLookup, registryEntryLookup2, builder -> builder.spawn(SpawnGroup.MONSTER, spawnEntry))
		);
	}
}
