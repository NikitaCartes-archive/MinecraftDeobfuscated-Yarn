package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ai.goal.SkeletonHorseTrapTriggerGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.HorseBaseEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;

public class SkeletonHorseEntity extends HorseBaseEntity {
	private final SkeletonHorseTrapTriggerGoal trapTriggerGoal = new SkeletonHorseTrapTriggerGoal(this);
	private boolean trapped;
	private int trapTime;

	public SkeletonHorseEntity(EntityType<? extends SkeletonHorseEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createSkeletonHorseAttributes() {
		return createBaseHorseAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
	}

	@Override
	protected void initAttributes() {
		this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(this.getChildJumpStrengthBonus());
	}

	@Override
	protected void initCustomGoals() {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return this.isSubmergedIn(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		super.getHurtSound(source);
		return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
	}

	@Override
	protected SoundEvent getSwimSound() {
		if (this.onGround) {
			if (!this.hasPassengers()) {
				return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
			}

			this.soundTicks++;
			if (this.soundTicks > 5 && this.soundTicks % 3 == 0) {
				return SoundEvents.ENTITY_SKELETON_HORSE_GALLOP_WATER;
			}

			if (this.soundTicks <= 5) {
				return SoundEvents.ENTITY_SKELETON_HORSE_STEP_WATER;
			}
		}

		return SoundEvents.ENTITY_SKELETON_HORSE_SWIM;
	}

	@Override
	protected void playSwimSound(float volume) {
		if (this.onGround) {
			super.playSwimSound(0.3F);
		} else {
			super.playSwimSound(Math.min(0.1F, volume * 25.0F));
		}
	}

	@Override
	protected void playJumpSound() {
		if (this.isTouchingWater()) {
			this.playSound(SoundEvents.ENTITY_SKELETON_HORSE_JUMP_WATER, 0.4F, 1.0F);
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
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putBoolean("SkeletonTrap", this.isTrapped());
		tag.putInt("SkeletonTrapTime", this.trapTime);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		this.setTrapped(tag.getBoolean("SkeletonTrap"));
		this.trapTime = tag.getInt("SkeletonTrapTime");
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

	public void setTrapped(boolean trapped) {
		if (trapped != this.trapped) {
			this.trapped = trapped;
			if (trapped) {
				this.goalSelector.add(1, this.trapTriggerGoal);
			} else {
				this.goalSelector.remove(this.trapTriggerGoal);
			}
		}
	}

	@Nullable
	@Override
	public PassiveEntity createChild(ServerWorld world, PassiveEntity entity) {
		return EntityType.SKELETON_HORSE.create(world);
	}

	@Override
	public ActionResult interactMob(PlayerEntity player, Hand hand) {
		ItemStack itemStack = player.getStackInHand(hand);
		if (!this.isTame()) {
			return ActionResult.PASS;
		} else if (this.isBaby()) {
			return super.interactMob(player, hand);
		} else if (player.shouldCancelInteraction()) {
			this.openInventory(player);
			return ActionResult.success(this.world.isClient);
		} else if (this.hasPassengers()) {
			return super.interactMob(player, hand);
		} else {
			if (!itemStack.isEmpty()) {
				if (itemStack.getItem() == Items.SADDLE && !this.isSaddled()) {
					this.openInventory(player);
					return ActionResult.success(this.world.isClient);
				}

				ActionResult actionResult = itemStack.useOnEntity(player, this, hand);
				if (actionResult.isAccepted()) {
					return actionResult;
				}
			}

			this.putPlayerOnBack(player);
			return ActionResult.success(this.world.isClient);
		}
	}
}
