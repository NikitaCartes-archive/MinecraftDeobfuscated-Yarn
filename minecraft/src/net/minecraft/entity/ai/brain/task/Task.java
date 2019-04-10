package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.Set;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.server.world.ServerWorld;

public abstract class Task<E extends LivingEntity> {
	private Task.Status status = Task.Status.field_18337;
	private long endTime;
	private final int minRunTime;
	private final int maxRunTime;

	public Task() {
		this(60);
	}

	public Task(int i) {
		this(i, i);
	}

	public Task(int i, int j) {
		this.minRunTime = i;
		this.maxRunTime = j;
	}

	public Task.Status getStatus() {
		return this.status;
	}

	public final boolean tryStarting(ServerWorld serverWorld, E livingEntity, long l) {
		if (this.hasRequiredMemoryState(livingEntity) && this.shouldRun(serverWorld, livingEntity)) {
			this.status = Task.Status.field_18338;
			int i = this.minRunTime + serverWorld.getRandom().nextInt(this.maxRunTime + 1 - this.minRunTime);
			this.endTime = l + (long)i;
			this.run(serverWorld, livingEntity, l);
			return true;
		} else {
			return false;
		}
	}

	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
	}

	public final void tick(ServerWorld serverWorld, E livingEntity, long l) {
		if (!this.isTimeLimitExceeded(l) && this.shouldKeepRunning(serverWorld, livingEntity, l)) {
			this.keepRunning(serverWorld, livingEntity, l);
		} else {
			this.stop(serverWorld, livingEntity, l);
		}
	}

	protected void keepRunning(ServerWorld serverWorld, E livingEntity, long l) {
	}

	public final void stop(ServerWorld serverWorld, E livingEntity, long l) {
		this.status = Task.Status.field_18337;
		this.finishRunning(serverWorld, livingEntity, l);
	}

	protected void finishRunning(ServerWorld serverWorld, E livingEntity, long l) {
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		return false;
	}

	protected boolean isTimeLimitExceeded(long l) {
		return l > this.endTime;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E livingEntity) {
		return true;
	}

	protected abstract Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState();

	public String toString() {
		return this.getClass().getSimpleName();
	}

	private boolean hasRequiredMemoryState(E livingEntity) {
		return this.getRequiredMemoryState().stream().allMatch(pair -> {
			MemoryModuleType<?> memoryModuleType = (MemoryModuleType<?>)pair.getFirst();
			MemoryModuleState memoryModuleState = (MemoryModuleState)pair.getSecond();
			return livingEntity.getBrain().isMemoryInState(memoryModuleType, memoryModuleState);
		});
	}

	public static enum Status {
		field_18337,
		field_18338;
	}
}
