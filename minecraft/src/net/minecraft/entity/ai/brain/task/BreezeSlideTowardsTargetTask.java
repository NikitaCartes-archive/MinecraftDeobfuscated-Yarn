package net.minecraft.entity.ai.brain.task;

import java.util.Map;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.FuzzyTargeting;
import net.minecraft.entity.ai.NoPenaltyTargeting;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.BreezeEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Unit;
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
			boolean bl = breezeEntity.isWithinLargeRange(livingEntity.getPos());
			boolean bl2 = breezeEntity.isWithinMediumRange(livingEntity.getPos());
			boolean bl3 = breezeEntity.isWithinShortRange(livingEntity.getPos());
			Vec3d vec3d = null;
			if (bl) {
				vec3d = getRandomPosInMediumRange(breezeEntity, livingEntity);
			} else if (bl3) {
				Vec3d vec3d2 = NoPenaltyTargeting.findFrom(breezeEntity, 5, 5, livingEntity.getPos());
				if (vec3d2 != null && livingEntity.squaredDistanceTo(vec3d2.x, vec3d2.y, vec3d2.z) > livingEntity.squaredDistanceTo(breezeEntity)) {
					vec3d = vec3d2;
				}
			} else if (bl2) {
				vec3d = FuzzyTargeting.find(breezeEntity, 5, 3);
			}

			if (vec3d != null) {
				breezeEntity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(BlockPos.ofFloored(vec3d), 0.6F, 1));
			}
		}
	}

	protected void finishRunning(ServerWorld serverWorld, BreezeEntity breezeEntity, long l) {
		breezeEntity.getBrain().remember(MemoryModuleType.BREEZE_JUMP_COOLDOWN, Unit.INSTANCE, 20L);
	}

	private static Vec3d getRandomPosInMediumRange(BreezeEntity breeze, LivingEntity target) {
		Vec3d vec3d = target.getPos().subtract(breeze.getPos());
		double d = vec3d.length() - MathHelper.lerp(breeze.getRandom().nextDouble(), 8.0, 4.0);
		Vec3d vec3d2 = vec3d.normalize().multiply(d, d, d);
		return breeze.getPos().add(vec3d2);
	}
}
