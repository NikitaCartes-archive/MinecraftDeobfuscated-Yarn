package net.minecraft.world.loot;

import net.minecraft.world.loot.condition.LootCondition;

public interface ConditionConsumerBuilder<T> {
	T method_840(LootCondition.Builder builder);

	T getThis();
}
