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
	public static final RegistryKey<PlacedFeature> PILE_POTATO_FRUIT = PlacedFeatures.of("pile_potato_fruit");
	public static final RegistryKey<PlacedFeature> PILE_SNOW = PlacedFeatures.of("pile_snow");
	public static final RegistryKey<PlacedFeature> PILE_ICE = PlacedFeatures.of("pile_ice");
	public static final RegistryKey<PlacedFeature> PILE_PUMPKIN = PlacedFeatures.of("pile_pumpkin");
	public static final RegistryKey<PlacedFeature> OAK = PlacedFeatures.of("oak");
	public static final RegistryKey<PlacedFeature> POTATO = PlacedFeatures.of("potato");
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
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_POTATO_FRUIT);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry3 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_MELON);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry4 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_SNOW);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry5 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_ICE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry6 = registryEntryLookup.getOrThrow(PileConfiguredFeatures.PILE_PUMPKIN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry7 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry8 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.POTATO_TREE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry9 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.ACACIA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry10 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SPRUCE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry11 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.PINE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry12 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_CACTUS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry13 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.FLOWER_PLAIN);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry14 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_TAIGA_GRASS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry15 = registryEntryLookup.getOrThrow(VegetationConfiguredFeatures.PATCH_BERRY_BUSH);
		PlacedFeatures.register(featureRegisterable, PILE_HAY, registryEntry);
		PlacedFeatures.register(featureRegisterable, PILE_POTATO_FRUIT, registryEntry2);
		PlacedFeatures.register(featureRegisterable, PILE_MELON, registryEntry3);
		PlacedFeatures.register(featureRegisterable, PILE_SNOW, registryEntry4);
		PlacedFeatures.register(featureRegisterable, PILE_ICE, registryEntry5);
		PlacedFeatures.register(featureRegisterable, PILE_PUMPKIN, registryEntry6);
		PlacedFeatures.register(featureRegisterable, OAK, registryEntry7, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, POTATO, registryEntry8, PlacedFeatures.wouldSurvive(Blocks.POTATO_SPROUTS));
		PlacedFeatures.register(featureRegisterable, ACACIA, registryEntry9, PlacedFeatures.wouldSurvive(Blocks.ACACIA_SAPLING));
		PlacedFeatures.register(featureRegisterable, SPRUCE, registryEntry10, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, PINE, registryEntry11, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, PATCH_CACTUS, registryEntry12);
		PlacedFeatures.register(featureRegisterable, FLOWER_PLAIN, registryEntry13);
		PlacedFeatures.register(featureRegisterable, PATCH_TAIGA_GRASS, registryEntry14);
		PlacedFeatures.register(featureRegisterable, PATCH_BERRY_BUSH, registryEntry15);
	}
}
