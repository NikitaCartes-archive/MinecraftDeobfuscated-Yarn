package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class HeightmapRangeDecoratorConfig implements DecoratorConfig {
	public final int min;
	public final int max;

	public HeightmapRangeDecoratorConfig(int i, int j) {
		this.min = i;
		this.max = j;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("min"), dynamicOps.createInt(this.min), dynamicOps.createString("max"), dynamicOps.createInt(this.max))
			)
		);
	}

	public static HeightmapRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.getInt("min", 0);
		int j = dynamic.getInt("max", 0);
		return new HeightmapRangeDecoratorConfig(i, j);
	}
}
