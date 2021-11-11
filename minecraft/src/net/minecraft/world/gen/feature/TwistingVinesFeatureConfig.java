package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public record TwistingVinesFeatureConfig() implements FeatureConfig {
	private final int spreadWidth;
	private final int spreadHeight;
	private final int maxHeight;
	public static final Codec<TwistingVinesFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_INT.fieldOf("spread_width").forGetter(TwistingVinesFeatureConfig::spreadWidth),
					Codecs.POSITIVE_INT.fieldOf("spread_height").forGetter(TwistingVinesFeatureConfig::spreadHeight),
					Codecs.POSITIVE_INT.fieldOf("max_height").forGetter(TwistingVinesFeatureConfig::maxHeight)
				)
				.apply(instance, TwistingVinesFeatureConfig::new)
	);

	public TwistingVinesFeatureConfig(int i, int j, int k) {
		this.spreadWidth = i;
		this.spreadHeight = j;
		this.maxHeight = k;
	}
}
