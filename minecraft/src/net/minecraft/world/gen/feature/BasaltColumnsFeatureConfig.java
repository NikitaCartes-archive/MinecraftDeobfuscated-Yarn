package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.UniformIntDistribution;

public class BasaltColumnsFeatureConfig implements FeatureConfig {
	public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					UniformIntDistribution.createValidatedCodec(0, 2, 1).fieldOf("reach").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.field_25841),
					UniformIntDistribution.createValidatedCodec(1, 5, 5).fieldOf("height").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.field_25842)
				)
				.apply(instance, BasaltColumnsFeatureConfig::new)
	);
	private final UniformIntDistribution field_25841;
	private final UniformIntDistribution field_25842;

	public BasaltColumnsFeatureConfig(UniformIntDistribution uniformIntDistribution, UniformIntDistribution uniformIntDistribution2) {
		this.field_25841 = uniformIntDistribution;
		this.field_25842 = uniformIntDistribution2;
	}

	public UniformIntDistribution method_30391() {
		return this.field_25841;
	}

	public UniformIntDistribution method_30394() {
		return this.field_25842;
	}
}
