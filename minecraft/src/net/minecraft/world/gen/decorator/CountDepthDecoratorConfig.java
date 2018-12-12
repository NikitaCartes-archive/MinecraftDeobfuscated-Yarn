package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountDepthDecoratorConfig implements DecoratorConfig {
	public final int count;
	public final int baseline;
	public final int spread;

	public CountDepthDecoratorConfig(int i, int j, int k) {
		this.count = i;
		this.baseline = j;
		this.spread = k;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("count"),
					dynamicOps.createInt(this.count),
					dynamicOps.createString("baseline"),
					dynamicOps.createInt(this.baseline),
					dynamicOps.createString("spread"),
					dynamicOps.createInt(this.spread)
				)
			)
		);
	}

	public static CountDepthDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.getInt("count", 0);
		int j = dynamic.getInt("baseline", 0);
		int k = dynamic.getInt("spread", 0);
		return new CountDepthDecoratorConfig(i, j, k);
	}
}
