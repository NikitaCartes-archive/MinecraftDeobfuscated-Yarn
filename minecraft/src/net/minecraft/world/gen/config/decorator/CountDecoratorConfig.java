package net.minecraft.world.gen.config.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class CountDecoratorConfig implements DecoratorConfig {
	public final int count;

	public CountDecoratorConfig(int i) {
		this.count = i;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("count"), dynamicOps.createInt(this.count))));
	}

	public static CountDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.getInt("count", 0);
		return new CountDecoratorConfig(i);
	}
}
