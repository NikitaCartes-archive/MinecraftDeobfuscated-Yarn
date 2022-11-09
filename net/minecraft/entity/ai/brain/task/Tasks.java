/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.task.CompositeTask;
import net.minecraft.entity.ai.brain.task.SingleTickTask;
import net.minecraft.entity.ai.brain.task.TaskRunnable;
import net.minecraft.entity.ai.brain.task.TaskTriggerer;
import net.minecraft.util.collection.WeightedList;

/**
 * Contains task-related utility methods.
 */
public class Tasks {
    /**
     * {@return a task that picks and runs a task from {@code weightedTasks} randomly}
     * 
     * @param weightedTasks the list of pairs of the task function and its weight
     */
    public static <E extends LivingEntity> SingleTickTask<E> pickRandomly(List<Pair<? extends TaskRunnable<? super E>, Integer>> weightedTasks) {
        return Tasks.weighted(weightedTasks, CompositeTask.Order.SHUFFLED, CompositeTask.RunMode.RUN_ONE);
    }

    /**
     * {@return a task that runs task(s) from {@code weightedTasks}}
     * 
     * @param order whether to sort or shuffle the task list
     * @param weightedTasks the list of pairs of the task function and its weight
     * @param runMode whether to run all or just one of the tasks
     */
    public static <E extends LivingEntity> SingleTickTask<E> weighted(List<Pair<? extends TaskRunnable<? super E>, Integer>> weightedTasks, CompositeTask.Order order, CompositeTask.RunMode runMode) {
        WeightedList weightedList = new WeightedList();
        weightedTasks.forEach(task -> weightedList.add((TaskRunnable)task.getFirst(), (Integer)task.getSecond()));
        return TaskTriggerer.task(context -> context.point((world, entity, time) -> {
            TaskRunnable taskRunnable;
            if (order == CompositeTask.Order.SHUFFLED) {
                weightedList.shuffle();
            }
            Iterator iterator = weightedList.iterator();
            while (iterator.hasNext() && (!(taskRunnable = (TaskRunnable)iterator.next()).trigger(world, entity, time) || runMode != CompositeTask.RunMode.RUN_ONE)) {
            }
            return true;
        }));
    }
}

