package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

/**
 * A functional interface that represents a task.
 */
public interface TaskRunnable<E extends LivingEntity> {
	/**
	 * Runs the task.
	 * 
	 * @return whether the task successfully ran
	 */
	boolean trigger(ServerWorld world, E entity, long time);
}
