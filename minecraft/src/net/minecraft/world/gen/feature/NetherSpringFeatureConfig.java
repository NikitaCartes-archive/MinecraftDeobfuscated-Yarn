package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NetherSpringFeatureConfig implements FeatureConfig {
	public final boolean insideRock;

	public NetherSpringFeatureConfig(boolean insideRock) {
		this.insideRock = insideRock;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("inside_rock"), ops.createBoolean(this.insideRock))));
	}

	public static <T> NetherSpringFeatureConfig deserialize(Dynamic<T> dynamic) {
		boolean bl = dynamic.get("inside_rock").asBoolean(false);
		return new NetherSpringFeatureConfig(bl);
	}
}
