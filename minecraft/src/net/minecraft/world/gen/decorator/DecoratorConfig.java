package net.minecraft.world.gen.decorator;

import com.mojang.datafixers.Dynamic;
import com.mojang.datafixers.types.DynamicOps;

public interface DecoratorConfig {
	NopeDecoratorConfig field_13436 = new NopeDecoratorConfig();

	<T> Dynamic<T> serialize(DynamicOps<T> dynamicOps);
}
