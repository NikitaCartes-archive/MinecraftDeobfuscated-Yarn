package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.LookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.WalkTarget;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.server.world.ServerWorld;

public class class_4822 extends Task<LivingEntity> {
	private final float field_22323;

	public class_4822(float f) {
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
		this.field_22323 = f;
	}

	@Override
	protected void run(ServerWorld world, LivingEntity entity, long time) {
		if (LookTargetUtil.method_24556(entity, this.method_24608(entity))) {
			this.method_24604(entity);
		} else {
			this.method_24605(entity, method_24607(entity));
		}
	}

	private void method_24605(LivingEntity livingEntity, LivingEntity livingEntity2) {
		LookTarget lookTarget = new EntityPosWrapper(livingEntity2);
		livingEntity.getBrain().putMemory(MemoryModuleType.WALK_TARGET, new WalkTarget(lookTarget, this.field_22323, 0));
	}

	private void method_24604(LivingEntity livingEntity) {
		livingEntity.getBrain().forget(MemoryModuleType.WALK_TARGET);
	}

	private static LivingEntity method_24607(LivingEntity livingEntity) {
		return (LivingEntity)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}

	private double method_24608(LivingEntity livingEntity) {
		return Math.max(this.method_24606(livingEntity.getMainHandStack()), this.method_24606(livingEntity.getOffHandStack()));
	}

	private double method_24606(ItemStack itemStack) {
		Item item = itemStack.getItem();
		return item instanceof RangedWeaponItem ? (double)((RangedWeaponItem)item).method_24792() - 1.0 : 1.5;
	}
}
