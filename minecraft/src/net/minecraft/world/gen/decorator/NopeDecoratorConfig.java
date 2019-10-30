package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NopeDecoratorConfig implements DecoratorConfig {
	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> ops) {
		return new Dynamic<>(ops, ops.emptyMap());
	}

	public static NopeDecoratorConfig deserialize(Dynamic<?> dynamic) {
		return new NopeDecoratorConfig();
	}
}
