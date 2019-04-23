package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class RandomTask<E extends LivingEntity> extends CompositeTask<E> {
	public RandomTask(List<Pair<Task<? super E>, Integer>> list) {
		this(ImmutableSet.of(), list);
	}

	public RandomTask(Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set, List<Pair<Task<? super E>, Integer>> list) {
		super(set, ImmutableSet.of(), CompositeTask.Order.field_18349, CompositeTask.RunMode.RUN_ALL, list);
	}
}
