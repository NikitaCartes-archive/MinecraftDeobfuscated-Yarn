package net.minecraft.entity.mob;

import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.EntityData;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.control.BodyControl;
import net.minecraft.entity.ai.control.LookControl;
import net.minecraft.entity.ai.control.MoveControl;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.IWorld;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.World;

public class PhantomEntity extends FlyingEntity implements Monster {
	private static final TrackedData<Integer> SIZE = DataTracker.registerData(PhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private Vec3d targetPosition = Vec3d.ZERO;
	private BlockPos circlingCenter = BlockPos.ORIGIN;
	private PhantomEntity.PhantomMovementType movementType = PhantomEntity.PhantomMovementType.CIRCLE;

	public PhantomEntity(EntityType<? extends PhantomEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
		this.moveControl = new PhantomEntity.PhantomMoveControl(this);
		this.lookControl = new PhantomEntity.PhantomLookControl(this);
	}

	@Override
	protected BodyControl createBodyControl() {
		return new PhantomEntity.PhantomBodyControl(this);
	}

	@Override
	protected void initGoals() {
		this.goalSelector.add(1, new PhantomEntity.StartAttackGoal());
		this.goalSelector.add(2, new PhantomEntity.SwoopMovementGoal());
		this.goalSelector.add(3, new PhantomEntity.CircleMovementGoal());
		this.targetSelector.add(1, new PhantomEntity.FindTargetGoal());
	}

	@Override
	protected void initDataTracker() {
		super.initDataTracker();
		this.dataTracker.startTracking(SIZE, 0);
	}

	public void setPhantomSize(int size) {
		this.dataTracker.set(SIZE, MathHelper.clamp(size, 0, 64));
	}

	private void onSizeChanged() {
		this.calculateDimensions();
		this.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE).setBaseValue((double)(6 + this.getPhantomSize()));
	}

	public int getPhantomSize() {
		return this.dataTracker.get(SIZE);
	}

	@Override
	protected float getActiveEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.35F;
	}

	@Override
	public void onTrackedDataSet(TrackedData<?> data) {
		if (SIZE.equals(data)) {
			this.onSizeChanged();
		}

		super.onTrackedDataSet(data);
	}

	@Override
	protected boolean isDisallowedInPeaceful() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.world.isClient) {
			float f = MathHelper.cos((float)(this.getEntityId() * 3 + this.age) * 0.13F + (float) Math.PI);
			float g = MathHelper.cos((float)(this.getEntityId() * 3 + this.age + 1) * 0.13F + (float) Math.PI);
			if (f > 0.0F && g <= 0.0F) {
				this.world
					.playSound(
						this.getX(),
						this.getY(),
						this.getZ(),
						SoundEvents.ENTITY_PHANTOM_FLAP,
						this.getSoundCategory(),
						0.95F + this.random.nextFloat() * 0.05F,
						0.95F + this.random.nextFloat() * 0.05F,
						false
					);
			}

			int i = this.getPhantomSize();
			float h = MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float j = MathHelper.sin(this.yaw * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float k = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
			this.world.addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)h, this.getY() + (double)k, this.getZ() + (double)j, 0.0, 0.0, 0.0);
			this.world.addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)h, this.getY() + (double)k, this.getZ() - (double)j, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public void tickMovement() {
		if (this.isAlive() && this.isInDaylight()) {
			this.setOnFireFor(8);
		}

		super.tickMovement();
	}

	@Override
	protected void mobTick() {
		super.mobTick();
	}

	@Override
	public EntityData initialize(
		IWorld world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable CompoundTag entityTag
	) {
		this.circlingCenter = this.getBlockPos().up(5);
		this.setPhantomSize(0);
		return super.initialize(world, difficulty, spawnReason, entityData, entityTag);
	}

	@Override
	public void readCustomDataFromTag(CompoundTag tag) {
		super.readCustomDataFromTag(tag);
		if (tag.contains("AX")) {
			this.circlingCenter = new BlockPos(tag.getInt("AX"), tag.getInt("AY"), tag.getInt("AZ"));
		}

		this.setPhantomSize(tag.getInt("Size"));
	}

	@Override
	public void writeCustomDataToTag(CompoundTag tag) {
		super.writeCustomDataToTag(tag);
		tag.putInt("AX", this.circlingCenter.getX());
		tag.putInt("AY", this.circlingCenter.getY());
		tag.putInt("AZ", this.circlingCenter.getZ());
		tag.putInt("Size", this.getPhantomSize());
	}

	@Environment(EnvType.CLIENT)
	@Override
	public boolean shouldRender(double distance) {
		return true;
	}

	@Override
	public SoundCategory getSoundCategory() {
		return SoundCategory.HOSTILE;
	}

	@Override
	protected SoundEvent getAmbientSound() {
		return SoundEvents.ENTITY_PHANTOM_AMBIENT;
	}

	@Override
	protected SoundEvent getHurtSound(DamageSource source) {
		return SoundEvents.ENTITY_PHANTOM_HURT;
	}

	@Override
	protected SoundEvent getDeathSound() {
		return SoundEvents.ENTITY_PHANTOM_DEATH;
	}

	@Override
	public EntityGroup getGroup() {
		return EntityGroup.UNDEAD;
	}

	@Override
	protected float getSoundVolume() {
		return 1.0F;
	}

	@Override
	public boolean canTarget(EntityType<?> type) {
		return true;
	}

	@Override
	public EntityDimensions getDimensions(EntityPose pose) {
		int i = this.getPhantomSize();
		EntityDimensions entityDimensions = super.getDimensions(pose);
		float f = (entityDimensions.width + 0.2F * (float)i) / entityDimensions.width;
		return entityDimensions.scaled(f);
	}

	class CircleMovementGoal extends PhantomEntity.MovementGoal {
		private float angle;
		private float radius;
		private float yOffset;
		private float circlingDirection;

		private CircleMovementGoal() {
		}

		@Override
		public boolean canStart() {
			return PhantomEntity.this.getTarget() == null || PhantomEntity.this.movementType == PhantomEntity.PhantomMovementType.CIRCLE;
		}

		@Override
		public void start() {
			this.radius = 5.0F + PhantomEntity.this.random.nextFloat() * 10.0F;
			this.yOffset = -4.0F + PhantomEntity.this.random.nextFloat() * 9.0F;
			this.circlingDirection = PhantomEntity.this.random.nextBoolean() ? 1.0F : -1.0F;
			this.adjustDirection();
		}

		@Override
		public void tick() {
			if (PhantomEntity.this.random.nextInt(350) == 0) {
				this.yOffset = -4.0F + PhantomEntity.this.random.nextFloat() * 9.0F;
			}

			if (PhantomEntity.this.random.nextInt(250) == 0) {
				this.radius++;
				if (this.radius > 15.0F) {
					this.radius = 5.0F;
					this.circlingDirection = -this.circlingDirection;
				}
			}

			if (PhantomEntity.this.random.nextInt(450) == 0) {
				this.angle = PhantomEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
				this.adjustDirection();
			}

			if (this.isCloseToTarget()) {
				this.adjustDirection();
			}

			if (PhantomEntity.this.targetPosition.y < PhantomEntity.this.getY() && !PhantomEntity.this.world.isAir(PhantomEntity.this.getBlockPos().down(1))) {
				this.yOffset = Math.max(1.0F, this.yOffset);
				this.adjustDirection();
			}

			if (PhantomEntity.this.targetPosition.y > PhantomEntity.this.getY() && !PhantomEntity.this.world.isAir(PhantomEntity.this.getBlockPos().up(1))) {
				this.yOffset = Math.min(-1.0F, this.yOffset);
				this.adjustDirection();
			}
		}

		private void adjustDirection() {
			if (BlockPos.ORIGIN.equals(PhantomEntity.this.circlingCenter)) {
				PhantomEntity.this.circlingCenter = PhantomEntity.this.getBlockPos();
			}

			this.angle = this.angle + this.circlingDirection * 15.0F * (float) (Math.PI / 180.0);
			PhantomEntity.this.targetPosition = Vec3d.of(PhantomEntity.this.circlingCenter)
				.add((double)(this.radius * MathHelper.cos(this.angle)), (double)(-4.0F + this.yOffset), (double)(this.radius * MathHelper.sin(this.angle)));
		}
	}

	class FindTargetGoal extends Goal {
		private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = new TargetPredicate().setBaseMaxDistance(64.0);
		private int delay = 20;

		private FindTargetGoal() {
		}

		@Override
		public boolean canStart() {
			if (this.delay > 0) {
				this.delay--;
				return false;
			} else {
				this.delay = 60;
				List<PlayerEntity> list = PhantomEntity.this.world
					.getPlayers(this.PLAYERS_IN_RANGE_PREDICATE, PhantomEntity.this, PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0));
				if (!list.isEmpty()) {
					list.sort((playerEntityx, playerEntity2) -> playerEntityx.getY() > playerEntity2.getY() ? -1 : 1);

					for (PlayerEntity playerEntity : list) {
						if (PhantomEntity.this.isTarget(playerEntity, TargetPredicate.DEFAULT)) {
							PhantomEntity.this.setTarget(playerEntity);
							return true;
						}
					}
				}

				return false;
			}
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = PhantomEntity.this.getTarget();
			return livingEntity != null ? PhantomEntity.this.isTarget(livingEntity, TargetPredicate.DEFAULT) : false;
		}
	}

	abstract class MovementGoal extends Goal {
		public MovementGoal() {
			this.setControls(EnumSet.of(Goal.Control.MOVE));
		}

		protected boolean isCloseToTarget() {
			return PhantomEntity.this.targetPosition.squaredDistanceTo(PhantomEntity.this.getX(), PhantomEntity.this.getY(), PhantomEntity.this.getZ()) < 4.0;
		}
	}

	class PhantomBodyControl extends BodyControl {
		public PhantomBodyControl(MobEntity entity) {
			super(entity);
		}

		@Override
		public void tick() {
			PhantomEntity.this.headYaw = PhantomEntity.this.bodyYaw;
			PhantomEntity.this.bodyYaw = PhantomEntity.this.yaw;
		}
	}

	class PhantomLookControl extends LookControl {
		public PhantomLookControl(MobEntity entity) {
			super(entity);
		}

		@Override
		public void tick() {
		}
	}

	class PhantomMoveControl extends MoveControl {
		/**
		 * The movement speed that the phantom tends towards
		 */
		private float targetSpeed = 0.1F;

		public PhantomMoveControl(MobEntity owner) {
			super(owner);
		}

		@Override
		public void tick() {
			if (PhantomEntity.this.horizontalCollision) {
				PhantomEntity.this.yaw += 180.0F;
				this.targetSpeed = 0.1F;
			}

			float f = (float)(PhantomEntity.this.targetPosition.x - PhantomEntity.this.getX());
			float g = (float)(PhantomEntity.this.targetPosition.y - PhantomEntity.this.getY());
			float h = (float)(PhantomEntity.this.targetPosition.z - PhantomEntity.this.getZ());
			double d = (double)MathHelper.sqrt(f * f + h * h);
			double e = 1.0 - (double)MathHelper.abs(g * 0.7F) / d;
			f = (float)((double)f * e);
			h = (float)((double)h * e);
			d = (double)MathHelper.sqrt(f * f + h * h);
			double i = (double)MathHelper.sqrt(f * f + h * h + g * g);
			float j = PhantomEntity.this.yaw;
			float k = (float)MathHelper.atan2((double)h, (double)f);
			float l = MathHelper.wrapDegrees(PhantomEntity.this.yaw + 90.0F);
			float m = MathHelper.wrapDegrees(k * (180.0F / (float)Math.PI));
			PhantomEntity.this.yaw = MathHelper.stepUnwrappedAngleTowards(l, m, 4.0F) - 90.0F;
			PhantomEntity.this.bodyYaw = PhantomEntity.this.yaw;
			if (MathHelper.angleBetween(j, PhantomEntity.this.yaw) < 3.0F) {
				this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 1.8F, 0.005F * (1.8F / this.targetSpeed));
			} else {
				this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 0.2F, 0.025F);
			}

			float n = (float)(-(MathHelper.atan2((double)(-g), d) * 180.0F / (float)Math.PI));
			PhantomEntity.this.pitch = n;
			float o = PhantomEntity.this.yaw + 90.0F;
			double p = (double)(this.targetSpeed * MathHelper.cos(o * (float) (Math.PI / 180.0))) * Math.abs((double)f / i);
			double q = (double)(this.targetSpeed * MathHelper.sin(o * (float) (Math.PI / 180.0))) * Math.abs((double)h / i);
			double r = (double)(this.targetSpeed * MathHelper.sin(n * (float) (Math.PI / 180.0))) * Math.abs((double)g / i);
			Vec3d vec3d = PhantomEntity.this.getVelocity();
			PhantomEntity.this.setVelocity(vec3d.add(new Vec3d(p, r, q).subtract(vec3d).multiply(0.2)));
		}
	}

	static enum PhantomMovementType {
		CIRCLE,
		SWOOP;
	}

	class StartAttackGoal extends Goal {
		private int cooldown;

		private StartAttackGoal() {
		}

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = PhantomEntity.this.getTarget();
			return livingEntity != null ? PhantomEntity.this.isTarget(PhantomEntity.this.getTarget(), TargetPredicate.DEFAULT) : false;
		}

		@Override
		public void start() {
			this.cooldown = 10;
			PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
			this.startSwoop();
		}

		@Override
		public void stop() {
			PhantomEntity.this.circlingCenter = PhantomEntity.this.world
				.getTopPosition(Heightmap.Type.MOTION_BLOCKING, PhantomEntity.this.circlingCenter)
				.up(10 + PhantomEntity.this.random.nextInt(20));
		}

		@Override
		public void tick() {
			if (PhantomEntity.this.movementType == PhantomEntity.PhantomMovementType.CIRCLE) {
				this.cooldown--;
				if (this.cooldown <= 0) {
					PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.SWOOP;
					this.startSwoop();
					this.cooldown = (8 + PhantomEntity.this.random.nextInt(4)) * 20;
					PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + PhantomEntity.this.random.nextFloat() * 0.1F);
				}
			}
		}

		private void startSwoop() {
			PhantomEntity.this.circlingCenter = PhantomEntity.this.getTarget().getBlockPos().up(20 + PhantomEntity.this.random.nextInt(20));
			if (PhantomEntity.this.circlingCenter.getY() < PhantomEntity.this.world.getSeaLevel()) {
				PhantomEntity.this.circlingCenter = new BlockPos(
					PhantomEntity.this.circlingCenter.getX(), PhantomEntity.this.world.getSeaLevel() + 1, PhantomEntity.this.circlingCenter.getZ()
				);
			}
		}
	}

	class SwoopMovementGoal extends PhantomEntity.MovementGoal {
		private SwoopMovementGoal() {
		}

		@Override
		public boolean canStart() {
			return PhantomEntity.this.getTarget() != null && PhantomEntity.this.movementType == PhantomEntity.PhantomMovementType.SWOOP;
		}

		@Override
		public boolean shouldContinue() {
			LivingEntity livingEntity = PhantomEntity.this.getTarget();
			if (livingEntity == null) {
				return false;
			} else if (!livingEntity.isAlive()) {
				return false;
			} else if (!(livingEntity instanceof PlayerEntity) || !((PlayerEntity)livingEntity).isSpectator() && !((PlayerEntity)livingEntity).isCreative()) {
				if (!this.canStart()) {
					return false;
				} else {
					if (PhantomEntity.this.age % 20 == 0) {
						List<CatEntity> list = PhantomEntity.this.world
							.getEntities(CatEntity.class, PhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY);
						if (!list.isEmpty()) {
							for (CatEntity catEntity : list) {
								catEntity.hiss();
							}

							return false;
						}
					}

					return true;
				}
			} else {
				return false;
			}
		}

		@Override
		public void start() {
		}

		@Override
		public void stop() {
			PhantomEntity.this.setTarget(null);
			PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
		}

		@Override
		public void tick() {
			LivingEntity livingEntity = PhantomEntity.this.getTarget();
			PhantomEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5), livingEntity.getZ());
			if (PhantomEntity.this.getBoundingBox().expand(0.2F).intersects(livingEntity.getBoundingBox())) {
				PhantomEntity.this.tryAttack(livingEntity);
				PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
				if (!PhantomEntity.this.isSilent()) {
					PhantomEntity.this.world.syncWorldEvent(1039, PhantomEntity.this.getBlockPos(), 0);
				}
			} else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
				PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
			}
		}
	}
}
