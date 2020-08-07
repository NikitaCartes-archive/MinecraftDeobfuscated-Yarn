package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.MushroomBlock;
import net.minecraft.block.SweetBerryBushBlock;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.CountConfig;
import net.minecraft.world.gen.GenerationStep;
import net.minecraft.world.gen.ProbabilityConfig;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountExtraDecoratorConfig;
import net.minecraft.world.gen.decorator.CountNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.CountNoiseDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.DepthAverageDecoratorConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.feature.size.ThreeLayersFeatureSize;
import net.minecraft.world.gen.feature.size.TwoLayersFeatureSize;
import net.minecraft.world.gen.foliage.AcaciaFoliagePlacer;
import net.minecraft.world.gen.foliage.BlobFoliagePlacer;
import net.minecraft.world.gen.foliage.BushFoliagePlacer;
import net.minecraft.world.gen.foliage.DarkOakFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.foliage.PineFoliagePlacer;
import net.minecraft.world.gen.foliage.SpruceFoliagePlacer;
import net.minecraft.world.gen.placer.ColumnPlacer;
import net.minecraft.world.gen.placer.DoublePlantPlacer;
import net.minecraft.world.gen.placer.SimpleBlockPlacer;
import net.minecraft.world.gen.stateprovider.ForestFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PillarBlockStateProvider;
import net.minecraft.world.gen.stateprovider.PlainsFlowerBlockStateProvider;
import net.minecraft.world.gen.stateprovider.SimpleBlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;
import net.minecraft.world.gen.tree.AlterGroundTreeDecorator;
import net.minecraft.world.gen.tree.BeehiveTreeDecorator;
import net.minecraft.world.gen.tree.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.tree.LeaveVineTreeDecorator;
import net.minecraft.world.gen.tree.TrunkVineTreeDecorator;
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class ConfiguredFeatures {
	public static final ConfiguredFeature<?, ?> field_26040 = register(
		"end_spike", Feature.field_13522.configure(new EndSpikeFeatureConfig(false, ImmutableList.of(), null))
	);
	public static final ConfiguredFeature<?, ?> field_26091 = register(
		"end_gateway",
		Feature.field_13564
			.configure(EndGatewayFeatureConfig.createConfig(ServerWorld.END_SPAWN_POS, true))
			.method_23388(Decorator.field_14230.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> field_26118 = register(
		"end_gateway_delayed", Feature.field_13564.configure(EndGatewayFeatureConfig.createConfig())
	);
	public static final ConfiguredFeature<?, ?> field_26119 = register(
		"chorus_plant", Feature.field_13552.configure(FeatureConfig.DEFAULT).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeatRandomly(4)
	);
	public static final ConfiguredFeature<?, ?> field_26120 = register("end_island", Feature.field_13574.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> field_26121 = register(
		"end_island_decorated", field_26120.method_23388(Decorator.field_14251.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> field_26122 = register(
		"delta",
		Feature.field_23885
			.configure(
				new DeltaFeatureConfig(
					ConfiguredFeatures.States.LAVA_BLOCK, ConfiguredFeatures.States.MAGMA_BLOCK, UniformIntDistribution.of(3, 4), UniformIntDistribution.of(0, 2)
				)
			)
			.method_23388(Decorator.field_25860.configure(new CountConfig(40)))
	);
	public static final ConfiguredFeature<?, ?> field_26123 = register(
		"small_basalt_columns",
		Feature.field_23884
			.configure(new BasaltColumnsFeatureConfig(UniformIntDistribution.of(1), UniformIntDistribution.of(1, 3)))
			.method_23388(Decorator.field_25860.configure(new CountConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> field_26124 = register(
		"large_basalt_columns",
		Feature.field_23884
			.configure(new BasaltColumnsFeatureConfig(UniformIntDistribution.of(2, 1), UniformIntDistribution.of(5, 5)))
			.method_23388(Decorator.field_25860.configure(new CountConfig(2)))
	);
	public static final ConfiguredFeature<?, ?> field_26125 = register(
		"basalt_blobs",
		Feature.field_23886
			.configure(new NetherrackReplaceBlobsFeatureConfig(ConfiguredFeatures.States.NETHERRACK, ConfiguredFeatures.States.BASALT, UniformIntDistribution.of(3, 4)))
			.method_30377(128)
			.spreadHorizontally()
			.repeat(75)
	);
	public static final ConfiguredFeature<?, ?> field_26126 = register(
		"blackstone_blobs",
		Feature.field_23886
			.configure(
				new NetherrackReplaceBlobsFeatureConfig(ConfiguredFeatures.States.NETHERRACK, ConfiguredFeatures.States.BLACKSTONE, UniformIntDistribution.of(3, 4))
			)
			.method_30377(128)
			.spreadHorizontally()
			.repeat(25)
	);
	public static final ConfiguredFeature<?, ?> field_26127 = register(
		"glowstone_extra", Feature.field_13568.configure(FeatureConfig.DEFAULT).method_23388(Decorator.field_25875.configure(new CountConfig(10)))
	);
	public static final ConfiguredFeature<?, ?> field_26128 = register(
		"glowstone", Feature.field_13568.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26129 = register(
		"crimson_forest_vegetation",
		Feature.field_22186.configure(ConfiguredFeatures.Configs.CRIMSON_ROOTS_CONFIG).method_23388(Decorator.field_25860.configure(new CountConfig(6)))
	);
	public static final ConfiguredFeature<?, ?> field_26130 = register(
		"warped_forest_vegetation",
		Feature.field_22186.configure(ConfiguredFeatures.Configs.WARPED_ROOTS_CONFIG).method_23388(Decorator.field_25860.configure(new CountConfig(5)))
	);
	public static final ConfiguredFeature<?, ?> field_26131 = register(
		"nether_sprouts",
		Feature.field_22186.configure(ConfiguredFeatures.Configs.NETHER_SPROUTS_CONFIG).method_23388(Decorator.field_25860.configure(new CountConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> field_26132 = register(
		"twisting_vines", Feature.field_23088.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26133 = register(
		"weeping_vines", Feature.field_22187.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26134 = register(
		"basalt_pillar", Feature.field_22188.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26135 = register(
		"seagrass_cold", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).repeat(32).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_26136 = register(
		"seagrass_deep_cold",
		Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).repeat(40).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_26137 = register(
		"seagrass_normal", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).repeat(48).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_26138 = register(
		"seagrass_river", Feature.SEAGRASS.configure(new ProbabilityConfig(0.4F)).repeat(48).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_26139 = register(
		"seagrass_deep", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).repeat(48).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_26140 = register(
		"seagrass_swamp", Feature.SEAGRASS.configure(new ProbabilityConfig(0.6F)).repeat(64).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_25949 = register(
		"seagrass_warm", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).repeat(80).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_25950 = register(
		"seagrass_deep_warm",
		Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).repeat(80).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_25951 = register(
		"sea_pickle", Feature.field_13575.configure(new CountConfig(20)).method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP).applyChance(16)
	);
	public static final ConfiguredFeature<?, ?> field_25952 = register(
		"ice_spike", Feature.field_13562.configure(FeatureConfig.DEFAULT).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeat(3)
	);
	public static final ConfiguredFeature<?, ?> field_25953 = register(
		"ice_patch",
		Feature.field_13551
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.States.PACKED_ICE,
					UniformIntDistribution.of(2, 1),
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
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> field_25954 = register(
		"forest_rock",
		Feature.field_13584
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.MOSSY_COBBLESTONE))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeatRandomly(2)
	);
	public static final ConfiguredFeature<?, ?> field_25955 = register(
		"seagrass_simple",
		Feature.field_13518
			.configure(
				new SimpleBlockFeatureConfig(
					ConfiguredFeatures.States.SEAGRASS,
					ImmutableList.of(ConfiguredFeatures.States.STONE),
					ImmutableList.of(ConfiguredFeatures.States.WATER_BLOCK),
					ImmutableList.of(ConfiguredFeatures.States.WATER_BLOCK)
				)
			)
			.method_23388(Decorator.field_14229.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.field_13166, 0.1F)))
	);
	public static final ConfiguredFeature<?, ?> field_25956 = register(
		"iceberg_packed",
		Feature.field_13544
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.PACKED_ICE))
			.method_23388(Decorator.field_14243.configure(NopeDecoratorConfig.INSTANCE))
			.applyChance(16)
	);
	public static final ConfiguredFeature<?, ?> field_25957 = register(
		"iceberg_blue",
		Feature.field_13544
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.BLUE_ICE))
			.method_23388(Decorator.field_14243.configure(NopeDecoratorConfig.INSTANCE))
			.applyChance(200)
	);
	public static final ConfiguredFeature<?, ?> field_25958 = register(
		"kelp_cold",
		Feature.field_13535
			.configure(FeatureConfig.DEFAULT)
			.method_23388(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP)
			.spreadHorizontally()
			.method_23388(Decorator.field_25864.configure(new CountNoiseBiasedDecoratorConfig(120, 80.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> field_25959 = register(
		"kelp_warm",
		Feature.field_13535
			.configure(FeatureConfig.DEFAULT)
			.method_23388(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP)
			.spreadHorizontally()
			.method_23388(Decorator.field_25864.configure(new CountNoiseBiasedDecoratorConfig(80, 80.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> field_25960 = register(
		"blue_ice",
		Feature.field_13560
			.configure(FeatureConfig.DEFAULT)
			.method_23388(Decorator.field_25870.configure(new RangeDecoratorConfig(30, 32, 64)))
			.spreadHorizontally()
			.repeatRandomly(19)
	);
	public static final ConfiguredFeature<?, ?> field_25961 = register(
		"bamboo_light",
		Feature.field_13540.configure(new ProbabilityConfig(0.0F)).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(16)
	);
	public static final ConfiguredFeature<?, ?> field_25962 = register(
		"bamboo",
		Feature.field_13540
			.configure(new ProbabilityConfig(0.2F))
			.method_23388(ConfiguredFeatures.Decorators.HEIGHTMAP_WORLD_SURFACE)
			.spreadHorizontally()
			.method_23388(Decorator.field_25864.configure(new CountNoiseBiasedDecoratorConfig(160, 80.0, 0.3)))
	);
	public static final ConfiguredFeature<?, ?> field_25963 = register(
		"vines", Feature.field_13559.configure(FeatureConfig.DEFAULT).spreadHorizontally().repeat(50)
	);
	public static final ConfiguredFeature<?, ?> field_25964 = register(
		"lake_water",
		Feature.field_13573
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.WATER_BLOCK))
			.method_23388(Decorator.field_14242.configure(new ChanceDecoratorConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> field_25965 = register(
		"lake_lava",
		Feature.field_13573
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.LAVA_BLOCK))
			.method_23388(Decorator.field_14237.configure(new ChanceDecoratorConfig(80)))
	);
	public static final ConfiguredFeature<?, ?> field_25966 = register(
		"disk_clay",
		Feature.field_13509
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.States.CLAY, UniformIntDistribution.of(2, 1), 1, ImmutableList.of(ConfiguredFeatures.States.DIRT, ConfiguredFeatures.States.CLAY)
				)
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_25967 = register(
		"disk_gravel",
		Feature.field_13509
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.States.GRAVEL,
					UniformIntDistribution.of(2, 3),
					2,
					ImmutableList.of(ConfiguredFeatures.States.DIRT, ConfiguredFeatures.States.GRASS_BLOCK)
				)
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_25968 = register(
		"disk_sand",
		Feature.field_13509
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.States.SAND,
					UniformIntDistribution.of(2, 4),
					2,
					ImmutableList.of(ConfiguredFeatures.States.DIRT, ConfiguredFeatures.States.GRASS_BLOCK)
				)
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_TOP_SOLID_HEIGHTMAP)
			.repeat(3)
	);
	public static final ConfiguredFeature<?, ?> field_25969 = register("freeze_top_layer", Feature.field_13539.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> field_25970 = register("bonus_chest", Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> field_25971 = register("void_start_platform", Feature.field_13591.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> field_25972 = register(
		"monster_room", Feature.field_13579.configure(FeatureConfig.DEFAULT).method_30377(256).spreadHorizontally().repeat(8)
	);
	public static final ConfiguredFeature<?, ?> field_25973 = register(
		"desert_well", Feature.field_13592.configure(FeatureConfig.DEFAULT).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(1000)
	);
	public static final ConfiguredFeature<?, ?> field_25974 = register("fossil", Feature.field_13516.configure(FeatureConfig.DEFAULT).applyChance(64));
	public static final ConfiguredFeature<?, ?> field_26002 = register(
		"spring_lava_double",
		Feature.field_13513
			.configure(ConfiguredFeatures.Configs.LAVA_SPRING_CONFIG)
			.method_23388(Decorator.field_25872.configure(new RangeDecoratorConfig(8, 16, 256)))
			.spreadHorizontally()
			.repeat(40)
	);
	public static final ConfiguredFeature<?, ?> field_26003 = register(
		"spring_lava",
		Feature.field_13513
			.configure(ConfiguredFeatures.Configs.LAVA_SPRING_CONFIG)
			.method_23388(Decorator.field_25872.configure(new RangeDecoratorConfig(8, 16, 256)))
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_26004 = register(
		"spring_delta",
		Feature.field_13513
			.configure(
				new SpringFeatureConfig(
					ConfiguredFeatures.States.LAVA_FLUID,
					true,
					4,
					1,
					ImmutableSet.of(Blocks.field_10515, Blocks.field_10114, Blocks.field_10255, Blocks.field_10092, Blocks.field_23869)
				)
			)
			.method_23388(ConfiguredFeatures.Decorators.NETHER_SPRING)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> field_26005 = register(
		"spring_closed",
		Feature.field_13513
			.configure(ConfiguredFeatures.Configs.ENCLOSED_NETHER_SPRING_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> field_26006 = register(
		"spring_closed_double",
		Feature.field_13513
			.configure(ConfiguredFeatures.Configs.ENCLOSED_NETHER_SPRING_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(32)
	);
	public static final ConfiguredFeature<?, ?> field_26007 = register(
		"spring_open",
		Feature.field_13513
			.configure(new SpringFeatureConfig(ConfiguredFeatures.States.LAVA_FLUID, false, 4, 1, ImmutableSet.of(Blocks.field_10515)))
			.method_23388(ConfiguredFeatures.Decorators.NETHER_SPRING)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> field_26008 = register(
		"spring_water",
		Feature.field_13513
			.configure(
				new SpringFeatureConfig(
					ConfiguredFeatures.States.WATER_FLUID, true, 4, 1, ImmutableSet.of(Blocks.field_10340, Blocks.field_10474, Blocks.field_10508, Blocks.field_10115)
				)
			)
			.method_23388(Decorator.field_25871.configure(new RangeDecoratorConfig(8, 8, 256)))
			.spreadHorizontally()
			.repeat(50)
	);
	public static final ConfiguredFeature<?, ?> field_26009 = register(
		"pile_hay", Feature.field_21221.configure(new BlockPileFeatureConfig(new PillarBlockStateProvider(Blocks.field_10359)))
	);
	public static final ConfiguredFeature<?, ?> field_26010 = register(
		"pile_melon", Feature.field_21221.configure(new BlockPileFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.States.MELON)))
	);
	public static final ConfiguredFeature<?, ?> field_26011 = register(
		"pile_snow", Feature.field_21221.configure(new BlockPileFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.States.SNOW)))
	);
	public static final ConfiguredFeature<?, ?> field_26012 = register(
		"pile_ice",
		Feature.field_21221
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider().addState(ConfiguredFeatures.States.BLUE_ICE, 1).addState(ConfiguredFeatures.States.PACKED_ICE, 5)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> field_26013 = register(
		"pile_pumpkin",
		Feature.field_21221
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider().addState(ConfiguredFeatures.States.PUMPKIN, 19).addState(ConfiguredFeatures.States.JACK_O_LANTERN, 1)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> field_26014 = register(
		"patch_fire",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.FIRE), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.NETHERRACK.getBlock()))
					.cannotProject()
					.build()
			)
			.method_23388(ConfiguredFeatures.Decorators.FIRE)
	);
	public static final ConfiguredFeature<?, ?> field_26015 = register(
		"patch_soul_fire",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.SOUL_FIRE), new SimpleBlockPlacer())
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.SOUL_SOIL.getBlock()))
					.cannotProject()
					.build()
			)
			.method_23388(ConfiguredFeatures.Decorators.FIRE)
	);
	public static final ConfiguredFeature<?, ?> field_26016 = register(
		"patch_brown_mushroom",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.BROWN_MUSHROOM), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> field_26017 = register(
		"patch_red_mushroom",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.RED_MUSHROOM), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> field_26018 = register(
		"patch_crimson_roots",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.CRIMSON_ROOTS), new SimpleBlockPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.method_30377(128)
	);
	public static final ConfiguredFeature<?, ?> field_26019 = register(
		"patch_sunflower",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.SUNFLOWER), new DoublePlantPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26020 = register(
		"patch_pumpkin",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.PUMPKIN), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.GRASS_BLOCK.getBlock()))
					.cannotProject()
					.build()
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.applyChance(32)
	);
	public static final ConfiguredFeature<?, ?> field_26021 = register(
		"patch_taiga_grass", Feature.field_21220.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> field_26022 = register(
		"patch_berry_bush", Feature.field_21220.configure(ConfiguredFeatures.Configs.SWEET_BERRY_BUSH_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> field_26023 = register(
		"patch_grass_plain",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.GRASS_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.method_23388(Decorator.field_25863.configure(new CountNoiseDecoratorConfig(-0.8, 5, 10)))
	);
	public static final ConfiguredFeature<?, ?> field_26024 = register(
		"patch_grass_forest",
		Feature.field_21220.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(2)
	);
	public static final ConfiguredFeature<?, ?> field_26025 = register(
		"patch_grass_badlands",
		Feature.field_21220.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> field_26026 = register(
		"patch_grass_savanna",
		Feature.field_21220.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_26027 = register(
		"patch_grass_normal",
		Feature.field_21220.configure(ConfiguredFeatures.Configs.GRASS_CONFIG).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(5)
	);
	public static final ConfiguredFeature<?, ?> field_25975 = register(
		"patch_grass_taiga_2",
		Feature.field_21220.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> field_25976 = register(
		"patch_grass_taiga",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.TAIGA_GRASS_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> field_25977 = register(
		"patch_grass_jungle",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.LUSH_GRASS_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(25)
	);
	public static final ConfiguredFeature<?, ?> field_25978 = register(
		"patch_dead_bush_2",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> field_25979 = register(
		"patch_dead_bush",
		Feature.field_21220.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> field_25980 = register(
		"patch_dead_bush_badlands",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.DEAD_BUSH_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_25981 = register(
		"patch_melon",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.MELON), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.States.GRASS_BLOCK.getBlock()))
					.canReplace()
					.cannotProject()
					.build()
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> field_25982 = register(
		"patch_berry_sparse", field_26022.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> field_25983 = register(
		"patch_berry_decorated", field_26022.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(12)
	);
	public static final ConfiguredFeature<?, ?> field_25984 = register(
		"patch_waterlilly",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LILY_PAD), SimpleBlockPlacer.INSTANCE).tries(10).build()
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> field_25985 = register(
		"patch_tall_grass_2",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.TALL_GRASS_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.HEIGHTMAP)
			.spreadHorizontally()
			.method_23388(Decorator.field_25863.configure(new CountNoiseDecoratorConfig(-0.8, 0, 7)))
	);
	public static final ConfiguredFeature<?, ?> field_25986 = register(
		"patch_tall_grass",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.TALL_GRASS_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> field_25988 = register(
		"patch_large_fern",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LARGE_FERN), new DoublePlantPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> field_25989 = register(
		"patch_cactus",
		Feature.field_21220
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.CACTUS), new ColumnPlacer(1, 2))
					.tries(10)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> field_25990 = register(
		"patch_cactus_desert", field_25989.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_25991 = register(
		"patch_cactus_decorated", field_25989.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).repeat(5)
	);
	public static final ConfiguredFeature<?, ?> field_25992 = register(
		"patch_sugar_cane_swamp",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_25993 = register(
		"patch_sugar_cane_desert",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(60)
	);
	public static final ConfiguredFeature<?, ?> field_25994 = register(
		"patch_sugar_cane_badlands",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(13)
	);
	public static final ConfiguredFeature<?, ?> field_25995 = register(
		"patch_sugar_cane",
		Feature.field_21220
			.configure(ConfiguredFeatures.Configs.SUGAR_CANE_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_25996 = register("brown_mushroom_nether", field_26016.method_30377(128).applyChance(2));
	public static final ConfiguredFeature<?, ?> field_25997 = register("red_mushroom_nether", field_26017.method_30377(128).applyChance(2));
	public static final ConfiguredFeature<?, ?> field_25998 = register(
		"brown_mushroom_normal", field_26016.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(4)
	);
	public static final ConfiguredFeature<?, ?> field_25999 = register(
		"red_mushroom_normal", field_26017.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE).applyChance(8)
	);
	public static final ConfiguredFeature<?, ?> field_26000 = register(
		"brown_mushroom_taiga", field_26016.applyChance(4).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_26001 = register(
		"red_mushroom_taiga", field_26017.applyChance(8).method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP_SPREAD_DOUBLE)
	);
	public static final ConfiguredFeature<?, ?> field_26055 = register("brown_mushroom_giant", field_26000.repeat(3));
	public static final ConfiguredFeature<?, ?> field_26056 = register("red_mushroom_giant", field_26001.repeat(3));
	public static final ConfiguredFeature<?, ?> field_26057 = register("brown_mushroom_swamp", field_26000.repeat(8));
	public static final ConfiguredFeature<?, ?> field_26058 = register("red_mushroom_swamp", field_26001.repeat(8));
	public static final ConfiguredFeature<?, ?> field_26059 = register(
		"ore_magma",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.MAGMA_BLOCK, 33))
			.method_23388(Decorator.field_14244.configure(NopeDecoratorConfig.INSTANCE))
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> field_26060 = register(
		"ore_soul_sand",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.SOUL_SAND, 12))
			.method_30377(32)
			.spreadHorizontally()
			.repeat(12)
	);
	public static final ConfiguredFeature<?, ?> field_26061 = register(
		"ore_gold_deltas",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_GOLD_ORE, 10))
			.method_23388(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_26062 = register(
		"ore_quartz_deltas",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_QUARTZ_ORE, 14))
			.method_23388(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(32)
	);
	public static final ConfiguredFeature<?, ?> field_26063 = register(
		"ore_gold_nether",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_GOLD_ORE, 10))
			.method_23388(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26064 = register(
		"ore_quartz_nether",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_QUARTZ_ORE, 14))
			.method_23388(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> field_26065 = register(
		"ore_gravel_nether",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.GRAVEL, 33))
			.method_23388(Decorator.field_25870.configure(new RangeDecoratorConfig(5, 0, 37)))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> field_26066 = register(
		"ore_blackstone",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.BLACKSTONE, 33))
			.method_23388(Decorator.field_25870.configure(new RangeDecoratorConfig(5, 10, 37)))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> field_26067 = register(
		"ore_dirt",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIRT, 33))
			.method_30377(256)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26068 = register(
		"ore_gravel",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRAVEL, 33))
			.method_30377(256)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> field_26069 = register(
		"ore_granite",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRANITE, 33))
			.method_30377(80)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26070 = register(
		"ore_diorite",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIORITE, 33))
			.method_30377(80)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26071 = register(
		"ore_andesite",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.ANDESITE, 33))
			.method_30377(80)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> field_26072 = register(
		"ore_coal",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.COAL_ORE, 17))
			.method_30377(128)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_26073 = register(
		"ore_iron",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.IRON_ORE, 9))
			.method_30377(64)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_26074 = register(
		"ore_gold_extra",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GOLD_ORE, 9))
			.method_23388(Decorator.field_25870.configure(new RangeDecoratorConfig(32, 32, 80)))
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> field_26075 = register(
		"ore_gold",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GOLD_ORE, 9))
			.method_30377(32)
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> field_26076 = register(
		"ore_redstone",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.REDSTONE_ORE, 8))
			.method_30377(16)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> field_26077 = register(
		"ore_diamond",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIAMOND_ORE, 8))
			.method_30377(16)
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> field_26078 = register(
		"ore_lapis",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.LAPIS_ORE, 7))
			.method_23388(Decorator.field_25873.configure(new DepthAverageDecoratorConfig(16, 16)))
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> field_26079 = register(
		"ore_infested",
		Feature.field_13517
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.INFESTED_STONE, 9))
			.method_30377(64)
			.spreadHorizontally()
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> field_26080 = register(
		"ore_emerald",
		Feature.field_13594
			.configure(new EmeraldOreFeatureConfig(ConfiguredFeatures.States.STONE, ConfiguredFeatures.States.EMERALD_ORE))
			.method_23388(Decorator.field_14268.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> field_26028 = register(
		"ore_debris_large",
		Feature.field_22189
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, ConfiguredFeatures.States.ANCIENT_DEBRIS, 3))
			.method_23388(Decorator.field_25873.configure(new DepthAverageDecoratorConfig(16, 8)))
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> field_26029 = register(
		"ore_debris_small",
		Feature.field_22189
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, ConfiguredFeatures.States.ANCIENT_DEBRIS, 2))
			.method_23388(Decorator.field_25870.configure(new RangeDecoratorConfig(8, 16, 128)))
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> field_26030 = register(
		"crimson_fungi",
		Feature.field_22185.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_NOT_PLANTED_CONFIG).method_23388(Decorator.field_25860.configure(new CountConfig(8)))
	);
	public static final ConfiguredFeature<HugeFungusFeatureConfig, ?> field_26031 = register(
		"crimson_fungi_planted", Feature.field_22185.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> field_26032 = register(
		"warped_fungi",
		Feature.field_22185.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_NOT_PLANTED_CONFIG).method_23388(Decorator.field_25860.configure(new CountConfig(8)))
	);
	public static final ConfiguredFeature<HugeFungusFeatureConfig, ?> field_26033 = register(
		"warped_fungi_planted", Feature.field_22185.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> field_26034 = register(
		"huge_brown_mushroom",
		Feature.field_13531
			.configure(
				new HugeMushroomFeatureConfig(
					new SimpleBlockStateProvider(ConfiguredFeatures.States.BROWN_MUSHROOM_BLOCK), new SimpleBlockStateProvider(ConfiguredFeatures.States.MUSHROOM_STEM), 3
				)
			)
	);
	public static final ConfiguredFeature<?, ?> field_26035 = register(
		"huge_red_mushroom",
		Feature.field_13571
			.configure(
				new HugeMushroomFeatureConfig(
					new SimpleBlockStateProvider(ConfiguredFeatures.States.RED_MUSHROOM_BLOCK), new SimpleBlockStateProvider(ConfiguredFeatures.States.MUSHROOM_STEM), 2
				)
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26036 = register(
		"oak",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(4, 2, 0),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26037 = register(
		"dark_oak",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.DARK_OAK_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.DARK_OAK_LEAVES),
						new DarkOakFoliagePlacer(UniformIntDistribution.of(0), UniformIntDistribution.of(0)),
						new DarkOakTrunkPlacer(6, 2, 1),
						new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
					)
					.maxWaterDepth(Integer.MAX_VALUE)
					.heightmap(Heightmap.Type.field_13197)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26038 = register(
		"birch",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(5, 2, 0),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26039 = register(
		"acacia",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.ACACIA_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.ACACIA_LEAVES),
						new AcaciaFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0)),
						new ForkingTrunkPlacer(5, 2, 2),
						new TwoLayersFeatureSize(1, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26041 = register(
		"spruce",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new SpruceFoliagePlacer(UniformIntDistribution.of(2, 1), UniformIntDistribution.of(0, 2), UniformIntDistribution.of(1, 1)),
						new StraightTrunkPlacer(5, 2, 1),
						new TwoLayersFeatureSize(2, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26042 = register(
		"pine",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new PineFoliagePlacer(UniformIntDistribution.of(1), UniformIntDistribution.of(1), UniformIntDistribution.of(3, 1)),
						new StraightTrunkPlacer(6, 4, 0),
						new TwoLayersFeatureSize(2, 0, 2)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26043 = register(
		"jungle_tree",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(4, 8, 0),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.decorators(ImmutableList.of(new CocoaBeansTreeDecorator(0.2F), TrunkVineTreeDecorator.INSTANCE, LeaveVineTreeDecorator.INSTANCE))
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26044 = register(
		"fancy_oak",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new LargeOakFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(4), 4),
						new LargeOakTrunkPlacer(3, 11, 0),
						new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
					)
					.ignoreVines()
					.heightmap(Heightmap.Type.field_13197)
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26045 = register(
		"jungle_tree_no_vine",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(4, 8, 0),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26046 = register(
		"mega_jungle_tree",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LEAVES),
						new JungleFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 2),
						new MegaJungleTrunkPlacer(10, 2, 19),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(TrunkVineTreeDecorator.INSTANCE, LeaveVineTreeDecorator.INSTANCE))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26047 = register(
		"mega_spruce",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new MegaPineFoliagePlacer(UniformIntDistribution.of(0), UniformIntDistribution.of(0), UniformIntDistribution.of(13, 4)),
						new GiantTrunkPlacer(13, 2, 14),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ConfiguredFeatures.States.PODZOL))))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26048 = register(
		"mega_pine",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new MegaPineFoliagePlacer(UniformIntDistribution.of(0), UniformIntDistribution.of(0), UniformIntDistribution.of(3, 4)),
						new GiantTrunkPlacer(13, 2, 14),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ConfiguredFeatures.States.PODZOL))))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26049 = register(
		"super_birch_bees_0002",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(5, 2, 6),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.decorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES))
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> field_26050 = register(
		"swamp_tree",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(3), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(5, 3, 0),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.maxWaterDepth(1)
					.decorators(ImmutableList.of(LeaveVineTreeDecorator.INSTANCE))
					.build()
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26051 = register(
		"jungle_bush",
		Feature.field_24134
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BushFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(1), 2),
						new StraightTrunkPlacer(1, 0, 0),
						new TwoLayersFeatureSize(0, 0, 0)
					)
					.heightmap(Heightmap.Type.field_13203)
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26052 = register(
		"oak_bees_0002",
		Feature.field_24134.configure(field_26036.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26053 = register(
		"oak_bees_002",
		Feature.field_24134.configure(field_26036.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26054 = register(
		"oak_bees_005", Feature.field_24134.configure(field_26036.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26092 = register(
		"birch_bees_0002",
		Feature.field_24134.configure(field_26038.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26093 = register(
		"birch_bees_002",
		Feature.field_24134.configure(field_26038.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26094 = register(
		"birch_bees_005",
		Feature.field_24134.configure(field_26038.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26095 = register(
		"fancy_oak_bees_0002",
		Feature.field_24134.configure(field_26044.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.VERY_RARE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26096 = register(
		"fancy_oak_bees_002",
		Feature.field_24134.configure(field_26044.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.REGULAR_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> field_26097 = register(
		"fancy_oak_bees_005",
		Feature.field_24134.configure(field_26044.getConfig().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.Decorators.MORE_BEEHIVES_TREES)))
	);
	public static final ConfiguredFeature<?, ?> field_26098 = register(
		"oak_badlands",
		field_26036.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(5, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26099 = register(
		"spruce_snowy",
		field_26041.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26100 = register(
		"flower_warm",
		Feature.FLOWER
			.configure(ConfiguredFeatures.Configs.DEFAULT_FLOWER_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> field_26101 = register(
		"flower_default",
		Feature.FLOWER
			.configure(ConfiguredFeatures.Configs.DEFAULT_FLOWER_CONFIG)
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> field_26102 = register(
		"flower_forest",
		Feature.FLOWER
			.configure(new RandomPatchFeatureConfig.Builder(ForestFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE).tries(64).build())
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(100)
	);
	public static final ConfiguredFeature<?, ?> field_26103 = register(
		"flower_swamp",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.BLUE_ORCHID), SimpleBlockPlacer.INSTANCE).tries(64).build()
			)
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);
	public static final ConfiguredFeature<?, ?> field_26104 = register(
		"flower_plain",
		Feature.FLOWER.configure(new RandomPatchFeatureConfig.Builder(PlainsFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE).tries(64).build())
	);
	public static final ConfiguredFeature<?, ?> field_26105 = register(
		"flower_plain_decorated",
		field_26104.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.HEIGHTMAP)
			.spreadHorizontally()
			.method_23388(Decorator.field_25863.configure(new CountNoiseDecoratorConfig(-0.8, 15, 4)))
	);
	private static final ImmutableList<Supplier<ConfiguredFeature<?, ?>>> FOREST_FLOWER_VEGETATION_CONFIGS = ImmutableList.of(
		() -> Feature.field_21220
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LILAC), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.field_21220
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.ROSE_BUSH), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.field_21220
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.PEONY), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.field_26361
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.LILY_OF_THE_VALLEY), SimpleBlockPlacer.INSTANCE)
						.tries(64)
						.build()
				)
	);
	public static final ConfiguredFeature<?, ?> field_26106 = register(
		"forest_flower_vegetation_common",
		Feature.field_13555
			.configure(new SimpleRandomFeatureConfig(FOREST_FLOWER_VEGETATION_CONFIGS))
			.repeat(UniformIntDistribution.of(-1, 4))
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(5)
	);
	public static final ConfiguredFeature<?, ?> field_26107 = register(
		"forest_flower_vegetation",
		Feature.field_13555
			.configure(new SimpleRandomFeatureConfig(FOREST_FLOWER_VEGETATION_CONFIGS))
			.repeat(UniformIntDistribution.of(-3, 4))
			.method_23388(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(5)
	);
	public static final ConfiguredFeature<?, ?> field_26108 = register(
		"dark_forest_vegetation_brown",
		Feature.field_13593
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(
						field_26034.withChance(0.025F),
						field_26035.withChance(0.05F),
						field_26037.withChance(0.6666667F),
						field_26038.withChance(0.2F),
						field_26044.withChance(0.1F)
					),
					field_26036
				)
			)
			.method_23388(Decorator.field_14239.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> field_26109 = register(
		"dark_forest_vegetation_red",
		Feature.field_13593
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(
						field_26035.withChance(0.025F),
						field_26034.withChance(0.05F),
						field_26037.withChance(0.6666667F),
						field_26038.withChance(0.2F),
						field_26044.withChance(0.1F)
					),
					field_26036
				)
			)
			.method_23388(Decorator.field_14239.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> field_26110 = register(
		"warm_ocean_vegetation",
		Feature.field_13555
			.configure(
				new SimpleRandomFeatureConfig(
					ImmutableList.of(
						() -> Feature.field_13525.configure(FeatureConfig.DEFAULT),
						() -> Feature.field_13546.configure(FeatureConfig.DEFAULT),
						() -> Feature.field_13585.configure(FeatureConfig.DEFAULT)
					)
				)
			)
			.method_23388(ConfiguredFeatures.Decorators.TOP_SOLID_HEIGHTMAP)
			.spreadHorizontally()
			.method_23388(Decorator.field_25864.configure(new CountNoiseBiasedDecoratorConfig(20, 400.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> field_26111 = register(
		"forest_flower_trees",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26093.withChance(0.2F), field_26096.withChance(0.1F)), field_26053))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(6, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26112 = register(
		"taiga_vegetation",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26042.withChance(0.33333334F)), field_26041))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26113 = register(
		"trees_shattered_savanna",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26039.withChance(0.8F)), field_26036))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26114 = register(
		"trees_savanna",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26039.withChance(0.8F)), field_26036))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(1, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26115 = register(
		"birch_tall",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26049.withChance(0.5F)), field_26092))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26376 = register(
		"trees_birch",
		field_26092.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26116 = register(
		"trees_mountain_edge",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26041.withChance(0.666F), field_26044.withChance(0.1F)), field_26036))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(3, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26117 = register(
		"trees_mountain",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26041.withChance(0.666F), field_26044.withChance(0.1F)), field_26036))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26081 = register(
		"trees_water",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26044.withChance(0.1F)), field_26036))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26082 = register(
		"birch_other",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26092.withChance(0.2F), field_26095.withChance(0.1F)), field_26052))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26083 = register(
		"plain_vegetation",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26097.withChance(0.33333334F)), field_26054))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(0, 0.05F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26084 = register(
		"trees_jungle_edge",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26044.withChance(0.1F), field_26051.withChance(0.5F)), field_26043))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26085 = register(
		"trees_giant_spruce",
		Feature.field_13593
			.configure(new RandomFeatureConfig(ImmutableList.of(field_26047.withChance(0.33333334F), field_26042.withChance(0.33333334F)), field_26041))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26086 = register(
		"trees_giant",
		Feature.field_13593
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(field_26047.withChance(0.025641026F), field_26048.withChance(0.30769232F), field_26042.withChance(0.33333334F)), field_26041
				)
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26087 = register(
		"trees_jungle",
		Feature.field_13593
			.configure(
				new RandomFeatureConfig(ImmutableList.of(field_26044.withChance(0.1F), field_26051.withChance(0.5F), field_26046.withChance(0.33333334F)), field_26043)
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(50, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26088 = register(
		"bamboo_vegetation",
		Feature.field_13593
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(field_26044.withChance(0.05F), field_26051.withChance(0.15F), field_26046.withChance(0.7F)),
					Feature.field_21220.configure(ConfiguredFeatures.Configs.LUSH_GRASS_CONFIG)
				)
			)
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.method_23388(Decorator.field_25865.configure(new CountExtraDecoratorConfig(30, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> field_26089 = register(
		"mushroom_field_vegetation",
		Feature.field_13550
			.configure(new RandomBooleanFeatureConfig(() -> field_26035, () -> field_26034))
			.method_23388(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
	);

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
				new WeightedBlockStateProvider().addState(ConfiguredFeatures.States.GRASS, 1).addState(ConfiguredFeatures.States.FERN, 4), SimpleBlockPlacer.INSTANCE
			)
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig LUSH_GRASS_CONFIG = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider().addState(ConfiguredFeatures.States.GRASS, 3).addState(ConfiguredFeatures.States.FERN, 1), SimpleBlockPlacer.INSTANCE
			)
			.blacklist(ImmutableSet.of(ConfiguredFeatures.States.PODZOL))
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig DEFAULT_FLOWER_CONFIG = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider().addState(ConfiguredFeatures.States.POPPY, 2).addState(ConfiguredFeatures.States.DANDELION, 1), SimpleBlockPlacer.INSTANCE
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
				new SimpleBlockStateProvider(ConfiguredFeatures.States.SUGAR_CANE), new ColumnPlacer(2, 2)
			)
			.tries(20)
			.spreadX(4)
			.spreadY(0)
			.spreadZ(4)
			.cannotProject()
			.needsWater()
			.build();
		public static final SpringFeatureConfig LAVA_SPRING_CONFIG = new SpringFeatureConfig(
			ConfiguredFeatures.States.LAVA_FLUID, true, 4, 1, ImmutableSet.of(Blocks.field_10340, Blocks.field_10474, Blocks.field_10508, Blocks.field_10115)
		);
		public static final SpringFeatureConfig ENCLOSED_NETHER_SPRING_CONFIG = new SpringFeatureConfig(
			ConfiguredFeatures.States.LAVA_FLUID, false, 5, 0, ImmutableSet.of(Blocks.field_10515)
		);
		public static final BlockPileFeatureConfig CRIMSON_ROOTS_CONFIG = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider()
				.addState(ConfiguredFeatures.States.CRIMSON_ROOTS, 87)
				.addState(ConfiguredFeatures.States.CRIMSON_FUNGUS, 11)
				.addState(ConfiguredFeatures.States.WARPED_FUNGUS, 1)
		);
		public static final BlockPileFeatureConfig WARPED_ROOTS_CONFIG = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider()
				.addState(ConfiguredFeatures.States.WARPED_ROOTS, 85)
				.addState(ConfiguredFeatures.States.CRIMSON_ROOTS, 1)
				.addState(ConfiguredFeatures.States.WARPED_FUNGUS, 13)
				.addState(ConfiguredFeatures.States.CRIMSON_FUNGUS, 1)
		);
		public static final BlockPileFeatureConfig NETHER_SPROUTS_CONFIG = new BlockPileFeatureConfig(
			new SimpleBlockStateProvider(ConfiguredFeatures.States.NETHER_SPROUTS)
		);
	}

	public static final class Decorators {
		public static final BeehiveTreeDecorator VERY_RARE_BEEHIVES_TREES = new BeehiveTreeDecorator(0.002F);
		public static final BeehiveTreeDecorator REGULAR_BEEHIVES_TREES = new BeehiveTreeDecorator(0.02F);
		public static final BeehiveTreeDecorator MORE_BEEHIVES_TREES = new BeehiveTreeDecorator(0.05F);
		public static final ConfiguredDecorator<CountConfig> FIRE = Decorator.field_14235.configure(new CountConfig(10));
		public static final ConfiguredDecorator<NopeDecoratorConfig> HEIGHTMAP = Decorator.field_25867.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = Decorator.field_14231.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> HEIGHTMAP_WORLD_SURFACE = Decorator.field_25869.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.field_25868.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<RangeDecoratorConfig> NETHER_ORE = Decorator.field_25870.configure(new RangeDecoratorConfig(10, 20, 128));
		public static final ConfiguredDecorator<RangeDecoratorConfig> NETHER_SPRING = Decorator.field_25870.configure(new RangeDecoratorConfig(4, 8, 128));
		public static final ConfiguredDecorator<?> SPREAD_32_ABOVE = Decorator.field_25874.configure(NopeDecoratorConfig.INSTANCE);
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP = HEIGHTMAP.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_SPREAD_DOUBLE = HEIGHTMAP_SPREAD_DOUBLE.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_TOP_SOLID_HEIGHTMAP = TOP_SOLID_HEIGHTMAP.spreadHorizontally();
	}

	public static final class States {
		protected static final BlockState GRASS = Blocks.field_10479.getDefaultState();
		protected static final BlockState FERN = Blocks.field_10112.getDefaultState();
		protected static final BlockState PODZOL = Blocks.field_10520.getDefaultState();
		protected static final BlockState COARSE_DIRT = Blocks.field_10253.getDefaultState();
		protected static final BlockState MYCELIUM = Blocks.field_10402.getDefaultState();
		protected static final BlockState SNOW_BLOCK = Blocks.field_10491.getDefaultState();
		protected static final BlockState ICE = Blocks.field_10295.getDefaultState();
		protected static final BlockState OAK_LOG = Blocks.field_10431.getDefaultState();
		protected static final BlockState OAK_LEAVES = Blocks.field_10503.getDefaultState();
		protected static final BlockState JUNGLE_LOG = Blocks.field_10306.getDefaultState();
		protected static final BlockState JUNGLE_LEAVES = Blocks.field_10335.getDefaultState();
		protected static final BlockState SPRUCE_LOG = Blocks.field_10037.getDefaultState();
		protected static final BlockState SPRUCE_LEAVES = Blocks.field_9988.getDefaultState();
		protected static final BlockState ACACIA_LOG = Blocks.field_10533.getDefaultState();
		protected static final BlockState ACACIA_LEAVES = Blocks.field_10098.getDefaultState();
		protected static final BlockState BIRCH_LOG = Blocks.field_10511.getDefaultState();
		protected static final BlockState BIRCH_LEAVES = Blocks.field_10539.getDefaultState();
		protected static final BlockState DARK_OAK_LOG = Blocks.field_10010.getDefaultState();
		protected static final BlockState DARK_OAK_LEAVES = Blocks.field_10035.getDefaultState();
		protected static final BlockState GRASS_BLOCK = Blocks.field_10219.getDefaultState();
		protected static final BlockState LARGE_FERN = Blocks.field_10313.getDefaultState();
		protected static final BlockState TALL_GRASS = Blocks.field_10214.getDefaultState();
		protected static final BlockState LILAC = Blocks.field_10378.getDefaultState();
		protected static final BlockState ROSE_BUSH = Blocks.field_10430.getDefaultState();
		protected static final BlockState PEONY = Blocks.field_10003.getDefaultState();
		protected static final BlockState BROWN_MUSHROOM = Blocks.field_10251.getDefaultState();
		protected static final BlockState RED_MUSHROOM = Blocks.field_10559.getDefaultState();
		protected static final BlockState PACKED_ICE = Blocks.field_10225.getDefaultState();
		protected static final BlockState BLUE_ICE = Blocks.field_10384.getDefaultState();
		protected static final BlockState LILY_OF_THE_VALLEY = Blocks.field_10548.getDefaultState();
		protected static final BlockState BLUE_ORCHID = Blocks.field_10086.getDefaultState();
		protected static final BlockState POPPY = Blocks.field_10449.getDefaultState();
		protected static final BlockState DANDELION = Blocks.field_10182.getDefaultState();
		protected static final BlockState DEAD_BUSH = Blocks.field_10428.getDefaultState();
		protected static final BlockState MELON = Blocks.field_10545.getDefaultState();
		protected static final BlockState PUMPKIN = Blocks.field_10261.getDefaultState();
		protected static final BlockState SWEET_BERRY_BUSH = Blocks.field_16999.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3));
		protected static final BlockState FIRE = Blocks.field_10036.getDefaultState();
		protected static final BlockState SOUL_FIRE = Blocks.field_22089.getDefaultState();
		protected static final BlockState NETHERRACK = Blocks.field_10515.getDefaultState();
		protected static final BlockState SOUL_SOIL = Blocks.field_22090.getDefaultState();
		protected static final BlockState CRIMSON_ROOTS = Blocks.field_22125.getDefaultState();
		protected static final BlockState LILY_PAD = Blocks.field_10588.getDefaultState();
		protected static final BlockState SNOW = Blocks.field_10477.getDefaultState();
		protected static final BlockState JACK_O_LANTERN = Blocks.field_10009.getDefaultState();
		protected static final BlockState SUNFLOWER = Blocks.field_10583.getDefaultState();
		protected static final BlockState CACTUS = Blocks.field_10029.getDefaultState();
		protected static final BlockState SUGAR_CANE = Blocks.field_10424.getDefaultState();
		protected static final BlockState RED_MUSHROOM_BLOCK = Blocks.field_10240.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final BlockState BROWN_MUSHROOM_BLOCK = Blocks.field_10580
			.getDefaultState()
			.with(MushroomBlock.UP, Boolean.valueOf(true))
			.with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final BlockState MUSHROOM_STEM = Blocks.field_10556
			.getDefaultState()
			.with(MushroomBlock.UP, Boolean.valueOf(false))
			.with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final FluidState WATER_FLUID = Fluids.WATER.getDefaultState();
		protected static final FluidState LAVA_FLUID = Fluids.LAVA.getDefaultState();
		protected static final BlockState WATER_BLOCK = Blocks.field_10382.getDefaultState();
		protected static final BlockState LAVA_BLOCK = Blocks.field_10164.getDefaultState();
		protected static final BlockState DIRT = Blocks.field_10566.getDefaultState();
		protected static final BlockState GRAVEL = Blocks.field_10255.getDefaultState();
		protected static final BlockState GRANITE = Blocks.field_10474.getDefaultState();
		protected static final BlockState DIORITE = Blocks.field_10508.getDefaultState();
		protected static final BlockState ANDESITE = Blocks.field_10115.getDefaultState();
		protected static final BlockState COAL_ORE = Blocks.field_10418.getDefaultState();
		protected static final BlockState IRON_ORE = Blocks.field_10212.getDefaultState();
		protected static final BlockState GOLD_ORE = Blocks.field_10571.getDefaultState();
		protected static final BlockState REDSTONE_ORE = Blocks.field_10080.getDefaultState();
		protected static final BlockState DIAMOND_ORE = Blocks.field_10442.getDefaultState();
		protected static final BlockState LAPIS_ORE = Blocks.field_10090.getDefaultState();
		protected static final BlockState STONE = Blocks.field_10340.getDefaultState();
		protected static final BlockState EMERALD_ORE = Blocks.field_10013.getDefaultState();
		protected static final BlockState INFESTED_STONE = Blocks.field_10277.getDefaultState();
		protected static final BlockState SAND = Blocks.field_10102.getDefaultState();
		protected static final BlockState CLAY = Blocks.field_10460.getDefaultState();
		protected static final BlockState MOSSY_COBBLESTONE = Blocks.field_9989.getDefaultState();
		protected static final BlockState SEAGRASS = Blocks.field_10376.getDefaultState();
		protected static final BlockState MAGMA_BLOCK = Blocks.field_10092.getDefaultState();
		protected static final BlockState SOUL_SAND = Blocks.field_10114.getDefaultState();
		protected static final BlockState NETHER_GOLD_ORE = Blocks.field_23077.getDefaultState();
		protected static final BlockState NETHER_QUARTZ_ORE = Blocks.field_10213.getDefaultState();
		protected static final BlockState BLACKSTONE = Blocks.field_23869.getDefaultState();
		protected static final BlockState ANCIENT_DEBRIS = Blocks.field_22109.getDefaultState();
		protected static final BlockState BASALT = Blocks.field_22091.getDefaultState();
		protected static final BlockState CRIMSON_FUNGUS = Blocks.field_22121.getDefaultState();
		protected static final BlockState WARPED_FUNGUS = Blocks.field_22114.getDefaultState();
		protected static final BlockState WARPED_ROOTS = Blocks.field_22116.getDefaultState();
		protected static final BlockState NETHER_SPROUTS = Blocks.field_22117.getDefaultState();
	}
}
