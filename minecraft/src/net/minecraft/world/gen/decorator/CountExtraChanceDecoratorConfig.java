package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CountExtraChanceDecoratorConfig implements DecoratorConfig {
	public static final Codec<CountExtraChanceDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("count").forGetter(countExtraChanceDecoratorConfig -> countExtraChanceDecoratorConfig.count),
					Codec.FLOAT.fieldOf("extra_chance").forGetter(countExtraChanceDecoratorConfig -> countExtraChanceDecoratorConfig.extraChance),
					Codec.INT.fieldOf("extra_count").forGetter(countExtraChanceDecoratorConfig -> countExtraChanceDecoratorConfig.extraCount)
				)
				.apply(instance, CountExtraChanceDecoratorConfig::new)
	);
	public final int count;
	public final float extraChance;
	public final int extraCount;

	public CountExtraChanceDecoratorConfig(int count, float extraChance, int extraCount) {
		this.count = count;
		this.extraChance = extraChance;
		this.extraCount = extraCount;
	}
}
