package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WaterDepthThresholdDecoratorConfig implements DecoratorConfig {
	public static final Codec<WaterDepthThresholdDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("max_water_depth").forGetter(waterDepthThresholdDecoratorConfig -> waterDepthThresholdDecoratorConfig.maxWaterDepth)
				)
				.apply(instance, WaterDepthThresholdDecoratorConfig::new)
	);
	public final int maxWaterDepth;

	public WaterDepthThresholdDecoratorConfig(int maxWaterDepth) {
		this.maxWaterDepth = maxWaterDepth;
	}
}
