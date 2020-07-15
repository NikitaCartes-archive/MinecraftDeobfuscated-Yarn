package net.minecraft.world.gen.feature;

import java.util.stream.Stream;

public interface FeatureConfig {
	DefaultFeatureConfig DEFAULT = DefaultFeatureConfig.INSTANCE;

	default Stream<ConfiguredFeature<?, ?>> method_30649() {
		return Stream.empty();
	}
}
