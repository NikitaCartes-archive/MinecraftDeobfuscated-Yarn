package net.minecraft;

import com.google.common.collect.ImmutableMap;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ProjectileUtil;
import net.minecraft.entity.ai.brain.EntityPosWrapper;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.ai.brain.task.LookTargetUtil;
import net.minecraft.entity.ai.brain.task.Task;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class class_4810<E extends MobEntity & CrossbowUser, T extends LivingEntity> extends Task<E> {
	private final int field_22292 = 8;
	private int field_22293;
	private class_4810.class_4811 field_22294 = class_4810.class_4811.field_22295;

	public class_4810() {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), 1200);
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
		return mobEntity.method_24518(Items.CROSSBOW) && LookTargetUtil.method_24556(mobEntity, 8.0);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E mobEntity, long l) {
		return this.shouldRun(serverWorld, mobEntity);
	}

	protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
		LivingEntity livingEntity = method_24568(mobEntity);
		this.method_24572(mobEntity, livingEntity);
		this.method_24569(mobEntity, livingEntity);
	}

	protected void finishRunning(ServerWorld serverWorld, E mobEntity, long l) {
		if (mobEntity.isUsingItem()) {
			mobEntity.clearActiveItem();
		}

		if (mobEntity.method_24518(Items.CROSSBOW)) {
			mobEntity.setCharging(false);
			CrossbowItem.setCharged(mobEntity.getActiveItem(), false);
		}
	}

	private void method_24569(E mobEntity, LivingEntity livingEntity) {
		if (this.field_22294 == class_4810.class_4811.field_22295) {
			mobEntity.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(mobEntity, Items.CROSSBOW));
			this.field_22294 = class_4810.class_4811.field_22296;
			mobEntity.setCharging(true);
		} else if (this.field_22294 == class_4810.class_4811.field_22296) {
			if (!mobEntity.isUsingItem()) {
				this.field_22294 = class_4810.class_4811.field_22295;
			}

			int i = mobEntity.getItemUseTime();
			ItemStack itemStack = mobEntity.getActiveItem();
			if (i >= CrossbowItem.getPullTime(itemStack)) {
				mobEntity.stopUsingItem();
				this.field_22294 = class_4810.class_4811.field_22297;
				this.field_22293 = 20 + mobEntity.getRandom().nextInt(20);
				mobEntity.setCharging(false);
			}
		} else if (this.field_22294 == class_4810.class_4811.field_22297) {
			this.field_22293--;
			if (this.field_22293 == 0) {
				this.field_22294 = class_4810.class_4811.field_22298;
			}
		} else if (this.field_22294 == class_4810.class_4811.field_22298) {
			mobEntity.attack(livingEntity, 1.0F);
			ItemStack itemStack2 = mobEntity.getStackInHand(ProjectileUtil.getHandPossiblyHolding(mobEntity, Items.CROSSBOW));
			CrossbowItem.setCharged(itemStack2, false);
			this.field_22294 = class_4810.class_4811.field_22295;
		}
	}

	private void method_24572(MobEntity mobEntity, LivingEntity livingEntity) {
		mobEntity.getBrain().putMemory(MemoryModuleType.LOOK_TARGET, new EntityPosWrapper(livingEntity));
	}

	private static LivingEntity method_24568(LivingEntity livingEntity) {
		return (LivingEntity)livingEntity.getBrain().getOptionalMemory(MemoryModuleType.ATTACK_TARGET).get();
	}

	static enum class_4811 {
		field_22295,
		field_22296,
		field_22297,
		field_22298;
	}
}
