package net.minecraft.loot.condition;

import net.minecraft.world.loot.condition.LootCondition;

public interface LootConditionConsumingBuilder<T> {
	T withCondition(LootCondition.Builder builder);

	T getThis();
}
