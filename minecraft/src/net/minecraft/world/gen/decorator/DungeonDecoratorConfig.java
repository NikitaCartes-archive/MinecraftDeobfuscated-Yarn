package net.minecraft.world.gen.decorator;

import com.google.common.collect.ImmutableMap;
import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class DungeonDecoratorConfig implements DecoratorConfig {
	public final int chance;

	public DungeonDecoratorConfig(int chance) {
		this.chance = chance;
	}

	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.createMap(ImmutableMap.of(ops.createString("chance"), ops.createInt(this.chance))));
	}

	public static DungeonDecoratorConfig deserialize(Dynamic<?> dynamic) {
		int i = dynamic.get("chance").asInt(0);
		return new DungeonDecoratorConfig(i);
	}
}
