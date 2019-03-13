package net.minecraft.world.loot;

import net.minecraft.world.loot.function.LootFunction;

public interface FunctionConsumerBuilder<T> {
	T method_511(LootFunction.Builder builder);

	T getThis();
}
