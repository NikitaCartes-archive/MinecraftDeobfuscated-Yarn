package net.minecraft.entity.passive;

import java.util.Random;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.goal.EscapeDangerGoal;
import net.minecraft.entity.ai.goal.FollowParentGoal;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.SwimGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;

public class PolarBearEntity extends AnimalEntity {
	private static final TrackedData<Boolean> WARNING = DataTracker.registerData(PolarBearEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private float lastWarningAnimationProgress;
	private float warningAnimationProgress;
	private int warningSoundCooldown;

	public PolarBearEntity(EntityType<? extends PolarBearEntity> entityType, World world) {
		super(entityType, world);
	}

	@Override
	public PassiveEntity createChild(PassiveEntity passiveEntity) {
		return EntityType.field_6042.method_5883(this.field_6002);
	}

	@Override
	public boolean isBreedingItem(ItemStack itemStack) {
		return false;
	}

	@Override
	protected void initGoals() {
		super.initGoals();
		this.goalSelector.add(0, new SwimGoal(this));
		this.goalSelector.add(1, new PolarBearEntity.AttackGoal());
		this.goalSelector.add(1, new PolarBearEntity.PolarBearEscapeDangerGoal());
		this.goalSelector.add(4, new FollowParentGoal(this, 1.25));
		this.goalSelector.add(5, new WanderAroundGoal(this, 1.0));
		this.goalSelector.add(6, new LookAtEntityGoal(this, PlayerEntity.class, 6.0F));
		this.goalSelector.add(7, new LookAroundGoal(this));
		this.targetSelector.add(1, new PolarBearEntity.PolarBearRevengeGoal());
		this.targetSelector.add(2, new PolarBearEntity.FollowPlayersGoal());
		this.targetSelector.add(3, new FollowTargetGoal(this, FoxEntity.class, 10, true, true, null));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(20.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.25);
		this.getAttributeContainer().register(EntityAttributes.ATTACK_DAMAGE);
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
	}

	public static boolean method_20668(EntityType<PolarBearEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		Biome biome = iWorld.method_8310(blockPos);
		return biome != Biomes.field_9435 && biome != Biomes.field_9418
			? method_20663(entityType, iWorld, spawnType, blockPos, random)
			: iWorld.getLightLevel(blockPos, 0) > 8 && iWorld.method_8320(blockPos.down()).getBlock() == Blocks.field_10295;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isBaby() ? SoundEvents.field_14605 : SoundEvents.field_15078;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15107;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_15209;
	}

	@Override
	protected void method_5712(BlockPos blockPos, BlockState blockState) {
		this.playSound(SoundEvents.field_15036, 0.15F, 1.0F);
	}

	protected void playWarningSound() {
		if (this.warningSoundCooldown <= 0) {
			this.playSound(SoundEvents.field_14937, 1.0F, this.getSoundPitch());
			this.warningSoundCooldown = 40;
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(WARNING, false);
	}

	@Override
	public void tick() {
		super.tick();
		if (this.field_6002.isClient) {
			if (this.warningAnimationProgress != this.lastWarningAnimationProgress) {
				this.calculateDimensions();
			}

			this.lastWarningAnimationProgress = this.warningAnimationProgress;
			if (this.isWarning()) {
				this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress + 1.0F, 0.0F, 6.0F);
			} else {
				this.warningAnimationProgress = MathHelper.clamp(this.warningAnimationProgress - 1.0F, 0.0F, 6.0F);
			}
		}

		if (this.warningSoundCooldown > 0) {
			this.warningSoundCooldown--;
		}
	}

	@Override
	public EntityDimensions method_18377(EntityPose entityPose) {
		if (this.warningAnimationProgress > 0.0F) {
			float f = this.warningAnimationProgress / 6.0F;
			float g = 1.0F + f;
			return super.method_18377(entityPose).scaled(1.0F, g);
		} else {
			return super.method_18377(entityPose);
		}
	}

	@Override
	public boolean tryAttack(Entity entity) {
		boolean bl = entity.damage(DamageSource.mob(this), (float)((int)this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue()));
		if (bl) {
			this.dealDamage(this, entity);
		}

		return bl;
	}

	public boolean isWarning() {
		return this.dataTracker.get(WARNING);
	}

	public void setWarning(boolean bl) {
		this.dataTracker.set(WARNING, bl);
	}

	@Environment(EnvType.CLIENT)
	public float getWarningAnimationProgress(float f) {
		return MathHelper.lerp(f, this.lastWarningAnimationProgress, this.warningAnimationProgress) / 6.0F;
	}

	@Override
	protected float getBaseMovementSpeedMultiplier() {
		return 0.98F;
	}

	@Override
	public EntityData method_5943(
		IWorld iWorld, LocalDifficulty localDifficulty, SpawnType spawnType, @Nullable EntityData entityData, @Nullable CompoundTag compoundTag
	) {
		if (entityData instanceof PolarBearEntity.PolarBearEntityData) {
			this.setBreedingAge(-24000);
		} else {
			entityData = new PolarBearEntity.PolarBearEntityData();
		}

		return entityData;
	}

	class AttackGoal extends MeleeAttackGoal {
		public AttackGoal() {
			super(PolarBearEntity.this, 1.25, true);
		}

		@Override
		protected void attack(LivingEntity livingEntity, double d) {
			double e = this.getSquaredMaxAttackDistance(livingEntity);
			if (d <= e && this.ticksUntilAttack <= 0) {
				this.ticksUntilAttack = 20;
				this.mob.tryAttack(livingEntity);
				PolarBearEntity.this.setWarning(false);
			} else if (d <= e * 2.0) {
				if (this.ticksUntilAttack <= 0) {
					PolarBearEntity.this.setWarning(false);
					this.ticksUntilAttack = 20;
				}

				if (this.ticksUntilAttack <= 10) {
					PolarBearEntity.this.setWarning(true);
					PolarBearEntity.this.playWarningSound();
				}
			} else {
				this.ticksUntilAttack = 20;
				PolarBearEntity.this.setWarning(false);
			}
		}

		@Override
		public void stop() {
			PolarBearEntity.this.setWarning(false);
			super.stop();
		}

		@Override
		protected double getSquaredMaxAttackDistance(LivingEntity livingEntity) {
			return (double)(4.0F + livingEntity.getWidth());
		}
	}

	class FollowPlayersGoal extends FollowTargetGoal<PlayerEntity> {
		public FollowPlayersGoal() {
			super(PolarBearEntity.this, PlayerEntity.class, 20, true, true, null);
		}

		@Override
		public boolean canStart() {
			if (PolarBearEntity.this.isBaby()) {
				return false;
			} else {
				if (super.canStart()) {
					for (PolarBearEntity polarBearEntity : PolarBearEntity.this.field_6002
						.method_18467(PolarBearEntity.class, PolarBearEntity.this.method_5829().expand(8.0, 4.0, 8.0))) {
						if (polarBearEntity.isBaby()) {
							return true;
						}
					}
				}

				return false;
			}
		}

		@Override
		protected double getFollowRange() {
			return super.getFollowRange() * 0.5;
		}
	}

	static class PolarBearEntityData implements EntityData {
		private PolarBearEntityData() {
		}
	}

	class PolarBearEscapeDangerGoal extends EscapeDangerGoal {
		public PolarBearEscapeDangerGoal() {
			super(PolarBearEntity.this, 2.0);
		}

		@Override
		public boolean canStart() {
			return !PolarBearEntity.this.isBaby() && !PolarBearEntity.this.isOnFire() ? false : super.canStart();
		}
	}

	class PolarBearRevengeGoal extends RevengeGoal {
		public PolarBearRevengeGoal() {
			super(PolarBearEntity.this);
		}

		@Override
		public void start() {
			super.start();
			if (PolarBearEntity.this.isBaby()) {
				this.callSameTypeForRevenge();
				this.stop();
			}
		}

		@Override
		protected void setMobEntityTarget(MobEntity mobEntity, LivingEntity livingEntity) {
			if (mobEntity instanceof PolarBearEntity && !mobEntity.isBaby()) {
				super.setMobEntityTarget(mobEntity, livingEntity);
			}
		}
	}
}
