package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.UniformIntDistribution;
import net.minecraft.world.gen.YOffset;

public class RavineCarverConfig extends CarverConfig {
	public static final Codec<RavineCarverConfig> RAVINE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.floatRange(0.0F, 1.0F).fieldOf("probability").forGetter(ravineCarverConfig -> ravineCarverConfig.probability),
					CarverDebugConfig.CODEC.optionalFieldOf("debug_settings", CarverDebugConfig.DEFAULT).forGetter(CarverConfig::getDebugConfig),
					YOffset.OFFSET_CODEC.fieldOf("bottom_inclusive").forGetter(RavineCarverConfig::getBottom),
					YOffset.OFFSET_CODEC.fieldOf("top_inclusive").forGetter(RavineCarverConfig::getTop),
					UniformIntDistribution.CODEC.fieldOf("y_scale").forGetter(RavineCarverConfig::getYScale),
					FloatProvider.createValidatedCodec(0.0F, 1.0F).fieldOf("distance_factor").forGetter(RavineCarverConfig::getDistanceFactor),
					FloatProvider.VALUE_CODEC.fieldOf("vertical_rotation").forGetter(RavineCarverConfig::getVerticalRotation),
					FloatProvider.VALUE_CODEC.fieldOf("thickness").forGetter(RavineCarverConfig::getThickness),
					Codec.intRange(0, Integer.MAX_VALUE).fieldOf("width_smoothness").forGetter(RavineCarverConfig::getWidthSmoothness),
					FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_factor").forGetter(RavineCarverConfig::getHorizontalRadiusFactor),
					Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter(RavineCarverConfig::getVerticalRadiusDefaultFactor),
					Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter(RavineCarverConfig::getVerticalRadiusCenterFactor)
				)
				.apply(instance, RavineCarverConfig::new)
	);
	private final YOffset bottom;
	private final YOffset top;
	private final UniformIntDistribution yScale;
	private final FloatProvider distanceFactor;
	private final FloatProvider verticalRotation;
	private final FloatProvider thickness;
	private final int widthSmoothness;
	private final FloatProvider horizontalRadiusFactor;
	private final float verticalRadiusDefaultFactor;
	private final float verticalRadiusCenterFactor;

	public RavineCarverConfig(
		float probability,
		CarverDebugConfig debugConfig,
		YOffset bottom,
		YOffset top,
		UniformIntDistribution yScale,
		FloatProvider distanceFactor,
		FloatProvider verticalRotation,
		FloatProvider thickness,
		int widthSmoothness,
		FloatProvider horizontalRadiusFactor,
		float verticalRadiusDefaultFactor,
		float verticalRadiusCenterFactor
	) {
		super(probability, debugConfig);
		this.bottom = bottom;
		this.top = top;
		this.yScale = yScale;
		this.distanceFactor = distanceFactor;
		this.verticalRotation = verticalRotation;
		this.thickness = thickness;
		this.widthSmoothness = widthSmoothness;
		this.horizontalRadiusFactor = horizontalRadiusFactor;
		this.verticalRadiusDefaultFactor = verticalRadiusDefaultFactor;
		this.verticalRadiusCenterFactor = verticalRadiusCenterFactor;
	}

	public YOffset getBottom() {
		return this.bottom;
	}

	public YOffset getTop() {
		return this.top;
	}

	public UniformIntDistribution getYScale() {
		return this.yScale;
	}

	public FloatProvider getDistanceFactor() {
		return this.distanceFactor;
	}

	public FloatProvider getVerticalRotation() {
		return this.verticalRotation;
	}

	public FloatProvider getThickness() {
		return this.thickness;
	}

	public int getWidthSmoothness() {
		return this.widthSmoothness;
	}

	public FloatProvider getHorizontalRadiusFactor() {
		return this.horizontalRadiusFactor;
	}

	public float getVerticalRadiusDefaultFactor() {
		return this.verticalRadiusDefaultFactor;
	}

	public float getVerticalRadiusCenterFactor() {
		return this.verticalRadiusCenterFactor;
	}
}
