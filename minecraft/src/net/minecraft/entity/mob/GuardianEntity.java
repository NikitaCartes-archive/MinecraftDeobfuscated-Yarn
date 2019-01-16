package net.minecraft.entity.mob;

import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1361;
import net.minecraft.class_1370;
import net.minecraft.class_1376;
import net.minecraft.class_1379;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.pathing.EntityNavigation;
import net.minecraft.entity.ai.pathing.SwimNavigation;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Difficulty;
import net.minecraft.world.IWorld;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;

public class GuardianEntity extends HostileEntity {
	private static final TrackedData<Boolean> field_7280 = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> field_7290 = DataTracker.registerData(GuardianEntity.class, TrackedDataHandlerRegistry.INTEGER);
	protected float field_7286;
	protected float field_7284;
	protected float field_7281;
	protected float field_7285;
	protected float field_7287;
	private LivingEntity field_7288;
	private int field_7282;
	private boolean field_7283;
	protected class_1379 field_7289;

	protected GuardianEntity(EntityType<?> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 10;
		this.moveControl = new GuardianEntity.GuardianMoveControl(this);
		this.field_7286 = this.random.nextFloat();
		this.field_7284 = this.field_7286;
	}

	public GuardianEntity(World world) {
		this(EntityType.GUARDIAN, world);
	}

	@Override
	protected void method_5959() {
		class_1370 lv = new class_1370(this, 1.0);
		this.field_7289 = new class_1379(this, 1.0, 80);
		this.goalSelector.add(4, new GuardianEntity.class_1578(this));
		this.goalSelector.add(5, lv);
		this.goalSelector.add(7, this.field_7289);
		this.goalSelector.add(8, new class_1361(this, PlayerEntity.class, 8.0F));
		this.goalSelector.add(8, new class_1361(this, GuardianEntity.class, 12.0F, 0.01F));
		this.goalSelector.add(9, new class_1376(this));
		this.field_7289.setControlBits(3);
		lv.setControlBits(3);
		this.targetSelector.add(1, new FollowTargetGoal(this, LivingEntity.class, 10, true, false, new GuardianEntity.class_1579(this)));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
		this.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getAttributeInstance(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
		this.getAttributeInstance(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
	}

	@Override
	protected EntityNavigation createNavigation(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(field_7280, false);
		this.dataTracker.startTracking(field_7290, 0);
	}

	@Override
	public boolean method_6094() {
		return true;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.AQUATIC;
	}

	public boolean method_7058() {
		return this.dataTracker.get(field_7280);
	}

	private void method_7054(boolean bl) {
		this.dataTracker.set(field_7280, bl);
	}

	public int method_7055() {
		return 80;
	}

	private void method_7060(int i) {
		this.dataTracker.set(field_7290, i);
	}

	public boolean method_7063() {
		return this.dataTracker.get(field_7290) != 0;
	}

	@Nullable
	public LivingEntity method_7052() {
		if (!this.method_7063()) {
			return null;
		} else if (this.world.isClient) {
			if (this.field_7288 != null) {
				return this.field_7288;
			} else {
				Entity entity = this.world.getEntityById(this.dataTracker.get(field_7290));
				if (entity instanceof LivingEntity) {
					this.field_7288 = (LivingEntity)entity;
					return this.field_7288;
				} else {
					return null;
				}
			}
		} else {
			return this.getTarget();
		}
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> trackedData) {
		super.onTrackedDataSet(trackedData);
		if (field_7290.equals(trackedData)) {
			this.field_7282 = 0;
			this.field_7288 = null;
		}
	}

	@Override
	public int method_5970() {
		return 160;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_14714 : SoundEvents.field_14968;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource damageSource) {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_14679 : SoundEvents.field_14758;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_15138 : SoundEvents.field_15232;
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	public float getEyeHeight() {
		return this.getHeight() * 0.5F;
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return viewableWorld.getFluidState(blockPos).matches(FluidTags.field_15517)
			? 10.0F + viewableWorld.method_8610(blockPos) - 0.5F
			: super.method_6144(blockPos, viewableWorld);
	}

	@Override
	public void updateMovement() {
		if (this.world.isClient) {
			this.field_7284 = this.field_7286;
			if (!this.isInsideWater()) {
				this.field_7281 = 2.0F;
				if (this.velocityY > 0.0 && this.field_7283 && !this.isSilent()) {
					this.world.playSound(this.x, this.y, this.z, this.method_7062(), this.getSoundCategory(), 1.0F, 1.0F, false);
				}

				this.field_7283 = this.velocityY < 0.0 && this.world.doesBlockHaveSolidTopSurface(new BlockPos(this).down());
			} else if (this.method_7058()) {
				if (this.field_7281 < 0.5F) {
					this.field_7281 = 4.0F;
				} else {
					this.field_7281 = this.field_7281 + (0.5F - this.field_7281) * 0.1F;
				}
			} else {
				this.field_7281 = this.field_7281 + (0.125F - this.field_7281) * 0.2F;
			}

			this.field_7286 = this.field_7286 + this.field_7281;
			this.field_7287 = this.field_7285;
			if (!this.isInsideWaterOrBubbleColumn()) {
				this.field_7285 = this.random.nextFloat();
			} else if (this.method_7058()) {
				this.field_7285 = this.field_7285 + (0.0F - this.field_7285) * 0.25F;
			} else {
				this.field_7285 = this.field_7285 + (1.0F - this.field_7285) * 0.06F;
			}

			if (this.method_7058() && this.isInsideWater()) {
				Vec3d vec3d = this.getRotationVec(0.0F);

				for (int i = 0; i < 2; i++) {
					this.world
						.addParticle(
							ParticleTypes.field_11247,
							this.x + (this.random.nextDouble() - 0.5) * (double)this.getWidth() - vec3d.x * 1.5,
							this.y + this.random.nextDouble() * (double)this.getHeight() - vec3d.y * 1.5,
							this.z + (this.random.nextDouble() - 0.5) * (double)this.getWidth() - vec3d.z * 1.5,
							0.0,
							0.0,
							0.0
						);
				}
			}

			if (this.method_7063()) {
				if (this.field_7282 < this.method_7055()) {
					this.field_7282++;
				}

				LivingEntity livingEntity = this.method_7052();
				if (livingEntity != null) {
					this.getLookControl().lookAt(livingEntity, 90.0F, 90.0F);
					this.getLookControl().tick();
					double d = (double)this.method_7061(0.0F);
					double e = livingEntity.x - this.x;
					double f = livingEntity.y + (double)(livingEntity.getHeight() * 0.5F) - (this.y + (double)this.getEyeHeight());
					double g = livingEntity.z - this.z;
					double h = Math.sqrt(e * e + f * f + g * g);
					e /= h;
					f /= h;
					g /= h;
					double j = this.random.nextDouble();

					while (j < h) {
						j += 1.8 - d + this.random.nextDouble() * (1.7 - d);
						this.world.addParticle(ParticleTypes.field_11247, this.x + e * j, this.y + f * j + (double)this.getEyeHeight(), this.z + g * j, 0.0, 0.0, 0.0);
					}
				}
			}
		}

		if (this.isInsideWaterOrBubbleColumn()) {
			this.setBreath(300);
		} else if (this.onGround) {
			this.velocityY += 0.5;
			this.velocityX = this.velocityX + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
			this.velocityZ = this.velocityZ + (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F);
			this.yaw = this.random.nextFloat() * 360.0F;
			this.onGround = false;
			this.velocityDirty = true;
		}

		if (this.method_7063()) {
			this.yaw = this.headYaw;
		}

		super.updateMovement();
	}

	protected SoundEvent method_7062() {
		return SoundEvents.field_14584;
	}

	@Environment(EnvType.CLIENT)
	public float method_7057(float f) {
		return MathHelper.lerp(f, this.field_7284, this.field_7286);
	}

	@Environment(EnvType.CLIENT)
	public float method_7053(float f) {
		return MathHelper.lerp(f, this.field_7287, this.field_7285);
	}

	public float method_7061(float f) {
		return ((float)this.field_7282 + f) / (float)this.method_7055();
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		return viewableWorld.method_8606(this, this.getBoundingBox());
	}

	@Override
	public boolean canSpawn(IWorld iWorld, SpawnType spawnType) {
		return (this.random.nextInt(20) == 0 || !iWorld.method_8626(new BlockPos(this))) && super.canSpawn(iWorld, spawnType);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (!this.method_7058() && !damageSource.getMagic() && damageSource.getSource() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)damageSource.getSource();
			if (!damageSource.isExplosive()) {
				livingEntity.damage(DamageSource.thorns(this), 2.0F);
			}
		}

		if (this.field_7289 != null) {
			this.field_7289.method_6304();
		}

		return super.damage(damageSource, f);
	}

	@Override
	public int method_5978() {
		return 180;
	}

	@Override
	public void method_6091(float f, float g, float h) {
		if (this.method_6034() && this.isInsideWater()) {
			this.method_5724(f, g, h, 0.1F);
			this.move(MovementType.SELF, this.velocityX, this.velocityY, this.velocityZ);
			this.velocityX *= 0.9F;
			this.velocityY *= 0.9F;
			this.velocityZ *= 0.9F;
			if (!this.method_7058() && this.getTarget() == null) {
				this.velocityY -= 0.005;
			}
		} else {
			super.method_6091(f, g, h);
		}
	}

	static class GuardianMoveControl extends MoveControl {
		private final GuardianEntity guardian;

		public GuardianMoveControl(GuardianEntity guardianEntity) {
			super(guardianEntity);
			this.guardian = guardianEntity;
		}

		@Override
		public void tick() {
			if (this.field_6374 == MoveControl.class_1336.field_6378 && !this.guardian.getNavigation().method_6357()) {
				double d = this.field_6370 - this.guardian.x;
				double e = this.field_6369 - this.guardian.y;
				double f = this.field_6367 - this.guardian.z;
				double g = (double)MathHelper.sqrt(d * d + e * e + f * f);
				e /= g;
				float h = (float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F;
				this.guardian.yaw = this.method_6238(this.guardian.yaw, h, 90.0F);
				this.guardian.field_6283 = this.guardian.yaw;
				float i = (float)(this.field_6372 * this.guardian.getAttributeInstance(EntityAttributes.MOVEMENT_SPEED).getValue());
				this.guardian.method_6125(MathHelper.lerp(0.125F, this.guardian.method_6029(), i));
				double j = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.5) * 0.05;
				double k = Math.cos((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				double l = Math.sin((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				this.guardian.velocityX += j * k;
				this.guardian.velocityZ += j * l;
				j = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.75) * 0.05;
				this.guardian.velocityY += j * (l + k) * 0.25;
				this.guardian.velocityY = this.guardian.velocityY + (double)this.guardian.method_6029() * e * 0.1;
				LookControl lookControl = this.guardian.getLookControl();
				double m = this.guardian.x + d / g * 2.0;
				double n = (double)this.guardian.getEyeHeight() + this.guardian.y + e / g;
				double o = this.guardian.z + f / g * 2.0;
				double p = lookControl.getLookX();
				double q = lookControl.getLookY();
				double r = lookControl.getLookZ();
				if (!lookControl.isActive()) {
					p = m;
					q = n;
					r = o;
				}

				this.guardian.getLookControl().lookAt(MathHelper.lerp(0.125, p, m), MathHelper.lerp(0.125, q, n), MathHelper.lerp(0.125, r, o), 10.0F, 40.0F);
				this.guardian.method_7054(true);
			} else {
				this.guardian.method_6125(0.0F);
				this.guardian.method_7054(false);
			}
		}
	}

	static class class_1578 extends Goal {
		private final GuardianEntity field_7293;
		private int field_7291;
		private final boolean field_7292;

		public class_1578(GuardianEntity guardianEntity) {
			this.field_7293 = guardianEntity;
			this.field_7292 = guardianEntity instanceof ElderGuardianEntity;
			this.setControlBits(3);
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.field_7293.getTarget();
			return livingEntity != null && livingEntity.isValid();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && (this.field_7292 || this.field_7293.squaredDistanceTo(this.field_7293.getTarget()) > 9.0);
		}

		@Override
		public void start() {
			this.field_7291 = -10;
			this.field_7293.getNavigation().method_6340();
			this.field_7293.getLookControl().lookAt(this.field_7293.getTarget(), 90.0F, 90.0F);
			this.field_7293.velocityDirty = true;
		}

		@Override
		public void onRemove() {
			this.field_7293.method_7060(0);
			this.field_7293.setTarget(null);
			this.field_7293.field_7289.method_6304();
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.field_7293.getTarget();
			this.field_7293.getNavigation().method_6340();
			this.field_7293.getLookControl().lookAt(livingEntity, 90.0F, 90.0F);
			if (!this.field_7293.canSee(livingEntity)) {
				this.field_7293.setTarget(null);
			} else {
				this.field_7291++;
				if (this.field_7291 == 0) {
					this.field_7293.method_7060(this.field_7293.getTarget().getEntityId());
					this.field_7293.world.summonParticle(this.field_7293, (byte)21);
				} else if (this.field_7291 >= this.field_7293.method_7055()) {
					float f = 1.0F;
					if (this.field_7293.world.getDifficulty() == Difficulty.HARD) {
						f += 2.0F;
					}

					if (this.field_7292) {
						f += 2.0F;
					}

					livingEntity.damage(DamageSource.magic(this.field_7293, this.field_7293), f);
					livingEntity.damage(DamageSource.mob(this.field_7293), (float)this.field_7293.getAttributeInstance(EntityAttributes.ATTACK_DAMAGE).getValue());
					this.field_7293.setTarget(null);
				}

				super.tick();
			}
		}
	}

	static class class_1579 implements Predicate<LivingEntity> {
		private final GuardianEntity field_7294;

		public class_1579(GuardianEntity guardianEntity) {
			this.field_7294 = guardianEntity;
		}

		public boolean method_7064(@Nullable LivingEntity livingEntity) {
			return (livingEntity instanceof PlayerEntity || livingEntity instanceof SquidEntity) && livingEntity.squaredDistanceTo(this.field_7294) > 9.0;
		}
	}
}
