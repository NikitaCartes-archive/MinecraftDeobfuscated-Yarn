package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import java.util.function.Supplier;
import java.util.stream.Stream;

public class RandomBooleanFeatureConfig implements FeatureConfig {
	public static final Codec<RandomBooleanFeatureConfig> CODEC = RecordCodecBuilder.create(
		instance -> instance.group(
					ConfiguredFeature.CODEC.fieldOf("feature_true").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureTrue),
					ConfiguredFeature.CODEC.fieldOf("feature_false").forGetter(randomBooleanFeatureConfig -> randomBooleanFeatureConfig.featureFalse)
				)
				.apply(instance, RandomBooleanFeatureConfig::new)
	);
	public final Supplier<ConfiguredFeature<?, ?>> featureTrue;
	public final Supplier<ConfiguredFeature<?, ?>> featureFalse;

	public RandomBooleanFeatureConfig(Supplier<ConfiguredFeature<?, ?>> supplier, Supplier<ConfiguredFeature<?, ?>> supplier2) {
		this.featureTrue = supplier;
		this.featureFalse = supplier2;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> method_30649() {
		return Stream.concat(((ConfiguredFeature)this.featureTrue.get()).method_30648(), ((ConfiguredFeature)this.featureFalse.get()).method_30648());
	}
}
