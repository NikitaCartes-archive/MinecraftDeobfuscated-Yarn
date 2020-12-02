package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.UniformFloatDistribution;
import net.minecraft.world.gen.UniformIntDistribution;

public class LargeDripstoneFeatureConfig implements FeatureConfig {
	public static final Codec<LargeDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(1, 512)
						.fieldOf("floor_to_ceiling_search_range")
						.orElse(30)
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.floorToCeilingSearchRange),
					UniformIntDistribution.createValidatedCodec(1, 30, 30)
						.fieldOf("column_radius")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.columnRadius),
					UniformFloatDistribution.createValidatedCodec(0.0F, 10.0F, 10.0F)
						.fieldOf("height_scale")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.heightScale),
					Codec.floatRange(0.1F, 1.0F)
						.fieldOf("max_column_radius_to_cave_height_ratio")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.maxColumnRadiusToCaveHeightRatio),
					UniformFloatDistribution.createValidatedCodec(0.1F, 5.0F, 5.0F)
						.fieldOf("stalactite_bluntness")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalactiteBluntness),
					UniformFloatDistribution.createValidatedCodec(0.1F, 5.0F, 5.0F)
						.fieldOf("stalagmite_bluntness")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalagmiteBluntness),
					UniformFloatDistribution.createValidatedCodec(0.0F, 1.0F, 1.0F)
						.fieldOf("wind_speed")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.windSpeed),
					Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.minRadiusForWind),
					Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.minBluntnessForWind)
				)
				.apply(instance, LargeDripstoneFeatureConfig::new)
	);
	public final int floorToCeilingSearchRange;
	public final UniformIntDistribution columnRadius;
	public final UniformFloatDistribution heightScale;
	public final float maxColumnRadiusToCaveHeightRatio;
	public final UniformFloatDistribution stalactiteBluntness;
	public final UniformFloatDistribution stalagmiteBluntness;
	public final UniformFloatDistribution windSpeed;
	public final int minRadiusForWind;
	public final float minBluntnessForWind;

	public LargeDripstoneFeatureConfig(
		int floorToCeilingSearchRange,
		UniformIntDistribution columnRadius,
		UniformFloatDistribution heightScale,
		float maxColumnRadiusToCaveHeightRatio,
		UniformFloatDistribution stalactiteBluntness,
		UniformFloatDistribution stalagmiteBluntness,
		UniformFloatDistribution windSpeed,
		int minRadiusForWind,
		float minBluntnessForWind
	) {
		this.floorToCeilingSearchRange = floorToCeilingSearchRange;
		this.columnRadius = columnRadius;
		this.heightScale = heightScale;
		this.maxColumnRadiusToCaveHeightRatio = maxColumnRadiusToCaveHeightRatio;
		this.stalactiteBluntness = stalactiteBluntness;
		this.stalagmiteBluntness = stalagmiteBluntness;
		this.windSpeed = windSpeed;
		this.minRadiusForWind = minRadiusForWind;
		this.minBluntnessForWind = minBluntnessForWind;
	}
}
