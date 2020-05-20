package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class IcePatchFeatureConfig implements FeatureConfig {
	public static final Codec<IcePatchFeatureConfig> field_24884 = Codec.INT
		.fieldOf("radius")
		.<IcePatchFeatureConfig>xmap(IcePatchFeatureConfig::new, icePatchFeatureConfig -> icePatchFeatureConfig.radius)
		.codec();
	public final int radius;

	public IcePatchFeatureConfig(int radius) {
		this.radius = radius;
	}
}
