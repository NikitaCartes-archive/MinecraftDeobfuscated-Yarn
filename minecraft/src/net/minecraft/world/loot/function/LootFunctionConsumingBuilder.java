package net.minecraft.world.loot.function;

public interface LootFunctionConsumingBuilder<T> {
	T withFunction(LootFunction.Builder builder);

	T getThis();
}
