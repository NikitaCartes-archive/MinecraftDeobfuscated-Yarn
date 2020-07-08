package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;
import java.util.function.Supplier;

public class SimpleRandomFeatureConfig implements FeatureConfig {
	public static final Codec<SimpleRandomFeatureConfig> CODEC = ConfiguredFeature.CODEC
		.listOf()
		.fieldOf("features")
		.<SimpleRandomFeatureConfig>xmap(SimpleRandomFeatureConfig::new, simpleRandomFeatureConfig -> simpleRandomFeatureConfig.features)
		.codec();
	public final List<Supplier<ConfiguredFeature<?, ?>>> features;

	public SimpleRandomFeatureConfig(List<Supplier<ConfiguredFeature<?, ?>>> features) {
		this.features = features;
	}
}
