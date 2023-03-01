package net.minecraft.world.gen.feature;

import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.placementmodifier.AbstractConditionalPlacementModifier;
import net.minecraft.world.gen.placementmodifier.BlockFilterPlacementModifier;
import net.minecraft.world.gen.placementmodifier.CountPlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightRangePlacementModifier;
import net.minecraft.world.gen.placementmodifier.HeightmapPlacementModifier;
import net.minecraft.world.gen.placementmodifier.PlacementModifier;

public class PlacedFeatures {
	public static final PlacementModifier MOTION_BLOCKING_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING);
	public static final PlacementModifier OCEAN_FLOOR_WG_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG);
	public static final PlacementModifier WORLD_SURFACE_WG_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG);
	public static final PlacementModifier OCEAN_FLOOR_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR);
	public static final PlacementModifier BOTTOM_TO_TOP_RANGE = HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop());
	public static final PlacementModifier TEN_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(10), YOffset.belowTop(10));
	public static final PlacementModifier EIGHT_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(8), YOffset.belowTop(8));
	public static final PlacementModifier FOUR_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(4), YOffset.belowTop(4));
	public static final PlacementModifier BOTTOM_TO_120_RANGE = HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(256));

	public static void bootstrap(Registerable<PlacedFeature> featureRegisterable) {
		OceanPlacedFeatures.bootstrap(featureRegisterable);
		UndergroundPlacedFeatures.bootstrap(featureRegisterable);
		EndPlacedFeatures.bootstrap(featureRegisterable);
		MiscPlacedFeatures.bootstrap(featureRegisterable);
		NetherPlacedFeatures.bootstrap(featureRegisterable);
		OrePlacedFeatures.bootstrap(featureRegisterable);
		TreePlacedFeatures.bootstrap(featureRegisterable);
		VegetationPlacedFeatures.bootstrap(featureRegisterable);
		VillagePlacedFeatures.bootstrap(featureRegisterable);
	}

	public static RegistryKey<PlacedFeature> of(String id) {
		return RegistryKey.of(RegistryKeys.PLACED_FEATURE, new Identifier(id));
	}

	public static void register(
		Registerable<PlacedFeature> featureRegisterable,
		RegistryKey<PlacedFeature> key,
		RegistryEntry<ConfiguredFeature<?, ?>> feature,
		List<PlacementModifier> modifiers
	) {
		featureRegisterable.register(key, new PlacedFeature(feature, List.copyOf(modifiers)));
	}

	public static void register(
		Registerable<PlacedFeature> featureRegisterable,
		RegistryKey<PlacedFeature> key,
		RegistryEntry<ConfiguredFeature<?, ?>> feature,
		PlacementModifier... modifiers
	) {
		register(featureRegisterable, key, feature, List.of(modifiers));
	}

	public static PlacementModifier createCountExtraModifier(int count, float extraChance, int extraCount) {
		float f = 1.0F / extraChance;
		if (Math.abs(f - (float)((int)f)) > 1.0E-5F) {
			throw new IllegalStateException("Chance data cannot be represented as list weight");
		} else {
			DataPool<IntProvider> dataPool = DataPool.<IntProvider>builder()
				.add(ConstantIntProvider.create(count), (int)f - 1)
				.add(ConstantIntProvider.create(count + extraCount), 1)
				.build();
			return CountPlacementModifier.of(new WeightedListIntProvider(dataPool));
		}
	}

	public static AbstractConditionalPlacementModifier isAir() {
		return BlockFilterPlacementModifier.of(BlockPredicate.IS_AIR);
	}

	public static BlockFilterPlacementModifier wouldSurvive(Block block) {
		return BlockFilterPlacementModifier.of(BlockPredicate.wouldSurvive(block.getDefaultState(), BlockPos.ORIGIN));
	}

	public static RegistryEntry<PlacedFeature> createEntry(RegistryEntry<ConfiguredFeature<?, ?>> feature, PlacementModifier... modifiers) {
		return RegistryEntry.of(new PlacedFeature(feature, List.of(modifiers)));
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<PlacedFeature> createEntry(
		F feature, FC featureConfig, PlacementModifier... modifiers
	) {
		return createEntry(RegistryEntry.of(new ConfiguredFeature(feature, featureConfig)), modifiers);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<PlacedFeature> createEntry(F feature, FC featureConfig) {
		return createEntry(feature, featureConfig, BlockPredicate.IS_AIR);
	}

	public static <FC extends FeatureConfig, F extends Feature<FC>> RegistryEntry<PlacedFeature> createEntry(F feature, FC featureConfig, BlockPredicate predicate) {
		return createEntry(feature, featureConfig, BlockFilterPlacementModifier.of(predicate));
	}
}
