package net.minecraft.loot.condition;

public interface LootConditionConsumingBuilder<T> {
	T withCondition(LootCondition.Builder builder);

	T getThis();
}
