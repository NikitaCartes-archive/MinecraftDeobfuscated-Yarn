package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceDecoratorConfig implements DecoratorConfig {
	public final int chance;

	public ChanceDecoratorConfig(int chance) {
		this.chance = chance;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("chance"), ops.createInt(this.chance))));
	}

	public static ChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("chance").asInt(0);
		return new ChanceDecoratorConfig(i);
	}
}
