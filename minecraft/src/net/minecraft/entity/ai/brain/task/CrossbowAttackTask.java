package net.minecraft.entity.ai.brain.task;

import com.google.common.collect.ImmutableMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.entity.CrossbowUser;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.brain.EntityLookTarget;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.server.world.ServerWorld;

public class CrossbowAttackTask<E extends MobEntity & CrossbowUser, T extends LivingEntity> extends MultiTickTask<E> {
	private static final int RUN_TIME = 1200;
	private int chargingCooldown;
	private CrossbowAttackTask.CrossbowState state = CrossbowAttackTask.CrossbowState.UNCHARGED;

	public CrossbowAttackTask() {
		super(ImmutableMap.of(MemoryModuleType.LOOK_TARGET, MemoryModuleState.REGISTERED, MemoryModuleType.ATTACK_TARGET, MemoryModuleState.VALUE_PRESENT), 1200);
	}

	protected boolean shouldRun(ServerWorld serverWorld, E mobEntity) {
		LivingEntity livingEntity = getAttackTarget(mobEntity);
		return mobEntity.isHolding(Items.CROSSBOW)
			&& LookTargetUtil.isVisibleInMemory(mobEntity, livingEntity)
			&& LookTargetUtil.isTargetWithinAttackRange(mobEntity, livingEntity, 0);
	}

	protected boolean shouldKeepRunning(ServerWorld serverWorld, E mobEntity, long l) {
		return mobEntity.getBrain().hasMemoryModule(MemoryModuleType.ATTACK_TARGET) && this.shouldRun(serverWorld, mobEntity);
	}

	protected void keepRunning(ServerWorld serverWorld, E mobEntity, long l) {
		LivingEntity livingEntity = getAttackTarget(mobEntity);
		this.setLookTarget(mobEntity, livingEntity);
		this.tickState(mobEntity, livingEntity);
	}

	protected void finishRunning(ServerWorld serverWorld, E mobEntity, long l) {
		if (mobEntity.isUsingItem()) {
			mobEntity.clearActiveItem();
		}

		if (mobEntity.isHolding(Items.CROSSBOW)) {
			mobEntity.setCharging(false);
			mobEntity.getActiveItem().set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
		}
	}

	private void tickState(E entity, LivingEntity target) {
		if (this.state == CrossbowAttackTask.CrossbowState.UNCHARGED) {
			entity.setCurrentHand(ProjectileUtil.getHandPossiblyHolding(entity, Items.CROSSBOW));
			this.state = CrossbowAttackTask.CrossbowState.CHARGING;
			entity.setCharging(true);
		} else if (this.state == CrossbowAttackTask.CrossbowState.CHARGING) {
			if (!entity.isUsingItem()) {
				this.state = CrossbowAttackTask.CrossbowState.UNCHARGED;
			}

			int i = entity.getItemUseTime();
			ItemStack itemStack = entity.getActiveItem();
			if (i >= CrossbowItem.getPullTime(itemStack, entity)) {
				entity.stopUsingItem();
				this.state = CrossbowAttackTask.CrossbowState.CHARGED;
				this.chargingCooldown = 20 + entity.getRandom().nextInt(20);
				entity.setCharging(false);
			}
		} else if (this.state == CrossbowAttackTask.CrossbowState.CHARGED) {
			this.chargingCooldown--;
			if (this.chargingCooldown == 0) {
				this.state = CrossbowAttackTask.CrossbowState.READY_TO_ATTACK;
			}
		} else if (this.state == CrossbowAttackTask.CrossbowState.READY_TO_ATTACK) {
			entity.shootAt(target, 1.0F);
			this.state = CrossbowAttackTask.CrossbowState.UNCHARGED;
		}
	}

	private void setLookTarget(MobEntity entity, LivingEntity target) {
		entity.getBrain().remember(MemoryModuleType.LOOK_TARGET, new EntityLookTarget(target, true));
	}

	private static LivingEntity getAttackTarget(LivingEntity entity) {
		return (LivingEntity)entity.getBrain().getOptionalRegisteredMemory(MemoryModuleType.ATTACK_TARGET).get();
	}

	static enum CrossbowState {
		UNCHARGED,
		CHARGING,
		CHARGED,
		READY_TO_ATTACK;
	}
}
