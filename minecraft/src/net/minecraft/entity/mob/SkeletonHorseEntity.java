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
		return createBaseHorseAttributes().add(EntityAttributes.field_23716, 15.0).add(EntityAttributes.field_23719, 0.2F);
	}

	@Override
	protected void initAttributes() {
		this.getAttributeInstance(EntityAttributes.field_23728).setBaseValue(this.getChildJumpStrengthBonus());
	}

	@Override
	protected void initCustomGoals() {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		super.getAmbientSound();
		return this.isSubmergedIn(FluidTags.field_15517) ? SoundEvents.field_14686 : SoundEvents.field_14984;
	}

	@Override
	protected SoundEvent getDeathSound() {
		super.getDeathSound();
		return SoundEvents.field_14721;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		super.getHurtSound(source);
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
	public PassiveEntity createChild(ServerWorld serverWorld, PassiveEntity passiveEntity) {
		return EntityType.field_6075.create(serverWorld);
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
				if (itemStack.getItem() == Items.field_8175 && !this.isSaddled()) {
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
