package net.minecraft.world.gen.feature;

import java.util.Random;
import net.minecraft.util.Util;
import net.minecraft.util.collection.DataPool;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.util.math.intprovider.WeightedListIntProvider;
import net.minecraft.util.registry.BuiltinRegistries;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Heightmap;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.decorator.CountPlacementModifier;
import net.minecraft.world.gen.decorator.HeightRangePlacementModifier;
import net.minecraft.world.gen.decorator.HeightmapPlacementModifier;
import net.minecraft.world.gen.decorator.PlacementModifier;

public class PlacedFeatures {
	public static final PlacementModifier MOTION_BLOCKING_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.MOTION_BLOCKING);
	public static final PlacementModifier OCEAN_FLOOR_WG_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR_WG);
	public static final PlacementModifier WORLD_SURFACE_WG_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.WORLD_SURFACE_WG);
	public static final PlacementModifier OCEAN_FLOOR_HEIGHTMAP = HeightmapPlacementModifier.of(Heightmap.Type.OCEAN_FLOOR);
	public static final PlacementModifier BOTTOM_TO_TOP_RANGE = HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.getTop());
	public static final PlacementModifier TEN_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(10), YOffset.belowTop(10));
	public static final PlacementModifier EIGHT_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(8), YOffset.belowTop(8));
	public static final PlacementModifier FOUR_ABOVE_AND_BELOW_RANGE = HeightRangePlacementModifier.uniform(YOffset.aboveBottom(4), YOffset.belowTop(4));
	public static final PlacementModifier BOTTOM_TO_120_RANGE = HeightRangePlacementModifier.uniform(YOffset.getBottom(), YOffset.fixed(120));

	public static PlacedFeature getDefaultPlacedFeature() {
		PlacedFeature[] placedFeatures = new PlacedFeature[]{
			OceanPlacedFeatures.KELP_COLD,
			UndergroundPlacedFeatures.CAVE_VINES,
			EndPlacedFeatures.CHORUS_PLANT,
			MiscPlacedFeatures.BLUE_ICE,
			NetherPlacedFeatures.BASALT_BLOBS,
			OrePlacedFeatures.ORE_ANCIENT_DEBRIS_LARGE,
			TreePlacedFeatures.ACACIA_CHECKED,
			VegetationPlacedFeatures.BAMBOO_VEGETATION,
			VillagePlacedFeatures.PILE_HAY
		};
		return Util.getRandom(placedFeatures, new Random());
	}

	public static PlacedFeature register(String id, PlacedFeature feature) {
		return Registry.register(BuiltinRegistries.PLACED_FEATURE, id, feature);
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
}
