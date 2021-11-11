package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.util.dynamic.Codecs;

public class SimpleRandomFeatureConfig implements FeatureConfig {
	public static final Codec<SimpleRandomFeatureConfig> CODEC = Codecs.nonEmptyList(PlacedFeature.LIST_CODEC)
		.fieldOf("features")
		.<SimpleRandomFeatureConfig>xmap(SimpleRandomFeatureConfig::new, simpleRandomFeatureConfig -> simpleRandomFeatureConfig.features)
		.codec();
	public final List<Supplier<PlacedFeature>> features;

	public SimpleRandomFeatureConfig(List<Supplier<PlacedFeature>> features) {
		this.features = features;
	}

	@Override
	public Stream<ConfiguredFeature<?, ?>> getDecoratedFeatures() {
		return this.features.stream().flatMap(supplier -> ((PlacedFeature)supplier.get()).getDecoratedFeatures());
	}
}
