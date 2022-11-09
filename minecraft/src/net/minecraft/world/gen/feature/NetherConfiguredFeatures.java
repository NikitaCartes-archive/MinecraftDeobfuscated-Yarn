package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.UniformIntProvider;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;
import net.minecraft.world.gen.stateprovider.WeightedBlockStateProvider;

public class NetherConfiguredFeatures {
	public static final RegistryKey<ConfiguredFeature<?, ?>> DELTA = ConfiguredFeatures.of("delta");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SMALL_BASALT_COLUMNS = ConfiguredFeatures.of("small_basalt_columns");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SMALL_BASALT_COLUMNS_TEMP = ConfiguredFeatures.of("large_basalt_columns");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BASALT_BLOBS = ConfiguredFeatures.of("basalt_blobs");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BLACKSTONE_BLOBS = ConfiguredFeatures.of("blackstone_blobs");
	public static final RegistryKey<ConfiguredFeature<?, ?>> GLOWSTONE_EXTRA = ConfiguredFeatures.of("glowstone_extra");
	public static final RegistryKey<ConfiguredFeature<?, ?>> CRIMSON_FOREST_VEGETATION = ConfiguredFeatures.of("crimson_forest_vegetation");
	public static final RegistryKey<ConfiguredFeature<?, ?>> CRIMSON_FOREST_VEGETATION_BONEMEAL = ConfiguredFeatures.of("crimson_forest_vegetation_bonemeal");
	public static final RegistryKey<ConfiguredFeature<?, ?>> WARPED_FOREST_VEGETATION = ConfiguredFeatures.of("warped_forest_vegetation");
	public static final RegistryKey<ConfiguredFeature<?, ?>> WARPED_FOREST_VEGETATION_BONEMEAL = ConfiguredFeatures.of("warped_forest_vegetation_bonemeal");
	public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_SPROUTS = ConfiguredFeatures.of("nether_sprouts");
	public static final RegistryKey<ConfiguredFeature<?, ?>> NETHER_SPROUTS_BONEMEAL = ConfiguredFeatures.of("nether_sprouts_bonemeal");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TWISTING_VINES = ConfiguredFeatures.of("twisting_vines");
	public static final RegistryKey<ConfiguredFeature<?, ?>> TWISTING_VINES_BONEMEAL = ConfiguredFeatures.of("twisting_vines_bonemeal");
	public static final RegistryKey<ConfiguredFeature<?, ?>> WEEPING_VINES = ConfiguredFeatures.of("weeping_vines");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_CRIMSON_ROOTS = ConfiguredFeatures.of("patch_crimson_roots");
	public static final RegistryKey<ConfiguredFeature<?, ?>> BASALT_PILLAR = ConfiguredFeatures.of("basalt_pillar");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SPRING_LAVA_NETHER = ConfiguredFeatures.of("spring_lava_nether");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SPRING_NETHER_CLOSED = ConfiguredFeatures.of("spring_nether_closed");
	public static final RegistryKey<ConfiguredFeature<?, ?>> SPRING_NETHER_OPEN = ConfiguredFeatures.of("spring_nether_open");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_FIRE = ConfiguredFeatures.of("patch_fire");
	public static final RegistryKey<ConfiguredFeature<?, ?>> PATCH_SOUL_FIRE = ConfiguredFeatures.of("patch_soul_fire");

	public static void bootstrap(Registerable<ConfiguredFeature<?, ?>> featureRegisterable) {
		ConfiguredFeatures.register(
			featureRegisterable,
			DELTA,
			Feature.DELTA_FEATURE,
			new DeltaFeatureConfig(Blocks.LAVA.getDefaultState(), Blocks.MAGMA_BLOCK.getDefaultState(), UniformIntProvider.create(3, 7), UniformIntProvider.create(0, 2))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			SMALL_BASALT_COLUMNS,
			Feature.BASALT_COLUMNS,
			new BasaltColumnsFeatureConfig(ConstantIntProvider.create(1), UniformIntProvider.create(1, 4))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			SMALL_BASALT_COLUMNS_TEMP,
			Feature.BASALT_COLUMNS,
			new BasaltColumnsFeatureConfig(UniformIntProvider.create(2, 3), UniformIntProvider.create(5, 10))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			BASALT_BLOBS,
			Feature.NETHERRACK_REPLACE_BLOBS,
			new ReplaceBlobsFeatureConfig(Blocks.NETHERRACK.getDefaultState(), Blocks.BASALT.getDefaultState(), UniformIntProvider.create(3, 7))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			BLACKSTONE_BLOBS,
			Feature.NETHERRACK_REPLACE_BLOBS,
			new ReplaceBlobsFeatureConfig(Blocks.NETHERRACK.getDefaultState(), Blocks.BLACKSTONE.getDefaultState(), UniformIntProvider.create(3, 7))
		);
		ConfiguredFeatures.register(featureRegisterable, GLOWSTONE_EXTRA, Feature.GLOWSTONE_BLOB);
		WeightedBlockStateProvider weightedBlockStateProvider = new WeightedBlockStateProvider(
			DataPool.<BlockState>builder()
				.add(Blocks.CRIMSON_ROOTS.getDefaultState(), 87)
				.add(Blocks.CRIMSON_FUNGUS.getDefaultState(), 11)
				.add(Blocks.WARPED_FUNGUS.getDefaultState(), 1)
		);
		ConfiguredFeatures.register(
			featureRegisterable, CRIMSON_FOREST_VEGETATION, Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(weightedBlockStateProvider, 8, 4)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			CRIMSON_FOREST_VEGETATION_BONEMEAL,
			Feature.NETHER_FOREST_VEGETATION,
			new NetherForestVegetationFeatureConfig(weightedBlockStateProvider, 3, 1)
		);
		WeightedBlockStateProvider weightedBlockStateProvider2 = new WeightedBlockStateProvider(
			DataPool.<BlockState>builder()
				.add(Blocks.WARPED_ROOTS.getDefaultState(), 85)
				.add(Blocks.CRIMSON_ROOTS.getDefaultState(), 1)
				.add(Blocks.WARPED_FUNGUS.getDefaultState(), 13)
				.add(Blocks.CRIMSON_FUNGUS.getDefaultState(), 1)
		);
		ConfiguredFeatures.register(
			featureRegisterable, WARPED_FOREST_VEGETATION, Feature.NETHER_FOREST_VEGETATION, new NetherForestVegetationFeatureConfig(weightedBlockStateProvider2, 8, 4)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			WARPED_FOREST_VEGETATION_BONEMEAL,
			Feature.NETHER_FOREST_VEGETATION,
			new NetherForestVegetationFeatureConfig(weightedBlockStateProvider2, 3, 1)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			NETHER_SPROUTS,
			Feature.NETHER_FOREST_VEGETATION,
			new NetherForestVegetationFeatureConfig(BlockStateProvider.of(Blocks.NETHER_SPROUTS), 8, 4)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			NETHER_SPROUTS_BONEMEAL,
			Feature.NETHER_FOREST_VEGETATION,
			new NetherForestVegetationFeatureConfig(BlockStateProvider.of(Blocks.NETHER_SPROUTS), 3, 1)
		);
		ConfiguredFeatures.register(featureRegisterable, TWISTING_VINES, Feature.TWISTING_VINES, new TwistingVinesFeatureConfig(8, 4, 8));
		ConfiguredFeatures.register(featureRegisterable, TWISTING_VINES_BONEMEAL, Feature.TWISTING_VINES, new TwistingVinesFeatureConfig(3, 1, 2));
		ConfiguredFeatures.register(featureRegisterable, WEEPING_VINES, Feature.WEEPING_VINES);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_CRIMSON_ROOTS,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.CRIMSON_ROOTS)))
		);
		ConfiguredFeatures.register(featureRegisterable, BASALT_PILLAR, Feature.BASALT_PILLAR);
		ConfiguredFeatures.register(
			featureRegisterable,
			SPRING_LAVA_NETHER,
			Feature.SPRING_FEATURE,
			new SpringFeatureConfig(
				Fluids.LAVA.getDefaultState(),
				true,
				4,
				1,
				RegistryEntryList.of(Block::getRegistryEntry, Blocks.NETHERRACK, Blocks.SOUL_SAND, Blocks.GRAVEL, Blocks.MAGMA_BLOCK, Blocks.BLACKSTONE)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			SPRING_NETHER_CLOSED,
			Feature.SPRING_FEATURE,
			new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), false, 5, 0, RegistryEntryList.of(Block::getRegistryEntry, Blocks.NETHERRACK))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			SPRING_NETHER_OPEN,
			Feature.SPRING_FEATURE,
			new SpringFeatureConfig(Fluids.LAVA.getDefaultState(), false, 4, 1, RegistryEntryList.of(Block::getRegistryEntry, Blocks.NETHERRACK))
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_FIRE,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.FIRE)), List.of(Blocks.NETHERRACK)
			)
		);
		ConfiguredFeatures.register(
			featureRegisterable,
			PATCH_SOUL_FIRE,
			Feature.RANDOM_PATCH,
			ConfiguredFeatures.createRandomPatchFeatureConfig(
				Feature.SIMPLE_BLOCK, new SimpleBlockFeatureConfig(BlockStateProvider.of(Blocks.SOUL_FIRE)), List.of(Blocks.SOUL_SOIL)
			)
		);
	}
}
