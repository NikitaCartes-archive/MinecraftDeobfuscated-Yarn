package net.minecraft.entity.vehicle;

import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.LilyPadBlock;
import net.minecraft.client.network.packet.EntitySpawnS2CPacket;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.WaterCreatureEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.ProjectileDamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.AnimalEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.server.network.packet.BoatPaddleStateC2SPacket;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.World;

public class BoatEntity extends Entity {
	private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.FLOAT);
	private static final TrackedData<Integer> BOAT_TYPE = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Boolean> LEFT_PADDLE_MOVING = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> RIGHT_PADDLE_MOVING = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Integer> BUBBLE_WOBBLE_TICKS = DataTracker.registerData(BoatEntity.class, TrackedDataHandlerRegistry.INTEGER);
	private final float[] paddlePhases = new float[2];
	private float velocityDecay;
	private float ticksUnderwater;
	private float yawVelocity;
	private int field_7708;
	private double field_7686;
	private double field_7700;
	private double field_7685;
	private double field_7699;
	private double field_7684;
	private boolean pressingLeft;
	private boolean pressingRight;
	private boolean pressingForward;
	private boolean pressingBack;
	private double waterLevel;
	private float field_7714;
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
		this.field_6033 = true;
	}

	public BoatEntity(World world, double d, double e, double f) {
		this(EntityType.field_6121, world);
		this.setPosition(d, e, f);
		this.setVelocity(Vec3d.ZERO);
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
	}

	@Override
	protected boolean canClimb() {
		return false;
	}

	@Override
	protected void initDataTracker() {
		this.dataTracker.startTracking(DAMAGE_WOBBLE_TICKS, 0);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_SIDE, 1);
		this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, 0.0F);
		this.dataTracker.startTracking(BOAT_TYPE, BoatEntity.Type.field_7727.ordinal());
		this.dataTracker.startTracking(LEFT_PADDLE_MOVING, false);
		this.dataTracker.startTracking(RIGHT_PADDLE_MOVING, false);
		this.dataTracker.startTracking(BUBBLE_WOBBLE_TICKS, 0);
	}

	@Nullable
	@Override
	public BoundingBox method_5708(Entity entity) {
		return entity.isPushable() ? entity.getBoundingBox() : null;
	}

	@Nullable
	@Override
	public BoundingBox getCollisionBox() {
		return this.getBoundingBox();
	}

	@Override
	public boolean isPushable() {
		return true;
	}

	@Override
	public double getMountedHeightOffset() {
		return -0.1;
	}

	@Override
	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else if (this.world.isClient || this.removed) {
			return true;
		} else if (damageSource instanceof ProjectileDamageSource && damageSource.getAttacker() != null && this.hasPassenger(damageSource.getAttacker())) {
			return false;
		} else {
			this.setDamageWobbleSide(-this.getDamageWobbleSide());
			this.setDamageWobbleTicks(10);
			this.setDamageWobbleStrength(this.getDamageWobbleStrength() + f * 10.0F);
			this.scheduleVelocityUpdate();
			boolean bl = damageSource.getAttacker() instanceof PlayerEntity && ((PlayerEntity)damageSource.getAttacker()).abilities.creativeMode;
			if (bl || this.getDamageWobbleStrength() > 40.0F) {
				if (!bl && this.world.getGameRules().getBoolean("doEntityDrops")) {
					this.dropItem(this.asItem());
				}

				this.remove();
			}

			return true;
		}
	}

	@Override
	public void onBubbleColumnSurfaceCollision(boolean bl) {
		if (!this.world.isClient) {
			this.onBubbleColumnSurface = true;
			this.bubbleColumnIsDrag = bl;
			if (this.getBubbleWobbleTicks() == 0) {
				this.setBubbleWobbleTicks(60);
			}
		}

		this.world
			.addParticle(ParticleTypes.field_11202, this.x + (double)this.random.nextFloat(), this.y + 0.7, this.z + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
		if (this.random.nextInt(20) == 0) {
			this.world.playSound(this.x, this.y, this.z, this.getSplashSound(), this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat(), false);
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
		switch (this.getBoatType()) {
			case field_7727:
			default:
				return Items.field_8533;
			case field_7728:
				return Items.field_8486;
			case field_7729:
				return Items.field_8442;
			case field_7730:
				return Items.field_8730;
			case field_7725:
				return Items.field_8094;
			case field_7723:
				return Items.field_8138;
		}
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void animateDamage() {
		this.setDamageWobbleSide(-this.getDamageWobbleSide());
		this.setDamageWobbleTicks(10);
		this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0F);
	}

	@Override
	public boolean collides() {
		return !this.removed;
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void setPositionAndRotations(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.field_7686 = d;
		this.field_7700 = e;
		this.field_7685 = f;
		this.field_7699 = (double)g;
		this.field_7684 = (double)h;
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
		if (this.location != BoatEntity.Location.field_7717 && this.location != BoatEntity.Location.field_7716) {
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

		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		super.tick();
		this.method_7555();
		if (this.isLogicalSideForUpdatingMovement()) {
			if (this.getPassengerList().isEmpty() || !(this.getPassengerList().get(0) instanceof PlayerEntity)) {
				this.setPaddleMovings(false, false);
			}

			this.updateVelocity();
			if (this.world.isClient) {
				this.updatePaddles();
				this.world.sendPacket(new BoatPaddleStateC2SPacket(this.isPaddleMoving(0), this.isPaddleMoving(1)));
			}

			this.move(MovementType.field_6308, this.getVelocity());
		} else {
			this.setVelocity(Vec3d.ZERO);
		}

		this.handleBubbleColumn();

		for (int i = 0; i <= 1; i++) {
			if (this.isPaddleMoving(i)) {
				if (!this.isSilent()
					&& (double)(this.paddlePhases[i] % (float) (Math.PI * 2)) <= (float) (Math.PI / 4)
					&& ((double)this.paddlePhases[i] + (float) (Math.PI / 8)) % (float) (Math.PI * 2) >= (float) (Math.PI / 4)) {
					SoundEvent soundEvent = this.getPaddleSoundEvent();
					if (soundEvent != null) {
						Vec3d vec3d = this.getRotationVec(1.0F);
						double d = i == 1 ? -vec3d.z : vec3d.z;
						double e = i == 1 ? vec3d.x : -vec3d.x;
						this.world.playSound(null, this.x + d, this.y, this.z + e, soundEvent, this.getSoundCategory(), 1.0F, 0.8F + 0.4F * this.random.nextFloat());
					}
				}

				this.paddlePhases[i] = (float)((double)this.paddlePhases[i] + (float) (Math.PI / 8));
			} else {
				this.paddlePhases[i] = 0.0F;
			}
		}

		this.checkBlockCollision();
		List<Entity> list = this.world.getEntities(this, this.getBoundingBox().expand(0.2F, -0.01F, 0.2F), EntityPredicates.canBePushedBy(this));
		if (!list.isEmpty()) {
			boolean bl = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);

			for (int j = 0; j < list.size(); j++) {
				Entity entity = (Entity)list.get(j);
				if (!entity.hasPassenger(this)) {
					if (bl
						&& this.getPassengerList().size() < 2
						&& !entity.hasVehicle()
						&& entity.getWidth() < this.getWidth()
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
						this.setVelocity(vec3d.x, this.hasPassengerType(PlayerEntity.class) ? 2.7 : 0.6, vec3d.z);
					}
				}

				this.onBubbleColumnSurface = false;
			}
		}
	}

	@Nullable
	protected SoundEvent getPaddleSoundEvent() {
		switch (this.checkLocation()) {
			case field_7718:
			case field_7717:
			case field_7716:
				return SoundEvents.field_15171;
			case field_7719:
				return SoundEvents.field_14886;
			case field_7720:
			default:
				return null;
		}
	}

	private void method_7555() {
		if (this.field_7708 > 0 && !this.isLogicalSideForUpdatingMovement()) {
			double d = this.x + (this.field_7686 - this.x) / (double)this.field_7708;
			double e = this.y + (this.field_7700 - this.y) / (double)this.field_7708;
			double f = this.z + (this.field_7685 - this.z) / (double)this.field_7708;
			double g = MathHelper.wrapDegrees(this.field_7699 - (double)this.yaw);
			this.yaw = (float)((double)this.yaw + g / (double)this.field_7708);
			this.pitch = (float)((double)this.pitch + (this.field_7684 - (double)this.pitch) / (double)this.field_7708);
			this.field_7708--;
			this.setPosition(d, e, f);
			this.setRotation(this.yaw, this.pitch);
		}
	}

	public void setPaddleMovings(boolean bl, boolean bl2) {
		this.dataTracker.set(LEFT_PADDLE_MOVING, bl);
		this.dataTracker.set(RIGHT_PADDLE_MOVING, bl2);
	}

	@Environment(EnvType.CLIENT)
	public float interpolatePaddlePhase(int i, float f) {
		return this.isPaddleMoving(i)
			? (float)MathHelper.clampedLerp((double)this.paddlePhases[i] - (float) (Math.PI / 8), (double)this.paddlePhases[i], (double)f)
			: 0.0F;
	}

	private BoatEntity.Location checkLocation() {
		BoatEntity.Location location = this.getUnderWaterLocation();
		if (location != null) {
			this.waterLevel = this.getBoundingBox().maxY;
			return location;
		} else if (this.checKBoatInWater()) {
			return BoatEntity.Location.field_7718;
		} else {
			float f = this.method_7548();
			if (f > 0.0F) {
				this.field_7714 = f;
				return BoatEntity.Location.field_7719;
			} else {
				return BoatEntity.Location.field_7720;
			}
		}
	}

	public float method_7544() {
		BoundingBox boundingBox = this.getBoundingBox();
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.maxY);
		int l = MathHelper.ceil(boundingBox.maxY - this.fallVelocity);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			label136:
			for (int o = k; o < l; o++) {
				float f = 0.0F;
				int p = i;

				while (true) {
					if (p < j) {
						for (int q = m; q < n; q++) {
							pooledMutable.method_10113(p, o, q);
							FluidState fluidState = this.world.getFluidState(pooledMutable);
							if (fluidState.matches(FluidTags.field_15517)) {
								f = Math.max(f, fluidState.getHeight(this.world, pooledMutable));
							}

							if (f >= 1.0F) {
								continue label136;
							}
						}

						p++;
					} else {
						if (f < 1.0F) {
							return (float)pooledMutable.getY() + f;
						}
						break;
					}
				}
			}

			return (float)(l + 1);
		}
	}

	public float method_7548() {
		BoundingBox boundingBox = this.getBoundingBox();
		BoundingBox boundingBox2 = new BoundingBox(boundingBox.minX, boundingBox.minY - 0.001, boundingBox.minZ, boundingBox.maxX, boundingBox.minY, boundingBox.maxZ);
		int i = MathHelper.floor(boundingBox2.minX) - 1;
		int j = MathHelper.ceil(boundingBox2.maxX) + 1;
		int k = MathHelper.floor(boundingBox2.minY) - 1;
		int l = MathHelper.ceil(boundingBox2.maxY) + 1;
		int m = MathHelper.floor(boundingBox2.minZ) - 1;
		int n = MathHelper.ceil(boundingBox2.maxZ) + 1;
		VoxelShape voxelShape = VoxelShapes.cuboid(boundingBox2);
		float f = 0.0F;
		int o = 0;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int p = i; p < j; p++) {
				for (int q = m; q < n; q++) {
					int r = (p != i && p != j - 1 ? 0 : 1) + (q != m && q != n - 1 ? 0 : 1);
					if (r != 2) {
						for (int s = k; s < l; s++) {
							if (r <= 0 || s != k && s != l - 1) {
								pooledMutable.method_10113(p, s, q);
								BlockState blockState = this.world.getBlockState(pooledMutable);
								if (!(blockState.getBlock() instanceof LilyPadBlock)
									&& VoxelShapes.matchesAnywhere(
										blockState.getCollisionShape(this.world, pooledMutable).offset((double)p, (double)s, (double)q), voxelShape, BooleanBiFunction.AND
									)) {
									f += blockState.getBlock().getSlipperiness();
									o++;
								}
							}
						}
					}
				}
			}
		}

		return f / (float)o;
	}

	private boolean checKBoatInWater() {
		BoundingBox boundingBox = this.getBoundingBox();
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.minY);
		int l = MathHelper.ceil(boundingBox.minY + 0.001);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		boolean bl = false;
		this.waterLevel = Double.MIN_VALUE;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						pooledMutable.method_10113(o, p, q);
						FluidState fluidState = this.world.getFluidState(pooledMutable);
						if (fluidState.matches(FluidTags.field_15517)) {
							float f = (float)p + fluidState.getHeight(this.world, pooledMutable);
							this.waterLevel = Math.max((double)f, this.waterLevel);
							bl |= boundingBox.minY < (double)f;
						}
					}
				}
			}
		}

		return bl;
	}

	@Nullable
	private BoatEntity.Location getUnderWaterLocation() {
		BoundingBox boundingBox = this.getBoundingBox();
		double d = boundingBox.maxY + 0.001;
		int i = MathHelper.floor(boundingBox.minX);
		int j = MathHelper.ceil(boundingBox.maxX);
		int k = MathHelper.floor(boundingBox.maxY);
		int l = MathHelper.ceil(d);
		int m = MathHelper.floor(boundingBox.minZ);
		int n = MathHelper.ceil(boundingBox.maxZ);
		boolean bl = false;

		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
			for (int o = i; o < j; o++) {
				for (int p = k; p < l; p++) {
					for (int q = m; q < n; q++) {
						pooledMutable.method_10113(o, p, q);
						FluidState fluidState = this.world.getFluidState(pooledMutable);
						if (fluidState.matches(FluidTags.field_15517) && d < (double)((float)pooledMutable.getY() + fluidState.getHeight(this.world, pooledMutable))) {
							if (!fluidState.isStill()) {
								return BoatEntity.Location.field_7716;
							}

							bl = true;
						}
					}
				}
			}
		}

		return bl ? BoatEntity.Location.field_7717 : null;
	}

	private void updateVelocity() {
		double d = -0.04F;
		double e = this.hasNoGravity() ? 0.0 : -0.04F;
		double f = 0.0;
		this.velocityDecay = 0.05F;
		if (this.lastLocation == BoatEntity.Location.field_7720 && this.location != BoatEntity.Location.field_7720 && this.location != BoatEntity.Location.field_7719
			)
		 {
			this.waterLevel = this.getBoundingBox().minY + (double)this.getHeight();
			this.setPosition(this.x, (double)(this.method_7544() - this.getHeight()) + 0.101, this.z);
			this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
			this.fallVelocity = 0.0;
			this.location = BoatEntity.Location.field_7718;
		} else {
			if (this.location == BoatEntity.Location.field_7718) {
				f = (this.waterLevel - this.getBoundingBox().minY) / (double)this.getHeight();
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.field_7716) {
				e = -7.0E-4;
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.field_7717) {
				f = 0.01F;
				this.velocityDecay = 0.45F;
			} else if (this.location == BoatEntity.Location.field_7720) {
				this.velocityDecay = 0.9F;
			} else if (this.location == BoatEntity.Location.field_7719) {
				this.velocityDecay = this.field_7714;
				if (this.getPrimaryPassenger() instanceof PlayerEntity) {
					this.field_7714 /= 2.0F;
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

			this.yaw = this.yaw + this.yawVelocity;
			if (this.pressingForward) {
				f += 0.04F;
			}

			if (this.pressingBack) {
				f -= 0.005F;
			}

			this.setVelocity(
				this.getVelocity()
					.add((double)(MathHelper.sin(-this.yaw * (float) (Math.PI / 180.0)) * f), 0.0, (double)(MathHelper.cos(this.yaw * (float) (Math.PI / 180.0)) * f))
			);
			this.setPaddleMovings(this.pressingRight && !this.pressingLeft || this.pressingForward, this.pressingLeft && !this.pressingRight || this.pressingForward);
		}
	}

	@Override
	public void updatePassengerPosition(Entity entity) {
		if (this.hasPassenger(entity)) {
			float f = 0.0F;
			float g = (float)((this.removed ? 0.01F : this.getMountedHeightOffset()) + entity.getHeightOffset());
			if (this.getPassengerList().size() > 1) {
				int i = this.getPassengerList().indexOf(entity);
				if (i == 0) {
					f = 0.2F;
				} else {
					f = -0.6F;
				}

				if (entity instanceof AnimalEntity) {
					f = (float)((double)f + 0.2);
				}
			}

			Vec3d vec3d = new Vec3d((double)f, 0.0, 0.0).rotateY(-this.yaw * (float) (Math.PI / 180.0) - (float) (Math.PI / 2));
			entity.setPosition(this.x + vec3d.x, this.y + (double)g, this.z + vec3d.z);
			entity.yaw = entity.yaw + this.yawVelocity;
			entity.setHeadYaw(entity.getHeadYaw() + this.yawVelocity);
			this.copyEntityData(entity);
			if (entity instanceof AnimalEntity && this.getPassengerList().size() > 1) {
				int j = entity.getEntityId() % 2 == 0 ? 90 : 270;
				entity.setYaw(((AnimalEntity)entity).field_6283 + (float)j);
				entity.setHeadYaw(entity.getHeadYaw() + (float)j);
			}
		}
	}

	protected void copyEntityData(Entity entity) {
		entity.setYaw(this.yaw);
		float f = MathHelper.wrapDegrees(entity.yaw - this.yaw);
		float g = MathHelper.clamp(f, -105.0F, 105.0F);
		entity.prevYaw += g - f;
		entity.yaw += g - f;
		entity.setHeadYaw(entity.yaw);
	}

	@Environment(EnvType.CLIENT)
	@Override
	public void onPassengerLookAround(Entity entity) {
		this.copyEntityData(entity);
	}

	@Override
	protected void writeCustomDataToTag(CompoundTag compoundTag) {
		compoundTag.putString("Type", this.getBoatType().getName());
	}

	@Override
	protected void readCustomDataFromTag(CompoundTag compoundTag) {
		if (compoundTag.containsKey("Type", 8)) {
			this.setBoatType(BoatEntity.Type.getType(compoundTag.getString("Type")));
		}
	}

	@Override
	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		if (playerEntity.isSneaking()) {
			return false;
		} else {
			if (!this.world.isClient && this.ticksUnderwater < 60.0F) {
				playerEntity.startRiding(this);
			}

			return true;
		}
	}

	@Override
	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		this.fallVelocity = this.getVelocity().y;
		if (!this.hasVehicle()) {
			if (bl) {
				if (this.fallDistance > 3.0F) {
					if (this.location != BoatEntity.Location.field_7719) {
						this.fallDistance = 0.0F;
						return;
					}

					this.handleFallDamage(this.fallDistance, 1.0F);
					if (!this.world.isClient && !this.removed) {
						this.remove();
						if (this.world.getGameRules().getBoolean("doEntityDrops")) {
							for (int i = 0; i < 3; i++) {
								this.dropItem(this.getBoatType().getBaseBlock());
							}

							for (int i = 0; i < 2; i++) {
								this.dropItem(Items.field_8600);
							}
						}
					}
				}

				this.fallDistance = 0.0F;
			} else if (!this.world.getFluidState(new BlockPos(this).down()).matches(FluidTags.field_15517) && d < 0.0) {
				this.fallDistance = (float)((double)this.fallDistance - d);
			}
		}
	}

	public boolean isPaddleMoving(int i) {
		return this.dataTracker.get(i == 0 ? LEFT_PADDLE_MOVING : RIGHT_PADDLE_MOVING) && this.getPrimaryPassenger() != null;
	}

	public void setDamageWobbleStrength(float f) {
		this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, f);
	}

	public float getDamageWobbleStrength() {
		return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH);
	}

	public void setDamageWobbleTicks(int i) {
		this.dataTracker.set(DAMAGE_WOBBLE_TICKS, i);
	}

	public int getDamageWobbleTicks() {
		return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
	}

	private void setBubbleWobbleTicks(int i) {
		this.dataTracker.set(BUBBLE_WOBBLE_TICKS, i);
	}

	private int getBubbleWobbleTicks() {
		return this.dataTracker.get(BUBBLE_WOBBLE_TICKS);
	}

	@Environment(EnvType.CLIENT)
	public float interpolateBubbleWobble(float f) {
		return MathHelper.lerp(f, this.lastBubbleWobble, this.bubbleWobble);
	}

	public void setDamageWobbleSide(int i) {
		this.dataTracker.set(DAMAGE_WOBBLE_SIDE, i);
	}

	public int getDamageWobbleSide() {
		return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
	}

	public void setBoatType(BoatEntity.Type type) {
		this.dataTracker.set(BOAT_TYPE, type.ordinal());
	}

	public BoatEntity.Type getBoatType() {
		return BoatEntity.Type.getType(this.dataTracker.get(BOAT_TYPE));
	}

	@Override
	protected boolean canAddPassenger(Entity entity) {
		return this.getPassengerList().size() < 2 && !this.isInFluid(FluidTags.field_15517);
	}

	@Nullable
	@Override
	public Entity getPrimaryPassenger() {
		List<Entity> list = this.getPassengerList();
		return list.isEmpty() ? null : (Entity)list.get(0);
	}

	@Environment(EnvType.CLIENT)
	public void setInputs(boolean bl, boolean bl2, boolean bl3, boolean bl4) {
		this.pressingLeft = bl;
		this.pressingRight = bl2;
		this.pressingForward = bl3;
		this.pressingBack = bl4;
	}

	@Override
	public Packet<?> createSpawnPacket() {
		return new EntitySpawnS2CPacket(this);
	}

	public static enum Location {
		field_7718,
		field_7717,
		field_7716,
		field_7719,
		field_7720;
	}

	public static enum Type {
		field_7727(Blocks.field_10161, "oak"),
		field_7728(Blocks.field_9975, "spruce"),
		field_7729(Blocks.field_10148, "birch"),
		field_7730(Blocks.field_10334, "jungle"),
		field_7725(Blocks.field_10218, "acacia"),
		field_7723(Blocks.field_10075, "dark_oak");

		private final String name;
		private final Block baseBlock;

		private Type(Block block, String string2) {
			this.name = string2;
			this.baseBlock = block;
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

		public static BoatEntity.Type getType(int i) {
			BoatEntity.Type[] types = values();
			if (i < 0 || i >= types.length) {
				i = 0;
			}

			return types[i];
		}

		public static BoatEntity.Type getType(String string) {
			BoatEntity.Type[] types = values();

			for (int i = 0; i < types.length; i++) {
				if (types[i].getName().equals(string)) {
					return types[i];
				}
			}

			return types[0];
		}
	}
}
