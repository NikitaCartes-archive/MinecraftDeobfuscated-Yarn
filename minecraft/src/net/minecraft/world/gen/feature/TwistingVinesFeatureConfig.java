package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.dynamic.Codecs;

public record TwistingVinesFeatureConfig(int spreadWidth, int spreadHeight, int maxHeight) implements FeatureConfig {
	public static final Codec<TwistingVinesFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_INT.fieldOf("spread_width").forGetter(TwistingVinesFeatureConfig::spreadWidth),
					Codecs.POSITIVE_INT.fieldOf("spread_height").forGetter(TwistingVinesFeatureConfig::spreadHeight),
					Codecs.POSITIVE_INT.fieldOf("max_height").forGetter(TwistingVinesFeatureConfig::maxHeight)
				)
				.apply(instance, TwistingVinesFeatureConfig::new)
	);
}
