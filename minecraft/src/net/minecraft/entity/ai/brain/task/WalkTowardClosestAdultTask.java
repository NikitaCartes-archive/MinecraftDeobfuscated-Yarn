package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.intprovider.UniformIntProvider;

public class WalkTowardClosestAdultTask<E extends PassiveEntity> extends Task<E> {
	private final UniformIntProvider executionRange;
	private final Function<LivingEntity, Float> speed;

	public WalkTowardClosestAdultTask(UniformIntProvider executionRange, float speed) {
		this(executionRange, entity -> speed);
	}

	public WalkTowardClosestAdultTask(UniformIntProvider executionRange, Function<LivingEntity, Float> speed) {
		super(ImmutableMap.of(MemoryModuleType.NEAREST_VISIBLE_ADULT, MemoryModuleState.VALUE_PRESENT, MemoryModuleType.WALK_TARGET, MemoryModuleState.VALUE_ABSENT));
		this.executionRange = executionRange;
		this.speed = speed;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E passiveEntity) {
		if (!passiveEntity.isBaby()) {
			return false;
		} else {
			PassiveEntity passiveEntity2 = this.getNearestVisibleAdult(passiveEntity);
			return passiveEntity.isInRange(passiveEntity2, (double)(this.executionRange.getMax() + 1))
				&& !passiveEntity.isInRange(passiveEntity2, (double)this.executionRange.getMin());
		}
	}

	protected void run(ServerWorld serverWorld, E passiveEntity, long l) {
		LookTargetUtil.walkTowards(
			passiveEntity, this.getNearestVisibleAdult(passiveEntity), (Float)this.speed.apply(passiveEntity), this.executionRange.getMin() - 1
		);
	}

	private PassiveEntity getNearestVisibleAdult(E entity) {
		return (PassiveEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.NEAREST_VISIBLE_ADULT).get();
	}
}
