package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class PlantedFeatureConfig implements FeatureConfig {
	public final boolean planted;

	public PlantedFeatureConfig(boolean planted) {
		this.planted = planted;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("planted"), ops.createBoolean(this.planted))));
	}

	public static <T> PlantedFeatureConfig deserialize(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("planted").asBoolean(false);
		return new PlantedFeatureConfig(bl);
	}
}
