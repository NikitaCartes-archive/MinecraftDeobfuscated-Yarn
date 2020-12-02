package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.UniformFloatDistribution;
import net.minecraft.world.gen.UniformIntDistribution;

public class DripstoneClusterFeatureConfig implements FeatureConfig {
	public static final Codec<DripstoneClusterFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(1, 512)
						.fieldOf("floor_to_ceiling_search_range")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.floorToCeilingSearchRange),
					UniformIntDistribution.createValidatedCodec(1, 64, 64).fieldOf("height").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.height),
					UniformIntDistribution.createValidatedCodec(1, 64, 64).fieldOf("radius").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.radius),
					Codec.intRange(0, 64)
						.fieldOf("max_stalagmite_stalactite_height_diff")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.maxStalagmiteStalactiteHeightDiff),
					Codec.intRange(1, 64).fieldOf("height_deviation").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.heightDeviation),
					UniformIntDistribution.createValidatedCodec(0, 64, 64)
						.fieldOf("dripstone_block_layer_thickness")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.dripstoneBlockLayerThickness),
					UniformFloatDistribution.createValidatedCodec(0.0F, 1.0F, 1.0F)
						.fieldOf("density")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.density),
					UniformFloatDistribution.createValidatedCodec(0.0F, 1.0F, 1.0F)
						.fieldOf("wetness")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.wetness),
					Codec.floatRange(0.0F, 1.0F).fieldOf("wetness_mean").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.wetnessMean),
					Codec.floatRange(0.0F, 1.0F).fieldOf("wetness_deviation").forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.wetnessDeviation),
					Codec.floatRange(0.0F, 1.0F)
						.fieldOf("chance_of_dripstone_column_at_max_distance_from_center")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.chanceOfDripstoneColumnAtMaxDistanceFromCenter),
					Codec.intRange(1, 64)
						.fieldOf("max_distance_from_center_affecting_chance_of_dripstone_column")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.maxDistanceFromCenterAffectingChanceOfDripstoneColumn),
					Codec.intRange(1, 64)
						.fieldOf("max_distance_from_center_affecting_height_bias")
						.forGetter(dripstoneClusterFeatureConfig -> dripstoneClusterFeatureConfig.maxDistanceFromCenterAffectingHeightBias)
				)
				.apply(instance, DripstoneClusterFeatureConfig::new)
	);
	public final int floorToCeilingSearchRange;
	public final UniformIntDistribution height;
	public final UniformIntDistribution radius;
	public final int maxStalagmiteStalactiteHeightDiff;
	public final int heightDeviation;
	public final UniformIntDistribution dripstoneBlockLayerThickness;
	public final UniformFloatDistribution density;
	public final UniformFloatDistribution wetness;
	public final float wetnessMean;
	public final float wetnessDeviation;
	public final float chanceOfDripstoneColumnAtMaxDistanceFromCenter;
	public final int maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
	public final int maxDistanceFromCenterAffectingHeightBias;

	public DripstoneClusterFeatureConfig(
		int floorToCeilingSearchRange,
		UniformIntDistribution height,
		UniformIntDistribution radius,
		int maxStalagmiteStalactiteHeightDiff,
		int heightDeviation,
		UniformIntDistribution dripstoneBlockLayerThickness,
		UniformFloatDistribution density,
		UniformFloatDistribution wetness,
		float wetnessMean,
		float wetnessDeviation,
		float chanceOfDripstoneColumnAtMaxDistanceFromCenter,
		int maxDistanceFromCenterAffectingChanceOfDripstoneColumn,
		int maxDistanceFromCenterAffectingHeightBias
	) {
		this.floorToCeilingSearchRange = floorToCeilingSearchRange;
		this.height = height;
		this.radius = radius;
		this.maxStalagmiteStalactiteHeightDiff = maxStalagmiteStalactiteHeightDiff;
		this.heightDeviation = heightDeviation;
		this.dripstoneBlockLayerThickness = dripstoneBlockLayerThickness;
		this.density = density;
		this.wetness = wetness;
		this.wetnessMean = wetnessMean;
		this.wetnessDeviation = wetnessDeviation;
		this.chanceOfDripstoneColumnAtMaxDistanceFromCenter = chanceOfDripstoneColumnAtMaxDistanceFromCenter;
		this.maxDistanceFromCenterAffectingChanceOfDripstoneColumn = maxDistanceFromCenterAffectingChanceOfDripstoneColumn;
		this.maxDistanceFromCenterAffectingHeightBias = maxDistanceFromCenterAffectingHeightBias;
	}
}
