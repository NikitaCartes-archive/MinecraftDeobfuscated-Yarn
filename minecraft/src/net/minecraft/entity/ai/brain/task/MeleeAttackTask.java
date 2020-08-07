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
				MemoryModuleType.field_18446,
				MemoryModuleState.field_18458,
				MemoryModuleType.field_22355,
				MemoryModuleState.field_18456,
				MemoryModuleType.field_22475,
				MemoryModuleState.field_18457
			)
		);
		this.interval = interval;
	}

	protected boolean method_24588(ServerWorld serverWorld, MobEntity mobEntity) {
		LivingEntity livingEntity = this.method_25944(mobEntity);
		return !this.method_25942(mobEntity) && LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity) && LookTargetUtil.method_25941(mobEntity, livingEntity);
	}

	private boolean method_25942(MobEntity mobEntity) {
		return mobEntity.isHolding(item -> item instanceof RangedWeaponItem && mobEntity.canUseRangedWeapon((RangedWeaponItem)item));
	}

	protected void method_24590(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		LivingEntity livingEntity = this.method_25944(mobEntity);
		LookTargetUtil.lookAt(mobEntity, livingEntity);
		mobEntity.swingHand(Hand.field_5808);
		mobEntity.tryAttack(livingEntity);
		mobEntity.getBrain().remember(MemoryModuleType.field_22475, true, (long)this.interval);
	}

	private LivingEntity method_25944(MobEntity mobEntity) {
		return (LivingEntity)mobEntity.getBrain().getOptionalMemory(MemoryModuleType.field_22355).get();
	}
}
