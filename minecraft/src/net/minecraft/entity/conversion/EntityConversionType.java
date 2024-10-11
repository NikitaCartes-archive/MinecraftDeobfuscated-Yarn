package net.minecraft.entity.conversion;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ai.brain.Brain;
import net.minecraft.entity.ai.brain.MemoryModuleState;
import net.minecraft.entity.ai.brain.MemoryModuleType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.scoreboard.Scoreboard;

public enum EntityConversionType {
	SINGLE(true) {
		@Override
		void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
			Entity entity = oldEntity.getFirstPassenger();
			newEntity.copyPositionAndRotation(oldEntity);
			newEntity.setVelocity(oldEntity.getVelocity());
			if (entity != null) {
				entity.stopRiding();
				entity.ridingCooldown = 0;

				for (Entity entity2 : newEntity.getPassengerList()) {
					entity2.stopRiding();
					entity2.remove(Entity.RemovalReason.DISCARDED);
				}

				entity.startRiding(newEntity);
			}

			Entity entity3 = oldEntity.getVehicle();
			if (entity3 != null) {
				oldEntity.stopRiding();
				newEntity.startRiding(entity3);
			}

			if (context.keepEquipment()) {
				for (EquipmentSlot equipmentSlot : EquipmentSlot.VALUES) {
					ItemStack itemStack = oldEntity.getEquippedStack(equipmentSlot);
					if (!itemStack.isEmpty()) {
						newEntity.equipStack(equipmentSlot, itemStack.copyAndEmpty());
						newEntity.setEquipmentDropChance(equipmentSlot, oldEntity.getDropChance(equipmentSlot));
					}
				}
			}

			newEntity.fallDistance = oldEntity.fallDistance;
			newEntity.setFlag(Entity.GLIDING_FLAG_INDEX, oldEntity.isGliding());
			newEntity.playerHitTimer = oldEntity.playerHitTimer;
			newEntity.hurtTime = oldEntity.hurtTime;
			newEntity.bodyYaw = oldEntity.bodyYaw;
			newEntity.setOnGround(oldEntity.isOnGround());
			oldEntity.getSleepingPosition().ifPresent(newEntity::setSleepingPosition);
			Entity entity2 = oldEntity.getLeashHolder();
			if (entity2 != null) {
				newEntity.attachLeash(entity2, true);
			}

			this.copyData(oldEntity, newEntity, context);
		}
	},
	SPLIT_ON_DEATH(false) {
		@Override
		void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
			Entity entity = oldEntity.getFirstPassenger();
			if (entity != null) {
				entity.stopRiding();
			}

			Entity entity2 = oldEntity.getLeashHolder();
			if (entity2 != null) {
				oldEntity.detachLeash(true, true);
			}

			this.copyData(oldEntity, newEntity, context);
		}
	};

	private final boolean discardOldEntity;

	EntityConversionType(final boolean discardOldEntity) {
		this.discardOldEntity = discardOldEntity;
	}

	public boolean shouldDiscardOldEntity() {
		return this.discardOldEntity;
	}

	public abstract void setUpNewEntity(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context);

	void copyData(MobEntity oldEntity, MobEntity newEntity, EntityConversionContext context) {
		newEntity.setAbsorptionAmount(oldEntity.getAbsorptionAmount());

		for (StatusEffectInstance statusEffectInstance : oldEntity.getStatusEffects()) {
			newEntity.addStatusEffect(new StatusEffectInstance(statusEffectInstance));
		}

		if (oldEntity.isBaby()) {
			newEntity.setBaby(true);
		}

		if (oldEntity instanceof PassiveEntity passiveEntity && newEntity instanceof PassiveEntity passiveEntity2) {
			passiveEntity2.setBreedingAge(passiveEntity.getBreedingAge());
			passiveEntity2.forcedAge = passiveEntity.forcedAge;
			passiveEntity2.happyTicksRemaining = passiveEntity.happyTicksRemaining;
		}

		Brain<?> brain = oldEntity.getBrain();
		Brain<?> brain2 = newEntity.getBrain();
		if (brain.isMemoryInState(MemoryModuleType.ANGRY_AT, MemoryModuleState.REGISTERED) && brain.hasMemoryModule(MemoryModuleType.ANGRY_AT)) {
			brain2.remember(MemoryModuleType.ANGRY_AT, brain.getOptionalRegisteredMemory(MemoryModuleType.ANGRY_AT));
		}

		if (context.preserveCanPickUpLoot()) {
			newEntity.setCanPickUpLoot(oldEntity.canPickUpLoot());
		}

		newEntity.setLeftHanded(oldEntity.isLeftHanded());
		newEntity.setAiDisabled(oldEntity.isAiDisabled());
		if (oldEntity.isPersistent()) {
			newEntity.setPersistent();
		}

		if (oldEntity.hasCustomName()) {
			newEntity.setCustomName(oldEntity.getCustomName());
			newEntity.setCustomNameVisible(oldEntity.isCustomNameVisible());
		}

		newEntity.setOnFire(oldEntity.isOnFire());
		newEntity.setInvulnerable(oldEntity.isInvulnerable());
		newEntity.setNoGravity(oldEntity.hasNoGravity());
		newEntity.setPortalCooldown(oldEntity.getPortalCooldown());
		newEntity.setSilent(oldEntity.isSilent());
		oldEntity.getCommandTags().forEach(newEntity::addCommandTag);
		if (context.team() != null) {
			Scoreboard scoreboard = newEntity.getWorld().getScoreboard();
			scoreboard.addScoreHolderToTeam(newEntity.getUuidAsString(), context.team());
			if (oldEntity.getScoreboardTeam() != null && oldEntity.getScoreboardTeam() == context.team()) {
				scoreboard.removeScoreHolderFromTeam(oldEntity.getUuidAsString(), oldEntity.getScoreboardTeam());
			}
		}

		if (oldEntity instanceof ZombieEntity zombieEntity && zombieEntity.canBreakDoors() && newEntity instanceof ZombieEntity zombieEntity2) {
			zombieEntity2.setCanBreakDoors(true);
		}
	}
}
