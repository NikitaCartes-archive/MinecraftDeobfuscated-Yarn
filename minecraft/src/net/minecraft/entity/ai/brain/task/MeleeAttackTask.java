package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class MeleeAttackTask extends Task<MobEntity> {
	private final double range;
	private final int interval;
	private int cooldown = 0;

	public MeleeAttackTask(double range, int interval) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT));
		this.range = range;
		this.interval = interval;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		if (this.cooldown > 0) {
			this.cooldown--;
			return false;
		} else {
			return !this.isHoldingRangedWeapon(mobEntity) && LookTargetUtil.isAttackTargetClose(mobEntity, this.range);
		}
	}

	private boolean isHoldingRangedWeapon(MobEntity entity) {
		return entity.isHolding(item -> item instanceof RangedWeaponItem);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return LookTargetUtil.isAttackTargetClose(mobEntity, this.range);
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		LivingEntity livingEntity = getAttackTarget(mobEntity);
		LookTargetUtil.lookAt(mobEntity, livingEntity);
		this.attack(mobEntity, livingEntity);
		this.cooldown = this.interval;
	}

	private void attack(MobEntity entity, LivingEntity target) {
		entity.swingHand(Hand.MAIN_HAND);
		entity.tryAttack(target);
	}

	private static LivingEntity getAttackTarget(LivingEntity entity) {
		return (LivingEntity)entity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}
