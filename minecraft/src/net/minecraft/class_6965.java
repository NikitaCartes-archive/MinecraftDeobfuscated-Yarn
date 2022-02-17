package net.minecraft;

import java.util.Map;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.server.world.ServerWorld;

public abstract class class_6965<E extends LivingEntity> extends Task<E> {
	public class_6965(Map<MemoryModuleType<?>, MemoryModuleState> map) {
		super(map);
	}

	public class_6965(Map<MemoryModuleType<?>, MemoryModuleState> map, int i) {
		super(map, i);
	}

	public class_6965(Map<MemoryModuleType<?>, MemoryModuleState> map, int i, int j) {
		super(map, i, j);
	}

	@Override
	protected boolean shouldRun(ServerWorld world, E entity) {
		return true;
	}

	@Override
	protected abstract void run(ServerWorld world, E entity, long time);

	@Override
	protected final boolean shouldKeepRunning(ServerWorld world, E entity, long time) {
		return false;
	}

	@Override
	protected final void keepRunning(ServerWorld world, E entity, long time) {
	}

	@Override
	protected void finishRunning(ServerWorld world, E entity, long time) {
	}

	@Override
	protected final boolean isTimeLimitExceeded(long time) {
		return false;
	}
}
