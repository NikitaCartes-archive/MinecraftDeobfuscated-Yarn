package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.PredicatedStateProvider;

public class MiscConfiguredFeatures {
	public static final RegistryKey<ConfiguredFeature<?, ?>> ICE_SPIKE = ConfiguredFeatures.of("ice_spike");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ICE_PATCH = ConfiguredFeatures.of("ice_patch");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FOREST_ROCK = ConfiguredFeatures.of("forest_rock");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ICEBERG_PACKED = ConfiguredFeatures.of("iceberg_packed");
	public static final RegistryKey<ConfiguredFeature<?, ?>> ICEBERG_BLUE = ConfiguredFeatures.of("iceberg_blue");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BLUE_ICE = ConfiguredFeatures.of("blue_ice");
	public static final RegistryKey<ConfiguredFeature<?, ?>> LAKE_LAVA = ConfiguredFeatures.of("lake_lava");
	public static final RegistryKey<ConfiguredFeature<?, ?>> DISK_CLAY = ConfiguredFeatures.of("disk_clay");
	public static final RegistryKey<ConfiguredFeature<?, ?>> DISK_GRAVEL = ConfiguredFeatures.of("disk_gravel");
	public static final RegistryKey<ConfiguredFeature<?, ?>> DISK_SAND = ConfiguredFeatures.of("disk_sand");
	public static final RegistryKey<ConfiguredFeature<?, ?>> FREEZE_TOP_LAYER = ConfiguredFeatures.of("freeze_top_layer");
	public static final RegistryKey<ConfiguredFeature<?, ?>> DISK_GRASS = ConfiguredFeatures.of("disk_grass");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BONUS_CHEST = ConfiguredFeatures.of("bonus_chest");
	public static final RegistryKey<ConfiguredFeature<?, ?>> VOID_START_PLATFORM = ConfiguredFeatures.of("void_start_platform");
	public static final RegistryKey<ConfiguredFeature<?, ?>> DESERT_WELL = ConfiguredFeatures.of("desert_well");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SPRING_LAVA_OVERWORLD = ConfiguredFeatures.of("spring_lava_overworld");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SPRING_LAVA_FROZEN = ConfiguredFeatures.of("spring_lava_frozen");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SPRING_WATER = ConfiguredFeatures.of("spring_water");

	public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable) {
		ConfiguredFeatures.register(featureRegisterable, ICE_SPIKE, Feature.ICE_SPIKE);
		ConfiguredFeatures.register(
			featureRegisterable,
			ICE_PATCH,
			Feature.DISK,
			new DiskFeatureConfig(
				PredicatedStateProvider.of(Blocks.PACKED_ICE),
				BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.PODZOL, Blocks.COARSE_DIRT, Blocks.MYCELIUM, Blocks.SNOW_BLOCK, Blocks.ICE)),
				UniformIntProvider.create(2, 3),
				1
			)
		);
		ConfiguredFeatures.register(featureRegisterable, FOREST_ROCK, Feature.FOREST_ROCK, new SingleStateFeatureConfig(Blocks.MOSSY_COBBLESTONE.getDefaultState()));
		ConfiguredFeatures.register(featureRegisterable, ICEBERG_PACKED, Feature.ICEBERG, new SingleStateFeatureConfig(Blocks.PACKED_ICE.getDefaultState()));
		ConfiguredFeatures.register(featureRegisterable, ICEBERG_BLUE, Feature.ICEBERG, new SingleStateFeatureConfig(Blocks.BLUE_ICE.getDefaultState()));
		ConfiguredFeatures.register(featureRegisterable, BLUE_ICE, Feature.BLUE_ICE);
		ConfiguredFeatures.register(
			featureRegisterable,
			LAKE_LAVA,
			Feature.LAKE,
			new LakeFeature.Config(BlockStateProvider.of(Blocks.LAVA.getDefaultState()), BlockStateProvider.of(Blocks.STONE.getDefaultState()))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			DISK_CLAY,
			Feature.DISK,
			new DiskFeatureConfig(
				PredicatedStateProvider.of(Blocks.CLAY), BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.CLAY)), UniformIntProvider.create(2, 3), 1
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			DISK_GRAVEL,
			Feature.DISK,
			new DiskFeatureConfig(
				PredicatedStateProvider.of(Blocks.GRAVEL), BlockPredicate.matchingBlocks(List.of(Blocks.DIRT, Blocks.GRASS_BLOCK)), UniformIntProvider.create(2, 5), 2
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			DISK_SAND,
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
		ConfiguredFeatures.register(featureRegisterable, FREEZE_TOP_LAYER, Feature.FREEZE_TOP_LAYER);
		ConfiguredFeatures.register(
			featureRegisterable,
			DISK_GRASS,
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
		ConfiguredFeatures.register(featureRegisterable, BONUS_CHEST, Feature.BONUS_CHEST);
		ConfiguredFeatures.register(featureRegisterable, VOID_START_PLATFORM, Feature.VOID_START_PLATFORM);
		ConfiguredFeatures.register(featureRegisterable, DESERT_WELL, Feature.DESERT_WELL);
		ConfiguredFeatures.register(
			featureRegisterable,
			SPRING_LAVA_OVERWORLD,
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
		ConfiguredFeatures.register(
			featureRegisterable,
			SPRING_LAVA_FROZEN,
			Feature.SPRING_FEATURE,
			new SpringFeatureConfig(
				Fluids.LAVA.getDefaultState(), true, 4, 1, RegistryEntryList.of(Block::getRegistryEntry, Blocks.SNOW_BLOCK, Blocks.POWDER_SNOW, Blocks.PACKED_ICE)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			SPRING_WATER,
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
}
