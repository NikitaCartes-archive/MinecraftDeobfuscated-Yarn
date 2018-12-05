package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ShipwreckFeatureConfig implements FeatureConfig {
	public final boolean isBeached;

	public ShipwreckFeatureConfig(boolean bl) {
		this.isBeached = bl;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("is_beached"), dynamicOps.createBoolean(this.isBeached))));
	}

	public static <T> ShipwreckFeatureConfig deserialize(Dynamic<T> dynamic) {
		boolean bl = dynamic.getBoolean("is_beached", false);
		return new ShipwreckFeatureConfig(bl);
	}
}
