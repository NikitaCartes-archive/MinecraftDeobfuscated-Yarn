package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class DepthAverageDecoratorConfig implements DecoratorConfig {
	public static final Codec<DepthAverageDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("baseline").forGetter(depthAverageDecoratorConfig -> depthAverageDecoratorConfig.baseline),
					Codec.INT.fieldOf("spread").forGetter(depthAverageDecoratorConfig -> depthAverageDecoratorConfig.spread)
				)
				.apply(instance, DepthAverageDecoratorConfig::new)
	);
	public final int baseline;
	public final int spread;

	public DepthAverageDecoratorConfig(int baseline, int spread) {
		this.baseline = baseline;
		this.spread = spread;
	}
}
