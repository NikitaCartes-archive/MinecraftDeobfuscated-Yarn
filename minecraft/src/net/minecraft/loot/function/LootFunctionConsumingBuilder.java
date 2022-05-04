package net.minecraft.loot.function;

import java.util.Arrays;
import java.util.function.Function;

public interface LootFunctionConsumingBuilder<T extends LootFunctionConsumingBuilder<T>> {
	T apply(LootFunction.Builder function);

	default <E> T apply(Iterable<E> functions, Function<E, LootFunction.Builder> toBuilderFunction) {
		T lootFunctionConsumingBuilder = this.getThisFunctionConsumingBuilder();

		for (E object : functions) {
			lootFunctionConsumingBuilder = lootFunctionConsumingBuilder.apply((LootFunction.Builder)toBuilderFunction.apply(object));
		}

		return lootFunctionConsumingBuilder;
	}

	default <E> T apply(E[] functions, Function<E, LootFunction.Builder> toBuilderFunction) {
		return this.apply(Arrays.asList(functions), toBuilderFunction);
	}

	T getThisFunctionConsumingBuilder();
}
