package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;
import java.util.List;

public class SimpleRandomFeatureConfig implements FeatureConfig {
	public static final Codec<SimpleRandomFeatureConfig> CODEC = ConfiguredFeature.CODEC
		.listOf()
		.fieldOf("features")
		.<SimpleRandomFeatureConfig>xmap(SimpleRandomFeatureConfig::new, simpleRandomFeatureConfig -> simpleRandomFeatureConfig.features)
		.codec();
	public final List<ConfiguredFeature<?, ?>> features;

	public SimpleRandomFeatureConfig(List<ConfiguredFeature<?, ?>> features) {
		this.features = features;
	}
}
