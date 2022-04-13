package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.BiomePlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountMultilayerPlacementModifier;
import net.minecraft.world.gen.placementmodifier.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class TreePlacedFeatures {
	public static final RegistryEntry<PlacedFeature> CRIMSON_FUNGI = PlacedFeatures.register(
		"crimson_fungi", TreeConfiguredFeatures.CRIMSON_FUNGUS, CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> WARPED_FUNGI = PlacedFeatures.register(
		"warped_fungi", TreeConfiguredFeatures.WARPED_FUNGUS, CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of()
	);
	public static final RegistryEntry<PlacedFeature> OAK_CHECKED = PlacedFeatures.register(
		"oak_checked", TreeConfiguredFeatures.OAK, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> DARK_OAK_CHECKED = PlacedFeatures.register(
		"dark_oak_checked", TreeConfiguredFeatures.DARK_OAK, PlacedFeatures.wouldSurvive(Blocks.DARK_OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> BIRCH_CHECKED = PlacedFeatures.register(
		"birch_checked", TreeConfiguredFeatures.BIRCH, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> ACACIA_CHECKED = PlacedFeatures.register(
		"acacia_checked", TreeConfiguredFeatures.ACACIA, PlacedFeatures.wouldSurvive(Blocks.ACACIA_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> SPRUCE_CHECKED = PlacedFeatures.register(
		"spruce_checked", TreeConfiguredFeatures.SPRUCE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> MANGROVE_CHECKED = PlacedFeatures.register(
		"mangrove_checked", TreeConfiguredFeatures.MANGROVE, PlacedFeatures.wouldSurvive(Blocks.MANGROVE_PROPAGULE)
	);
	public static final BlockPredicate ON_SNOW_PREDICATE = BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW);
	public static final List<PlacementModifier> ON_SNOW_MODIFIERS = List.of(
		EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.not(BlockPredicate.matchingBlocks(Blocks.POWDER_SNOW)), 8),
		BlockFilterPlacementModifier.of(ON_SNOW_PREDICATE)
	);
	public static final RegistryEntry<PlacedFeature> PINE_ON_SNOW = PlacedFeatures.register("pine_on_snow", TreeConfiguredFeatures.PINE, ON_SNOW_MODIFIERS);
	public static final RegistryEntry<PlacedFeature> SPRUCE_ON_SNOW = PlacedFeatures.register("spruce_on_snow", TreeConfiguredFeatures.SPRUCE, ON_SNOW_MODIFIERS);
	public static final RegistryEntry<PlacedFeature> PINE_CHECKED = PlacedFeatures.register(
		"pine_checked", TreeConfiguredFeatures.PINE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> JUNGLE_TREE = PlacedFeatures.register(
		"jungle_tree", TreeConfiguredFeatures.JUNGLE_TREE, PlacedFeatures.wouldSurvive(Blocks.JUNGLE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> FANCY_OAK_CHECKED = PlacedFeatures.register(
		"fancy_oak_checked", TreeConfiguredFeatures.FANCY_OAK, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> MEGA_JUNGLE_TREE_CHECKED = PlacedFeatures.register(
		"mega_jungle_tree_checked", TreeConfiguredFeatures.MEGA_JUNGLE_TREE, PlacedFeatures.wouldSurvive(Blocks.JUNGLE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> MEGA_SPRUCE_CHECKED = PlacedFeatures.register(
		"mega_spruce_checked", TreeConfiguredFeatures.MEGA_SPRUCE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> MEGA_PINE_CHECKED = PlacedFeatures.register(
		"mega_pine_checked", TreeConfiguredFeatures.MEGA_PINE, PlacedFeatures.wouldSurvive(Blocks.SPRUCE_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> TALL_MANGROVE_CHECKED = PlacedFeatures.register(
		"tall_mangrove_checked", TreeConfiguredFeatures.TALL_MANGROVE, PlacedFeatures.wouldSurvive(Blocks.MANGROVE_PROPAGULE)
	);
	public static final RegistryEntry<PlacedFeature> JUNGLE_BUSH = PlacedFeatures.register(
		"jungle_bush", TreeConfiguredFeatures.JUNGLE_BUSH, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> SUPER_BIRCH_BEES_0002 = PlacedFeatures.register(
		"super_birch_bees_0002", TreeConfiguredFeatures.SUPER_BIRCH_BEES_0002, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> SUPER_BIRCH_BEES = PlacedFeatures.register(
		"super_birch_bees", TreeConfiguredFeatures.SUPER_BIRCH_BEES, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> OAK_BEES_0002 = PlacedFeatures.register(
		"oak_bees_0002", TreeConfiguredFeatures.OAK_BEES_0002, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> OAK_BEES_002 = PlacedFeatures.register(
		"oak_bees_002", TreeConfiguredFeatures.OAK_BEES_002, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> BIRCH_BEES_0002 = PlacedFeatures.register(
		"birch_bees_0002", TreeConfiguredFeatures.BIRCH_BEES_0002, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> BIRCH_BEES_002 = PlacedFeatures.register(
		"birch_bees_002", TreeConfiguredFeatures.BIRCH_BEES_002, PlacedFeatures.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> FANCY_OAK_BEES_0002 = PlacedFeatures.register(
		"fancy_oak_bees_0002", TreeConfiguredFeatures.FANCY_OAK_BEES_0002, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> FANCY_OAK_BEES_002 = PlacedFeatures.register(
		"fancy_oak_bees_002", TreeConfiguredFeatures.FANCY_OAK_BEES_002, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final RegistryEntry<PlacedFeature> FANCY_OAK_BEES = PlacedFeatures.register(
		"fancy_oak_bees", TreeConfiguredFeatures.FANCY_OAK_BEES, PlacedFeatures.wouldSurvive(Blocks.OAK_SAPLING)
	);
}
