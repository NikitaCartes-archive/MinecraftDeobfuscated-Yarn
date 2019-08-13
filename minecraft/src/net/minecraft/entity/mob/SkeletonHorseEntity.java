package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SkeletonHorseTrapTriggerGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SkeletonHorseEntity extends HorseBaseEntity {
	private final SkeletonHorseTrapTriggerGoal field_7003 = new SkeletonHorseTrapTriggerGoal(this);
	private boolean trapped;
	private int trapTime;

	public SkeletonHorseEntity(EntityType<? extends SkeletonHorseEntity> entityType, World world) {
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
	protected void initCustomGoals() {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return this.isInFluid(FluidTags.field_15517) ? SoundEvents.field_14686 : SoundEvents.field_14984;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.field_14721;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		super.getHurtSound(damageSource);
		return SoundEvents.field_14855;
	}

	@Override
	protected SoundEvent getSwimSound() {
		if (this.onGround) {
			if (!this.hasPassengers()) {
				return SoundEvents.field_15182;
			}

			this.soundTicks++;
			if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
				return SoundEvents.field_15108;
			}

			if (this.soundTicks <= 5) {
				return SoundEvents.field_15182;
			}
		}

		return SoundEvents.field_14617;
	}

	@Override
	protected void playSwimSound(float f) {
		if (this.onGround) {
			super.playSwimSound(0.3F);
		} else {
			super.playSwimSound(Math.min(0.1F, f * 25.0F));
		}
	}

	@Override
	protected void playJumpSound() {
		if (this.isInsideWater()) {
			this.playSound(SoundEvents.field_14901, 0.4F, 1.0F);
		} else {
			super.playJumpSound();
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	public double getMountedHeightOffset() {
		return super.getMountedHeightOffset() - 0.1875;
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.isTrapped() && this.trapTime++ >= 18000) {
			this.remove();
		}
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putBoolean("SkeletonTrap", this.isTrapped());
		compoundTag.putInt("SkeletonTrapTime", this.trapTime);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		this.setTrapped(compoundTag.getBoolean("SkeletonTrap"));
		this.trapTime = compoundTag.getInt("SkeletonTrapTime");
	}

	@Override
	public boolean canBeRiddenInWater() {
		return true;
	}

	@Override
	protected float getBaseMovementSpeedMultiplier() {
		return 0.96F;
	}

	public boolean isTrapped() {
		return this.trapped;
	}

	public void setTrapped(boolean bl) {
		if (bl != this.trapped) {
			this.trapped = bl;
			if (bl) {
				this.goalSelector.add(1, this.field_7003);
			} else {
				this.goalSelector.remove(this.field_7003);
			}
		}
	}

	@Nullable
	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.field_6075.create(this.world);
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
				if (itemStack.getItem() == Items.field_8175 && !this.isSaddled()) {
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
}
