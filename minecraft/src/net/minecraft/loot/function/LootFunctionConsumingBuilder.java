package net.minecraft.loot.function;

public interface LootFunctionConsumingBuilder<T> {
	T withFunction(LootFunction.Builder lootFunctionBuilder);

	T getThis();
}
