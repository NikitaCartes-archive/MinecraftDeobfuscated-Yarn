package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ShipwreckFeatureConfig implements FeatureConfig {
	public final boolean isBeached;

	public ShipwreckFeatureConfig(boolean isBeached) {
		this.isBeached = isBeached;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("is_beached"), ops.createBoolean(this.isBeached))));
	}

	public static <T> ShipwreckFeatureConfig deserialize(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("is_beached").asBoolean(false);
		return new ShipwreckFeatureConfig(bl);
	}
}
