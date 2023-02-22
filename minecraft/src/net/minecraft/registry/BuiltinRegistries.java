package net.minecraft.registry;

import java.util.List;
import net.minecraft.entity.damage.DamageTypes;
import net.minecraft.item.trim.ArmorTrimMaterials;
import net.minecraft.item.trim.ArmorTrimPatterns;
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
import net.minecraft.world.biome.source.MultiNoiseBiomeSourceParameterLists;
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
		.addRegistry(RegistryKeys.CONFIGURED_CARVER, ConfiguredCarvers::bootstrap)
		.addRegistry(RegistryKeys.CONFIGURED_FEATURE, ConfiguredFeatures::bootstrap)
		.addRegistry(RegistryKeys.PLACED_FEATURE, PlacedFeatures::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE, Structures::bootstrap)
		.addRegistry(RegistryKeys.STRUCTURE_SET, StructureSets::bootstrap)
		.addRegistry(RegistryKeys.PROCESSOR_LIST, StructureProcessorLists::bootstrap)
		.addRegistry(RegistryKeys.TEMPLATE_POOL, StructurePools::bootstrap)
		.addRegistry(RegistryKeys.BIOME, BuiltinBiomes::bootstrap)
		.addRegistry(RegistryKeys.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST, MultiNoiseBiomeSourceParameterLists::bootstrap)
		.addRegistry(RegistryKeys.NOISE_PARAMETERS, BuiltinNoiseParameters::bootstrap)
		.addRegistry(RegistryKeys.DENSITY_FUNCTION, DensityFunctions::bootstrap)
		.addRegistry(RegistryKeys.CHUNK_GENERATOR_SETTINGS, ChunkGeneratorSettings::bootstrap)
		.addRegistry(RegistryKeys.WORLD_PRESET, WorldPresets::bootstrap)
		.addRegistry(RegistryKeys.FLAT_LEVEL_GENERATOR_PRESET, FlatLevelGeneratorPresets::bootstrap)
		.addRegistry(RegistryKeys.MESSAGE_TYPE, MessageType::bootstrap)
		.addRegistry(RegistryKeys.TRIM_PATTERN, ArmorTrimPatterns::bootstrap)
		.addRegistry(RegistryKeys.TRIM_MATERIAL, ArmorTrimMaterials::bootstrap)
		.addRegistry(RegistryKeys.DAMAGE_TYPE, DamageTypes::bootstrap);

	private static void validate(RegistryWrapper.WrapperLookup wrapperLookup) {
		validate(wrapperLookup.getWrapperOrThrow(RegistryKeys.PLACED_FEATURE), wrapperLookup.getWrapperOrThrow(RegistryKeys.BIOME));
	}

	public static void validate(RegistryEntryLookup<PlacedFeature> placedFeatureLookup, RegistryWrapper<Biome> biomeLookup) {
		biomeLookup.streamEntries().forEach(biome -> {
			Identifier identifier = biome.registryKey().getValue();
			List<RegistryEntryList<PlacedFeature>> list = ((Biome)biome.value()).getGenerationSettings().getFeatures();
			list.stream().flatMap(RegistryEntryList::stream).forEach(placedFeature -> placedFeature.getKeyOrValue().ifLeft(key -> {
					RegistryEntry.Reference<PlacedFeature> referencex = placedFeatureLookup.getOrThrow(key);
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
