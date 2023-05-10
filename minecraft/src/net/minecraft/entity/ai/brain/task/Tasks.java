package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import net.minecraft.entity.LivingEntity;
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
		return weighted(weightedTasks, CompositeTask.Order.SHUFFLED, CompositeTask.RunMode.RUN_ONE);
	}

	/**
	 * {@return a task that runs task(s) from {@code weightedTasks}}
	 * 
	 * @param order whether to sort or shuffle the task list
	 * @param weightedTasks the list of pairs of the task function and its weight
	 * @param runMode whether to run all or just one of the tasks
	 */
	public static <E extends LivingEntity> SingleTickTask<E> weighted(
		List<Pair<? extends TaskRunnable<? super E>, Integer>> weightedTasks, CompositeTask.Order order, CompositeTask.RunMode runMode
	) {
		WeightedList<TaskRunnable<? super E>> weightedList = new WeightedList<>();
		weightedTasks.forEach(task -> weightedList.add((TaskRunnable<? super E>)task.getFirst(), (Integer)task.getSecond()));
		return TaskTriggerer.task(context -> context.point((world, entity, time) -> {
				if (order == CompositeTask.Order.SHUFFLED) {
					weightedList.shuffle();
				}

				for (TaskRunnable<? super E> taskRunnable : weightedList) {
					if (taskRunnable.trigger(world, (E)entity, time) && runMode == CompositeTask.RunMode.RUN_ONE) {
						break;
					}
				}

				return true;
			}));
	}
}
