package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.MathHelper;

public class AttackTask<E extends MobEntity> extends Task<E> {
	private final int distance;
	private final float forwardMovement;

	public AttackTask(int distance, float forwardMovement) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.VALUE_PRESENT
			)
		);
		this.distance = distance;
		this.forwardMovement = forwardMovement;
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
		return this.isAttackTargetVisible(mobEntity) && this.isNearAttackTarget(mobEntity);
	}

	protected void run(ServerWorld serverWorld, E mobEntity, long l) {
		mobEntity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(this.getAttackTarget(mobEntity), true));
		mobEntity.getMoveControl().strafeTo(-this.forwardMovement, 0.0F);
		mobEntity.yaw = MathHelper.stepAngleTowards(mobEntity.yaw, mobEntity.headYaw, 0.0F);
	}

	private boolean isAttackTargetVisible(E entity) {
		return ((List)entity.getBrain().getOptionalMemory(MemoryModuleType.VISIBLE_MOBS).get()).contains(this.getAttackTarget(entity));
	}

	private boolean isNearAttackTarget(E entity) {
		return this.getAttackTarget(entity).isInRange(entity, (double)this.distance);
	}

	private LivingEntity getAttackTarget(E entity) {
		return (LivingEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}
