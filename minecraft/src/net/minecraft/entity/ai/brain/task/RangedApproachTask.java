package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;

public class RangedApproachTask extends Task<LivingEntity> {
	private final float speed;

	public RangedApproachTask(float speed) {
		super(
			ImmutableMap.of(
				MemoryModuleType.WALK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.VISIBLE_MOBS,
				MemoryModuleState.REGISTERED
			)
		);
		this.speed = speed;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		if (LookTargetUtil.isAttackTargetClose(entity, this.getWeaponRange(entity))) {
			this.forgetWalkTarget(entity);
		} else {
			this.rememberWalkTarget(entity, getAttackTarget(entity));
		}
	}

	private void rememberWalkTarget(LivingEntity entity, LivingEntity target) {
		LookTarget lookTarget = new EntityLookTarget(target);
		entity.getBrain().remember(MemoryModuleType.WALK_TARGET, new WalkTarget(lookTarget, this.speed, 0));
	}

	private void forgetWalkTarget(LivingEntity entity) {
		entity.getBrain().forget(MemoryModuleType.WALK_TARGET);
	}

	private static LivingEntity getAttackTarget(LivingEntity entity) {
		return (LivingEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}

	private double getWeaponRange(LivingEntity entity) {
		return Math.max(this.getRange(entity.getMainHandStack()), this.getRange(entity.getOffHandStack()));
	}

	private double getRange(ItemStack weapon) {
		Item item = weapon.getItem();
		return item instanceof RangedWeaponItem ? (double)((RangedWeaponItem)item).getRange() - 1.0 : 1.5;
	}
}
