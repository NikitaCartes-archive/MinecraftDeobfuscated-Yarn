package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.IntRange;

public class WalkTowardClosestAdultTask<E extends PassiveEntity> extends Task<E> {
	private final IntRange executionRange;
	private final float speed;

	public WalkTowardClosestAdultTask(IntRange executionRange, float speed) {
		super(ImmutableMap.of(MemoryModuleType.field_25359, MemoryModuleState.field_18456, MemoryModuleType.field_18445, MemoryModuleState.field_18457));
		this.executionRange = executionRange;
		this.speed = speed;
	}

	protected boolean method_29521(ServerWorld serverWorld, E passiveEntity) {
		if (!passiveEntity.isBaby()) {
			return false;
		} else {
			PassiveEntity passiveEntity2 = this.getNearestVisibleAdult(passiveEntity);
			return passiveEntity.isInRange(passiveEntity2, (double)(this.executionRange.getMax() + 1))
				&& !passiveEntity.isInRange(passiveEntity2, (double)this.executionRange.getMin());
		}
	}

	protected void method_29522(ServerWorld serverWorld, E passiveEntity, long l) {
		LookTargetUtil.walkTowards(passiveEntity, this.getNearestVisibleAdult(passiveEntity), this.speed, this.executionRange.getMin() - 1);
	}

	private PassiveEntity getNearestVisibleAdult(E entity) {
		return (PassiveEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.field_25359).get();
	}
}
