package net.minecraft.world.gen;

import com.mojang.serialization.Codec;
import net.minecraft.util.math.intprovider.ConstantIntProvider;
import net.minecraft.util.math.intprovider.IntProvider;
import net.minecraft.world.gen.feature.FeatureConfig;

public class CountConfig implements FeatureConfig {
	public static final Codec<CountConfig> CODEC = IntProvider.createValidatingCodec(0, 256)
		.fieldOf("count")
		.<CountConfig>xmap(CountConfig::new, CountConfig::getCount)
		.codec();
	private final IntProvider count;

	public CountConfig(int count) {
		this.count = ConstantIntProvider.create(count);
	}

	public CountConfig(IntProvider distribution) {
		this.count = distribution;
	}

	public IntProvider getCount() {
		return this.count;
	}
}
