package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.class_5863;
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
					class_5863.method_33916(0.0F, 20.0F).fieldOf("height_scale").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.heightScale),
					Codec.floatRange(0.1F, 1.0F)
						.fieldOf("max_column_radius_to_cave_height_ratio")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.maxColumnRadiusToCaveHeightRatio),
					class_5863.method_33916(0.1F, 10.0F)
						.fieldOf("stalactite_bluntness")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalactiteBluntness),
					class_5863.method_33916(0.1F, 10.0F)
						.fieldOf("stalagmite_bluntness")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalagmiteBluntness),
					class_5863.method_33916(0.0F, 2.0F).fieldOf("wind_speed").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.windSpeed),
					Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.minRadiusForWind),
					Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.minBluntnessForWind)
				)
				.apply(instance, LargeDripstoneFeatureConfig::new)
	);
	public final int floorToCeilingSearchRange;
	public final UniformIntDistribution columnRadius;
	public final class_5863 heightScale;
	public final float maxColumnRadiusToCaveHeightRatio;
	public final class_5863 stalactiteBluntness;
	public final class_5863 stalagmiteBluntness;
	public final class_5863 windSpeed;
	public final int minRadiusForWind;
	public final float minBluntnessForWind;

	public LargeDripstoneFeatureConfig(
		int floorToCeilingSearchRange,
		UniformIntDistribution columnRadius,
		class_5863 heightScale,
		float maxColumnRadiusToCaveHeightRatio,
		class_5863 stalactiteBluntness,
		class_5863 stalagmiteBluntness,
		class_5863 windSpeed,
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
