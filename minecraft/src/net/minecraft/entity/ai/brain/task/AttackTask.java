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
				MemoryModuleType.field_18445,
				MemoryModuleState.field_18457,
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_22355,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_18442,
				MemoryModuleState.field_18456
			)
		);
		this.distance = distance;
		this.forwardMovement = forwardMovement;
	}

	protected boolean method_24552(ServerWorld serverWorld, E mobEntity) {
		return this.isAttackTargetVisible(mobEntity) && this.isNearAttackTarget(mobEntity);
	}

	protected void method_24553(ServerWorld serverWorld, E mobEntity, long l) {
		mobEntity.getBrain().remember(MemoryModuleType.field_18446, new EntityLookTarget(this.getAttackTarget(mobEntity), true));
		mobEntity.getMoveControl().strafeTo(-this.forwardMovement, 0.0F);
		mobEntity.yaw = MathHelper.stepAngleTowards(mobEntity.yaw, mobEntity.headYaw, 0.0F);
	}

	private boolean isAttackTargetVisible(E entity) {
		return ((List)entity.getBrain().getOptionalMemory(MemoryModuleType.field_18442).get()).contains(this.getAttackTarget(entity));
	}

	private boolean isNearAttackTarget(E entity) {
		return this.getAttackTarget(entity).isInRange(entity, (double)this.distance);
	}

	private LivingEntity getAttackTarget(E entity) {
		return (LivingEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.field_22355).get();
	}
}
