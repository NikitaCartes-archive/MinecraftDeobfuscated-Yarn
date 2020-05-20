package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class ChanceRangeDecoratorConfig implements DecoratorConfig {
	public static final Codec<ChanceRangeDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.FLOAT.fieldOf("chance").forGetter(chanceRangeDecoratorConfig -> chanceRangeDecoratorConfig.chance),
					Codec.INT.fieldOf("bottom_offset").withDefault(0).forGetter(chanceRangeDecoratorConfig -> chanceRangeDecoratorConfig.bottomOffset),
					Codec.INT.fieldOf("top_offset").withDefault(0).forGetter(chanceRangeDecoratorConfig -> chanceRangeDecoratorConfig.topOffset),
					Codec.INT.fieldOf("top").withDefault(0).forGetter(chanceRangeDecoratorConfig -> chanceRangeDecoratorConfig.top)
				)
				.apply(instance, ChanceRangeDecoratorConfig::new)
	);
	public final float chance;
	public final int bottomOffset;
	public final int topOffset;
	public final int top;

	public ChanceRangeDecoratorConfig(float chance, int bottomOffset, int topOffset, int top) {
		this.chance = chance;
		this.bottomOffset = bottomOffset;
		this.topOffset = topOffset;
		this.top = top;
	}
}
