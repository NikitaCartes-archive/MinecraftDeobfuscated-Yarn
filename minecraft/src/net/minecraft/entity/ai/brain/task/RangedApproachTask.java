package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;

public class RangedApproachTask extends Task<MobEntity> {
	private final float speed;

	public RangedApproachTask(float speed) {
		super(
			ImmutableMap.of(
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_22355,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_18442,
				MemoryModuleState.field_18458
			)
		);
		this.speed = speed;
	}

	protected void method_25945(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		LivingEntity livingEntity = (LivingEntity)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.field_22355).get();
		if (LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.method_25940(mobEntity, livingEntity, 1)) {
			this.forgetWalkTarget(mobEntity);
		} else {
			this.rememberWalkTarget(mobEntity, livingEntity);
		}
	}

	private void rememberWalkTarget(LivingEntity entity, LivingEntity target) {
		Brain brain = entity.getBrain();
		brain.remember(MemoryModuleType.field_18446, new EntityLookTarget(target, true));
		WalkTarget walkTarget = new WalkTarget(new EntityLookTarget(target, false), this.speed, 0);
		brain.remember(MemoryModuleType.field_18445, walkTarget);
	}

	private void forgetWalkTarget(LivingEntity entity) {
		entity.getBrain().forget(MemoryModuleType.field_18445);
	}
}
