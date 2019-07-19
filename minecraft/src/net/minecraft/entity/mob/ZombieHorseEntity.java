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
		return SoundEvents.ENTITY_ZOMBIE_HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.ENTITY_ZOMBIE_HORSE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		super.getHurtSound(source);
		return SoundEvents.ENTITY_ZOMBIE_HORSE_HURT;
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity mate) {
		return EntityType.ZOMBIE_HORSE.create(this.world);
	}

	@Override
	public boolean interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (itemStack.getItem() instanceof SpawnEggItem) {
			return super.interactMob(player, hand);
		} else if (!this.isTame()) {
			return false;
		} else if (this.isBaby()) {
			return super.interactMob(player, hand);
		} else if (player.isSneaking()) {
			this.openInventory(player);
			return true;
		} else if (this.hasPassengers()) {
			return super.interactMob(player, hand);
		} else {
			if (!itemStack.isEmpty()) {
				if (!this.isSaddled() && itemStack.getItem() == Items.SADDLE) {
					this.openInventory(player);
					return true;
				}

				if (itemStack.useOnEntity(player, this, hand)) {
					return true;
				}
			}

			this.putPlayerOnBack(player);
			return true;
		}
	}

	@Override
	protected void initCustomGoals() {
	}
}
