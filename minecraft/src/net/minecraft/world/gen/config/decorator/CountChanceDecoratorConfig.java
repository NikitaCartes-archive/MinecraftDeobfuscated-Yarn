package net.minecraft.world.gen.config.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountChanceDecoratorConfig implements DecoratorConfig {
	public final int count;
	public final float chance;

	public CountChanceDecoratorConfig(int i, float f) {
		this.count = i;
		this.chance = f;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(
			dynamicOps,
			dynamicOps.createMap(
				ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count), dynamicOps.createString("chance"), dynamicOps.createFloat(this.chance))
			)
		);
	}

	public static CountChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.getInt("count", 0);
		float f = dynamic.getFloat("chance", 0.0F);
		return new CountChanceDecoratorConfig(i, f);
	}
}
