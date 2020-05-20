package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class HeightmapRangeDecoratorConfig implements DecoratorConfig {
	public static final Codec<HeightmapRangeDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("min").forGetter(heightmapRangeDecoratorConfig -> heightmapRangeDecoratorConfig.min),
					Codec.INT.fieldOf("max").forGetter(heightmapRangeDecoratorConfig -> heightmapRangeDecoratorConfig.max)
				)
				.apply(instance, HeightmapRangeDecoratorConfig::new)
	);
	public final int min;
	public final int max;

	public HeightmapRangeDecoratorConfig(int min, int max) {
		this.min = min;
		this.max = max;
	}
}
