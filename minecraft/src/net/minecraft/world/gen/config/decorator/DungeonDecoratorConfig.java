package net.minecraft.world.gen.config.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DungeonDecoratorConfig implements DecoratorConfig {
	public final int chance;

	public DungeonDecoratorConfig(int i) {
		this.chance = i;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.createMap(ImmutableMap.of(dynamicOps.createString("chance"), dynamicOps.createInt(this.chance))));
	}

	public static DungeonDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.getInt("chance", 0);
		return new DungeonDecoratorConfig(i);
	}
}
