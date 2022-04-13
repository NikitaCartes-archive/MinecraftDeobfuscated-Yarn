package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;

public class MiscConfiguredFeatures {
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> ICE_SPIKE = ConfiguredFeatures.register("ice_spike", Feature.ICE_SPIKE);
	public static final RegistryEntry<ConfiguredFeature<DiskFeatureConfig, ?>> ICE_PATCH = ConfiguredFeatures.register(
		"ice_patch",
		Feature.DISK,
		new DiskFeatureConfig(
			PredicatedStateProvider.of(Blocks.PACKED_ICE),
			BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.ICE)),
			UniformIntProvider.create(2, 3),
			1
		)
	);
	public static final RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, ?>> FOREST_ROCK = ConfiguredFeatures.register(
		"forest_rock", Feature.FOREST_ROCK, new SingleStateFeatureConfig(Blocks.MOSSY_COBBLESTONE.getDefaultState())
	);
	public static final RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, ?>> ICEBERG_PACKED = ConfiguredFeatures.register(
		"iceberg_packed", Feature.ICEBERG, new SingleStateFeatureConfig(Blocks.PACKED_ICE.getDefaultState())
	);
	public static final RegistryEntry<ConfiguredFeature<SingleStateFeatureConfig, ?>> ICEBERG_BLUE = ConfiguredFeatures.register(
		"iceberg_blue", Feature.ICEBERG, new SingleStateFeatureConfig(Blocks.BLUE_ICE.getDefaultState())
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> BLUE_ICE = ConfiguredFeatures.register("blue_ice", Feature.BLUE_ICE);
	public static final RegistryEntry<ConfiguredFeature<LakeFeature.Config, ?>> LAKE_LAVA = ConfiguredFeatures.register(
		"lake_lava",
		Feature.LAKE,
		new LakeFeature.Config(BlockStateProvider.of(Blocks.LAVA.getDefaultState()), BlockStateProvider.of(Blocks.STONE.getDefaultState()))
	);
	public static final RegistryEntry<ConfiguredFeature<DiskFeatureConfig, ?>> DISK_CLAY = ConfiguredFeatures.register(
		"disk_clay",
		Feature.DISK,
		new DiskFeatureConfig(
			PredicatedStateProvider.of(Blocks.CLAY), BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformIntProvider.create(2, 3), 1
		)
	);
	public static final RegistryEntry<ConfiguredFeature<DiskFeatureConfig, ?>> DISK_GRAVEL = ConfiguredFeatures.register(
		"disk_gravel",
		Feature.DISK,
		new DiskFeatureConfig(
			PredicatedStateProvider.of(Blocks.GRAVEL), BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformIntProvider.create(2, 5), 2
		)
	);
	public static final RegistryEntry<ConfiguredFeature<DiskFeatureConfig, ?>> DISK_SAND = ConfiguredFeatures.register(
		"disk_sand",
		Feature.DISK,
		new DiskFeatureConfig(
			new PredicatedStateProvider(
				BlockStateProvider.of(Blocks.SAND),
				List.of(new PredicatedStateProvider.Rule(BlockPredicate.matchingBlocks(Direction.DOWN.getVector(), Blocks.AIR), BlockStateProvider.of(Blocks.SANDSTONE)))
			),
			BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)),
			UniformIntProvider.create(2, 6),
			2
		)
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> FREEZE_TOP_LAYER = ConfiguredFeatures.register(
		"freeze_top_layer", Feature.FREEZE_TOP_LAYER
	);
	public static final RegistryEntry<ConfiguredFeature<DiskFeatureConfig, ?>> DISK_GRASS = ConfiguredFeatures.register(
		"disk_grass",
		Feature.DISK,
		new DiskFeatureConfig(
			new PredicatedStateProvider(
				BlockStateProvider.of(Blocks.DIRT),
				List.of(
					new PredicatedStateProvider.Rule(
						BlockPredicate.not(
							BlockPredicate.eitherOf(BlockPredicate.solid(Direction.UP.getVector()), BlockPredicate.matchingFluids(Direction.UP.getVector(), Fluids.WATER))
						),
						BlockStateProvider.of(Blocks.GRASS_BLOCK)
					)
				)
			),
			BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.MUD)),
			UniformIntProvider.create(2, 6),
			2
		)
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> BONUS_CHEST = ConfiguredFeatures.register("bonus_chest", Feature.BONUS_CHEST);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> VOID_START_PLATFORM = ConfiguredFeatures.register(
		"void_start_platform", Feature.VOID_START_PLATFORM
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> DESERT_WELL = ConfiguredFeatures.register("desert_well", Feature.DESERT_WELL);
	public static final RegistryEntry<ConfiguredFeature<SpringFeatureConfig, ?>> SPRING_LAVA_OVERWORLD = ConfiguredFeatures.register(
		"spring_lava_overworld",
		Feature.SPRING_FEATURE,
		new SpringFeatureConfig(
			Fluids.LAVA.getDefaultState(),
			true,
			4,
			1,
			RegistryEntryList.of(
				Block::getRegistryEntry, Blocks.STONE, Blocks.GRANITE, Blocks.DIORITE, Blocks.ANDESITE, Blocks.DEEPSLATE, Blocks.TUFF, Blocks.CALCITE, Blocks.DIRT
			)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<SpringFeatureConfig, ?>> SPRING_LAVA_FROZEN = ConfiguredFeatures.register(
		"spring_lava_frozen",
		Feature.SPRING_FEATURE,
		new SpringFeatureConfig(
			Fluids.LAVA.getDefaultState(), true, 4, 1, RegistryEntryList.of(Block::getRegistryEntry, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<SpringFeatureConfig, ?>> SPRING_WATER = ConfiguredFeatures.register(
		"spring_water",
		Feature.SPRING_FEATURE,
		new SpringFeatureConfig(
			Fluids.WATER.getDefaultState(),
			true,
			4,
			1,
			RegistryEntryList.of(
				Block::getRegistryEntry,
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
	);
}
