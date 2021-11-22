package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import java.util.List;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.CaveVinesHeadBlock;
import net.minecraft.block.SmallDripleafBlock;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.EnvironmentScanPlacementModifier;
import net.minecraft.world.gen.decorator.RandomOffsetPlacementModifier;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class UndergroundConfiguredFeatures {
	public static final ConfiguredFeature<DefaultFeatureConfig, ?> MONSTER_ROOM = ConfiguredFeatures.register(
		"monster_room", Feature.MONSTER_ROOM.configure(FeatureConfig.DEFAULT)
	);
	private static final List<Identifier> FOSSIL_IDS = List.of(
		new Identifier("fossil/spine_1"),
		new Identifier("fossil/spine_2"),
		new Identifier("fossil/spine_3"),
		new Identifier("fossil/spine_4"),
		new Identifier("fossil/skull_1"),
		new Identifier("fossil/skull_2"),
		new Identifier("fossil/skull_3"),
		new Identifier("fossil/skull_4")
	);
	private static final List<Identifier> COAL_FOSSIL_IDS = List.of(
		new Identifier("fossil/spine_1_coal"),
		new Identifier("fossil/spine_2_coal"),
		new Identifier("fossil/spine_3_coal"),
		new Identifier("fossil/spine_4_coal"),
		new Identifier("fossil/skull_1_coal"),
		new Identifier("fossil/skull_2_coal"),
		new Identifier("fossil/skull_3_coal"),
		new Identifier("fossil/skull_4_coal")
	);
	public static final ConfiguredFeature<FossilFeatureConfig, ?> FOSSIL_COAL = ConfiguredFeatures.register(
		"fossil_coal",
		Feature.FOSSIL.configure(new FossilFeatureConfig(FOSSIL_IDS, COAL_FOSSIL_IDS, StructureProcessorLists.FOSSIL_ROT, StructureProcessorLists.FOSSIL_COAL, 4))
	);
	public static final ConfiguredFeature<FossilFeatureConfig, ?> FOSSIL_DIAMONDS = ConfiguredFeatures.register(
		"fossil_diamonds",
		Feature.FOSSIL
			.configure(new FossilFeatureConfig(FOSSIL_IDS, COAL_FOSSIL_IDS, StructureProcessorLists.FOSSIL_ROT, StructureProcessorLists.FOSSIL_DIAMONDS, 4))
	);
	public static final ConfiguredFeature<DripstoneClusterFeatureConfig, ?> DRIPSTONE_CLUSTER = ConfiguredFeatures.register(
		"dripstone_cluster",
		Feature.DRIPSTONE_CLUSTER
			.configure(
				new DripstoneClusterFeatureConfig(
					12,
					UniformIntProvider.create(3, 6),
					UniformIntProvider.create(2, 8),
					1,
					3,
					UniformIntProvider.create(2, 4),
					UniformFloatProvider.create(0.3F, 0.7F),
					ClampedNormalFloatProvider.create(0.1F, 0.3F, 0.1F, 0.9F),
					0.1F,
					3,
					8
				)
			)
	);
	public static final ConfiguredFeature<LargeDripstoneFeatureConfig, ?> LARGE_DRIPSTONE = ConfiguredFeatures.register(
		"large_dripstone",
		Feature.LARGE_DRIPSTONE
			.configure(
				new LargeDripstoneFeatureConfig(
					30,
					UniformIntProvider.create(3, 19),
					UniformFloatProvider.create(0.4F, 2.0F),
					0.33F,
					UniformFloatProvider.create(0.3F, 0.9F),
					UniformFloatProvider.create(0.4F, 1.0F),
					UniformFloatProvider.create(0.0F, 0.3F),
					4,
					0.6F
				)
			)
	);
	public static final ConfiguredFeature<SimpleRandomFeatureConfig, ?> POINTED_DRIPSTONE = ConfiguredFeatures.register(
		"pointed_dripstone",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(
				new SimpleRandomFeatureConfig(
					ImmutableList.of(
						() -> Feature.POINTED_DRIPSTONE
								.configure(new SmallDripstoneFeatureConfig(0.2F, 0.7F, 0.5F, 0.5F))
								.withPlacement(
									EnvironmentScanPlacementModifier.of(Direction.DOWN, BlockPredicate.solid(), BlockPredicate.IS_AIR_OR_WATER, 12),
									RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(1))
								),
						() -> Feature.POINTED_DRIPSTONE
								.configure(new SmallDripstoneFeatureConfig(0.2F, 0.7F, 0.5F, 0.5F))
								.withPlacement(
									EnvironmentScanPlacementModifier.of(Direction.UP, BlockPredicate.solid(), BlockPredicate.IS_AIR_OR_WATER, 12),
									RandomOffsetPlacementModifier.vertically(ConstantIntProvider.create(-1))
								)
					)
				)
			)
	);
	public static final ConfiguredFeature<UnderwaterMagmaFeatureConfig, ?> UNDERWATER_MAGMA = ConfiguredFeatures.register(
		"underwater_magma", Feature.UNDERWATER_MAGMA.configure(new UnderwaterMagmaFeatureConfig(5, 1, 0.5F))
	);
	public static final ConfiguredFeature<GlowLichenFeatureConfig, ?> GLOW_LICHEN = ConfiguredFeatures.register(
		"glow_lichen",
		Feature.GLOW_LICHEN
			.configure(
				new GlowLichenFeatureConfig(
					20,
					false,
					true,
					true,
					0.5F,
					List.of(Blocks.STONE, Blocks.ANDESITE, Blocks.DIORITE, Blocks.GRANITE, Blocks.DRIPSTONE_BLOCK, Blocks.CALCITE, Blocks.TUFF, Blocks.DEEPSLATE)
				)
			)
	);
	public static final ConfiguredFeature<RootSystemFeatureConfig, ?> ROOTED_AZALEA_TREE = ConfiguredFeatures.register(
		"rooted_azalea_tree",
		Feature.ROOT_SYSTEM
			.configure(
				new RootSystemFeatureConfig(
					() -> TreeConfiguredFeatures.AZALEA_TREE.withPlacement(),
					3,
					3,
					BlockTags.AZALEA_ROOT_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.ROOTED_DIRT),
					20,
					100,
					3,
					2,
					BlockStateProvider.of(Blocks.HANGING_ROOTS),
					20,
					2,
					BlockPredicate.bothOf(
						BlockPredicate.anyOf(
							BlockPredicate.matchingBlocks(List.of(Blocks.AIR, Blocks.CAVE_AIR, Blocks.VOID_AIR, Blocks.WATER)),
							BlockPredicate.matchingBlockTag(BlockTags.LEAVES),
							BlockPredicate.matchingBlockTag(BlockTags.REPLACEABLE_PLANTS)
						),
						BlockPredicate.matchingBlockTag(BlockTags.AZALEA_GROWS_ON, Direction.DOWN.getVector())
					)
				)
			)
	);
	private static final WeightedBlockStateProvider CAVE_VINES_PLANT_PROVIDER = new WeightedBlockStateProvider(
		DataPool.<BlockState>builder()
			.add(Blocks.CAVE_VINES_PLANT.getDefaultState(), 4)
			.add(Blocks.CAVE_VINES_PLANT.getDefaultState().with(CaveVines.BERRIES, Boolean.valueOf(true)), 1)
	);
	private static final RandomizedIntBlockStateProvider RANDOMIZED_AGE_CAVE_VINES_PROVIDER = new RandomizedIntBlockStateProvider(
		new WeightedBlockStateProvider(
			DataPool.<BlockState>builder()
				.add(Blocks.CAVE_VINES.getDefaultState(), 4)
				.add(Blocks.CAVE_VINES.getDefaultState().with(CaveVines.BERRIES, Boolean.valueOf(true)), 1)
		),
		CaveVinesHeadBlock.AGE,
		UniformIntProvider.create(23, 25)
	);
	public static final ConfiguredFeature<BlockColumnFeatureConfig, ?> CAVE_VINE = ConfiguredFeatures.register(
		"cave_vine",
		Feature.BLOCK_COLUMN
			.configure(
				new BlockColumnFeatureConfig(
					List.of(
						BlockColumnFeatureConfig.createLayer(
							new WeightedListIntProvider(
								DataPool.<IntProvider>builder()
									.add(UniformIntProvider.create(0, 19), 2)
									.add(UniformIntProvider.create(0, 2), 3)
									.add(UniformIntProvider.create(0, 6), 10)
									.build()
							),
							CAVE_VINES_PLANT_PROVIDER
						),
						BlockColumnFeatureConfig.createLayer(ConstantIntProvider.create(1), RANDOMIZED_AGE_CAVE_VINES_PROVIDER)
					),
					Direction.DOWN,
					BlockPredicate.IS_AIR,
					true
				)
			)
	);
	public static final ConfiguredFeature<BlockColumnFeatureConfig, ?> CAVE_VINE_IN_MOSS = ConfiguredFeatures.register(
		"cave_vine_in_moss",
		Feature.BLOCK_COLUMN
			.configure(
				new BlockColumnFeatureConfig(
					List.of(
						BlockColumnFeatureConfig.createLayer(
							new WeightedListIntProvider(DataPool.<IntProvider>builder().add(UniformIntProvider.create(0, 3), 5).add(UniformIntProvider.create(1, 7), 1).build()),
							CAVE_VINES_PLANT_PROVIDER
						),
						BlockColumnFeatureConfig.createLayer(ConstantIntProvider.create(1), RANDOMIZED_AGE_CAVE_VINES_PROVIDER)
					),
					Direction.DOWN,
					BlockPredicate.IS_AIR,
					true
				)
			)
	);
	public static final ConfiguredFeature<SimpleBlockFeatureConfig, ?> MOSS_VEGETATION = ConfiguredFeatures.register(
		"moss_vegetation",
		Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(
						DataPool.<BlockState>builder()
							.add(Blocks.FLOWERING_AZALEA.getDefaultState(), 4)
							.add(Blocks.AZALEA.getDefaultState(), 7)
							.add(Blocks.MOSS_CARPET.getDefaultState(), 25)
							.add(Blocks.GRASS.getDefaultState(), 50)
							.add(Blocks.TALL_GRASS.getDefaultState(), 10)
					)
				)
			)
	);
	public static final ConfiguredFeature<VegetationPatchFeatureConfig, ?> MOSS_PATCH = ConfiguredFeatures.register(
		"moss_patch",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.MOSS_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.MOSS_BLOCK),
					() -> MOSS_VEGETATION.withPlacement(),
					VerticalSurfaceType.FLOOR,
					ConstantIntProvider.create(1),
					0.0F,
					5,
					0.8F,
					UniformIntProvider.create(4, 7),
					0.3F
				)
			)
	);
	public static final ConfiguredFeature<VegetationPatchFeatureConfig, ?> MOSS_PATCH_BONEMEAL = ConfiguredFeatures.register(
		"moss_patch_bonemeal",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.MOSS_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.MOSS_BLOCK),
					() -> MOSS_VEGETATION.withPlacement(),
					VerticalSurfaceType.FLOOR,
					ConstantIntProvider.create(1),
					0.0F,
					5,
					0.6F,
					UniformIntProvider.create(1, 2),
					0.75F
				)
			)
	);
	public static final ConfiguredFeature<SimpleRandomFeatureConfig, ?> DRIPLEAF = ConfiguredFeatures.register(
		"dripleaf",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(
				new SimpleRandomFeatureConfig(
					List.of(
						UndergroundConfiguredFeatures::createSmallDripleafFeature,
						(Supplier)() -> createBigDripleafFeature(Direction.EAST),
						(Supplier)() -> createBigDripleafFeature(Direction.WEST),
						(Supplier)() -> createBigDripleafFeature(Direction.SOUTH),
						(Supplier)() -> createBigDripleafFeature(Direction.NORTH)
					)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> CLAY_WITH_DRIPLEAVES = ConfiguredFeatures.register(
		"clay_with_dripleaves",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.LUSH_GROUND_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.CLAY),
					() -> DRIPLEAF.withPlacement(),
					VerticalSurfaceType.FLOOR,
					ConstantIntProvider.create(3),
					0.8F,
					2,
					0.05F,
					UniformIntProvider.create(4, 7),
					0.7F
				)
			)
	);
	public static final ConfiguredFeature<?, ?> CLAY_POOL_WITH_DRIPLEAVES = ConfiguredFeatures.register(
		"clay_pool_with_dripleaves",
		Feature.WATERLOGGED_VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.LUSH_GROUND_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.CLAY),
					() -> DRIPLEAF.withPlacement(),
					VerticalSurfaceType.FLOOR,
					ConstantIntProvider.create(3),
					0.8F,
					5,
					0.1F,
					UniformIntProvider.create(4, 7),
					0.7F
				)
			)
	);
	public static final ConfiguredFeature<RandomBooleanFeatureConfig, ?> LUSH_CAVES_CLAY = ConfiguredFeatures.register(
		"lush_caves_clay",
		Feature.RANDOM_BOOLEAN_SELECTOR
			.configure(new RandomBooleanFeatureConfig(() -> CLAY_WITH_DRIPLEAVES.withPlacement(), () -> CLAY_POOL_WITH_DRIPLEAVES.withPlacement()))
	);
	public static final ConfiguredFeature<VegetationPatchFeatureConfig, ?> MOSS_PATCH_CEILING = ConfiguredFeatures.register(
		"moss_patch_ceiling",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.MOSS_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.MOSS_BLOCK),
					() -> CAVE_VINE_IN_MOSS.withPlacement(),
					VerticalSurfaceType.CEILING,
					UniformIntProvider.create(1, 2),
					0.0F,
					5,
					0.08F,
					UniformIntProvider.create(4, 7),
					0.3F
				)
			)
	);
	public static final ConfiguredFeature<SimpleBlockFeatureConfig, ?> SPORE_BLOSSOM = ConfiguredFeatures.register(
		"spore_blossom", Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SPORE_BLOSSOM)))
	);
	public static final ConfiguredFeature<GeodeFeatureConfig, ?> AMETHYST_GEODE = ConfiguredFeatures.register(
		"amethyst_geode",
		Feature.GEODE
			.configure(
				new GeodeFeatureConfig(
					new GeodeLayerConfig(
						BlockStateProvider.of(Blocks.AIR),
						BlockStateProvider.of(Blocks.AMETHYST_BLOCK),
						BlockStateProvider.of(Blocks.BUDDING_AMETHYST),
						BlockStateProvider.of(Blocks.CALCITE),
						BlockStateProvider.of(Blocks.SMOOTH_BASALT),
						List.of(
							Blocks.SMALL_AMETHYST_BUD.getDefaultState(),
							Blocks.MEDIUM_AMETHYST_BUD.getDefaultState(),
							Blocks.LARGE_AMETHYST_BUD.getDefaultState(),
							Blocks.AMETHYST_CLUSTER.getDefaultState()
						),
						BlockTags.FEATURES_CANNOT_REPLACE.getId(),
						BlockTags.GEODE_INVALID_BLOCKS.getId()
					),
					new GeodeLayerThicknessConfig(1.7, 2.2, 3.2, 4.2),
					new GeodeCrackConfig(0.95, 2.0, 2),
					0.35,
					0.083,
					true,
					UniformIntProvider.create(4, 6),
					UniformIntProvider.create(3, 4),
					UniformIntProvider.create(1, 2),
					-16,
					16,
					0.05,
					1
				)
			)
	);

	private static PlacedFeature createBigDripleafFeature(Direction direction) {
		return Feature.BLOCK_COLUMN
			.configure(
				new BlockColumnFeatureConfig(
					List.of(
						BlockColumnFeatureConfig.createLayer(
							new WeightedListIntProvider(DataPool.<IntProvider>builder().add(UniformIntProvider.create(0, 4), 2).add(ConstantIntProvider.create(0), 1).build()),
							BlockStateProvider.of(Blocks.BIG_DRIPLEAF_STEM.getDefaultState().with(Properties.HORIZONTAL_FACING, direction))
						),
						BlockColumnFeatureConfig.createLayer(
							ConstantIntProvider.create(1), BlockStateProvider.of(Blocks.BIG_DRIPLEAF.getDefaultState().with(Properties.HORIZONTAL_FACING, direction))
						)
					),
					Direction.UP,
					BlockPredicate.IS_AIR_OR_WATER,
					true
				)
			)
			.withPlacement();
	}

	private static PlacedFeature createSmallDripleafFeature() {
		return Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(
						DataPool.<BlockState>builder()
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.EAST), 1)
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.WEST), 1)
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.NORTH), 1)
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.SOUTH), 1)
					)
				)
			)
			.withPlacement();
	}
}
