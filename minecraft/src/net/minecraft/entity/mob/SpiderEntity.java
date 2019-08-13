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
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SpiderNavigation;
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
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
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
			this.setCanClimb(this.horizontalCollision);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(16.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_15170;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_14657;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14579;
	}

	@Override
	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_14760, 0.15F, 1.0F);
	}

	@Override
	public boolean isClimbing() {
		return this.getCanClimb();
	}

	@Override
	public void slowMovement(BlockState blockState, Vec3d vec3d) {
		if (blockState.getBlock() != Blocks.field_10343) {
			super.slowMovement(blockState, vec3d);
		}
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.ARTHROPOD;
	}

	@Override
	public boolean isPotionEffective(StatusEffectInstance statusEffectInstance) {
		return statusEffectInstance.getEffectType() == StatusEffects.field_5899 ? false : super.isPotionEffective(statusEffectInstance);
	}

	public boolean getCanClimb() {
		return (this.dataTracker.get(SPIDER_FLAGS) & 1) != 0;
	}

	public void setCanClimb(boolean bl) {
		byte b = this.dataTracker.get(SPIDER_FLAGS);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.set(SPIDER_FLAGS, b);
	}

	@Nullable
	@Override
	public EntityData initialize(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.initialize(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (iWorld.getRandom().nextInt(100) == 0) {
			SkeletonEntity skeletonEntity = EntityType.field_6137.create(this.world);
			skeletonEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			skeletonEntity.initialize(iWorld, localDifficulty, spawnType, null, null);
			iWorld.spawnEntity(skeletonEntity);
			skeletonEntity.startRiding(this);
		}

		if (entityData == null) {
			entityData = new SpiderEntity.SpawnEffectData();
			if (iWorld.getDifficulty() == Difficulty.field_5807 && iWorld.getRandom().nextFloat() < 0.1F * localDifficulty.getClampedLocalDifficulty()) {
				((SpiderEntity.SpawnEffectData)entityData).setEffect(iWorld.getRandom());
			}
		}

		if (entityData instanceof SpiderEntity.SpawnEffectData) {
			StatusEffect statusEffect = ((SpiderEntity.SpawnEffectData)entityData).effect;
			if (statusEffect != null) {
				this.addPotionEffect(new StatusEffectInstance(statusEffect, Integer.MAX_VALUE));
			}
		}

		return entityData;
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 0.65F;
	}

	static class AttackGoal extends MeleeAttackGoal {
		public AttackGoal(SpiderEntity spiderEntity) {
			super(spiderEntity, 1.0, true);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.mob.hasPassengers();
		}

		@Override
		public boolean shouldContinue() {
			float f = this.mob.getBrightnessAtEyes();
			if (f >= 0.5F && this.mob.getRand().nextInt(100) == 0) {
				this.mob.setTarget(null);
				return false;
			} else {
				return super.shouldContinue();
			}
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity livingEntity) {
			return (double)(4.0F + livingEntity.getWidth());
		}
	}

	static class FollowTargetGoal<T extends LivingEntity> extends net.minecraft.entity.ai.goal.FollowTargetGoal<T> {
		public FollowTargetGoal(SpiderEntity spiderEntity, Class<T> class_) {
			super(spiderEntity, class_, true);
		}

		@Override
		public boolean canStart() {
			float f = this.mob.getBrightnessAtEyes();
			return f >= 0.5F ? false : super.canStart();
		}
	}

	public static class SpawnEffectData implements EntityData {
		public StatusEffect effect;

		public void setEffect(Random random) {
			int i = random.nextInt(5);
			if (i <= 1) {
				this.effect = StatusEffects.field_5904;
			} else if (i <= 2) {
				this.effect = StatusEffects.field_5910;
			} else if (i <= 3) {
				this.effect = StatusEffects.field_5924;
			} else if (i <= 4) {
				this.effect = StatusEffects.field_5905;
			}
		}
	}
}
