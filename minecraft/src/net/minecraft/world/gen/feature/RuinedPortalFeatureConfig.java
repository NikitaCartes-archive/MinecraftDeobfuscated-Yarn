package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class RuinedPortalFeatureConfig implements FeatureConfig {
	public final RuinedPortalFeature.Type portalType;

	public RuinedPortalFeatureConfig() {
		this(RuinedPortalFeature.Type.STANDARD);
	}

	public RuinedPortalFeatureConfig(RuinedPortalFeature.Type type) {
		this.portalType = type;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("portal_type"), ops.createString(this.portalType.getName()))));
	}

	public static <T> RuinedPortalFeatureConfig deserialize(Dynamic<T> dynamic) {
		RuinedPortalFeature.Type type = RuinedPortalFeature.Type.byName(dynamic.get("portal_type").asString(""));
		return new RuinedPortalFeatureConfig(type);
	}
}
