package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

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
		this.targetSelector.add(1, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.y - this.y) <= 4.0));
	}

	@Environment(EnvType.CLIENT)
	public boolean isShooting() {
		return this.dataTracker.get(SHOOTING);
	}

	public void setShooting(boolean bl) {
		this.dataTracker.set(SHOOTING, bl);
	}

	public int getFireballStrength() {
		return this.fireballStrength;
	}

	@Override
	public void tick() {
		super.tick();
		if (!this.field_6002.isClient && this.field_6002.getDifficulty() == Difficulty.field_5801) {
			this.remove();
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (damageSource.getSource() instanceof FireballEntity && damageSource.getAttacker() instanceof PlayerEntity) {
			super.damage(damageSource, 1000.0F);
			return true;
		} else {
			return super.damage(damageSource, f);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SHOOTING, false);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(100.0);
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.field_15251;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.field_14566;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return SoundEvents.field_15054;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.field_14648;
	}

	@Override
	protected float getSoundVolume() {
		return 10.0F;
	}

	public static boolean method_20675(EntityType<GhastEntity> entityType, IWorld iWorld, SpawnType spawnType, BlockPos blockPos, Random random) {
		return iWorld.getDifficulty() != Difficulty.field_5801 && random.nextInt(20) == 0 && method_20636(entityType, iWorld, spawnType, blockPos, random);
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Override
	public void writeCustomDataToTag(CompoundTag compoundTag) {
		super.writeCustomDataToTag(compoundTag);
		compoundTag.putInt("ExplosionPower", this.fireballStrength);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag compoundTag) {
		super.readCustomDataFromTag(compoundTag);
		if (compoundTag.containsKey("ExplosionPower", 99)) {
			this.fireballStrength = compoundTag.getInt("ExplosionPower");
		}
	}

	@Override
	protected float getActiveEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return 2.6F;
	}

	static class FlyRandomlyGoal extends Goal {
		private final GhastEntity ghast;

		public FlyRandomlyGoal(GhastEntity ghastEntity) {
			this.ghast = ghastEntity;
			this.setControls(EnumSet.of(Goal.Control.field_18405));
		}

		@Override
		public boolean canStart() {
			MoveControl moveControl = this.ghast.getMoveControl();
			if (!moveControl.isMoving()) {
				return true;
			} else {
				double d = moveControl.getTargetX() - this.ghast.x;
				double e = moveControl.getTargetY() - this.ghast.y;
				double f = moveControl.getTargetZ() - this.ghast.z;
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
			Random random = this.ghast.getRand();
			double d = this.ghast.x + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double e = this.ghast.y + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double f = this.ghast.z + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.ghast.getMoveControl().moveTo(d, e, f, 1.0);
		}
	}

	static class GhastMoveControl extends MoveControl {
		private final GhastEntity ghast;
		private int field_7276;

		public GhastMoveControl(GhastEntity ghastEntity) {
			super(ghastEntity);
			this.ghast = ghastEntity;
		}

		@Override
		public void tick() {
			if (this.state == MoveControl.State.field_6378) {
				if (this.field_7276-- <= 0) {
					this.field_7276 = this.field_7276 + this.ghast.getRand().nextInt(5) + 2;
					Vec3d vec3d = new Vec3d(this.targetX - this.ghast.x, this.targetY - this.ghast.y, this.targetZ - this.ghast.z);
					double d = vec3d.length();
					vec3d = vec3d.normalize();
					if (this.method_7051(vec3d, MathHelper.ceil(d))) {
						this.ghast.method_18799(this.ghast.method_18798().add(vec3d.multiply(0.1)));
					} else {
						this.state = MoveControl.State.field_6377;
					}
				}
			}
		}

		private boolean method_7051(Vec3d vec3d, int i) {
			Box box = this.ghast.method_5829();

			for (int j = 1; j < i; j++) {
				box = box.method_997(vec3d);
				if (!this.ghast.field_6002.method_8587(this.ghast, box)) {
					return false;
				}
			}

			return true;
		}
	}

	static class LookAtTargetGoal extends Goal {
		private final GhastEntity ghast;

		public LookAtTargetGoal(GhastEntity ghastEntity) {
			this.ghast = ghastEntity;
			this.setControls(EnumSet.of(Goal.Control.field_18406));
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			if (this.ghast.getTarget() == null) {
				Vec3d vec3d = this.ghast.method_18798();
				this.ghast.yaw = -((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180.0F / (float)Math.PI);
				this.ghast.field_6283 = this.ghast.yaw;
			} else {
				LivingEntity livingEntity = this.ghast.getTarget();
				double d = 64.0;
				if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0) {
					double e = livingEntity.x - this.ghast.x;
					double f = livingEntity.z - this.ghast.z;
					this.ghast.yaw = -((float)MathHelper.atan2(e, f)) * (180.0F / (float)Math.PI);
					this.ghast.field_6283 = this.ghast.yaw;
				}
			}
		}
	}

	static class ShootFireballGoal extends Goal {
		private final GhastEntity ghast;
		public int cooldown;

		public ShootFireballGoal(GhastEntity ghastEntity) {
			this.ghast = ghastEntity;
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
		public void tick() {
			LivingEntity livingEntity = this.ghast.getTarget();
			double d = 64.0;
			if (livingEntity.squaredDistanceTo(this.ghast) < 4096.0 && this.ghast.canSee(livingEntity)) {
				World world = this.ghast.field_6002;
				this.cooldown++;
				if (this.cooldown == 10) {
					world.playLevelEvent(null, 1015, new BlockPos(this.ghast), 0);
				}

				if (this.cooldown == 20) {
					double e = 4.0;
					Vec3d vec3d = this.ghast.method_5828(1.0F);
					double f = livingEntity.x - (this.ghast.x + vec3d.x * 4.0);
					double g = livingEntity.method_5829().minY + (double)(livingEntity.getHeight() / 2.0F) - (0.5 + this.ghast.y + (double)(this.ghast.getHeight() / 2.0F));
					double h = livingEntity.z - (this.ghast.z + vec3d.z * 4.0);
					world.playLevelEvent(null, 1016, new BlockPos(this.ghast), 0);
					FireballEntity fireballEntity = new FireballEntity(world, this.ghast, f, g, h);
					fireballEntity.explosionPower = this.ghast.getFireballStrength();
					fireballEntity.x = this.ghast.x + vec3d.x * 4.0;
					fireballEntity.y = this.ghast.y + (double)(this.ghast.getHeight() / 2.0F) + 0.5;
					fireballEntity.z = this.ghast.z + vec3d.z * 4.0;
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
