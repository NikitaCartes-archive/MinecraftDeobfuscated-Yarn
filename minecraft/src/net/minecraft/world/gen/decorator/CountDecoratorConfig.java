package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;

public class CountDecoratorConfig implements DecoratorConfig {
	public static final Codec<CountDecoratorConfig> field_24985 = Codec.INT
		.fieldOf("count")
		.<CountDecoratorConfig>xmap(CountDecoratorConfig::new, countDecoratorConfig -> countDecoratorConfig.count)
		.codec();
	public final int count;

	public CountDecoratorConfig(int count) {
		this.count = count;
	}
}
