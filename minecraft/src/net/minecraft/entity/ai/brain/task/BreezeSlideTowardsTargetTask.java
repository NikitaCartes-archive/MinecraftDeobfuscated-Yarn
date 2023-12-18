package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class BreezeSlideTowardsTargetTask extends MultiTickTask<BreezeEntity> {
	public BreezeSlideTowardsTargetTask() {
		super(
			Map.of(
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.BREEZE_JUMP_COOLDOWN,
				MemoryModuleState.VALUE_ABSENT,
				MemoryModuleType.BREEZE_SHOOT,
				MemoryModuleState.VALUE_ABSENT
			)
		);
	}

	protected boolean shouldRun(ServerWorld serverWorld, BreezeEntity breezeEntity) {
		return breezeEntity.isOnGround() && !breezeEntity.isTouchingWater() && breezeEntity.getPose() == EntityPose.STANDING;
	}

	protected void run(ServerWorld serverWorld, BreezeEntity breezeEntity, long l) {
		LivingEntity livingEntity = (LivingEntity)breezeEntity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);
		if (livingEntity != null) {
			boolean bl = breezeEntity.isWithinShortRange(livingEntity.getPos());
			Vec3d vec3d = null;
			if (bl) {
				Vec3d vec3d2 = NoPenaltyTargeting.findFrom(breezeEntity, 5, 5, livingEntity.getPos());
				if (vec3d2 != null
					&& BreezeMovementUtil.canMoveTo(breezeEntity, vec3d2)
					&& livingEntity.squaredDistanceTo(vec3d2.x, vec3d2.y, vec3d2.z) > livingEntity.squaredDistanceTo(breezeEntity)) {
					vec3d = vec3d2;
				}
			}

			if (vec3d == null) {
				vec3d = breezeEntity.getRandom().nextBoolean()
					? BreezeMovementUtil.getRandomPosBehindTarget(livingEntity, breezeEntity.getRandom())
					: getRandomPosInMediumRange(breezeEntity, livingEntity);
			}

			breezeEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(BlockPos.ofFloored(vec3d), 0.6F, 1));
		}
	}

	private static Vec3d getRandomPosInMediumRange(BreezeEntity breeze, LivingEntity target) {
		Vec3d vec3d = target.getPos().subtract(breeze.getPos());
		double d = vec3d.length() - MathHelper.lerp(breeze.getRandom().nextDouble(), 8.0, 4.0);
		Vec3d vec3d2 = vec3d.normalize().multiply(d, d, d);
		return breeze.getPos().add(vec3d2);
	}
}
