package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.block.BigDripleafBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.CaveVines;
import net.minecraft.block.CaveVinesHeadBlock;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SmallDripleafBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.property.Properties;
import net.minecraft.structure.processor.StructureProcessorLists;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.VerticalSurfaceType;
import net.minecraft.util.math.floatprovider.ClampedNormalFloatProvider;
import net.minecraft.util.math.floatprovider.UniformFloatProvider;
import net.minecraft.util.math.intprovider.BiasedToBottomIntProvider;
import net.minecraft.util.math.intprovider.ClampedIntProvider;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.CaveSurfaceDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;
import net.minecraft.world.gen.decorator.CountNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.CountNoiseDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.HeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
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
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.ForestFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PlainsFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.RandomizedIntBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
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
		END_ISLAND.method_36296(YOffset.fixed(55), YOffset.fixed(70))
			.spreadHorizontally()
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(1, 0.25F, 1)))
			.applyChance(14)
	);
	public static final ConfiguredFeature<?, ?> DELTA = register(
		"delta",
		Feature.DELTA_FEATURE
			.configure(
				new DeltaFeatureConfig(
					ConfiguredFeatures.States.LAVA_BLOCK, ConfiguredFeatures.States.MAGMA_BLOCK, UniformIntProvider.create(3, 7), UniformIntProvider.create(0, 2)
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
			.configure(new ReplaceBlobsFeatureConfig(ConfiguredFeatures.States.NETHERRACK, ConfiguredFeatures.States.BASALT, UniformIntProvider.create(3, 7)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP)
			.spreadHorizontally()
			.repeat(75)
	);
	public static final ConfiguredFeature<?, ?> BLACKSTONE_BLOBS = register(
		"blackstone_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS
			.configure(new ReplaceBlobsFeatureConfig(ConfiguredFeatures.States.NETHERRACK, ConfiguredFeatures.States.BLACKSTONE, UniformIntProvider.create(3, 7)))
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
					ConfiguredFeatures.States.PACKED_ICE,
					UniformIntProvider.create(2, 3),
					1,
					ImmutableList.of(
						ConfiguredFeatures.States.DIRT,
						ConfiguredFeatures.States.GRASS_BLOCK,
						ConfiguredFeatures.States.PODZOL,
						ConfiguredFeatures.States.COARSE_DIRT,
						ConfiguredFeatures.States.MYCELIUM,
						ConfiguredFeatures.States.SNOW_BLOCK,
						ConfiguredFeatures.States.ICE
					)
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> FOREST_ROCK = register(
		"forest_rock",
		Feature.FOREST_ROCK
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.MOSSY_COBBLESTONE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeatRandomly(2)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_SIMPLE = register(
		"seagrass_simple",
		Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					new SimpleBlockStateProvider(ConfiguredFeatures.States.SEAGRASS),
					ImmutableList.of(ConfiguredFeatures.States.STONE),
					ImmutableList.of(ConfiguredFeatures.States.WATER_BLOCK),
					ImmutableList.of(ConfiguredFeatures.States.WATER_BLOCK)
				)
			)
			.applyChance(10)
			.decorate(Decorator.CARVING_MASK.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID)))
	);
	public static final ConfiguredFeature<?, ?> ICEBERG_PACKED = register(
		"iceberg_packed",
		Feature.ICEBERG
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.PACKED_ICE))
			.decorate(Decorator.ICEBERG.configure(NopeDecoratorConfig.INSTANCE))
			.applyChance(16)
	);
	public static final ConfiguredFeature<?, ?> ICEBERG_BLUE = register(
		"iceberg_blue",
		Feature.ICEBERG
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.BLUE_ICE))
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
		"blue_ice", Feature.BLUE_ICE.configure(FeatureConfig.DEFAULT).method_36296(YOffset.fixed(30), YOffset.fixed(61)).spreadHorizontally().repeatRandomly(19)
	);
	public static final ConfiguredFeature<?, ?> BAMBOO_LIGHT = register(
		"bamboo_light", Feature.BAMBOO.configure(new ProbabilityConfig(0.0F)).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(16)
	);
	public static final ConfiguredFeature<?, ?> BAMBOO = register(
		"bamboo",
		Feature.BAMBOO
			.configure(new ProbabilityConfig(0.2F))
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE_BIASED.configure(new CountNoiseBiasedDecoratorConfig(160, 80.0, 0.3)))
	);
	public static final ConfiguredFeature<?, ?> VINES = register("vines", Feature.VINES.configure(FeatureConfig.DEFAULT).spreadHorizontally().repeat(50));
	public static final ConfiguredFeature<?, ?> PROTOTYPE_VINES = register(
		"prototype_vines", Feature.VINES.configure(FeatureConfig.DEFAULT).method_36296(YOffset.fixed(64), YOffset.fixed(100)).spreadHorizontally().repeat(127)
	);
	public static final ConfiguredFeature<?, ?> LAKE_WATER = register(
		"lake_water",
		Feature.LAKE
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.WATER_BLOCK))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP)
			.spreadHorizontally()
			.applyChance(4)
	);
	public static final ConfiguredFeature<?, ?> LAKE_LAVA = register(
		"lake_lava",
		Feature.LAKE
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.LAVA_BLOCK))
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
					ConfiguredFeatures.States.CLAY, UniformIntProvider.create(2, 3), 1, ImmutableList.of(ConfiguredFeatures.States.DIRT, ConfiguredFeatures.States.CLAY)
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> DISK_GRAVEL = register(
		"disk_gravel",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.States.GRAVEL,
					UniformIntProvider.create(2, 5),
					2,
					ImmutableList.of(ConfiguredFeatures.States.DIRT, ConfiguredFeatures.States.GRASS_BLOCK)
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> DISK_SAND = register(
		"disk_sand",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.States.SAND,
					UniformIntProvider.create(2, 6),
					2,
					ImmutableList.of(ConfiguredFeatures.States.DIRT, ConfiguredFeatures.States.GRASS_BLOCK)
				)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
			.repeat(3)
	);
	public static final ConfiguredFeature<?, ?> FREEZE_TOP_LAYER = register("freeze_top_layer", Feature.FREEZE_TOP_LAYER.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> BONUS_CHEST = register("bonus_chest", Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> VOID_START_PLATFORM = register("void_start_platform", Feature.VOID_START_PLATFORM.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> MONSTER_ROOM = register(
		"monster_room", Feature.MONSTER_ROOM.configure(FeatureConfig.DEFAULT).range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).spreadHorizontally().repeat(8)
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
	public static final ConfiguredFeature<?, ?> FOSSIL = register(
		"fossil",
		Feature.FOSSIL
			.configure(new FossilFeatureConfig(FOSSIL_STRUCTURES, FOSSIL_OVERLAY_STRUCTURES, StructureProcessorLists.FOSSIL_ROT, StructureProcessorLists.FOSSIL_COAL, 4))
			.applyChance(64)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_FOSSIL_UPPER = register(
		"prototype_fossil_upper",
		Feature.FOSSIL
			.configure(new FossilFeatureConfig(FOSSIL_STRUCTURES, FOSSIL_OVERLAY_STRUCTURES, StructureProcessorLists.FOSSIL_ROT, StructureProcessorLists.FOSSIL_COAL, 4))
			.method_36296(YOffset.fixed(0), YOffset.getTop())
			.applyChance(64)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_FOSSIL_LOWER = register(
		"prototype_fossil_lower",
		Feature.FOSSIL
			.configure(
				new FossilFeatureConfig(FOSSIL_STRUCTURES, FOSSIL_OVERLAY_STRUCTURES, StructureProcessorLists.FOSSIL_ROT, StructureProcessorLists.FOSSIL_DIAMONDS, 4)
			)
			.method_36296(YOffset.getBottom(), YOffset.fixed(-8))
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
	public static final ConfiguredFeature<?, ?> SPRING_DELTA = register(
		"spring_delta",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(
					ConfiguredFeatures.States.LAVA_FLUID,
					true,
					4,
					1,
					ImmutableSet.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE)
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
			.configure(new SpringFeatureConfig(ConfiguredFeatures.States.LAVA_FLUID, false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_4)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> SPRING_WATER = register(
		"spring_water",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(ConfiguredFeatures.States.WATER_FLUID, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE))
			)
			.range(new RangeDecoratorConfig(BiasedToBottomHeightProvider.create(YOffset.getBottom(), YOffset.belowTop(8), 8)))
			.spreadHorizontally()
			.repeat(50)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_SPRING_WATER = register(
		"prototype_spring_water",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(
					ConfiguredFeatures.States.WATER_FLUID,
					true,
					4,
					1,
					ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF)
				)
			)
			.method_36296(YOffset.getBottom(), YOffset.getTop())
			.spreadHorizontally()
			.repeat(50)
	);
	public static final ConfiguredFeature<?, ?> PILE_HAY = register(
		"pile_hay", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new PillarBlockStateProvider(Blocks.HAY_BLOCK)))
	);
	public static final ConfiguredFeature<?, ?> PILE_MELON = register(
		"pile_melon", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.States.MELON)))
	);
	public static final ConfiguredFeature<?, ?> PILE_SNOW = register(
		"pile_snow", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.States.SNOW)))
	);
	public static final ConfiguredFeature<?, ?> PILE_ICE = register(
		"pile_ice",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider(method_35926().add(ConfiguredFeatures.States.BLUE_ICE, 1).add(ConfiguredFeatures.States.PACKED_ICE, 5))
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PILE_PUMPKIN = register(
		"pile_pumpkin",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider(method_35926().add(ConfiguredFeatures.States.PUMPKIN, 19).add(ConfiguredFeatures.States.JACK_O_LANTERN, 1))
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_FIRE = register(
		"patch_fire",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.FIRE), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.NETHERRACK.getBlock()))
					.cannotProject()
					.build()
			)
			.decorate(ConfiguredFeatures.Decorators.FIRE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SOUL_FIRE = register(
		"patch_soul_fire",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.SOUL_FIRE), new SimpleBlockPlacer())
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.SOUL_SOIL.getBlock()))
					.cannotProject()
					.build()
			)
			.decorate(ConfiguredFeatures.Decorators.FIRE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BROWN_MUSHROOM = register(
		"patch_brown_mushroom",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.BROWN_MUSHROOM), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_RED_MUSHROOM = register(
		"patch_red_mushroom",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.RED_MUSHROOM), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CRIMSON_ROOTS = register(
		"patch_crimson_roots",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.CRIMSON_ROOTS), new SimpleBlockPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUNFLOWER = register(
		"patch_sunflower",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.SUNFLOWER), new DoublePlantPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> PATCH_PUMPKIN = register(
		"patch_pumpkin",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.PUMPKIN), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.GRASS_BLOCK.getBlock()))
					.cannotProject()
					.build()
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.applyChance(32)
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
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.decorate(Decorator.COUNT_NOISE.configure(new CountNoiseDecoratorConfig(-0.8, 5, 10)))
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_FOREST = register(
		"patch_grass_forest",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(2)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_BADLANDS = register(
		"patch_grass_badlands",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_SAVANNA = register(
		"patch_grass_savanna",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_NORMAL = register(
		"patch_grass_normal",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA_2 = register(
		"patch_grass_taiga_2",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA = register(
		"patch_grass_taiga",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_JUNGLE = register(
		"patch_grass_jungle",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.LUSH_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(25)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_2 = register(
		"patch_dead_bush_2",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(2)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH = register(
		"patch_dead_bush",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_BADLANDS = register(
		"patch_dead_bush_badlands",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_MELON = register(
		"patch_melon",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.MELON), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.GRASS_BLOCK.getBlock()))
					.canReplace()
					.cannotProject()
					.build()
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_SPARSE = register(
		"patch_berry_sparse", PATCH_BERRY_BUSH.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_DECORATED = register(
		"patch_berry_decorated", PATCH_BERRY_BUSH.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(12)
	);
	public static final ConfiguredFeature<?, ?> PATCH_WATERLILLY = register(
		"patch_waterlilly",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LILY_PAD), SimpleBlockPlacer.INSTANCE).tries(10).build()
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS_2 = register(
		"patch_tall_grass_2",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.TALL_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE.configure(new CountNoiseDecoratorConfig(-0.8, 0, 7)))
	);
	public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS = register(
		"patch_tall_grass",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.TALL_GRASS_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> PATCH_LARGE_FERN = register(
		"patch_large_fern",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LARGE_FERN), new DoublePlantPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS = register(
		"patch_cactus",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.CACTUS), new ColumnPlacer(BiasedToBottomIntProvider.create(1, 3))
					)
					.tries(10)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DESERT = register(
		"patch_cactus_desert", PATCH_CACTUS.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(10)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DECORATED = register(
		"patch_cactus_decorated", PATCH_CACTUS.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_SWAMP = register(
		"patch_sugar_cane_swamp",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_DESERT = register(
		"patch_sugar_cane_desert",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(60)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_BADLANDS = register(
		"patch_sugar_cane_badlands",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(13)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE = register(
		"patch_sugar_cane",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NETHER = register(
		"brown_mushroom_nether", PATCH_BROWN_MUSHROOM.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).applyChance(2)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NETHER = register(
		"red_mushroom_nether", PATCH_RED_MUSHROOM.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP).applyChance(2)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NORMAL = register(
		"brown_mushroom_normal", PATCH_BROWN_MUSHROOM.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(4)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NORMAL = register(
		"red_mushroom_normal", PATCH_RED_MUSHROOM.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(8)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_TAIGA = register(
		"brown_mushroom_taiga", PATCH_BROWN_MUSHROOM.applyChance(4).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_TAIGA = register(
		"red_mushroom_taiga", PATCH_RED_MUSHROOM.applyChance(8).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_GIANT = register("brown_mushroom_giant", BROWN_MUSHROOM_TAIGA.repeat(3));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_GIANT = register("red_mushroom_giant", RED_MUSHROOM_TAIGA.repeat(3));
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_SWAMP = register("brown_mushroom_swamp", BROWN_MUSHROOM_TAIGA.repeat(8));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_SWAMP = register("red_mushroom_swamp", RED_MUSHROOM_TAIGA.repeat(8));
	public static final ImmutableList<OreFeatureConfig.Target> IRON_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.IRON_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.DEEPSLATE_IRON_ORE)
	);
	public static final ImmutableList<OreFeatureConfig.Target> REDSTONE_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.REDSTONE_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.DEEPSLATE_REDSTONE_ORE)
	);
	public static final ImmutableList<OreFeatureConfig.Target> GOLD_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.GOLD_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.DEEPSLATE_GOLD_ORE)
	);
	public static final ImmutableList<OreFeatureConfig.Target> DIAMOND_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.DIAMOND_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.DEEPSLATE_DIAMOND_ORE)
	);
	public static final ImmutableList<OreFeatureConfig.Target> LAPIS_ORE_TARGETS = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.LAPIS_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.DEEPSLATE_LAPIS_ORE)
	);
	public static final ImmutableList<OreFeatureConfig.Target> field_33634 = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.EMERALD_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.field_33639)
	);
	public static final ImmutableList<OreFeatureConfig.Target> field_33635 = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.COPPER_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.field_33638)
	);
	public static final ImmutableList<OreFeatureConfig.Target> field_33636 = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.COAL_ORE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.field_33637)
	);
	public static final ImmutableList<OreFeatureConfig.Target> INFESTED_TARGETS = ImmutableList.of(
		OreFeatureConfig.create(OreFeatureConfig.Rules.STONE_ORE_REPLACEABLES, ConfiguredFeatures.States.INFESTED_STONE),
		OreFeatureConfig.create(OreFeatureConfig.Rules.DEEPSLATE_ORE_REPLACEABLES, ConfiguredFeatures.States.INFESTED_DEEPSLATE)
	);
	public static final OreFeatureConfig IRON_CONFIG = new OreFeatureConfig(IRON_ORE_TARGETS, 9);
	public static final OreFeatureConfig REDSTONE_CONFIG = new OreFeatureConfig(REDSTONE_ORE_TARGETS, 8);
	public static final ConfiguredFeature<?, ?> ORE_MAGMA = register(
		"ore_magma",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.MAGMA_BLOCK, 33))
			.method_36296(YOffset.fixed(27), YOffset.fixed(36))
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_SOUL_SAND = register(
		"ore_soul_sand",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.SOUL_SAND, 12))
			.method_36296(YOffset.getBottom(), YOffset.fixed(31))
			.spreadHorizontally()
			.repeat(12)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_DELTAS = register(
		"ore_gold_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_GOLD_ORE, 10))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_DELTAS = register(
		"ore_quartz_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_QUARTZ_ORE, 14))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(32)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_NETHER = register(
		"ore_gold_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_GOLD_ORE, 10))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_NETHER = register(
		"ore_quartz_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_QUARTZ_ORE, 14))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_10)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL_NETHER = register(
		"ore_gravel_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.GRAVEL, 33))
			.method_36296(YOffset.fixed(5), YOffset.fixed(41))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_BLACKSTONE = register(
		"ore_blackstone",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.BLACKSTONE, 33))
			.method_36296(YOffset.fixed(5), YOffset.fixed(31))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIRT = register(
		"ore_dirt",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIRT, 33))
			.method_36296(YOffset.fixed(0), YOffset.getTop())
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_DIRT = register(
		"prototype_ore_dirt",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIRT, 33))
			.method_36296(YOffset.fixed(0), YOffset.getTop())
			.spreadHorizontally()
			.repeat(15)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL = register(
		"ore_gravel",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRAVEL, 33))
			.method_36296(YOffset.fixed(0), YOffset.getTop())
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_GRAVEL = register(
		"prototype_ore_gravel",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRAVEL, 33))
			.method_36296(YOffset.fixed(0), YOffset.getTop())
			.spreadHorizontally()
			.repeat(12)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRANITE = register(
		"ore_granite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRANITE, 33))
			.method_36296(YOffset.fixed(0), YOffset.fixed(79))
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_GRANITE = register(
		"prototype_ore_granite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRANITE, 64))
			.method_36296(YOffset.fixed(0), YOffset.fixed(79))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIORITE = register(
		"ore_diorite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIORITE, 33))
			.method_36296(YOffset.fixed(0), YOffset.fixed(79))
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_DIORITE = register(
		"prototype_ore_diorite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIORITE, 64))
			.method_36296(YOffset.fixed(0), YOffset.fixed(79))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_ANDESITE = register(
		"ore_andesite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.ANDESITE, 33))
			.method_36296(YOffset.fixed(0), YOffset.fixed(79))
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_ANDESITE = register(
		"prototype_ore_andesite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.ANDESITE, 64))
			.method_36296(YOffset.fixed(0), YOffset.fixed(79))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_TUFF = register(
		"ore_tuff",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.TUFF, 33))
			.method_36296(YOffset.fixed(0), YOffset.fixed(16))
			.spreadHorizontally()
			.repeat(1)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_TUFF = register(
		"prototype_ore_tuff",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.TUFF, 64))
			.method_36296(YOffset.getBottom(), YOffset.fixed(0))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_DEEPSLATE = register(
		"ore_deepslate",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.field_33144, 64))
			.method_36296(YOffset.fixed(0), YOffset.fixed(16))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_COAL = register(
		"ore_coal",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.COAL_ORE, 17))
			.method_36296(YOffset.getBottom(), YOffset.fixed(127))
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_COAL_UPPER = register(
		"prototype_ore_coal_upper",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.COAL_ORE, 17))
			.method_36296(YOffset.fixed(136), YOffset.getTop())
			.spreadHorizontally()
			.repeat(30)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_COAL_LOWER = register(
		"prototype_ore_coal_lower",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.COAL_ORE, 17, 0.5F))
			.method_36297(YOffset.fixed(0), YOffset.fixed(192))
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_IRON = register(
		"ore_iron",
		Feature.ORE.configure(new OreFeatureConfig(IRON_ORE_TARGETS, 9)).method_36296(YOffset.getBottom(), YOffset.fixed(63)).spreadHorizontally().repeat(20)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_IRON_UPPER = register(
		"prototype_ore_iron_upper", Feature.ORE.configure(IRON_CONFIG).method_36297(YOffset.fixed(128), YOffset.fixed(384)).spreadHorizontally().repeat(40)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_IRON_MIDDLE = register(
		"prototype_ore_iron_middle", Feature.ORE.configure(IRON_CONFIG).method_36297(YOffset.fixed(-24), YOffset.fixed(56)).spreadHorizontally().repeat(6)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_IRON_LOWER = register(
		"prototype_ore_iron_lower",
		Feature.ORE.configure(new OreFeatureConfig(IRON_ORE_TARGETS, 4)).method_36296(YOffset.getBottom(), YOffset.fixed(-32)).spreadHorizontally().repeat(3)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_EXTRA = register(
		"ore_gold_extra",
		Feature.ORE.configure(new OreFeatureConfig(GOLD_ORE_TARGETS, 9)).method_36296(YOffset.fixed(32), YOffset.fixed(79)).spreadHorizontally().repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD = register(
		"ore_gold",
		Feature.ORE.configure(new OreFeatureConfig(GOLD_ORE_TARGETS, 9)).method_36296(YOffset.getBottom(), YOffset.fixed(31)).spreadHorizontally().repeat(2)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_GOLD = register(
		"prototype_ore_gold",
		Feature.ORE.configure(new OreFeatureConfig(GOLD_ORE_TARGETS, 9, 0.5F)).method_36297(YOffset.fixed(-64), YOffset.fixed(32)).spreadHorizontally().repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_REDSTONE = register(
		"ore_redstone", Feature.ORE.configure(REDSTONE_CONFIG).method_36296(YOffset.getBottom(), YOffset.fixed(15)).spreadHorizontally().repeat(8)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_REDSTONE = register(
		"prototype_ore_redstone", Feature.ORE.configure(REDSTONE_CONFIG).method_36296(YOffset.getBottom(), YOffset.fixed(15)).spreadHorizontally().repeat(4)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_REDSTONE_LOWER = register(
		"prototype_ore_redstone_lower",
		Feature.ORE.configure(REDSTONE_CONFIG).method_36297(YOffset.aboveBottom(-32), YOffset.aboveBottom(32)).spreadHorizontally().repeat(8)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND = register(
		"ore_diamond", Feature.ORE.configure(new OreFeatureConfig(DIAMOND_ORE_TARGETS, 8)).method_36296(YOffset.getBottom(), YOffset.fixed(16)).spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_DIAMOND = register(
		"prototype_ore_diamond",
		Feature.ORE
			.configure(new OreFeatureConfig(DIAMOND_ORE_TARGETS, 4, 0.5F))
			.method_36297(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))
			.spreadHorizontally()
			.repeat(6)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_DIAMOND_LARGE = register(
		"prototype_ore_diamond_large",
		Feature.ORE
			.configure(new OreFeatureConfig(DIAMOND_ORE_TARGETS, 12, 0.7F))
			.method_36297(YOffset.aboveBottom(-80), YOffset.aboveBottom(80))
			.spreadHorizontally()
			.applyChance(9)
	);
	public static final ConfiguredFeature<?, ?> ORE_LAPIS = register(
		"ore_lapis", Feature.ORE.configure(new OreFeatureConfig(LAPIS_ORE_TARGETS, 7)).method_36297(YOffset.fixed(0), YOffset.fixed(30)).spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_LAPIS = register(
		"prototype_ore_lapis",
		Feature.ORE.configure(new OreFeatureConfig(LAPIS_ORE_TARGETS, 7)).method_36297(YOffset.fixed(-32), YOffset.fixed(32)).spreadHorizontally().repeat(2)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_LAPIS_BURIED = register(
		"prototype_ore_lapis_buried",
		Feature.SCATTERED_ORE
			.configure(new OreFeatureConfig(LAPIS_ORE_TARGETS, 7, 1.0F))
			.method_36296(YOffset.getBottom(), YOffset.fixed(64))
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_INFESTED = register(
		"ore_infested",
		Feature.ORE.configure(new OreFeatureConfig(INFESTED_TARGETS, 9)).method_36296(YOffset.getBottom(), YOffset.fixed(63)).spreadHorizontally().repeat(7)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_INFESTED = register(
		"prototype_ore_infested",
		Feature.ORE.configure(new OreFeatureConfig(INFESTED_TARGETS, 9)).method_36296(YOffset.getBottom(), YOffset.fixed(63)).spreadHorizontally().repeat(14)
	);
	public static final ConfiguredFeature<?, ?> ORE_EMERALD = register(
		"ore_emerald",
		Feature.REPLACE_SINGLE_BLOCK
			.configure(new EmeraldOreFeatureConfig(field_33634))
			.method_36296(YOffset.fixed(4), YOffset.fixed(31))
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(6, 24))
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_EMERALD = register(
		"prototype_ore_emerald",
		Feature.ORE.configure(new OreFeatureConfig(field_33634, 3)).method_36297(YOffset.fixed(32), YOffset.fixed(480)).spreadHorizontally().repeat(50)
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_LARGE = register(
		"ore_debris_large",
		Feature.SCATTERED_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, ConfiguredFeatures.States.ANCIENT_DEBRIS, 3, 1.0F))
			.method_36297(YOffset.fixed(8), YOffset.fixed(24))
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_SMALL = register(
		"ore_debris_small",
		Feature.SCATTERED_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, ConfiguredFeatures.States.ANCIENT_DEBRIS, 2, 1.0F))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_TOP_OFFSET_8)
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> ORE_COPPER = register(
		"ore_copper",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.COPPER_ORE, 10))
			.method_36297(YOffset.fixed(0), YOffset.fixed(96))
			.spreadHorizontally()
			.repeat(6)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_ORE_COPPER = register(
		"prototype_ore_copper",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.COPPER_ORE, 10))
			.method_36296(YOffset.fixed(0), YOffset.fixed(63))
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_CLAY = register(
		"ore_clay",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.CLAY, 33))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_60)
			.spreadHorizontally()
			.repeat(15)
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
			.method_36296(YOffset.getBottom(), YOffset.fixed(59))
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(10, 20))
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
			.method_36296(YOffset.getBottom(), YOffset.fixed(59))
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(2, 10))
	);
	public static final ConfiguredFeature<?, ?> SMALL_DRIPSTONE = register(
		"small_dripstone",
		Feature.SMALL_DRIPSTONE
			.configure(new SmallDripstoneFeatureConfig(5, 10, 2, 0.2F))
			.method_36296(YOffset.getBottom(), YOffset.fixed(59))
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(40, 120))
	);
	public static final ConfiguredFeature<?, ?> RARE_DRIPSTONE_CLUSTER = register(
		"rare_dripstone_cluster",
		Feature.DRIPSTONE_CLUSTER
			.configure(
				new DripstoneClusterFeatureConfig(
					12,
					UniformIntProvider.create(3, 3),
					UniformIntProvider.create(2, 6),
					1,
					3,
					UniformIntProvider.create(2, 2),
					UniformFloatProvider.create(0.3F, 0.4F),
					ClampedNormalFloatProvider.create(0.1F, 0.3F, 0.1F, 0.9F),
					0.1F,
					3,
					8
				)
			)
			.method_36296(YOffset.getBottom(), YOffset.fixed(59))
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(10, 10))
			.applyChance(25)
	);
	public static final ConfiguredFeature<?, ?> RARE_SMALL_DRIPSTONE = register(
		"rare_small_dripstone",
		Feature.SMALL_DRIPSTONE
			.configure(new SmallDripstoneFeatureConfig(5, 10, 2, 0.2F))
			.method_36296(YOffset.getBottom(), YOffset.fixed(59))
			.spreadHorizontally()
			.repeat(UniformIntProvider.create(40, 80))
			.applyChance(30)
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_UNDERWATER_MAGMA = register(
		"prototype_underwater_magma",
		Feature.UNDERWATER_MAGMA
			.configure(new UnderwaterMagmaFeatureConfig(5, 1, 0.5F))
			.spreadHorizontally()
			.method_36296(YOffset.getBottom(), YOffset.fixed(39))
			.repeat(UniformIntProvider.create(4, 10))
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
			.spreadHorizontally()
			.method_36296(YOffset.getBottom(), YOffset.fixed(54))
			.repeat(UniformIntProvider.create(20, 30))
	);
	public static final ConfiguredFeature<?, ?> PROTOTYPE_GLOW_LICHEN = register(
		"prototype_glow_lichen",
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
			.spreadHorizontally()
			.method_36296(YOffset.getBottom(), YOffset.fixed(54))
			.repeat(UniformIntProvider.create(40, 60))
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
					new SimpleBlockStateProvider(ConfiguredFeatures.States.BROWN_MUSHROOM_BLOCK), new SimpleBlockStateProvider(ConfiguredFeatures.States.MUSHROOM_STEM), 3
				)
			)
	);
	public static final ConfiguredFeature<?, ?> HUGE_RED_MUSHROOM = register(
		"huge_red_mushroom",
		Feature.HUGE_RED_MUSHROOM
			.configure(
				new HugeMushroomFeatureConfig(
					new SimpleBlockStateProvider(ConfiguredFeatures.States.RED_MUSHROOM_BLOCK), new SimpleBlockStateProvider(ConfiguredFeatures.States.MUSHROOM_STEM), 2
				)
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK = register(
		"oak",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new StraightTrunkPlacer(4, 2, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> DARK_OAK = register(
		"dark_oak",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.DARK_OAK_LOG),
						new DarkOakTrunkPlacer(6, 2, 1),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.DARK_OAK_LEAVES),
						new DarkOakFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0)),
						new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH = register(
		"birch",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LOG),
						new StraightTrunkPlacer(5, 2, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LEAVES),
						new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> ACACIA = register(
		"acacia",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.ACACIA_LOG),
						new ForkingTrunkPlacer(5, 2, 2),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.ACACIA_LEAVES),
						new AcaciaFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0)),
						new TwoLayersFeatureSize(1, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> SPRUCE = register(
		"spruce",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new StraightTrunkPlacer(5, 2, 1),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new SpruceFoliagePlacer(UniformIntProvider.create(2, 3), UniformIntProvider.create(0, 2), UniformIntProvider.create(1, 2)),
						new TwoLayersFeatureSize(2, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> PINE = register(
		"pine",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new StraightTrunkPlacer(6, 4, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new PineFoliagePlacer(ConstantIntProvider.create(1), ConstantIntProvider.create(1), UniformIntProvider.create(3, 4)),
						new TwoLayersFeatureSize(2, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> JUNGLE_TREE = register(
		"jungle_tree",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new StraightTrunkPlacer(4, 8, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LEAVES),
						new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.decorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), TrunkVineTreeDecorator.INSTANCE, LeavesVineTreeDecorator.INSTANCE))
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK = register(
		"fancy_oak",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new LargeOakTrunkPlacer(3, 11, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new LargeOakFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(4), 4),
						new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> JUNGLE_TREE_NO_VINE = register(
		"jungle_tree_no_vine",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new StraightTrunkPlacer(4, 8, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LEAVES),
						new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_JUNGLE_TREE = register(
		"mega_jungle_tree",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new MegaJungleTrunkPlacer(10, 2, 19),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LEAVES),
						new JungleFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 2),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE, LeavesVineTreeDecorator.INSTANCE))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_SPRUCE = register(
		"mega_spruce",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new GiantTrunkPlacer(13, 2, 14),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new MegaPineFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0), UniformIntProvider.create(13, 17)),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ConfiguredFeatures.States.PODZOL))))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_PINE = register(
		"mega_pine",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new GiantTrunkPlacer(13, 2, 14),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new MegaPineFoliagePlacer(ConstantIntProvider.create(0), ConstantIntProvider.create(0), UniformIntProvider.create(3, 7)),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ConfiguredFeatures.States.PODZOL))))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> SUPER_BIRCH_BEES_0002 = register(
		"super_birch_bees_0002",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LOG),
						new StraightTrunkPlacer(5, 2, 6),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LEAVES),
						new BlobFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.decorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> SWAMP_OAK = register(
		"swamp_oak",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new StraightTrunkPlacer(5, 3, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BlobFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), 3),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.decorators(ImmutableList.of(LeavesVineTreeDecorator.INSTANCE))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> JUNGLE_BUSH = register(
		"jungle_bush",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new StraightTrunkPlacer(1, 0, 0),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BushFoliagePlacer(ConstantIntProvider.create(2), ConstantIntProvider.create(1), 2),
						new TwoLayersFeatureSize(0, 0, 0)
					)
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> AZALEA_TREE = register(
		"azalea_tree",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new BendingTrunkPlacer(4, 2, 0, 3, UniformIntProvider.create(1, 2)),
						new WeightedBlockStateProvider(method_35926().add(Blocks.AZALEA_LEAVES.getDefaultState(), 3).add(Blocks.AZALEA_LEAVES_FLOWERS.getDefaultState(), 1)),
						new RandomSpreadFoliagePlacer(ConstantIntProvider.create(3), ConstantIntProvider.create(0), ConstantIntProvider.create(2), 50),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.dirtProvider(new SimpleBlockStateProvider(Blocks.ROOTED_DIRT.getDefaultState()))
					.forceDirt()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BEES_0002 = register(
		"oak_bees_0002", Feature.TREE.configure(OAK.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BEES_002 = register(
		"oak_bees_002", Feature.TREE.configure(OAK.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BEES_005 = register(
		"oak_bees_005", Feature.TREE.configure(OAK.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_BEES_0002 = register(
		"birch_bees_0002", Feature.TREE.configure(BIRCH.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_BEES_002 = register(
		"birch_bees_002", Feature.TREE.configure(BIRCH.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_BEES_005 = register(
		"birch_bees_005", Feature.TREE.configure(BIRCH.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK_BEES_0002 = register(
		"fancy_oak_bees_0002",
		Feature.TREE.configure(FANCY_OAK.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK_BEES_002 = register(
		"fancy_oak_bees_002", Feature.TREE.configure(FANCY_OAK.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK_BEES_005 = register(
		"fancy_oak_bees_005", Feature.TREE.configure(FANCY_OAK.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<?, ?> FLOWER_WARM = register(
		"flower_warm",
		Feature.FLOWER
			.configure(ConfiguredFeatures.Configs.DEFAULT_FLOWER_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_DEFAULT = register(
		"flower_default",
		Feature.FLOWER
			.configure(ConfiguredFeatures.Configs.DEFAULT_FLOWER_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_FOREST = register(
		"flower_forest",
		Feature.FLOWER
			.configure(new RandomPatchFeatureConfig.Builder(ForestFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE).tries(64).build())
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(100)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_SWAMP = register(
		"flower_swamp",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.BLUE_ORCHID), SimpleBlockPlacer.INSTANCE).tries(64).build()
			)
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_PLAIN = register(
		"flower_plain",
		Feature.FLOWER.configure(new RandomPatchFeatureConfig.Builder(PlainsFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE).tries(64).build())
	);
	public static final ConfiguredFeature<?, ?> FLOWER_PLAIN_DECORATED = register(
		"flower_plain_decorated",
		FLOWER_PLAIN.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP)
			.spreadHorizontally()
			.decorate(Decorator.COUNT_NOISE.configure(new CountNoiseDecoratorConfig(-0.8, 15, 4)))
	);
	private static final ImmutableList<Supplier<ConfiguredFeature<?, ?>>> FOREST_FLOWER_VEGETATION_CONFIGS = ImmutableList.of(
		() -> Feature.RANDOM_PATCH
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LILAC), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.RANDOM_PATCH
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.ROSE_BUSH), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.RANDOM_PATCH
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.PEONY), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.NO_BONEMEAL_FLOWER
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LILY_OF_THE_VALLEY), SimpleBlockPlacer.INSTANCE)
						.tries(64)
						.build()
				)
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION_COMMON = register(
		"forest_flower_vegetation_common",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(new SimpleRandomFeatureConfig(FOREST_FLOWER_VEGETATION_CONFIGS))
			.repeat(ClampedIntProvider.create(UniformIntProvider.create(-1, 3), 0, 3))
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(5)
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION = register(
		"forest_flower_vegetation",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(new SimpleRandomFeatureConfig(FOREST_FLOWER_VEGETATION_CONFIGS))
			.repeat(ClampedIntProvider.create(UniformIntProvider.create(-3, 1), 0, 1))
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(5)
	);
	public static final ConfiguredFeature<?, ?> DARK_FOREST_VEGETATION_BROWN = register(
		"dark_forest_vegetation_brown",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(
						HUGE_BROWN_MUSHROOM.withChance(0.025F),
						HUGE_RED_MUSHROOM.withChance(0.05F),
						DARK_OAK.withChance(0.6666667F),
						BIRCH.withChance(0.2F),
						FANCY_OAK.withChance(0.1F)
					),
					OAK
				)
			)
			.decorate(ConfiguredFeatures.Decorators.DARK_OAK_TREE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> DARK_FOREST_VEGETATION_RED = register(
		"dark_forest_vegetation_red",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(
						HUGE_RED_MUSHROOM.withChance(0.025F),
						HUGE_BROWN_MUSHROOM.withChance(0.05F),
						DARK_OAK.withChance(0.6666667F),
						BIRCH.withChance(0.2F),
						FANCY_OAK.withChance(0.1F)
					),
					OAK
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
	public static final ConfiguredFeature<?, ?> TAIGA_VEGETATION = register(
		"taiga_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(PINE.withChance(0.33333334F)), SPRUCE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_BADLANDS = register(
		"trees_badlands",
		OAK.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(5, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SNOWY = register(
		"trees_snowy",
		SPRUCE.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SWAMP = register(
		"trees_swamp",
		SWAMP_OAK.decorate(ConfiguredFeatures.Decorators.HEIGHTMAP_OCEAN_FLOOR)
			.decorate(Decorator.WATER_DEPTH_THRESHOLD.configure(new WaterDepthThresholdDecoratorConfig(1)))
			.spreadHorizontally()
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SHATTERED_SAVANNA = register(
		"trees_shattered_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA.withChance(0.8F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SAVANNA = register(
		"trees_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA.withChance(0.8F)), OAK))
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
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE.withChance(0.666F), FANCY_OAK.withChance(0.1F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(3, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN = register(
		"trees_mountain",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE.withChance(0.666F), FANCY_OAK.withChance(0.1F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_WATER = register(
		"trees_water",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F)), OAK))
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
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK_BEES_005.withChance(0.33333334F)), OAK_BEES_005))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.05F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE_EDGE = register(
		"trees_jungle_edge",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F)), JUNGLE_TREE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT_SPRUCE = register(
		"trees_giant_spruce",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.withChance(0.33333334F), PINE.withChance(0.33333334F)), SPRUCE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT = register(
		"trees_giant",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.withChance(0.025641026F), MEGA_PINE.withChance(0.30769232F), PINE.withChance(0.33333334F)), SPRUCE)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE = register(
		"trees_jungle",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F), MEGA_JUNGLE_TREE.withChance(0.33333334F)), JUNGLE_TREE)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(50, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BAMBOO_VEGETATION = register(
		"bamboo_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(FANCY_OAK.withChance(0.05F), JUNGLE_BUSH.withChance(0.15F), MEGA_JUNGLE_TREE.withChance(0.7F)),
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
					new SimpleBlockStateProvider(Blocks.ROOTED_DIRT.getDefaultState()),
					20,
					100,
					3,
					2,
					new SimpleBlockStateProvider(Blocks.HANGING_ROOTS.getDefaultState()),
					20,
					2
				)
			)
			.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_60)
			.spreadHorizontally()
			.applyChance(2)
	);
	private static final WeightedBlockStateProvider RANDOM_BERRIES_CAVE_VINES_BODY_PROVIDER = new WeightedBlockStateProvider(
		method_35926()
			.add(Blocks.CAVE_VINES_PLANT.getDefaultState(), 4)
			.add(Blocks.CAVE_VINES_PLANT.getDefaultState().with(CaveVines.BERRIES, Boolean.valueOf(true)), 1)
	);
	private static final RandomizedIntBlockStateProvider RANDOM_AGE_CAVE_VINES_HEAD_PROVIDER = new RandomizedIntBlockStateProvider(
		new WeightedBlockStateProvider(
			method_35926().add(Blocks.CAVE_VINES.getDefaultState(), 4).add(Blocks.CAVE_VINES.getDefaultState().with(CaveVines.BERRIES, Boolean.valueOf(true)), 1)
		),
		CaveVinesHeadBlock.AGE,
		UniformIntProvider.create(17, 25)
	);
	public static final ConfiguredFeature<GrowingPlantFeatureConfig, ?> CAVE_VINE = register(
		"cave_vine",
		Feature.GROWING_PLANT
			.configure(
				new GrowingPlantFeatureConfig(
					DataPool.<IntProvider>builder()
						.add(UniformIntProvider.create(1, 20), 2)
						.add(UniformIntProvider.create(1, 3), 3)
						.add(UniformIntProvider.create(1, 7), 10)
						.build(),
					Direction.DOWN,
					RANDOM_BERRIES_CAVE_VINES_BODY_PROVIDER,
					RANDOM_AGE_CAVE_VINES_HEAD_PROVIDER,
					false
				)
			)
	);
	public static final ConfiguredFeature<GrowingPlantFeatureConfig, ?> CAVE_VINE_IN_MOSS = register(
		"cave_vine_in_moss",
		Feature.GROWING_PLANT
			.configure(
				new GrowingPlantFeatureConfig(
					DataPool.<IntProvider>builder().add(UniformIntProvider.create(1, 4), 5).add(UniformIntProvider.create(2, 8), 1).build(),
					Direction.DOWN,
					RANDOM_BERRIES_CAVE_VINES_BODY_PROVIDER,
					RANDOM_AGE_CAVE_VINES_HEAD_PROVIDER,
					false
				)
			)
	);
	public static final ConfiguredFeature<?, ?> CAVE_VINES = register(
		"cave_vines",
		CAVE_VINE.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_60)
			.spreadHorizontally()
			.repeat(60)
	);
	public static final ConfiguredFeature<SimpleBlockFeatureConfig, ?> MOSS_VEGETATION = register(
		"moss_vegetation",
		Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(
						method_35926()
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
					new SimpleBlockStateProvider(Blocks.MOSS_BLOCK.getDefaultState()),
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
					new SimpleBlockStateProvider(Blocks.MOSS_BLOCK.getDefaultState()),
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
		MOSS_PATCH.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.FLOOR, 12)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_60)
			.spreadHorizontally()
			.repeat(40)
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
					new SimpleBlockStateProvider(Blocks.CLAY.getDefaultState()),
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
					new SimpleBlockStateProvider(Blocks.CLAY.getDefaultState()),
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
			.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.FLOOR, 12)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_60)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<VegetationPatchFeatureConfig, ?> MOSS_PATCH_CEILING = register(
		"moss_patch_ceiling",
		Feature.VEGETATION_PATCH
			.configure(
				new VegetationPatchFeatureConfig(
					BlockTags.MOSS_REPLACEABLE.getId(),
					new SimpleBlockStateProvider(Blocks.MOSS_BLOCK.getDefaultState()),
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
		MOSS_PATCH_CEILING.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_60)
			.spreadHorizontally()
			.repeat(40)
	);
	public static final ConfiguredFeature<?, ?> SPORE_BLOSSOM = register(
		"spore_blossom",
		Feature.SIMPLE_BLOCK
			.configure(new SimpleBlockFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.States.SPORE_BLOSSOM)))
			.decorate(Decorator.CAVE_SURFACE.configure(new CaveSurfaceDecoratorConfig(VerticalSurfaceType.CEILING, 12)))
			.range(ConfiguredFeatures.Decorators.BOTTOM_TO_60)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> CLASSIC_VINES_CAVE_FEATURE = register(
		"classic_vines_cave_feature",
		Feature.VINES.configure(FeatureConfig.DEFAULT).range(ConfiguredFeatures.Decorators.BOTTOM_TO_60).spreadHorizontally().repeat(127)
	);
	public static final ConfiguredFeature<?, ?> AMETHYST_GEODE = register(
		"amethyst_geode",
		Feature.GEODE
			.configure(
				new GeodeFeatureConfig(
					new GeodeLayerConfig(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.AIR),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.AMETHYST_BLOCK),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BUDDING_AMETHYST),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.CALCITE),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SMOOTH_BASALT),
						ImmutableList.of(
							Blocks.SMALL_AMETHYST_BUD.getDefaultState(),
							Blocks.MEDIUM_AMETHYST_BUD.getDefaultState(),
							Blocks.LARGE_AMETHYST_BUD.getDefaultState(),
							Blocks.AMETHYST_CLUSTER.getDefaultState()
						)
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
			.method_36296(YOffset.aboveBottom(6), YOffset.fixed(46))
			.spreadHorizontally()
			.applyChance(30)
	);

	private static DataPool.Builder<BlockState> method_35926() {
		return DataPool.builder();
	}

	private static ConfiguredFeature<GrowingPlantFeatureConfig, ?> createBigDripleafFeature(Direction blockDirection) {
		return Feature.GROWING_PLANT
			.configure(
				new GrowingPlantFeatureConfig(
					DataPool.<IntProvider>builder().add(UniformIntProvider.create(1, 5), 2).add(ConstantIntProvider.create(1), 1).build(),
					Direction.UP,
					new SimpleBlockStateProvider(Blocks.BIG_DRIPLEAF_STEM.getDefaultState().with(Properties.HORIZONTAL_FACING, blockDirection)),
					new SimpleBlockStateProvider(Blocks.BIG_DRIPLEAF.getDefaultState().with(Properties.HORIZONTAL_FACING, blockDirection)),
					true
				)
			);
	}

	private static ConfiguredFeature<SimpleBlockFeatureConfig, ?> createSmallDripleafFeature() {
		return Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					new WeightedBlockStateProvider(
						method_35926()
							.add(ConfiguredFeatures.States.SMALL_DRIPLEAF_EAST, 1)
							.add(ConfiguredFeatures.States.SMALL_DRIPLEAF_WEST, 1)
							.add(ConfiguredFeatures.States.SMALL_DRIPLEAF_NORTH, 1)
							.add(ConfiguredFeatures.States.SMALL_DRIPLEAF_SOUTH, 1)
					)
				)
			);
	}

	private static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String id, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, id, configuredFeature);
	}

	public static final class Configs {
		public static final RandomPatchFeatureConfig GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.States.GRASS), SimpleBlockPlacer.INSTANCE
			)
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig TAIGA_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider(ConfiguredFeatures.method_35926().add(ConfiguredFeatures.States.GRASS, 1).add(ConfiguredFeatures.States.FERN, 4)),
				SimpleBlockPlacer.INSTANCE
			)
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig LUSH_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider(ConfiguredFeatures.method_35926().add(ConfiguredFeatures.States.GRASS, 3).add(ConfiguredFeatures.States.FERN, 1)),
				SimpleBlockPlacer.INSTANCE
			)
			.blacklist(ImmutableSet.of(ConfiguredFeatures.States.PODZOL))
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig DEFAULT_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider(ConfiguredFeatures.method_35926().add(ConfiguredFeatures.States.POPPY, 2).add(ConfiguredFeatures.States.DANDELION, 1)),
				SimpleBlockPlacer.INSTANCE
			)
			.tries(64)
			.build();
		public static final RandomPatchFeatureConfig DEAD_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.States.DEAD_BUSH), SimpleBlockPlacer.INSTANCE
			)
			.tries(4)
			.build();
		public static final RandomPatchFeatureConfig SWEET_BERRY_BUSH_CONFIG = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.States.SWEET_BERRY_BUSH), SimpleBlockPlacer.INSTANCE
			)
			.tries(64)
			.whitelist(ImmutableSet.of(ConfiguredFeatures.States.GRASS_BLOCK.getBlock()))
			.cannotProject()
			.build();
		public static final RandomPatchFeatureConfig TALL_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.States.TALL_GRASS), new DoublePlantPlacer()
			)
			.tries(64)
			.cannotProject()
			.build();
		public static final RandomPatchFeatureConfig SUGAR_CANE_CONFIG = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.States.SUGAR_CANE), new ColumnPlacer(BiasedToBottomIntProvider.create(2, 4))
			)
			.tries(20)
			.spreadX(4)
			.spreadY(0)
			.spreadZ(4)
			.cannotProject()
			.needsWater()
			.build();
		public static final SpringFeatureConfig LAVA_SPRING_CONFIG = new SpringFeatureConfig(
			ConfiguredFeatures.States.LAVA_FLUID,
			true,
			4,
			1,
			ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF)
		);
		public static final SpringFeatureConfig ENCLOSED_NETHER_SPRING_CONFIG = new SpringFeatureConfig(
			ConfiguredFeatures.States.LAVA_FLUID, false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK)
		);
		public static final BlockPileFeatureConfig CRIMSON_ROOTS_CONFIG = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider(
				ConfiguredFeatures.method_35926()
					.add(ConfiguredFeatures.States.CRIMSON_ROOTS, 87)
					.add(ConfiguredFeatures.States.CRIMSON_FUNGUS, 11)
					.add(ConfiguredFeatures.States.WARPED_FUNGUS, 1)
			)
		);
		public static final BlockPileFeatureConfig WARPED_ROOTS_CONFIG = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider(
				ConfiguredFeatures.method_35926()
					.add(ConfiguredFeatures.States.WARPED_ROOTS, 85)
					.add(ConfiguredFeatures.States.CRIMSON_ROOTS, 1)
					.add(ConfiguredFeatures.States.WARPED_FUNGUS, 13)
					.add(ConfiguredFeatures.States.CRIMSON_FUNGUS, 1)
			)
		);
		public static final BlockPileFeatureConfig NETHER_SPROUTS_CONFIG = new BlockPileFeatureConfig(
			new SimpleBlockStateProvider(ConfiguredFeatures.States.NETHER_SPROUTS)
		);
	}

	public static final class Decorators {
		public static final BeehiveTreeDecorator VERY_RARE_BEEHIVES_TREES = new BeehiveTreeDecorator(0.002F);
		public static final BeehiveTreeDecorator REGULAR_BEEHIVES_TREES = new BeehiveTreeDecorator(0.02F);
		public static final BeehiveTreeDecorator MORE_BEEHIVES_TREES = new BeehiveTreeDecorator(0.05F);
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING));
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> TOP_SOLID_HEIGHTMAP = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR_WG));
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_WORLD_SURFACE = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.WORLD_SURFACE_WG));
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_OCEAN_FLOOR = Decorator.HEIGHTMAP
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.OCEAN_FLOOR));
		public static final ConfiguredDecorator<HeightmapDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.HEIGHTMAP_SPREAD_DOUBLE
			.configure(new HeightmapDecoratorConfig(Heightmap.Type.MOTION_BLOCKING));
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
		public static final RangeDecoratorConfig BOTTOM_TO_60 = new RangeDecoratorConfig(UniformHeightProvider.create(YOffset.getBottom(), YOffset.fixed(60)));
		public static final ConfiguredDecorator<?> FIRE = Decorator.RANGE.configure(BOTTOM_TO_TOP_OFFSET_4).spreadHorizontally().repeatRandomly(5);
		public static final ConfiguredDecorator<?> SPREAD_32_ABOVE = Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE);
		public static final ConfiguredDecorator<?> HEIGHTMAP_OCEAN_FLOOR_NO_WATER = HEIGHTMAP_OCEAN_FLOOR.decorate(
			Decorator.WATER_DEPTH_THRESHOLD.configure(new WaterDepthThresholdDecoratorConfig(0))
		);
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_OCEAN_FLOOR_NO_WATER = HEIGHTMAP_OCEAN_FLOOR_NO_WATER.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP = HEIGHTMAP.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_SPREAD_DOUBLE = HEIGHTMAP_SPREAD_DOUBLE.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_TOP_SOLID_HEIGHTMAP = TOP_SOLID_HEIGHTMAP.spreadHorizontally();
		public static final ConfiguredDecorator<?> DARK_OAK_TREE_HEIGHTMAP = HEIGHTMAP_OCEAN_FLOOR_NO_WATER.decorate(
			Decorator.DARK_OAK_TREE.configure(DecoratorConfig.DEFAULT)
		);

		protected Decorators() {
		}
	}

	public static final class States {
		protected static final BlockState GRASS = Blocks.GRASS.getDefaultState();
		protected static final BlockState FERN = Blocks.FERN.getDefaultState();
		protected static final BlockState PODZOL = Blocks.PODZOL.getDefaultState();
		protected static final BlockState COARSE_DIRT = Blocks.COARSE_DIRT.getDefaultState();
		protected static final BlockState MYCELIUM = Blocks.MYCELIUM.getDefaultState();
		protected static final BlockState SNOW_BLOCK = Blocks.SNOW_BLOCK.getDefaultState();
		protected static final BlockState ICE = Blocks.ICE.getDefaultState();
		protected static final BlockState OAK_LOG = Blocks.OAK_LOG.getDefaultState();
		protected static final BlockState OAK_LEAVES = Blocks.OAK_LEAVES.getDefaultState();
		protected static final BlockState JUNGLE_LOG = Blocks.JUNGLE_LOG.getDefaultState();
		protected static final BlockState JUNGLE_LEAVES = Blocks.JUNGLE_LEAVES.getDefaultState();
		protected static final BlockState SPRUCE_LOG = Blocks.SPRUCE_LOG.getDefaultState();
		protected static final BlockState SPRUCE_LEAVES = Blocks.SPRUCE_LEAVES.getDefaultState();
		protected static final BlockState ACACIA_LOG = Blocks.ACACIA_LOG.getDefaultState();
		protected static final BlockState ACACIA_LEAVES = Blocks.ACACIA_LEAVES.getDefaultState();
		protected static final BlockState BIRCH_LOG = Blocks.BIRCH_LOG.getDefaultState();
		protected static final BlockState BIRCH_LEAVES = Blocks.BIRCH_LEAVES.getDefaultState();
		protected static final BlockState DARK_OAK_LOG = Blocks.DARK_OAK_LOG.getDefaultState();
		protected static final BlockState DARK_OAK_LEAVES = Blocks.DARK_OAK_LEAVES.getDefaultState();
		protected static final BlockState GRASS_BLOCK = Blocks.GRASS_BLOCK.getDefaultState();
		protected static final BlockState LARGE_FERN = Blocks.LARGE_FERN.getDefaultState();
		protected static final BlockState TALL_GRASS = Blocks.TALL_GRASS.getDefaultState();
		protected static final BlockState LILAC = Blocks.LILAC.getDefaultState();
		protected static final BlockState ROSE_BUSH = Blocks.ROSE_BUSH.getDefaultState();
		protected static final BlockState PEONY = Blocks.PEONY.getDefaultState();
		protected static final BlockState BROWN_MUSHROOM = Blocks.BROWN_MUSHROOM.getDefaultState();
		protected static final BlockState RED_MUSHROOM = Blocks.RED_MUSHROOM.getDefaultState();
		protected static final BlockState PACKED_ICE = Blocks.PACKED_ICE.getDefaultState();
		protected static final BlockState BLUE_ICE = Blocks.BLUE_ICE.getDefaultState();
		protected static final BlockState LILY_OF_THE_VALLEY = Blocks.LILY_OF_THE_VALLEY.getDefaultState();
		protected static final BlockState BLUE_ORCHID = Blocks.BLUE_ORCHID.getDefaultState();
		protected static final BlockState POPPY = Blocks.POPPY.getDefaultState();
		protected static final BlockState DANDELION = Blocks.DANDELION.getDefaultState();
		protected static final BlockState DEAD_BUSH = Blocks.DEAD_BUSH.getDefaultState();
		protected static final BlockState MELON = Blocks.MELON.getDefaultState();
		protected static final BlockState PUMPKIN = Blocks.PUMPKIN.getDefaultState();
		protected static final BlockState SWEET_BERRY_BUSH = Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3));
		protected static final BlockState FIRE = Blocks.FIRE.getDefaultState();
		protected static final BlockState SOUL_FIRE = Blocks.SOUL_FIRE.getDefaultState();
		protected static final BlockState NETHERRACK = Blocks.NETHERRACK.getDefaultState();
		protected static final BlockState SOUL_SOIL = Blocks.SOUL_SOIL.getDefaultState();
		protected static final BlockState CRIMSON_ROOTS = Blocks.CRIMSON_ROOTS.getDefaultState();
		protected static final BlockState LILY_PAD = Blocks.LILY_PAD.getDefaultState();
		protected static final BlockState SNOW = Blocks.SNOW.getDefaultState();
		protected static final BlockState JACK_O_LANTERN = Blocks.JACK_O_LANTERN.getDefaultState();
		protected static final BlockState SUNFLOWER = Blocks.SUNFLOWER.getDefaultState();
		protected static final BlockState CACTUS = Blocks.CACTUS.getDefaultState();
		protected static final BlockState SUGAR_CANE = Blocks.SUGAR_CANE.getDefaultState();
		protected static final BlockState RED_MUSHROOM_BLOCK = Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final BlockState BROWN_MUSHROOM_BLOCK = Blocks.BROWN_MUSHROOM_BLOCK
			.getDefaultState()
			.with(MushroomBlock.UP, Boolean.valueOf(true))
			.with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final BlockState MUSHROOM_STEM = Blocks.MUSHROOM_STEM
			.getDefaultState()
			.with(MushroomBlock.UP, Boolean.valueOf(false))
			.with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final FluidState WATER_FLUID = Fluids.WATER.getDefaultState();
		protected static final FluidState LAVA_FLUID = Fluids.LAVA.getDefaultState();
		protected static final BlockState WATER_BLOCK = Blocks.WATER.getDefaultState();
		protected static final BlockState LAVA_BLOCK = Blocks.LAVA.getDefaultState();
		protected static final BlockState DIRT = Blocks.DIRT.getDefaultState();
		protected static final BlockState GRAVEL = Blocks.GRAVEL.getDefaultState();
		protected static final BlockState GRANITE = Blocks.GRANITE.getDefaultState();
		protected static final BlockState DIORITE = Blocks.DIORITE.getDefaultState();
		protected static final BlockState ANDESITE = Blocks.ANDESITE.getDefaultState();
		protected static final BlockState COAL_ORE = Blocks.COAL_ORE.getDefaultState();
		protected static final BlockState field_33637 = Blocks.DEEPSLATE_COAL_ORE.getDefaultState();
		protected static final BlockState COPPER_ORE = Blocks.COPPER_ORE.getDefaultState();
		protected static final BlockState field_33638 = Blocks.DEEPSLATE_COPPER_ORE.getDefaultState();
		protected static final BlockState IRON_ORE = Blocks.IRON_ORE.getDefaultState();
		protected static final BlockState DEEPSLATE_IRON_ORE = Blocks.DEEPSLATE_IRON_ORE.getDefaultState();
		protected static final BlockState GOLD_ORE = Blocks.GOLD_ORE.getDefaultState();
		protected static final BlockState DEEPSLATE_GOLD_ORE = Blocks.DEEPSLATE_GOLD_ORE.getDefaultState();
		protected static final BlockState REDSTONE_ORE = Blocks.REDSTONE_ORE.getDefaultState();
		protected static final BlockState DEEPSLATE_REDSTONE_ORE = Blocks.DEEPSLATE_REDSTONE_ORE.getDefaultState();
		protected static final BlockState DIAMOND_ORE = Blocks.DIAMOND_ORE.getDefaultState();
		protected static final BlockState DEEPSLATE_DIAMOND_ORE = Blocks.DEEPSLATE_DIAMOND_ORE.getDefaultState();
		protected static final BlockState LAPIS_ORE = Blocks.LAPIS_ORE.getDefaultState();
		protected static final BlockState DEEPSLATE_LAPIS_ORE = Blocks.DEEPSLATE_LAPIS_ORE.getDefaultState();
		protected static final BlockState STONE = Blocks.STONE.getDefaultState();
		protected static final BlockState EMERALD_ORE = Blocks.EMERALD_ORE.getDefaultState();
		protected static final BlockState field_33639 = Blocks.DEEPSLATE_EMERALD_ORE.getDefaultState();
		protected static final BlockState INFESTED_STONE = Blocks.INFESTED_STONE.getDefaultState();
		protected static final BlockState INFESTED_DEEPSLATE = Blocks.INFESTED_DEEPSLATE.getDefaultState();
		protected static final BlockState SAND = Blocks.SAND.getDefaultState();
		protected static final BlockState CLAY = Blocks.CLAY.getDefaultState();
		protected static final BlockState MOSSY_COBBLESTONE = Blocks.MOSSY_COBBLESTONE.getDefaultState();
		protected static final BlockState SEAGRASS = Blocks.SEAGRASS.getDefaultState();
		protected static final BlockState MAGMA_BLOCK = Blocks.MAGMA_BLOCK.getDefaultState();
		protected static final BlockState SOUL_SAND = Blocks.SOUL_SAND.getDefaultState();
		protected static final BlockState NETHER_GOLD_ORE = Blocks.NETHER_GOLD_ORE.getDefaultState();
		protected static final BlockState NETHER_QUARTZ_ORE = Blocks.NETHER_QUARTZ_ORE.getDefaultState();
		protected static final BlockState BLACKSTONE = Blocks.BLACKSTONE.getDefaultState();
		protected static final BlockState ANCIENT_DEBRIS = Blocks.ANCIENT_DEBRIS.getDefaultState();
		protected static final BlockState BASALT = Blocks.BASALT.getDefaultState();
		protected static final BlockState CRIMSON_FUNGUS = Blocks.CRIMSON_FUNGUS.getDefaultState();
		protected static final BlockState WARPED_FUNGUS = Blocks.WARPED_FUNGUS.getDefaultState();
		protected static final BlockState WARPED_ROOTS = Blocks.WARPED_ROOTS.getDefaultState();
		protected static final BlockState NETHER_SPROUTS = Blocks.NETHER_SPROUTS.getDefaultState();
		protected static final BlockState AIR = Blocks.AIR.getDefaultState();
		protected static final BlockState AMETHYST_BLOCK = Blocks.AMETHYST_BLOCK.getDefaultState();
		protected static final BlockState BUDDING_AMETHYST = Blocks.BUDDING_AMETHYST.getDefaultState();
		protected static final BlockState CALCITE = Blocks.CALCITE.getDefaultState();
		protected static final BlockState SMOOTH_BASALT = Blocks.SMOOTH_BASALT.getDefaultState();
		protected static final BlockState TUFF = Blocks.TUFF.getDefaultState();
		protected static final BlockState SPORE_BLOSSOM = Blocks.SPORE_BLOSSOM.getDefaultState();
		protected static final BlockState SMALL_DRIPLEAF_EAST = Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.EAST);
		protected static final BlockState SMALL_DRIPLEAF_WEST = Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.WEST);
		protected static final BlockState SMALL_DRIPLEAF_NORTH = Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.NORTH);
		protected static final BlockState SMALL_DRIPLEAF_SOUTH = Blocks.SMALL_DRIPLEAF.getDefaultState().with(SmallDripleafBlock.FACING, Direction.SOUTH);
		protected static final BlockState BIG_DRIPLEAF_EAST = Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.EAST);
		protected static final BlockState BIG_DRIPLEAF_WEST = Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.WEST);
		protected static final BlockState BIG_DRIPLEAF_NORTH = Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.NORTH);
		protected static final BlockState BIG_DIRPLEAF_SOUTH = Blocks.BIG_DRIPLEAF.getDefaultState().with(BigDripleafBlock.FACING, Direction.SOUTH);
		protected static final BlockState field_33144 = Blocks.DEEPSLATE.getDefaultState();
	}
}
