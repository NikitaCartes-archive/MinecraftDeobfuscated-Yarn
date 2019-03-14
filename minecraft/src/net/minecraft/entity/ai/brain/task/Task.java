package net.minecraft.entity.ai.brain.task;

import com.mojang.datafixers.util.Pair;
import java.util.List;
import java.util.Set;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;

public abstract class Task<E extends LivingEntity> {
	private Task.Status status = Task.Status.field_18337;
	private long endTime;
	private final int minRunTime;
	private final int maxRunTime;

	public Task() {
		this(60, 60);
	}

	public Task(int i, int j) {
		this.minRunTime = i;
		this.maxRunTime = j;
	}

	public Task.Status getStatus() {
		return this.status;
	}

	public final boolean tryStarting(ServerWorld serverWorld, E livingEntity, long l) {
		if (this.getRequiredMemoryState()
				.stream()
				.allMatch(pair -> livingEntity.getBrain().isMemoryInState((MemoryModuleType<?>)pair.getFirst(), (MemoryModuleState)pair.getSecond()))
			&& this.shouldRun(serverWorld, livingEntity)) {
			this.status = Task.Status.field_18338;
			this.endTime = l + (long)this.minRunTime + (long)serverWorld.getRandom().nextInt(this.maxRunTime + 1 - this.minRunTime);
			this.run(serverWorld, livingEntity, l);
			return true;
		} else {
			return false;
		}
	}

	protected void run(ServerWorld serverWorld, E livingEntity, long l) {
	}

	public final boolean tick(ServerWorld serverWorld, E livingEntity, long l) {
		if (!this.isTimeLimitExceeded(l) && this.shouldKeepRunning(serverWorld, livingEntity, l)) {
			this.keepRunning(serverWorld, livingEntity, l);
			return true;
		} else {
			this.stop(serverWorld, livingEntity, l);
			return false;
		}
	}

	protected void keepRunning(ServerWorld serverWorld, E livingEntity, long l) {
	}

	public final void stop(ServerWorld serverWorld, E livingEntity, long l) {
		this.status = Task.Status.field_18337;
		this.method_18926(serverWorld, livingEntity, l);
	}

	protected void method_18926(ServerWorld serverWorld, E livingEntity, long l) {
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E livingEntity, long l) {
		return false;
	}

	protected boolean isTimeLimitExceeded(long l) {
		return l > this.endTime;
	}

	protected void method_18916(LivingEntity livingEntity, LivingEntity livingEntity2) {
		Brain<?> brain = livingEntity.getBrain();
		Brain<?> brain2 = livingEntity2.getBrain();
		brain.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntity2));
		brain2.putMemory(MemoryModuleType.field_18446, new EntityPosWrapper(livingEntity));
		brain.putMemory(
			MemoryModuleType.field_18445,
			new WalkTarget(new EntityPosWrapper(livingEntity2), (float)livingEntity.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue(), 2)
		);
		brain2.putMemory(
			MemoryModuleType.field_18445,
			new WalkTarget(new EntityPosWrapper(livingEntity), (float)livingEntity2.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue(), 2)
		);
	}

	protected boolean method_18918(Brain<?> brain, MemoryModuleType<? extends LivingEntity> memoryModuleType, EntityType<?> entityType) {
		if (brain.getMemory(memoryModuleType).isPresent() && brain.getMemory(MemoryModuleType.field_18442).isPresent()) {
			LivingEntity livingEntity = (LivingEntity)brain.getMemory(memoryModuleType).get();
			return livingEntity.getType() == entityType && livingEntity.isValid() && ((List)brain.getMemory(MemoryModuleType.field_18442).get()).contains(livingEntity);
		} else {
			return false;
		}
	}

	protected boolean shouldRun(ServerWorld serverWorld, E livingEntity) {
		return true;
	}

	protected abstract Set<Pair<MemoryModuleType<?>, MemoryModuleState>> getRequiredMemoryState();

	public static enum Status {
		field_18337,
		field_18338;
	}
}
