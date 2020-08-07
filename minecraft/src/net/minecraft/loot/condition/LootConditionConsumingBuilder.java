package net.minecraft.loot.condition;

public interface LootConditionConsumingBuilder<T> {
	T conditionally(LootCondition.Builder condition);

	T getThis();
}
