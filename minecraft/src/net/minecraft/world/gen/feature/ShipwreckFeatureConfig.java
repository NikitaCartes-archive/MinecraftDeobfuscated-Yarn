package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class ShipwreckFeatureConfig implements FeatureConfig {
	public static final Codec<ShipwreckFeatureConfig> field_24908 = Codec.BOOL
		.fieldOf("is_beached")
		.withDefault(false)
		.<ShipwreckFeatureConfig>xmap(ShipwreckFeatureConfig::new, shipwreckFeatureConfig -> shipwreckFeatureConfig.isBeached)
		.codec();
	public final boolean isBeached;

	public ShipwreckFeatureConfig(boolean isBeached) {
		this.isBeached = isBeached;
	}
}
