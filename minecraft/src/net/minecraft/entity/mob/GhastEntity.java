package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
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
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;

public class GhastEntity extends FlyingEntity implements Monster {
	private static final TrackedData<Boolean> field_7273 = DataTracker.registerData(GhastEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private int fireballStrength = 1;

	public GhastEntity(EntityType<? extends GhastEntity> entityType, World world) {
		super(entityType, world);
		this.fireImmune = true;
		this.experiencePoints = 5;
		this.field_6207 = new GhastEntity.GhastMoveControl(this);
	}

	@Override
	protected void initGoals() {
		this.field_6201.add(5, new GhastEntity.FlyRandomlyGoal(this));
		this.field_6201.add(7, new GhastEntity.class_1572(this));
		this.field_6201.add(7, new GhastEntity.ShootFireballGoal(this));
		this.field_6185.add(1, new FollowTargetGoal(this, PlayerEntity.class, 10, true, false, livingEntity -> Math.abs(livingEntity.y - this.y) <= 4.0));
	}

	@Environment(EnvType.CLIENT)
	public boolean isShooting() {
		return this.field_6011.get(field_7273);
	}

	public void setShooting(boolean bl) {
		this.field_6011.set(field_7273, bl);
	}

	public int getFireballStrength() {
		return this.fireballStrength;
	}

	@Override
	public void update() {
		super.update();
		if (!this.field_6002.isClient && this.field_6002.getDifficulty() == Difficulty.PEACEFUL) {
			this.invalidate();
		}
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (damageSource.method_5526() instanceof FireballEntity && damageSource.method_5529() instanceof PlayerEntity) {
			super.damage(damageSource, 1000.0F);
			return true;
		} else {
			return super.damage(damageSource, f);
		}
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7273, false);
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(10.0);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(100.0);
	}

	@Override
	public SoundCategory method_5634() {
		return SoundCategory.field_15251;
	}

	@Override
	protected SoundEvent method_5994() {
		return SoundEvents.field_14566;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return SoundEvents.field_15054;
	}

	@Override
	protected SoundEvent method_6002() {
		return SoundEvents.field_14648;
	}

	@Override
	protected float getSoundVolume() {
		return 10.0F;
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return this.random.nextInt(20) == 0 && super.method_5979(iWorld, spawnType) && iWorld.getDifficulty() != Difficulty.PEACEFUL;
	}

	@Override
	public int getLimitPerChunk() {
		return 1;
	}

	@Override
	public void method_5652(CompoundTag compoundTag) {
		super.method_5652(compoundTag);
		compoundTag.putInt("ExplosionPower", this.fireballStrength);
	}

	@Override
	public void method_5749(CompoundTag compoundTag) {
		super.method_5749(compoundTag);
		if (compoundTag.containsKey("ExplosionPower", 99)) {
			this.fireballStrength = compoundTag.getInt("ExplosionPower");
		}
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return 2.6F;
	}

	static class FlyRandomlyGoal extends Goal {
		private final GhastEntity field_7279;

		public FlyRandomlyGoal(GhastEntity ghastEntity) {
			this.field_7279 = ghastEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405));
		}

		@Override
		public boolean canStart() {
			MoveControl moveControl = this.field_7279.method_5962();
			if (!moveControl.isMoving()) {
				return true;
			} else {
				double d = moveControl.getTargetX() - this.field_7279.x;
				double e = moveControl.getTargetY() - this.field_7279.y;
				double f = moveControl.getTargetZ() - this.field_7279.z;
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
			Random random = this.field_7279.getRand();
			double d = this.field_7279.x + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double e = this.field_7279.y + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			double f = this.field_7279.z + (double)((random.nextFloat() * 2.0F - 1.0F) * 16.0F);
			this.field_7279.method_5962().moveTo(d, e, f, 1.0);
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
			BoundingBox boundingBox = this.ghast.method_5829();

			for (int j = 1; j < i; j++) {
				boundingBox = boundingBox.method_997(vec3d);
				if (!this.ghast.field_6002.method_8587(this.ghast, boundingBox)) {
					return false;
				}
			}

			return true;
		}
	}

	static class ShootFireballGoal extends Goal {
		private final GhastEntity owner;
		public int cooldown;

		public ShootFireballGoal(GhastEntity ghastEntity) {
			this.owner = ghastEntity;
		}

		@Override
		public boolean canStart() {
			return this.owner.getTarget() != null;
		}

		@Override
		public void start() {
			this.cooldown = 0;
		}

		@Override
		public void onRemove() {
			this.owner.setShooting(false);
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.owner.getTarget();
			double d = 64.0;
			if (livingEntity.squaredDistanceTo(this.owner) < 4096.0 && this.owner.canSee(livingEntity)) {
				World world = this.owner.field_6002;
				this.cooldown++;
				if (this.cooldown == 10) {
					world.method_8444(null, 1015, new BlockPos(this.owner), 0);
				}

				if (this.cooldown == 20) {
					double e = 4.0;
					Vec3d vec3d = this.owner.method_5828(1.0F);
					double f = livingEntity.x - (this.owner.x + vec3d.x * 4.0);
					double g = livingEntity.method_5829().minY + (double)(livingEntity.getHeight() / 2.0F) - (0.5 + this.owner.y + (double)(this.owner.getHeight() / 2.0F));
					double h = livingEntity.z - (this.owner.z + vec3d.z * 4.0);
					world.method_8444(null, 1016, new BlockPos(this.owner), 0);
					FireballEntity fireballEntity = new FireballEntity(world, this.owner, f, g, h);
					fireballEntity.explosionPower = this.owner.getFireballStrength();
					fireballEntity.x = this.owner.x + vec3d.x * 4.0;
					fireballEntity.y = this.owner.y + (double)(this.owner.getHeight() / 2.0F) + 0.5;
					fireballEntity.z = this.owner.z + vec3d.z * 4.0;
					world.spawnEntity(fireballEntity);
					this.cooldown = -40;
				}
			} else if (this.cooldown > 0) {
				this.cooldown--;
			}

			this.owner.setShooting(this.cooldown > 10);
		}
	}

	static class class_1572 extends Goal {
		private final GhastEntity field_7274;

		public class_1572(GhastEntity ghastEntity) {
			this.field_7274 = ghastEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			return true;
		}

		@Override
		public void tick() {
			if (this.field_7274.getTarget() == null) {
				Vec3d vec3d = this.field_7274.method_18798();
				this.field_7274.yaw = -((float)MathHelper.atan2(vec3d.x, vec3d.z)) * (180.0F / (float)Math.PI);
				this.field_7274.field_6283 = this.field_7274.yaw;
			} else {
				LivingEntity livingEntity = this.field_7274.getTarget();
				double d = 64.0;
				if (livingEntity.squaredDistanceTo(this.field_7274) < 4096.0) {
					double e = livingEntity.x - this.field_7274.x;
					double f = livingEntity.z - this.field_7274.z;
					this.field_7274.yaw = -((float)MathHelper.atan2(e, f)) * (180.0F / (float)Math.PI);
					this.field_7274.field_6283 = this.field_7274.yaw;
				}
			}
		}
	}
}
