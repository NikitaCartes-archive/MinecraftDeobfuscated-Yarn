package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;

public class RandomTask<E extends LivingEntity> extends CompositeTask<E> {
	public RandomTask(List<Pair<Task<? super E>, Integer>> list) {
		this(ImmutableMap.of(), list);
	}

	public RandomTask(Map<MemoryModuleType<?>, MemoryModuleState> map, List<Pair<Task<? super E>, Integer>> list) {
		super(map, ImmutableSet.of(), CompositeTask.Order.field_18349, CompositeTask.RunMode.field_18855, list);
	}
}
