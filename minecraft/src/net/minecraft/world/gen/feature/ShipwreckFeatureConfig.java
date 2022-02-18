package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class ShipwreckFeatureConfig implements FeatureConfig {
	public static final Codec<ShipwreckFeatureConfig> CODEC = Codec.BOOL
		.fieldOf("is_beached")
		.orElse(false)
		.<ShipwreckFeatureConfig>xmap(ShipwreckFeatureConfig::new, config -> config.isBeached)
		.codec();
	public final boolean isBeached;

	public ShipwreckFeatureConfig(boolean isBeached) {
		this.isBeached = isBeached;
	}
}
