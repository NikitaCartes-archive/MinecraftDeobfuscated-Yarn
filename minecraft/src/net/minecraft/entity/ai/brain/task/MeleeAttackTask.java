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

	public MeleeAttackTask(double range, int interval) {
		super(
			ImmutableMap.of(
				MemoryModuleType.LOOK_TARGET,
				MemoryModuleState.REGISTERED,
				MemoryModuleType.ATTACK_TARGET,
				MemoryModuleState.VALUE_PRESENT,
				MemoryModuleType.ATTACK_COOLING_DOWN,
				MemoryModuleState.VALUE_ABSENT
			)
		);
		this.range = range;
		this.interval = interval;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		return !mobEntity.isHolding(item -> item instanceof RangedWeaponItem) && LookTargetUtil.isAttackTargetClose(mobEntity, this.range);
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		LivingEntity livingEntity = (LivingEntity)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
		LookTargetUtil.lookAt(mobEntity, livingEntity);
		mobEntity.swingHand(Hand.MAIN_HAND);
		mobEntity.tryAttack(livingEntity);
		mobEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.interval);
	}
}
