package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.util.registry.RegistryEntryList;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class NetherConfiguredFeatures {
	public static final RegistryEntry<ConfiguredFeature<DeltaFeatureConfig, ?>> DELTA = ConfiguredFeatures.register(
		"delta",
		Feature.DELTA_FEATURE,
		new DeltaFeatureConfig(Blocks.LAVA.getDefaultState(), Blocks.MAGMA_BLOCK.getDefaultState(), UniformIntProvider.create(3, 7), UniformIntProvider.create(0, 2))
	);
	public static final RegistryEntry<ConfiguredFeature<BasaltColumnsFeatureConfig, ?>> SMALL_BASALT_COLUMNS = ConfiguredFeatures.register(
		"small_basalt_columns", Feature.BASALT_COLUMNS, new BasaltColumnsFeatureConfig(ConstantIntProvider.create(1), UniformIntProvider.create(1, 4))
	);
	public static final RegistryEntry<ConfiguredFeature<BasaltColumnsFeatureConfig, ?>> SMALL_BASALT_COLUMNS_TEMP = ConfiguredFeatures.register(
		"large_basalt_columns", Feature.BASALT_COLUMNS, new BasaltColumnsFeatureConfig(UniformIntProvider.create(2, 3), UniformIntProvider.create(5, 10))
	);
	public static final RegistryEntry<ConfiguredFeature<ReplaceBlobsFeatureConfig, ?>> BASALT_BLOBS = ConfiguredFeatures.register(
		"basalt_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS,
		new ReplaceBlobsFeatureConfig(Blocks.NETHERRACK.getDefaultState(), Blocks.BASALT.getDefaultState(), UniformIntProvider.create(3, 7))
	);
	public static final RegistryEntry<ConfiguredFeature<ReplaceBlobsFeatureConfig, ?>> BLACKSTONE_BLOBS = ConfiguredFeatures.register(
		"blackstone_blobs",
		Feature.NETHERRACK_REPLACE_BLOBS,
		new ReplaceBlobsFeatureConfig(Blocks.NETHERRACK.getDefaultState(), Blocks.BLACKSTONE.getDefaultState(), UniformIntProvider.create(3, 7))
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> GLOWSTONE_EXTRA = ConfiguredFeatures.register(
		"glowstone_extra", Feature.GLOWSTONE_BLOB
	);
	public static final WeightedBlockStateProvider CRIMSON_FOREST_VEGETATION_PROVIDER = new WeightedBlockStateProvider(
		DataPool.<BlockState>builder()
			.add(Blocks.CRIMSON_ROOTS.getDefaultState(), 87)
			.add(Blocks.CRIMSON_FUNGUS.getDefaultState(), 11)
			.add(Blocks.WARPED_FUNGUS.getDefaultState(), 1)
	);
	public static final RegistryEntry<ConfiguredFeature<NetherForestVegetationFeatureConfig, ?>> CRIMSON_FOREST_VEGETATION = ConfiguredFeatures.register(
		"crimson_forest_vegetation", Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(CRIMSON_FOREST_VEGETATION_PROVIDER, 8, 4)
	);
	public static final RegistryEntry<ConfiguredFeature<NetherForestVegetationFeatureConfig, ?>> CRIMSON_FOREST_VEGETATION_BONEMEAL = ConfiguredFeatures.register(
		"crimson_forest_vegetation_bonemeal", Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(CRIMSON_FOREST_VEGETATION_PROVIDER, 3, 1)
	);
	public static final WeightedBlockStateProvider WARPED_FOREST_VEGETATION_PROVIDER = new WeightedBlockStateProvider(
		DataPool.<BlockState>builder()
			.add(Blocks.WARPED_ROOTS.getDefaultState(), 85)
			.add(Blocks.CRIMSON_ROOTS.getDefaultState(), 1)
			.add(Blocks.WARPED_FUNGUS.getDefaultState(), 13)
			.add(Blocks.CRIMSON_FUNGUS.getDefaultState(), 1)
	);
	public static final RegistryEntry<ConfiguredFeature<NetherForestVegetationFeatureConfig, ?>> WARPED_FOREST_VEGETATION = ConfiguredFeatures.register(
		"warped_forest_vegetation", Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(WARPED_FOREST_VEGETATION_PROVIDER, 8, 4)
	);
	public static final RegistryEntry<ConfiguredFeature<NetherForestVegetationFeatureConfig, ?>> WARPED_FOREST_VEGETATION_BONEMEAL = ConfiguredFeatures.register(
		"warped_forest_vegetation_bonemeal", Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(WARPED_FOREST_VEGETATION_PROVIDER, 3, 1)
	);
	public static final RegistryEntry<ConfiguredFeature<NetherForestVegetationFeatureConfig, ?>> NETHER_SPROUTS = ConfiguredFeatures.register(
		"nether_sprouts", Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(BlockStateProvider.of(Blocks.NETHER_SPROUTS), 8, 4)
	);
	public static final RegistryEntry<ConfiguredFeature<NetherForestVegetationFeatureConfig, ?>> NETHER_SPROUTS_BONEMEAL = ConfiguredFeatures.register(
		"nether_sprouts_bonemeal", Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(BlockStateProvider.of(Blocks.NETHER_SPROUTS), 3, 1)
	);
	public static final RegistryEntry<ConfiguredFeature<TwistingVinesFeatureConfig, ?>> TWISTING_VINES = ConfiguredFeatures.register(
		"twisting_vines", Feature.TWISTING_VINES, new TwistingVinesFeatureConfig(8, 4, 8)
	);
	public static final RegistryEntry<ConfiguredFeature<TwistingVinesFeatureConfig, ?>> TWISTING_VINES_BONEMEAL = ConfiguredFeatures.register(
		"twisting_vines_bonemeal", Feature.TWISTING_VINES, new TwistingVinesFeatureConfig(3, 1, 2)
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> WEEPING_VINES = ConfiguredFeatures.register(
		"weeping_vines", Feature.WEEPING_VINES
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_CRIMSON_ROOTS = ConfiguredFeatures.register(
		"patch_crimson_roots",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.CRIMSON_ROOTS)))
	);
	public static final RegistryEntry<ConfiguredFeature<DefaultFeatureConfig, ?>> BASALT_PILLAR = ConfiguredFeatures.register(
		"basalt_pillar", Feature.BASALT_PILLAR
	);
	public static final RegistryEntry<ConfiguredFeature<SpringFeatureConfig, ?>> SPRING_LAVA_NETHER = ConfiguredFeatures.register(
		"spring_lava_nether",
		Feature.SPRING_FEATURE,
		new SpringFeatureConfig(
			Fluids.LAVA.getDefaultState(),
			true,
			4,
			1,
			RegistryEntryList.of(Block::getRegistryEntry, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<SpringFeatureConfig, ?>> SPRING_NETHER_CLOSED = ConfiguredFeatures.register(
		"spring_nether_closed",
		Feature.SPRING_FEATURE,
		new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), false, 5, 0, RegistryEntryList.of(Block::getRegistryEntry, Blocks.NETHERRACK))
	);
	public static final RegistryEntry<ConfiguredFeature<SpringFeatureConfig, ?>> SPRING_NETHER_OPEN = ConfiguredFeatures.register(
		"spring_nether_open",
		Feature.SPRING_FEATURE,
		new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), false, 4, 1, RegistryEntryList.of(Block::getRegistryEntry, Blocks.NETHERRACK))
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_FIRE = ConfiguredFeatures.register(
		"patch_fire",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(
			Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.FIRE)), List.of(Blocks.NETHERRACK)
		)
	);
	public static final RegistryEntry<ConfiguredFeature<RandomPatchFeatureConfig, ?>> PATCH_SOUL_FIRE = ConfiguredFeatures.register(
		"patch_soul_fire",
		Feature.RANDOM_PATCH,
		ConfiguredFeatures.createRandomPatchFeatureConfig(
			Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SOUL_FIRE)), List.of(Blocks.SOUL_SOIL)
		)
	);
}
