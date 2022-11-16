package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountMultilayerPlacementModifier;
import net.minecraft.world.gen.placementmodifier.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class TreePlacedFeatures {
	public static final RegistryKey<PlacedFeature> CRIMSON_FUNGI = PlacedFeatures.of("crimson_fungi");
	public static final RegistryKey<PlacedFeature> WARPED_FUNGI = PlacedFeatures.of("warped_fungi");
	public static final RegistryKey<PlacedFeature> OAK_CHECKED = PlacedFeatures.of("oak_checked");
	public static final RegistryKey<PlacedFeature> DARK_OAK_CHECKED = PlacedFeatures.of("dark_oak_checked");
	public static final RegistryKey<PlacedFeature> BIRCH_CHECKED = PlacedFeatures.of("birch_checked");
	public static final RegistryKey<PlacedFeature> ACACIA_CHECKED = PlacedFeatures.of("acacia_checked");
	public static final RegistryKey<PlacedFeature> SPRUCE_CHECKED = PlacedFeatures.of("spruce_checked");
	public static final RegistryKey<PlacedFeature> MANGROVE_CHECKED = PlacedFeatures.of("mangrove_checked");
	public static final RegistryKey<PlacedFeature> PINE_ON_SNOW = PlacedFeatures.of("pine_on_snow");
	public static final RegistryKey<PlacedFeature> SPRUCE_ON_SNOW = PlacedFeatures.of("spruce_on_snow");
	public static final RegistryKey<PlacedFeature> PINE_CHECKED = PlacedFeatures.of("pine_checked");
	public static final RegistryKey<PlacedFeature> JUNGLE_TREE = PlacedFeatures.of("jungle_tree");
	public static final RegistryKey<PlacedFeature> FANCY_OAK_CHECKED = PlacedFeatures.of("fancy_oak_checked");
	public static final RegistryKey<PlacedFeature> MEGA_JUNGLE_TREE_CHECKED = PlacedFeatures.of("mega_jungle_tree_checked");
	public static final RegistryKey<PlacedFeature> MEGA_SPRUCE_CHECKED = PlacedFeatures.of("mega_spruce_checked");
	public static final RegistryKey<PlacedFeature> MEGA_PINE_CHECKED = PlacedFeatures.of("mega_pine_checked");
	public static final RegistryKey<PlacedFeature> TALL_MANGROVE_CHECKED = PlacedFeatures.of("tall_mangrove_checked");
	public static final RegistryKey<PlacedFeature> JUNGLE_BUSH = PlacedFeatures.of("jungle_bush");
	public static final RegistryKey<PlacedFeature> SUPER_BIRCH_BEES_0002 = PlacedFeatures.of("super_birch_bees_0002");
	public static final RegistryKey<PlacedFeature> SUPER_BIRCH_BEES = PlacedFeatures.of("super_birch_bees");
	public static final RegistryKey<PlacedFeature> OAK_BEES_0002 = PlacedFeatures.of("oak_bees_0002");
	public static final RegistryKey<PlacedFeature> OAK_BEES_002 = PlacedFeatures.of("oak_bees_002");
	public static final RegistryKey<PlacedFeature> BIRCH_BEES_0002 = PlacedFeatures.of("birch_bees_0002");
	public static final RegistryKey<PlacedFeature> BIRCH_BEES_002 = PlacedFeatures.of("birch_bees_002");
	public static final RegistryKey<PlacedFeature> FANCY_OAK_BEES_0002 = PlacedFeatures.of("fancy_oak_bees_0002");
	public static final RegistryKey<PlacedFeature> FANCY_OAK_BEES_002 = PlacedFeatures.of("fancy_oak_bees_002");
	public static final RegistryKey<PlacedFeature> FANCY_OAK_BEES = PlacedFeatures.of("fancy_oak_bees");

	public static void bootstrap(Registerable<PlacedFeature> featureRegisterable) {
		RegistryEntryLookup<ConfiguredFeature<?, ?>> registryEntryLookup = featureRegisterable.getRegistryLookup(RegistryKeys.CONFIGURED_FEATURE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.CRIMSON_FUNGUS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry2 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.WARPED_FUNGUS);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry3 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry4 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.DARK_OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry5 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.BIRCH);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry6 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.ACACIA);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry7 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SPRUCE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry8 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.MANGROVE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry9 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.PINE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry10 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.JUNGLE_TREE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry11 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.FANCY_OAK);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry12 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.MEGA_JUNGLE_TREE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry13 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.MEGA_SPRUCE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry14 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.MEGA_PINE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry15 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.TALL_MANGROVE);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry16 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.JUNGLE_BUSH);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry17 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SUPER_BIRCH_BEES_0002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry18 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.SUPER_BIRCH_BEES);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry19 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK_BEES_0002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry20 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.OAK_BEES_002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry21 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.BIRCH_BEES_0002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry22 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.BIRCH_BEES_002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry23 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.FANCY_OAK_BEES_0002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry24 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.FANCY_OAK_BEES_002);
		RegistryEntry<ConfiguredFeature<?, ?>> registryEntry25 = registryEntryLookup.getOrThrow(TreeConfiguredFeatures.FANCY_OAK_BEES);
		PlacedFeatures.register(featureRegisterable, CRIMSON_FUNGI, registryEntry, CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of());
		PlacedFeatures.register(featureRegisterable, WARPED_FUNGI, registryEntry2, CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of());
		PlacedFeatures.register(featureRegisterable, OAK_CHECKED, registryEntry3, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, DARK_OAK_CHECKED, registryEntry4, PlacedFeatures.wouldSurvive(Blocks.DARK_OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, BIRCH_CHECKED, registryEntry5, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
		PlacedFeatures.register(featureRegisterable, ACACIA_CHECKED, registryEntry6, PlacedFeatures.wouldSurvive(Blocks.ACACIA_SAPLING));
		PlacedFeatures.register(featureRegisterable, SPRUCE_CHECKED, registryEntry7, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, MANGROVE_CHECKED, registryEntry8, PlacedFeatures.wouldSurvive(Blocks.MANGROVE_PROPAGULE));
		BlockPredicate blockPredicate = BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW);
		List<PlacementModifier> list = List.of(
			EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.not(BlockPredicate.matchingBlocks(Blocks.POWDER_SNOW)), 8),
			BlockFilterPlacementModifier.of(blockPredicate)
		);
		PlacedFeatures.register(featureRegisterable, PINE_ON_SNOW, registryEntry9, list);
		PlacedFeatures.register(featureRegisterable, SPRUCE_ON_SNOW, registryEntry7, list);
		PlacedFeatures.register(featureRegisterable, PINE_CHECKED, registryEntry9, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, JUNGLE_TREE, registryEntry10, PlacedFeatures.wouldSurvive(Blocks.JUNGLE_SAPLING));
		PlacedFeatures.register(featureRegisterable, FANCY_OAK_CHECKED, registryEntry11, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, MEGA_JUNGLE_TREE_CHECKED, registryEntry12, PlacedFeatures.wouldSurvive(Blocks.JUNGLE_SAPLING));
		PlacedFeatures.register(featureRegisterable, MEGA_SPRUCE_CHECKED, registryEntry13, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, MEGA_PINE_CHECKED, registryEntry14, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING));
		PlacedFeatures.register(featureRegisterable, TALL_MANGROVE_CHECKED, registryEntry15, PlacedFeatures.wouldSurvive(Blocks.MANGROVE_PROPAGULE));
		PlacedFeatures.register(featureRegisterable, JUNGLE_BUSH, registryEntry16, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, SUPER_BIRCH_BEES_0002, registryEntry17, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
		PlacedFeatures.register(featureRegisterable, SUPER_BIRCH_BEES, registryEntry18, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
		PlacedFeatures.register(featureRegisterable, OAK_BEES_0002, registryEntry19, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, OAK_BEES_002, registryEntry20, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, BIRCH_BEES_0002, registryEntry21, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
		PlacedFeatures.register(featureRegisterable, BIRCH_BEES_002, registryEntry22, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING));
		PlacedFeatures.register(featureRegisterable, FANCY_OAK_BEES_0002, registryEntry23, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, FANCY_OAK_BEES_002, registryEntry24, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
		PlacedFeatures.register(featureRegisterable, FANCY_OAK_BEES, registryEntry25, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING));
	}
}
