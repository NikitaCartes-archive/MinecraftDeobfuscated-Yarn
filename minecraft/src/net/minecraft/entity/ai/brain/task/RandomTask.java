package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.LivingEntity;

public class RandomTask<E extends LivingEntity> extends CompositeTask<E> {
	public RandomTask(List<Pair<Task<? super E>, Integer>> list) {
		super(ImmutableSet.of(), ImmutableSet.of(), CompositeTask.Order.field_18349, CompositeTask.class_4216.field_18855, list);
	}
}
