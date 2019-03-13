package net.minecraft.entity.mob;

import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.class_1394;
import net.minecraft.class_1399;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.PounceAtTargetGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
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
	private static final TrackedData<Byte> field_7403 = DataTracker.registerData(SpiderEntity.class, TrackedDataHandlerRegistry.BYTE);

	public SpiderEntity(EntityType<? extends SpiderEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(1, new SwimGoal(this));
		this.field_6201.add(3, new PounceAtTargetGoal(this, 0.4F));
		this.field_6201.add(4, new SpiderEntity.class_1629(this));
		this.field_6201.add(5, new class_1394(this, 0.8));
		this.field_6201.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(6, new LookAroundGoal(this));
		this.field_6185.add(1, new class_1399(this));
		this.field_6185.add(2, new SpiderEntity.class_1631(this, PlayerEntity.class));
		this.field_6185.add(3, new SpiderEntity.class_1631(this, IronGolemEntity.class));
	}

	@Override
	public double getMountedHeightOffset() {
		return (double)(this.getHeight() * 0.5F);
	}

	@Override
	protected EntityNavigation method_5965(World world) {
		return new SpiderNavigation(this, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7403, (byte)0);
	}

	@Override
	public void update() {
		super.update();
		if (!this.field_6002.isClient) {
			this.setCanClimb(this.horizontalCollision);
		}
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(16.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.3F);
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_15170;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_14657;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14579;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.method_5783(SoundEvents.field_14760, 0.15F, 1.0F);
	}

	@Override
	public boolean canClimb() {
		return this.getCanClimb();
	}

	@Override
	public void method_5844(BlockState blockState, Vec3d vec3d) {
		if (blockState.getBlock() != Blocks.field_10343) {
			super.method_5844(blockState, vec3d);
		}
	}

	@Override
	public EntityGroup method_6046() {
		return EntityGroup.ARTHROPOD;
	}

	@Override
	public boolean isPotionEffective(StatusEffectInstance statusEffectInstance) {
		return statusEffectInstance.getEffectType() == StatusEffects.field_5899 ? false : super.isPotionEffective(statusEffectInstance);
	}

	public boolean getCanClimb() {
		return (this.field_6011.get(field_7403) & 1) != 0;
	}

	public void setCanClimb(boolean bl) {
		byte b = this.field_6011.get(field_7403);
		if (bl) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.field_6011.set(field_7403, b);
	}

	@Nullable
	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		entityData = super.method_5943(iWorld, localDifficulty, spawnType, entityData, compoundTag);
		if (iWorld.getRandom().nextInt(100) == 0) {
			SkeletonEntity skeletonEntity = EntityType.SKELETON.method_5883(this.field_6002);
			skeletonEntity.setPositionAndAngles(this.x, this.y, this.z, this.yaw, 0.0F);
			skeletonEntity.method_5943(iWorld, localDifficulty, spawnType, null, null);
			iWorld.spawnEntity(skeletonEntity);
			skeletonEntity.startRiding(this);
		}

		if (entityData == null) {
			entityData = new SpiderEntity.class_1630();
			if (iWorld.getDifficulty() == Difficulty.HARD && iWorld.getRandom().nextFloat() < 0.1F * localDifficulty.getClampedLocalDifficulty()) {
				((SpiderEntity.class_1630)entityData).method_7168(iWorld.getRandom());
			}
		}

		if (entityData instanceof SpiderEntity.class_1630) {
			StatusEffect statusEffect = ((SpiderEntity.class_1630)entityData).field_7404;
			if (statusEffect != null) {
				this.addPotionEffect(new StatusEffectInstance(statusEffect, Integer.MAX_VALUE));
			}
		}

		return entityData;
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 0.65F;
	}

	static class class_1629 extends MeleeAttackGoal {
		public class_1629(SpiderEntity spiderEntity) {
			super(spiderEntity, 1.0, true);
		}

		@Override
		public boolean canStart() {
			return super.canStart() && !this.entity.hasPassengers();
		}

		@Override
		public boolean shouldContinue() {
			float f = this.entity.method_5718();
			if (f >= 0.5F && this.entity.getRand().nextInt(100) == 0) {
				this.entity.setTarget(null);
				return false;
			} else {
				return super.shouldContinue();
			}
		}

		@Override
		protected double method_6289(LivingEntity livingEntity) {
			return (double)(4.0F + livingEntity.getWidth());
		}
	}

	public static class class_1630 implements EntityData {
		public StatusEffect field_7404;

		public void method_7168(Random random) {
			int i = random.nextInt(5);
			if (i <= 1) {
				this.field_7404 = StatusEffects.field_5904;
			} else if (i <= 2) {
				this.field_7404 = StatusEffects.field_5910;
			} else if (i <= 3) {
				this.field_7404 = StatusEffects.field_5924;
			} else if (i <= 4) {
				this.field_7404 = StatusEffects.field_5905;
			}
		}
	}

	static class class_1631<T extends LivingEntity> extends FollowTargetGoal<T> {
		public class_1631(SpiderEntity spiderEntity, Class<T> class_) {
			super(spiderEntity, class_, true);
		}

		@Override
		public boolean canStart() {
			float f = this.entity.method_5718();
			return f >= 0.5F ? false : super.canStart();
		}
	}
}
