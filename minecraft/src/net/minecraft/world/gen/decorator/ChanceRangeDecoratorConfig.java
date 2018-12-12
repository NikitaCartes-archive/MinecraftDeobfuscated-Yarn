package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceRangeDecoratorConfig implements DecoratorConfig {
	public final float chance;
	public final int bottomOffset;
	public final int topOffset;
	public final int top;

	public ChanceRangeDecoratorConfig(float f, int i, int j, int k) {
		this.chance = f;
		this.bottomOffset = i;
		this.topOffset = j;
		this.top = k;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(
					dynamicOps.createString("chance"),
					dynamicOps.createFloat(this.chance),
					dynamicOps.createString("bottom_offset"),
					dynamicOps.createInt(this.bottomOffset),
					dynamicOps.createString("top_offset"),
					dynamicOps.createInt(this.topOffset),
					dynamicOps.createString("top"),
					dynamicOps.createInt(this.top)
				)
			)
		);
	}

	public static ChanceRangeDecoratorConfig deserialize(Dynamic<?> dynamic) {
		float f = dynamic.getFloat("chance", 0.0F);
		int i = dynamic.getInt("bottom_offset", 0);
		int j = dynamic.getInt("top_offset", 0);
		int k = dynamic.getInt("top", 0);
		return new ChanceRangeDecoratorConfig(f, i, j, k);
	}
}
