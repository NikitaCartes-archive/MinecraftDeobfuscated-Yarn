package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class CountDepthDecoratorConfig implements DecoratorConfig {
	public static final Codec<CountDepthDecoratorConfig> field_24982 = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("baseline").forGetter(countDepthDecoratorConfig -> countDepthDecoratorConfig.count),
					Codec.INT.fieldOf("spread").forGetter(countDepthDecoratorConfig -> countDepthDecoratorConfig.spread)
				)
				.apply(instance, CountDepthDecoratorConfig::new)
	);
	public final int count;
	public final int spread;

	public CountDepthDecoratorConfig(int count, int baseline) {
		this.count = count;
		this.spread = baseline;
	}
}
