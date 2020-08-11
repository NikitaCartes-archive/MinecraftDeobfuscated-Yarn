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
import net.minecraft.world.gen.foliage.JungleFoliagePlacer;
import net.minecraft.world.gen.foliage.LargeOakFoliagePlacer;
import net.minecraft.world.gen.foliage.MegaPineFoliagePlacer;
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
	public static final ConfiguredFeature<?, ?> END_SPIKE = register(
		"end_spike", Feature.END_SPIKE.configure(new EndSpikeFeatureConfig(false, ImmutableList.of(), null))
	);
	public static final ConfiguredFeature<?, ?> END_GATEWAY = register(
		"end_gateway",
		Feature.END_GATEWAY
			.configure(EndGatewayFeatureConfig.createConfig(ServerWorld.END_SPAWN_POS, true))
			.decorate(Decorator.END_GATEWAY.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> END_GATEWAY_DELAYED = register(
		"end_gateway_delayed", Feature.END_GATEWAY.configure(EndGatewayFeatureConfig.createConfig())
	);
	public static final ConfiguredFeature<?, ?> CHORUS_PLANT = register(
		"chorus_plant", Feature.CHORUS_PLANT.configure(FeatureConfig.DEFAULT).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).repeatRandomly(4)
	);
	public static final ConfiguredFeature<?, ?> END_ISLAND = register("end_island", Feature.END_ISLAND.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> END_ISLAND_DECORATED = register(
		"end_island_decorated", END_ISLAND.decorate(Decorator.END_ISLAND.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> DELTA = register(
		"delta",
		Feature.DELTA_FEATURE
			.configure(
				new DeltaFeatureConfig(
					ConfiguredFeatures.States.LAVA_BLOCK, ConfiguredFeatures.States.MAGMA_BLOCK, UniformIntDistribution.of(3, 4), UniformIntDistribution.of(0, 2)
				)
			)
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(40)))
	);
	public static final ConfiguredFeature<?, ?> SMALL_BASALT_COLUMNS = register(
		"small_basalt_columns",
		Feature.BASALT_COLUMNS
			.configure(new BasaltColumnsFeatureConfig(UniformIntDistribution.of(1), UniformIntDistribution.of(1, 3)))
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> LARGE_BASALT_COLUMNS = register(
		"large_basalt_columns",
		Feature.BASALT_COLUMNS
			.configure(new BasaltColumnsFeatureConfig(UniformIntDistribution.of(2, 1), UniformIntDistribution.of(5, 5)))
			.decorate(Decorator.COUNT_MULTILAYER.configure(new CountConfig(2)))
	);
	public static final ConfiguredFeature<?, ?> BASALT_BLOBS = register(
		"basalt_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS
			.configure(new NetherrackReplaceBlobsFeatureConfig(ConfiguredFeatures.States.NETHERRACK, ConfiguredFeatures.States.BASALT, UniformIntDistribution.of(3, 4)))
			.method_30377(128)
			.spreadHorizontally()
			.repeat(75)
	);
	public static final ConfiguredFeature<?, ?> BLACKSTONE_BLOBS = register(
		"blackstone_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS
			.configure(
				new NetherrackReplaceBlobsFeatureConfig(ConfiguredFeatures.States.NETHERRACK, ConfiguredFeatures.States.BLACKSTONE, UniformIntDistribution.of(3, 4))
			)
			.method_30377(128)
			.spreadHorizontally()
			.repeat(25)
	);
	public static final ConfiguredFeature<?, ?> GLOWSTONE_EXTRA = register(
		"glowstone_extra", Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).decorate(Decorator.GLOWSTONE.configure(new CountConfig(10)))
	);
	public static final ConfiguredFeature<?, ?> GLOWSTONE = register(
		"glowstone", Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
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
		"twisting_vines", Feature.TWISTING_VINES.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> WEEPING_VINES = register(
		"weeping_vines", Feature.WEEPING_VINES.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
	);
	public static final ConfiguredFeature<?, ?> BASALT_PILLAR = register(
		"basalt_pillar", Feature.BASALT_PILLAR.configure(FeatureConfig.DEFAULT).method_30377(128).spreadHorizontally().repeat(10)
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
					ConfiguredFeatures.States.SEAGRASS,
					ImmutableList.of(ConfiguredFeatures.States.STONE),
					ImmutableList.of(ConfiguredFeatures.States.WATER_BLOCK),
					ImmutableList.of(ConfiguredFeatures.States.WATER_BLOCK)
				)
			)
			.decorate(Decorator.CARVING_MASK.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1F)))
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
		"blue_ice",
		Feature.BLUE_ICE
			.configure(FeatureConfig.DEFAULT)
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(30, 32, 64)))
			.spreadHorizontally()
			.repeatRandomly(19)
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
	public static final ConfiguredFeature<?, ?> LAKE_WATER = register(
		"lake_water",
		Feature.LAKE
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.WATER_BLOCK))
			.decorate(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> LAKE_LAVA = register(
		"lake_lava",
		Feature.LAKE
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.States.LAVA_BLOCK))
			.decorate(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)))
	);
	public static final ConfiguredFeature<?, ?> DISK_CLAY = register(
		"disk_clay",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.States.CLAY, UniformIntDistribution.of(2, 1), 1, ImmutableList.of(ConfiguredFeatures.States.DIRT, ConfiguredFeatures.States.CLAY)
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
					UniformIntDistribution.of(2, 3),
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
					UniformIntDistribution.of(2, 4),
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
		"monster_room", Feature.MONSTER_ROOM.configure(FeatureConfig.DEFAULT).method_30377(256).spreadHorizontally().repeat(8)
	);
	public static final ConfiguredFeature<?, ?> DESERT_WELL = register(
		"desert_well", Feature.DESERT_WELL.configure(FeatureConfig.DEFAULT).decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).applyChance(1000)
	);
	public static final ConfiguredFeature<?, ?> FOSSIL = register("fossil", Feature.FOSSIL.configure(FeatureConfig.DEFAULT).applyChance(64));
	public static final ConfiguredFeature<?, ?> SPRING_LAVA_DOUBLE = register(
		"spring_lava_double",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.LAVA_SPRING_CONFIG)
			.decorate(Decorator.RANGE_VERY_BIASED.configure(new RangeDecoratorConfig(8, 16, 256)))
			.spreadHorizontally()
			.repeat(40)
	);
	public static final ConfiguredFeature<?, ?> SPRING_LAVA = register(
		"spring_lava",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.LAVA_SPRING_CONFIG)
			.decorate(Decorator.RANGE_VERY_BIASED.configure(new RangeDecoratorConfig(8, 16, 256)))
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
			.decorate(ConfiguredFeatures.Decorators.NETHER_SPRING)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> SPRING_CLOSED = register(
		"spring_closed",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.ENCLOSED_NETHER_SPRING_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> SPRING_CLOSED_DOUBLE = register(
		"spring_closed_double",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.Configs.ENCLOSED_NETHER_SPRING_CONFIG)
			.decorate(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(32)
	);
	public static final ConfiguredFeature<?, ?> SPRING_OPEN = register(
		"spring_open",
		Feature.SPRING_FEATURE
			.configure(new SpringFeatureConfig(ConfiguredFeatures.States.LAVA_FLUID, false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK)))
			.decorate(ConfiguredFeatures.Decorators.NETHER_SPRING)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> SPRING_WATER = register(
		"spring_water",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(ConfiguredFeatures.States.WATER_FLUID, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE))
			)
			.decorate(Decorator.RANGE_BIASED.configure(new RangeDecoratorConfig(8, 8, 256)))
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
					new WeightedBlockStateProvider().addState(ConfiguredFeatures.States.BLUE_ICE, 1).addState(ConfiguredFeatures.States.PACKED_ICE, 5)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PILE_PUMPKIN = register(
		"pile_pumpkin",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider().addState(ConfiguredFeatures.States.PUMPKIN, 19).addState(ConfiguredFeatures.States.JACK_O_LANTERN, 1)
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
			.method_30377(128)
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
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.States.CACTUS), new ColumnPlacer(1, 2))
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
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NETHER = register("brown_mushroom_nether", PATCH_BROWN_MUSHROOM.method_30377(128).applyChance(2));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NETHER = register("red_mushroom_nether", PATCH_RED_MUSHROOM.method_30377(128).applyChance(2));
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
	public static final ConfiguredFeature<?, ?> ORE_MAGMA = register(
		"ore_magma",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.MAGMA_BLOCK, 33))
			.decorate(Decorator.MAGMA.configure(NopeDecoratorConfig.INSTANCE))
			.spreadHorizontally()
			.repeat(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_SOUL_SAND = register(
		"ore_soul_sand",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.SOUL_SAND, 12))
			.method_30377(32)
			.spreadHorizontally()
			.repeat(12)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_DELTAS = register(
		"ore_gold_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_GOLD_ORE, 10))
			.decorate(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_DELTAS = register(
		"ore_quartz_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_QUARTZ_ORE, 14))
			.decorate(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(32)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_NETHER = register(
		"ore_gold_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_GOLD_ORE, 10))
			.decorate(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_NETHER = register(
		"ore_quartz_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.NETHER_QUARTZ_ORE, 14))
			.decorate(ConfiguredFeatures.Decorators.NETHER_ORE)
			.spreadHorizontally()
			.repeat(16)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL_NETHER = register(
		"ore_gravel_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.GRAVEL, 33))
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(5, 0, 37)))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_BLACKSTONE = register(
		"ore_blackstone",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.NETHERRACK, ConfiguredFeatures.States.BLACKSTONE, 33))
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(5, 10, 37)))
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIRT = register(
		"ore_dirt",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIRT, 33))
			.method_30377(256)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL = register(
		"ore_gravel",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRAVEL, 33))
			.method_30377(256)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRANITE = register(
		"ore_granite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GRANITE, 33))
			.method_30377(80)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIORITE = register(
		"ore_diorite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIORITE, 33))
			.method_30377(80)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_ANDESITE = register(
		"ore_andesite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.ANDESITE, 33))
			.method_30377(80)
			.spreadHorizontally()
			.repeat(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_COAL = register(
		"ore_coal",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.COAL_ORE, 17))
			.method_30377(128)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_IRON = register(
		"ore_iron",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.IRON_ORE, 9))
			.method_30377(64)
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_EXTRA = register(
		"ore_gold_extra",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GOLD_ORE, 9))
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(32, 32, 80)))
			.spreadHorizontally()
			.repeat(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD = register(
		"ore_gold",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.GOLD_ORE, 9))
			.method_30377(32)
			.spreadHorizontally()
			.repeat(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_REDSTONE = register(
		"ore_redstone",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.REDSTONE_ORE, 8))
			.method_30377(16)
			.spreadHorizontally()
			.repeat(8)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND = register(
		"ore_diamond",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.DIAMOND_ORE, 8))
			.method_30377(16)
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> ORE_LAPIS = register(
		"ore_lapis",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.LAPIS_ORE, 7))
			.decorate(Decorator.DEPTH_AVERAGE.configure(new DepthAverageDecoratorConfig(16, 16)))
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> ORE_INFESTED = register(
		"ore_infested",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_OVERWORLD, ConfiguredFeatures.States.INFESTED_STONE, 9))
			.method_30377(64)
			.spreadHorizontally()
			.repeat(7)
	);
	public static final ConfiguredFeature<?, ?> ORE_EMERALD = register(
		"ore_emerald",
		Feature.EMERALD_ORE
			.configure(new EmeraldOreFeatureConfig(ConfiguredFeatures.States.STONE, ConfiguredFeatures.States.EMERALD_ORE))
			.decorate(Decorator.EMERALD_ORE.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_LARGE = register(
		"ore_debris_large",
		Feature.NO_SURFACE_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, ConfiguredFeatures.States.ANCIENT_DEBRIS, 3))
			.decorate(Decorator.DEPTH_AVERAGE.configure(new DepthAverageDecoratorConfig(16, 8)))
			.spreadHorizontally()
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_SMALL = register(
		"ore_debris_small",
		Feature.NO_SURFACE_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.Rules.BASE_STONE_NETHER, ConfiguredFeatures.States.ANCIENT_DEBRIS, 2))
			.decorate(Decorator.RANGE.configure(new RangeDecoratorConfig(8, 16, 128)))
			.spreadHorizontally()
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
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(4, 2, 0),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.States.DARK_OAK_LEAVES),
						new DarkOakFoliagePlacer(UniformIntDistribution.of(0), UniformIntDistribution.of(0)),
						new DarkOakTrunkPlacer(6, 2, 1),
						new ThreeLayersFeatureSize(1, 1, 0, 1, 2, OptionalInt.empty())
					)
					.maxWaterDepth(Integer.MAX_VALUE)
					.heightmap(Heightmap.Type.MOTION_BLOCKING)
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
						new SimpleBlockStateProvider(ConfiguredFeatures.States.BIRCH_LEAVES),
						new BlobFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0), 3),
						new StraightTrunkPlacer(5, 2, 0),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.States.ACACIA_LEAVES),
						new AcaciaFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(0)),
						new ForkingTrunkPlacer(5, 2, 2),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new SpruceFoliagePlacer(UniformIntDistribution.of(2, 1), UniformIntDistribution.of(0, 2), UniformIntDistribution.of(1, 1)),
						new StraightTrunkPlacer(5, 2, 1),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.States.SPRUCE_LEAVES),
						new PineFoliagePlacer(UniformIntDistribution.of(1), UniformIntDistribution.of(1), UniformIntDistribution.of(3, 1)),
						new StraightTrunkPlacer(6, 4, 0),
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
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK = register(
		"fancy_oak",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new LargeOakFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(4), 4),
						new LargeOakTrunkPlacer(3, 11, 0),
						new TwoLayersFeatureSize(0, 0, 0, OptionalInt.of(4))
					)
					.ignoreVines()
					.heightmap(Heightmap.Type.MOTION_BLOCKING)
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> JUNGLE_TREE_NO_VINE = register(
		"jungle_tree_no_vine",
		Feature.TREE
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
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_JUNGLE_TREE = register(
		"mega_jungle_tree",
		Feature.TREE
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
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_SPRUCE = register(
		"mega_spruce",
		Feature.TREE
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
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_PINE = register(
		"mega_pine",
		Feature.TREE
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
	public static final ConfiguredFeature<TreeFeatureConfig, ?> SUPER_BIRCH_BEES_0002 = register(
		"super_birch_bees_0002",
		Feature.TREE
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
	public static final ConfiguredFeature<?, ?> SWAMP_TREE = register(
		"swamp_tree",
		Feature.TREE
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
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> JUNGLE_BUSH = register(
		"jungle_bush",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.States.JUNGLE_LOG),
						new SimpleBlockStateProvider(ConfiguredFeatures.States.OAK_LEAVES),
						new BushFoliagePlacer(UniformIntDistribution.of(2), UniformIntDistribution.of(1), 2),
						new StraightTrunkPlacer(1, 0, 0),
						new TwoLayersFeatureSize(0, 0, 0)
					)
					.heightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)
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
	public static final ConfiguredFeature<?, ?> OAK_BADLANDS = register(
		"oak_badlands",
		OAK.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(5, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> SPRUCE_SNOWY = register(
		"spruce_snowy",
		SPRUCE.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP).decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
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
		() -> Feature.field_26361
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
			.repeat(UniformIntDistribution.of(-1, 4))
			.decorate(ConfiguredFeatures.Decorators.SPREAD_32_ABOVE)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.repeat(5)
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION = register(
		"forest_flower_vegetation",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(new SimpleRandomFeatureConfig(FOREST_FLOWER_VEGETATION_CONFIGS))
			.repeat(UniformIntDistribution.of(-3, 4))
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
			.decorate(Decorator.DARK_OAK_TREE.configure(DecoratorConfig.DEFAULT))
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
			.decorate(Decorator.DARK_OAK_TREE.configure(DecoratorConfig.DEFAULT))
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
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(6, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TAIGA_VEGETATION = register(
		"taiga_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(PINE.withChance(0.33333334F)), SPRUCE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SHATTERED_SAVANNA = register(
		"trees_shattered_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA.withChance(0.8F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SAVANNA = register(
		"trees_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA.withChance(0.8F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(1, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BIRCH_TALL = register(
		"birch_tall",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SUPER_BIRCH_BEES_0002.withChance(0.5F)), BIRCH_BEES_0002))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_BIRCH = register(
		"trees_birch",
		BIRCH_BEES_0002.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN_EDGE = register(
		"trees_mountain_edge",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE.withChance(0.666F), FANCY_OAK.withChance(0.1F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(3, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN = register(
		"trees_mountain",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE.withChance(0.666F), FANCY_OAK.withChance(0.1F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_WATER = register(
		"trees_water",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F)), OAK))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BIRCH_OTHER = register(
		"birch_other",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(BIRCH_BEES_0002.withChance(0.2F), FANCY_OAK_BEES_0002.withChance(0.1F)), OAK_BEES_0002))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> PLAIN_VEGETATION = register(
		"plain_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK_BEES_005.withChance(0.33333334F)), OAK_BEES_005))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(0, 0.05F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE_EDGE = register(
		"trees_jungle_edge",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F)), JUNGLE_TREE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT_SPRUCE = register(
		"trees_giant_spruce",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.withChance(0.33333334F), PINE.withChance(0.33333334F)), SPRUCE))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT = register(
		"trees_giant",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.withChance(0.025641026F), MEGA_PINE.withChance(0.30769232F), PINE.withChance(0.33333334F)), SPRUCE)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE = register(
		"trees_jungle",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F), MEGA_JUNGLE_TREE.withChance(0.33333334F)), JUNGLE_TREE)
			)
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
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
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
			.decorate(Decorator.COUNT_EXTRA.configure(new CountExtraDecoratorConfig(30, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> MUSHROOM_FIELD_VEGETATION = register(
		"mushroom_field_vegetation",
		Feature.RANDOM_BOOLEAN_SELECTOR
			.configure(new RandomBooleanFeatureConfig(() -> HUGE_RED_MUSHROOM, () -> HUGE_BROWN_MUSHROOM))
			.decorate(ConfiguredFeatures.Decorators.SQUARE_HEIGHTMAP)
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
			ConfiguredFeatures.States.LAVA_FLUID, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
		);
		public static final SpringFeatureConfig ENCLOSED_NETHER_SPRING_CONFIG = new SpringFeatureConfig(
			ConfiguredFeatures.States.LAVA_FLUID, false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK)
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
		public static final ConfiguredDecorator<CountConfig> FIRE = Decorator.FIRE.configure(new CountConfig(10));
		public static final ConfiguredDecorator<NopeDecoratorConfig> HEIGHTMAP = Decorator.HEIGHTMAP.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> TOP_SOLID_HEIGHTMAP = Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> HEIGHTMAP_WORLD_SURFACE = Decorator.HEIGHTMAP_WORLD_SURFACE.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> HEIGHTMAP_SPREAD_DOUBLE = Decorator.HEIGHTMAP_SPREAD_DOUBLE.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<RangeDecoratorConfig> NETHER_ORE = Decorator.RANGE.configure(new RangeDecoratorConfig(10, 20, 128));
		public static final ConfiguredDecorator<RangeDecoratorConfig> NETHER_SPRING = Decorator.RANGE.configure(new RangeDecoratorConfig(4, 8, 128));
		public static final ConfiguredDecorator<?> SPREAD_32_ABOVE = Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE);
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP = HEIGHTMAP.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_HEIGHTMAP_SPREAD_DOUBLE = HEIGHTMAP_SPREAD_DOUBLE.spreadHorizontally();
		public static final ConfiguredDecorator<?> SQUARE_TOP_SOLID_HEIGHTMAP = TOP_SOLID_HEIGHTMAP.spreadHorizontally();
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
		protected static final BlockState IRON_ORE = Blocks.IRON_ORE.getDefaultState();
		protected static final BlockState GOLD_ORE = Blocks.GOLD_ORE.getDefaultState();
		protected static final BlockState REDSTONE_ORE = Blocks.REDSTONE_ORE.getDefaultState();
		protected static final BlockState DIAMOND_ORE = Blocks.DIAMOND_ORE.getDefaultState();
		protected static final BlockState LAPIS_ORE = Blocks.LAPIS_ORE.getDefaultState();
		protected static final BlockState STONE = Blocks.STONE.getDefaultState();
		protected static final BlockState EMERALD_ORE = Blocks.EMERALD_ORE.getDefaultState();
		protected static final BlockState INFESTED_STONE = Blocks.INFESTED_STONE.getDefaultState();
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
	}
}
