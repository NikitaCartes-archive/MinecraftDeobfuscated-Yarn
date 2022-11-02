package net.minecraft.util.registry;

import java.util.List;
import net.minecraft.network.message.MessageType;
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
		.addRegistry(Registry.DIMENSION_TYPE_KEY, DimensionTypeRegistrar::bootstrap)
		.addRegistry(Registry.CONFIGURED_CARVER_KEY, ConfiguredCarvers::bootstrap)
		.addRegistry(Registry.CONFIGURED_FEATURE_KEY, ConfiguredFeatures::bootstrap)
		.addRegistry(Registry.PLACED_FEATURE_KEY, PlacedFeatures::getDefaultPlacedFeature)
		.addRegistry(Registry.STRUCTURE_KEY, Structures::bootstrap)
		.addRegistry(Registry.STRUCTURE_SET_KEY, StructureSets::bootstrap)
		.addRegistry(Registry.STRUCTURE_PROCESSOR_LIST_KEY, StructureProcessorLists::bootstrap)
		.addRegistry(Registry.STRUCTURE_POOL_KEY, StructurePools::bootstrap)
		.addRegistry(Registry.BIOME_KEY, BuiltinBiomes::bootstrap)
		.addRegistry(Registry.NOISE_KEY, BuiltinNoiseParameters::init)
		.addRegistry(Registry.DENSITY_FUNCTION_KEY, DensityFunctions::bootstrap)
		.addRegistry(Registry.CHUNK_GENERATOR_SETTINGS_KEY, ChunkGeneratorSettings::bootstrap)
		.addRegistry(Registry.WORLD_PRESET_KEY, WorldPresets::bootstrap)
		.addRegistry(Registry.FLAT_LEVEL_GENERATOR_PRESET_KEY, FlatLevelGeneratorPresets::bootstrap)
		.addRegistry(Registry.MESSAGE_TYPE_KEY, MessageType::bootstrap);

	private static void validate(RegistryWrapper.WrapperLookup wrapperLookup) {
		RegistryEntryLookup<PlacedFeature> registryEntryLookup = wrapperLookup.getWrapperOrThrow(Registry.PLACED_FEATURE_KEY);
		wrapperLookup.getWrapperOrThrow(Registry.BIOME_KEY).streamEntries().forEach(biome -> {
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
		DynamicRegistryManager.Immutable immutable = DynamicRegistryManager.of(Registry.REGISTRIES);
		RegistryWrapper.WrapperLookup wrapperLookup = REGISTRY_BUILDER.createWrapperLookup(immutable);
		validate(wrapperLookup);
		return wrapperLookup;
	}
}
