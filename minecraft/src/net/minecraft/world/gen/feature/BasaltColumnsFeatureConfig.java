package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.gen.UniformIntDistribution;

public class BasaltColumnsFeatureConfig implements FeatureConfig {
	public static final Codec<BasaltColumnsFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					UniformIntDistribution.createValidatedCodec(0, 2, 1).fieldOf("reach").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.reach),
					UniformIntDistribution.createValidatedCodec(1, 5, 5).fieldOf("height").forGetter(basaltColumnsFeatureConfig -> basaltColumnsFeatureConfig.height)
				)
				.apply(instance, BasaltColumnsFeatureConfig::new)
	);
	private final UniformIntDistribution reach;
	private final UniformIntDistribution height;

	public BasaltColumnsFeatureConfig(UniformIntDistribution reach, UniformIntDistribution height) {
		this.reach = reach;
		this.height = height;
	}

	public UniformIntDistribution getReach() {
		return this.reach;
	}

	public UniformIntDistribution getHeight() {
		return this.height;
	}
}
