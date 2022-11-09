package net.minecraft.world.gen.carver;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.block.Block;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.util.dynamic.Codecs;
import net.minecraft.util.math.floatprovider.FloatProvider;
import net.minecraft.world.gen.YOffset;
import net.minecraft.world.gen.heightprovider.HeightProvider;

public class RavineCarverConfig extends CarverConfig {
	public static final Codec<RavineCarverConfig> RAVINE_CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					CarverConfig.CONFIG_CODEC.forGetter(ravineCarverConfig -> ravineCarverConfig),
					FloatProvider.VALUE_CODEC.fieldOf("vertical_rotation").forGetter(config -> config.verticalRotation),
					RavineCarverConfig.Shape.CODEC.fieldOf("shape").forGetter(config -> config.shape)
				)
				.apply(instance, RavineCarverConfig::new)
	);
	public final FloatProvider verticalRotation;
	public final RavineCarverConfig.Shape shape;

	public RavineCarverConfig(
		float probability,
		HeightProvider y,
		FloatProvider yScale,
		YOffset lavaLevel,
		CarverDebugConfig debugConfig,
		RegistryEntryList<Block> replaceable,
		FloatProvider verticalRotation,
		RavineCarverConfig.Shape shape
	) {
		super(probability, y, yScale, lavaLevel, debugConfig, replaceable);
		this.verticalRotation = verticalRotation;
		this.shape = shape;
	}

	public RavineCarverConfig(CarverConfig config, FloatProvider verticalRotation, RavineCarverConfig.Shape shape) {
		this(config.probability, config.y, config.yScale, config.lavaLevel, config.debugConfig, config.replaceable, verticalRotation, shape);
	}

	public static class Shape {
		public static final Codec<RavineCarverConfig.Shape> CODEC = RecordCodecBuilder.create(
			instance -> instance.group(
						FloatProvider.VALUE_CODEC.fieldOf("distance_factor").forGetter(shape -> shape.distanceFactor),
						FloatProvider.VALUE_CODEC.fieldOf("thickness").forGetter(shape -> shape.thickness),
						Codecs.NONNEGATIVE_INT.fieldOf("width_smoothness").forGetter(shape -> shape.widthSmoothness),
						FloatProvider.VALUE_CODEC.fieldOf("horizontal_radius_factor").forGetter(shape -> shape.horizontalRadiusFactor),
						Codec.FLOAT.fieldOf("vertical_radius_default_factor").forGetter(shape -> shape.verticalRadiusDefaultFactor),
						Codec.FLOAT.fieldOf("vertical_radius_center_factor").forGetter(shape -> shape.verticalRadiusCenterFactor)
					)
					.apply(instance, RavineCarverConfig.Shape::new)
		);
		public final FloatProvider distanceFactor;
		public final FloatProvider thickness;
		public final int widthSmoothness;
		public final FloatProvider horizontalRadiusFactor;
		public final float verticalRadiusDefaultFactor;
		public final float verticalRadiusCenterFactor;

		public Shape(
			FloatProvider distanceFactor,
			FloatProvider thickness,
			int widthSmoothness,
			FloatProvider horizontalRadiusFactor,
			float verticalRadiusDefaultFactor,
			float verticalRadiusCenterFactor
		) {
			this.widthSmoothness = widthSmoothness;
			this.horizontalRadiusFactor = horizontalRadiusFactor;
			this.verticalRadiusDefaultFactor = verticalRadiusDefaultFactor;
			this.verticalRadiusCenterFactor = verticalRadiusCenterFactor;
			this.distanceFactor = distanceFactor;
			this.thickness = thickness;
		}
	}
}
