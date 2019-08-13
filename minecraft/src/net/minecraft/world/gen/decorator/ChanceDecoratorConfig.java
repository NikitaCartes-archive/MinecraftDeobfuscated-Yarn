package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class ChanceDecoratorConfig implements DecoratorConfig {
	public final int chance;

	public ChanceDecoratorConfig(int i) {
		this.chance = i;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createInt(this.chance))));
	}

	public static ChanceDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("chance").asInt(0);
		return new ChanceDecoratorConfig(i);
	}
}
