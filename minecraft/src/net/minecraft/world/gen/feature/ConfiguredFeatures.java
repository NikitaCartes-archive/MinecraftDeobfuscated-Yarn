package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import java.util.OptionalInt;
import java.util.function.Supplier;
import net.minecraft.class_5428;
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
import net.minecraft.world.gen.decorator.AlterGroundTreeDecorator;
import net.minecraft.world.gen.decorator.BeehiveTreeDecorator;
import net.minecraft.world.gen.decorator.CarvingMaskDecoratorConfig;
import net.minecraft.world.gen.decorator.ChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.CocoaBeansTreeDecorator;
import net.minecraft.world.gen.decorator.ConfiguredDecorator;
import net.minecraft.world.gen.decorator.CountDepthDecoratorConfig;
import net.minecraft.world.gen.decorator.CountExtraChanceDecoratorConfig;
import net.minecraft.world.gen.decorator.Decorator;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.decorator.LeaveVineTreeDecorator;
import net.minecraft.world.gen.decorator.NoiseHeightmapDecoratorConfig;
import net.minecraft.world.gen.decorator.NopeDecoratorConfig;
import net.minecraft.world.gen.decorator.RangeDecoratorConfig;
import net.minecraft.world.gen.decorator.TopSolidHeightmapNoiseBiasedDecoratorConfig;
import net.minecraft.world.gen.decorator.TrunkVineTreeDecorator;
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
import net.minecraft.world.gen.trunk.DarkOakTrunkPlacer;
import net.minecraft.world.gen.trunk.ForkingTrunkPlacer;
import net.minecraft.world.gen.trunk.GiantTrunkPlacer;
import net.minecraft.world.gen.trunk.LargeOakTrunkPlacer;
import net.minecraft.world.gen.trunk.MegaJungleTrunkPlacer;
import net.minecraft.world.gen.trunk.StraightTrunkPlacer;

public class ConfiguredFeatures {
	public static final ConfiguredFeature<?, ?> NOPE = register("nope", Feature.NO_OP.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> END_SPIKE = register(
		"end_spike", Feature.END_SPIKE.configure(new EndSpikeFeatureConfig(false, ImmutableList.of(), null))
	);
	public static final ConfiguredFeature<?, ?> END_GATEWAY = register(
		"end_gateway",
		Feature.END_GATEWAY
			.configure(EndGatewayFeatureConfig.createConfig(ServerWorld.END_SPAWN_POS, true))
			.method_30374(Decorator.END_GATEWAY.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> END_GATEWAY_DELAYED = register(
		"end_gateway_delayed", Feature.END_GATEWAY.configure(EndGatewayFeatureConfig.createConfig())
	);
	public static final ConfiguredFeature<?, ?> CHORUS_PLANT = register(
		"chorus_plant", Feature.CHORUS_PLANT.configure(FeatureConfig.DEFAULT).method_30374(ConfiguredFeatures.class_5466.field_26165).method_30376(4)
	);
	public static final ConfiguredFeature<?, ?> END_ISLAND = register("end_island", Feature.END_ISLAND.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> END_ISLAND_DECORATED = register(
		"end_island_decorated", END_ISLAND.method_30374(Decorator.END_ISLAND.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> DELTA = register(
		"delta",
		Feature.DELTA_FEATURE
			.configure(
				new DeltaFeatureConfig(
					ConfiguredFeatures.class_5467.field_26203, ConfiguredFeatures.class_5467.field_26222, class_5428.method_30315(3, 4), class_5428.method_30315(0, 2)
				)
			)
			.method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(40)))
	);
	public static final ConfiguredFeature<?, ?> SMALL_BASALT_COLUMNS = register(
		"small_basalt_columns",
		Feature.BASALT_COLUMNS
			.configure(new BasaltColumnsFeatureConfig(class_5428.method_30314(1), class_5428.method_30315(1, 3)))
			.method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> LARGE_BASALT_COLUMNS = register(
		"large_basalt_columns",
		Feature.BASALT_COLUMNS
			.configure(new BasaltColumnsFeatureConfig(class_5428.method_30315(2, 1), class_5428.method_30315(5, 5)))
			.method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(2)))
	);
	public static final ConfiguredFeature<?, ?> BASALT_BLOBS = register(
		"basalt_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS
			.configure(
				new NetherrackReplaceBlobsFeatureConfig(ConfiguredFeatures.class_5467.field_26181, ConfiguredFeatures.class_5467.field_26195, class_5428.method_30315(3, 4))
			)
			.method_30377(128)
			.method_30371()
			.method_30375(75)
	);
	public static final ConfiguredFeature<?, ?> BLACKSTONE_BLOBS = register(
		"blackstone_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS
			.configure(
				new NetherrackReplaceBlobsFeatureConfig(ConfiguredFeatures.class_5467.field_26181, ConfiguredFeatures.class_5467.field_26226, class_5428.method_30315(3, 4))
			)
			.method_30377(128)
			.method_30371()
			.method_30375(25)
	);
	public static final ConfiguredFeature<?, ?> GLOWSTONE_EXTRA = register(
		"glowstone_extra", Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).method_30374(Decorator.GLOWSTONE.configure(new CountConfig(10)))
	);
	public static final ConfiguredFeature<?, ?> GLOWSTONE = register(
		"glowstone", Feature.GLOWSTONE_BLOB.configure(FeatureConfig.DEFAULT).method_30377(128).method_30371().method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> CRIMSON_FOREST_VEGETATION = register(
		"crimson_forest_vegetation",
		Feature.NETHER_FOREST_VEGETATION.configure(ConfiguredFeatures.class_5465.field_26151).method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(6)))
	);
	public static final ConfiguredFeature<?, ?> WARPED_FOREST_VEGETATION = register(
		"warped_forest_vegetation",
		Feature.NETHER_FOREST_VEGETATION.configure(ConfiguredFeatures.class_5465.field_26152).method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(5)))
	);
	public static final ConfiguredFeature<?, ?> NETHER_SPROUTS = register(
		"nether_sprouts",
		Feature.NETHER_FOREST_VEGETATION.configure(ConfiguredFeatures.class_5465.field_26153).method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> TWISTING_VINES = register(
		"twisting_vines", Feature.TWISTING_VINES.configure(FeatureConfig.DEFAULT).method_30377(128).method_30371().method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> WEEPING_VINES = register(
		"weeping_vines", Feature.WEEPING_VINES.configure(FeatureConfig.DEFAULT).method_30377(128).method_30371().method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> BASALT_PILLAR = register(
		"basalt_pillar", Feature.BASALT_PILLAR.configure(FeatureConfig.DEFAULT).method_30377(128).method_30371().method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_COLD = register(
		"seagrass_cold", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).method_30375(32).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP_COLD = register(
		"seagrass_deep_cold", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).method_30375(40).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_NORMAL = register(
		"seagrass_normal", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).method_30375(48).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_RIVER = register(
		"seagrass_river", Feature.SEAGRASS.configure(new ProbabilityConfig(0.4F)).method_30375(48).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP = register(
		"seagrass_deep", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).method_30375(48).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_SWAMP = register(
		"seagrass_swamp", Feature.SEAGRASS.configure(new ProbabilityConfig(0.6F)).method_30375(64).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_WARM = register(
		"seagrass_warm", Feature.SEAGRASS.configure(new ProbabilityConfig(0.3F)).method_30375(80).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_DEEP_WARM = register(
		"seagrass_deep_warm", Feature.SEAGRASS.configure(new ProbabilityConfig(0.8F)).method_30375(80).method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> SEA_PICKLE = register(
		"sea_pickle", Feature.SEA_PICKLE.configure(new CountConfig(20)).method_30374(ConfiguredFeatures.class_5466.field_26167).method_30372(16)
	);
	public static final ConfiguredFeature<?, ?> ICE_SPIKE = register(
		"ice_spike", Feature.ICE_SPIKE.configure(FeatureConfig.DEFAULT).method_30374(ConfiguredFeatures.class_5466.field_26165).method_30375(3)
	);
	public static final ConfiguredFeature<?, ?> ICE_PATCH = register(
		"ice_patch",
		Feature.ICE_PATCH
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.class_5467.field_26169,
					class_5428.method_30315(2, 1),
					1,
					ImmutableList.of(
						ConfiguredFeatures.class_5467.field_26204,
						ConfiguredFeatures.class_5467.field_26245,
						ConfiguredFeatures.class_5467.field_26228,
						ConfiguredFeatures.class_5467.field_26229,
						ConfiguredFeatures.class_5467.field_26230,
						ConfiguredFeatures.class_5467.field_26231,
						ConfiguredFeatures.class_5467.field_26232
					)
				)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(2)
	);
	public static final ConfiguredFeature<?, ?> FOREST_ROCK = register(
		"forest_rock",
		Feature.FOREST_ROCK
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.class_5467.field_26220))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30376(2)
	);
	public static final ConfiguredFeature<?, ?> SEAGRASS_SIMPLE = register(
		"seagrass_simple",
		Feature.SIMPLE_BLOCK
			.configure(
				new SimpleBlockFeatureConfig(
					ConfiguredFeatures.class_5467.field_26221,
					ImmutableList.of(ConfiguredFeatures.class_5467.field_26215),
					ImmutableList.of(ConfiguredFeatures.class_5467.field_26202),
					ImmutableList.of(ConfiguredFeatures.class_5467.field_26202)
				)
			)
			.method_30374(Decorator.CARVING_MASK.configure(new CarvingMaskDecoratorConfig(GenerationStep.Carver.LIQUID, 0.1F)))
	);
	public static final ConfiguredFeature<?, ?> ICEBERG_PACKED = register(
		"iceberg_packed",
		Feature.ICEBERG
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.class_5467.field_26169))
			.method_30374(Decorator.ICEBERG.configure(NopeDecoratorConfig.INSTANCE))
			.method_30372(16)
	);
	public static final ConfiguredFeature<?, ?> ICEBERG_BLUE = register(
		"iceberg_blue",
		Feature.ICEBERG
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.class_5467.field_26170))
			.method_30374(Decorator.ICEBERG.configure(NopeDecoratorConfig.INSTANCE))
			.method_30372(200)
	);
	public static final ConfiguredFeature<?, ?> KELP_COLD = register(
		"kelp_cold",
		Feature.KELP
			.configure(FeatureConfig.DEFAULT)
			.method_30374(ConfiguredFeatures.class_5466.field_26159)
			.method_30371()
			.method_30374(Decorator.COUNT_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(120, 80.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> KELP_WARM = register(
		"kelp_warm",
		Feature.KELP
			.configure(FeatureConfig.DEFAULT)
			.method_30374(ConfiguredFeatures.class_5466.field_26159)
			.method_30371()
			.method_30374(Decorator.COUNT_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(80, 80.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> BLUE_ICE = register(
		"blue_ice",
		Feature.BLUE_ICE
			.configure(FeatureConfig.DEFAULT)
			.method_30374(Decorator.RANGE.configure(new RangeDecoratorConfig(30, 32, 64)))
			.method_30371()
			.method_30376(19)
	);
	public static final ConfiguredFeature<?, ?> BAMBOO_LIGHT = register(
		"bamboo_light", Feature.BAMBOO.configure(new ProbabilityConfig(0.0F)).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(16)
	);
	public static final ConfiguredFeature<?, ?> BAMBOO = register(
		"bamboo",
		Feature.BAMBOO
			.configure(new ProbabilityConfig(0.2F))
			.method_30374(ConfiguredFeatures.class_5466.field_26160)
			.method_30371()
			.method_30374(Decorator.COUNT_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(160, 80.0, 0.3)))
	);
	public static final ConfiguredFeature<?, ?> VINES = register("vines", Feature.VINES.configure(FeatureConfig.DEFAULT).method_30371().method_30375(50));
	public static final ConfiguredFeature<?, ?> LAKE_WATER = register(
		"lake_water",
		Feature.LAKE
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.class_5467.field_26202))
			.method_30374(Decorator.WATER_LAKE.configure(new ChanceDecoratorConfig(4)))
	);
	public static final ConfiguredFeature<?, ?> LAKE_LAVA = register(
		"lake_lava",
		Feature.LAKE
			.configure(new SingleStateFeatureConfig(ConfiguredFeatures.class_5467.field_26203))
			.method_30374(Decorator.LAVA_LAKE.configure(new ChanceDecoratorConfig(80)))
	);
	public static final ConfiguredFeature<?, ?> DISK_CLAY = register(
		"disk_clay",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.class_5467.field_26219,
					class_5428.method_30315(2, 1),
					1,
					ImmutableList.of(ConfiguredFeatures.class_5467.field_26204, ConfiguredFeatures.class_5467.field_26219)
				)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> DISK_GRAVEL = register(
		"disk_gravel",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.class_5467.field_26205,
					class_5428.method_30315(2, 3),
					2,
					ImmutableList.of(ConfiguredFeatures.class_5467.field_26204, ConfiguredFeatures.class_5467.field_26245)
				)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26167)
	);
	public static final ConfiguredFeature<?, ?> DISK_SAND = register(
		"disk_sand",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					ConfiguredFeatures.class_5467.field_26218,
					class_5428.method_30315(2, 4),
					2,
					ImmutableList.of(ConfiguredFeatures.class_5467.field_26204, ConfiguredFeatures.class_5467.field_26245)
				)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26167)
			.method_30375(3)
	);
	public static final ConfiguredFeature<?, ?> FREEZE_TOP_LAYER = register("freeze_top_layer", Feature.FREEZE_TOP_LAYER.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> BONUS_CHEST = register("bonus_chest", Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> VOID_START_PLATFORM = register("void_start_platform", Feature.VOID_START_PLATFORM.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> MONSTER_ROOM = register(
		"monster_room", Feature.MONSTER_ROOM.configure(FeatureConfig.DEFAULT).method_30377(256).method_30371().method_30375(8)
	);
	public static final ConfiguredFeature<?, ?> DESERT_WELL = register(
		"desert_well", Feature.DESERT_WELL.configure(FeatureConfig.DEFAULT).method_30374(ConfiguredFeatures.class_5466.field_26165).method_30372(1000)
	);
	public static final ConfiguredFeature<?, ?> FOSSIL = register("fossil", Feature.FOSSIL.configure(FeatureConfig.DEFAULT).method_30372(64));
	public static final ConfiguredFeature<?, ?> SPRING_LAVA_DOUBLE = register(
		"spring_lava_double",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.class_5465.field_26149)
			.method_30374(Decorator.RANGE_VERY_BIASED.configure(new RangeDecoratorConfig(8, 16, 256)))
			.method_30371()
			.method_30375(40)
	);
	public static final ConfiguredFeature<?, ?> SPRING_LAVA = register(
		"spring_lava",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.class_5465.field_26149)
			.method_30374(Decorator.RANGE_VERY_BIASED.configure(new RangeDecoratorConfig(8, 16, 256)))
			.method_30371()
			.method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> SPRING_DELTA = register(
		"spring_delta",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(
					ConfiguredFeatures.class_5467.field_26201,
					true,
					4,
					1,
					ImmutableSet.of(Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE)
				)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26163)
			.method_30371()
			.method_30375(16)
	);
	public static final ConfiguredFeature<?, ?> SPRING_CLOSED = register(
		"spring_closed",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.class_5465.field_26150)
			.method_30374(ConfiguredFeatures.class_5466.field_26162)
			.method_30371()
			.method_30375(16)
	);
	public static final ConfiguredFeature<?, ?> SPRING_CLOSED_DOUBLE = register(
		"spring_closed_double",
		Feature.SPRING_FEATURE
			.configure(ConfiguredFeatures.class_5465.field_26150)
			.method_30374(ConfiguredFeatures.class_5466.field_26162)
			.method_30371()
			.method_30375(32)
	);
	public static final ConfiguredFeature<?, ?> SPRING_OPEN = register(
		"spring_open",
		Feature.SPRING_FEATURE
			.configure(new SpringFeatureConfig(ConfiguredFeatures.class_5467.field_26201, false, 4, 1, ImmutableSet.of(Blocks.NETHERRACK)))
			.method_30374(ConfiguredFeatures.class_5466.field_26163)
			.method_30371()
			.method_30375(8)
	);
	public static final ConfiguredFeature<?, ?> SPRING_WATER = register(
		"spring_water",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(
					ConfiguredFeatures.class_5467.field_26193, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
				)
			)
			.method_30374(Decorator.RANGE_BIASED.configure(new RangeDecoratorConfig(8, 8, 256)))
			.method_30371()
			.method_30375(50)
	);
	public static final ConfiguredFeature<?, ?> PILE_HAY = register(
		"pile_hay", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new PillarBlockStateProvider(Blocks.HAY_BLOCK)))
	);
	public static final ConfiguredFeature<?, ?> PILE_MELON = register(
		"pile_melon", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26176)))
	);
	public static final ConfiguredFeature<?, ?> PILE_SNOW = register(
		"pile_snow", Feature.BLOCK_PILE.configure(new BlockPileFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26185)))
	);
	public static final ConfiguredFeature<?, ?> PILE_ICE = register(
		"pile_ice",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider().addState(ConfiguredFeatures.class_5467.field_26170, 1).addState(ConfiguredFeatures.class_5467.field_26169, 5)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PILE_PUMPKIN = register(
		"pile_pumpkin",
		Feature.BLOCK_PILE
			.configure(
				new BlockPileFeatureConfig(
					new WeightedBlockStateProvider().addState(ConfiguredFeatures.class_5467.field_26177, 19).addState(ConfiguredFeatures.class_5467.field_26186, 1)
				)
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_FIRE = register(
		"patch_fire",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26179), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.class_5467.field_26181.getBlock()))
					.cannotProject()
					.build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26157)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SOUL_FIRE = register(
		"patch_soul_fire",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26180), new SimpleBlockPlacer())
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.class_5467.field_26182.getBlock()))
					.cannotProject()
					.build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26157)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BROWN_MUSHROOM = register(
		"patch_brown_mushroom",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26251), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_RED_MUSHROOM = register(
		"patch_red_mushroom",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26168), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CRIMSON_ROOTS = register(
		"patch_crimson_roots",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26183), new SimpleBlockPlacer())
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
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26187), new DoublePlantPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> PATCH_PUMPKIN = register(
		"patch_pumpkin",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26177), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.class_5467.field_26245.getBlock()))
					.cannotProject()
					.build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26166)
			.method_30372(32)
	);
	public static final ConfiguredFeature<?, ?> PATCH_TAIGA_GRASS = register(
		"patch_taiga_grass", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26142)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_BUSH = register(
		"patch_berry_bush", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26146)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_PLAIN = register(
		"patch_grass_plain",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.class_5465.field_26141)
			.method_30374(ConfiguredFeatures.class_5466.field_26166)
			.method_30374(Decorator.COUNT_NOISE.configure(new NoiseHeightmapDecoratorConfig(-0.8, 5, 10)))
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_FOREST = register(
		"patch_grass_forest",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26141).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(2)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_BADLANDS = register(
		"patch_grass_badlands", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26141).method_30374(ConfiguredFeatures.class_5466.field_26166)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_SAVANNA = register(
		"patch_grass_savanna",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26141).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_NORMAL = register(
		"patch_grass_normal",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26141).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA_2 = register(
		"patch_grass_taiga_2", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26142).method_30374(ConfiguredFeatures.class_5466.field_26166)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_TAIGA = register(
		"patch_grass_taiga",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26142).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(7)
	);
	public static final ConfiguredFeature<?, ?> PATCH_GRASS_JUNGLE = register(
		"patch_grass_jungle",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26143).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(25)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_2 = register(
		"patch_dead_bush_2",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26145).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(2)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH = register(
		"patch_dead_bush", Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26145).method_30374(ConfiguredFeatures.class_5466.field_26166)
	);
	public static final ConfiguredFeature<?, ?> PATCH_DEAD_BUSH_BADLANDS = register(
		"patch_dead_bush_badlands",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26145).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_MELON = register(
		"patch_melon",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26176), SimpleBlockPlacer.INSTANCE)
					.tries(64)
					.whitelist(ImmutableSet.of(ConfiguredFeatures.class_5467.field_26245.getBlock()))
					.canReplace()
					.cannotProject()
					.build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26166)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_SPARSE = register(
		"patch_berry_sparse", PATCH_BERRY_BUSH.method_30374(ConfiguredFeatures.class_5466.field_26166)
	);
	public static final ConfiguredFeature<?, ?> PATCH_BERRY_DECORATED = register(
		"patch_berry_decorated", PATCH_BERRY_BUSH.method_30374(ConfiguredFeatures.class_5466.field_26166).method_30372(12)
	);
	public static final ConfiguredFeature<?, ?> PATCH_WATERLILLY = register(
		"patch_waterlilly",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26184), SimpleBlockPlacer.INSTANCE).tries(10).build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26166)
			.method_30375(4)
	);
	public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS_2 = register(
		"patch_tall_grass_2",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.class_5465.field_26147)
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26158)
			.method_30371()
			.method_30374(Decorator.COUNT_NOISE.configure(new NoiseHeightmapDecoratorConfig(-0.8, 0, 7)))
	);
	public static final ConfiguredFeature<?, ?> PATCH_TALL_GRASS = register(
		"patch_tall_grass",
		Feature.RANDOM_PATCH
			.configure(ConfiguredFeatures.class_5465.field_26147)
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(7)
	);
	public static final ConfiguredFeature<?, ?> PATCH_LARGE_FERN = register(
		"patch_large_fern",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26246), new DoublePlantPlacer())
					.tries(64)
					.cannotProject()
					.build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(7)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS = register(
		"patch_cactus",
		Feature.RANDOM_PATCH
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26188), new ColumnPlacer(1, 2))
					.tries(10)
					.cannotProject()
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DESERT = register(
		"patch_cactus_desert", PATCH_CACTUS.method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> PATCH_CACTUS_DECORATED = register(
		"patch_cactus_decorated", PATCH_CACTUS.method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(5)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_SWAMP = register(
		"patch_sugar_cane_swamp",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26148).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_DESERT = register(
		"patch_sugar_cane_desert",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26148).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(60)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE_BADLANDS = register(
		"patch_sugar_cane_badlands",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26148).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(13)
	);
	public static final ConfiguredFeature<?, ?> PATCH_SUGAR_CANE = register(
		"patch_sugar_cane",
		Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26148).method_30374(ConfiguredFeatures.class_5466.field_26166).method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NETHER = register("brown_mushroom_nether", PATCH_BROWN_MUSHROOM.method_30377(128).method_30372(2));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NETHER = register("red_mushroom_nether", PATCH_RED_MUSHROOM.method_30377(128).method_30372(2));
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_NORMAL = register(
		"brown_mushroom_normal", PATCH_BROWN_MUSHROOM.method_30374(ConfiguredFeatures.class_5466.field_26166).method_30372(4)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_NORMAL = register(
		"red_mushroom_normal", PATCH_RED_MUSHROOM.method_30374(ConfiguredFeatures.class_5466.field_26166).method_30372(8)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_TAIGA = register(
		"brown_mushroom_taiga", PATCH_BROWN_MUSHROOM.method_30372(4).method_30374(ConfiguredFeatures.class_5466.field_26165)
	);
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_TAIGA = register(
		"red_mushroom_taiga", PATCH_RED_MUSHROOM.method_30372(8).method_30374(ConfiguredFeatures.class_5466.field_26166)
	);
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_GIANT = register("brown_mushroom_giant", BROWN_MUSHROOM_TAIGA.method_30375(3));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_GIANT = register("red_mushroom_giant", RED_MUSHROOM_TAIGA.method_30375(3));
	public static final ConfiguredFeature<?, ?> BROWN_MUSHROOM_SWAMP = register("brown_mushroom_swamp", BROWN_MUSHROOM_TAIGA.method_30375(8));
	public static final ConfiguredFeature<?, ?> RED_MUSHROOM_SWAMP = register("red_mushroom_swamp", RED_MUSHROOM_TAIGA.method_30375(8));
	public static final ConfiguredFeature<?, ?> ORE_MAGMA = register(
		"ore_magma",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26222, 33))
			.method_30374(Decorator.MAGMA.configure(NopeDecoratorConfig.INSTANCE))
			.method_30371()
			.method_30375(4)
	);
	public static final ConfiguredFeature<?, ?> ORE_SOUL_SAND = register(
		"ore_soul_sand",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26223, 12))
			.method_30377(32)
			.method_30371()
			.method_30375(12)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_DELTAS = register(
		"ore_gold_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26224, 10))
			.method_30374(ConfiguredFeatures.class_5466.field_26162)
			.method_30371()
			.method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_DELTAS = register(
		"ore_quartz_deltas",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26225, 14))
			.method_30374(ConfiguredFeatures.class_5466.field_26162)
			.method_30371()
			.method_30375(32)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_NETHER = register(
		"ore_gold_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26224, 10))
			.method_30374(ConfiguredFeatures.class_5466.field_26162)
			.method_30371()
			.method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_QUARTZ_NETHER = register(
		"ore_quartz_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26225, 14))
			.method_30374(ConfiguredFeatures.class_5466.field_26162)
			.method_30371()
			.method_30375(16)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL_NETHER = register(
		"ore_gravel_nether",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26205, 33))
			.method_30374(Decorator.RANGE.configure(new RangeDecoratorConfig(5, 0, 37)))
			.method_30371()
			.method_30375(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_BLACKSTONE = register(
		"ore_blackstone",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25846, ConfiguredFeatures.class_5467.field_26226, 33))
			.method_30374(Decorator.RANGE.configure(new RangeDecoratorConfig(5, 10, 37)))
			.method_30371()
			.method_30375(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIRT = register(
		"ore_dirt",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26204, 33))
			.method_30377(256)
			.method_30371()
			.method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRAVEL = register(
		"ore_gravel",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26205, 33))
			.method_30377(256)
			.method_30371()
			.method_30375(8)
	);
	public static final ConfiguredFeature<?, ?> ORE_GRANITE = register(
		"ore_granite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26206, 33))
			.method_30377(80)
			.method_30371()
			.method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIORITE = register(
		"ore_diorite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26207, 33))
			.method_30377(80)
			.method_30371()
			.method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_ANDESITE = register(
		"ore_andesite",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26208, 33))
			.method_30377(80)
			.method_30371()
			.method_30375(10)
	);
	public static final ConfiguredFeature<?, ?> ORE_COAL = register(
		"ore_coal",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26209, 17))
			.method_30377(128)
			.method_30371()
			.method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_IRON = register(
		"ore_iron",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26210, 9))
			.method_30377(64)
			.method_30371()
			.method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD_EXTRA = register(
		"ore_gold_extra",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26211, 9))
			.method_30374(Decorator.RANGE.configure(new RangeDecoratorConfig(32, 32, 80)))
			.method_30371()
			.method_30375(20)
	);
	public static final ConfiguredFeature<?, ?> ORE_GOLD = register(
		"ore_gold",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26211, 9))
			.method_30377(32)
			.method_30371()
			.method_30375(2)
	);
	public static final ConfiguredFeature<?, ?> ORE_REDSTONE = register(
		"ore_redstone",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26212, 8))
			.method_30377(16)
			.method_30371()
			.method_30375(8)
	);
	public static final ConfiguredFeature<?, ?> ORE_DIAMOND = register(
		"ore_diamond",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26213, 8))
			.method_30377(16)
			.method_30371()
	);
	public static final ConfiguredFeature<?, ?> ORE_LAPIS = register(
		"ore_lapis",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26214, 7))
			.method_30374(Decorator.DEPTH_AVERAGE.configure(new CountDepthDecoratorConfig(16, 16)))
			.method_30371()
	);
	public static final ConfiguredFeature<?, ?> ORE_INFESTED = register(
		"ore_infested",
		Feature.ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25845, ConfiguredFeatures.class_5467.field_26217, 9))
			.method_30377(64)
			.method_30371()
			.method_30375(7)
	);
	public static final ConfiguredFeature<?, ?> ORE_EMERALD = register(
		"ore_emerald",
		Feature.EMERALD_ORE
			.configure(new EmeraldOreFeatureConfig(ConfiguredFeatures.class_5467.field_26215, ConfiguredFeatures.class_5467.field_26216))
			.method_30374(Decorator.EMERALD_ORE.configure(DecoratorConfig.DEFAULT))
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_LARGE = register(
		"ore_debris_large",
		Feature.NO_SURFACE_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25847, ConfiguredFeatures.class_5467.field_26194, 3))
			.method_30374(Decorator.DEPTH_AVERAGE.configure(new CountDepthDecoratorConfig(16, 8)))
			.method_30371()
	);
	public static final ConfiguredFeature<?, ?> ORE_DEBRIS_SMALL = register(
		"ore_debris_small",
		Feature.NO_SURFACE_ORE
			.configure(new OreFeatureConfig(OreFeatureConfig.class_5436.field_25847, ConfiguredFeatures.class_5467.field_26194, 2))
			.method_30374(Decorator.RANGE.configure(new RangeDecoratorConfig(8, 16, 128)))
			.method_30371()
	);
	public static final ConfiguredFeature<?, ?> CRIMSON_FUNGI = register(
		"crimson_fungi",
		Feature.HUGE_FUNGUS
			.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_NOT_PLANTED_CONFIG)
			.method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(8)))
	);
	public static final ConfiguredFeature<HugeFungusFeatureConfig, ?> CRIMSON_FUNGI_PLANTED = register(
		"crimson_fungi_planted", Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.CRIMSON_FUNGUS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> WARPED_FUNGI = register(
		"warped_fungi",
		Feature.HUGE_FUNGUS
			.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_NOT_PLANTED_CONFIG)
			.method_30374(Decorator.COUNT_MULTILAYER.configure(new CountConfig(8)))
	);
	public static final ConfiguredFeature<HugeFungusFeatureConfig, ?> WARPED_FUNGI_PLANTED = register(
		"warped_fungi_planted", Feature.HUGE_FUNGUS.configure(HugeFungusFeatureConfig.WARPED_FUNGUS_CONFIG)
	);
	public static final ConfiguredFeature<?, ?> HUGE_BROWN_MUSHROOM = register(
		"huge_brown_mushroom",
		Feature.HUGE_BROWN_MUSHROOM
			.configure(
				new HugeMushroomFeatureConfig(
					new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26191), new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26192), 3
				)
			)
	);
	public static final ConfiguredFeature<?, ?> HUGE_RED_MUSHROOM = register(
		"huge_red_mushroom",
		Feature.HUGE_RED_MUSHROOM
			.configure(
				new HugeMushroomFeatureConfig(
					new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26190), new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26192), 2
				)
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK = register(
		"oak",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26233),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26234),
						new BlobFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(0), 3),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26243),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26244),
						new DarkOakFoliagePlacer(class_5428.method_30314(0), class_5428.method_30314(0)),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26241),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26242),
						new BlobFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(0), 3),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26239),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26240),
						new AcaciaFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(0)),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26237),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26238),
						new SpruceFoliagePlacer(class_5428.method_30315(2, 1), class_5428.method_30315(0, 2), class_5428.method_30315(1, 1)),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26237),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26238),
						new PineFoliagePlacer(class_5428.method_30314(1), class_5428.method_30314(1), class_5428.method_30315(3, 1)),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26235),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26236),
						new BlobFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(0), 3),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26233),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26234),
						new LargeOakFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(4), 4),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26235),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26236),
						new BlobFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(0), 3),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26235),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26236),
						new JungleFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(0), 2),
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
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26237),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26238),
						new MegaPineFoliagePlacer(class_5428.method_30314(0), class_5428.method_30314(0), class_5428.method_30315(13, 4)),
						new GiantTrunkPlacer(13, 2, 14),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26228))))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> MEGA_PINE = register(
		"mega_pine",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26237),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26238),
						new MegaPineFoliagePlacer(class_5428.method_30314(0), class_5428.method_30314(0), class_5428.method_30315(3, 4)),
						new GiantTrunkPlacer(13, 2, 14),
						new TwoLayersFeatureSize(1, 1, 2)
					)
					.decorators(ImmutableList.of(new AlterGroundTreeDecorator(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26228))))
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> SUPER_BIRCH_BEES_0002 = register(
		"super_birch_bees_0002",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26241),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26242),
						new BlobFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(0), 3),
						new StraightTrunkPlacer(5, 2, 6),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.ignoreVines()
					.decorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26154))
					.build()
			)
	);
	public static final ConfiguredFeature<?, ?> SWAMP_TREE = register(
		"swamp_tree",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26233),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26234),
						new BlobFoliagePlacer(class_5428.method_30314(3), class_5428.method_30314(0), 3),
						new StraightTrunkPlacer(5, 3, 0),
						new TwoLayersFeatureSize(1, 0, 1)
					)
					.maxWaterDepth(1)
					.decorators(ImmutableList.of(LeaveVineTreeDecorator.INSTANCE))
					.build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> JUNGLE_BUSH = register(
		"jungle_bush",
		Feature.TREE
			.configure(
				new TreeFeatureConfig.Builder(
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26235),
						new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26234),
						new BushFoliagePlacer(class_5428.method_30314(2), class_5428.method_30314(1), 2),
						new StraightTrunkPlacer(1, 0, 0),
						new TwoLayersFeatureSize(0, 0, 0)
					)
					.heightmap(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES)
					.build()
			)
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BEES_0002 = register(
		"oak_bees_0002", Feature.TREE.configure(OAK.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26154)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BEES_002 = register(
		"oak_bees_002", Feature.TREE.configure(OAK.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26155)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> OAK_BEES_005 = register(
		"oak_bees_005", Feature.TREE.configure(OAK.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26156)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_BEES_0002 = register(
		"birch_bees_0002", Feature.TREE.configure(BIRCH.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26154)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_BEES_002 = register(
		"birch_bees_002", Feature.TREE.configure(BIRCH.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26155)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> BIRCH_BEES_005 = register(
		"birch_bees_005", Feature.TREE.configure(BIRCH.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26156)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK_BEES_0002 = register(
		"fancy_oak_bees_0002", Feature.TREE.configure(FANCY_OAK.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26154)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK_BEES_002 = register(
		"fancy_oak_bees_002", Feature.TREE.configure(FANCY_OAK.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26155)))
	);
	public static final ConfiguredFeature<TreeFeatureConfig, ?> FANCY_OAK_BEES_005 = register(
		"fancy_oak_bees_005", Feature.TREE.configure(FANCY_OAK.method_30381().setTreeDecorators(ImmutableList.of(ConfiguredFeatures.class_5466.field_26156)))
	);
	public static final ConfiguredFeature<?, ?> OAK_BADLANDS = register(
		"oak_badlands",
		OAK.method_30374(ConfiguredFeatures.class_5466.field_26165).method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(5, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> SPRUCE_SNOVY = register(
		"spruce_snovy",
		SPRUCE.method_30374(ConfiguredFeatures.class_5466.field_26165).method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> FLOWER_WARM = register(
		"flower_warm",
		Feature.FLOWER
			.configure(ConfiguredFeatures.class_5465.field_26144)
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(4)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_DEFAULT = register(
		"flower_default",
		Feature.FLOWER
			.configure(ConfiguredFeatures.class_5465.field_26144)
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(2)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_FOREST = register(
		"flower_forest",
		Feature.FLOWER
			.configure(new RandomPatchFeatureConfig.Builder(ForestFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE).tries(64).build())
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(100)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_SWAMP = register(
		"flower_swamp",
		Feature.FLOWER
			.configure(
				new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26172), SimpleBlockPlacer.INSTANCE).tries(64).build()
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
	);
	public static final ConfiguredFeature<?, ?> FLOWER_PLAIN = register(
		"flower_plain",
		Feature.FLOWER.configure(new RandomPatchFeatureConfig.Builder(PlainsFlowerBlockStateProvider.INSTANCE, SimpleBlockPlacer.INSTANCE).tries(64).build())
	);
	public static final ConfiguredFeature<?, ?> FLOWER_PLAIN_DECORATED = register(
		"flower_plain_decorated",
		FLOWER_PLAIN.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26158)
			.method_30371()
			.method_30374(Decorator.COUNT_NOISE.configure(new NoiseHeightmapDecoratorConfig(-0.8, 15, 4)))
	);
	private static final ImmutableList<Supplier<ConfiguredFeature<?, ?>>> field_26090 = ImmutableList.of(
		() -> Feature.RANDOM_PATCH
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26248), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.RANDOM_PATCH
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26249), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.RANDOM_PATCH
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26250), new DoublePlantPlacer())
						.tries(64)
						.cannotProject()
						.build()
				),
		() -> Feature.FLOWER
				.configure(
					new RandomPatchFeatureConfig.Builder(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26171), SimpleBlockPlacer.INSTANCE)
						.tries(64)
						.build()
				)
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION_COMMON = register(
		"forest_flower_vegetation_common",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(new SimpleRandomFeatureConfig(field_26090))
			.method_30373(class_5428.method_30315(-1, 4))
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(5)
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_VEGETATION = register(
		"forest_flower_vegetation",
		Feature.SIMPLE_RANDOM_SELECTOR
			.configure(new SimpleRandomFeatureConfig(field_26090))
			.method_30373(class_5428.method_30315(-3, 4))
			.method_30374(ConfiguredFeatures.class_5466.field_26164)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30375(5)
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
			.method_30374(Decorator.DARK_OAK_TREE.configure(DecoratorConfig.DEFAULT))
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
			.method_30374(Decorator.DARK_OAK_TREE.configure(DecoratorConfig.DEFAULT))
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
			.method_30374(ConfiguredFeatures.class_5466.field_26159)
			.method_30371()
			.method_30374(Decorator.COUNT_NOISE_BIASED.configure(new TopSolidHeightmapNoiseBiasedDecoratorConfig(20, 400.0, 0.0)))
	);
	public static final ConfiguredFeature<?, ?> FOREST_FLOWER_TREES = register(
		"forest_flower_trees",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(BIRCH_BEES_002.withChance(0.2F), FANCY_OAK_BEES_002.withChance(0.1F)), OAK_BEES_002))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(6, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TAIGA_VEGETATION = register(
		"taiga_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(PINE.withChance(0.33333334F)), SPRUCE))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SHATTERED_SAVANNA = register(
		"trees_shattered_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA.withChance(0.8F)), OAK))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_SAVANNA = register(
		"trees_savanna",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(ACACIA.withChance(0.8F)), OAK))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(1, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BIRCH_TALL = register(
		"birch_tall",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SUPER_BIRCH_BEES_0002.withChance(0.5F)), BIRCH_BEES_0002))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN_EDGE = register(
		"trees_mountain_edge",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE.withChance(0.666F), FANCY_OAK.withChance(0.1F)), OAK))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(3, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_MOUNTAIN = register(
		"trees_mountain",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(SPRUCE.withChance(0.666F), FANCY_OAK.withChance(0.1F)), OAK))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_WATER = register(
		"trees_water",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F)), OAK))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(0, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BIRCH_OTHER = register(
		"birch_other",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(BIRCH_BEES_0002.withChance(0.2F), FANCY_OAK_BEES_0002.withChance(0.1F)), OAK_BEES_0002))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> PLAIN_VEGETATION = register(
		"plain_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK_BEES_005.withChance(0.33333334F)), OAK_BEES_005))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(0, 0.05F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE_EDGE = register(
		"trees_jungle_edge",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F)), JUNGLE_TREE))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(2, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT_SPRUCE = register(
		"trees_giant_spruce",
		Feature.RANDOM_SELECTOR
			.configure(new RandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.withChance(0.33333334F), PINE.withChance(0.33333334F)), SPRUCE))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_GIANT = register(
		"trees_giant",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(ImmutableList.of(MEGA_SPRUCE.withChance(0.025641026F), MEGA_PINE.withChance(0.30769232F), PINE.withChance(0.33333334F)), SPRUCE)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(10, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> TREES_JUNGLE = register(
		"trees_jungle",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(ImmutableList.of(FANCY_OAK.withChance(0.1F), JUNGLE_BUSH.withChance(0.5F), MEGA_JUNGLE_TREE.withChance(0.33333334F)), JUNGLE_TREE)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(50, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> BAMBOO_VEGETATION = register(
		"bamboo_vegetation",
		Feature.RANDOM_SELECTOR
			.configure(
				new RandomFeatureConfig(
					ImmutableList.of(FANCY_OAK.withChance(0.05F), JUNGLE_BUSH.withChance(0.15F), MEGA_JUNGLE_TREE.withChance(0.7F)),
					Feature.RANDOM_PATCH.configure(ConfiguredFeatures.class_5465.field_26143)
				)
			)
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
			.method_30374(Decorator.COUNT_EXTRA.configure(new CountExtraChanceDecoratorConfig(30, 0.1F, 1)))
	);
	public static final ConfiguredFeature<?, ?> MUSHROOM_FIELD_VEGETATION = register(
		"mushroom_field_vegetation",
		Feature.RANDOM_BOOLEAN_SELECTOR
			.configure(new RandomBooleanFeatureConfig(() -> HUGE_RED_MUSHROOM, () -> HUGE_BROWN_MUSHROOM))
			.method_30374(ConfiguredFeatures.class_5466.field_26165)
	);

	private static <FC extends FeatureConfig> ConfiguredFeature<FC, ?> register(String name, ConfiguredFeature<FC, ?> configuredFeature) {
		return Registry.register(BuiltinRegistries.CONFIGURED_FEATURE, name, configuredFeature);
	}

	public static final class class_5465 {
		public static final RandomPatchFeatureConfig field_26141 = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26200), SimpleBlockPlacer.INSTANCE
			)
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig field_26142 = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider().addState(ConfiguredFeatures.class_5467.field_26200, 1).addState(ConfiguredFeatures.class_5467.field_26227, 4),
				SimpleBlockPlacer.INSTANCE
			)
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig field_26143 = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider().addState(ConfiguredFeatures.class_5467.field_26200, 3).addState(ConfiguredFeatures.class_5467.field_26227, 1),
				SimpleBlockPlacer.INSTANCE
			)
			.blacklist(ImmutableSet.of(ConfiguredFeatures.class_5467.field_26228))
			.tries(32)
			.build();
		public static final RandomPatchFeatureConfig field_26144 = new RandomPatchFeatureConfig.Builder(
				new WeightedBlockStateProvider().addState(ConfiguredFeatures.class_5467.field_26173, 2).addState(ConfiguredFeatures.class_5467.field_26174, 1),
				SimpleBlockPlacer.INSTANCE
			)
			.tries(64)
			.build();
		public static final RandomPatchFeatureConfig field_26145 = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26175), SimpleBlockPlacer.INSTANCE
			)
			.tries(4)
			.build();
		public static final RandomPatchFeatureConfig field_26146 = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26178), SimpleBlockPlacer.INSTANCE
			)
			.tries(64)
			.whitelist(ImmutableSet.of(ConfiguredFeatures.class_5467.field_26245.getBlock()))
			.cannotProject()
			.build();
		public static final RandomPatchFeatureConfig field_26147 = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26247), new DoublePlantPlacer()
			)
			.tries(64)
			.cannotProject()
			.build();
		public static final RandomPatchFeatureConfig field_26148 = new RandomPatchFeatureConfig.Builder(
				new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26189), new ColumnPlacer(2, 2)
			)
			.tries(20)
			.spreadX(4)
			.spreadY(0)
			.spreadZ(4)
			.cannotProject()
			.needsWater()
			.build();
		public static final SpringFeatureConfig field_26149 = new SpringFeatureConfig(
			ConfiguredFeatures.class_5467.field_26201, true, 4, 1, ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE)
		);
		public static final SpringFeatureConfig field_26150 = new SpringFeatureConfig(
			ConfiguredFeatures.class_5467.field_26201, false, 5, 0, ImmutableSet.of(Blocks.NETHERRACK)
		);
		public static final BlockPileFeatureConfig field_26151 = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider()
				.addState(ConfiguredFeatures.class_5467.field_26183, 87)
				.addState(ConfiguredFeatures.class_5467.field_26196, 11)
				.addState(ConfiguredFeatures.class_5467.field_26197, 1)
		);
		public static final BlockPileFeatureConfig field_26152 = new BlockPileFeatureConfig(
			new WeightedBlockStateProvider()
				.addState(ConfiguredFeatures.class_5467.field_26198, 85)
				.addState(ConfiguredFeatures.class_5467.field_26183, 1)
				.addState(ConfiguredFeatures.class_5467.field_26197, 13)
				.addState(ConfiguredFeatures.class_5467.field_26196, 1)
		);
		public static final BlockPileFeatureConfig field_26153 = new BlockPileFeatureConfig(new SimpleBlockStateProvider(ConfiguredFeatures.class_5467.field_26199));
	}

	public static final class class_5466 {
		public static final BeehiveTreeDecorator field_26154 = new BeehiveTreeDecorator(0.002F);
		public static final BeehiveTreeDecorator field_26155 = new BeehiveTreeDecorator(0.02F);
		public static final BeehiveTreeDecorator field_26156 = new BeehiveTreeDecorator(0.05F);
		public static final ConfiguredDecorator<CountConfig> field_26157 = Decorator.FIRE.configure(new CountConfig(10));
		public static final ConfiguredDecorator<NopeDecoratorConfig> field_26158 = Decorator.HEIGHTMAP.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> field_26159 = Decorator.TOP_SOLID_HEIGHTMAP.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> field_26160 = Decorator.HEIGHTMAP_WORLD_SURFACE.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<NopeDecoratorConfig> field_26161 = Decorator.HEIGHTMAP_SPREAD_DOUBLE.configure(DecoratorConfig.DEFAULT);
		public static final ConfiguredDecorator<RangeDecoratorConfig> field_26162 = Decorator.RANGE.configure(new RangeDecoratorConfig(10, 20, 128));
		public static final ConfiguredDecorator<RangeDecoratorConfig> field_26163 = Decorator.RANGE.configure(new RangeDecoratorConfig(4, 8, 128));
		public static final ConfiguredDecorator<?> field_26164 = Decorator.SPREAD_32_ABOVE.configure(NopeDecoratorConfig.INSTANCE);
		public static final ConfiguredDecorator<?> field_26165 = field_26158.method_30371();
		public static final ConfiguredDecorator<?> field_26166 = field_26161.method_30371();
		public static final ConfiguredDecorator<?> field_26167 = field_26159.method_30371();
	}

	public static final class class_5467 {
		protected static final BlockState field_26200 = Blocks.GRASS.getDefaultState();
		protected static final BlockState field_26227 = Blocks.FERN.getDefaultState();
		protected static final BlockState field_26228 = Blocks.PODZOL.getDefaultState();
		protected static final BlockState field_26229 = Blocks.COARSE_DIRT.getDefaultState();
		protected static final BlockState field_26230 = Blocks.MYCELIUM.getDefaultState();
		protected static final BlockState field_26231 = Blocks.SNOW_BLOCK.getDefaultState();
		protected static final BlockState field_26232 = Blocks.ICE.getDefaultState();
		protected static final BlockState field_26233 = Blocks.OAK_LOG.getDefaultState();
		protected static final BlockState field_26234 = Blocks.OAK_LEAVES.getDefaultState();
		protected static final BlockState field_26235 = Blocks.JUNGLE_LOG.getDefaultState();
		protected static final BlockState field_26236 = Blocks.JUNGLE_LEAVES.getDefaultState();
		protected static final BlockState field_26237 = Blocks.SPRUCE_LOG.getDefaultState();
		protected static final BlockState field_26238 = Blocks.SPRUCE_LEAVES.getDefaultState();
		protected static final BlockState field_26239 = Blocks.ACACIA_LOG.getDefaultState();
		protected static final BlockState field_26240 = Blocks.ACACIA_LEAVES.getDefaultState();
		protected static final BlockState field_26241 = Blocks.BIRCH_LOG.getDefaultState();
		protected static final BlockState field_26242 = Blocks.BIRCH_LEAVES.getDefaultState();
		protected static final BlockState field_26243 = Blocks.DARK_OAK_LOG.getDefaultState();
		protected static final BlockState field_26244 = Blocks.DARK_OAK_LEAVES.getDefaultState();
		protected static final BlockState field_26245 = Blocks.GRASS_BLOCK.getDefaultState();
		protected static final BlockState field_26246 = Blocks.LARGE_FERN.getDefaultState();
		protected static final BlockState field_26247 = Blocks.TALL_GRASS.getDefaultState();
		protected static final BlockState field_26248 = Blocks.LILAC.getDefaultState();
		protected static final BlockState field_26249 = Blocks.ROSE_BUSH.getDefaultState();
		protected static final BlockState field_26250 = Blocks.PEONY.getDefaultState();
		protected static final BlockState field_26251 = Blocks.BROWN_MUSHROOM.getDefaultState();
		protected static final BlockState field_26168 = Blocks.RED_MUSHROOM.getDefaultState();
		protected static final BlockState field_26169 = Blocks.PACKED_ICE.getDefaultState();
		protected static final BlockState field_26170 = Blocks.BLUE_ICE.getDefaultState();
		protected static final BlockState field_26171 = Blocks.LILY_OF_THE_VALLEY.getDefaultState();
		protected static final BlockState field_26172 = Blocks.BLUE_ORCHID.getDefaultState();
		protected static final BlockState field_26173 = Blocks.POPPY.getDefaultState();
		protected static final BlockState field_26174 = Blocks.DANDELION.getDefaultState();
		protected static final BlockState field_26175 = Blocks.DEAD_BUSH.getDefaultState();
		protected static final BlockState field_26176 = Blocks.MELON.getDefaultState();
		protected static final BlockState field_26177 = Blocks.PUMPKIN.getDefaultState();
		protected static final BlockState field_26178 = Blocks.SWEET_BERRY_BUSH.getDefaultState().with(SweetBerryBushBlock.AGE, Integer.valueOf(3));
		protected static final BlockState field_26179 = Blocks.FIRE.getDefaultState();
		protected static final BlockState field_26180 = Blocks.SOUL_FIRE.getDefaultState();
		protected static final BlockState field_26181 = Blocks.NETHERRACK.getDefaultState();
		protected static final BlockState field_26182 = Blocks.SOUL_SOIL.getDefaultState();
		protected static final BlockState field_26183 = Blocks.CRIMSON_ROOTS.getDefaultState();
		protected static final BlockState field_26184 = Blocks.LILY_PAD.getDefaultState();
		protected static final BlockState field_26185 = Blocks.SNOW.getDefaultState();
		protected static final BlockState field_26186 = Blocks.JACK_O_LANTERN.getDefaultState();
		protected static final BlockState field_26187 = Blocks.SUNFLOWER.getDefaultState();
		protected static final BlockState field_26188 = Blocks.CACTUS.getDefaultState();
		protected static final BlockState field_26189 = Blocks.SUGAR_CANE.getDefaultState();
		protected static final BlockState field_26190 = Blocks.RED_MUSHROOM_BLOCK.getDefaultState().with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final BlockState field_26191 = Blocks.BROWN_MUSHROOM_BLOCK
			.getDefaultState()
			.with(MushroomBlock.UP, Boolean.valueOf(true))
			.with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final BlockState field_26192 = Blocks.MUSHROOM_STEM
			.getDefaultState()
			.with(MushroomBlock.UP, Boolean.valueOf(false))
			.with(MushroomBlock.DOWN, Boolean.valueOf(false));
		protected static final FluidState field_26193 = Fluids.WATER.getDefaultState();
		protected static final FluidState field_26201 = Fluids.LAVA.getDefaultState();
		protected static final BlockState field_26202 = Blocks.WATER.getDefaultState();
		protected static final BlockState field_26203 = Blocks.LAVA.getDefaultState();
		protected static final BlockState field_26204 = Blocks.DIRT.getDefaultState();
		protected static final BlockState field_26205 = Blocks.GRAVEL.getDefaultState();
		protected static final BlockState field_26206 = Blocks.GRANITE.getDefaultState();
		protected static final BlockState field_26207 = Blocks.DIORITE.getDefaultState();
		protected static final BlockState field_26208 = Blocks.ANDESITE.getDefaultState();
		protected static final BlockState field_26209 = Blocks.COAL_ORE.getDefaultState();
		protected static final BlockState field_26210 = Blocks.IRON_ORE.getDefaultState();
		protected static final BlockState field_26211 = Blocks.GOLD_ORE.getDefaultState();
		protected static final BlockState field_26212 = Blocks.REDSTONE_ORE.getDefaultState();
		protected static final BlockState field_26213 = Blocks.DIAMOND_ORE.getDefaultState();
		protected static final BlockState field_26214 = Blocks.LAPIS_ORE.getDefaultState();
		protected static final BlockState field_26215 = Blocks.STONE.getDefaultState();
		protected static final BlockState field_26216 = Blocks.EMERALD_ORE.getDefaultState();
		protected static final BlockState field_26217 = Blocks.INFESTED_STONE.getDefaultState();
		protected static final BlockState field_26218 = Blocks.SAND.getDefaultState();
		protected static final BlockState field_26219 = Blocks.CLAY.getDefaultState();
		protected static final BlockState field_26220 = Blocks.MOSSY_COBBLESTONE.getDefaultState();
		protected static final BlockState field_26221 = Blocks.SEAGRASS.getDefaultState();
		protected static final BlockState field_26222 = Blocks.MAGMA_BLOCK.getDefaultState();
		protected static final BlockState field_26223 = Blocks.SOUL_SAND.getDefaultState();
		protected static final BlockState field_26224 = Blocks.NETHER_GOLD_ORE.getDefaultState();
		protected static final BlockState field_26225 = Blocks.NETHER_QUARTZ_ORE.getDefaultState();
		protected static final BlockState field_26226 = Blocks.BLACKSTONE.getDefaultState();
		protected static final BlockState field_26194 = Blocks.ANCIENT_DEBRIS.getDefaultState();
		protected static final BlockState field_26195 = Blocks.BASALT.getDefaultState();
		protected static final BlockState field_26196 = Blocks.CRIMSON_FUNGUS.getDefaultState();
		protected static final BlockState field_26197 = Blocks.WARPED_FUNGUS.getDefaultState();
		protected static final BlockState field_26198 = Blocks.WARPED_ROOTS.getDefaultState();
		protected static final BlockState field_26199 = Blocks.NETHER_SPROUTS.getDefaultState();
	}
}
