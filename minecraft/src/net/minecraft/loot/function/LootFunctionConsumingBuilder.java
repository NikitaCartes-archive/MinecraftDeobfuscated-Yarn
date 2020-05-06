package net.minecraft.loot.function;

public interface LootFunctionConsumingBuilder<T> {
	T apply(LootFunction.Builder function);

	T getThis();
}
