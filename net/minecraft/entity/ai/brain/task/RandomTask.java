/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.Task;

public class RandomTask<E extends LivingEntity>
extends CompositeTask<E> {
    public RandomTask(List<Pair<Task<? super E>, Integer>> list) {
        this(ImmutableSet.of(), list);
    }

    public RandomTask(Set<Pair<MemoryModuleType<?>, MemoryModuleState>> set, List<Pair<Task<? super E>, Integer>> list) {
        super(set, ImmutableSet.of(), CompositeTask.Order.SHUFFLED, CompositeTask.RunMode.RUN_ALL, list);
    }
}

