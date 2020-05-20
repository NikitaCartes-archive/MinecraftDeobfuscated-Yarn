package net.minecraft.world.gen.feature;

import com.mojang.serialization.Codec;

public class RuinedPortalFeatureConfig implements FeatureConfig {
	public static final Codec<RuinedPortalFeatureConfig> field_24906 = RuinedPortalFeature.Type.field_24840
		.fieldOf("portal_type")
		.<RuinedPortalFeatureConfig>xmap(RuinedPortalFeatureConfig::new, ruinedPortalFeatureConfig -> ruinedPortalFeatureConfig.portalType)
		.codec();
	public final RuinedPortalFeature.Type portalType;

	public RuinedPortalFeatureConfig(RuinedPortalFeature.Type type) {
		this.portalType = type;
	}
}
