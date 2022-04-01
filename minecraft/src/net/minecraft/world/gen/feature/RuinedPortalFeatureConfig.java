package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class RuinedPortalFeatureConfig implements FeatureConfig {
	public static final Codec<RuinedPortalFeatureConfig> CODEC = RuinedPortalFeature.Type.CODEC
		.fieldOf("portal_type")
		.<RuinedPortalFeatureConfig>xmap(RuinedPortalFeatureConfig::new, config -> config.portalType)
		.codec();
	public final RuinedPortalFeature.Type portalType;

	public RuinedPortalFeatureConfig(RuinedPortalFeature.Type portalType) {
		this.portalType = portalType;
	}
}
