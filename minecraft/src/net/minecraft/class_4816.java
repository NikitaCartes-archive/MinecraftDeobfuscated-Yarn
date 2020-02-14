package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;

public class class_4816 extends Task<MobEntity> {
	private final double field_22307;
	private final int field_22308;
	private int field_22309 = 0;

	public class_4816(double d, int i) {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT));
		this.field_22307 = d;
		this.field_22308 = i;
	}

	protected boolean shouldRun(ServerWorld serverWorld, MobEntity mobEntity) {
		if (this.field_22309 > 0) {
			this.field_22309--;
			return false;
		} else {
			return !this.method_24585(mobEntity) && LookTargetUtil.method_24556(mobEntity, this.field_22307);
		}
	}

	private boolean method_24585(MobEntity mobEntity) {
		return mobEntity.method_24520(item -> item instanceof RangedWeaponItem);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		return LookTargetUtil.method_24556(mobEntity, this.field_22307);
	}

	protected void run(ServerWorld serverWorld, MobEntity mobEntity, long l) {
		LivingEntity livingEntity = method_24584(mobEntity);
		LookTargetUtil.lookAt(mobEntity, livingEntity);
		this.method_24586(mobEntity, livingEntity);
		this.field_22309 = this.field_22308;
	}

	private void method_24586(MobEntity mobEntity, LivingEntity livingEntity) {
		mobEntity.swingHand(Hand.MAIN_HAND);
		mobEntity.tryAttack(livingEntity);
	}

	private static LivingEntity method_24584(LivingEntity livingEntity) {
		return (LivingEntity)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}
}
