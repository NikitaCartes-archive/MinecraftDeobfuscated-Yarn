package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CountExtraDecoratorConfig implements DecoratorConfig {
	public static final Codec<CountExtraDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("count").forGetter(countExtraDecoratorConfig -> countExtraDecoratorConfig.count),
					Codec.FLOAT.fieldOf("extra_chance").forGetter(countExtraDecoratorConfig -> countExtraDecoratorConfig.extraChance),
					Codec.INT.fieldOf("extra_count").forGetter(countExtraDecoratorConfig -> countExtraDecoratorConfig.extraCount)
				)
				.apply(instance, CountExtraDecoratorConfig::new)
	);
	public final int count;
	public final float extraChance;
	public final int extraCount;

	public CountExtraDecoratorConfig(int count, float extraChance, int extraCount) {
		this.count = count;
		this.extraChance = extraChance;
		this.extraCount = extraCount;
	}
}
