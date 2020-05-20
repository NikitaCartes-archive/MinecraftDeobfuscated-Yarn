package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CountChanceDecoratorConfig implements DecoratorConfig {
	public static final Codec<CountChanceDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("count").forGetter(countChanceDecoratorConfig -> countChanceDecoratorConfig.count),
					Codec.FLOAT.fieldOf("chance").forGetter(countChanceDecoratorConfig -> countChanceDecoratorConfig.chance)
				)
				.apply(instance, CountChanceDecoratorConfig::new)
	);
	public final int count;
	public final float chance;

	public CountChanceDecoratorConfig(int count, float chance) {
		this.count = count;
		this.chance = chance;
	}
}
