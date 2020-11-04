package net.minecraft.entity.mob;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;

public class SpiderEntity extends HostileEntity {
	private static final TrackedData<Byte> SPIDER_FLAGS = DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BYTE);

	public SpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new SwimGoal(this));
		this.goalSelector.add(3, new PounceAtTargetGoal(this, 0.4F));
		this.goalSelector.add(4, new SpiderEntity.AttackGoal(this));
		this.goalSelector.add(5, new WanderAroundFarGoal(this, 0.8));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(6, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this));
		this.targetSelector.add(2, new SpiderEntity.FollowTargetGoal(this, PlayerEntity.class));
		this.targetSelector.add(3, new SpiderEntity.FollowTargetGoal(this, IronGolemEntity.class));
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)(this.getHeight() * 0.5F);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SpiderNavigation(this, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SPIDER_FLAGS, (byte)0);
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.world.isClient) {
			this.setClimbingWall(this.horizontalCollision);
		}
	}

	public static DefaultAttributeContainer.Builder createSpiderAttributes() {
		return HostileEntity.createHostileAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.3F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_SPIDER_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_SPIDER_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_SPIDER_DEATH;
	}

	@Override
	protected void playStepSound(BlockPos pos, BlockState state) {
		this.playSound(SoundEvents.ENTITY_SPIDER_STEP, 0.15F, 1.0F);
	}

	@Override
	public boolean isClimbing() {
		return this.isClimbingWall();
	}

	@Override
	public void slowMovement(BlockState state, Vec3d multiplier) {
		if (!state.isOf(Blocks.COBWEB)) {
			super.slowMovement(state, multiplier);
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}

	@Override
	public boolean canHaveStatusEffect(StatusEffectInstance effect) {
		return effect.getEffectType() == StatusEffects.POISON ? false : super.canHaveStatusEffect(effect);
	}

	public boolean isClimbingWall() {
		return (this.dataTracker.get(SPIDER_FLAGS) & 1) != 0;
	}

	public void setClimbingWall(boolean climbing) {
		byte b = this.dataTracker.get(SPIDER_FLAGS);
		if (climbing) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.set(SPIDER_FLAGS, b);
	}

	@Nullable
	@Override
	public EntityData initialize(
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		entityData = super.initialize(world, difficulty, spawnReason, entityData, entityTag);
		if (world.getRandom().nextInt(100) == 0) {
			SkeletonEntity skeletonEntity = EntityType.SKELETON.create(this.world);
			skeletonEntity.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.yaw, 0.0F);
			skeletonEntity.initialize(world, difficulty, spawnReason, null, null);
			skeletonEntity.startRiding(this);
		}

		if (entityData == null) {
			entityData = new SpiderEntity.SpiderData();
			if (world.getDifficulty() == Difficulty.HARD && world.getRandom().nextFloat() < 0.1F * difficulty.getClampedLocalDifficulty()) {
				((SpiderEntity.SpiderData)entityData).setEffect(world.getRandom());
			}
		}

		if (entityData instanceof SpiderEntity.SpiderData) {
			StatusEffect statusEffect = ((SpiderEntity.SpiderData)entityData).effect;
			if (statusEffect != null) {
				this.addStatusEffect(new StatusEffectInstance(statusEffect, Integer.MAX_VALUE));
			}
		}

		return entityData;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return 0.65F;
	}

	static class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(SpiderEntity spider) {
			super(spider, 1.0, true);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.mob.hasPassengers();
		}

		@Override
		public boolean shouldContinue() {
			float f = this.mob.getBrightnessAtEyes();
			if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
				this.mob.setTarget(null);
				return false;
			} else {
				return super.shouldContinue();
			}
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity entity) {
			return (double)(4.0F + entity.getWidth());
		}
	}

	static class FollowTargetGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.FollowTargetGoal<T> {
		public FollowTargetGoal(SpiderEntity spider, Class<T> targetEntityClass) {
			super(spider, targetEntityClass, true);
		}

		@Override
		public boolean canStart() {
			float f = this.mob.getBrightnessAtEyes();
			return f >= 0.5F ? false : super.canStart();
		}
	}

	public static class SpiderData implements EntityData {
		public StatusEffect effect;

		public void setEffect(Random random) {
			int i = random.nextInt(5);
			if (i <= 1) {
				this.effect = StatusEffects.SPEED;
			} else if (i <= 2) {
				this.effect = StatusEffects.STRENGTH;
			} else if (i <= 3) {
				this.effect = StatusEffects.REGENERATION;
			} else if (i <= 4) {
				this.effect = StatusEffects.INVISIBILITY;
			}
		}
	}
}
