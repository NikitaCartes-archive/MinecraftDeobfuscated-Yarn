package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RangeDecoratorConfig implements DecoratorConfig {
	public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("bottom_offset").orElse(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.bottomOffset),
					Codec.INT.fieldOf("top_offset").orElse(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.topOffset),
					Codec.INT.fieldOf("maximum").orElse(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.maximum)
				)
				.apply(instance, RangeDecoratorConfig::new)
	);
	public final int bottomOffset;
	public final int topOffset;
	public final int maximum;

	public RangeDecoratorConfig(int bottomOffset, int topOffset, int maximum) {
		this.bottomOffset = bottomOffset;
		this.topOffset = topOffset;
		this.maximum = maximum;
	}
}
