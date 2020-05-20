package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public class SeagrassFeatureConfig implements FeatureConfig {
	public static final Codec<SeagrassFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codec.INT.fieldOf("count").forGetter(seagrassFeatureConfig -> seagrassFeatureConfig.count),
					Codec.DOUBLE.fieldOf("probability").forGetter(seagrassFeatureConfig -> seagrassFeatureConfig.tallSeagrassProbability)
				)
				.apply(instance, SeagrassFeatureConfig::new)
	);
	public final int count;
	public final double tallSeagrassProbability;

	public SeagrassFeatureConfig(int count, double tallSeagrassProbability) {
		this.count = count;
		this.tallSeagrassProbability = tallSeagrassProbability;
	}
}
