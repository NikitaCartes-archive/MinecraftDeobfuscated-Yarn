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
	public RandomTask(List<Pair<? extends Task<? super E>, Integer>> tasks) {
		this(ImmutableMap.of(), tasks);
	}

	public RandomTask(Map<MemoryModuleType<?>, MemoryModuleState> requiredMemoryState, List<Pair<? extends Task<? super E>, Integer>> tasks) {
		super(requiredMemoryState, ImmutableSet.of(), CompositeTask.Order.SHUFFLED, CompositeTask.RunMode.RUN_ONE, tasks);
	}
}
