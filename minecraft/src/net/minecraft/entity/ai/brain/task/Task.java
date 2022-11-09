package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public interface Task<E extends LivingEntity> {
	MultiTickTask.Status getStatus();

	boolean tryStarting(ServerWorld world, E entity, long time);

	void tick(ServerWorld world, E entity, long time);

	void stop(ServerWorld world, E entity, long time);

	String getName();
}
