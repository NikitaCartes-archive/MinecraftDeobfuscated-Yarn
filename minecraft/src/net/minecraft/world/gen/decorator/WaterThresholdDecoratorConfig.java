package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class WaterThresholdDecoratorConfig implements DecoratorConfig {
	public static final Codec<WaterThresholdDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(Codec.INT.fieldOf("max_water_depth").forGetter(waterThresholdDecoratorConfig -> waterThresholdDecoratorConfig.maxWaterDepth))
				.apply(instance, WaterThresholdDecoratorConfig::new)
	);
	public final int maxWaterDepth;

	public WaterThresholdDecoratorConfig(int maxWaterDepth) {
		this.maxWaterDepth = maxWaterDepth;
	}
}
