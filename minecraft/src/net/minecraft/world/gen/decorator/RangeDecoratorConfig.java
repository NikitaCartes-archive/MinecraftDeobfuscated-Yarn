package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class RangeDecoratorConfig implements DecoratorConfig {
	public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("count").forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.count),
					Codec.INT.fieldOf("bottom_offset").withDefault(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.bottomOffset),
					Codec.INT.fieldOf("top_offset").withDefault(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.topOffset),
					Codec.INT.fieldOf("maximum").withDefault(0).forGetter(rangeDecoratorConfig -> rangeDecoratorConfig.maximum)
				)
				.apply(instance, RangeDecoratorConfig::new)
	);
	public final int count;
	public final int bottomOffset;
	public final int topOffset;
	public final int maximum;

	public RangeDecoratorConfig(int count, int bottomOffset, int topOffset, int maximum) {
		this.count = count;
		this.bottomOffset = bottomOffset;
		this.topOffset = topOffset;
		this.maximum = maximum;
	}
}
