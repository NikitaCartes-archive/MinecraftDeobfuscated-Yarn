package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountChanceDecoratorConfig implements DecoratorConfig {
	public final int count;
	public final float chance;

	public CountChanceDecoratorConfig(int count, float chance) {
		this.count = count;
		this.chance = chance;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(
			ops, ops.createMap(ImmutableMap.of(ops.createString("count"), ops.createInt(this.count), ops.createString("chance"), ops.createFloat(this.chance)))
		);
	}

	public static CountChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("count").asInt(0);
		float f = dynamic.get("chance").asFloat(0.0F);
		return new CountChanceDecoratorConfig(i, f);
	}
}
