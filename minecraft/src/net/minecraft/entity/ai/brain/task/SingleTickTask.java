package net.minecraft.entity.ai.brain.task;

import net.minecraft.entity.LivingEntity;
import net.minecraft.server.world.ServerWorld;

public abstract class SingleTickTask<E extends LivingEntity> implements Task<E>, TaskRunnable<E> {
	private MultiTickTask.Status status = MultiTickTask.Status.STOPPED;

	@Override
	public final MultiTickTask.Status getStatus() {
		return this.status;
	}

	@Override
	public final boolean tryStarting(ServerWorld world, E entity, long time) {
		if (this.trigger(world, entity, time)) {
			this.status = MultiTickTask.Status.RUNNING;
			return true;
		} else {
			return false;
		}
	}

	@Override
	public final void tick(ServerWorld world, E entity, long time) {
		this.stop(world, entity, time);
	}

	@Override
	public final void stop(ServerWorld world, E entity, long time) {
		this.status = MultiTickTask.Status.STOPPED;
	}

	@Override
	public String getName() {
		return this.getClass().getSimpleName();
	}
}
