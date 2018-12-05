package net.minecraft.world.gen.config.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface DecoratorConfig {
	NopeDecoratorConfig DEFAULT = new NopeDecoratorConfig();

	<T> Dynamic<T> serialize(DynamicOps<T> dynamicOps);
}
