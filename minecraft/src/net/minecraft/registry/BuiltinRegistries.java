package net.minecraft.registry;

import java.util.List;
import net.minecraft.network.message.MessageType;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.structure.StructureSets;
import net.minecraft.structure.pool.StructurePools;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.BuiltinBiomes;
import net.minecraft.world.dimension.DimensionTypeRegistrar;
import net.minecraft.world.gen.FlatLevelGeneratorPresets;
import net.minecraft.world.gen.WorldPresets;
import net.minecraft.world.gen.carver.ConfiguredCarvers;
import net.minecraft.world.gen.chunk.ChunkGeneratorSettings;
import net.minecraft.world.gen.densityfunction.DensityFunctions;
import net.minecraft.world.gen.feature.ConfiguredFeatures;
import net.minecraft.world.gen.feature.PlacedFeature;
import net.minecraft.world.gen.feature.PlacedFeatures;
import net.minecraft.world.gen.noise.BuiltinNoiseParameters;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.structure.Structures;

public class BuiltinRegistries {
	private static final RegistryBuilder REGISTRY_BUILDER = new RegistryBuilder()
		.addRegistry(RegistryKeys.DIMENSION_TYPE, DimensionTypeRegistrar::bootstrap)
		.addRegistry(RegistryKeys.CONFIGURED_CARVER_WORLDGEN, ConfiguredCarvers::bootstrap)
		.addRegistry(RegistryKeys.CONFIGURED_FEATURE_WORLDGEN, ConfiguredFeatures::bootstrap)
		.addRegistry(RegistryKeys.PLACED_FEATURE_WORLDGEN, PlacedFeatures::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE_WORLDGEN, Structures::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE_SET_WORLDGEN, StructureSets::bootstrap)
		.addRegistry(RegistryKeys.PROCESSOR_LIST_WORLDGEN, StructureProcessorLists::bootstrap)
		.addRegistry(RegistryKeys.TEMPLATE_POOL_WORLDGEN, StructurePools::bootstrap)
		.addRegistry(RegistryKeys.BIOME_WORLDGEN, BuiltinBiomes::bootstrap)
		.addRegistry(RegistryKeys.NOISE_WORLDGEN, BuiltinNoiseParameters::bootstrap)
		.addRegistry(RegistryKeys.DENSITY_FUNCTION_WORLDGEN, DensityFunctions::bootstrap)
		.addRegistry(RegistryKeys.NOISE_SETTINGS_WORLDGEN, ChunkGeneratorSettings::bootstrap)
		.addRegistry(RegistryKeys.WORLD_PRESET_WORLDGEN, WorldPresets::bootstrap)
		.addRegistry(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET_WORLDGEN, FlatLevelGeneratorPresets::bootstrap)
		.addRegistry(RegistryKeys.CHAT_TYPE, MessageType::bootstrap);

	private static void validate(RegistryWrapper.WrapperLookup wrapperLookup) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE_WORLDGEN);
		wrapperLookup.getWrapperOrThrow(RegistryKeys.BIOME_WORLDGEN).streamEntries().forEach(biome -> {
			Identifier identifier = biome.registryKey().getValue();
			List<RegistryEntryList<PlacedFeature>> list = ((Biome)biome.value()).getGenerationSettings().getFeatures();
			list.stream().flatMap(RegistryEntryList::stream).forEach(placedFeature -> placedFeature.getKeyOrValue().ifLeft(key -> {
					RegistryEntry.Reference<PlacedFeature> referencex = registryEntryLookup.getOrThrow(key);
					if (!hasBiomePlacementModifier(referencex.value())) {
						Util.error("Placed feature " + key.getValue() + " in biome " + identifier + " is missing BiomeFilter.biome()");
					}
				}).ifRight(value -> {
					if (!hasBiomePlacementModifier(value)) {
						Util.error("Placed inline feature in biome " + biome + " is missing BiomeFilter.biome()");
					}
				}));
		});
	}

	private static boolean hasBiomePlacementModifier(PlacedFeature placedFeature) {
		return placedFeature.placementModifiers().contains(BiomePlacementModifier.of());
	}

	public static RegistryWrapper.WrapperLookup createWrapperLookup() {
		DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registries.REGISTRIES);
		RegistryWrapper.WrapperLookup wrapperLookup = REGISTRY_BUILDER.createWrapperLookup(immutable);
		validate(wrapperLookup);
		return wrapperLookup;
	}
}
