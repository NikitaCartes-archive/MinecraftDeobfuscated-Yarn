package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountDepthDecoratorConfig implements DecoratorConfig {
	public final int count;
	public final int baseline;
	public final int spread;

	public CountDepthDecoratorConfig(int count, int baseline, int spread) {
		this.count = count;
		this.baseline = baseline;
		this.spread = spread;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops,
			ops.createMap(
				ImmutableMap.of(
					ops.createString("count"),
					ops.createInt(this.count),
					ops.createString("baseline"),
					ops.createInt(this.baseline),
					ops.createString("spread"),
					ops.createInt(this.spread)
				)
			)
		);
	}

	public static CountDepthDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		int j = dynamic.get("baseline").asInt(0);
		int k = dynamic.get("spread").asInt(0);
		return new CountDepthDecoratorConfig(i, j, k);
	}
}
