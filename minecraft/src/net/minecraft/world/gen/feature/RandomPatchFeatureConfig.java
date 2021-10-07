package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import net.minecraft.util.dynamic.Codecs;

public record RandomPatchFeatureConfig() implements FeatureConfig {
	private final int tries;
	private final int xzSpread;
	private final int ySpread;
	private final Supplier<ConfiguredFeature<?, ?>> feature;
	public static final Codec<RandomPatchFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					Codecs.POSITIVE_INT.fieldOf("tries").orElse(128).forGetter(RandomPatchFeatureConfig::tries),
					Codecs.NONNEGATIVE_INT.fieldOf("xz_spread").orElse(7).forGetter(RandomPatchFeatureConfig::xzSpread),
					Codecs.NONNEGATIVE_INT.fieldOf("y_spread").orElse(3).forGetter(RandomPatchFeatureConfig::ySpread),
					ConfiguredFeature.REGISTRY_CODEC.fieldOf("feature").forGetter(RandomPatchFeatureConfig::feature)
				)
				.apply(instance, RandomPatchFeatureConfig::new)
	);

	public RandomPatchFeatureConfig(int i, int j, int k, Supplier<ConfiguredFeature<?, ?>> supplier) {
		this.tries = i;
		this.xzSpread = j;
		this.ySpread = k;
		this.feature = supplier;
	}
}
