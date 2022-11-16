package net.minecraft.world.gen.feature;

import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;

public class VillagePlacedFeatures {
	public static final RegistryKey<PlacedFeature> PILE_HAY = PlacedFeatures.of("pile_hay");
	public static final RegistryKey<PlacedFeature> PILE_MELON = PlacedFeatures.of("pile_melon");
	public static final RegistryKey<PlacedFeature> PILE_SNOW = PlacedFeatures.of("pile_snow");
	public static final RegistryKey<PlacedFeature> PILE_ICE = PlacedFeatures.of("pile_ice");
	public static final RegistryKey<PlacedFeature> PILE_PUMPKIN = PlacedFeatures.of("pile_pumpkin");
	public static final RegistryKey<PlacedFeature> OAK = PlacedFeatures.of("oak");
	public static final RegistryKey<PlacedFeature> ACACIA = PlacedFeatures.of("acacia");
	public static final RegistryKey<PlacedFeature> SPRUCE = PlacedFeatures.of("spruce");
	public static final RegistryKey<PlacedFeature> PINE = PlacedFeatures.of("pine");
	public static final RegistryKey<PlacedFeature> PATCH_CACTUS = PlacedFeatures.of("patch_cactus");
	public static final RegistryKey<PlacedFeature> FLOWER_PLAIN = PlacedFeatures.of("flower_plain");
	public static final RegistryKey<PlacedFeature> PATCH_TAIGA_GRASS = PlacedFeatures.of("patch_taiga_grass");
	public static final RegistryKey<PlacedFeature> PATCH_BERRY_BUSH = PlacedFeatures.of("patch_berry_bush");

	public static void bootstrap(Registerable<PlacedFeature> featureRegisterable) {
		RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_HAY);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_MELON);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry3 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_SNOW);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry4 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_ICE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry5 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_PUMPKIN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry6 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry7 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.ACACIA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry8 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SPRUCE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry9 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.PINE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry10 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_CACTUS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry11 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_PLAIN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry12 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_TAIGA_GRASS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry13 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_BERRY_BUSH);
		PlacedFeatures.register(featureRegisterable, PILE_HAY, registryEntry);
		PlacedFeatures.register(featureRegisterable, PILE_MELON, registryEntry2);
		PlacedFeatures.register(featureRegisterable, PILE_SNOW, registryEntry3);
		PlacedFeatures.register(featureRegisterable, PILE_ICE, registryEntry4);
		PlacedFeatures.register(featureRegisterable, PILE_PUMPKIN, registryEntry5);
		PlacedFeatures.register(featureRegisterable, OAK, registryEntry6, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, ACACIA, registryEntry7, PlacedFeatures.wouldSurvive(Blocks.ACACIA_SAPLING));
		PlacedFeatures.register(featureRegisterable, SPRUCE, registryEntry8, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, PINE, registryEntry9, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, PATCH_CACTUS, registryEntry10);
		PlacedFeatures.register(featureRegisterable, FLOWER_PLAIN, registryEntry11);
		PlacedFeatures.register(featureRegisterable, PATCH_TAIGA_GRASS, registryEntry12);
		PlacedFeatures.register(featureRegisterable, PATCH_BERRY_BUSH, registryEntry13);
	}
}
