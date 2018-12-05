package net.minecraft.world.gen.config.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public class NopeDecoratorConfig implements DecoratorConfig {
	@Override
	public <T> Dynamic<T> serialize(DynamicOps<T> dynamicOps) {
		return new Dynamic<>(dynamicOps, dynamicOps.emptyMap());
	}

	public static NopeDecoratorConfig deserialize(Dynamic<?> dynamic) {
		return new NopeDecoratorConfig();
	}
}
