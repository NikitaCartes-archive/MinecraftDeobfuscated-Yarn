package net.minecraft.entity.vehicle;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.IntFunction;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.VariantHolder;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.mob.WaterCreatureEntity;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.c2s.play.BoatPaddleStateC2SPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.StringIdentifiable;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.function.ValueLists;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockLocating;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;

public class BoatEntity extends Entity implements VariantHolder<BoatEntity.Type> {
	private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> BOAT_TYPE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> LEFT_PADDLE_MOVING = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> RIGHT_PADDLE_MOVING = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> BUBBLE_WOBBLE_TICKS = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	public static final int field_30697 = 0;
	public static final int field_30698 = 1;
	private static final int field_30695 = 60;
	private static final float NEXT_PADDLE_PHASE = (float) (Math.PI / 8);
	/**
	 * A boat will emit a sound event every time a paddle is near this rotation.
	 */
	public static final double EMIT_SOUND_EVENT_PADDLE_ROTATION = (float) (Math.PI / 4);
	public static final int field_30700 = 60;
	private final float[] paddlePhases = new float[2];
	private float velocityDecay;
	private float ticksUnderwater;
	private float yawVelocity;
	private int field_7708;
	private double x;
	private double y;
	private double z;
	private double boatYaw;
	private double boatPitch;
	private boolean pressingLeft;
	private boolean pressingRight;
	private boolean pressingForward;
	private boolean pressingBack;
	private double waterLevel;
	private float nearbySlipperiness;
	private BoatEntity.Location location;
	private BoatEntity.Location lastLocation;
	private double fallVelocity;
	private boolean onBubbleColumnSurface;
	private boolean bubbleColumnIsDrag;
	private float bubbleWobbleStrength;
	private float bubbleWobble;
	private float lastBubbleWobble;

	public BoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
		super(entityType, world);
		this.intersectionChecked = true;
	}

	public BoatEntity(World world, double x, double y, double z) {
		this(EntityType.BOAT, world);
		this.setPosition(x, y, z);
		this.prevX = x;
		this.prevY = y;
		this.prevZ = z;
	}

	@Override
	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height;
	}

	@Override
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.EVENTS;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);
		this.dataTracker.startTracking(BOAT_TYPE, BoatEntity.Type.OAK.ordinal());
		this.dataTracker.startTracking(LEFT_PADDLE_MOVING, false);
		this.dataTracker.startTracking(RIGHT_PADDLE_MOVING, false);
		this.dataTracker.startTracking(BUBBLE_WOBBLE_TICKS, 0);
	}

	@Override
	public boolean collidesWith(Entity other) {
		return canCollide(this, other);
	}

	public static boolean canCollide(Entity entity, Entity other) {
		return (other.isCollidable() || other.isPushable()) && !entity.isConnectedThroughVehicle(other);
	}

	@Override
	public boolean isCollidable() {
		return true;
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	protected Vec3d positionInPortal(Direction.Axis portalAxis, BlockLocating.Rectangle portalRect) {
		return LivingEntity.positionInPortal(super.positionInPortal(portalAxis, portalRect));
	}

	@Override
	public double getMountedHeightOffset() {
		return this.getVariant() == BoatEntity.Type.BAMBOO ? 0.25 : -0.1;
	}

	@Override
	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else if (!this.world.isClient && !this.isRemoved()) {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setDamageWobbleTicks(10);
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0F);
			this.scheduleVelocityUpdate();
			this.emitGameEvent(GameEvent.ENTITY_DAMAGE, source.getAttacker());
			boolean bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
			if (bl || this.getDamageWobbleStrength() > 40.0F) {
				if (!bl && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
					this.dropItems(source);
				}

				this.discard();
			}

			return true;
		} else {
			return true;
		}
	}

	protected void dropItems(DamageSource source) {
		this.dropItem(this.asItem());
	}

	@Override
	public void onBubbleColumnSurfaceCollision(boolean drag) {
		if (!this.world.isClient) {
			this.onBubbleColumnSurface = true;
			this.bubbleColumnIsDrag = drag;
			if (this.getBubbleWobbleTicks() == 0) {
				this.setBubbleWobbleTicks(60);
			}
		}

		this.world
			.addParticle(
				ParticleTypes.SPLASH, this.getX() + (double)this.random.nextFloat(), this.getY() + 0.7, this.getZ() + (double)this.random.nextFloat(), 0.0, 0.0, 0.0
			);
		if (this.random.nextInt(20) == 0) {
			this.world
				.playSound(this.getX(), this.getY(), this.getZ(), this.getSplashSound(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
			this.emitGameEvent(GameEvent.SPLASH, this.getControllingPassenger());
		}
	}

	@Override
	public void pushAwayFrom(Entity entity) {
		if (entity instanceof BoatEntity) {
			if (entity.getBoundingBox().minY < this.getBoundingBox().maxY) {
				super.pushAwayFrom(entity);
			}
		} else if (entity.getBoundingBox().minY <= this.getBoundingBox().minY) {
			super.pushAwayFrom(entity);
		}
	}

	public Item asItem() {
		return switch (this.getVariant()) {
			case SPRUCE -> Items.SPRUCE_BOAT;
			case BIRCH -> Items.BIRCH_BOAT;
			case JUNGLE -> Items.JUNGLE_BOAT;
			case ACACIA -> Items.ACACIA_BOAT;
			case CHERRY -> Items.CHERRY_BOAT;
			case DARK_OAK -> Items.DARK_OAK_BOAT;
			case MANGROVE -> Items.MANGROVE_BOAT;
			case BAMBOO -> Items.BAMBOO_RAFT;
			default -> Items.OAK_BOAT;
		};
	}

	@Override
	public void animateDamage(float yaw) {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setDamageWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0F);
	}

	@Override
	public boolean canHit() {
		return !this.isRemoved();
	}

	@Override
	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.boatYaw = (double)yaw;
		this.boatPitch = (double)pitch;
		this.field_7708 = 10;
	}

	@Override
	public Direction getMovementDirection() {
		return this.getHorizontalFacing().rotateYClockwise();
	}

	@Override
	public void tick() {
		this.lastLocation = this.location;
		this.location = this.checkLocation();
		if (this.location != BoatEntity.Location.UNDER_WATER && this.location != BoatEntity.Location.UNDER_FLOWING_WATER) {
			this.ticksUnderwater = 0.0F;
		} else {
			this.ticksUnderwater++;
		}

		if (!this.world.isClient && this.ticksUnderwater >= 60.0F) {
			this.removeAllPassengers();
		}

		if (this.getDamageWobbleTicks() > 0) {
			this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
		}

		if (this.getDamageWobbleStrength() > 0.0F) {
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0F);
		}

		super.tick();
		this.updatePositionAndRotation();
		if (this.isLogicalSideForUpdatingMovement()) {
			if (!(this.getFirstPassenger() instanceof PlayerEntity)) {
				this.setPaddleMovings(false, false);
			}

			this.updateVelocity();
			if (this.world.isClient) {
				this.updatePaddles();
				this.world.sendPacket(new BoatPaddleStateC2SPacket(this.isPaddleMoving(0), this.isPaddleMoving(1)));
			}

			this.move(MovementType.SELF, this.getVelocity());
		} else {
			this.setVelocity(Vec3d.ZERO);
		}

		this.handleBubbleColumn();

		for (int i = 0; i <= 1; i++) {
			if (this.isPaddleMoving(i)) {
				if (!this.isSilent()
					&& (double)(this.paddlePhases[i] % (float) (Math.PI * 2)) <= (float) (Math.PI / 4)
					&& (double)((this.paddlePhases[i] + (float) (Math.PI / 8)) % (float) (Math.PI * 2)) >= (float) (Math.PI / 4)) {
					SoundEvent soundEvent = this.getPaddleSoundEvent();
					if (soundEvent != null) {
						Vec3d vec3d = this.getRotationVec(1.0F);
						double d = i == 1 ? -vec3d.z : vec3d.z;
						double e = i == 1 ? vec3d.x : -vec3d.x;
						this.world
							.playSound(null, this.getX() + d, this.getY(), this.getZ() + e, soundEvent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
					}
				}

				this.paddlePhases[i] = this.paddlePhases[i] + (float) (Math.PI / 8);
			} else {
				this.paddlePhases[i] = 0.0F;
			}
		}

		this.checkBlockCollision();
		List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2F, -0.01F, 0.2F), EntityPredicates.canBePushedBy(this));
		if (!list.isEmpty()) {
			boolean bl = !this.world.isClient && !(this.getControllingPassenger() instanceof PlayerEntity);

			for (int j = 0; j < list.size(); j++) {
				Entity entity = (Entity)list.get(j);
				if (!entity.hasPassenger(this)) {
					if (bl
						&& this.getPassengerList().size() < this.getMaxPassengers()
						&& !entity.hasVehicle()
						&& this.isSmallerThanBoat(entity)
						&& entity instanceof LivingEntity
						&& !(entity instanceof WaterCreatureEntity)
						&& !(entity instanceof PlayerEntity)) {
						entity.startRiding(this);
					} else {
						this.pushAwayFrom(entity);
					}
				}
			}
		}
	}

	private void handleBubbleColumn() {
		if (this.world.isClient) {
			int i = this.getBubbleWobbleTicks();
			if (i > 0) {
				this.bubbleWobbleStrength += 0.05F;
			} else {
				this.bubbleWobbleStrength -= 0.1F;
			}

			this.bubbleWobbleStrength = MathHelper.clamp(this.bubbleWobbleStrength, 0.0F, 1.0F);
			this.lastBubbleWobble = this.bubbleWobble;
			this.bubbleWobble = 10.0F * (float)Math.sin((double)(0.5F * (float)this.world.getTime())) * this.bubbleWobbleStrength;
		} else {
			if (!this.onBubbleColumnSurface) {
				this.setBubbleWobbleTicks(0);
			}

			int i = this.getBubbleWobbleTicks();
			if (i > 0) {
				this.setBubbleWobbleTicks(--i);
				int j = 60 - i - 1;
				if (j > 0 && i == 0) {
					this.setBubbleWobbleTicks(0);
					Vec3d vec3d = this.getVelocity();
					if (this.bubbleColumnIsDrag) {
						this.setVelocity(vec3d.add(0.0, -0.7, 0.0));
						this.removeAllPassengers();
					} else {
						this.setVelocity(vec3d.x, this.hasPassenger(entity -> entity instanceof PlayerEntity) ? 2.7 : 0.6, vec3d.z);
					}
				}

				this.onBubbleColumnSurface = false;
			}
		}
	}

	@Nullable
	protected SoundEvent getPaddleSoundEvent() {
		switch (this.checkLocation()) {
			case IN_WATER:
			case UNDER_WATER:
			case UNDER_FLOWING_WATER:
				return SoundEvents.ENTITY_BOAT_PADDLE_WATER;
			case ON_LAND:
				return SoundEvents.ENTITY_BOAT_PADDLE_LAND;
			case IN_AIR:
			default:
				return null;
		}
	}

	private void updatePositionAndRotation() {
		if (this.isLogicalSideForUpdatingMovement()) {
			this.field_7708 = 0;
			this.updateTrackedPosition(this.getX(), this.getY(), this.getZ());
		}

		if (this.field_7708 > 0) {
			double d = this.getX() + (this.x - this.getX()) / (double)this.field_7708;
			double e = this.getY() + (this.y - this.getY()) / (double)this.field_7708;
			double f = this.getZ() + (this.z - this.getZ()) / (double)this.field_7708;
			double g = MathHelper.wrapDegrees(this.boatYaw - (double)this.getYaw());
			this.setYaw(this.getYaw() + (float)g / (float)this.field_7708);
			this.setPitch(this.getPitch() + (float)(this.boatPitch - (double)this.getPitch()) / (float)this.field_7708);
			this.field_7708--;
			this.setPosition(d, e, f);
			this.setRotation(this.getYaw(), this.getPitch());
		}
	}

	public void setPaddleMovings(boolean leftMoving, boolean rightMoving) {
		this.dataTracker.set(LEFT_PADDLE_MOVING, leftMoving);
		this.dataTracker.set(RIGHT_PADDLE_MOVING, rightMoving);
	}

	public float interpolatePaddlePhase(int paddle, float tickDelta) {
		return this.isPaddleMoving(paddle) ? MathHelper.clampedLerp(this.paddlePhases[paddle] - (float) (Math.PI / 8), this.paddlePhases[paddle], tickDelta) : 0.0F;
	}

	private BoatEntity.Location checkLocation() {
		BoatEntity.Location location = this.getUnderWaterLocation();
		if (location != null) {
			this.waterLevel = this.getBoundingBox().maxY;
			return location;
		} else if (this.checkBoatInWater()) {
			return BoatEntity.Location.IN_WATER;
		} else {
			float f = this.getNearbySlipperiness();
			if (f > 0.0F) {
				this.nearbySlipperiness = f;
				return BoatEntity.Location.ON_LAND;
			} else {
				return BoatEntity.Location.IN_AIR;
			}
		}
	}

	public float getWaterHeightBelow() {
		Box box = this.getBoundingBox();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.maxY);
		int l = MathHelper.ceil(box.maxY - this.fallVelocity);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		label39:
		for (int o = k; o < l; o++) {
			float f = 0.0F;

			for (int p = i; p < j; p++) {
				for (int q = m; q < n; q++) {
					mutable.set(p, o, q);
					FluidState fluidState = this.world.getFluidState(mutable);
					if (fluidState.isIn(FluidTags.WATER)) {
						f = Math.max(f, fluidState.getHeight(this.world, mutable));
					}

					if (f >= 1.0F) {
						continue label39;
					}
				}
			}

			if (f < 1.0F) {
				return (float)mutable.getY() + f;
			}
		}

		return (float)(l + 1);
	}

	public float getNearbySlipperiness() {
		Box box = this.getBoundingBox();
		Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
		int i = MathHelper.floor(box2.minX) - 1;
		int j = MathHelper.ceil(box2.maxX) + 1;
		int k = MathHelper.floor(box2.minY) - 1;
		int l = MathHelper.ceil(box2.maxY) + 1;
		int m = MathHelper.floor(box2.minZ) - 1;
		int n = MathHelper.ceil(box2.maxZ) + 1;
		VoxelShape voxelShape = VoxelShapes.cuboid(box2);
		float f = 0.0F;
		int o = 0;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int p = i; p < j; p++) {
			for (int q = m; q < n; q++) {
				int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
				if (r != 2) {
					for (int s = k; s < l; s++) {
						if (r <= 0 || s != k && s != l - 1) {
							mutable.set(p, s, q);
							BlockState blockState = this.world.getBlockState(mutable);
							if (!(blockState.getBlock() instanceof LilyPadBlock)
								&& VoxelShapes.matchesAnywhere(
									blockState.getCollisionShape(this.world, mutable).offset((double)p, (double)s, (double)q), voxelShape, BooleanBiFunction.AND
								)) {
								f += blockState.getBlock().getSlipperiness();
								o++;
							}
						}
					}
				}
			}
		}

		return f / (float)o;
	}

	private boolean checkBoatInWater() {
		Box box = this.getBoundingBox();
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.minY + 0.001);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		boolean bl = false;
		this.waterLevel = -Double.MAX_VALUE;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					mutable.set(o, p, q);
					FluidState fluidState = this.world.getFluidState(mutable);
					if (fluidState.isIn(FluidTags.WATER)) {
						float f = (float)p + fluidState.getHeight(this.world, mutable);
						this.waterLevel = Math.max((double)f, this.waterLevel);
						bl |= box.minY < (double)f;
					}
				}
			}
		}

		return bl;
	}

	@Nullable
	private BoatEntity.Location getUnderWaterLocation() {
		Box box = this.getBoundingBox();
		double d = box.maxY + 0.001;
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.maxY);
		int l = MathHelper.ceil(d);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		boolean bl = false;
		BlockPos.Mutable mutable = new BlockPos.Mutable();

		for (int o = i; o < j; o++) {
			for (int p = k; p < l; p++) {
				for (int q = m; q < n; q++) {
					mutable.set(o, p, q);
					FluidState fluidState = this.world.getFluidState(mutable);
					if (fluidState.isIn(FluidTags.WATER) && d < (double)((float)mutable.getY() + fluidState.getHeight(this.world, mutable))) {
						if (!fluidState.isStill()) {
							return BoatEntity.Location.UNDER_FLOWING_WATER;
						}

						bl = true;
					}
				}
			}
		}

		return bl ? BoatEntity.Location.UNDER_WATER : null;
	}

	private void updateVelocity() {
		double d = -0.04F;
		double e = this.hasNoGravity() ? 0.0 : -0.04F;
		double f = 0.0;
		this.velocityDecay = 0.05F;
		if (this.lastLocation == BoatEntity.Location.IN_AIR && this.location != BoatEntity.Location.IN_AIR && this.location != BoatEntity.Location.ON_LAND) {
			this.waterLevel = this.getBodyY(1.0);
			this.setPosition(this.getX(), (double)(this.getWaterHeightBelow() - this.getHeight()) + 0.101, this.getZ());
			this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
			this.fallVelocity = 0.0;
			this.location = BoatEntity.Location.IN_WATER;
		} else {
			if (this.location == BoatEntity.Location.IN_WATER) {
				f = (this.waterLevel - this.getY()) / (double)this.getHeight();
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.UNDER_FLOWING_WATER) {
				e = -7.0E-4;
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.UNDER_WATER) {
				f = 0.01F;
				this.velocityDecay = 0.45F;
			} else if (this.location == BoatEntity.Location.IN_AIR) {
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.ON_LAND) {
				this.velocityDecay = this.nearbySlipperiness;
				if (this.getControllingPassenger() instanceof PlayerEntity) {
					this.nearbySlipperiness /= 2.0F;
				}
			}

			Vec3d vec3d = this.getVelocity();
			this.setVelocity(vec3d.x * (double)this.velocityDecay, vec3d.y + e, vec3d.z * (double)this.velocityDecay);
			this.yawVelocity = this.yawVelocity * this.velocityDecay;
			if (f > 0.0) {
				Vec3d vec3d2 = this.getVelocity();
				this.setVelocity(vec3d2.x, (vec3d2.y + f * 0.06153846016296973) * 0.75, vec3d2.z);
			}
		}
	}

	private void updatePaddles() {
		if (this.hasPassengers()) {
			float f = 0.0F;
			if (this.pressingLeft) {
				this.yawVelocity--;
			}

			if (this.pressingRight) {
				this.yawVelocity++;
			}

			if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
				f += 0.005F;
			}

			this.setYaw(this.getYaw() + this.yawVelocity);
			if (this.pressingForward) {
				f += 0.04F;
			}

			if (this.pressingBack) {
				f -= 0.005F;
			}

			this.setVelocity(
				this.getVelocity()
					.add(
						(double)(MathHelper.sin(-this.getYaw() * (float) (Math.PI / 180.0)) * f), 0.0, (double)(MathHelper.cos(this.getYaw() * (float) (Math.PI / 180.0)) * f)
					)
			);
			this.setPaddleMovings(this.pressingRight && !this.pressingLeft || this.pressingForward, this.pressingLeft && !this.pressingRight || this.pressingForward);
		}
	}

	protected float getPassengerHorizontalOffset() {
		return 0.0F;
	}

	public boolean isSmallerThanBoat(Entity entity) {
		return entity.getWidth() < this.getWidth();
	}

	@Override
	public void updatePassengerPosition(Entity passenger) {
		if (this.hasPassenger(passenger)) {
			float f = this.getPassengerHorizontalOffset();
			float g = (float)((this.isRemoved() ? 0.01F : this.getMountedHeightOffset()) + passenger.getHeightOffset());
			if (this.getPassengerList().size() > 1) {
				int i = this.getPassengerList().indexOf(passenger);
				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}

				if (passenger instanceof AnimalEntity) {
					f += 0.2F;
				}
			}

			Vec3d vec3d = new Vec3d((double)f, 0.0, 0.0).rotateY(-this.getYaw() * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
			passenger.setPosition(this.getX() + vec3d.x, this.getY() + (double)g, this.getZ() + vec3d.z);
			passenger.setYaw(passenger.getYaw() + this.yawVelocity);
			passenger.setHeadYaw(passenger.getHeadYaw() + this.yawVelocity);
			this.copyEntityData(passenger);
			if (passenger instanceof AnimalEntity && this.getPassengerList().size() == this.getMaxPassengers()) {
				int j = passenger.getId() % 2 == 0 ? 90 : 270;
				passenger.setBodyYaw(((AnimalEntity)passenger).bodyYaw + (float)j);
				passenger.setHeadYaw(passenger.getHeadYaw() + (float)j);
			}
		}
	}

	@Override
	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		Vec3d vec3d = getPassengerDismountOffset((double)(this.getWidth() * MathHelper.SQUARE_ROOT_OF_TWO), (double)passenger.getWidth(), passenger.getYaw());
		double d = this.getX() + vec3d.x;
		double e = this.getZ() + vec3d.z;
		BlockPos blockPos = BlockPos.ofFloored(d, this.getBoundingBox().maxY, e);
		BlockPos blockPos2 = blockPos.down();
		if (!this.world.isWater(blockPos2)) {
			List<Vec3d> list = Lists.<Vec3d>newArrayList();
			double f = this.world.getDismountHeight(blockPos);
			if (Dismounting.canDismountInBlock(f)) {
				list.add(new Vec3d(d, (double)blockPos.getY() + f, e));
			}

			double g = this.world.getDismountHeight(blockPos2);
			if (Dismounting.canDismountInBlock(g)) {
				list.add(new Vec3d(d, (double)blockPos2.getY() + g, e));
			}

			for (EntityPose entityPose : passenger.getPoses()) {
				for (Vec3d vec3d2 : list) {
					if (Dismounting.canPlaceEntityAt(this.world, vec3d2, passenger, entityPose)) {
						passenger.setPose(entityPose);
						return vec3d2;
					}
				}
			}
		}

		return super.updatePassengerForDismount(passenger);
	}

	protected void copyEntityData(Entity entity) {
		entity.setBodyYaw(this.getYaw());
		float f = MathHelper.wrapDegrees(entity.getYaw() - this.getYaw());
		float g = MathHelper.clamp(f, -105.0F, 105.0F);
		entity.prevYaw += g - f;
		entity.setYaw(entity.getYaw() + g - f);
		entity.setHeadYaw(entity.getYaw());
	}

	@Override
	public void onPassengerLookAround(Entity passenger) {
		this.copyEntityData(passenger);
	}

	@Override
	protected void writeCustomDataToNbt(NbtCompound nbt) {
		nbt.putString("Type", this.getVariant().asString());
	}

	@Override
	protected void readCustomDataFromNbt(NbtCompound nbt) {
		if (nbt.contains("Type", NbtElement.STRING_TYPE)) {
			this.setVariant(BoatEntity.Type.getType(nbt.getString("Type")));
		}
	}

	@Override
	public ActionResult interact(PlayerEntity player, Hand hand) {
		if (player.shouldCancelInteraction()) {
			return ActionResult.PASS;
		} else if (this.ticksUnderwater < 60.0F) {
			if (!this.world.isClient) {
				return player.startRiding(this) ? ActionResult.CONSUME : ActionResult.PASS;
			} else {
				return ActionResult.SUCCESS;
			}
		} else {
			return ActionResult.PASS;
		}
	}

	@Override
	protected void fall(double heightDifference, boolean onGround, BlockState state, BlockPos landedPosition) {
		this.fallVelocity = this.getVelocity().y;
		if (!this.hasVehicle()) {
			if (onGround) {
				if (this.fallDistance > 3.0F) {
					if (this.location != BoatEntity.Location.ON_LAND) {
						this.onLanding();
						return;
					}

					this.handleFallDamage(this.fallDistance, 1.0F, this.getDamageSources().fall());
					if (!this.world.isClient && !this.isRemoved()) {
						this.kill();
						if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
							for (int i = 0; i < 3; i++) {
								this.dropItem(this.getVariant().getBaseBlock());
							}

							for (int i = 0; i < 2; i++) {
								this.dropItem(Items.STICK);
							}
						}
					}
				}

				this.onLanding();
			} else if (!this.world.getFluidState(this.getBlockPos().down()).isIn(FluidTags.WATER) && heightDifference < 0.0) {
				this.fallDistance -= (float)heightDifference;
			}
		}
	}

	public boolean isPaddleMoving(int paddle) {
		return this.dataTracker.get(paddle == 0 ? LEFT_PADDLE_MOVING : RIGHT_PADDLE_MOVING) && this.getControllingPassenger() != null;
	}

	public void setDamageWobbleStrength(float wobbleStrength) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, wobbleStrength);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public void setDamageWobbleTicks(int wobbleTicks) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
	}

	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}

	private void setBubbleWobbleTicks(int wobbleTicks) {
		this.dataTracker.set(BUBBLE_WOBBLE_TICKS, wobbleTicks);
	}

	private int getBubbleWobbleTicks() {
		return this.dataTracker.get(BUBBLE_WOBBLE_TICKS);
	}

	public float interpolateBubbleWobble(float tickDelta) {
		return MathHelper.lerp(tickDelta, this.lastBubbleWobble, this.bubbleWobble);
	}

	public void setDamageWobbleSide(int side) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, side);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	public void setVariant(BoatEntity.Type type) {
		this.dataTracker.set(BOAT_TYPE, type.ordinal());
	}

	public BoatEntity.Type getVariant() {
		return BoatEntity.Type.getType(this.dataTracker.get(BOAT_TYPE));
	}

	@Override
	protected boolean canAddPassenger(Entity passenger) {
		return this.getPassengerList().size() < this.getMaxPassengers() && !this.isSubmergedIn(FluidTags.WATER);
	}

	protected int getMaxPassengers() {
		return 2;
	}

	@Nullable
	@Override
	public LivingEntity getControllingPassenger() {
		return this.getFirstPassenger() instanceof LivingEntity livingEntity ? livingEntity : null;
	}

	public void setInputs(boolean pressingLeft, boolean pressingRight, boolean pressingForward, boolean pressingBack) {
		this.pressingLeft = pressingLeft;
		this.pressingRight = pressingRight;
		this.pressingForward = pressingForward;
		this.pressingBack = pressingBack;
	}

	@Override
	public boolean isSubmergedInWater() {
		return this.location == BoatEntity.Location.UNDER_WATER || this.location == BoatEntity.Location.UNDER_FLOWING_WATER;
	}

	@Override
	public ItemStack getPickBlockStack() {
		return new ItemStack(this.asItem());
	}

	public static enum Location {
		IN_WATER,
		UNDER_WATER,
		UNDER_FLOWING_WATER,
		ON_LAND,
		IN_AIR;
	}

	public static enum Type implements StringIdentifiable {
		OAK(Blocks.OAK_PLANKS, "oak"),
		SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
		BIRCH(Blocks.BIRCH_PLANKS, "birch"),
		JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
		ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
		CHERRY(Blocks.CHERRY_PLANKS, "cherry"),
		DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak"),
		MANGROVE(Blocks.MANGROVE_PLANKS, "mangrove"),
		BAMBOO(Blocks.BAMBOO_PLANKS, "bamboo");

		private final String name;
		private final Block baseBlock;
		public static final StringIdentifiable.Codec<BoatEntity.Type> CODEC = StringIdentifiable.createCodec(BoatEntity.Type::values);
		private static final IntFunction<BoatEntity.Type> BY_ID = ValueLists.createIdToValueFunction(Enum::ordinal, values(), ValueLists.OutOfBoundsHandling.ZERO);

		private Type(Block baseBlock, String name) {
			this.name = name;
			this.baseBlock = baseBlock;
		}

		@Override
		public String asString() {
			return this.name;
		}

		public String getName() {
			return this.name;
		}

		public Block getBaseBlock() {
			return this.baseBlock;
		}

		public String toString() {
			return this.name;
		}

		public static BoatEntity.Type getType(int type) {
			return (BoatEntity.Type)BY_ID.apply(type);
		}

		public static BoatEntity.Type getType(String name) {
			return (BoatEntity.Type)CODEC.byId(name, OAK);
		}
	}
}
