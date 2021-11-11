package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.BiomePlacementModifier;
import net.minecraft.world.gen.decorator.BlockFilterPlacementModifier;
import net.minecraft.world.gen.decorator.CountMultilayerPlacementModifier;
import net.minecraft.world.gen.decorator.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.decorator.PlacementModifier;

public class TreePlacedFeatures {
	public static final PlacedFeature CRIMSON_FUNGI = PlacedFeatures.register(
		"crimson_fungi", TreeConfiguredFeatures.CRIMSON_FUNGUS.withPlacement(CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of())
	);
	public static final PlacedFeature WARPED_FUNGI = PlacedFeatures.register(
		"warped_fungi", TreeConfiguredFeatures.WARPED_FUNGUS.withPlacement(CountMultilayerPlacementModifier.of(8), BiomePlacementModifier.of())
	);
	public static final PlacedFeature OAK_CHECKED = PlacedFeatures.register("oak_checked", TreeConfiguredFeatures.OAK.withWouldSurviveFilter(Blocks.OAK_SAPLING));
	public static final PlacedFeature DARK_OAK_CHECKED = PlacedFeatures.register(
		"dark_oak_checked", TreeConfiguredFeatures.DARK_OAK.withWouldSurviveFilter(Blocks.DARK_OAK_SAPLING)
	);
	public static final PlacedFeature BIRCH_CHECKED = PlacedFeatures.register(
		"birch_checked", TreeConfiguredFeatures.BIRCH.withWouldSurviveFilter(Blocks.BIRCH_SAPLING)
	);
	public static final PlacedFeature ACACIA_CHECKED = PlacedFeatures.register(
		"acacia_checked", TreeConfiguredFeatures.ACACIA.withWouldSurviveFilter(Blocks.ACACIA_SAPLING)
	);
	public static final PlacedFeature SPRUCE_CHECKED = PlacedFeatures.register(
		"spruce_checked", TreeConfiguredFeatures.SPRUCE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING)
	);
	public static final BlockPredicate ON_SNOW_PREDICATE = BlockPredicate.matchingBlocks(List.of(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW), new BlockPos(0, -1, 0));
	public static final List<PlacementModifier> ON_SNOW_MODIFIERS = List.of(
		EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.not(BlockPredicate.matchingBlock(Blocks.POWDER_SNOW, BlockPos.ORIGIN)), 8),
		BlockFilterPlacementModifier.of(ON_SNOW_PREDICATE)
	);
	public static final PlacedFeature PINE_ON_SNOW = PlacedFeatures.register("pine_on_snow", TreeConfiguredFeatures.PINE.withPlacement(ON_SNOW_MODIFIERS));
	public static final PlacedFeature SPRUCE_ON_SNOW = PlacedFeatures.register("spruce_on_snow", TreeConfiguredFeatures.SPRUCE.withPlacement(ON_SNOW_MODIFIERS));
	public static final PlacedFeature PINE_CHECKED = PlacedFeatures.register(
		"pine_checked", TreeConfiguredFeatures.PINE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING)
	);
	public static final PlacedFeature JUNGLE_TREE = PlacedFeatures.register(
		"jungle_tree", TreeConfiguredFeatures.JUNGLE_TREE.withWouldSurviveFilter(Blocks.JUNGLE_SAPLING)
	);
	public static final PlacedFeature FANCY_OAK_CHECKED = PlacedFeatures.register(
		"fancy_oak_checked", TreeConfiguredFeatures.FANCY_OAK.withWouldSurviveFilter(Blocks.OAK_SAPLING)
	);
	public static final PlacedFeature MEGA_JUNGLE_TREE_CHECKED = PlacedFeatures.register(
		"mega_jungle_tree_checked", TreeConfiguredFeatures.MEGA_JUNGLE_TREE.withWouldSurviveFilter(Blocks.JUNGLE_SAPLING)
	);
	public static final PlacedFeature MEGA_SPRUCE_CHECKED = PlacedFeatures.register(
		"mega_spruce_checked", TreeConfiguredFeatures.MEGA_SPRUCE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING)
	);
	public static final PlacedFeature MEGA_PINE_CHECKED = PlacedFeatures.register(
		"mega_pine_checked", TreeConfiguredFeatures.MEGA_PINE.withWouldSurviveFilter(Blocks.SPRUCE_SAPLING)
	);
	public static final PlacedFeature JUNGLE_BUSH = PlacedFeatures.register(
		"jungle_bush", TreeConfiguredFeatures.JUNGLE_BUSH.withWouldSurviveFilter(Blocks.OAK_SAPLING)
	);
	public static final PlacedFeature SUPER_BIRCH_BEES_0002 = PlacedFeatures.register(
		"super_birch_bees_0002", TreeConfiguredFeatures.SUPER_BIRCH_BEES_0002.withWouldSurviveFilter(Blocks.BIRCH_SAPLING)
	);
	public static final PlacedFeature SUPER_BIRCH_BEES = PlacedFeatures.register(
		"super_birch_bees", TreeConfiguredFeatures.SUPER_BIRCH_BEES.withWouldSurviveFilter(Blocks.BIRCH_SAPLING)
	);
	public static final PlacedFeature OAK_BEES_0002 = PlacedFeatures.register(
		"oak_bees_0002", TreeConfiguredFeatures.OAK_BEES_0002.withWouldSurviveFilter(Blocks.OAK_SAPLING)
	);
	public static final PlacedFeature OAK_BEES_002 = PlacedFeatures.register(
		"oak_bees_002", TreeConfiguredFeatures.OAK_BEES_002.withWouldSurviveFilter(Blocks.OAK_SAPLING)
	);
	public static final PlacedFeature BIRCH_BEES_0002 = PlacedFeatures.register(
		"birch_bees_0002", TreeConfiguredFeatures.BIRCH_BEES_0002.withWouldSurviveFilter(Blocks.BIRCH_SAPLING)
	);
	public static final PlacedFeature BIRCH_BEES_002 = PlacedFeatures.register(
		"birch_bees_002", TreeConfiguredFeatures.BIRCH_BEES_002.withWouldSurviveFilter(Blocks.BIRCH_SAPLING)
	);
	public static final PlacedFeature FANCY_OAK_BEES_0002 = PlacedFeatures.register(
		"fancy_oak_bees_0002", TreeConfiguredFeatures.FANCY_OAK_BEES_0002.withWouldSurviveFilter(Blocks.OAK_SAPLING)
	);
	public static final PlacedFeature FANCY_OAK_BEES_002 = PlacedFeatures.register(
		"fancy_oak_bees_002", TreeConfiguredFeatures.FANCY_OAK_BEES_002.withWouldSurviveFilter(Blocks.OAK_SAPLING)
	);
	public static final PlacedFeature FANCY_OAK_BEES = PlacedFeatures.register(
		"fancy_oak_bees", TreeConfiguredFeatures.FANCY_OAK_BEES.withWouldSurviveFilter(Blocks.OAK_SAPLING)
	);
}
