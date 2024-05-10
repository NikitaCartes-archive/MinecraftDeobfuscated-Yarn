package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.GoToWalkTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.ai.goal.WanderAroundFarGoal;
import net.minecraft.entity.ai.pathing.PathNodeType;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.SmallFireballEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;

public class BlazeEntity extends HostileEntity {
	private float eyeOffset = 0.5F;
	private int eyeOffsetCooldown;
	/**
	 * The tracked flags of blazes. Only has the {@code 1} bit for {@linkplain
	 * #isFireActive() fire activation}.
	 */
	private static final TrackedData<Byte> BLAZE_FLAGS = DataTracker.registerData(BlazeEntity.class, TrackedDataHandlerRegistry.BYTE);

	public BlazeEntity(EntityType<? extends BlazeEntity> entityType, World world) {
		super(entityType, world);
		this.setPathfindingPenalty(PathNodeType.WATER, -1.0F);
		this.setPathfindingPenalty(PathNodeType.LAVA, 8.0F);
		this.setPathfindingPenalty(PathNodeType.DANGER_FIRE, 0.0F);
		this.setPathfindingPenalty(PathNodeType.DAMAGE_FIRE, 0.0F);
		this.experiencePoints = 10;
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(4, new BlazeEntity.ShootFireballGoal(this));
		this.goalSelector.add(5, new GoToWalkTargetGoal(this, 1.0));
		this.goalSelector.add(7, new WanderAroundFarGoal(this, 1.0, 0.0F));
		this.goalSelector.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new LookAroundGoal(this));
		this.targetSelector.add(1, new RevengeGoal(this).setGroupRevenge());
		this.targetSelector.add(2, new ActiveTargetGoal(this, PlayerEntity.class, true));
	}

	public static DefaultAttributeContainer.Builder createBlazeAttributes() {
		return HostileEntity.createHostileAttributes()
			.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 6.0)
			.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.23F)
			.add(EntityAttributes.GENERIC_FOLLOW_RANGE, 48.0);
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(BLAZE_FLAGS, (byte)0);
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_BLAZE_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_BLAZE_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_BLAZE_DEATH;
	}

	@Override
	public float getBrightnessAtEyes() {
		return 1.0F;
	}

	@Override
	public void tickMovement() {
		if (!this.isOnGround() && this.getVelocity().y < 0.0) {
			this.setVelocity(this.getVelocity().multiply(1.0, 0.6, 1.0));
		}

		if (this.getWorld().isClient) {
			if (this.random.nextInt(24) == 0 && !this.isSilent()) {
				this.getWorld()
					.playSound(
						this.getX() + 0.5,
						this.getY() + 0.5,
						this.getZ() + 0.5,
						SoundEvents.ENTITY_BLAZE_BURN,
						this.getSoundCategory(),
						1.0F + this.random.nextFloat(),
						this.random.nextFloat() * 0.7F + 0.3F,
						false
					);
			}

			for (int i = 0; i < 2; i++) {
				this.getWorld().addParticle(ParticleTypes.LARGE_SMOKE, this.getParticleX(0.5), this.getRandomBodyY(), this.getParticleZ(0.5), 0.0, 0.0, 0.0);
			}
		}

		super.tickMovement();
	}

	@Override
	public boolean hurtByWater() {
		return true;
	}

	@Override
	protected void mobTick() {
		this.eyeOffsetCooldown--;
		if (this.eyeOffsetCooldown <= 0) {
			this.eyeOffsetCooldown = 100;
			this.eyeOffset = (float)this.random.nextTriangular(0.5, 6.891);
		}

		LivingEntity livingEntity = this.getTarget();
		if (livingEntity != null && livingEntity.getEyeY() > this.getEyeY() + (double)this.eyeOffset && this.canTarget(livingEntity)) {
			Vec3d vec3d = this.getVelocity();
			this.setVelocity(this.getVelocity().add(0.0, (0.3F - vec3d.y) * 0.3F, 0.0));
			this.velocityDirty = true;
		}

		super.mobTick();
	}

	@Override
	public boolean isOnFire() {
		return this.isFireActive();
	}

	private boolean isFireActive() {
		return (this.dataTracker.get(BLAZE_FLAGS) & 1) != 0;
	}

	void setFireActive(boolean fireActive) {
		byte b = this.dataTracker.get(BLAZE_FLAGS);
		if (fireActive) {
			b = (byte)(b | 1);
		} else {
			b = (byte)(b & -2);
		}

		this.dataTracker.set(BLAZE_FLAGS, b);
	}

	static class ShootFireballGoal extends Goal {
		private final BlazeEntity blaze;
		private int fireballsFired;
		private int fireballCooldown;
		private int targetNotVisibleTicks;

		public ShootFireballGoal(BlazeEntity blaze) {
			this.blaze = blaze;
			this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.blaze.getTarget();
			return livingEntity != null && livingEntity.isAlive() && this.blaze.canTarget(livingEntity);
		}

		@Override
		public void start() {
			this.fireballsFired = 0;
		}

		@Override
		public void stop() {
			this.blaze.setFireActive(false);
			this.targetNotVisibleTicks = 0;
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			this.fireballCooldown--;
			LivingEntity livingEntity = this.blaze.getTarget();
			if (livingEntity != null) {
				boolean bl = this.blaze.getVisibilityCache().canSee(livingEntity);
				if (bl) {
					this.targetNotVisibleTicks = 0;
				} else {
					this.targetNotVisibleTicks++;
				}

				double d = this.blaze.squaredDistanceTo(livingEntity);
				if (d < 4.0) {
					if (!bl) {
						return;
					}

					if (this.fireballCooldown <= 0) {
						this.fireballCooldown = 20;
						this.blaze.tryAttack(livingEntity);
					}

					this.blaze.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0);
				} else if (d < this.getFollowRange() * this.getFollowRange() && bl) {
					double e = livingEntity.getX() - this.blaze.getX();
					double f = livingEntity.getBodyY(0.5) - this.blaze.getBodyY(0.5);
					double g = livingEntity.getZ() - this.blaze.getZ();
					if (this.fireballCooldown <= 0) {
						this.fireballsFired++;
						if (this.fireballsFired == 1) {
							this.fireballCooldown = 60;
							this.blaze.setFireActive(true);
						} else if (this.fireballsFired <= 4) {
							this.fireballCooldown = 6;
						} else {
							this.fireballCooldown = 100;
							this.fireballsFired = 0;
							this.blaze.setFireActive(false);
						}

						if (this.fireballsFired > 1) {
							double h = Math.sqrt(Math.sqrt(d)) * 0.5;
							if (!this.blaze.isSilent()) {
								this.blaze.getWorld().syncWorldEvent(null, WorldEvents.BLAZE_SHOOTS, this.blaze.getBlockPos(), 0);
							}

							for (int i = 0; i < 1; i++) {
								Vec3d vec3d = new Vec3d(this.blaze.getRandom().nextTriangular(e, 2.297 * h), f, this.blaze.getRandom().nextTriangular(g, 2.297 * h));
								SmallFireballEntity smallFireballEntity = new SmallFireballEntity(this.blaze.getWorld(), this.blaze, vec3d.normalize());
								smallFireballEntity.setPosition(smallFireballEntity.getX(), this.blaze.getBodyY(0.5) + 0.5, smallFireballEntity.getZ());
								this.blaze.getWorld().spawnEntity(smallFireballEntity);
							}
						}
					}

					this.blaze.getLookControl().lookAt(livingEntity, 10.0F, 10.0F);
				} else if (this.targetNotVisibleTicks < 5) {
					this.blaze.getMoveControl().moveTo(livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), 1.0);
				}

				super.tick();
			}
		}

		private double getFollowRange() {
			return this.blaze.getAttributeValue(EntityAttributes.GENERIC_FOLLOW_RANGE);
		}
	}
}
