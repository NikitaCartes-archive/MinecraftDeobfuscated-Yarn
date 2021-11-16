package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableSet;
import java.util.List;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class MiscConfiguredFeatures {
	public static final ConfiguredFeature<DefaultFeatureConfig, ?> ICE_SPIKE = ConfiguredFeatures.register(
		"ice_spike", Feature.ICE_SPIKE.configure(FeatureConfig.DEFAULT)
	);
	public static final ConfiguredFeature<DiskFeatureConfig, ?> ICE_PATCH = ConfiguredFeatures.register(
		"ice_patch",
		Feature.ICE_PATCH
			.configure(
				new DiskFeatureConfig(
					Blocks.PACKED_ICE.getDefaultState(),
					UniformIntProvider.create(2, 3),
					1,
					List.of(
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
	);
	public static final ConfiguredFeature<SingleStateFeatureConfig, ?> FOREST_ROCK = ConfiguredFeatures.register(
		"forest_rock", Feature.FOREST_ROCK.configure(new SingleStateFeatureConfig(Blocks.MOSSY_COBBLESTONE.getDefaultState()))
	);
	public static final ConfiguredFeature<SingleStateFeatureConfig, ?> ICEBERG_PACKED = ConfiguredFeatures.register(
		"iceberg_packed", Feature.ICEBERG.configure(new SingleStateFeatureConfig(Blocks.PACKED_ICE.getDefaultState()))
	);
	public static final ConfiguredFeature<SingleStateFeatureConfig, ?> ICEBERG_BLUE = ConfiguredFeatures.register(
		"iceberg_blue", Feature.ICEBERG.configure(new SingleStateFeatureConfig(Blocks.BLUE_ICE.getDefaultState()))
	);
	public static final ConfiguredFeature<DefaultFeatureConfig, ?> BLUE_ICE = ConfiguredFeatures.register(
		"blue_ice", Feature.BLUE_ICE.configure(FeatureConfig.DEFAULT)
	);
	public static final ConfiguredFeature<LakeFeature.Config, ?> LAKE_LAVA = ConfiguredFeatures.register(
		"lake_lava",
		Feature.LAKE.configure(new LakeFeature.Config(BlockStateProvider.of(Blocks.LAVA.getDefaultState()), BlockStateProvider.of(Blocks.STONE.getDefaultState())))
	);
	public static final ConfiguredFeature<DiskFeatureConfig, ?> DISK_CLAY = ConfiguredFeatures.register(
		"disk_clay",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					Blocks.CLAY.getDefaultState(), UniformIntProvider.create(2, 3), 1, List.of(Blocks.DIRT.getDefaultState(), Blocks.CLAY.getDefaultState())
				)
			)
	);
	public static final ConfiguredFeature<DiskFeatureConfig, ?> DISK_GRAVEL = ConfiguredFeatures.register(
		"disk_gravel",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					Blocks.GRAVEL.getDefaultState(), UniformIntProvider.create(2, 5), 2, List.of(Blocks.DIRT.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState())
				)
			)
	);
	public static final ConfiguredFeature<DiskFeatureConfig, ?> DISK_SAND = ConfiguredFeatures.register(
		"disk_sand",
		Feature.DISK
			.configure(
				new DiskFeatureConfig(
					Blocks.SAND.getDefaultState(), UniformIntProvider.create(2, 6), 2, List.of(Blocks.DIRT.getDefaultState(), Blocks.GRASS_BLOCK.getDefaultState())
				)
			)
	);
	public static final ConfiguredFeature<?, ?> FREEZE_TOP_LAYER = ConfiguredFeatures.register(
		"freeze_top_layer", Feature.FREEZE_TOP_LAYER.configure(FeatureConfig.DEFAULT)
	);
	public static final ConfiguredFeature<?, ?> BONUS_CHEST = ConfiguredFeatures.register("bonus_chest", Feature.BONUS_CHEST.configure(FeatureConfig.DEFAULT));
	public static final ConfiguredFeature<?, ?> VOID_START_PLATFORM = ConfiguredFeatures.register(
		"void_start_platform", Feature.VOID_START_PLATFORM.configure(FeatureConfig.DEFAULT)
	);
	public static final ConfiguredFeature<DefaultFeatureConfig, ?> DESERT_WELL = ConfiguredFeatures.register(
		"desert_well", Feature.DESERT_WELL.configure(FeatureConfig.DEFAULT)
	);
	public static final ConfiguredFeature<SpringFeatureConfig, ?> SPRING_LAVA_OVERWORLD = ConfiguredFeatures.register(
		"spring_lava_overworld",
		Feature.SPRING_FEATURE
			.configure(
				new SpringFeatureConfig(
					Fluids.LAVA.getDefaultState(),
					true,
					4,
					1,
					ImmutableSet.of(Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DIRT)
				)
			)
	);
	public static final ConfiguredFeature<SpringFeatureConfig, ?> SPRING_LAVA_FROZEN = ConfiguredFeatures.register(
		"spring_lava_frozen",
		Feature.SPRING_FEATURE
			.configure(new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), true, 4, 1, ImmutableSet.of(Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE)))
	);
	public static final ConfiguredFeature<SpringFeatureConfig, ?> SPRING_WATER = ConfiguredFeatures.register(
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
	);
}
