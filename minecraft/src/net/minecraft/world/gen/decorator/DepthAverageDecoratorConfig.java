package net.minecraft.world.gen.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.YOffset;

public class DepthAverageDecoratorConfig implements DecoratorConfig {
	public static final Codec<DepthAverageDecoratorConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					YOffset.OFFSET_CODEC.fieldOf("baseline").forGetter(DepthAverageDecoratorConfig::getBaseline),
					Codec.INT.fieldOf("spread").forGetter(DepthAverageDecoratorConfig::getSpread)
				)
				.apply(instance, DepthAverageDecoratorConfig::new)
	);
	private final YOffset baseline;
	private final int spread;

	public DepthAverageDecoratorConfig(YOffset baseline, int spread) {
		this.baseline = baseline;
		this.spread = spread;
	}

	public YOffset getBaseline() {
		return this.baseline;
	}

	public int getSpread() {
		return this.spread;
	}
}
