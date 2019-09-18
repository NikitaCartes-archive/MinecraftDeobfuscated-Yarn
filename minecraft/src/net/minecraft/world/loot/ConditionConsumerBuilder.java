package net.minecraft.world.loot;

import net.minecraft.class_4570;

public interface ConditionConsumerBuilder<T> {
	T withCondition(class_4570.Builder builder);

	T getThis();
}
