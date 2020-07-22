package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.world.gen.decorator.DecoratorConfig;
import net.minecraft.world.gen.feature.FeatureConfig;

public class CountConfig implements DecoratorConfig, FeatureConfig {
	public static final Codec<CountConfig> CODEC = UniformIntDistribution.createValidatedCodec(-10, 128, 128)
		.fieldOf("count")
		.<CountConfig>xmap(CountConfig::new, CountConfig::getCount)
		.codec();
	private final UniformIntDistribution count;

	public CountConfig(int count) {
		this.count = UniformIntDistribution.of(count);
	}

	public CountConfig(UniformIntDistribution distribution) {
		this.count = distribution;
	}

	public UniformIntDistribution getCount() {
		return this.count;
	}
}
