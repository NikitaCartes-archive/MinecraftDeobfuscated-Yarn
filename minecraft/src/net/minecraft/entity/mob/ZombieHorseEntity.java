package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class ZombieHorseEntity extends HorseBaseEntity {
	public ZombieHorseEntity(EntityType<? extends ZombieHorseEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(15.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.2F);
		this.getAttributeInstance(JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return SoundEvents.field_15154;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.field_14543;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return SoundEvents.field_15179;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.field_6048.create(this.world);
	}

	@Override
	public boolean interactMob(PlayerEntity playerEntity, Hand hand) {
		ItemStack itemStack = playerEntity.getStackInHand(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(playerEntity, hand);
		} else if (!this.isTame()) {
			return false;
		} else if (this.isBaby()) {
			return super.interactMob(playerEntity, hand);
		} else if (playerEntity.isSneaking()) {
			this.openInventory(playerEntity);
			return true;
		} else if (this.hasPassengers()) {
			return super.interactMob(playerEntity, hand);
		} else {
			if (!itemStack.isEmpty()) {
				if (!this.isSaddled() && itemStack.getItem() == Items.field_8175) {
					this.openInventory(playerEntity);
					return true;
				}

				if (itemStack.useOnEntity(playerEntity, this, hand)) {
					return true;
				}
			}

			this.putPlayerOnBack(playerEntity);
			return true;
		}
	}

	@Override
	protected void initCustomGoals() {
	}
}
