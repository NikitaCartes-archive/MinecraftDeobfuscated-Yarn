package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class SeaPickleFeatureConfig implements FeatureConfig {
	public static final Codec<SeaPickleFeatureConfig> field_24878 = Codec.INT
		.fieldOf("count")
		.<SeaPickleFeatureConfig>xmap(SeaPickleFeatureConfig::new, seaPickleFeatureConfig -> seaPickleFeatureConfig.count)
		.codec();
	public final int count;

	public SeaPickleFeatureConfig(int count) {
		this.count = count;
	}
}
