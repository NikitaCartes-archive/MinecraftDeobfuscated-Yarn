package net.minecraft.loot.condition;

import java.util.function.Function;

public interface LootConditionConsumingBuilder<T extends LootConditionConsumingBuilder<T>> {
	T conditionally(LootCondition.Builder condition);

	default <E> T conditionally(Iterable<E> conditions, Function<E, LootCondition.Builder> toBuilderFunction) {
		T lootConditionConsumingBuilder = this.getThisConditionConsumingBuilder();

		for (E object : conditions) {
			lootConditionConsumingBuilder = lootConditionConsumingBuilder.conditionally((LootCondition.Builder)toBuilderFunction.apply(object));
		}

		return lootConditionConsumingBuilder;
	}

	T getThisConditionConsumingBuilder();
}
