package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.util.math.intprovider.IntProvider;

public class LargeDripstoneFeatureConfig implements FeatureConfig {
	public static final Codec<LargeDripstoneFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.intRange(1, 512)
						.fieldOf("floor_to_ceiling_search_range")
						.orElse(30)
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.floorToCeilingSearchRange),
					IntProvider.createValidatingCodec(1, 60).fieldOf("column_radius").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.columnRadius),
					FloatProvider.createValidatedCodec(0.0F, 20.0F).fieldOf("height_scale").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.heightScale),
					Codec.floatRange(0.1F, 1.0F)
						.fieldOf("max_column_radius_to_cave_height_ratio")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.maxColumnRadiusToCaveHeightRatio),
					FloatProvider.createValidatedCodec(0.1F, 10.0F)
						.fieldOf("stalactite_bluntness")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalactiteBluntness),
					FloatProvider.createValidatedCodec(0.1F, 10.0F)
						.fieldOf("stalagmite_bluntness")
						.forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.stalagmiteBluntness),
					FloatProvider.createValidatedCodec(0.0F, 2.0F).fieldOf("wind_speed").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.windSpeed),
					Codec.intRange(0, 100).fieldOf("min_radius_for_wind").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.minRadiusForWind),
					Codec.floatRange(0.0F, 5.0F).fieldOf("min_bluntness_for_wind").forGetter(largeDripstoneFeatureConfig -> largeDripstoneFeatureConfig.minBluntnessForWind)
				)
				.apply(instance, LargeDripstoneFeatureConfig::new)
	);
	public final int floorToCeilingSearchRange;
	public final IntProvider columnRadius;
	public final FloatProvider heightScale;
	public final float maxColumnRadiusToCaveHeightRatio;
	public final FloatProvider stalactiteBluntness;
	public final FloatProvider stalagmiteBluntness;
	public final FloatProvider windSpeed;
	public final int minRadiusForWind;
	public final float minBluntnessForWind;

	public LargeDripstoneFeatureConfig(
		int floorToCeilingSearchRange,
		IntProvider columnRadius,
		FloatProvider heightScale,
		float maxColumnRadiusToCaveHeightRatio,
		FloatProvider stalactiteBluntness,
		FloatProvider stalagmiteBluntness,
		FloatProvider windSpeed,
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
