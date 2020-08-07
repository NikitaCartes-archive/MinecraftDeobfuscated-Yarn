package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;

public class ChanceDecoratorConfig implements DecoratorConfig {
	public static final Codec<ChanceDecoratorConfig> CODEC = Codec.INT
		.fieldOf("chance")
		.<ChanceDecoratorConfig>xmap(ChanceDecoratorConfig::new, chanceDecoratorConfig -> chanceDecoratorConfig.chance)
		.codec();
	public final int chance;

	public ChanceDecoratorConfig(int chance) {
		this.chance = chance;
	}
}
