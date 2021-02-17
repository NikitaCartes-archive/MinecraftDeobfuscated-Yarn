package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.YOffset;

public class BiasedRangedDecoratorConfig implements DecoratorConfig {
	public static final Codec<BiasedRangedDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					YOffset.OFFSET_CODEC.fieldOf("bottom_inclusive").forGetter(BiasedRangedDecoratorConfig::getBottom),
					YOffset.OFFSET_CODEC.fieldOf("top_inclusive").forGetter(BiasedRangedDecoratorConfig::getTop),
					Codec.INT.fieldOf("cutoff").forGetter(BiasedRangedDecoratorConfig::getCutoff)
				)
				.apply(instance, BiasedRangedDecoratorConfig::new)
	);
	private final YOffset bottom;
	private final YOffset top;
	private final int cutoff;

	public BiasedRangedDecoratorConfig(YOffset bottom, YOffset top, int cutoff) {
		this.bottom = bottom;
		this.cutoff = cutoff;
		this.top = top;
	}

	public YOffset getBottom() {
		return this.bottom;
	}

	public int getCutoff() {
		return this.cutoff;
	}

	public YOffset getTop() {
		return this.top;
	}
}
