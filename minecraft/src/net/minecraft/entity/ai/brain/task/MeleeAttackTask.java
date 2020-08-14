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
	private final int interval;

	public MeleeAttackTask(int interval) {
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
		this.interval = interval;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		LivingEntity livingEntity = this.method_25944(mobEntity);
		return !this.method_25942(mobEntity) && LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.method_25941(mobEntity, livingEntity);
	}

	private boolean method_25942(MobEntity mobEntity) {
		return mobEntity.isHolding(item -> item instanceof RangedWeaponItem && mobEntity.canUseRangedWeapon((RangedWeaponItem)item));
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		LivingEntity livingEntity = this.method_25944(mobEntity);
		LookTargetUtil.lookAt(mobEntity, livingEntity);
		mobEntity.swingHand(Hand.MAIN_HAND);
		mobEntity.tryAttack(livingEntity);
		mobEntity.getBrain().remember(MemoryModuleType.ATTACK_COOLING_DOWN, true, (long)this.interval);
	}

	private LivingEntity method_25944(MobEntity mobEntity) {
		return (LivingEntity)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}
