package net.minecraft.entity.mob;

import java.util.EnumSet;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldEvents;

public class GhastEntity extends FlyingEntity implements Monster {
	private static final TrackedData<Boolean> SHOOTING = DataTracker.registerData(GhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int fireballStrength = 1;

	public GhastEntity(EntityType<? extends GhastEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
		this.moveControl = new GhastEntity.GhastMoveControl(this);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(5, new GhastEntity.FlyRandomlyGoal(this));
		this.goalSelector.add(7, new GhastEntity.LookAtTargetGoal(this));
		this.goalSelector.add(7, new GhastEntity.ShootFireballGoal(this));
		this.targetSelector.add(1, new ActiveTargetGoal(this, PlayerEntity.class, 10, true, false, entity -> Math.abs(entity.getY() - this.getY()) <= 4.0));
	}

	public boolean isShooting() {
		return this.dataTracker.get(SHOOTING);
	}

	public void setShooting(boolean shooting) {
		this.dataTracker.set(SHOOTING, shooting);
	}

	public int getFireballStrength() {
		return this.fireballStrength;
	}

	@Override
	protected boolean isDisallowedInPeaceful() {
		return true;
	}

	/**
	 * {@return whether {@code damageSource} is caused by a player's fireball}
	 * 
	 * <p>This returns {@code true} for ghast fireballs reflected by a player,
	 * since the attacker is set as the player in that case.
	 */
	private static boolean isFireballFromPlayer(DamageSource damageSource) {
		return damageSource.getSource() instanceof FireballEntity && damageSource.getAttacker() instanceof PlayerEntity;
	}

	@Override
	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.isInvulnerable() && !damageSource.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)
			|| !isFireballFromPlayer(damageSource) && super.isInvulnerableTo(damageSource);
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (isFireballFromPlayer(source)) {
			super.damage(source, 1000.0F);
			return true;
		} else {
			return this.isInvulnerableTo(source) ? false : super.damage(source, amount);
		}
	}

	@Override
	protected void initDataTracker(DataTracker.Builder builder) {
		super.initDataTracker(builder);
		builder.add(SHOOTING, false);
	}

	public static DefaultAttributeContainer.Builder createGhastAttributes() {
		return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 10.0).add(EntityAttributes.GENERIC_FOLLOW_RANGE, 100.0);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_GHAST_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_GHAST_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_GHAST_DEATH;
	}

	@Override
	protected float getSoundVolume() {
		return 5.0F;
	}

	public static boolean canSpawn(EntityType<GhastEntity> type, WorldAccess world, SpawnReason spawnReason, BlockPos pos, Random random) {
		return world.getDifficulty() != Difficulty.PEACEFUL && random.nextInt(20) == 0 && canMobSpawn(type, world, spawnReason, pos, random);
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putByte("ExplosionPower", (byte)this.fireballStrength);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("ExplosionPower", NbtElement.NUMBER_TYPE)) {
			this.fireballStrength = nbt.getByte("ExplosionPower");
		}
	}

	static class FlyRandomlyGoal extends Goal {
		private final GhastEntity ghast;

		public FlyRandomlyGoal(GhastEntity ghast) {
			this.ghast = ghast;
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		@Override
		public boolean canStart() {
			MoveControl moveControl = this.ghast.getMoveControl();
			if (!moveControl.isMoving()) {
				return true;
			} else {
				double d = moveControl.getTargetX() - this.ghast.getX();
				double e = moveControl.getTargetY() - this.ghast.getY();
				double f = moveControl.getTargetZ() - this.ghast.getZ();
				double g = d * d + e * e + f * f;
				return g < 1.0 || g > 3600.0;
			}
		}

		@Override
		public boolean shouldContinue() {
			return false;
		}

		@Override
		public void start() {
			Random random = this.ghast.getRandom();
			double d = this.ghast.getX() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double e = this.ghast.getY() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double f = this.ghast.getZ() + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.ghast.getMoveControl().moveTo(d, e, f, 1.0);
		}
	}

	static class GhastMoveControl extends MoveControl {
		private final GhastEntity ghast;
		private int collisionCheckCooldown;

		public GhastMoveControl(GhastEntity ghast) {
			super(ghast);
			this.ghast = ghast;
		}

		@Override
		public void tick() {
			if (this.state == MoveControl.State.MOVE_TO) {
				if (this.collisionCheckCooldown-- <= 0) {
					this.collisionCheckCooldown = this.collisionCheckCooldown + this.ghast.getRandom().nextInt(5) + 2;
					Vec3d vec3d = new Vec3d(this.targetX - this.ghast.getX(), this.targetY - this.ghast.getY(), this.targetZ - this.ghast.getZ());
					double d = vec3d.length();
					vec3d = vec3d.normalize();
					if (this.willCollide(vec3d, MathHelper.ceil(d))) {
						this.ghast.setVelocity(this.ghast.getVelocity().add(vec3d.multiply(0.1)));
					} else {
						this.state = MoveControl.State.WAIT;
					}
				}
			}
		}

		private boolean willCollide(Vec3d direction, int steps) {
			Box box = this.ghast.getBoundingBox();

			for (int i = 1; i < steps; i++) {
				box = box.offset(direction);
				if (!this.ghast.getWorld().isSpaceEmpty(this.ghast, box)) {
					return false;
				}
			}

			return true;
		}
	}

	static class LookAtTargetGoal extends Goal {
		private final GhastEntity ghast;

		public LookAtTargetGoal(GhastEntity ghast) {
			this.ghast = ghast;
			this.setControls(EnumSet.of(Goal.Control.LOOK));
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			if (this.ghast.getTarget() == null) {
				Vec3d vec3d = this.ghast.getVelocity();
				this.ghast.setYaw(-((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180.0F / (float)Math.PI));
				this.ghast.bodyYaw = this.ghast.getYaw();
			} else {
				LivingEntity livingEntity = this.ghast.getTarget();
				double d = 64.0;
				if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0) {
					double e = livingEntity.getX() - this.ghast.getX();
					double f = livingEntity.getZ() - this.ghast.getZ();
					this.ghast.setYaw(-((float)MathHelper.atan2(e, f)) * (180.0F / (float)Math.PI));
					this.ghast.bodyYaw = this.ghast.getYaw();
				}
			}
		}
	}

	static class ShootFireballGoal extends Goal {
		private final GhastEntity ghast;
		public int cooldown;

		public ShootFireballGoal(GhastEntity ghast) {
			this.ghast = ghast;
		}

		@Override
		public boolean canStart() {
			return this.ghast.getTarget() != null;
		}

		@Override
		public void start() {
			this.cooldown = 0;
		}

		@Override
		public void stop() {
			this.ghast.setShooting(false);
		}

		@Override
		public boolean shouldRunEveryTick() {
			return true;
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.ghast.getTarget();
			if (livingEntity != null) {
				double d = 64.0;
				if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0 && this.ghast.canSee(livingEntity)) {
					World world = this.ghast.getWorld();
					this.cooldown++;
					if (this.cooldown == 10 && !this.ghast.isSilent()) {
						world.syncWorldEvent(null, WorldEvents.GHAST_WARNS, this.ghast.getBlockPos(), 0);
					}

					if (this.cooldown == 20) {
						double e = 4.0;
						Vec3d vec3d = this.ghast.getRotationVec(1.0F);
						double f = livingEntity.getX() - (this.ghast.getX() + vec3d.x * 4.0);
						double g = livingEntity.getBodyY(0.5) - (0.5 + this.ghast.getBodyY(0.5));
						double h = livingEntity.getZ() - (this.ghast.getZ() + vec3d.z * 4.0);
						Vec3d vec3d2 = new Vec3d(f, g, h);
						if (!this.ghast.isSilent()) {
							world.syncWorldEvent(null, WorldEvents.GHAST_SHOOTS, this.ghast.getBlockPos(), 0);
						}

						FireballEntity fireballEntity = new FireballEntity(world, this.ghast, vec3d2.normalize(), this.ghast.getFireballStrength());
						fireballEntity.setPosition(this.ghast.getX() + vec3d.x * 4.0, this.ghast.getBodyY(0.5) + 0.5, fireballEntity.getZ() + vec3d.z * 4.0);
						world.spawnEntity(fireballEntity);
						this.cooldown = -40;
					}
				} else if (this.cooldown > 0) {
					this.cooldown--;
				}

				this.ghast.setShooting(this.cooldown > 10);
			}
		}
	}
}
