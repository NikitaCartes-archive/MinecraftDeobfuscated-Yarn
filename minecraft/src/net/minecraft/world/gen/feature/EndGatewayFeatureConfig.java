package net.minecraft.world.gen.feature;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class EndGatewayFeatureConfig implements FeatureConfig {
	private final boolean exitsAtSpawn;

	public EndGatewayFeatureConfig(boolean bl) {
		this.exitsAtSpawn = bl;
	}

	public boolean exitsAtSpawn() {
		return this.exitsAtSpawn;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("exits_at_spawn"), dynamicOps.createBoolean(this.exitsAtSpawn)))
		);
	}

	public static <T> EndGatewayFeatureConfig deserialize(Dynamic<T> dynamic) {
		boolean bl = dynamic.getBoolean("exits_at_spawn", false);
		return new EndGatewayFeatureConfig(bl);
	}
}
