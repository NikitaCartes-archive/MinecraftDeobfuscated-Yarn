package net.minecraft.entity.mob;

import java.util.Comparator;
import java.util.EnumSet;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
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
import net.minecraft.nbt.NbtCompound;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.Heightmap;
import net.minecraft.world.LocalDifficulty;
import net.minecraft.world.ServerWorldAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import org.joml.Vector3f;

public class PhantomEntity extends FlyingEntity implements Monster {
	public static final float field_30475 = 7.448451F;
	public static final int WING_FLAP_TICKS = MathHelper.ceil(24.166098F);
	private static final TrackedData<Integer> SIZE = DataTracker.registerData(PhantomEntity.class, TrackedDataHandlerRegistry.INTEGER);
	Vec3d targetPosition = Vec3d.ZERO;
	BlockPos circlingCenter = BlockPos.ORIGIN;
	PhantomEntity.PhantomMovementType movementType = PhantomEntity.PhantomMovementType.CIRCLE;

	public PhantomEntity(EntityType<? extends PhantomEntity> entityType, World world) {
		super(entityType, world);
		this.experiencePoints = 5;
		this.moveControl = new PhantomEntity.PhantomMoveControl(this);
		this.lookControl = new PhantomEntity.PhantomLookControl(this);
	}

	@Override
	public boolean isFlappingWings() {
		return (this.getWingFlapTickOffset() + this.age) % WING_FLAP_TICKS == 0;
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

	public int getWingFlapTickOffset() {
		return this.getId() * 3;
	}

	@Override
	protected boolean isDisallowedInPeaceful() {
		return true;
	}

	@Override
	public void tick() {
		super.tick();
		if (this.getWorld().isClient) {
			float f = MathHelper.cos((float)(this.getWingFlapTickOffset() + this.age) * 7.448451F * (float) (Math.PI / 180.0) + (float) Math.PI);
			float g = MathHelper.cos((float)(this.getWingFlapTickOffset() + this.age + 1) * 7.448451F * (float) (Math.PI / 180.0) + (float) Math.PI);
			if (f > 0.0F && g <= 0.0F) {
				this.getWorld()
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
			float h = MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float j = MathHelper.sin(this.getYaw() * (float) (Math.PI / 180.0)) * (1.3F + 0.21F * (float)i);
			float k = (0.3F + f * 0.45F) * ((float)i * 0.2F + 1.0F);
			this.getWorld().addParticle(ParticleTypes.MYCELIUM, this.getX() + (double)h, this.getY() + (double)k, this.getZ() + (double)j, 0.0, 0.0, 0.0);
			this.getWorld().addParticle(ParticleTypes.MYCELIUM, this.getX() - (double)h, this.getY() + (double)k, this.getZ() - (double)j, 0.0, 0.0, 0.0);
		}
	}

	@Override
	public void tickMovement() {
		if (this.isAlive() && this.isAffectedByDaylight()) {
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
		ServerWorldAccess world, LocalDifficulty difficulty, SpawnReason spawnReason, @Nullable EntityData entityData, @Nullable NbtCompound entityNbt
	) {
		this.circlingCenter = this.getBlockPos().up(5);
		this.setPhantomSize(0);
		return super.initialize(world, difficulty, spawnReason, entityData, entityNbt);
	}

	@Override
	public void readCustomDataFromNbt(NbtCompound nbt) {
		super.readCustomDataFromNbt(nbt);
		if (nbt.contains("AX")) {
			this.circlingCenter = new BlockPos(nbt.getInt("AX"), nbt.getInt("AY"), nbt.getInt("AZ"));
		}

		this.setPhantomSize(nbt.getInt("Size"));
	}

	@Override
	public void writeCustomDataToNbt(NbtCompound nbt) {
		super.writeCustomDataToNbt(nbt);
		nbt.putInt("AX", this.circlingCenter.getX());
		nbt.putInt("AY", this.circlingCenter.getY());
		nbt.putInt("AZ", this.circlingCenter.getZ());
		nbt.putInt("Size", this.getPhantomSize());
	}

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
		return entityDimensions.scaled(1.0F + 0.15F * (float)i);
	}

	@Override
	protected Vector3f getPassengerAttachmentPos(Entity passenger, EntityDimensions dimensions, float scaleFactor) {
		return new Vector3f(0.0F, dimensions.height * 0.675F, 0.0F);
	}

	@Override
	protected float getUnscaledRidingOffset(Entity vehicle) {
		return -0.125F;
	}

	class CircleMovementGoal extends PhantomEntity.MovementGoal {
		private float angle;
		private float radius;
		private float yOffset;
		private float circlingDirection;

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
			if (PhantomEntity.this.random.nextInt(this.getTickCount(350)) == 0) {
				this.yOffset = -4.0F + PhantomEntity.this.random.nextFloat() * 9.0F;
			}

			if (PhantomEntity.this.random.nextInt(this.getTickCount(250)) == 0) {
				this.radius++;
				if (this.radius > 15.0F) {
					this.radius = 5.0F;
					this.circlingDirection = -this.circlingDirection;
				}
			}

			if (PhantomEntity.this.random.nextInt(this.getTickCount(450)) == 0) {
				this.angle = PhantomEntity.this.random.nextFloat() * 2.0F * (float) Math.PI;
				this.adjustDirection();
			}

			if (this.isNearTarget()) {
				this.adjustDirection();
			}

			if (PhantomEntity.this.targetPosition.y < PhantomEntity.this.getY() && !PhantomEntity.this.getWorld().isAir(PhantomEntity.this.getBlockPos().down(1))) {
				this.yOffset = Math.max(1.0F, this.yOffset);
				this.adjustDirection();
			}

			if (PhantomEntity.this.targetPosition.y > PhantomEntity.this.getY() && !PhantomEntity.this.getWorld().isAir(PhantomEntity.this.getBlockPos().up(1))) {
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
		private final TargetPredicate PLAYERS_IN_RANGE_PREDICATE = TargetPredicate.createAttackable().setBaseMaxDistance(64.0);
		private int delay = toGoalTicks(20);

		@Override
		public boolean canStart() {
			if (this.delay > 0) {
				this.delay--;
				return false;
			} else {
				this.delay = toGoalTicks(60);
				List<PlayerEntity> list = PhantomEntity.this.getWorld()
					.getPlayers(this.PLAYERS_IN_RANGE_PREDICATE, PhantomEntity.this, PhantomEntity.this.getBoundingBox().expand(16.0, 64.0, 16.0));
				if (!list.isEmpty()) {
					list.sort(Comparator.comparing(Entity::getY).reversed());

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

		protected boolean isNearTarget() {
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
			PhantomEntity.this.bodyYaw = PhantomEntity.this.getYaw();
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
				PhantomEntity.this.setYaw(PhantomEntity.this.getYaw() + 180.0F);
				this.targetSpeed = 0.1F;
			}

			double d = PhantomEntity.this.targetPosition.x - PhantomEntity.this.getX();
			double e = PhantomEntity.this.targetPosition.y - PhantomEntity.this.getY();
			double f = PhantomEntity.this.targetPosition.z - PhantomEntity.this.getZ();
			double g = Math.sqrt(d * d + f * f);
			if (Math.abs(g) > 1.0E-5F) {
				double h = 1.0 - Math.abs(e * 0.7F) / g;
				d *= h;
				f *= h;
				g = Math.sqrt(d * d + f * f);
				double i = Math.sqrt(d * d + f * f + e * e);
				float j = PhantomEntity.this.getYaw();
				float k = (float)MathHelper.atan2(f, d);
				float l = MathHelper.wrapDegrees(PhantomEntity.this.getYaw() + 90.0F);
				float m = MathHelper.wrapDegrees(k * (180.0F / (float)Math.PI));
				PhantomEntity.this.setYaw(MathHelper.stepUnwrappedAngleTowards(l, m, 4.0F) - 90.0F);
				PhantomEntity.this.bodyYaw = PhantomEntity.this.getYaw();
				if (MathHelper.angleBetween(j, PhantomEntity.this.getYaw()) < 3.0F) {
					this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 1.8F, 0.005F * (1.8F / this.targetSpeed));
				} else {
					this.targetSpeed = MathHelper.stepTowards(this.targetSpeed, 0.2F, 0.025F);
				}

				float n = (float)(-(MathHelper.atan2(-e, g) * 180.0F / (float)Math.PI));
				PhantomEntity.this.setPitch(n);
				float o = PhantomEntity.this.getYaw() + 90.0F;
				double p = (double)(this.targetSpeed * MathHelper.cos(o * (float) (Math.PI / 180.0))) * Math.abs(d / i);
				double q = (double)(this.targetSpeed * MathHelper.sin(o * (float) (Math.PI / 180.0))) * Math.abs(f / i);
				double r = (double)(this.targetSpeed * MathHelper.sin(n * (float) (Math.PI / 180.0))) * Math.abs(e / i);
				Vec3d vec3d = PhantomEntity.this.getVelocity();
				PhantomEntity.this.setVelocity(vec3d.add(new Vec3d(p, r, q).subtract(vec3d).multiply(0.2)));
			}
		}
	}

	static enum PhantomMovementType {
		CIRCLE,
		SWOOP;
	}

	class StartAttackGoal extends Goal {
		private int cooldown;

		@Override
		public boolean canStart() {
			LivingEntity livingEntity = PhantomEntity.this.getTarget();
			return livingEntity != null ? PhantomEntity.this.isTarget(livingEntity, TargetPredicate.DEFAULT) : false;
		}

		@Override
		public void start() {
			this.cooldown = this.getTickCount(10);
			PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
			this.startSwoop();
		}

		@Override
		public void stop() {
			PhantomEntity.this.circlingCenter = PhantomEntity.this.getWorld()
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
					this.cooldown = this.getTickCount((8 + PhantomEntity.this.random.nextInt(4)) * 20);
					PhantomEntity.this.playSound(SoundEvents.ENTITY_PHANTOM_SWOOP, 10.0F, 0.95F + PhantomEntity.this.random.nextFloat() * 0.1F);
				}
			}
		}

		private void startSwoop() {
			PhantomEntity.this.circlingCenter = PhantomEntity.this.getTarget().getBlockPos().up(20 + PhantomEntity.this.random.nextInt(20));
			if (PhantomEntity.this.circlingCenter.getY() < PhantomEntity.this.getWorld().getSeaLevel()) {
				PhantomEntity.this.circlingCenter = new BlockPos(
					PhantomEntity.this.circlingCenter.getX(), PhantomEntity.this.getWorld().getSeaLevel() + 1, PhantomEntity.this.circlingCenter.getZ()
				);
			}
		}
	}

	class SwoopMovementGoal extends PhantomEntity.MovementGoal {
		private static final int CAT_CHECK_INTERVAL = 20;
		private boolean catsNearby;
		private int nextCatCheckAge;

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
			} else {
				if (livingEntity instanceof PlayerEntity playerEntity && (livingEntity.isSpectator() || playerEntity.isCreative())) {
					return false;
				}

				if (!this.canStart()) {
					return false;
				} else {
					if (PhantomEntity.this.age > this.nextCatCheckAge) {
						this.nextCatCheckAge = PhantomEntity.this.age + 20;
						List<CatEntity> list = PhantomEntity.this.getWorld()
							.getEntitiesByClass(CatEntity.class, PhantomEntity.this.getBoundingBox().expand(16.0), EntityPredicates.VALID_ENTITY);

						for (CatEntity catEntity : list) {
							catEntity.hiss();
						}

						this.catsNearby = !list.isEmpty();
					}

					return !this.catsNearby;
				}
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
			if (livingEntity != null) {
				PhantomEntity.this.targetPosition = new Vec3d(livingEntity.getX(), livingEntity.getBodyY(0.5), livingEntity.getZ());
				if (PhantomEntity.this.getBoundingBox().expand(0.2F).intersects(livingEntity.getBoundingBox())) {
					PhantomEntity.this.tryAttack(livingEntity);
					PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
					if (!PhantomEntity.this.isSilent()) {
						PhantomEntity.this.getWorld().syncWorldEvent(WorldEvents.PHANTOM_BITES, PhantomEntity.this.getBlockPos(), 0);
					}
				} else if (PhantomEntity.this.horizontalCollision || PhantomEntity.this.hurtTime > 0) {
					PhantomEntity.this.movementType = PhantomEntity.PhantomMovementType.CIRCLE;
				}
			}
		}
	}
}
