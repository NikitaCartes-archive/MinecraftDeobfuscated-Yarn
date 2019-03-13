package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_1370;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntitySize;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.SpawnType;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.ai.goal.LookAroundGoal;
import net.minecraft.entity.ai.goal.LookAtEntityGoal;
import net.minecraft.entity.ai.goal.WanderAroundGoal;
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
	protected float spikesExtension;
	protected float prevSpikesExtension;
	protected float spikesExtensionRate;
	protected float tailAngle;
	protected float prevTailAngle;
	private LivingEntity cachedBeamTarget;
	private int beamTicks;
	private boolean flopping;
	protected WanderAroundGoal field_7289;

	public GuardianEntity(EntityType<? extends GuardianEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 10;
		this.field_6207 = new GuardianEntity.GuardianMoveControl(this);
		this.spikesExtension = this.random.nextFloat();
		this.prevSpikesExtension = this.spikesExtension;
	}

	@Override
	protected void initGoals() {
		class_1370 lv = new class_1370(this, 1.0);
		this.field_7289 = new WanderAroundGoal(this, 1.0, 80);
		this.field_6201.add(4, new GuardianEntity.FireBeamGoal(this));
		this.field_6201.add(5, lv);
		this.field_6201.add(7, this.field_7289);
		this.field_6201.add(8, new LookAtEntityGoal(this, PlayerEntity.class, 8.0F));
		this.field_6201.add(8, new LookAtEntityGoal(this, GuardianEntity.class, 12.0F, 0.01F));
		this.field_6201.add(9, new LookAroundGoal(this));
		this.field_7289.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		lv.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		this.field_6185.add(1, new FollowTargetGoal(this, LivingEntity.class, 10, true, false, new GuardianEntity.GuardianTargetPredicate(this)));
	}

	@Override
	protected void initAttributes() {
		super.initAttributes();
		this.method_5996(EntityAttributes.ATTACK_DAMAGE).setBaseValue(6.0);
		this.method_5996(EntityAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.method_5996(EntityAttributes.FOLLOW_RANGE).setBaseValue(16.0);
		this.method_5996(EntityAttributes.MAX_HEALTH).setBaseValue(30.0);
	}

	@Override
	protected EntityNavigation method_5965(World world) {
		return new SwimNavigation(this, world);
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.field_6011.startTracking(field_7280, false);
		this.field_6011.startTracking(field_7290, 0);
	}

	@Override
	public boolean canBreatheInWater() {
		return true;
	}

	@Override
	public EntityGroup method_6046() {
		return EntityGroup.AQUATIC;
	}

	public boolean areSpikesRetracted() {
		return this.field_6011.get(field_7280);
	}

	private void setSpikesRetracted(boolean bl) {
		this.field_6011.set(field_7280, bl);
	}

	public int getWarmupTime() {
		return 80;
	}

	private void setBeamTarget(int i) {
		this.field_6011.set(field_7290, i);
	}

	public boolean hasBeamTarget() {
		return this.field_6011.get(field_7290) != 0;
	}

	@Nullable
	public LivingEntity getBeamTarget() {
		if (!this.hasBeamTarget()) {
			return null;
		} else if (this.field_6002.isClient) {
			if (this.cachedBeamTarget != null) {
				return this.cachedBeamTarget;
			} else {
				Entity entity = this.field_6002.getEntityById(this.field_6011.get(field_7290));
				if (entity instanceof LivingEntity) {
					this.cachedBeamTarget = (LivingEntity)entity;
					return this.cachedBeamTarget;
				} else {
					return null;
				}
			}
		} else {
			return this.getTarget();
		}
	}

	@Override
	public void method_5674(TrackedData<?> trackedData) {
		super.method_5674(trackedData);
		if (field_7290.equals(trackedData)) {
			this.beamTicks = 0;
			this.cachedBeamTarget = null;
		}
	}

	@Override
	public int getMinAmbientSoundDelay() {
		return 160;
	}

	@Override
	protected SoundEvent method_5994() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_14714 : SoundEvents.field_14968;
	}

	@Override
	protected SoundEvent method_6011(DamageSource damageSource) {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_14679 : SoundEvents.field_14758;
	}

	@Override
	protected SoundEvent method_6002() {
		return this.isInsideWaterOrBubbleColumn() ? SoundEvents.field_15138 : SoundEvents.field_15232;
	}

	@Override
	protected boolean method_5658() {
		return false;
	}

	@Override
	protected float method_18394(EntityPose entityPose, EntitySize entitySize) {
		return entitySize.height * 0.5F;
	}

	@Override
	public float method_6144(BlockPos blockPos, ViewableWorld viewableWorld) {
		return viewableWorld.method_8316(blockPos).method_15767(FluidTags.field_15517)
			? 10.0F + viewableWorld.method_8610(blockPos) - 0.5F
			: super.method_6144(blockPos, viewableWorld);
	}

	@Override
	public void updateMovement() {
		if (this.isValid()) {
			if (this.field_6002.isClient) {
				this.prevSpikesExtension = this.spikesExtension;
				if (!this.isInsideWater()) {
					this.spikesExtensionRate = 2.0F;
					Vec3d vec3d = this.method_18798();
					if (vec3d.y > 0.0 && this.flopping && !this.isSilent()) {
						this.field_6002.method_8486(this.x, this.y, this.z, this.method_7062(), this.method_5634(), 1.0F, 1.0F, false);
					}

					this.flopping = vec3d.y < 0.0 && this.field_6002.method_8515(new BlockPos(this).down());
				} else if (this.areSpikesRetracted()) {
					if (this.spikesExtensionRate < 0.5F) {
						this.spikesExtensionRate = 4.0F;
					} else {
						this.spikesExtensionRate = this.spikesExtensionRate + (0.5F - this.spikesExtensionRate) * 0.1F;
					}
				} else {
					this.spikesExtensionRate = this.spikesExtensionRate + (0.125F - this.spikesExtensionRate) * 0.2F;
				}

				this.spikesExtension = this.spikesExtension + this.spikesExtensionRate;
				this.prevTailAngle = this.tailAngle;
				if (!this.isInsideWaterOrBubbleColumn()) {
					this.tailAngle = this.random.nextFloat();
				} else if (this.areSpikesRetracted()) {
					this.tailAngle = this.tailAngle + (0.0F - this.tailAngle) * 0.25F;
				} else {
					this.tailAngle = this.tailAngle + (1.0F - this.tailAngle) * 0.06F;
				}

				if (this.areSpikesRetracted() && this.isInsideWater()) {
					Vec3d vec3d = this.method_5828(0.0F);

					for (int i = 0; i < 2; i++) {
						this.field_6002
							.method_8406(
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

				if (this.hasBeamTarget()) {
					if (this.beamTicks < this.getWarmupTime()) {
						this.beamTicks++;
					}

					LivingEntity livingEntity = this.getBeamTarget();
					if (livingEntity != null) {
						this.method_5988().lookAt(livingEntity, 90.0F, 90.0F);
						this.method_5988().tick();
						double d = (double)this.getBeamProgress(0.0F);
						double e = livingEntity.x - this.x;
						double f = livingEntity.y + (double)(livingEntity.getHeight() * 0.5F) - (this.y + (double)this.getStandingEyeHeight());
						double g = livingEntity.z - this.z;
						double h = Math.sqrt(e * e + f * f + g * g);
						e /= h;
						f /= h;
						g /= h;
						double j = this.random.nextDouble();

						while (j < h) {
							j += 1.8 - d + this.random.nextDouble() * (1.7 - d);
							this.field_6002
								.method_8406(ParticleTypes.field_11247, this.x + e * j, this.y + f * j + (double)this.getStandingEyeHeight(), this.z + g * j, 0.0, 0.0, 0.0);
						}
					}
				}
			}

			if (this.isInsideWaterOrBubbleColumn()) {
				this.setBreath(300);
			} else if (this.onGround) {
				this.method_18799(
					this.method_18798().add((double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F), 0.5, (double)((this.random.nextFloat() * 2.0F - 1.0F) * 0.4F))
				);
				this.yaw = this.random.nextFloat() * 360.0F;
				this.onGround = false;
				this.velocityDirty = true;
			}

			if (this.hasBeamTarget()) {
				this.yaw = this.headYaw;
			}
		}

		super.updateMovement();
	}

	protected SoundEvent method_7062() {
		return SoundEvents.field_14584;
	}

	@Environment(EnvType.CLIENT)
	public float getSpikesExtension(float f) {
		return MathHelper.lerp(f, this.prevSpikesExtension, this.spikesExtension);
	}

	@Environment(EnvType.CLIENT)
	public float getTailAngle(float f) {
		return MathHelper.lerp(f, this.prevTailAngle, this.tailAngle);
	}

	public float getBeamProgress(float f) {
		return ((float)this.beamTicks + f) / (float)this.getWarmupTime();
	}

	@Override
	protected boolean checkLightLevelForSpawn() {
		return true;
	}

	@Override
	public boolean method_5957(ViewableWorld viewableWorld) {
		return viewableWorld.method_8606(this);
	}

	@Override
	public boolean method_5979(IWorld iWorld, SpawnType spawnType) {
		return (this.random.nextInt(20) == 0 || !iWorld.method_8626(new BlockPos(this))) && super.method_5979(iWorld, spawnType);
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (!this.areSpikesRetracted() && !damageSource.getMagic() && damageSource.method_5526() instanceof LivingEntity) {
			LivingEntity livingEntity = (LivingEntity)damageSource.method_5526();
			if (!damageSource.isExplosive()) {
				livingEntity.damage(DamageSource.method_5513(this), 2.0F);
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
	public void method_6091(Vec3d vec3d) {
		if (this.method_6034() && this.isInsideWater()) {
			this.method_5724(0.1F, vec3d);
			this.method_5784(MovementType.field_6308, this.method_18798());
			this.method_18799(this.method_18798().multiply(0.9));
			if (!this.areSpikesRetracted() && this.getTarget() == null) {
				this.method_18799(this.method_18798().add(0.0, -0.005, 0.0));
			}
		} else {
			super.method_6091(vec3d);
		}
	}

	static class FireBeamGoal extends Goal {
		private final GuardianEntity owner;
		private int beamTicks;
		private final boolean elderOwner;

		public FireBeamGoal(GuardianEntity guardianEntity) {
			this.owner = guardianEntity;
			this.elderOwner = guardianEntity instanceof ElderGuardianEntity;
			this.setControlBits(EnumSet.of(Goal.class_4134.field_18405, Goal.class_4134.field_18406));
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = this.owner.getTarget();
			return livingEntity != null && livingEntity.isValid();
		}

		@Override
		public boolean shouldContinue() {
			return super.shouldContinue() && (this.elderOwner || this.owner.squaredDistanceTo(this.owner.getTarget()) > 9.0);
		}

		@Override
		public void start() {
			this.beamTicks = -10;
			this.owner.method_5942().stop();
			this.owner.method_5988().lookAt(this.owner.getTarget(), 90.0F, 90.0F);
			this.owner.velocityDirty = true;
		}

		@Override
		public void onRemove() {
			this.owner.setBeamTarget(0);
			this.owner.setTarget(null);
			this.owner.field_7289.method_6304();
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = this.owner.getTarget();
			this.owner.method_5942().stop();
			this.owner.method_5988().lookAt(livingEntity, 90.0F, 90.0F);
			if (!this.owner.canSee(livingEntity)) {
				this.owner.setTarget(null);
			} else {
				this.beamTicks++;
				if (this.beamTicks == 0) {
					this.owner.setBeamTarget(this.owner.getTarget().getEntityId());
					this.owner.field_6002.summonParticle(this.owner, (byte)21);
				} else if (this.beamTicks >= this.owner.getWarmupTime()) {
					float f = 1.0F;
					if (this.owner.field_6002.getDifficulty() == Difficulty.HARD) {
						f += 2.0F;
					}

					if (this.elderOwner) {
						f += 2.0F;
					}

					livingEntity.damage(DamageSource.method_5536(this.owner, this.owner), f);
					livingEntity.damage(DamageSource.method_5511(this.owner), (float)this.owner.method_5996(EntityAttributes.ATTACK_DAMAGE).getValue());
					this.owner.setTarget(null);
				}

				super.tick();
			}
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
			if (this.state == MoveControl.State.field_6378 && !this.guardian.method_5942().isIdle()) {
				Vec3d vec3d = new Vec3d(this.targetX - this.guardian.x, this.targetY - this.guardian.y, this.targetZ - this.guardian.z);
				double d = vec3d.length();
				double e = vec3d.x / d;
				double f = vec3d.y / d;
				double g = vec3d.z / d;
				float h = (float)(MathHelper.atan2(vec3d.z, vec3d.x) * 180.0F / (float)Math.PI) - 90.0F;
				this.guardian.yaw = this.method_6238(this.guardian.yaw, h, 90.0F);
				this.guardian.field_6283 = this.guardian.yaw;
				float i = (float)(this.speed * this.guardian.method_5996(EntityAttributes.MOVEMENT_SPEED).getValue());
				float j = MathHelper.lerp(0.125F, this.guardian.getMovementSpeed(), i);
				this.guardian.setMovementSpeed(j);
				double k = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.5) * 0.05;
				double l = Math.cos((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				double m = Math.sin((double)(this.guardian.yaw * (float) (Math.PI / 180.0)));
				double n = Math.sin((double)(this.guardian.age + this.guardian.getEntityId()) * 0.75) * 0.05;
				this.guardian.method_18799(this.guardian.method_18798().add(k * l, n * (m + l) * 0.25 + (double)j * f * 0.1, k * m));
				LookControl lookControl = this.guardian.method_5988();
				double o = this.guardian.x + e * 2.0;
				double p = (double)this.guardian.getStandingEyeHeight() + this.guardian.y + f / d;
				double q = this.guardian.z + g * 2.0;
				double r = lookControl.getLookX();
				double s = lookControl.getLookY();
				double t = lookControl.getLookZ();
				if (!lookControl.isActive()) {
					r = o;
					s = p;
					t = q;
				}

				this.guardian.method_5988().lookAt(MathHelper.lerp(0.125, r, o), MathHelper.lerp(0.125, s, p), MathHelper.lerp(0.125, t, q), 10.0F, 40.0F);
				this.guardian.setSpikesRetracted(true);
			} else {
				this.guardian.setMovementSpeed(0.0F);
				this.guardian.setSpikesRetracted(false);
			}
		}
	}

	static class GuardianTargetPredicate implements Predicate<LivingEntity> {
		private final GuardianEntity owner;

		public GuardianTargetPredicate(GuardianEntity guardianEntity) {
			this.owner = guardianEntity;
		}

		public boolean method_7064(@Nullable LivingEntity livingEntity) {
			return (livingEntity instanceof PlayerEntity || livingEntity instanceof SquidEntity) && livingEntity.squaredDistanceTo(this.owner) > 9.0;
		}
	}
}
