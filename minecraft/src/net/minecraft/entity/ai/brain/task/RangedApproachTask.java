package net.minecraft.entity.ai.brain.task;

import java.util.Optional;
import java.util.function.Function;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LivingTargetCache;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.mob.MobEntity;

public class RangedApproachTask {
	private static final int WEAPON_REACH_REDUCTION = 1;

	public static Task<MobEntity> create(float speed) {
		return create(entity -> speed);
	}

	public static Task<MobEntity> create(Function<LivingEntity, Float> speed) {
		return TaskTriggerer.task(
			context -> context.group(
						context.queryMemoryOptional(MemoryModuleType.WALK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.LOOK_TARGET),
						context.queryMemoryValue(MemoryModuleType.ATTACK_TARGET),
						context.queryMemoryOptional(MemoryModuleType.VISIBLE_MOBS)
					)
					.apply(
						context,
						(walkTarget, lookTarget, attackTarget, visibleMobs) -> (world, entity, time) -> {
								LivingEntity livingEntity = context.getValue(attackTarget);
								Optional<LivingTargetCache> optional = context.getOptionalValue(visibleMobs);
								if (optional.isPresent()
									&& ((LivingTargetCache)optional.get()).contains(livingEntity)
									&& LookTargetUtil.isTargetWithinAttackRange(entity, livingEntity, 1)) {
									walkTarget.forget();
								} else {
									lookTarget.remember(new EntityLookTarget(livingEntity, true));
									walkTarget.remember(new WalkTarget(new EntityLookTarget(livingEntity, false), (Float)speed.apply(entity), 0));
								}

								return true;
							}
					)
		);
	}
}
