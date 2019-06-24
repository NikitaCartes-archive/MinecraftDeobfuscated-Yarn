package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.PortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.network.packet.EntityS2CPacket;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.DoubleTag;
import net.minecraft.nbt.FloatTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.Packet;
import net.minecraft.particle.BlockStateParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.scoreboard.AbstractTeam;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.command.CommandOutput;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.ReusableStream;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements Nameable, CommandOutput {
	protected static final Logger LOGGER = LogManager.getLogger();
	private static final AtomicInteger MAX_ENTITY_ID = new AtomicInteger();
	private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
	private static final Box NULL_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static double renderDistanceMultiplier = 1.0;
	private final EntityType<?> type;
	private int entityId = MAX_ENTITY_ID.incrementAndGet();
	public boolean inanimate;
	private final List<Entity> passengerList = Lists.<Entity>newArrayList();
	protected int ridingCooldown;
	private Entity vehicle;
	public boolean teleporting;
	public World world;
	public double prevX;
	public double prevY;
	public double prevZ;
	public double x;
	public double y;
	public double z;
	private Vec3d velocity = Vec3d.ZERO;
	public float yaw;
	public float pitch;
	public float prevYaw;
	public float prevPitch;
	private Box entityBounds = NULL_BOX;
	public boolean onGround;
	public boolean horizontalCollision;
	public boolean verticalCollision;
	public boolean collided;
	public boolean velocityModified;
	protected Vec3d movementMultiplier = Vec3d.ZERO;
	public boolean removed;
	public float prevHorizontalSpeed;
	public float horizontalSpeed;
	public float distanceWalked;
	public float fallDistance;
	private float nextStepDistance = 1.0F;
	private float aerialStepDelta = 1.0F;
	public double prevRenderX;
	public double prevRenderY;
	public double prevRenderZ;
	public float stepHeight;
	public boolean noClip;
	public float pushSpeedReduction;
	protected final Random random = new Random();
	public int age;
	private int fireTime = -this.getBurningDuration();
	protected boolean insideWater;
	protected double waterHeight;
	protected boolean inWater;
	protected boolean inLava;
	public int timeUntilRegen;
	protected boolean firstUpdate = true;
	protected final DataTracker dataTracker;
	protected static final TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
	private static final TrackedData<Integer> BREATH = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<Text>> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
	private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<EntityPose> POSE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
	public boolean updateNeeded;
	public int chunkX;
	public int chunkY;
	public int chunkZ;
	@Environment(EnvType.CLIENT)
	public long trackedX;
	@Environment(EnvType.CLIENT)
	public long trackedY;
	@Environment(EnvType.CLIENT)
	public long trackedZ;
	public boolean ignoreCameraFrustum;
	public boolean velocityDirty;
	public int portalCooldown;
	protected boolean inPortal;
	protected int portalTime;
	public DimensionType dimension;
	protected BlockPos lastPortalPosition;
	protected Vec3d lastPortalDirectionVector;
	protected Direction lastPortalDirection;
	private boolean invulnerable;
	protected UUID uuid = MathHelper.randomUuid(this.random);
	protected String uuidString = this.uuid.toString();
	protected boolean glowing;
	private final Set<String> scoreboardTags = Sets.<String>newHashSet();
	private boolean teleportRequested;
	private final double[] pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
	private long pistonMovementTick;
	private EntityDimensions dimensions;
	private float standingEyeHeight;

	public Entity(EntityType<?> entityType, World world) {
		this.type = entityType;
		this.world = world;
		this.dimensions = entityType.getDimensions();
		this.setPosition(0.0, 0.0, 0.0);
		if (world != null) {
			this.dimension = world.dimension.getType();
		}

		this.dataTracker = new DataTracker(this);
		this.dataTracker.startTracking(FLAGS, (byte)0);
		this.dataTracker.startTracking(BREATH, this.getMaxBreath());
		this.dataTracker.startTracking(NAME_VISIBLE, false);
		this.dataTracker.startTracking(CUSTOM_NAME, Optional.empty());
		this.dataTracker.startTracking(SILENT, false);
		this.dataTracker.startTracking(NO_GRAVITY, false);
		this.dataTracker.startTracking(POSE, EntityPose.STANDING);
		this.initDataTracker();
		this.standingEyeHeight = this.getEyeHeight(EntityPose.STANDING, this.dimensions);
	}

	public boolean isSpectator() {
		return false;
	}

	public final void detach() {
		if (this.hasPassengers()) {
			this.removeAllPassengers();
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}
	}

	@Environment(EnvType.CLIENT)
	public void updateTrackedPosition(double d, double e, double f) {
		this.trackedX = EntityS2CPacket.encodePacketCoordinate(d);
		this.trackedY = EntityS2CPacket.encodePacketCoordinate(e);
		this.trackedZ = EntityS2CPacket.encodePacketCoordinate(f);
	}

	public EntityType<?> getType() {
		return this.type;
	}

	public int getEntityId() {
		return this.entityId;
	}

	public void setEntityId(int i) {
		this.entityId = i;
	}

	public Set<String> getScoreboardTags() {
		return this.scoreboardTags;
	}

	public boolean addScoreboardTag(String string) {
		return this.scoreboardTags.size() >= 1024 ? false : this.scoreboardTags.add(string);
	}

	public boolean removeScoreboardTag(String string) {
		return this.scoreboardTags.remove(string);
	}

	public void kill() {
		this.remove();
	}

	protected abstract void initDataTracker();

	public DataTracker getDataTracker() {
		return this.dataTracker;
	}

	public boolean equals(Object object) {
		return object instanceof Entity ? ((Entity)object).entityId == this.entityId : false;
	}

	public int hashCode() {
		return this.entityId;
	}

	@Environment(EnvType.CLIENT)
	protected void afterSpawn() {
		if (this.world != null) {
			while (this.y > 0.0 && this.y < 256.0) {
				this.setPosition(this.x, this.y, this.z);
				if (this.world.doesNotCollide(this)) {
					break;
				}

				this.y++;
			}

			this.setVelocity(Vec3d.ZERO);
			this.pitch = 0.0F;
		}
	}

	public void remove() {
		this.removed = true;
	}

	protected void setPose(EntityPose entityPose) {
		this.dataTracker.set(POSE, entityPose);
	}

	public EntityPose getPose() {
		return this.dataTracker.get(POSE);
	}

	protected void setRotation(float f, float g) {
		this.yaw = f % 360.0F;
		this.pitch = g % 360.0F;
	}

	public void setPosition(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
		float g = this.dimensions.width / 2.0F;
		float h = this.dimensions.height;
		this.setBoundingBox(new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
	}

	@Environment(EnvType.CLIENT)
	public void changeLookDirection(double d, double e) {
		double f = e * 0.15;
		double g = d * 0.15;
		this.pitch = (float)((double)this.pitch + f);
		this.yaw = (float)((double)this.yaw + g);
		this.pitch = MathHelper.clamp(this.pitch, -90.0F, 90.0F);
		this.prevPitch = (float)((double)this.prevPitch + f);
		this.prevYaw = (float)((double)this.prevYaw + g);
		this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0F, 90.0F);
		if (this.vehicle != null) {
			this.vehicle.onPassengerLookAround(this);
		}
	}

	public void tick() {
		if (!this.world.isClient) {
			this.setFlag(6, this.isGlowing());
		}

		this.baseTick();
	}

	public void baseTick() {
		this.world.getProfiler().push("entityBaseTick");
		if (this.hasVehicle() && this.getVehicle().removed) {
			this.stopRiding();
		}

		if (this.ridingCooldown > 0) {
			this.ridingCooldown--;
		}

		this.prevHorizontalSpeed = this.horizontalSpeed;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
		this.tickPortal();
		this.attemptSprintingParticles();
		this.updateInWater();
		if (this.world.isClient) {
			this.extinguish();
		} else if (this.fireTime > 0) {
			if (this.isFireImmune()) {
				this.fireTime -= 4;
				if (this.fireTime < 0) {
					this.extinguish();
				}
			} else {
				if (this.fireTime % 20 == 0) {
					this.damage(DamageSource.ON_FIRE, 1.0F);
				}

				this.fireTime--;
			}
		}

		if (this.isInLava()) {
			this.setOnFireFromLava();
			this.fallDistance *= 0.5F;
		}

		if (this.y < -64.0) {
			this.destroy();
		}

		if (!this.world.isClient) {
			this.setFlag(0, this.fireTime > 0);
		}

		this.firstUpdate = false;
		this.world.getProfiler().pop();
	}

	protected void tickPortalCooldown() {
		if (this.portalCooldown > 0) {
			this.portalCooldown--;
		}
	}

	public int getMaxPortalTime() {
		return 1;
	}

	protected void setOnFireFromLava() {
		if (!this.isFireImmune()) {
			this.setOnFireFor(15);
			this.damage(DamageSource.LAVA, 4.0F);
		}
	}

	public void setOnFireFor(int i) {
		int j = i * 20;
		if (this instanceof LivingEntity) {
			j = ProtectionEnchantment.transformFireDuration((LivingEntity)this, j);
		}

		if (this.fireTime < j) {
			this.fireTime = j;
		}
	}

	public void method_20803(int i) {
		this.fireTime = i;
	}

	public int method_20802() {
		return this.fireTime;
	}

	public void extinguish() {
		this.fireTime = 0;
	}

	protected void destroy() {
		this.remove();
	}

	public boolean doesNotCollide(double d, double e, double f) {
		return this.doesNotCollide(this.getBoundingBox().offset(d, e, f));
	}

	private boolean doesNotCollide(Box box) {
		return this.world.doesNotCollide(this, box) && !this.world.intersectsFluid(box);
	}

	public void move(MovementType movementType, Vec3d vec3d) {
		if (this.noClip) {
			this.setBoundingBox(this.getBoundingBox().offset(vec3d));
			this.moveToBoundingBoxCenter();
		} else {
			if (movementType == MovementType.PISTON) {
				vec3d = this.applyPistonMovement(vec3d);
				if (vec3d.equals(Vec3d.ZERO)) {
					return;
				}
			}

			this.world.getProfiler().push("move");
			if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
				vec3d = vec3d.multiply(this.movementMultiplier);
				this.movementMultiplier = Vec3d.ZERO;
				this.setVelocity(Vec3d.ZERO);
			}

			vec3d = this.clipSneakingMovement(vec3d, movementType);
			Vec3d vec3d2 = this.handleCollisions(vec3d);
			if (vec3d2.lengthSquared() > 1.0E-7) {
				this.setBoundingBox(this.getBoundingBox().offset(vec3d2));
				this.moveToBoundingBoxCenter();
			}

			this.world.getProfiler().pop();
			this.world.getProfiler().push("rest");
			this.horizontalCollision = !MathHelper.method_20390(vec3d.x, vec3d2.x) || !MathHelper.method_20390(vec3d.z, vec3d2.z);
			this.verticalCollision = vec3d.y != vec3d2.y;
			this.onGround = this.verticalCollision && vec3d.y < 0.0;
			this.collided = this.horizontalCollision || this.verticalCollision;
			int i = MathHelper.floor(this.x);
			int j = MathHelper.floor(this.y - 0.2F);
			int k = MathHelper.floor(this.z);
			BlockPos blockPos = new BlockPos(i, j, k);
			BlockState blockState = this.world.getBlockState(blockPos);
			if (blockState.isAir()) {
				BlockPos blockPos2 = blockPos.down();
				BlockState blockState2 = this.world.getBlockState(blockPos2);
				Block block = blockState2.getBlock();
				if (block.matches(BlockTags.FENCES) || block.matches(BlockTags.WALLS) || block instanceof FenceGateBlock) {
					blockState = blockState2;
					blockPos = blockPos2;
				}
			}

			this.fall(vec3d2.y, this.onGround, blockState, blockPos);
			Vec3d vec3d3 = this.getVelocity();
			if (vec3d.x != vec3d2.x) {
				this.setVelocity(0.0, vec3d3.y, vec3d3.z);
			}

			if (vec3d.z != vec3d2.z) {
				this.setVelocity(vec3d3.x, vec3d3.y, 0.0);
			}

			Block block2 = blockState.getBlock();
			if (vec3d.y != vec3d2.y) {
				block2.onEntityLand(this.world, this);
			}

			if (this.canClimb() && (!this.onGround || !this.isSneaking() || !(this instanceof PlayerEntity)) && !this.hasVehicle()) {
				double d = vec3d2.x;
				double e = vec3d2.y;
				double f = vec3d2.z;
				if (block2 != Blocks.LADDER && block2 != Blocks.SCAFFOLDING) {
					e = 0.0;
				}

				if (this.onGround) {
					block2.onSteppedOn(this.world, blockPos, this);
				}

				this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(squaredHorizontalLength(vec3d2)) * 0.6);
				this.distanceWalked = (float)((double)this.distanceWalked + (double)MathHelper.sqrt(d * d + e * e + f * f) * 0.6);
				if (this.distanceWalked > this.nextStepDistance && !blockState.isAir()) {
					this.nextStepDistance = this.calculateStepDelta();
					if (this.isInsideWater()) {
						Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
						float g = entity == this ? 0.35F : 0.4F;
						Vec3d vec3d4 = entity.getVelocity();
						float h = MathHelper.sqrt(vec3d4.x * vec3d4.x * 0.2F + vec3d4.y * vec3d4.y + vec3d4.z * vec3d4.z * 0.2F) * g;
						if (h > 1.0F) {
							h = 1.0F;
						}

						this.playSwimSound(h);
					} else {
						this.playStepSound(blockPos, blockState);
					}
				} else if (this.distanceWalked > this.aerialStepDelta && this.method_5776() && blockState.isAir()) {
					this.aerialStepDelta = this.calculateAerialStepDelta(this.distanceWalked);
				}
			}

			try {
				this.inLava = false;
				this.checkBlockCollision();
			} catch (Throwable var21) {
				CrashReport crashReport = CrashReport.create(var21, "Checking entity block collision");
				CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
				this.populateCrashReport(crashReportSection);
				throw new CrashException(crashReport);
			}

			boolean bl = this.isTouchingWater();
			if (this.world.doesAreaContainFireSource(this.getBoundingBox().contract(0.001))) {
				if (!bl) {
					this.fireTime++;
					if (this.fireTime == 0) {
						this.setOnFireFor(8);
					}
				}

				this.burn(1);
			} else if (this.fireTime <= 0) {
				this.fireTime = -this.getBurningDuration();
			}

			if (bl && this.isOnFire()) {
				this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
				this.fireTime = -this.getBurningDuration();
			}

			this.world.getProfiler().pop();
		}
	}

	protected Vec3d clipSneakingMovement(Vec3d vec3d, MovementType movementType) {
		if (this instanceof PlayerEntity && (movementType == MovementType.SELF || movementType == MovementType.PLAYER) && this.onGround && this.isSneaking()) {
			double d = vec3d.x;
			double e = vec3d.z;
			double f = 0.05;

			while (d != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), 0.0))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}
			}

			while (e != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(0.0, (double)(-this.stepHeight), e))) {
				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			while (d != 0.0 && e != 0.0 && this.world.doesNotCollide(this, this.getBoundingBox().offset(d, (double)(-this.stepHeight), e))) {
				if (d < 0.05 && d >= -0.05) {
					d = 0.0;
				} else if (d > 0.0) {
					d -= 0.05;
				} else {
					d += 0.05;
				}

				if (e < 0.05 && e >= -0.05) {
					e = 0.0;
				} else if (e > 0.0) {
					e -= 0.05;
				} else {
					e += 0.05;
				}
			}

			vec3d = new Vec3d(d, vec3d.y, e);
		}

		return vec3d;
	}

	protected Vec3d applyPistonMovement(Vec3d vec3d) {
		if (vec3d.lengthSquared() <= 1.0E-7) {
			return vec3d;
		} else {
			long l = this.world.getTime();
			if (l != this.pistonMovementTick) {
				Arrays.fill(this.pistonMovementDelta, 0.0);
				this.pistonMovementTick = l;
			}

			if (vec3d.x != 0.0) {
				double d = this.calculatePistonMovementFactor(Direction.Axis.X, vec3d.x);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
			} else if (vec3d.y != 0.0) {
				double d = this.calculatePistonMovementFactor(Direction.Axis.Y, vec3d.y);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, d, 0.0);
			} else if (vec3d.z != 0.0) {
				double d = this.calculatePistonMovementFactor(Direction.Axis.Z, vec3d.z);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, 0.0, d);
			} else {
				return Vec3d.ZERO;
			}
		}
	}

	private double calculatePistonMovementFactor(Direction.Axis axis, double d) {
		int i = axis.ordinal();
		double e = MathHelper.clamp(d + this.pistonMovementDelta[i], -0.51, 0.51);
		d = e - this.pistonMovementDelta[i];
		this.pistonMovementDelta[i] = e;
		return d;
	}

	private Vec3d handleCollisions(Vec3d vec3d) {
		Box box = this.getBoundingBox();
		EntityContext entityContext = EntityContext.of(this);
		VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
		Stream<VoxelShape> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7)), BooleanBiFunction.AND)
			? Stream.empty()
			: Stream.of(voxelShape);
		Stream<VoxelShape> stream2 = this.world.method_20743(this, box.stretch(vec3d), ImmutableSet.of());
		ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(Stream.concat(stream2, stream));
		Vec3d vec3d2 = vec3d.lengthSquared() == 0.0 ? vec3d : calculateMotionVector(this, vec3d, box, this.world, entityContext, reusableStream);
		boolean bl = vec3d.x != vec3d2.x;
		boolean bl2 = vec3d.y != vec3d2.y;
		boolean bl3 = vec3d.z != vec3d2.z;
		boolean bl4 = this.onGround || bl2 && vec3d.y < 0.0;
		if (this.stepHeight > 0.0F && bl4 && (bl || bl3)) {
			Vec3d vec3d3 = calculateMotionVector(this, new Vec3d(vec3d.x, (double)this.stepHeight, vec3d.z), box, this.world, entityContext, reusableStream);
			Vec3d vec3d4 = calculateMotionVector(
				this, new Vec3d(0.0, (double)this.stepHeight, 0.0), box.stretch(vec3d.x, 0.0, vec3d.z), this.world, entityContext, reusableStream
			);
			if (vec3d4.y < (double)this.stepHeight) {
				Vec3d vec3d5 = calculateMotionVector(this, new Vec3d(vec3d.x, 0.0, vec3d.z), box.offset(vec3d4), this.world, entityContext, reusableStream).add(vec3d4);
				if (squaredHorizontalLength(vec3d5) > squaredHorizontalLength(vec3d3)) {
					vec3d3 = vec3d5;
				}
			}

			if (squaredHorizontalLength(vec3d3) > squaredHorizontalLength(vec3d2)) {
				return vec3d3.add(calculateMotionVector(this, new Vec3d(0.0, -vec3d3.y + vec3d.y, 0.0), box.offset(vec3d3), this.world, entityContext, reusableStream));
			}
		}

		return vec3d2;
	}

	public static double squaredHorizontalLength(Vec3d vec3d) {
		return vec3d.x * vec3d.x + vec3d.z * vec3d.z;
	}

	public static Vec3d calculateMotionVector(
		@Nullable Entity entity, Vec3d vec3d, Box box, World world, EntityContext entityContext, ReusableStream<VoxelShape> reusableStream
	) {
		boolean bl = vec3d.x == 0.0;
		boolean bl2 = vec3d.y == 0.0;
		boolean bl3 = vec3d.z == 0.0;
		if ((!bl || !bl2) && (!bl || !bl3) && (!bl2 || !bl3)) {
			ReusableStream<VoxelShape> reusableStream2 = new ReusableStream<>(Stream.concat(reusableStream.stream(), world.method_20812(entity, box.stretch(vec3d))));
			return method_20737(vec3d, box, reusableStream2);
		} else {
			return calculateTangentialMotionVector(vec3d, box, world, entityContext, reusableStream);
		}
	}

	public static Vec3d method_20737(Vec3d vec3d, Box box, ReusableStream<VoxelShape> reusableStream) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		if (e != 0.0) {
			e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box, reusableStream.stream(), e);
			if (e != 0.0) {
				box = box.offset(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, reusableStream.stream(), f);
			if (f != 0.0) {
				box = box.offset(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, box, reusableStream.stream(), d);
			if (!bl && d != 0.0) {
				box = box.offset(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, reusableStream.stream(), f);
		}

		return new Vec3d(d, e, f);
	}

	public static Vec3d calculateTangentialMotionVector(
		Vec3d vec3d, Box box, ViewableWorld viewableWorld, EntityContext entityContext, ReusableStream<VoxelShape> reusableStream
	) {
		double d = vec3d.x;
		double e = vec3d.y;
		double f = vec3d.z;
		if (e != 0.0) {
			e = VoxelShapes.calculateSoftOffset(Direction.Axis.Y, box, viewableWorld, e, entityContext, reusableStream.stream());
			if (e != 0.0) {
				box = box.offset(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = VoxelShapes.calculateSoftOffset(Direction.Axis.Z, box, viewableWorld, f, entityContext, reusableStream.stream());
			if (f != 0.0) {
				box = box.offset(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = VoxelShapes.calculateSoftOffset(Direction.Axis.X, box, viewableWorld, d, entityContext, reusableStream.stream());
			if (!bl && d != 0.0) {
				box = box.offset(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = VoxelShapes.calculateSoftOffset(Direction.Axis.Z, box, viewableWorld, f, entityContext, reusableStream.stream());
		}

		return new Vec3d(d, e, f);
	}

	protected float calculateStepDelta() {
		return (float)((int)this.distanceWalked + 1);
	}

	public void moveToBoundingBoxCenter() {
		Box box = this.getBoundingBox();
		this.x = (box.minX + box.maxX) / 2.0;
		this.y = box.minY;
		this.z = (box.minZ + box.maxZ) / 2.0;
	}

	protected SoundEvent getSwimSound() {
		return SoundEvents.ENTITY_GENERIC_SWIM;
	}

	protected SoundEvent getSplashSound() {
		return SoundEvents.ENTITY_GENERIC_SPLASH;
	}

	protected SoundEvent getHighSpeedSplashSound() {
		return SoundEvents.ENTITY_GENERIC_SPLASH;
	}

	protected void checkBlockCollision() {
		Box box = this.getBoundingBox();

		try (
			BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
			BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
			BlockPos.PooledMutable pooledMutable3 = BlockPos.PooledMutable.get();
		) {
			if (this.world.isAreaLoaded(pooledMutable, pooledMutable2)) {
				for (int i = pooledMutable.getX(); i <= pooledMutable2.getX(); i++) {
					for (int j = pooledMutable.getY(); j <= pooledMutable2.getY(); j++) {
						for (int k = pooledMutable.getZ(); k <= pooledMutable2.getZ(); k++) {
							pooledMutable3.method_10113(i, j, k);
							BlockState blockState = this.world.getBlockState(pooledMutable3);

							try {
								blockState.onEntityCollision(this.world, pooledMutable3, this);
								this.onBlockCollision(blockState);
							} catch (Throwable var60) {
								CrashReport crashReport = CrashReport.create(var60, "Colliding entity with block");
								CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
								CrashReportSection.addBlockInfo(crashReportSection, pooledMutable3, blockState);
								throw new CrashException(crashReport);
							}
						}
					}
				}
			}
		}
	}

	protected void onBlockCollision(BlockState blockState) {
	}

	protected void playStepSound(BlockPos blockPos, BlockState blockState) {
		if (!blockState.getMaterial().isLiquid()) {
			BlockState blockState2 = this.world.getBlockState(blockPos.up());
			BlockSoundGroup blockSoundGroup = blockState2.getBlock() == Blocks.SNOW ? blockState2.getSoundGroup() : blockState.getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		}
	}

	protected void playSwimSound(float f) {
		this.playSound(this.getSwimSound(), f, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
	}

	protected float calculateAerialStepDelta(float f) {
		return 0.0F;
	}

	protected boolean method_5776() {
		return false;
	}

	public void playSound(SoundEvent soundEvent, float f, float g) {
		if (!this.isSilent()) {
			this.world.playSound(null, this.x, this.y, this.z, soundEvent, this.getSoundCategory(), f, g);
		}
	}

	public boolean isSilent() {
		return this.dataTracker.get(SILENT);
	}

	public void setSilent(boolean bl) {
		this.dataTracker.set(SILENT, bl);
	}

	public boolean hasNoGravity() {
		return this.dataTracker.get(NO_GRAVITY);
	}

	public void setNoGravity(boolean bl) {
		this.dataTracker.set(NO_GRAVITY, bl);
	}

	protected boolean canClimb() {
		return true;
	}

	protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
		if (bl) {
			if (this.fallDistance > 0.0F) {
				blockState.getBlock().onLandedUpon(this.world, blockPos, this, this.fallDistance);
			}

			this.fallDistance = 0.0F;
		} else if (d < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - d);
		}
	}

	@Nullable
	public Box getCollisionBox() {
		return null;
	}

	protected void burn(int i) {
		if (!this.isFireImmune()) {
			this.damage(DamageSource.IN_FIRE, (float)i);
		}
	}

	public final boolean isFireImmune() {
		return this.getType().isFireImmune();
	}

	public void handleFallDamage(float f, float g) {
		if (this.hasPassengers()) {
			for (Entity entity : this.getPassengerList()) {
				entity.handleFallDamage(f, g);
			}
		}
	}

	public boolean isInsideWater() {
		return this.insideWater;
	}

	private boolean isBeingRainedOn() {
		boolean var3;
		try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.getEntityPos(this)) {
			var3 = this.world.hasRain(pooledMutable) || this.world.hasRain(pooledMutable.method_10112(this.x, this.y + (double)this.dimensions.height, this.z));
		}

		return var3;
	}

	private boolean isInsideBubbleColumn() {
		return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
	}

	public boolean isInsideWaterOrRain() {
		return this.isInsideWater() || this.isBeingRainedOn();
	}

	public boolean isTouchingWater() {
		return this.isInsideWater() || this.isBeingRainedOn() || this.isInsideBubbleColumn();
	}

	public boolean isInsideWaterOrBubbleColumn() {
		return this.isInsideWater() || this.isInsideBubbleColumn();
	}

	public boolean isInWater() {
		return this.inWater && this.isInsideWater();
	}

	private void updateInWater() {
		this.checkWaterState();
		this.updateWetState();
		this.updateSwimming();
	}

	public void updateSwimming() {
		if (this.isSwimming()) {
			this.setSwimming(this.isSprinting() && this.isInsideWater() && !this.hasVehicle());
		} else {
			this.setSwimming(this.isSprinting() && this.isInWater() && !this.hasVehicle());
		}
	}

	public boolean checkWaterState() {
		if (this.getVehicle() instanceof BoatEntity) {
			this.insideWater = false;
		} else if (this.updateMovementInFluid(FluidTags.WATER)) {
			if (!this.insideWater && !this.firstUpdate) {
				this.onSwimmingStart();
			}

			this.fallDistance = 0.0F;
			this.insideWater = true;
			this.extinguish();
		} else {
			this.insideWater = false;
		}

		return this.insideWater;
	}

	private void updateWetState() {
		this.inWater = this.isSubmergedIn(FluidTags.WATER, true);
	}

	protected void onSwimmingStart() {
		Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
		float f = entity == this ? 0.2F : 0.9F;
		Vec3d vec3d = entity.getVelocity();
		float g = MathHelper.sqrt(vec3d.x * vec3d.x * 0.2F + vec3d.y * vec3d.y + vec3d.z * vec3d.z * 0.2F) * f;
		if (g > 1.0F) {
			g = 1.0F;
		}

		if ((double)g < 0.25) {
			this.playSound(this.getSplashSound(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		} else {
			this.playSound(this.getHighSpeedSplashSound(), g, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
		}

		float h = (float)MathHelper.floor(this.getBoundingBox().minY);

		for (int i = 0; (float)i < 1.0F + this.dimensions.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			this.world
				.addParticle(
					ParticleTypes.BUBBLE, this.x + (double)j, (double)(h + 1.0F), this.z + (double)k, vec3d.x, vec3d.y - (double)(this.random.nextFloat() * 0.2F), vec3d.z
				);
		}

		for (int i = 0; (float)i < 1.0F + this.dimensions.width * 20.0F; i++) {
			float j = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			float k = (this.random.nextFloat() * 2.0F - 1.0F) * this.dimensions.width;
			this.world.addParticle(ParticleTypes.SPLASH, this.x + (double)j, (double)(h + 1.0F), this.z + (double)k, vec3d.x, vec3d.y, vec3d.z);
		}
	}

	public void attemptSprintingParticles() {
		if (this.isSprinting() && !this.isInsideWater()) {
			this.spawnSprintingParticles();
		}
	}

	protected void spawnSprintingParticles() {
		int i = MathHelper.floor(this.x);
		int j = MathHelper.floor(this.y - 0.2F);
		int k = MathHelper.floor(this.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			Vec3d vec3d = this.getVelocity();
			this.world
				.addParticle(
					new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
					this.x + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width,
					this.y + 0.1,
					this.z + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width,
					vec3d.x * -4.0,
					1.5,
					vec3d.z * -4.0
				);
		}
	}

	public boolean isInFluid(Tag<Fluid> tag) {
		return this.isSubmergedIn(tag, false);
	}

	public boolean isSubmergedIn(Tag<Fluid> tag, boolean bl) {
		if (this.getVehicle() instanceof BoatEntity) {
			return false;
		} else {
			double d = this.y + (double)this.getStandingEyeHeight();
			BlockPos blockPos = new BlockPos(this.x, d, this.z);
			if (bl && !this.world.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
				return false;
			} else {
				FluidState fluidState = this.world.getFluidState(blockPos);
				return fluidState.matches(tag) && d < (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos) + 0.11111111F);
			}
		}
	}

	public void setInLava() {
		this.inLava = true;
	}

	public boolean isInLava() {
		return this.inLava;
	}

	public void updateVelocity(float f, Vec3d vec3d) {
		Vec3d vec3d2 = movementInputToVelocity(vec3d, f, this.yaw);
		this.setVelocity(this.getVelocity().add(vec3d2));
	}

	protected static Vec3d movementInputToVelocity(Vec3d vec3d, float f, float g) {
		double d = vec3d.lengthSquared();
		if (d < 1.0E-7) {
			return Vec3d.ZERO;
		} else {
			Vec3d vec3d2 = (d > 1.0 ? vec3d.normalize() : vec3d).multiply((double)f);
			float h = MathHelper.sin(g * (float) (Math.PI / 180.0));
			float i = MathHelper.cos(g * (float) (Math.PI / 180.0));
			return new Vec3d(vec3d2.x * (double)i - vec3d2.z * (double)h, vec3d2.y, vec3d2.z * (double)i + vec3d2.x * (double)h);
		}
	}

	@Environment(EnvType.CLIENT)
	public int getLightmapCoordinates() {
		BlockPos blockPos = new BlockPos(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
		return this.world.isBlockLoaded(blockPos) ? this.world.getLightmapIndex(blockPos, 0) : 0;
	}

	public float getBrightnessAtEyes() {
		BlockPos.Mutable mutable = new BlockPos.Mutable(this.x, 0.0, this.z);
		if (this.world.isBlockLoaded(mutable)) {
			mutable.setY(MathHelper.floor(this.y + (double)this.getStandingEyeHeight()));
			return this.world.getBrightness(mutable);
		} else {
			return 0.0F;
		}
	}

	public void setWorld(World world) {
		this.world = world;
	}

	public void setPositionAnglesAndUpdate(double d, double e, double f, float g, float h) {
		this.x = MathHelper.clamp(d, -3.0E7, 3.0E7);
		this.y = e;
		this.z = MathHelper.clamp(f, -3.0E7, 3.0E7);
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		h = MathHelper.clamp(h, -90.0F, 90.0F);
		this.yaw = g;
		this.pitch = h;
		this.prevYaw = this.yaw;
		this.prevPitch = this.pitch;
		double i = (double)(this.prevYaw - g);
		if (i < -180.0) {
			this.prevYaw += 360.0F;
		}

		if (i >= 180.0) {
			this.prevYaw -= 360.0F;
		}

		this.setPosition(this.x, this.y, this.z);
		this.setRotation(g, h);
	}

	public void setPositionAndAngles(BlockPos blockPos, float f, float g) {
		this.setPositionAndAngles((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5, f, g);
	}

	public void setPositionAndAngles(double d, double e, double f, float g, float h) {
		this.x = d;
		this.y = e;
		this.z = f;
		this.prevX = this.x;
		this.prevY = this.y;
		this.prevZ = this.z;
		this.prevRenderX = this.x;
		this.prevRenderY = this.y;
		this.prevRenderZ = this.z;
		this.yaw = g;
		this.pitch = h;
		this.setPosition(this.x, this.y, this.z);
	}

	public float distanceTo(Entity entity) {
		float f = (float)(this.x - entity.x);
		float g = (float)(this.y - entity.y);
		float h = (float)(this.z - entity.z);
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public double squaredDistanceTo(double d, double e, double f) {
		double g = this.x - d;
		double h = this.y - e;
		double i = this.z - f;
		return g * g + h * h + i * i;
	}

	public double squaredDistanceTo(Entity entity) {
		return this.squaredDistanceTo(entity.getPos());
	}

	public double squaredDistanceTo(Vec3d vec3d) {
		double d = this.x - vec3d.x;
		double e = this.y - vec3d.y;
		double f = this.z - vec3d.z;
		return d * d + e * e + f * f;
	}

	public void onPlayerCollision(PlayerEntity playerEntity) {
	}

	public void pushAwayFrom(Entity entity) {
		if (!this.isConnectedThroughVehicle(entity)) {
			if (!entity.noClip && !this.noClip) {
				double d = entity.x - this.x;
				double e = entity.z - this.z;
				double f = MathHelper.absMax(d, e);
				if (f >= 0.01F) {
					f = (double)MathHelper.sqrt(f);
					d /= f;
					e /= f;
					double g = 1.0 / f;
					if (g > 1.0) {
						g = 1.0;
					}

					d *= g;
					e *= g;
					d *= 0.05F;
					e *= 0.05F;
					d *= (double)(1.0F - this.pushSpeedReduction);
					e *= (double)(1.0F - this.pushSpeedReduction);
					if (!this.hasPassengers()) {
						this.addVelocity(-d, 0.0, -e);
					}

					if (!entity.hasPassengers()) {
						entity.addVelocity(d, 0.0, e);
					}
				}
			}
		}
	}

	public void addVelocity(double d, double e, double f) {
		this.setVelocity(this.getVelocity().add(d, e, f));
		this.velocityDirty = true;
	}

	protected void scheduleVelocityUpdate() {
		this.velocityModified = true;
	}

	public boolean damage(DamageSource damageSource, float f) {
		if (this.isInvulnerableTo(damageSource)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			return false;
		}
	}

	public final Vec3d getRotationVec(float f) {
		return this.getRotationVector(this.getPitch(f), this.getYaw(f));
	}

	public float getPitch(float f) {
		return f == 1.0F ? this.pitch : MathHelper.lerp(f, this.prevPitch, this.pitch);
	}

	public float getYaw(float f) {
		return f == 1.0F ? this.yaw : MathHelper.lerp(f, this.prevYaw, this.yaw);
	}

	protected final Vec3d getRotationVector(float f, float g) {
		float h = f * (float) (Math.PI / 180.0);
		float i = -g * (float) (Math.PI / 180.0);
		float j = MathHelper.cos(i);
		float k = MathHelper.sin(i);
		float l = MathHelper.cos(h);
		float m = MathHelper.sin(h);
		return new Vec3d((double)(k * l), (double)(-m), (double)(j * l));
	}

	public final Vec3d getOppositeRotationVector(float f) {
		return this.getOppositeRotationVector(this.getPitch(f), this.getYaw(f));
	}

	protected final Vec3d getOppositeRotationVector(float f, float g) {
		return this.getRotationVector(f - 90.0F, g);
	}

	public Vec3d getCameraPosVec(float f) {
		if (f == 1.0F) {
			return new Vec3d(this.x, this.y + (double)this.getStandingEyeHeight(), this.z);
		} else {
			double d = MathHelper.lerp((double)f, this.prevX, this.x);
			double e = MathHelper.lerp((double)f, this.prevY, this.y) + (double)this.getStandingEyeHeight();
			double g = MathHelper.lerp((double)f, this.prevZ, this.z);
			return new Vec3d(d, e, g);
		}
	}

	@Environment(EnvType.CLIENT)
	public HitResult rayTrace(double d, float f, boolean bl) {
		Vec3d vec3d = this.getCameraPosVec(f);
		Vec3d vec3d2 = this.getRotationVec(f);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
		return this.world
			.rayTrace(
				new RayTraceContext(vec3d, vec3d3, RayTraceContext.ShapeType.OUTLINE, bl ? RayTraceContext.FluidHandling.ANY : RayTraceContext.FluidHandling.NONE, this)
			);
	}

	public boolean collides() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public void updateKilledAdvancementCriterion(Entity entity, int i, DamageSource damageSource) {
		if (entity instanceof ServerPlayerEntity) {
			Criterions.ENTITY_KILLED_PLAYER.handle((ServerPlayerEntity)entity, this, damageSource);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderFrom(double d, double e, double f) {
		double g = this.x - d;
		double h = this.y - e;
		double i = this.z - f;
		double j = g * g + h * h + i * i;
		return this.shouldRenderAtDistance(j);
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderAtDistance(double d) {
		double e = this.getBoundingBox().averageDimension();
		if (Double.isNaN(e)) {
			e = 1.0;
		}

		e *= 64.0 * renderDistanceMultiplier;
		return d < e * e;
	}

	public boolean saveSelfToTag(CompoundTag compoundTag) {
		String string = this.getSavedEntityId();
		if (!this.removed && string != null) {
			compoundTag.putString("id", string);
			this.toTag(compoundTag);
			return true;
		} else {
			return false;
		}
	}

	public boolean saveToTag(CompoundTag compoundTag) {
		return this.hasVehicle() ? false : this.saveSelfToTag(compoundTag);
	}

	public CompoundTag toTag(CompoundTag compoundTag) {
		try {
			compoundTag.put("Pos", this.toListTag(this.x, this.y, this.z));
			Vec3d vec3d = this.getVelocity();
			compoundTag.put("Motion", this.toListTag(vec3d.x, vec3d.y, vec3d.z));
			compoundTag.put("Rotation", this.toListTag(this.yaw, this.pitch));
			compoundTag.putFloat("FallDistance", this.fallDistance);
			compoundTag.putShort("Fire", (short)this.fireTime);
			compoundTag.putShort("Air", (short)this.getBreath());
			compoundTag.putBoolean("OnGround", this.onGround);
			compoundTag.putInt("Dimension", this.dimension.getRawId());
			compoundTag.putBoolean("Invulnerable", this.invulnerable);
			compoundTag.putInt("PortalCooldown", this.portalCooldown);
			compoundTag.putUuid("UUID", this.getUuid());
			Text text = this.getCustomName();
			if (text != null) {
				compoundTag.putString("CustomName", Text.Serializer.toJson(text));
			}

			if (this.isCustomNameVisible()) {
				compoundTag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
			}

			if (this.isSilent()) {
				compoundTag.putBoolean("Silent", this.isSilent());
			}

			if (this.hasNoGravity()) {
				compoundTag.putBoolean("NoGravity", this.hasNoGravity());
			}

			if (this.glowing) {
				compoundTag.putBoolean("Glowing", this.glowing);
			}

			if (!this.scoreboardTags.isEmpty()) {
				ListTag listTag = new ListTag();

				for (String string : this.scoreboardTags) {
					listTag.add(new StringTag(string));
				}

				compoundTag.put("Tags", listTag);
			}

			this.writeCustomDataToTag(compoundTag);
			if (this.hasPassengers()) {
				ListTag listTag = new ListTag();

				for (Entity entity : this.getPassengerList()) {
					CompoundTag compoundTag2 = new CompoundTag();
					if (entity.saveSelfToTag(compoundTag2)) {
						listTag.add(compoundTag2);
					}
				}

				if (!listTag.isEmpty()) {
					compoundTag.put("Passengers", listTag);
				}
			}

			return compoundTag;
		} catch (Throwable var8) {
			CrashReport crashReport = CrashReport.create(var8, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void fromTag(CompoundTag compoundTag) {
		try {
			ListTag listTag = compoundTag.getList("Pos", 6);
			ListTag listTag2 = compoundTag.getList("Motion", 6);
			ListTag listTag3 = compoundTag.getList("Rotation", 5);
			double d = listTag2.getDouble(0);
			double e = listTag2.getDouble(1);
			double f = listTag2.getDouble(2);
			this.setVelocity(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
			this.x = listTag.getDouble(0);
			this.y = listTag.getDouble(1);
			this.z = listTag.getDouble(2);
			this.prevRenderX = this.x;
			this.prevRenderY = this.y;
			this.prevRenderZ = this.z;
			this.prevX = this.x;
			this.prevY = this.y;
			this.prevZ = this.z;
			this.yaw = listTag3.getFloat(0);
			this.pitch = listTag3.getFloat(1);
			this.prevYaw = this.yaw;
			this.prevPitch = this.pitch;
			this.setHeadYaw(this.yaw);
			this.setYaw(this.yaw);
			this.fallDistance = compoundTag.getFloat("FallDistance");
			this.fireTime = compoundTag.getShort("Fire");
			this.setBreath(compoundTag.getShort("Air"));
			this.onGround = compoundTag.getBoolean("OnGround");
			if (compoundTag.containsKey("Dimension")) {
				this.dimension = DimensionType.byRawId(compoundTag.getInt("Dimension"));
			}

			this.invulnerable = compoundTag.getBoolean("Invulnerable");
			this.portalCooldown = compoundTag.getInt("PortalCooldown");
			if (compoundTag.hasUuid("UUID")) {
				this.uuid = compoundTag.getUuid("UUID");
				this.uuidString = this.uuid.toString();
			}

			if (!Double.isFinite(this.x) || !Double.isFinite(this.y) || !Double.isFinite(this.z)) {
				throw new IllegalStateException("Entity has invalid position");
			} else if (Double.isFinite((double)this.yaw) && Double.isFinite((double)this.pitch)) {
				this.setPosition(this.x, this.y, this.z);
				this.setRotation(this.yaw, this.pitch);
				if (compoundTag.containsKey("CustomName", 8)) {
					this.setCustomName(Text.Serializer.fromJson(compoundTag.getString("CustomName")));
				}

				this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
				this.setSilent(compoundTag.getBoolean("Silent"));
				this.setNoGravity(compoundTag.getBoolean("NoGravity"));
				this.setGlowing(compoundTag.getBoolean("Glowing"));
				if (compoundTag.containsKey("Tags", 9)) {
					this.scoreboardTags.clear();
					ListTag listTag4 = compoundTag.getList("Tags", 8);
					int i = Math.min(listTag4.size(), 1024);

					for (int j = 0; j < i; j++) {
						this.scoreboardTags.add(listTag4.getString(j));
					}
				}

				this.readCustomDataFromTag(compoundTag);
				if (this.shouldSetPositionOnLoad()) {
					this.setPosition(this.x, this.y, this.z);
				}
			} else {
				throw new IllegalStateException("Entity has invalid rotation");
			}
		} catch (Throwable var14) {
			CrashReport crashReport = CrashReport.create(var14, "Loading entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being loaded");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	protected boolean shouldSetPositionOnLoad() {
		return true;
	}

	@Nullable
	protected final String getSavedEntityId() {
		EntityType<?> entityType = this.getType();
		Identifier identifier = EntityType.getId(entityType);
		return entityType.isSaveable() && identifier != null ? identifier.toString() : null;
	}

	protected abstract void readCustomDataFromTag(CompoundTag compoundTag);

	protected abstract void writeCustomDataToTag(CompoundTag compoundTag);

	protected ListTag toListTag(double... ds) {
		ListTag listTag = new ListTag();

		for (double d : ds) {
			listTag.add(new DoubleTag(d));
		}

		return listTag;
	}

	protected ListTag toListTag(float... fs) {
		ListTag listTag = new ListTag();

		for (float f : fs) {
			listTag.add(new FloatTag(f));
		}

		return listTag;
	}

	@Nullable
	public ItemEntity dropItem(ItemConvertible itemConvertible) {
		return this.dropItem(itemConvertible, 0);
	}

	@Nullable
	public ItemEntity dropItem(ItemConvertible itemConvertible, int i) {
		return this.dropStack(new ItemStack(itemConvertible), (float)i);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack itemStack) {
		return this.dropStack(itemStack, 0.0F);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack itemStack, float f) {
		if (itemStack.isEmpty()) {
			return null;
		} else if (this.world.isClient) {
			return null;
		} else {
			ItemEntity itemEntity = new ItemEntity(this.world, this.x, this.y + (double)f, this.z, itemStack);
			itemEntity.setToDefaultPickupDelay();
			this.world.spawnEntity(itemEntity);
			return itemEntity;
		}
	}

	public boolean isAlive() {
		return !this.removed;
	}

	public boolean isInsideWall() {
		if (this.noClip) {
			return false;
		} else {
			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int i = 0; i < 8; i++) {
					int j = MathHelper.floor(this.y + (double)(((float)((i >> 0) % 2) - 0.5F) * 0.1F) + (double)this.standingEyeHeight);
					int k = MathHelper.floor(this.x + (double)(((float)((i >> 1) % 2) - 0.5F) * this.dimensions.width * 0.8F));
					int l = MathHelper.floor(this.z + (double)(((float)((i >> 2) % 2) - 0.5F) * this.dimensions.width * 0.8F));
					if (pooledMutable.getX() != k || pooledMutable.getY() != j || pooledMutable.getZ() != l) {
						pooledMutable.method_10113(k, j, l);
						if (this.world.getBlockState(pooledMutable).canSuffocate(this.world, pooledMutable)) {
							return true;
						}
					}
				}

				return false;
			}
		}
	}

	public boolean interact(PlayerEntity playerEntity, Hand hand) {
		return false;
	}

	@Nullable
	public Box getHardCollisionBox(Entity entity) {
		return null;
	}

	public void tickRiding() {
		this.setVelocity(Vec3d.ZERO);
		this.tick();
		if (this.hasVehicle()) {
			this.getVehicle().updatePassengerPosition(this);
		}
	}

	public void updatePassengerPosition(Entity entity) {
		if (this.hasPassenger(entity)) {
			entity.setPosition(this.x, this.y + this.getMountedHeightOffset() + entity.getHeightOffset(), this.z);
		}
	}

	@Environment(EnvType.CLIENT)
	public void onPassengerLookAround(Entity entity) {
	}

	public double getHeightOffset() {
		return 0.0;
	}

	public double getMountedHeightOffset() {
		return (double)this.dimensions.height * 0.75;
	}

	public boolean startRiding(Entity entity) {
		return this.startRiding(entity, false);
	}

	@Environment(EnvType.CLIENT)
	public boolean isLiving() {
		return this instanceof LivingEntity;
	}

	public boolean startRiding(Entity entity, boolean bl) {
		for (Entity entity2 = entity; entity2.vehicle != null; entity2 = entity2.vehicle) {
			if (entity2.vehicle == this) {
				return false;
			}
		}

		if (bl || this.canStartRiding(entity) && entity.canAddPassenger(this)) {
			if (this.hasVehicle()) {
				this.stopRiding();
			}

			this.vehicle = entity;
			this.vehicle.addPassenger(this);
			return true;
		} else {
			return false;
		}
	}

	protected boolean canStartRiding(Entity entity) {
		return this.ridingCooldown <= 0;
	}

	protected boolean wouldPoseNotCollide(EntityPose entityPose) {
		return this.world.doesNotCollide(this, this.calculateBoundsForPose(entityPose));
	}

	public void removeAllPassengers() {
		for (int i = this.passengerList.size() - 1; i >= 0; i--) {
			((Entity)this.passengerList.get(i)).stopRiding();
		}
	}

	public void stopRiding() {
		if (this.vehicle != null) {
			Entity entity = this.vehicle;
			this.vehicle = null;
			entity.removePassenger(this);
		}
	}

	protected void addPassenger(Entity entity) {
		if (entity.getVehicle() != this) {
			throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
		} else {
			if (!this.world.isClient && entity instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
				this.passengerList.add(0, entity);
			} else {
				this.passengerList.add(entity);
			}
		}
	}

	protected void removePassenger(Entity entity) {
		if (entity.getVehicle() == this) {
			throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
		} else {
			this.passengerList.remove(entity);
			entity.ridingCooldown = 60;
		}
	}

	protected boolean canAddPassenger(Entity entity) {
		return this.getPassengerList().size() < 1;
	}

	@Environment(EnvType.CLIENT)
	public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
		this.setPosition(d, e, f);
		this.setRotation(g, h);
	}

	@Environment(EnvType.CLIENT)
	public void updateTrackedHeadRotation(float f, int i) {
		this.setHeadYaw(f);
	}

	public float getTargetingMargin() {
		return 0.0F;
	}

	public Vec3d getRotationVector() {
		return this.getRotationVector(this.pitch, this.yaw);
	}

	public Vec2f getRotationClient() {
		return new Vec2f(this.pitch, this.yaw);
	}

	@Environment(EnvType.CLIENT)
	public Vec3d getRotationVecClient() {
		return Vec3d.fromPolar(this.getRotationClient());
	}

	public void setInPortal(BlockPos blockPos) {
		if (this.portalCooldown > 0) {
			this.portalCooldown = this.getDefaultPortalCooldown();
		} else {
			if (!this.world.isClient && !blockPos.equals(this.lastPortalPosition)) {
				this.lastPortalPosition = new BlockPos(blockPos);
				BlockPattern.Result result = ((PortalBlock)Blocks.NETHER_PORTAL).findPortal(this.world, this.lastPortalPosition);
				double d = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
				double e = Math.abs(
					MathHelper.minusDiv(
						(result.getForwards().getAxis() == Direction.Axis.X ? this.z : this.x)
							- (double)(result.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0),
						d,
						d - (double)result.getWidth()
					)
				);
				double f = MathHelper.minusDiv(this.y - 1.0, (double)result.getFrontTopLeft().getY(), (double)(result.getFrontTopLeft().getY() - result.getHeight()));
				this.lastPortalDirectionVector = new Vec3d(e, f, 0.0);
				this.lastPortalDirection = result.getForwards();
			}

			this.inPortal = true;
		}
	}

	protected void tickPortal() {
		if (this.world instanceof ServerWorld) {
			int i = this.getMaxPortalTime();
			if (this.inPortal) {
				if (this.world.getServer().isNetherAllowed() && !this.hasVehicle() && this.portalTime++ >= i) {
					this.world.getProfiler().push("portal");
					this.portalTime = i;
					this.portalCooldown = this.getDefaultPortalCooldown();
					this.changeDimension(this.world.dimension.getType() == DimensionType.THE_NETHER ? DimensionType.OVERWORLD : DimensionType.THE_NETHER);
					this.world.getProfiler().pop();
				}

				this.inPortal = false;
			} else {
				if (this.portalTime > 0) {
					this.portalTime -= 4;
				}

				if (this.portalTime < 0) {
					this.portalTime = 0;
				}
			}

			this.tickPortalCooldown();
		}
	}

	public int getDefaultPortalCooldown() {
		return 300;
	}

	@Environment(EnvType.CLIENT)
	public void setVelocityClient(double d, double e, double f) {
		this.setVelocity(d, e, f);
	}

	@Environment(EnvType.CLIENT)
	public void handleStatus(byte b) {
	}

	@Environment(EnvType.CLIENT)
	public void animateDamage() {
	}

	public Iterable<ItemStack> getItemsHand() {
		return EMPTY_STACK_LIST;
	}

	public Iterable<ItemStack> getArmorItems() {
		return EMPTY_STACK_LIST;
	}

	public Iterable<ItemStack> getItemsEquipped() {
		return Iterables.concat(this.getItemsHand(), this.getArmorItems());
	}

	public void setEquippedStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
	}

	public boolean isOnFire() {
		boolean bl = this.world != null && this.world.isClient;
		return !this.isFireImmune() && (this.fireTime > 0 || bl && this.getFlag(0));
	}

	public boolean hasVehicle() {
		return this.getVehicle() != null;
	}

	public boolean hasPassengers() {
		return !this.getPassengerList().isEmpty();
	}

	public boolean canBeRiddenInWater() {
		return true;
	}

	public boolean isSneaking() {
		return this.getFlag(1);
	}

	@Environment(EnvType.CLIENT)
	public boolean isInSneakingPose() {
		return this.getPose() == EntityPose.SNEAKING;
	}

	public void setSneaking(boolean bl) {
		this.setFlag(1, bl);
	}

	public boolean isSprinting() {
		return this.getFlag(3);
	}

	public void setSprinting(boolean bl) {
		this.setFlag(3, bl);
	}

	public boolean isSwimming() {
		return this.getFlag(4);
	}

	public boolean isInSwimmingPose() {
		return this.getPose() == EntityPose.SWIMMING;
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldLeaveSwimmingPose() {
		return this.isInSwimmingPose() && !this.isInsideWater();
	}

	public void setSwimming(boolean bl) {
		this.setFlag(4, bl);
	}

	public boolean isGlowing() {
		return this.glowing || this.world.isClient && this.getFlag(6);
	}

	public void setGlowing(boolean bl) {
		this.glowing = bl;
		if (!this.world.isClient) {
			this.setFlag(6, this.glowing);
		}
	}

	public boolean isInvisible() {
		return this.getFlag(5);
	}

	@Environment(EnvType.CLIENT)
	public boolean canSeePlayer(PlayerEntity playerEntity) {
		if (playerEntity.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam != null && playerEntity != null && playerEntity.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles()
				? false
				: this.isInvisible();
		}
	}

	@Nullable
	public AbstractTeam getScoreboardTeam() {
		return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
	}

	public boolean isTeammate(Entity entity) {
		return this.isTeamPlayer(entity.getScoreboardTeam());
	}

	public boolean isTeamPlayer(AbstractTeam abstractTeam) {
		return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(abstractTeam) : false;
	}

	public void setInvisible(boolean bl) {
		this.setFlag(5, bl);
	}

	protected boolean getFlag(int i) {
		return (this.dataTracker.get(FLAGS) & 1 << i) != 0;
	}

	protected void setFlag(int i, boolean bl) {
		byte b = this.dataTracker.get(FLAGS);
		if (bl) {
			this.dataTracker.set(FLAGS, (byte)(b | 1 << i));
		} else {
			this.dataTracker.set(FLAGS, (byte)(b & ~(1 << i)));
		}
	}

	public int getMaxBreath() {
		return 300;
	}

	public int getBreath() {
		return this.dataTracker.get(BREATH);
	}

	public void setBreath(int i) {
		this.dataTracker.set(BREATH, i);
	}

	public void onStruckByLightning(LightningEntity lightningEntity) {
		this.fireTime++;
		if (this.fireTime == 0) {
			this.setOnFireFor(8);
		}

		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
	}

	public void onBubbleColumnSurfaceCollision(boolean bl) {
		Vec3d vec3d = this.getVelocity();
		double d;
		if (bl) {
			d = Math.max(-0.9, vec3d.y - 0.03);
		} else {
			d = Math.min(1.8, vec3d.y + 0.1);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
	}

	public void onBubbleColumnCollision(boolean bl) {
		Vec3d vec3d = this.getVelocity();
		double d;
		if (bl) {
			d = Math.max(-0.3, vec3d.y - 0.03);
		} else {
			d = Math.min(0.7, vec3d.y + 0.06);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
		this.fallDistance = 0.0F;
	}

	public void onKilledOther(LivingEntity livingEntity) {
	}

	protected void pushOutOfBlocks(double d, double e, double f) {
		BlockPos blockPos = new BlockPos(d, e, f);
		Vec3d vec3d = new Vec3d(d - (double)blockPos.getX(), e - (double)blockPos.getY(), f - (double)blockPos.getZ());
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Direction direction = Direction.UP;
		double g = Double.MAX_VALUE;

		for (Direction direction2 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
			mutable.set(blockPos).setOffset(direction2);
			if (!Block.isShapeFullCube(this.world.getBlockState(mutable).getCollisionShape(this.world, mutable))) {
				double h = vec3d.getComponentAlongAxis(direction2.getAxis());
				double i = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - h : h;
				if (i < g) {
					g = i;
					direction = direction2;
				}
			}
		}

		float j = this.random.nextFloat() * 0.2F + 0.1F;
		float k = (float)direction.getDirection().offset();
		Vec3d vec3d2 = this.getVelocity().multiply(0.75);
		if (direction.getAxis() == Direction.Axis.X) {
			this.setVelocity((double)(k * j), vec3d2.y, vec3d2.z);
		} else if (direction.getAxis() == Direction.Axis.Y) {
			this.setVelocity(vec3d2.x, (double)(k * j), vec3d2.z);
		} else if (direction.getAxis() == Direction.Axis.Z) {
			this.setVelocity(vec3d2.x, vec3d2.y, (double)(k * j));
		}
	}

	public void slowMovement(BlockState blockState, Vec3d vec3d) {
		this.fallDistance = 0.0F;
		this.movementMultiplier = vec3d;
	}

	private static void removeClickEvents(Text text) {
		text.styled(style -> style.setClickEvent(null)).getSiblings().forEach(Entity::removeClickEvents);
	}

	@Override
	public Text getName() {
		Text text = this.getCustomName();
		if (text != null) {
			Text text2 = text.deepCopy();
			removeClickEvents(text2);
			return text2;
		} else {
			return this.type.getName();
		}
	}

	public boolean isPartOf(Entity entity) {
		return this == entity;
	}

	public float getHeadYaw() {
		return 0.0F;
	}

	public void setHeadYaw(float f) {
	}

	public void setYaw(float f) {
	}

	public boolean isAttackable() {
		return true;
	}

	public boolean handleAttack(Entity entity) {
		return false;
	}

	public String toString() {
		return String.format(
			Locale.ROOT,
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.getName().asString(),
			this.entityId,
			this.world == null ? "~NULL~" : this.world.getLevelProperties().getLevelName(),
			this.x,
			this.y,
			this.z
		);
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean bl) {
		this.invulnerable = bl;
	}

	public void copyPositionAndRotation(Entity entity) {
		this.setPositionAndAngles(entity.x, entity.y, entity.z, entity.yaw, entity.pitch);
	}

	public void copyFrom(Entity entity) {
		CompoundTag compoundTag = entity.toTag(new CompoundTag());
		compoundTag.remove("Dimension");
		this.fromTag(compoundTag);
		this.portalCooldown = entity.portalCooldown;
		this.lastPortalPosition = entity.lastPortalPosition;
		this.lastPortalDirectionVector = entity.lastPortalDirectionVector;
		this.lastPortalDirection = entity.lastPortalDirection;
	}

	@Nullable
	public Entity changeDimension(DimensionType dimensionType) {
		if (!this.world.isClient && !this.removed) {
			this.world.getProfiler().push("changeDimension");
			MinecraftServer minecraftServer = this.getServer();
			DimensionType dimensionType2 = this.dimension;
			ServerWorld serverWorld = minecraftServer.getWorld(dimensionType2);
			ServerWorld serverWorld2 = minecraftServer.getWorld(dimensionType);
			this.dimension = dimensionType;
			this.detach();
			this.world.getProfiler().push("reposition");
			Vec3d vec3d = this.getVelocity();
			float f = 0.0F;
			BlockPos blockPos;
			if (dimensionType2 == DimensionType.THE_END && dimensionType == DimensionType.OVERWORLD) {
				blockPos = serverWorld2.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverWorld2.getSpawnPos());
			} else if (dimensionType == DimensionType.THE_END) {
				blockPos = serverWorld2.getForcedSpawnPoint();
			} else {
				double d = this.x;
				double e = this.z;
				double g = 8.0;
				if (dimensionType2 == DimensionType.OVERWORLD && dimensionType == DimensionType.THE_NETHER) {
					d /= 8.0;
					e /= 8.0;
				} else if (dimensionType2 == DimensionType.THE_NETHER && dimensionType == DimensionType.OVERWORLD) {
					d *= 8.0;
					e *= 8.0;
				}

				double h = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundWest() + 16.0);
				double i = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundNorth() + 16.0);
				double j = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
				double k = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
				d = MathHelper.clamp(d, h, j);
				e = MathHelper.clamp(e, i, k);
				Vec3d vec3d2 = this.getLastPortalDirectionVector();
				blockPos = new BlockPos(d, this.y, e);
				BlockPattern.TeleportTarget teleportTarget = serverWorld2.getPortalForcer()
					.getPortal(blockPos, vec3d, this.getLastPortalDirection(), vec3d2.x, vec3d2.y, this instanceof PlayerEntity);
				if (teleportTarget == null) {
					return null;
				}

				blockPos = new BlockPos(teleportTarget.pos);
				vec3d = teleportTarget.velocity;
				f = (float)teleportTarget.yaw;
			}

			this.world.getProfiler().swap("reloading");
			Entity entity = this.getType().create(serverWorld2);
			if (entity != null) {
				entity.copyFrom(this);
				entity.setPositionAndAngles(blockPos, entity.yaw + f, entity.pitch);
				entity.setVelocity(vec3d);
				serverWorld2.method_18769(entity);
			}

			this.removed = true;
			this.world.getProfiler().pop();
			serverWorld.resetIdleTimeout();
			serverWorld2.resetIdleTimeout();
			this.world.getProfiler().pop();
			return entity;
		} else {
			return null;
		}
	}

	public boolean canUsePortals() {
		return true;
	}

	public float getEffectiveExplosionResistance(
		Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f
	) {
		return f;
	}

	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, float f) {
		return true;
	}

	public int getSafeFallDistance() {
		return 3;
	}

	public Vec3d getLastPortalDirectionVector() {
		return this.lastPortalDirectionVector;
	}

	public Direction getLastPortalDirection() {
		return this.lastPortalDirection;
	}

	public boolean canAvoidTraps() {
		return false;
	}

	public void populateCrashReport(CrashReportSection crashReportSection) {
		crashReportSection.add("Entity Type", (CrashCallable<String>)(() -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")"));
		crashReportSection.add("Entity ID", this.entityId);
		crashReportSection.add("Entity Name", (CrashCallable<String>)(() -> this.getName().getString()));
		crashReportSection.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.x, this.y, this.z));
		crashReportSection.add(
			"Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.x), MathHelper.floor(this.y), MathHelper.floor(this.z))
		);
		Vec3d vec3d = this.getVelocity();
		crashReportSection.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
		crashReportSection.add("Entity's Passengers", (CrashCallable<String>)(() -> this.getPassengerList().toString()));
		crashReportSection.add("Entity's Vehicle", (CrashCallable<String>)(() -> this.getVehicle().toString()));
	}

	@Environment(EnvType.CLIENT)
	public boolean doesRenderOnFire() {
		return this.isOnFire();
	}

	public void setUuid(UUID uUID) {
		this.uuid = uUID;
		this.uuidString = this.uuid.toString();
	}

	public UUID getUuid() {
		return this.uuid;
	}

	public String getUuidAsString() {
		return this.uuidString;
	}

	public String getEntityName() {
		return this.uuidString;
	}

	public boolean canFly() {
		return true;
	}

	@Environment(EnvType.CLIENT)
	public static double getRenderDistanceMultiplier() {
		return renderDistanceMultiplier;
	}

	@Environment(EnvType.CLIENT)
	public static void setRenderDistanceMultiplier(double d) {
		renderDistanceMultiplier = d;
	}

	@Override
	public Text getDisplayName() {
		return Team.modifyText(this.getScoreboardTeam(), this.getName())
			.styled(style -> style.setHoverEvent(this.getHoverEvent()).setInsertion(this.getUuidAsString()));
	}

	public void setCustomName(@Nullable Text text) {
		this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(text));
	}

	@Nullable
	@Override
	public Text getCustomName() {
		return (Text)this.dataTracker.get(CUSTOM_NAME).orElse(null);
	}

	@Override
	public boolean hasCustomName() {
		return this.dataTracker.get(CUSTOM_NAME).isPresent();
	}

	public void setCustomNameVisible(boolean bl) {
		this.dataTracker.set(NAME_VISIBLE, bl);
	}

	public boolean isCustomNameVisible() {
		return this.dataTracker.get(NAME_VISIBLE);
	}

	public final void teleport(double d, double e, double f) {
		if (this.world instanceof ServerWorld) {
			ChunkPos chunkPos = new ChunkPos(new BlockPos(d, e, f));
			((ServerWorld)this.world).method_14178().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 0, this.getEntityId());
			this.world.method_8497(chunkPos.x, chunkPos.z);
			this.requestTeleport(d, e, f);
		}
	}

	public void requestTeleport(double d, double e, double f) {
		if (this.world instanceof ServerWorld) {
			this.teleportRequested = true;
			this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
			((ServerWorld)this.world).checkChunk(this);
		}
	}

	@Environment(EnvType.CLIENT)
	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void onTrackedDataSet(TrackedData<?> trackedData) {
		if (POSE.equals(trackedData)) {
			this.calculateDimensions();
		}
	}

	public void calculateDimensions() {
		EntityDimensions entityDimensions = this.dimensions;
		EntityPose entityPose = this.getPose();
		EntityDimensions entityDimensions2 = this.getDimensions(entityPose);
		this.dimensions = entityDimensions2;
		this.standingEyeHeight = this.getEyeHeight(entityPose, entityDimensions2);
		if (entityDimensions2.width < entityDimensions.width) {
			double d = (double)entityDimensions2.width / 2.0;
			this.setBoundingBox(new Box(this.x - d, this.y, this.z - d, this.x + d, this.y + (double)entityDimensions2.height, this.z + d));
		} else {
			Box box = this.getBoundingBox();
			this.setBoundingBox(
				new Box(
					box.minX,
					box.minY,
					box.minZ,
					box.minX + (double)entityDimensions2.width,
					box.minY + (double)entityDimensions2.height,
					box.minZ + (double)entityDimensions2.width
				)
			);
			if (entityDimensions2.width > entityDimensions.width && !this.firstUpdate && !this.world.isClient) {
				float f = entityDimensions.width - entityDimensions2.width;
				this.move(MovementType.SELF, new Vec3d((double)f, 0.0, (double)f));
			}
		}
	}

	public Direction getHorizontalFacing() {
		return Direction.fromRotation((double)this.yaw);
	}

	public Direction getMovementDirection() {
		return this.getHorizontalFacing();
	}

	protected HoverEvent getHoverEvent() {
		CompoundTag compoundTag = new CompoundTag();
		Identifier identifier = EntityType.getId(this.getType());
		compoundTag.putString("id", this.getUuidAsString());
		if (identifier != null) {
			compoundTag.putString("type", identifier.toString());
		}

		compoundTag.putString("name", Text.Serializer.toJson(this.getName()));
		return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new LiteralText(compoundTag.toString()));
	}

	public boolean canBeSpectated(ServerPlayerEntity serverPlayerEntity) {
		return true;
	}

	public Box getBoundingBox() {
		return this.entityBounds;
	}

	@Environment(EnvType.CLIENT)
	public Box getVisibilityBoundingBox() {
		return this.getBoundingBox();
	}

	protected Box calculateBoundsForPose(EntityPose entityPose) {
		EntityDimensions entityDimensions = this.getDimensions(entityPose);
		float f = entityDimensions.width / 2.0F;
		Vec3d vec3d = new Vec3d(this.x - (double)f, this.y, this.z - (double)f);
		Vec3d vec3d2 = new Vec3d(this.x + (double)f, this.y + (double)entityDimensions.height, this.z + (double)f);
		return new Box(vec3d, vec3d2);
	}

	public void setBoundingBox(Box box) {
		this.entityBounds = box;
	}

	protected float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
		return entityDimensions.height * 0.85F;
	}

	@Environment(EnvType.CLIENT)
	public float getEyeHeight(EntityPose entityPose) {
		return this.getEyeHeight(entityPose, this.getDimensions(entityPose));
	}

	public final float getStandingEyeHeight() {
		return this.standingEyeHeight;
	}

	public boolean equip(int i, ItemStack itemStack) {
		return false;
	}

	@Override
	public void sendMessage(Text text) {
	}

	public BlockPos getBlockPos() {
		return new BlockPos(this);
	}

	public Vec3d getPosVector() {
		return new Vec3d(this.x, this.y, this.z);
	}

	public World getEntityWorld() {
		return this.world;
	}

	@Nullable
	public MinecraftServer getServer() {
		return this.world.getServer();
	}

	public ActionResult interactAt(PlayerEntity playerEntity, Vec3d vec3d, Hand hand) {
		return ActionResult.PASS;
	}

	public boolean isImmuneToExplosion() {
		return false;
	}

	protected void dealDamage(LivingEntity livingEntity, Entity entity) {
		if (entity instanceof LivingEntity) {
			EnchantmentHelper.onUserDamaged((LivingEntity)entity, livingEntity);
		}

		EnchantmentHelper.onTargetDamaged(livingEntity, entity);
	}

	public void onStartedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
	}

	public void onStoppedTrackingBy(ServerPlayerEntity serverPlayerEntity) {
	}

	public float applyRotation(BlockRotation blockRotation) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (blockRotation) {
			case CLOCKWISE_180:
				return f + 180.0F;
			case COUNTERCLOCKWISE_90:
				return f + 270.0F;
			case CLOCKWISE_90:
				return f + 90.0F;
			default:
				return f;
		}
	}

	public float applyMirror(BlockMirror blockMirror) {
		float f = MathHelper.wrapDegrees(this.yaw);
		switch (blockMirror) {
			case LEFT_RIGHT:
				return -f;
			case FRONT_BACK:
				return 180.0F - f;
			default:
				return f;
		}
	}

	public boolean entityDataRequiresOperator() {
		return false;
	}

	public boolean teleportRequested() {
		boolean bl = this.teleportRequested;
		this.teleportRequested = false;
		return bl;
	}

	@Nullable
	public Entity getPrimaryPassenger() {
		return null;
	}

	public List<Entity> getPassengerList() {
		return (List<Entity>)(this.passengerList.isEmpty() ? Collections.emptyList() : Lists.<Entity>newArrayList(this.passengerList));
	}

	public boolean hasPassenger(Entity entity) {
		for (Entity entity2 : this.getPassengerList()) {
			if (entity2.equals(entity)) {
				return true;
			}
		}

		return false;
	}

	public boolean hasPassengerType(Class<? extends Entity> class_) {
		for (Entity entity : this.getPassengerList()) {
			if (class_.isAssignableFrom(entity.getClass())) {
				return true;
			}
		}

		return false;
	}

	public Collection<Entity> getPassengersDeep() {
		Set<Entity> set = Sets.<Entity>newHashSet();

		for (Entity entity : this.getPassengerList()) {
			set.add(entity);
			entity.collectPassengers(false, set);
		}

		return set;
	}

	public boolean hasPlayerRider() {
		Set<Entity> set = Sets.<Entity>newHashSet();
		this.collectPassengers(true, set);
		return set.size() == 1;
	}

	private void collectPassengers(boolean bl, Set<Entity> set) {
		for (Entity entity : this.getPassengerList()) {
			if (!bl || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
				set.add(entity);
			}

			entity.collectPassengers(bl, set);
		}
	}

	public Entity getRootVehicle() {
		Entity entity = this;

		while (entity.hasVehicle()) {
			entity = entity.getVehicle();
		}

		return entity;
	}

	public boolean isConnectedThroughVehicle(Entity entity) {
		return this.getRootVehicle() == entity.getRootVehicle();
	}

	public boolean hasPassengerDeep(Entity entity) {
		for (Entity entity2 : this.getPassengerList()) {
			if (entity2.equals(entity)) {
				return true;
			}

			if (entity2.hasPassengerDeep(entity)) {
				return true;
			}
		}

		return false;
	}

	public boolean isLogicalSideForUpdatingMovement() {
		Entity entity = this.getPrimaryPassenger();
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).isMainPlayer() : !this.world.isClient;
	}

	@Nullable
	public Entity getVehicle() {
		return this.vehicle;
	}

	public PistonBehavior getPistonBehavior() {
		return PistonBehavior.NORMAL;
	}

	public SoundCategory getSoundCategory() {
		return SoundCategory.NEUTRAL;
	}

	protected int getBurningDuration() {
		return 1;
	}

	public ServerCommandSource getCommandSource() {
		return new ServerCommandSource(
			this,
			new Vec3d(this.x, this.y, this.z),
			this.getRotationClient(),
			this.world instanceof ServerWorld ? (ServerWorld)this.world : null,
			this.getPermissionLevel(),
			this.getName().getString(),
			this.getDisplayName(),
			this.world.getServer(),
			this
		);
	}

	protected int getPermissionLevel() {
		return 0;
	}

	public boolean allowsPermissionLevel(int i) {
		return this.getPermissionLevel() >= i;
	}

	@Override
	public boolean sendCommandFeedback() {
		return this.world.getGameRules().getBoolean(GameRules.SEND_COMMAND_FEEDBACK);
	}

	@Override
	public boolean shouldTrackOutput() {
		return true;
	}

	@Override
	public boolean shouldBroadcastConsoleToOps() {
		return true;
	}

	public void lookAt(EntityAnchorArgumentType.EntityAnchor entityAnchor, Vec3d vec3d) {
		Vec3d vec3d2 = entityAnchor.positionAt(this);
		double d = vec3d.x - vec3d2.x;
		double e = vec3d.y - vec3d2.y;
		double f = vec3d.z - vec3d2.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI)));
		this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F);
		this.setHeadYaw(this.yaw);
		this.prevPitch = this.pitch;
		this.prevYaw = this.yaw;
	}

	public boolean updateMovementInFluid(Tag<Fluid> tag) {
		Box box = this.getBoundingBox().contract(0.001);
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minY);
		int l = MathHelper.ceil(box.maxY);
		int m = MathHelper.floor(box.minZ);
		int n = MathHelper.ceil(box.maxZ);
		if (!this.world.isAreaLoaded(i, k, m, j, l, n)) {
			return false;
		} else {
			double d = 0.0;
			boolean bl = this.canFly();
			boolean bl2 = false;
			Vec3d vec3d = Vec3d.ZERO;
			int o = 0;

			try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get()) {
				for (int p = i; p < j; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							pooledMutable.method_10113(p, q, r);
							FluidState fluidState = this.world.getFluidState(pooledMutable);
							if (fluidState.matches(tag)) {
								double e = (double)((float)q + fluidState.getHeight(this.world, pooledMutable));
								if (e >= box.minY) {
									bl2 = true;
									d = Math.max(e - box.minY, d);
									if (bl) {
										Vec3d vec3d2 = fluidState.getVelocity(this.world, pooledMutable);
										if (d < 0.4) {
											vec3d2 = vec3d2.multiply(d);
										}

										vec3d = vec3d.add(vec3d2);
										o++;
									}
								}
							}
						}
					}
				}
			}

			if (vec3d.length() > 0.0) {
				if (o > 0) {
					vec3d = vec3d.multiply(1.0 / (double)o);
				}

				if (!(this instanceof PlayerEntity)) {
					vec3d = vec3d.normalize();
				}

				this.setVelocity(this.getVelocity().add(vec3d.multiply(0.014)));
			}

			this.waterHeight = d;
			return bl2;
		}
	}

	public double getWaterHeight() {
		return this.waterHeight;
	}

	public final float getWidth() {
		return this.dimensions.width;
	}

	public final float getHeight() {
		return this.dimensions.height;
	}

	public abstract Packet<?> createSpawnPacket();

	public EntityDimensions getDimensions(EntityPose entityPose) {
		return this.type.getDimensions();
	}

	public Vec3d getPos() {
		return new Vec3d(this.x, this.y, this.z);
	}

	public Vec3d getVelocity() {
		return this.velocity;
	}

	public void setVelocity(Vec3d vec3d) {
		this.velocity = vec3d;
	}

	public void setVelocity(double d, double e, double f) {
		this.setVelocity(new Vec3d(d, e, f));
	}
}
