package net.minecraft.entity.mob;

import javax.annotation.Nullable;
import net.minecraft.entity.EntityAttachmentType;
import net.minecraft.entity.EntityAttachments;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.SkeletonHorseTrapTriggerGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.passive.AbstractHorseEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

public class SkeletonHorseEntity extends AbstractHorseEntity {
	private final SkeletonHorseTrapTriggerGoal trapTriggerGoal = new SkeletonHorseTrapTriggerGoal(this);
	private static final int DESPAWN_AGE = 18000;
	private static final EntityDimensions BABY_BASE_DIMENSIONS = EntityType.SKELETON_HORSE
		.getDimensions()
		.withAttachments(EntityAttachments.builder().add(EntityAttachmentType.PASSENGER, 0.0F, EntityType.SKELETON_HORSE.getHeight() - 0.03125F, 0.0F))
		.scaled(0.5F);
	private boolean trapped;
	private int trapTime;

	public SkeletonHorseEntity(EntityType<? extends SkeletonHorseEntity> entityType, World world) {
		super(entityType, world);
	}

	public static DefaultAttributeContainer.Builder createSkeletonHorseAttributes() {
		return createBaseHorseAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 15.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.2F);
	}

	public static boolean canSpawn(EntityType<? extends AnimalEntity> type, WorldAccess world, SpawnReason reason, BlockPos pos, Random random) {
		return !SpawnReason.isAnySpawner(reason)
			? AnimalEntity.isValidNaturalSpawn(type, world, reason, pos, random)
			: SpawnReason.isTrialSpawner(reason) || isLightLevelValidForNaturalSpawn(world, pos);
	}

	@Override
	protected void initAttributes(Random random) {
		this.getAttributeInstance(EntityAttributes.HORSE_JUMP_STRENGTH).setBaseValue(getChildJumpStrengthBonus(random::nextDouble));
	}

	@Override
	protected void initCustomGoals() {
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isSubmergedIn(FluidTags.WATER) ? SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT_WATER : SoundEvents.ENTITY_SKELETON_HORSE_AMBIENT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SKELETON_HORSE_DEATH;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SKELETON_HORSE_HURT;
	}

	@Override
	protected SoundEvent getSwimSound() {
		if (this.isOnGround()) {
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
		if (this.isOnGround()) {
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
	public EntityDimensions getBaseDimensions(EntityPose pose) {
		return this.isBaby() ? BABY_BASE_DIMENSIONS : super.getBaseDimensions(pose);
	}

	@Override
	public void tickMovement() {
		super.tickMovement();
		if (this.isTrapped() && this.trapTime++ >= 18000) {
			this.discard();
		}
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putBoolean("SkeletonTrap", this.isTrapped());
		nbt.putInt("SkeletonTrapTime", this.trapTime);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		this.setTrapped(nbt.getBoolean("SkeletonTrap"));
		this.trapTime = nbt.getInt("SkeletonTrapTime");
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
		return !this.isTame() ? ActionResult.PASS : super.interactMob(player, hand);
	}
}
