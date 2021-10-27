package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.List;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.CaveVinesHeadBlock;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SmallDripleafBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.dynamic.Range;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.ClampedNormalIntProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.util.math.noise.DoublePerlinNoiseSampler;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.decorator.BlockFilterDecoratorConfig;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.CaveSurfaceDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;
import net.minecraft.world.gen.decorator.CountNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.CountNoiseDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.EnvironmentScanDecoratorConfig;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.ScatterDecoratorConfig;
import net.minecraft.world.gen.decorator.SurfaceRelativeThresholdDecoratorConfig;
import net.minecraft.world.gen.decorator.WaterDepthThresholdDecoratorConfig;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.JungleFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.foliage.MegaPineFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.RandomSpreadFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.heightprovider.BiasedToBottomHeightProvider;
import net.minecraft.world.gen.heightprovider.UniformHeightProvider;
import net.minecraft.world.gen.heightprovider.VeryBiasedToBottomHeightProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.DualNoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseBlockStateProvider;
import net.minecraft.world.gen.stateprovider.NoiseThresholdBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.treedecorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.treedecorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.treedecorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.treedecorator.LeavesVineTreeDecorator;
import net.minecraft.world.gen.treedecorator.TrunkVineTreeDecorator;
import net.minecraft.world.gen.trunk.BendingTrunkPlacer;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class ConfiguredFeatures {
	public static final BlockPredicate ONLY_IN_AIR = BlockPredicate.matchingBlock(Blocks.AIR, BlockPos.ORIGIN);
	public static final BlockPredicate ONLY_IN_AIR_OR_WATER = BlockPredicate.matchingBlocks(List.of(Blocks.AIR, Blocks.WATER), BlockPos.ORIGIN);
	public static final ConfiguredFeature<?, ?> END_SPIKE = register(
		"end_spike", Feature.END_SPIKE.configure(new EndSpikeFeatureConfig(false, ImmutableList.of(), null))
	);
	public static final ConfiguredFeature<?, ?> END_GATEWAY = register(
		"end_gateway",
		Feature.END_GATEWAY
			.configure(EndGatewayFeatureConfig.createConfig(ServerWorld.END_SPAWN_POS, true))
			.decorate(Decorator.END_GATEWAY.configure(DecoratorConfig.DEFAULT))
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP)
			.spreadHorizontally()
			.applyChance(700)
	);
	public static final ConfiguredFeature<?, ?> END_GATEWAY_DELAYED = register(
		"end_gateway_delayed", Feature.END_GATEWAY.configure(EndGatewayFeatureConfig.createConfig())
	);
	public static final ConfiguredFeature<?, ?> CHORUS_PLANT = register(
		"chorus_plant", Feature.CHORUS_PLANT.configure(FeatureConfig.DEFAULT).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeatRandomly(4)
	);
	public static final ConfiguredFeature<?, ?> END_ISLAND = register("end_island", Feature.END_ISLAND.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> END_ISLAND_DECORATED = register(
		"end_island_decorated",
		END_ISLAND.uniformRange(YOffset.fixed(55), YOffset.fixed(70))
			.spreadHorizontally()
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(1, 0.25F, 1)))
			.applyChance(14)
	);
	public static final ConfiguredFeature<?, ?> DELTA = register(
		"delta",
		Feature.DELTA_FEATURE
			.configure(
				new DeltaFeatureConfig(
					Blocks.LAVA.getDefaultState(), Blocks.MAGMA_BLOCK.getDefaultState(), UniformIntProvider.create(3, 7), UniformIntProvider.create(0, 2)
				)
			)
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(40)))
	);
	public static final ConfiguredFeature<?, ?> SMALL_BASALT_COLUMNS = register(
		"small_basalt_columns",
		Feature.BASALT_COLUMNS
			.configure(new BasaltColumnsFeatureConfig(ConstantIntProvider.create(1), UniformIntProvider.create(1, 4)))
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> LARGE_BASALT_COLUMNS = register(
		"large_basalt_columns",
		Feature.BASALT_COLUMNS
			.configure(new BasaltColumnsFeatureConfig(UniformIntProvider.create(2, 3), UniformIntProvider.create(5, 10)))
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(2)))
	);
	public static final ConfiguredFeature<?, ?> BASALT_BLOBS = register(
		"basalt_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS
			.configure(new ReplaceBlobsFeatureConfig(Blocks.NETHERRACK.getDefaultState(), Blocks.BASALT.getDefaultState(), UniformIntProvider.create(3, 7)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP)
			.spreadHorizontally()
			.repeat(75)
	);
	public static final ConfiguredFeature<?, ?> BLACKSTONE_BLOBS = register(
		"blackstone_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS
			.configure(new ReplaceBlobsFeatureConfig(Blocks.NETHERRACK.getDefaultState(), Blocks.BLACKSTONE.getDefaultState(), UniformIntProvider.create(3, 7)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP)
			.spreadHorizontally()
			.repeat(25)
	);
	public static final ConfiguredFeature<?, ?> GLOWSTONE_EXTRA = register(
		"glowstone_extra",
		Feature.GLOWSTONE_BLOB
			.configure(FeatureConfig.DEFAULT)
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_4)
			.spreadHorizontally()
			.repeat(BiasedToBottomIntProvider.create(0, 9))
	);
	public static final ConfiguredFeature<?, ?> GLOWSTONE = register(
		"glowstone", Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> CRIMSON_FOREST_VEGETATION = register(
		"crimson_forest_vegetation",
		Feature.NETHER_FOREST_VEGETATION
			.configure(ConfiguredFeatures.Configs.CRIMSON_ROOTS_CONFIG)
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(6)))
	);
	public static final ConfiguredFeature<?, ?> WARPED_FOREST_VEGETATION = register(
		"warped_forest_vegetation",
		Feature.NETHER_FOREST_VEGETATION.configure(ConfiguredFeatures.Configs.WARPED_ROOTS_CONFIG).decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(5)))
	);
	public static final ConfiguredFeature<?, ?> NETHER_SPROUTS = register(
		"nether_sprouts",
		Feature.NETHER_FOREST_VEGETATION
			.configure(ConfiguredFeatures.Configs.NETHER_SPROUTS_CONFIG)
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> TWISTING_VINES = register(
		"twisting_vines", Feature.TWISTING_VINES.configure(FeatureConfig.DEFAULT).range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> WEEPING_VINES = register(
		"weeping_vines", Feature.WEEPING_VINES.configure(FeatureConfig.DEFAULT).range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> BASALT_PILLAR = register(
		"basalt_pillar", Feature.BASALT_PILLAR.configure(FeatureConfig.DEFAULT).range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_COLD = register(
		"seagrass_cold", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).repeat(32).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP_COLD = register(
		"seagrass_deep_cold", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).repeat(40).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_NORMAL = register(
		"seagrass_normal", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).repeat(48).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_RIVER = register(
		"seagrass_river", Feature.SEAGRASS.configure(new ProbabilityConfig(0.4F)).repeat(48).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP = register(
		"seagrass_deep", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).repeat(48).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_SWAMP = register(
		"seagrass_swamp", Feature.SEAGRASS.configure(new ProbabilityConfig(0.6F)).repeat(64).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_WARM = register(
		"seagrass_warm", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).repeat(80).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP_WARM = register(
		"seagrass_deep_warm", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).repeat(80).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> SEA_PICKLE = register(
		"sea_pickle", Feature.SEA_PICKLE.configure(new CountConfig(20)).decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP).applyChance(16)
	);
	public static final ConfiguredFeature<?, ?> ICE_SPIKE = register(
		"ice_spike", Feature.ICE_SPIKE.configure(FeatureConfig.DEFAULT).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeat(3)
	);
	public static final ConfiguredFeature<?, ?> ICE_PATCH = register(
		"ice_patch",
		Feature.ICE_PATCH
			.configure(
				new DiskFeatureConfig(
					Blocks.PACKED_ICE.getDefaultState(),
					UniformIntProvider.create(2, 3),
					1,
					ImmutableList.of(
						Blocks.DIRT.getDefaultState(),
						Blocks.GRASS_BLOCK.getDefaultState(),
						Blocks.PODZOL.getDefaultState(),
						Blocks.COARSE_DIRT.getDefaultState(),
						Blocks.MYCELIUM.getDefaultState(),
						Blocks.SNOW_BLOCK.getDefaultState(),
						Blocks.ICE.getDefaultState()
					)
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> FOREST_ROCK = register(
		"forest_rock",
		Feature.FOREST_ROCK
			.configure(new SingleStateFeatureConfig(Blocks.MOSSY_COBBLESTONE.getDefaultState()))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeatRandomly(2)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_SIMPLE = register(
		"seagrass_simple",
		Feature.SIMPLE_BLOCK
			.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SEAGRASS)))
			.decorate(
				Decorator.BLOCK_FILTER
					.configure(
						new BlockFilterDecoratorConfig(
							BlockPredicate.allOf(
								BlockPredicate.matchingBlock(Blocks.STONE, new BlockPos(0, -1, 0)),
								BlockPredicate.matchingBlock(Blocks.WATER, BlockPos.ORIGIN),
								BlockPredicate.matchingBlock(Blocks.WATER, new BlockPos(0, 1, 0))
							)
						)
					)
			)
			.applyChance(10)
			.decorate(Decorator.CARVING_MASK.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID)))
	);
	public static final ConfiguredFeature<?, ?> ICEBERG_PACKED = register(
		"iceberg_packed",
		Feature.ICEBERG
			.configure(new SingleStateFeatureConfig(Blocks.PACKED_ICE.getDefaultState()))
			.decorate(Decorator.ICEBERG.configure(NopeDecoratorConfig.INSTANCE))
			.applyChance(16)
	);
	public static final ConfiguredFeature<?, ?> ICEBERG_BLUE = register(
		"iceberg_blue",
		Feature.ICEBERG
			.configure(new SingleStateFeatureConfig(Blocks.BLUE_ICE.getDefaultState()))
			.decorate(Decorator.ICEBERG.configure(NopeDecoratorConfig.INSTANCE))
			.applyChance(200)
	);
	public static final ConfiguredFeature<?, ?> KELP_COLD = register(
		"kelp_cold",
		Feature.KELP
			.configure(FeatureConfig.DEFAULT)
			.decorate(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE_BIASED.configure(new CountNoiseBiasedDecoratorConfig(120, 80.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> KELP_WARM = register(
		"kelp_warm",
		Feature.KELP
			.configure(FeatureConfig.DEFAULT)
			.decorate(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE_BIASED.configure(new CountNoiseBiasedDecoratorConfig(80, 80.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> BLUE_ICE = register(
		"blue_ice", Feature.BLUE_ICE.configure(FeatureConfig.DEFAULT).uniformRange(YOffset.fixed(30), YOffset.fixed(61)).spreadHorizontally().repeatRandomly(19)
	);
	public static final ConfiguredFeature<?, ?> BAMBOO_LIGHT = register(
		"bamboo_light", Feature.BAMBOO.configure(new ProbabilityConfig(0.0F)).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(4)
	);
	public static final ConfiguredFeature<?, ?> BAMBOO = register(
		"bamboo",
		Feature.BAMBOO
			.configure(new ProbabilityConfig(0.2F))
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE_BIASED.configure(new CountNoiseBiasedDecoratorConfig(160, 80.0, 0.3)))
	);
	public static final ConfiguredFeature<?, ?> VINES = register(
		"vines", Feature.VINES.configure(FeatureConfig.DEFAULT).uniformRange(YOffset.fixed(64), YOffset.fixed(100)).spreadHorizontally().repeat(127)
	);
	public static final ConfiguredFeature<?, ?> LAKE_LAVA = register(
		"lake_lava",
		Feature.LAKE
			.configure(new SingleStateFeatureConfig(Blocks.LAVA.getDefaultState()))
			.decorate(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)))
			.range(new RangeDecoratorConfig(BiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.getTop(), 8)))
			.spreadHorizontally()
			.applyChance(8)
	);
	public static final ConfiguredFeature<?, ?> DISK_CLAY = register(
		"disk_clay",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					Blocks.CLAY.getDefaultState(), UniformIntProvider.create(2, 3), 1, ImmutableList.of(Blocks.DIRT.getDefaultState(), Blocks.CLAY.getDefaultState())
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> DISK_GRAVEL = register(
		"disk_gravel",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					Blocks.GRAVEL.getDefaultState(), UniformIntProvider.create(2, 5), 2, ImmutableList.of(Blocks.DIRT.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState())
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> DISK_SAND = register(
		"disk_sand",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					Blocks.SAND.getDefaultState(), UniformIntProvider.create(2, 6), 2, ImmutableList.of(Blocks.DIRT.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState())
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
			.repeat(3)
	);
	public static final ConfiguredFeature<?, ?> FREEZE_TOP_LAYER = register("freeze_top_layer", Feature.FREEZE_TOP_LAYER.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> BONUS_CHEST = register("bonus_chest", Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> VOID_START_PLATFORM = register("void_start_platform", Feature.VOID_START_PLATFORM.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> MONSTER_ROOM = register(
		"monster_room",
		Feature.MONSTER_ROOM
			.configure(FeatureConfig.DEFAULT)
			.range(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.fixed(0), YOffset.getTop())))
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> MONSTER_ROOM_DEEP = register(
		"monster_room_deep",
		Feature.MONSTER_ROOM
			.configure(FeatureConfig.DEFAULT)
			.range(new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.aboveBottom(6), YOffset.fixed(-1))))
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> DESERT_WELL = register(
		"desert_well", Feature.DESERT_WELL.configure(FeatureConfig.DEFAULT).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(1000)
	);
	private static final ImmutableList<Identifier> FOSSIL_STRUCTURES = ImmutableList.of(
		new Identifier("fossil/spine_1"),
		new Identifier("fossil/spine_2"),
		new Identifier("fossil/spine_3"),
		new Identifier("fossil/spine_4"),
		new Identifier("fossil/skull_1"),
		new Identifier("fossil/skull_2"),
		new Identifier("fossil/skull_3"),
		new Identifier("fossil/skull_4")
	);
	private static final ImmutableList<Identifier> FOSSIL_OVERLAY_STRUCTURES = ImmutableList.of(
		new Identifier("fossil/spine_1_coal"),
		new Identifier("fossil/spine_2_coal"),
		new Identifier("fossil/spine_3_coal"),
		new Identifier("fossil/spine_4_coal"),
		new Identifier("fossil/skull_1_coal"),
		new Identifier("fossil/skull_2_coal"),
		new Identifier("fossil/skull_3_coal"),
		new Identifier("fossil/skull_4_coal")
	);
	public static final ConfiguredFeature<?, ?> FOSSIL_UPPER = register(
		"fossil_upper",
		Feature.FOSSIL
			.configure(new FossilFeatureConfig(FOSSIL_STRUCTURES, FOSSIL_OVERLAY_STRUCTURES, StructureProcessorLists.FOSSIL_ROT, StructureProcessorLists.FOSSIL_COAL, 4))
			.uniformRange(YOffset.fixed(0), YOffset.getTop())
			.applyChance(64)
	);
	public static final ConfiguredFeature<?, ?> FOSSIL_LOWER = register(
		"fossil_lower",
		Feature.FOSSIL
			.configure(
				new FossilFeatureConfig(FOSSIL_STRUCTURES, FOSSIL_OVERLAY_STRUCTURES, StructureProcessorLists.FOSSIL_ROT, StructureProcessorLists.FOSSIL_DIAMONDS, 4)
			)
			.uniformRange(YOffset.getBottom(), YOffset.fixed(-8))
			.applyChance(64)
	);
	public static final ConfiguredFeature<?, ?> SPRING_LAVA_DOUBLE = register(
		"spring_lava_double",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.LAVA_SPRING_CONFIG)
			.range(new RangeDecoratorConfig(VeryBiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)))
			.spreadHorizontally()
			.repeat(40)
	);
	public static final ConfiguredFeature<?, ?> SPRING_LAVA = register(
		"spring_lava",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.LAVA_SPRING_CONFIG)
			.range(new RangeDecoratorConfig(VeryBiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)))
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> SPRING_LAVA_FROZEN = register(
		"spring_lava_frozen",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.field_35560)
			.range(new RangeDecoratorConfig(VeryBiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)))
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> SPRING_DELTA = register(
		"spring_delta",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(
					Fluids.LAVA.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE)
				)
			)
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_4)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> SPRING_CLOSED = register(
		"spring_closed",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.ENCLOSED_NETHER_SPRING_CONFIG)
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> SPRING_CLOSED_DOUBLE = register(
		"spring_closed_double",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.ENCLOSED_NETHER_SPRING_CONFIG)
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(32)
	);
	public static final ConfiguredFeature<?, ?> SPRING_OPEN = register(
		"spring_open",
		Feature.SPRING_FEATURE
			.configure(new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_4)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> SPRING_WATER = register(
		"spring_water",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(
					Fluids.WATER.getDefaultState(),
					true,
					4,
					1,
					ImmutableSet.of(
						Blocks.STONE,
						Blocks.GRANITE,
						Blocks.DIORITE,
						Blocks.ANDESITE,
						Blocks.DEEPSLATE,
						Blocks.TUFF,
						Blocks.CALCITE,
						Blocks.DIRT,
						Blocks.SNOW_BLOCK,
						Blocks.POWDER_SNOW,
						Blocks.PACKED_ICE
					)
				)
			)
			.uniformRange(YOffset.getBottom(), YOffset.fixed(192))
			.spreadHorizontally()
			.repeat(25)
	);
	public static final ConfiguredFeature<?, ?> PILE_HAY = register(
		"pile_hay", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new PillarBlockStateProvider(Blocks.HAY_BLOCK)))
	);
	public static final ConfiguredFeature<?, ?> PILE_MELON = register(
		"pile_melon", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(BlockStateProvider.of(Blocks.MELON)))
	);
	public static final ConfiguredFeature<?, ?> PILE_SNOW = register(
		"pile_snow", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(BlockStateProvider.of(Blocks.SNOW)))
	);
	public static final ConfiguredFeature<?, ?> PILE_ICE = register(
		"pile_ice",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(new WeightedBlockStateProvider(pool().add(Blocks.BLUE_ICE.getDefaultState(), 1).add(Blocks.PACKED_ICE.getDefaultState(), 5)))
			)
	);
	public static final ConfiguredFeature<?, ?> PILE_PUMPKIN = register(
		"pile_pumpkin",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(new WeightedBlockStateProvider(pool().add(Blocks.PUMPKIN.getDefaultState(), 19).add(Blocks.JACK_O_LANTERN.getDefaultState(), 1)))
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_FIRE = register(
		"patch_fire",
		Feature.RANDOM_PATCH
			.configure(
				createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.FIRE))), List.of(Blocks.NETHERRACK))
			)
			.decorate(ConfiguredFeatures.Decorators.FIRE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SOUL_FIRE = register(
		"patch_soul_fire",
		Feature.RANDOM_PATCH
			.configure(
				createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SOUL_FIRE))), List.of(Blocks.SOUL_SOIL))
			)
			.decorate(ConfiguredFeatures.Decorators.FIRE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BROWN_MUSHROOM = register(
		"patch_brown_mushroom",
		Feature.RANDOM_PATCH
			.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BROWN_MUSHROOM)))))
	);
	public static final ConfiguredFeature<?, ?> PATCH_RED_MUSHROOM = register(
		"patch_red_mushroom",
		Feature.RANDOM_PATCH
			.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.RED_MUSHROOM)))))
	);
	public static final ConfiguredFeature<?, ?> PATCH_CRIMSON_ROOTS = register(
		"patch_crimson_roots",
		Feature.RANDOM_PATCH
			.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.CRIMSON_ROOTS)))))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUNFLOWER = register(
		"patch_sunflower",
		Feature.RANDOM_PATCH
			.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SUNFLOWER)))))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(3)
	);
	public static final ConfiguredFeature<?, ?> PATCH_PUMPKIN = register(
		"patch_pumpkin",
		Feature.RANDOM_PATCH
			.configure(
				createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PUMPKIN))), List.of(Blocks.GRASS_BLOCK))
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(2048)
	);
	public static final ConfiguredFeature<?, ?> PATCH_TAIGA_GRASS = register(
		"patch_taiga_grass", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_BUSH = register(
		"patch_berry_bush", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.SWEET_BERRY_BUSH_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_PLAIN = register(
		"patch_grass_plain",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE.configure(new CountNoiseDecoratorConfig(-0.8, 5, 10)))
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_FOREST = register(
		"patch_grass_forest",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_BADLANDS = register(
		"patch_grass_badlands",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE).spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_SAVANNA = register(
		"patch_grass_savanna",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_NORMAL = register(
		"patch_grass_normal",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA_2 = register(
		"patch_grass_taiga_2",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA = register(
		"patch_grass_taiga",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_JUNGLE = register(
		"patch_grass_jungle",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.LUSH_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(25)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_2 = register(
		"patch_dead_bush_2",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH = register(
		"patch_dead_bush",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_BADLANDS = register(
		"patch_dead_bush_badlands",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_MELON = register(
		"patch_melon",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig(
					64,
					7,
					3,
					() -> Feature.SIMPLE_BLOCK
							.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.MELON)))
							.applyBlockFilter(BlockPredicate.bothOf(BlockPredicate.replaceable(), BlockPredicate.matchingBlock(Blocks.GRASS_BLOCK, new BlockPos(0, -1, 0))))
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(64)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_COMMON = register(
		"patch_berry_common", PATCH_BERRY_BUSH.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE).spreadHorizontally().applyChance(128)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_RARE = register(
		"patch_berry_rare", PATCH_BERRY_BUSH.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE).spreadHorizontally().applyChance(1536)
	);
	public static final ConfiguredFeature<?, ?> PATCH_WATERLILLY = register(
		"patch_waterlilly",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig(
					10, 7, 3, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_PAD))).onlyInAir()
				)
			)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS_2 = register(
		"patch_tall_grass_2",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.TALL_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP)
			.spreadHorizontally()
			.applyChance(32)
			.decorate(Decorator.COUNT_NOISE.configure(new CountNoiseDecoratorConfig(-0.8, 0, 7)))
	);
	public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS = register(
		"patch_tall_grass",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.TALL_GRASS_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_LARGE_FERN = register(
		"patch_large_fern",
		Feature.RANDOM_PATCH
			.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LARGE_FERN)))))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS = register(
		"patch_cactus",
		Feature.RANDOM_PATCH
			.configure(
				createRandomPatchFeature(
					Feature.BLOCK_COLUMN
						.configure(BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(1, 3), BlockStateProvider.of(Blocks.CACTUS)))
						.wouldSurvive(Blocks.CACTUS),
					List.of(),
					10
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DESERT = register(
		"patch_cactus_desert", PATCH_CACTUS.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(6)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DECORATED = register(
		"patch_cactus_decorated", PATCH_CACTUS.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(13)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_SWAMP = register(
		"patch_sugar_cane_swamp",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(3)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_DESERT = register(
		"patch_sugar_cane_desert",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_BADLANDS = register(
		"patch_sugar_cane_badlands",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE = register(
		"patch_sugar_cane",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(6)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NETHER = register(
		"brown_mushroom_nether", PATCH_BROWN_MUSHROOM.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).applyChance(2)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NETHER = register(
		"red_mushroom_nether", PATCH_RED_MUSHROOM.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).applyChance(2)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NORMAL = register(
		"brown_mushroom_normal", PATCH_BROWN_MUSHROOM.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(256)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NORMAL = register(
		"red_mushroom_normal", PATCH_RED_MUSHROOM.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(512)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_TAIGA = register(
		"brown_mushroom_taiga", PATCH_BROWN_MUSHROOM.applyChance(4).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_TAIGA = register(
		"red_mushroom_taiga", PATCH_RED_MUSHROOM.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(512)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_GIANT = register("brown_mushroom_giant", BROWN_MUSHROOM_TAIGA.repeat(3));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_GIANT = register("red_mushroom_giant", RED_MUSHROOM_TAIGA.repeat(3));
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_SWAMP = register("brown_mushroom_swamp", BROWN_MUSHROOM_TAIGA.repeat(8));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_SWAMP = register("red_mushroom_swamp", RED_MUSHROOM_TAIGA.repeat(8));
	public static final ImmutableList<OreFeatureConfig.Target> IRON_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.IRON_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_IRON_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> REDSTONE_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.REDSTONE_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_REDSTONE_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> GOLD_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.GOLD_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_GOLD_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> DIAMOND_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.DIAMOND_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> LAPIS_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.LAPIS_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> EMERALD_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.EMERALD_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_EMERALD_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> COPPER_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.COPPER_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_COPPER_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> COAL_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.COAL_ORE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.DEEPSLATE_COAL_ORE.getDefaultState())
	);
	public static final ImmutableList<OreFeatureConfig.Target> INFESTED_TARGETS = ImmutableList.of(
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, Blocks.INFESTED_STONE.getDefaultState()),
		OreFeatureConfig.createTarget(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, Blocks.INFESTED_DEEPSLATE.getDefaultState())
	);
	public static final OreFeatureConfig IRON_CONFIG = new OreFeatureConfig(IRON_ORE_TARGETS, 9);
	public static final OreFeatureConfig REDSTONE_CONFIG = new OreFeatureConfig(REDSTONE_ORE_TARGETS, 8);
	public static final ConfiguredFeature<?, ?> ORE_MAGMA = register(
		"ore_magma",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.MAGMA_BLOCK.getDefaultState(), 33))
			.uniformRange(YOffset.fixed(27), YOffset.fixed(36))
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_SOUL_SAND = register(
		"ore_soul_sand",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.SOUL_SAND.getDefaultState(), 12))
			.uniformRange(YOffset.getBottom(), YOffset.fixed(31))
			.spreadHorizontally()
			.repeat(12)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_DELTAS = register(
		"ore_gold_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.NETHER_GOLD_ORE.getDefaultState(), 10))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_DELTAS = register(
		"ore_quartz_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(32)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_NETHER = register(
		"ore_gold_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.NETHER_GOLD_ORE.getDefaultState(), 10))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_NETHER = register(
		"ore_quartz_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.NETHER_QUARTZ_ORE.getDefaultState(), 14))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL_NETHER = register(
		"ore_gravel_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.GRAVEL.getDefaultState(), 33))
			.uniformRange(YOffset.fixed(5), YOffset.fixed(41))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_BLACKSTONE = register(
		"ore_blackstone",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, Blocks.BLACKSTONE.getDefaultState(), 33))
			.uniformRange(YOffset.fixed(5), YOffset.fixed(31))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIRT = register(
		"ore_dirt",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.DIRT.getDefaultState(), 33))
			.uniformRange(YOffset.fixed(0), YOffset.fixed(160))
			.spreadHorizontally()
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL = register(
		"ore_gravel",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.GRAVEL.getDefaultState(), 33))
			.uniformRange(YOffset.getBottom(), YOffset.getTop())
			.spreadHorizontally()
			.repeat(14)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRANITE_UPPER = register(
		"ore_granite_upper",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.GRANITE.getDefaultState(), 64))
			.uniformRange(YOffset.fixed(64), YOffset.fixed(128))
			.spreadHorizontally()
			.applyChance(6)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRANITE_LOWER = register(
		"ore_granite_lower",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.GRANITE.getDefaultState(), 64))
			.uniformRange(YOffset.fixed(0), YOffset.fixed(60))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIORITE_UPPER = register(
		"ore_diorite_upper",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.DIORITE.getDefaultState(), 64))
			.uniformRange(YOffset.fixed(64), YOffset.fixed(128))
			.spreadHorizontally()
			.applyChance(6)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIORITE_LOWER = register(
		"ore_diorite_lower",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.DIORITE.getDefaultState(), 64))
			.uniformRange(YOffset.fixed(0), YOffset.fixed(60))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_ANDESITE_UPPER = register(
		"ore_andesite_upper",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.ANDESITE.getDefaultState(), 64))
			.uniformRange(YOffset.fixed(64), YOffset.fixed(128))
			.spreadHorizontally()
			.applyChance(6)
	);
	public static final ConfiguredFeature<?, ?> ORE_ANDESITE_LOWER = register(
		"ore_andesite_lower",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.ANDESITE.getDefaultState(), 64))
			.uniformRange(YOffset.fixed(0), YOffset.fixed(60))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_TUFF = register(
		"ore_tuff",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.TUFF.getDefaultState(), 64))
			.uniformRange(YOffset.getBottom(), YOffset.fixed(0))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_COAL_UPPER = register(
		"ore_coal_upper",
		Feature.ORE.configure(new OreFeatureConfig(COAL_ORE_TARGETS, 17)).uniformRange(YOffset.fixed(136), YOffset.getTop()).spreadHorizontally().repeat(30)
	);
	public static final ConfiguredFeature<?, ?> ORE_COAL_LOWER = register(
		"ore_coal_lower",
		Feature.ORE.configure(new OreFeatureConfig(COAL_ORE_TARGETS, 17, 0.5F)).triangleRange(YOffset.fixed(0), YOffset.fixed(192)).spreadHorizontally().repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_IRON_UPPER = register(
		"ore_iron_upper", Feature.ORE.configure(IRON_CONFIG).triangleRange(YOffset.fixed(80), YOffset.fixed(384)).spreadHorizontally().repeat(90)
	);
	public static final ConfiguredFeature<?, ?> ORE_IRON_MIDDLE = register(
		"ore_iron_middle", Feature.ORE.configure(IRON_CONFIG).triangleRange(YOffset.fixed(-24), YOffset.fixed(56)).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_IRON_SMALL = register(
		"ore_iron_small",
		Feature.ORE.configure(new OreFeatureConfig(IRON_ORE_TARGETS, 4)).uniformRange(YOffset.getBottom(), YOffset.fixed(72)).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_EXTRA = register(
		"ore_gold_extra",
		Feature.ORE.configure(new OreFeatureConfig(GOLD_ORE_TARGETS, 9)).uniformRange(YOffset.fixed(32), YOffset.fixed(256)).spreadHorizontally().repeat(50)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD = register(
		"ore_gold",
		Feature.ORE.configure(new OreFeatureConfig(GOLD_ORE_TARGETS, 9, 0.5F)).triangleRange(YOffset.fixed(-64), YOffset.fixed(32)).spreadHorizontally().repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_LOWER = register(
		"ore_gold_lower",
		Feature.ORE
			.configure(new OreFeatureConfig(GOLD_ORE_TARGETS, 9, 0.5F))
			.uniformRange(YOffset.fixed(-64), YOffset.fixed(-48))
			.spreadHorizontally()
			.repeatRandomly(1)
	);
	public static final ConfiguredFeature<?, ?> ORE_REDSTONE = register(
		"ore_redstone", Feature.ORE.configure(REDSTONE_CONFIG).uniformRange(YOffset.getBottom(), YOffset.fixed(15)).spreadHorizontally().repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_REDSTONE_LOWER = register(
		"ore_redstone_lower", Feature.ORE.configure(REDSTONE_CONFIG).triangleRange(YOffset.aboveBottom(-32), YOffset.aboveBottom(32)).spreadHorizontally().repeat(8)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND = register(
		"ore_diamond",
		Feature.ORE
			.configure(new OreFeatureConfig(DIAMOND_ORE_TARGETS, 4, 0.5F))
			.triangleRange(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))
			.spreadHorizontally()
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND_LARGE = register(
		"ore_diamond_large",
		Feature.ORE
			.configure(new OreFeatureConfig(DIAMOND_ORE_TARGETS, 12, 0.7F))
			.triangleRange(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))
			.spreadHorizontally()
			.applyChance(9)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND_BURIED = register(
		"ore_diamond_buried",
		Feature.ORE
			.configure(new OreFeatureConfig(DIAMOND_ORE_TARGETS, 8, 1.0F))
			.triangleRange(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_LAPIS = register(
		"ore_lapis",
		Feature.ORE.configure(new OreFeatureConfig(LAPIS_ORE_TARGETS, 7)).triangleRange(YOffset.fixed(-32), YOffset.fixed(32)).spreadHorizontally().repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_LAPIS_BURIED = register(
		"ore_lapis_buried",
		Feature.ORE.configure(new OreFeatureConfig(LAPIS_ORE_TARGETS, 7, 1.0F)).uniformRange(YOffset.getBottom(), YOffset.fixed(64)).spreadHorizontally().repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_INFESTED = register(
		"ore_infested",
		Feature.ORE.configure(new OreFeatureConfig(INFESTED_TARGETS, 9)).uniformRange(YOffset.getBottom(), YOffset.fixed(63)).spreadHorizontally().repeat(14)
	);
	public static final ConfiguredFeature<?, ?> ORE_EMERALD = register(
		"ore_emerald",
		Feature.ORE.configure(new OreFeatureConfig(EMERALD_ORE_TARGETS, 3)).triangleRange(YOffset.fixed(-16), YOffset.fixed(480)).spreadHorizontally().repeat(100)
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_LARGE = register(
		"ore_debris_large",
		Feature.SCATTERED_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, Blocks.ANCIENT_DEBRIS.getDefaultState(), 3, 1.0F))
			.triangleRange(YOffset.fixed(8), YOffset.fixed(24))
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_SMALL = register(
		"ore_debris_small",
		Feature.SCATTERED_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, Blocks.ANCIENT_DEBRIS.getDefaultState(), 2, 1.0F))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_8)
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> ORE_COPPER = register(
		"ore_copper",
		Feature.ORE.configure(new OreFeatureConfig(COPPER_ORE_TARGETS, 10)).triangleRange(YOffset.fixed(-16), YOffset.fixed(112)).spreadHorizontally().repeat(16)
	);
	public static final ConfiguredFeature<?, ?> ORE_COPPER_LARGE = register(
		"ore_copper_large",
		Feature.ORE.configure(new OreFeatureConfig(COPPER_ORE_TARGETS, 20)).triangleRange(YOffset.fixed(-16), YOffset.fixed(112)).spreadHorizontally().repeat(16)
	);
	public static final ConfiguredFeature<?, ?> ORE_CLAY = register(
		"ore_clay",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, Blocks.CLAY.getDefaultState(), 33))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(38)
	);
	public static final ConfiguredFeature<?, ?> DRIPSTONE_CLUSTER = register(
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
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(35, 70))
	);
	public static final ConfiguredFeature<?, ?> LARGE_DRIPSTONE = register(
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
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(7, 35))
	);
	public static final ConfiguredFeature<?, ?> POINTED_DRIPSTONE = register(
		"pointed_dripstone",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(
				new SimpleRandomFeatureConfig(
					ImmutableList.of(
						() -> Feature.POINTED_DRIPSTONE
								.configure(new SmallDripstoneFeatureConfig(0.2F, 0.7F, 0.5F, 0.5F))
								.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.FLOOR, 12, true))),
						() -> Feature.POINTED_DRIPSTONE
								.configure(new SmallDripstoneFeatureConfig(0.2F, 0.7F, 0.5F, 0.5F))
								.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12, true)))
					)
				)
			)
			.decorate(
				Decorator.SCATTER
					.configure(
						new ScatterDecoratorConfig(ClampedNormalIntProvider.method_39156(0.0F, 3.0F, -10, 10), ClampedNormalIntProvider.method_39156(0.0F, 0.6F, -2, 2))
					)
			)
			.repeat(UniformIntProvider.create(1, 5))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(140, 220))
	);
	public static final ConfiguredFeature<?, ?> UNDERWATER_MAGMA = register(
		"underwater_magma",
		Feature.UNDERWATER_MAGMA
			.configure(new UnderwaterMagmaFeatureConfig(5, 1, 0.5F))
			.decorate(Decorator.SURFACE_RELATIVE_THRESHOLD.configure(new SurfaceRelativeThresholdDecoratorConfig(Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -2)))
			.spreadHorizontally()
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.repeat(UniformIntProvider.create(44, 52))
	);
	public static final ConfiguredFeature<?, ?> GLOW_LICHEN = register(
		"glow_lichen",
		Feature.GLOW_LICHEN
			.configure(
				new GlowLichenFeatureConfig(
					20,
					false,
					true,
					true,
					0.5F,
					ImmutableList.of(
						Blocks.STONE.getDefaultState(),
						Blocks.ANDESITE.getDefaultState(),
						Blocks.DIORITE.getDefaultState(),
						Blocks.GRANITE.getDefaultState(),
						Blocks.DRIPSTONE_BLOCK.getDefaultState(),
						Blocks.CALCITE.getDefaultState(),
						Blocks.TUFF.getDefaultState(),
						Blocks.DEEPSLATE.getDefaultState()
					)
				)
			)
			.decorate(Decorator.SURFACE_RELATIVE_THRESHOLD.configure(new SurfaceRelativeThresholdDecoratorConfig(Heightmap.Type.OCEAN_FLOOR_WG, Integer.MIN_VALUE, -13)))
			.spreadHorizontally()
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.repeat(UniformIntProvider.create(104, 157))
	);
	public static final ConfiguredFeature<?, ?> CRIMSON_FUNGI = register(
		"crimson_fungi",
		Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_NOT_PLANTED_CONFIG).decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(8)))
	);
	public static final ConfiguredFeature<HugeFungusFeatureConfig, ?> CRIMSON_FUNGI_PLANTED = register(
		"crimson_fungi_planted", Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> WARPED_FUNGI = register(
		"warped_fungi",
		Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_NOT_PLANTED_CONFIG).decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(8)))
	);
	public static final ConfiguredFeature<HugeFungusFeatureConfig, ?> WARPED_FUNGI_PLANTED = register(
		"warped_fungi_planted", Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> HUGE_BROWN_MUSHROOM = register(
		"huge_brown_mushroom",
		Feature.HUGE_BROWN_MUSHROOM
			.configure(
				new HugeMushroomFeatureConfig(
					BlockStateProvider.of(
						Blocks.BROWN_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(true)).with(MushroomBlock.DOWN, Boolean.valueOf(false))
					),
					BlockStateProvider.of(
						Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(false)).with(MushroomBlock.DOWN, Boolean.valueOf(false))
					),
					3
				)
			)
	);
	public static final ConfiguredFeature<?, ?> HUGE_RED_MUSHROOM = register(
		"huge_red_mushroom",
		Feature.HUGE_RED_MUSHROOM
			.configure(
				new HugeMushroomFeatureConfig(
					BlockStateProvider.of(Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false))),
					BlockStateProvider.of(
						Blocks.MUSHROOM_STEM.getDefaultState().with(MushroomBlock.UP, Boolean.valueOf(false)).with(MushroomBlock.DOWN, Boolean.valueOf(false))
					),
					2
				)
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK = register("oak", Feature.TREE.configure(buildOakTree().build()));
	public static final ConfiguredFeature<?, ?> OAK_CHECKED = register("oak_checked", OAK.wouldSurvive(Blocks.OAK_SAPLING));
	public static final ConfiguredFeature<TreeFeatureConfig, ?> DARK_OAK = register(
		"dark_oak",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.DARK_OAK_LOG),
						new DarkOakTrunkPlacer(6, 2, 1),
						BlockStateProvider.of(Blocks.DARK_OAK_LEAVES),
						new DarkOakFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0)),
						new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> DARK_OAK_CHECKED = register("dark_oak_checked", DARK_OAK.wouldSurvive(Blocks.DARK_OAK_SAPLING));
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH = register("birch", Feature.TREE.configure(buildBirchTree().build()));
	public static final ConfiguredFeature<?, ?> BIRCH_CHECKED = register("birch_checked", BIRCH.wouldSurvive(Blocks.BIRCH_SAPLING));
	public static final ConfiguredFeature<TreeFeatureConfig, ?> ACACIA = register(
		"acacia",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.ACACIA_LOG),
						new ForkingTrunkPlacer(5, 2, 2),
						BlockStateProvider.of(Blocks.ACACIA_LEAVES),
						new AcaciaFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
						new TwoLayersFeatureSize(1, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> ACACIA_CHECKED = register("acacia_checked", ACACIA.wouldSurvive(Blocks.ACACIA_SAPLING));
	public static final ConfiguredFeature<TreeFeatureConfig, ?> SPRUCE = register(
		"spruce",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.SPRUCE_LOG),
						new StraightTrunkPlacer(5, 2, 1),
						BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
						new SpruceFoliagePlacer(UniformIntProvider.create(2, 3), UniformIntProvider.create(0, 2), UniformIntProvider.create(1, 2)),
						new TwoLayersFeatureSize(2, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> SPRUCE_CHECKED = register("spruce_checked", SPRUCE.wouldSurvive(Blocks.SPRUCE_SAPLING));
	public static final BlockPredicate ONLY_ON_SNOW = BlockPredicate.matchingBlocks(List.of(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW), new BlockPos(0, -1, 0));
	public static final ConfiguredDecorator<?> MOVE_ONTO_SNOW_DECORATOR = Decorator.BLOCK_FILTER
		.configure(new BlockFilterDecoratorConfig(ONLY_ON_SNOW))
		.decorate(
			Decorator.ENVIRONMENT_SCAN
				.configure(new EnvironmentScanDecoratorConfig(Direction.UP, BlockPredicate.not(BlockPredicate.matchingBlock(Blocks.POWDER_SNOW, BlockPos.ORIGIN)), 8))
		);
	public static final ConfiguredFeature<?, ?> SPRUCE_ON_SNOW = register("spruce_on_snow", SPRUCE.decorate(MOVE_ONTO_SNOW_DECORATOR));
	public static final ConfiguredFeature<TreeFeatureConfig, ?> PINE = register(
		"pine",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.SPRUCE_LOG),
						new StraightTrunkPlacer(6, 4, 0),
						BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
						new PineFoliagePlacer(ConstantIntProvider.create(1), ConstantIntProvider.create(1), UniformIntProvider.create(3, 4)),
						new TwoLayersFeatureSize(2, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> PINE_CHECKED = register("pine_checked", PINE.wouldSurvive(Blocks.SPRUCE_SAPLING));
	public static final ConfiguredFeature<?, ?> PINE_ON_SNOW = register("pine_on_snow", PINE.decorate(MOVE_ONTO_SNOW_DECORATOR));
	public static final ConfiguredFeature<?, ?> JUNGLE_TREE = register(
		"jungle_tree",
		Feature.TREE
			.configure(
				buildJungleTree()
					.decorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), TrunkVineTreeDecorator.INSTANCE, LeavesVineTreeDecorator.INSTANCE))
					.ignoreVines()
					.build()
			)
			.wouldSurvive(Blocks.JUNGLE_SAPLING)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK = register("fancy_oak", Feature.TREE.configure(buildLargeOakTree().build()));
	public static final ConfiguredFeature<?, ?> FANCY_OAK_CHECKED = register("fancy_oak_checked", FANCY_OAK.wouldSurvive(Blocks.OAK_SAPLING));
	public static final ConfiguredFeature<?, ?> JUNGLE_TREE_NO_VINE = register(
		"jungle_tree_no_vine", Feature.TREE.configure(buildJungleTree().ignoreVines().build())
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_JUNGLE_TREE = register(
		"mega_jungle_tree",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.JUNGLE_LOG),
						new MegaJungleTrunkPlacer(10, 2, 19),
						BlockStateProvider.of(Blocks.JUNGLE_LEAVES),
						new JungleFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 2),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE, LeavesVineTreeDecorator.INSTANCE))
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> MEGA_JUNGLE_TREE_CHECKED = register(
		"mega_jungle_tree_checked", MEGA_JUNGLE_TREE.wouldSurvive(Blocks.JUNGLE_SAPLING)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_SPRUCE = register(
		"mega_spruce",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.SPRUCE_LOG),
						new GiantTrunkPlacer(13, 2, 14),
						BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
						new MegaPineFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0), UniformIntProvider.create(13, 17)),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(BlockStateProvider.of(Blocks.PODZOL))))
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> MEGA_SPRUCE_CHECKED = register("mega_spruce_checked", MEGA_SPRUCE.wouldSurvive(Blocks.SPRUCE_SAPLING));
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_PINE = register(
		"mega_pine",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.SPRUCE_LOG),
						new GiantTrunkPlacer(13, 2, 14),
						BlockStateProvider.of(Blocks.SPRUCE_LEAVES),
						new MegaPineFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0), UniformIntProvider.create(3, 7)),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(BlockStateProvider.of(Blocks.PODZOL))))
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> MEGA_PINE_CHECKED = register("mega_pine_checked", MEGA_PINE.wouldSurvive(Blocks.SPRUCE_SAPLING));
	public static final ConfiguredFeature<?, ?> SUPER_BIRCH_BEES_0002 = register(
		"super_birch_bees_0002",
		Feature.TREE
			.configure(buildTallBirchTree().decorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)).build())
			.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> SUPER_BIRCH_BEES = register(
		"super_birch_bees",
		Feature.TREE
			.configure(buildTallBirchTree().decorators(ImmutableList.of(ConfiguredFeatures.Decorators.HALF_BEEHIVES_TREES)).build())
			.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> SWAMP_OAK = register(
		"swamp_oak",
		Feature.TREE
			.configure(buildTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 5, 3, 0, 3).decorators(ImmutableList.of(LeavesVineTreeDecorator.INSTANCE)).build())
			.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> JUNGLE_BUSH = register(
		"jungle_bush",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.JUNGLE_LOG),
						new StraightTrunkPlacer(1, 0, 0),
						BlockStateProvider.of(Blocks.OAK_LEAVES),
						new BushFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1), 2),
						new TwoLayersFeatureSize(0, 0, 0)
					)
					.build()
			)
			.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> AZALEA_TREE = register(
		"azalea_tree",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						BlockStateProvider.of(Blocks.OAK_LOG),
						new BendingTrunkPlacer(4, 2, 0, 3, UniformIntProvider.create(1, 2)),
						new WeightedBlockStateProvider(pool().add(Blocks.AZALEA_LEAVES.getDefaultState(), 3).add(Blocks.FLOWERING_AZALEA_LEAVES.getDefaultState(), 1)),
						new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.dirtProvider(BlockStateProvider.of(Blocks.ROOTED_DIRT))
					.forceDirt()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> OAK_BEES_0002 = register(
		"oak_bees_0002",
		Feature.TREE.configure(buildOakTree().decorators(List.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)).build()).wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> OAK_BEES_002 = register(
		"oak_bees_002",
		Feature.TREE.configure(buildOakTree().decorators(List.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)).build()).wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BEES_005 = register(
		"oak_bees_005", Feature.TREE.configure(buildOakTree().decorators(List.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)).build())
	);
	public static final ConfiguredFeature<?, ?> BIRCH_BEES_0002 = register(
		"birch_bees_0002",
		Feature.TREE
			.configure(buildBirchTree().decorators(List.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)).build())
			.wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> BIRCH_BEES_002 = register(
		"birch_bees_002",
		Feature.TREE.configure(buildBirchTree().decorators(List.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)).build()).wouldSurvive(Blocks.BIRCH_SAPLING)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_BEES_005 = register(
		"birch_bees_005", Feature.TREE.configure(buildBirchTree().decorators(List.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)).build())
	);
	public static final ConfiguredFeature<?, ?> FANCY_OAK_BEES_0002 = register(
		"fancy_oak_bees_0002",
		Feature.TREE
			.configure(buildLargeOakTree().decorators(List.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)).build())
			.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> FANCY_OAK_BEES_002 = register(
		"fancy_oak_bees_002",
		Feature.TREE
			.configure(buildLargeOakTree().decorators(List.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)).build())
			.wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK_BEES_005 = register(
		"fancy_oak_bees_005", Feature.TREE.configure(buildLargeOakTree().decorators(List.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)).build())
	);
	public static final ConfiguredFeature<?, ?> FANCY_OAK_BEES = register(
		"fancy_oak_bees",
		Feature.TREE.configure(buildLargeOakTree().decorators(List.of(ConfiguredFeatures.Decorators.HALF_BEEHIVES_TREES)).build()).wouldSurvive(Blocks.OAK_SAPLING)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_WARM = register(
		"flower_warm",
		Feature.FLOWER.configure(ConfiguredFeatures.Configs.DEFAULT_FLOWER_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(16)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_DEFAULT = register(
		"flower_default",
		Feature.FLOWER.configure(ConfiguredFeatures.Configs.DEFAULT_FLOWER_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(32)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_FOREST = register(
		"flower_forest",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					96,
					6,
					2,
					() -> Feature.SIMPLE_BLOCK
							.configure(
								new SimpleBlockFeatureConfig(
									new NoiseBlockStateProvider(
										2345L,
										new DoublePerlinNoiseSampler.NoiseParameters(0, 1.0),
										0.020833334F,
										List.of(
											Blocks.DANDELION.getDefaultState(),
											Blocks.POPPY.getDefaultState(),
											Blocks.ALLIUM.getDefaultState(),
											Blocks.AZURE_BLUET.getDefaultState(),
											Blocks.RED_TULIP.getDefaultState(),
											Blocks.ORANGE_TULIP.getDefaultState(),
											Blocks.WHITE_TULIP.getDefaultState(),
											Blocks.PINK_TULIP.getDefaultState(),
											Blocks.OXEYE_DAISY.getDefaultState(),
											Blocks.CORNFLOWER.getDefaultState(),
											Blocks.LILY_OF_THE_VALLEY.getDefaultState()
										)
									)
								)
							)
							.onlyInAir()
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(2)
			.repeat(3)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_SWAMP = register(
		"flower_swamp",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					64, 6, 2, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.BLUE_ORCHID))).onlyInAir()
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(32)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_PLAIN = register(
		"flower_plain",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					64,
					6,
					2,
					() -> Feature.SIMPLE_BLOCK
							.configure(
								new SimpleBlockFeatureConfig(
									new NoiseThresholdBlockStateProvider(
										2345L,
										new DoublePerlinNoiseSampler.NoiseParameters(0, 1.0),
										0.005F,
										-0.8F,
										0.33333334F,
										Blocks.DANDELION.getDefaultState(),
										ImmutableList.of(
											Blocks.ORANGE_TULIP.getDefaultState(), Blocks.RED_TULIP.getDefaultState(), Blocks.PINK_TULIP.getDefaultState(), Blocks.WHITE_TULIP.getDefaultState()
										),
										ImmutableList.of(
											Blocks.POPPY.getDefaultState(), Blocks.AZURE_BLUET.getDefaultState(), Blocks.OXEYE_DAISY.getDefaultState(), Blocks.CORNFLOWER.getDefaultState()
										)
									)
								)
							)
							.onlyInAir()
				)
			)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_PLAIN_DECORATED = register(
		"flower_plain_decorated",
		FLOWER_PLAIN.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(32)
			.decorate(Decorator.COUNT_NOISE.configure(new CountNoiseDecoratorConfig(-0.8, 15, 4)))
	);
	public static final ConfiguredFeature<?, ?> FLOWER_MEADOW = register(
		"flower_meadow",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig(
					96,
					6,
					2,
					() -> Feature.SIMPLE_BLOCK
							.configure(
								new SimpleBlockFeatureConfig(
									new DualNoiseBlockStateProvider(
										new Range(1, 3),
										new DoublePerlinNoiseSampler.NoiseParameters(-10, 1.0),
										1.0F,
										2345L,
										new DoublePerlinNoiseSampler.NoiseParameters(-3, 1.0),
										1.0F,
										ImmutableList.of(
											Blocks.TALL_GRASS.getDefaultState(),
											Blocks.ALLIUM.getDefaultState(),
											Blocks.POPPY.getDefaultState(),
											Blocks.AZURE_BLUET.getDefaultState(),
											Blocks.DANDELION.getDefaultState(),
											Blocks.CORNFLOWER.getDefaultState(),
											Blocks.OXEYE_DAISY.getDefaultState(),
											Blocks.GRASS.getDefaultState()
										)
									)
								)
							)
							.onlyInAir()
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_35099 = Feature.SIMPLE_BLOCK
		.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.GRASS.getDefaultState())))
		.onlyInAir();
	private static final ImmutableList<Supplier<ConfiguredFeature<?, ?>>> FOREST_FLOWER_VEGETATION_CONFIGS = ImmutableList.of(
		() -> Feature.RANDOM_PATCH
				.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILAC))))),
		() -> Feature.RANDOM_PATCH
				.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.ROSE_BUSH))))),
		() -> Feature.RANDOM_PATCH
				.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.PEONY))))),
		() -> Feature.NO_BONEMEAL_FLOWER
				.configure(createRandomPatchFeature(Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.LILY_OF_THE_VALLEY)))))
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION_COMMON = register(
		"forest_flower_vegetation_common",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(new SimpleRandomFeatureConfig(FOREST_FLOWER_VEGETATION_CONFIGS))
			.repeat(ClampedIntProvider.create(UniformIntProvider.create(-1, 3), 0, 3))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(7)
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION = register(
		"forest_flower_vegetation",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(new SimpleRandomFeatureConfig(FOREST_FLOWER_VEGETATION_CONFIGS))
			.repeat(ClampedIntProvider.create(UniformIntProvider.create(-3, 1), 0, 1))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.applyChance(7)
	);
	public static final ConfiguredFeature<?, ?> DARK_FOREST_VEGETATION = register(
		"dark_forest_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(
						HUGE_BROWN_MUSHROOM.withChance(0.025F),
						HUGE_RED_MUSHROOM.withChance(0.05F),
						DARK_OAK_CHECKED.withChance(0.6666667F),
						BIRCH_CHECKED.withChance(0.2F),
						FANCY_OAK_CHECKED.withChance(0.1F)
					),
					OAK_CHECKED
				)
			)
			.decorate(ConfiguredFeatures.Decorators.DARK_OAK_TREE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> WARM_OCEAN_VEGETATION = register(
		"warm_ocean_vegetation",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(
				new SimpleRandomFeatureConfig(
					ImmutableList.of(
						() -> Feature.CORAL_TREE.configure(FeatureConfig.DEFAULT),
						() -> Feature.CORAL_CLAW.configure(FeatureConfig.DEFAULT),
						() -> Feature.CORAL_MUSHROOM.configure(FeatureConfig.DEFAULT)
					)
				)
			)
			.decorate(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE_BIASED.configure(new CountNoiseBiasedDecoratorConfig(20, 400.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_TREES = register(
		"forest_flower_trees",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(BIRCH_BEES_002.withChance(0.2F), FANCY_OAK_BEES_002.withChance(0.1F)), OAK_BEES_002))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(6, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> MEADOW_TREES = register(
		"meadow_trees",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK_BEES.withChance(0.5F)), SUPER_BIRCH_BEES))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.applyChance(100)
	);
	public static final ConfiguredFeature<?, ?> TAIGA_VEGETATION = register(
		"taiga_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(PINE_CHECKED.withChance(0.33333334F)), SPRUCE_CHECKED))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> GROVE_VEGETATION = register(
		"grove_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(PINE_ON_SNOW.withChance(0.33333334F)), SPRUCE_ON_SNOW))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_BADLANDS = register(
		"trees_badlands",
		OAK_CHECKED.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(5, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SNOWY = register(
		"trees_snowy",
		SPRUCE_CHECKED.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SWAMP = register(
		"trees_swamp",
		SWAMP_OAK.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_OCEAN_FLOOR)
			.decorate(Decorator.WATER_DEPTH_THRESHOLD.configure(new WaterDepthThresholdDecoratorConfig(2)))
			.spreadHorizontally()
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SHATTERED_SAVANNA = register(
		"trees_shattered_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA_CHECKED.withChance(0.8F)), OAK_CHECKED))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SAVANNA = register(
		"trees_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA_CHECKED.withChance(0.8F)), OAK_CHECKED))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(1, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BIRCH_TALL = register(
		"birch_tall",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SUPER_BIRCH_BEES_0002.withChance(0.5F)), BIRCH_BEES_0002))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_BIRCH = register(
		"trees_birch",
		BIRCH_BEES_0002.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN_EDGE = register(
		"trees_mountain_edge",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE_CHECKED.withChance(0.666F), FANCY_OAK_CHECKED.withChance(0.1F)), OAK_CHECKED))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(3, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN = register(
		"trees_mountain",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE_CHECKED.withChance(0.666F), FANCY_OAK_CHECKED.withChance(0.1F)), OAK_CHECKED))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_WATER = register(
		"trees_water",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK_CHECKED.withChance(0.1F)), OAK_CHECKED))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BIRCH_OTHER = register(
		"birch_other",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(BIRCH_BEES_0002.withChance(0.2F), FANCY_OAK_BEES_0002.withChance(0.1F)), OAK_BEES_0002))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> PLAIN_VEGETATION = register(
		"plain_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(List.of(FANCY_OAK_BEES_005.withChance(0.33333334F)), OAK_BEES_005))
			.wouldSurvive(Blocks.OAK_SAPLING)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.05F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE_EDGE = register(
		"trees_jungle_edge",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK_CHECKED.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F)), JUNGLE_TREE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT_SPRUCE = register(
		"trees_giant_spruce",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE_CHECKED.withChance(0.33333334F), PINE_CHECKED.withChance(0.33333334F)), SPRUCE_CHECKED))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT = register(
		"trees_giant",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(MEGA_SPRUCE_CHECKED.withChance(0.025641026F), MEGA_PINE_CHECKED.withChance(0.30769232F), PINE_CHECKED.withChance(0.33333334F)),
					SPRUCE_CHECKED
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE = register(
		"trees_jungle",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(FANCY_OAK_CHECKED.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F), MEGA_JUNGLE_TREE_CHECKED.withChance(0.33333334F)), JUNGLE_TREE
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(50, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BAMBOO_VEGETATION = register(
		"bamboo_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(FANCY_OAK_CHECKED.withChance(0.05F), JUNGLE_BUSH.withChance(0.15F), MEGA_JUNGLE_TREE_CHECKED.withChance(0.7F)),
					Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.LUSH_GRASS_CONFIG)
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(30, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> MUSHROOM_FIELD_VEGETATION = register(
		"mushroom_field_vegetation",
		Feature.RANDOM_BOOLEAN_SELECTOR
			.configure(new RandomBooleanFeatureConfig(() -> HUGE_RED_MUSHROOM, () -> HUGE_BROWN_MUSHROOM))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> ROOTED_AZALEA_TREES = register(
		"rooted_azalea_trees",
		Feature.ROOT_SYSTEM
			.configure(
				new RootSystemFeatureConfig(
					() -> AZALEA_TREE,
					3,
					3,
					BlockTags.LUSH_GROUND_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.ROOTED_DIRT),
					20,
					100,
					3,
					2,
					BlockStateProvider.of(Blocks.HANGING_ROOTS),
					20,
					2
				)
			)
			.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12, false)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
	);
	private static final WeightedBlockStateProvider RANDOM_BERRIES_CAVE_VINES_BODY_PROVIDER = new WeightedBlockStateProvider(
		pool().add(Blocks.CAVE_VINES_PLANT.getDefaultState(), 4).add(Blocks.CAVE_VINES_PLANT.getDefaultState().with(CaveVines.BERRIES, Boolean.valueOf(true)), 1)
	);
	private static final RandomizedIntBlockStateProvider RANDOM_AGE_CAVE_VINES_HEAD_PROVIDER = new RandomizedIntBlockStateProvider(
		new WeightedBlockStateProvider(
			pool().add(Blocks.CAVE_VINES.getDefaultState(), 4).add(Blocks.CAVE_VINES.getDefaultState().with(CaveVines.BERRIES, Boolean.valueOf(true)), 1)
		),
		CaveVinesHeadBlock.AGE,
		UniformIntProvider.create(23, 25)
	);
	public static final ConfiguredFeature<BlockColumnFeatureConfig, ?> CAVE_VINE = register(
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
							RANDOM_BERRIES_CAVE_VINES_BODY_PROVIDER
						),
						BlockColumnFeatureConfig.createLayer(ConstantIntProvider.create(1), RANDOM_AGE_CAVE_VINES_HEAD_PROVIDER)
					),
					Direction.DOWN,
					ONLY_IN_AIR,
					true
				)
			)
	);
	public static final ConfiguredFeature<BlockColumnFeatureConfig, ?> CAVE_VINE_IN_MOSS = register(
		"cave_vine_in_moss",
		Feature.BLOCK_COLUMN
			.configure(
				new BlockColumnFeatureConfig(
					List.of(
						BlockColumnFeatureConfig.createLayer(
							new WeightedListIntProvider(DataPool.<IntProvider>builder().add(UniformIntProvider.create(0, 3), 5).add(UniformIntProvider.create(1, 7), 1).build()),
							RANDOM_BERRIES_CAVE_VINES_BODY_PROVIDER
						),
						BlockColumnFeatureConfig.createLayer(ConstantIntProvider.create(1), RANDOM_AGE_CAVE_VINES_HEAD_PROVIDER)
					),
					Direction.DOWN,
					ONLY_IN_AIR,
					true
				)
			)
	);
	public static final ConfiguredFeature<?, ?> CAVE_VINES = register(
		"cave_vines",
		CAVE_VINE.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12, false)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(157)
	);
	public static final ConfiguredFeature<SimpleBlockFeatureConfig, ?> MOSS_VEGETATION = register(
		"moss_vegetation",
		Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(
						pool()
							.add(Blocks.FLOWERING_AZALEA.getDefaultState(), 4)
							.add(Blocks.AZALEA.getDefaultState(), 7)
							.add(Blocks.MOSS_CARPET.getDefaultState(), 25)
							.add(Blocks.GRASS.getDefaultState(), 50)
							.add(Blocks.TALL_GRASS.getDefaultState(), 10)
					)
				)
			)
	);
	public static final ConfiguredFeature<VegetationPatchFeatureConfig, ?> MOSS_PATCH = register(
		"moss_patch",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.MOSS_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.MOSS_BLOCK),
					() -> MOSS_VEGETATION,
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
	public static final ConfiguredFeature<VegetationPatchFeatureConfig, ?> MOSS_PATCH_BONEMEAL = register(
		"moss_patch_bonemeal",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.MOSS_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.MOSS_BLOCK),
					() -> MOSS_VEGETATION,
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
	public static final ConfiguredFeature<?, ?> LUSH_CAVES_VEGETATION = register(
		"lush_caves_vegetation",
		MOSS_PATCH.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.FLOOR, 12, false)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(104)
	);
	public static final ConfiguredFeature<SimpleRandomFeatureConfig, ?> DRIPLEAF = register(
		"dripleaf",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(
				new SimpleRandomFeatureConfig(
					ImmutableList.of(
						ConfiguredFeatures::createSmallDripleafFeature,
						() -> createBigDripleafFeature(Direction.EAST),
						() -> createBigDripleafFeature(Direction.WEST),
						() -> createBigDripleafFeature(Direction.SOUTH),
						() -> createBigDripleafFeature(Direction.NORTH)
					)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> CLAY_WITH_DRIPLEAVES = register(
		"clay_with_dripleaves",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.LUSH_GROUND_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.CLAY),
					() -> DRIPLEAF,
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
	public static final ConfiguredFeature<?, ?> CLAY_POOL_WITH_DRIPLEAVES = register(
		"clay_pool_with_dripleaves",
		Feature.WATERLOGGED_VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.LUSH_GROUND_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.CLAY),
					() -> DRIPLEAF,
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
	public static final ConfiguredFeature<?, ?> LUSH_CAVES_CLAY = register(
		"lush_caves_clay",
		Feature.RANDOM_BOOLEAN_SELECTOR
			.configure(new RandomBooleanFeatureConfig(() -> CLAY_WITH_DRIPLEAVES, () -> CLAY_POOL_WITH_DRIPLEAVES))
			.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.FLOOR, 12, false)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(52)
	);
	public static final ConfiguredFeature<VegetationPatchFeatureConfig, ?> MOSS_PATCH_CEILING = register(
		"moss_patch_ceiling",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.MOSS_REPLACEABLE.getId(),
					BlockStateProvider.of(Blocks.MOSS_BLOCK),
					() -> CAVE_VINE_IN_MOSS,
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
	public static final ConfiguredFeature<?, ?> LUSH_CAVES_CEILING_VEGETATION = register(
		"lush_caves_ceiling_vegetation",
		MOSS_PATCH_CEILING.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12, false)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(104)
	);
	public static final ConfiguredFeature<?, ?> SPORE_BLOSSOM = register(
		"spore_blossom",
		Feature.SIMPLE_BLOCK
			.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SPORE_BLOSSOM)))
			.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12, false)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_120)
			.spreadHorizontally()
			.repeat(21)
	);
	public static final ConfiguredFeature<?, ?> CLASSIC_VINES_CAVE_FEATURE = register(
		"classic_vines_cave_feature",
		Feature.VINES.configure(FeatureConfig.DEFAULT).range(ConfiguredFeatures.Decorators.BOTTOM_TO_120).spreadHorizontally().repeat(216)
	);
	public static final ConfiguredFeature<?, ?> AMETHYST_GEODE = register(
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
						ImmutableList.of(
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
			.uniformRange(YOffset.aboveBottom(6), YOffset.fixed(30))
			.spreadHorizontally()
			.applyChance(24)
	);

	static DataPool.Builder<BlockState> pool() {
		return DataPool.builder();
	}

	private static RandomPatchFeatureConfig createRandomPatchFeature(ConfiguredFeature<?, ?> feature, List<Block> generateOnBlocks, int tries) {
		ConfiguredFeature<?, ?> configuredFeature;
		if (!generateOnBlocks.isEmpty()) {
			configuredFeature = feature.applyBlockFilter(BlockPredicate.bothOf(ONLY_IN_AIR, BlockPredicate.matchingBlocks(generateOnBlocks, new BlockPos(0, -1, 0))));
		} else {
			configuredFeature = feature.applyBlockFilter(ONLY_IN_AIR);
		}

		return new RandomPatchFeatureConfig(tries, 7, 3, () -> configuredFeature);
	}

	static RandomPatchFeatureConfig createRandomPatchFeature(ConfiguredFeature<?, ?> feature, List<Block> generateOnBlocks) {
		return createRandomPatchFeature(feature, generateOnBlocks, 96);
	}

	static RandomPatchFeatureConfig createRandomPatchFeature(ConfiguredFeature<?, ?> feature) {
		return createRandomPatchFeature(feature, List.of(), 96);
	}

	private static TreeFeatureConfig.Builder buildTree(
		Block trunkBlock, Block foliageBlock, int baseHeight, int firstRandomHeight, int secondRandomHeight, int foliageRadius
	) {
		return new TreeFeatureConfig.Builder(
			BlockStateProvider.of(trunkBlock),
			new StraightTrunkPlacer(baseHeight, firstRandomHeight, secondRandomHeight),
			BlockStateProvider.of(foliageBlock),
			new BlobFoliagePlacer(ConstantIntProvider.create(foliageRadius), ConstantIntProvider.create(0), 3),
			new TwoLayersFeatureSize(1, 0, 1)
		);
	}

	private static TreeFeatureConfig.Builder buildOakTree() {
		return buildTree(Blocks.OAK_LOG, Blocks.OAK_LEAVES, 4, 2, 0, 2).ignoreVines();
	}

	private static TreeFeatureConfig.Builder buildBirchTree() {
		return buildTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 0, 2).ignoreVines();
	}

	private static TreeFeatureConfig.Builder buildTallBirchTree() {
		return buildTree(Blocks.BIRCH_LOG, Blocks.BIRCH_LEAVES, 5, 2, 6, 2).ignoreVines();
	}

	private static TreeFeatureConfig.Builder buildJungleTree() {
		return buildTree(Blocks.JUNGLE_LOG, Blocks.JUNGLE_LEAVES, 4, 8, 0, 2);
	}

	private static TreeFeatureConfig.Builder buildLargeOakTree() {
		return new TreeFeatureConfig.Builder(
				BlockStateProvider.of(Blocks.OAK_LOG),
				new LargeOakTrunkPlacer(3, 11, 0),
				BlockStateProvider.of(Blocks.OAK_LEAVES),
				new LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(4), 4),
				new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
			)
			.ignoreVines();
	}

	private static ConfiguredFeature<BlockColumnFeatureConfig, ?> createBigDripleafFeature(Direction blockDirection) {
		return Feature.BLOCK_COLUMN
			.configure(
				new BlockColumnFeatureConfig(
					List.of(
						BlockColumnFeatureConfig.createLayer(
							new WeightedListIntProvider(DataPool.<IntProvider>builder().add(UniformIntProvider.create(0, 4), 2).add(ConstantIntProvider.create(0), 1).build()),
							BlockStateProvider.of(Blocks.BIG_DRIPLEAF_STEM.getDefaultState().with(Properties.HORIZONTAL_FACING, blockDirection))
						),
						BlockColumnFeatureConfig.createLayer(
							ConstantIntProvider.create(1), BlockStateProvider.of(Blocks.BIG_DRIPLEAF.getDefaultState().with(Properties.HORIZONTAL_FACING, blockDirection))
						)
					),
					Direction.UP,
					ONLY_IN_AIR_OR_WATER,
					true
				)
			);
	}

	private static ConfiguredFeature<SimpleBlockFeatureConfig, ?> createSmallDripleafFeature() {
		return Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(
						pool()
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.EAST), 1)
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.WEST), 1)
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.NORTH), 1)
							.add(Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.SOUTH), 1)
					)
				)
			);
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
	}

	static RandomPatchFeatureConfig createRandomPatchFeature(BlockStateProvider toPlace, int tries) {
		return new RandomPatchFeatureConfig(tries, 7, 3, () -> Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(toPlace)).onlyInAir());
	}

	public static final class Configs {
		public static final RandomPatchFeatureConfig GRASS_CONFIG = ConfiguredFeatures.createRandomPatchFeature(BlockStateProvider.of(Blocks.GRASS), 32);
		public static final RandomPatchFeatureConfig TAIGA_GRASS_CONFIG = ConfiguredFeatures.createRandomPatchFeature(
			new WeightedBlockStateProvider(ConfiguredFeatures.pool().add(Blocks.GRASS.getDefaultState(), 1).add(Blocks.FERN.getDefaultState(), 4)), 32
		);
		public static final RandomPatchFeatureConfig LUSH_GRASS_CONFIG = new RandomPatchFeatureConfig(
			32,
			7,
			3,
			() -> Feature.SIMPLE_BLOCK
					.configure(
						new SimpleBlockFeatureConfig(
							new WeightedBlockStateProvider(ConfiguredFeatures.pool().add(Blocks.GRASS.getDefaultState(), 3).add(Blocks.FERN.getDefaultState(), 1))
						)
					)
					.applyBlockFilter(
						BlockPredicate.bothOf(ConfiguredFeatures.ONLY_IN_AIR, BlockPredicate.not(BlockPredicate.matchingBlock(Blocks.PODZOL, new BlockPos(0, -1, 0))))
					)
		);
		public static final RandomPatchFeatureConfig DEFAULT_FLOWER_CONFIG = ConfiguredFeatures.createRandomPatchFeature(
			new WeightedBlockStateProvider(ConfiguredFeatures.pool().add(Blocks.POPPY.getDefaultState(), 2).add(Blocks.DANDELION.getDefaultState(), 1)), 64
		);
		public static final RandomPatchFeatureConfig DEAD_BUSH_CONFIG = ConfiguredFeatures.createRandomPatchFeature(BlockStateProvider.of(Blocks.DEAD_BUSH), 4);
		public static final RandomPatchFeatureConfig SWEET_BERRY_BUSH_CONFIG = ConfiguredFeatures.createRandomPatchFeature(
			Feature.SIMPLE_BLOCK
				.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3))))),
			List.of(Blocks.GRASS_BLOCK)
		);
		public static final RandomPatchFeatureConfig TALL_GRASS_CONFIG = ConfiguredFeatures.createRandomPatchFeature(
			Feature.SIMPLE_BLOCK.configure(new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.TALL_GRASS)))
		);
		public static final RandomPatchFeatureConfig SUGAR_CANE_CONFIG = new RandomPatchFeatureConfig(
			20,
			4,
			0,
			() -> Feature.BLOCK_COLUMN
					.configure(BlockColumnFeatureConfig.create(BiasedToBottomIntProvider.create(2, 4), BlockStateProvider.of(Blocks.SUGAR_CANE)))
					.onlyInAir()
					.wouldSurvive(Blocks.SUGAR_CANE)
					.applyBlockFilter(
						BlockPredicate.anyOf(
							BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(1, -1, 0)),
							BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(-1, -1, 0)),
							BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, 1)),
							BlockPredicate.matchingFluids(List.of(Fluids.WATER, Fluids.FLOWING_WATER), new BlockPos(0, -1, -1))
						)
					)
		);
		public static final SpringFeatureConfig LAVA_SPRING_CONFIG = new SpringFeatureConfig(
			Fluids.LAVA.getDefaultState(),
			true,
			4,
			1,
			ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DIRT)
		);
		public static final SpringFeatureConfig field_35560 = new SpringFeatureConfig(
			Fluids.LAVA.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE)
		);
		public static final SpringFeatureConfig ENCLOSED_NETHER_SPRING_CONFIG = new SpringFeatureConfig(
			Fluids.LAVA.getDefaultState(), false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK)
		);
		public static final BlockPileFeatureConfig CRIMSON_ROOTS_CONFIG = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider(
				ConfiguredFeatures.pool()
					.add(Blocks.CRIMSON_ROOTS.getDefaultState(), 87)
					.add(Blocks.CRIMSON_FUNGUS.getDefaultState(), 11)
					.add(Blocks.WARPED_FUNGUS.getDefaultState(), 1)
			)
		);
		public static final BlockPileFeatureConfig WARPED_ROOTS_CONFIG = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider(
				ConfiguredFeatures.pool()
					.add(Blocks.WARPED_ROOTS.getDefaultState(), 85)
					.add(Blocks.CRIMSON_ROOTS.getDefaultState(), 1)
					.add(Blocks.WARPED_FUNGUS.getDefaultState(), 13)
					.add(Blocks.CRIMSON_FUNGUS.getDefaultState(), 1)
			)
		);
		public static final BlockPileFeatureConfig NETHER_SPROUTS_CONFIG = new BlockPileFeatureConfig(BlockStateProvider.of(Blocks.NETHER_SPROUTS));
	}

	static final class Decorators {
		public static final BeehiveTreeDecorator VERY_RARE_BEEHIVES_TREES = new BeehiveTreeDecorator(0.002F);
		public static final BeehiveTreeDecorator REGULAR_BEEHIVES_TREES = new BeehiveTreeDecorator(0.02F);
		public static final BeehiveTreeDecorator MORE_BEEHIVES_TREES = new BeehiveTreeDecorator(0.05F);
		public static final BeehiveTreeDecorator HALF_BEEHIVES_TREES = new BeehiveTreeDecorator(1.0F);
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING));
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> TOP_SOLID_HEIGHTMAP = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR_WG));
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_WORLD_SURFACE = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG));
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_OCEAN_FLOOR = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR));
		public static final RangeDecoratorConfig BOTTOM_TO_TOP = new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.getBottom(), YOffset.getTop()));
		public static final RangeDecoratorConfig BOTTOM_TO_TOP_OFFSET_10 = new RangeDecoratorConfig(
			UniformHeightProvider.create(YOffset.aboveBottom(10), YOffset.belowTop(10))
		);
		public static final RangeDecoratorConfig BOTTOM_TO_TOP_OFFSET_8 = new RangeDecoratorConfig(
			UniformHeightProvider.create(YOffset.aboveBottom(8), YOffset.belowTop(8))
		);
		public static final RangeDecoratorConfig BOTTOM_TO_TOP_OFFSET_4 = new RangeDecoratorConfig(
			UniformHeightProvider.create(YOffset.aboveBottom(4), YOffset.belowTop(4))
		);
		public static final RangeDecoratorConfig BOTTOM_TO_120 = new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.getBottom(), YOffset.fixed(256)));
		public static final ConfiguredDecorator<?> FIRE = Decorator.RANGE.configure(BOTTOM_TO_TOP_OFFSET_4).spreadHorizontally().repeatRandomly(5);
		public static final ConfiguredDecorator<?> HEIGHTMAP_OCEAN_FLOOR_NO_WATER = HEIGHTMAP_OCEAN_FLOOR.decorate(
			Decorator.WATER_DEPTH_THRESHOLD.configure(new WaterDepthThresholdDecoratorConfig(0))
		);
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER = HEIGHTMAP_OCEAN_FLOOR_NO_WATER.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP = HEIGHTMAP.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_TOP_SOLID_HEIGHTMAP = TOP_SOLID_HEIGHTMAP.spreadHorizontally();
		public static final ConfiguredDecorator<?> DARK_OAK_TREE_HEIGHTMAP = HEIGHTMAP_OCEAN_FLOOR_NO_WATER.decorate(
			Decorator.DARK_OAK_TREE.configure(DecoratorConfig.DEFAULT)
		);

		private Decorators() {
		}
	}
}
