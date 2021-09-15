package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class UnderwaterMagmaFeatureConfig implements FeatureConfig {
	public static final Codec<UnderwaterMagmaFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(0, 512).fieldOf("floor_search_range").forGetter(config -> config.floorSearchRange),
					Codec.intRange(0, 64).fieldOf("placement_radius_around_floor").forGetter(config -> config.placementRadiusAroundFloor),
					Codec.floatRange(0.0F, 1.0F).fieldOf("placement_probability_per_valid_position").forGetter(config -> config.placementProbabilityPerValidPosition)
				)
				.apply(instance, UnderwaterMagmaFeatureConfig::new)
	);
	public final int floorSearchRange;
	public final int placementRadiusAroundFloor;
	public final float placementProbabilityPerValidPosition;

	public UnderwaterMagmaFeatureConfig(int minDistanceBelowSurface, int floorSearchRange, float placementProbabilityPerValidPosition) {
		this.floorSearchRange = minDistanceBelowSurface;
		this.placementRadiusAroundFloor = floorSearchRange;
		this.placementProbabilityPerValidPosition = placementProbabilityPerValidPosition;
	}
}
