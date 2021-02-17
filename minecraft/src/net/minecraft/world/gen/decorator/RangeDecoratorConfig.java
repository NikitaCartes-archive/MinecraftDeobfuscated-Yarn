package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.YOffset;

public class RangeDecoratorConfig implements DecoratorConfig {
	public static final Codec<RangeDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					YOffset.OFFSET_CODEC.fieldOf("bottom_inclusive").forGetter(RangeDecoratorConfig::getBottom),
					YOffset.OFFSET_CODEC.fieldOf("top_inclusive").forGetter(RangeDecoratorConfig::getTop)
				)
				.apply(instance, RangeDecoratorConfig::new)
	);
	private final YOffset bottom;
	private final YOffset top;

	public RangeDecoratorConfig(YOffset bottom, YOffset top) {
		this.bottom = bottom;
		this.top = top;
	}

	public YOffset getBottom() {
		return this.bottom;
	}

	public YOffset getTop() {
		return this.top;
	}
}
