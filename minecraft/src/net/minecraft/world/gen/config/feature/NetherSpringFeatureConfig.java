package net.minecraft.world.gen.config.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NetherSpringFeatureConfig implements FeatureConfig {
	public final boolean insideRock;

	public NetherSpringFeatureConfig(boolean bl) {
		this.insideRock = bl;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("inside_rock"), dynamicOps.createBoolean(this.insideRock))));
	}

	public static <T> NetherSpringFeatureConfig deserialize(Dynamic<T> dynamic) {
		boolean bl = dynamic.getBoolean("inside_rock", false);
		return new NetherSpringFeatureConfig(bl);
	}
}
