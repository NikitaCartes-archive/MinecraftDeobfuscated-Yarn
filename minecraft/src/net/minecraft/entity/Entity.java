package net.minecraft.entity;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import it.unimi.dsi.fastutil.objects.Object2DoubleArrayMap;
import it.unimi.dsi.fastutil.objects.Object2DoubleMap;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;
import java.util.stream.Stream;
import javax.annotation.Nullable;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.argument.EntityAnchorArgumentType;
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
import net.minecraft.inventory.CommandItemSlot;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtDouble;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtFloat;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
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
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.tag.EntityTypeTags;
import net.minecraft.tag.FluidTags;
import net.minecraft.tag.Tag;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Nameable;
import net.minecraft.util.collection.ReusableStream;
import net.minecraft.util.crash.CrashCallable;
import net.minecraft.util.crash.CrashException;
import net.minecraft.util.crash.CrashReport;
import net.minecraft.util.crash.CrashReportSection;
import net.minecraft.util.function.BooleanBiFunction;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.GameRules;
import net.minecraft.world.Heightmap;
import net.minecraft.world.PortalUtil;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.TeleportTarget;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import net.minecraft.world.WorldView;
import net.minecraft.world.border.WorldBorder;
import net.minecraft.world.dimension.AreaHelper;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.entity.EntityChangeListener;
import net.minecraft.world.entity.EntityLike;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.listener.EntityGameEventHandler;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class Entity implements Nameable, EntityLike, CommandOutput {
	protected static final Logger LOGGER = LogManager.getLogger();
	public static final String ID_KEY = "id";
	public static final String PASSENGERS_KEY = "Passengers";
	private static final AtomicInteger ENTITY_ID_COUNTER = new AtomicInteger();
	private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
	public static final int field_29987 = 60;
	public static final int field_29988 = 300;
	public static final int field_29989 = 1024;
	public static final double field_29990 = 0.5000001;
	public static final float field_29991 = 0.11111111F;
	public static final int field_29992 = 140;
	public static final int field_29993 = 40;
	private static final Box NULL_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
	private static final double field_29984 = 0.014;
	private static final double field_29982 = 0.007;
	private static final double field_29983 = 0.0023333333333333335;
	public static final String UUID_KEY = "UUID";
	private static double renderDistanceMultiplier = 1.0;
	private final EntityType<?> type;
	private int entityId = ENTITY_ID_COUNTER.incrementAndGet();
	public boolean inanimate;
	private ImmutableList<Entity> passengerList = ImmutableList.of();
	protected int ridingCooldown;
	@Nullable
	private Entity vehicle;
	public World world;
	public double prevX;
	public double prevY;
	public double prevZ;
	private Vec3d pos;
	private BlockPos blockPos;
	private Vec3d velocity = Vec3d.ZERO;
	private float yaw;
	private float pitch;
	public float prevYaw;
	public float prevPitch;
	private Box entityBounds = NULL_BOX;
	protected boolean onGround;
	public boolean horizontalCollision;
	public boolean verticalCollision;
	public boolean velocityModified;
	protected Vec3d movementMultiplier = Vec3d.ZERO;
	@Nullable
	private Entity.RemovalReason removalReason;
	public static final float field_29973 = 0.6F;
	public static final float field_29974 = 1.8F;
	public float prevHorizontalSpeed;
	public float horizontalSpeed;
	public float distanceTraveled;
	public float field_28627;
	public float fallDistance;
	private float nextStepSoundDistance = 1.0F;
	public double lastRenderX;
	public double lastRenderY;
	public double lastRenderZ;
	public float stepHeight;
	public boolean noClip;
	protected final Random random = new Random();
	public int age;
	private int fireTicks = -this.getBurningDuration();
	protected boolean touchingWater;
	protected Object2DoubleMap<Tag<Fluid>> fluidHeight = new Object2DoubleArrayMap<>(2);
	protected boolean submergedInWater;
	@Nullable
	protected Tag<Fluid> submergedFluidTag;
	public int timeUntilRegen;
	protected boolean firstUpdate = true;
	protected final DataTracker dataTracker;
	protected static final TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
	protected static final int ON_FIRE_FLAG_INDEX = 0;
	private static final int SNEAKING_FLAG_INDEX = 1;
	private static final int SPRINTING_FLAG_INDEX = 3;
	private static final int SWIMMING_FLAG_INDEX = 4;
	private static final int INVISIBLE_FLAG_INDEX = 5;
	protected static final int GLOWING_FLAG_INDEX = 6;
	protected static final int FALL_FLYING_FLAG_INDEX = 7;
	private static final TrackedData<Integer> AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private static final TrackedData<Optional<Text>> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
	private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
	protected static final TrackedData<EntityPose> POSE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
	private static final TrackedData<Integer> FROZEN_TICKS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
	private EntityChangeListener entityChangeListener = EntityChangeListener.NONE;
	private Vec3d trackedPosition;
	public boolean ignoreCameraFrustum;
	public boolean velocityDirty;
	private int netherPortalCooldown;
	protected boolean inNetherPortal;
	protected int netherPortalTime;
	protected BlockPos lastNetherPortalPosition;
	private boolean invulnerable;
	protected UUID uuid = MathHelper.randomUuid(this.random);
	protected String uuidString = this.uuid.toString();
	private boolean glowing;
	private final Set<String> scoreboardTags = Sets.<String>newHashSet();
	private final double[] pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
	private long pistonMovementTick;
	private EntityDimensions dimensions;
	private float standingEyeHeight;
	public boolean inPowderSnow;
	public boolean wasInPowderSnow;
	public boolean wasOnFire;
	private float field_26997;
	private int prevAge;

	public Entity(EntityType<?> type, World world) {
		this.type = type;
		this.world = world;
		this.dimensions = type.getDimensions();
		this.pos = Vec3d.ZERO;
		this.blockPos = BlockPos.ORIGIN;
		this.trackedPosition = Vec3d.ZERO;
		this.dataTracker = new DataTracker(this);
		this.dataTracker.startTracking(FLAGS, (byte)0);
		this.dataTracker.startTracking(AIR, this.getMaxAir());
		this.dataTracker.startTracking(NAME_VISIBLE, false);
		this.dataTracker.startTracking(CUSTOM_NAME, Optional.empty());
		this.dataTracker.startTracking(SILENT, false);
		this.dataTracker.startTracking(NO_GRAVITY, false);
		this.dataTracker.startTracking(POSE, EntityPose.STANDING);
		this.dataTracker.startTracking(FROZEN_TICKS, 0);
		this.initDataTracker();
		this.setPosition(0.0, 0.0, 0.0);
		this.standingEyeHeight = this.getEyeHeight(EntityPose.STANDING, this.dimensions);
	}

	public boolean collidesWithStateAtPos(BlockPos pos, BlockState state) {
		VoxelShape voxelShape = state.getCollisionShape(this.world, pos, ShapeContext.of(this));
		VoxelShape voxelShape2 = voxelShape.offset((double)pos.getX(), (double)pos.getY(), (double)pos.getZ());
		return VoxelShapes.matchesAnywhere(voxelShape2, VoxelShapes.cuboid(this.getBoundingBox()), BooleanBiFunction.AND);
	}

	public int getTeamColorValue() {
		AbstractTeam abstractTeam = this.getScoreboardTeam();
		return abstractTeam != null && abstractTeam.getColor().getColorValue() != null ? abstractTeam.getColor().getColorValue() : 16777215;
	}

	public boolean isSpectator() {
		return false;
	}

	/**
	 * Removes all the passengers and removes this entity from any vehicles it is riding.
	 */
	public final void detach() {
		if (this.hasPassengers()) {
			this.removeAllPassengers();
		}

		if (this.hasVehicle()) {
			this.stopRiding();
		}
	}

	public void updateTrackedPosition(double x, double y, double z) {
		this.updateTrackedPosition(new Vec3d(x, y, z));
	}

	public void updateTrackedPosition(Vec3d pos) {
		this.trackedPosition = pos;
	}

	public Vec3d getTrackedPosition() {
		return this.trackedPosition;
	}

	public EntityType<?> getType() {
		return this.type;
	}

	@Override
	public int getId() {
		return this.entityId;
	}

	public void setEntityId(int id) {
		this.entityId = id;
	}

	public Set<String> getScoreboardTags() {
		return this.scoreboardTags;
	}

	public boolean addScoreboardTag(String tag) {
		return this.scoreboardTags.size() >= 1024 ? false : this.scoreboardTags.add(tag);
	}

	public boolean removeScoreboardTag(String tag) {
		return this.scoreboardTags.remove(tag);
	}

	public void kill() {
		this.remove(Entity.RemovalReason.KILLED);
	}

	public final void discard() {
		this.remove(Entity.RemovalReason.DISCARDED);
	}

	protected abstract void initDataTracker();

	public DataTracker getDataTracker() {
		return this.dataTracker;
	}

	public boolean equals(Object o) {
		return o instanceof Entity ? ((Entity)o).entityId == this.entityId : false;
	}

	public int hashCode() {
		return this.entityId;
	}

	public void remove(Entity.RemovalReason reason) {
		this.setRemoved(reason);
		if (reason == Entity.RemovalReason.KILLED) {
			this.emitGameEvent(GameEvent.ENTITY_KILLED);
		}
	}

	public void onRemoved() {
	}

	public void setPose(EntityPose pose) {
		this.dataTracker.set(POSE, pose);
	}

	public EntityPose getPose() {
		return this.dataTracker.get(POSE);
	}

	/**
	 * Checks if the distance between this entity and the {@code other} entity is less
	 * than {@code radius}.
	 */
	public boolean isInRange(Entity other, double radius) {
		double d = other.pos.x - this.pos.x;
		double e = other.pos.y - this.pos.y;
		double f = other.pos.z - this.pos.z;
		return d * d + e * e + f * f < radius * radius;
	}

	protected void setRotation(float yaw, float pitch) {
		this.setYaw(yaw % 360.0F);
		this.setPitch(pitch % 360.0F);
	}

	public final void setPosition(Vec3d pos) {
		this.setPosition(pos.getX(), pos.getY(), pos.getZ());
	}

	public void setPosition(double x, double y, double z) {
		this.setPos(x, y, z);
		this.setBoundingBox(this.calculateBoundingBox());
	}

	protected Box calculateBoundingBox() {
		return this.dimensions.getBoxAt(this.pos);
	}

	protected void refreshPosition() {
		this.setPosition(this.pos.x, this.pos.y, this.pos.z);
	}

	public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
		float f = (float)cursorDeltaY * 0.15F;
		float g = (float)cursorDeltaX * 0.15F;
		this.setPitch(this.getPitch() + f);
		this.setYaw(this.getYaw() + g);
		this.setPitch(MathHelper.clamp(this.getPitch(), -90.0F, 90.0F));
		this.prevPitch += f;
		this.prevYaw += g;
		this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0F, 90.0F);
		if (this.vehicle != null) {
			this.vehicle.onPassengerLookAround(this);
		}
	}

	public void tick() {
		this.baseTick();
	}

	public void baseTick() {
		this.world.getProfiler().push("entityBaseTick");
		if (this.hasVehicle() && this.getVehicle().isRemoved()) {
			this.stopRiding();
		}

		if (this.ridingCooldown > 0) {
			this.ridingCooldown--;
		}

		this.prevHorizontalSpeed = this.horizontalSpeed;
		this.prevPitch = this.getPitch();
		this.prevYaw = this.getYaw();
		this.tickNetherPortal();
		if (this.shouldSpawnSprintingParticles()) {
			this.spawnSprintingParticles();
		}

		this.wasInPowderSnow = this.inPowderSnow;
		this.inPowderSnow = false;
		this.updateWaterState();
		this.updateSubmergedInWaterState();
		this.updateSwimming();
		if (this.world.isClient) {
			this.extinguish();
		} else if (this.fireTicks > 0) {
			if (this.isFireImmune()) {
				this.setFireTicks(this.fireTicks - 4);
				if (this.fireTicks < 0) {
					this.extinguish();
				}
			} else {
				if (this.fireTicks % 20 == 0 && !this.isInLava()) {
					this.damage(DamageSource.ON_FIRE, 1.0F);
				}

				this.setFireTicks(this.fireTicks - 1);
			}

			if (this.getFrozenTicks() > 0) {
				this.setFrozenTicks(0);
				this.world.syncWorldEvent(null, WorldEvents.FIRE_EXTINGUISHED, this.blockPos, 1);
			}
		}

		if (this.isInLava()) {
			this.setOnFireFromLava();
			this.fallDistance *= 0.5F;
		}

		this.attemptTickInVoid();
		if (!this.world.isClient) {
			this.setOnFire(this.fireTicks > 0);
		}

		this.firstUpdate = false;
		this.world.getProfiler().pop();
	}

	public void setOnFire(boolean onFire) {
		this.setFlag(ON_FIRE_FLAG_INDEX, onFire);
	}

	/**
	 * Calls {@link #tickInVoid()} when the entity is 64 blocks below the world's {@linkplain net.minecraft.world.HeightLimitView#getBottomY() minimum Y position}.
	 */
	public void attemptTickInVoid() {
		if (this.getY() < (double)(this.world.getBottomY() - 64)) {
			this.tickInVoid();
		}
	}

	public void resetNetherPortalCooldown() {
		this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
	}

	public boolean hasNetherPortalCooldown() {
		return this.netherPortalCooldown > 0;
	}

	protected void tickNetherPortalCooldown() {
		if (this.hasNetherPortalCooldown()) {
			this.netherPortalCooldown--;
		}
	}

	public int getMaxNetherPortalTime() {
		return 0;
	}

	public void setOnFireFromLava() {
		if (!this.isFireImmune()) {
			if (!this.isWet() && !this.inPowderSnow) {
				this.setOnFireFor(15);
			}

			if (this.damage(DamageSource.LAVA, 4.0F)) {
				this.playSound(SoundEvents.ENTITY_GENERIC_BURN, 0.4F, 2.0F + this.random.nextFloat() * 0.4F);
			}
		}
	}

	public void setOnFireFor(int seconds) {
		int i = seconds * 20;
		if (this instanceof LivingEntity) {
			i = ProtectionEnchantment.transformFireDuration((LivingEntity)this, i);
		}

		if (this.fireTicks < i) {
			this.setFireTicks(i);
		}
	}

	public void setFireTicks(int ticks) {
		this.fireTicks = ticks;
	}

	public int getFireTicks() {
		return this.fireTicks;
	}

	public void extinguish() {
		this.setFireTicks(0);
	}

	/**
	 * Called when the entity is 64 blocks below the world's {@linkplain net.minecraft.world.HeightLimitView#getBottomY() minimum Y position}.
	 * 
	 * <p>{@linkplain LivingEntity Living entities} use this to deal {@linkplain net.minecraft.entity.damage.DamageSource#OUT_OF_WORLD out of world damage}.
	 */
	protected void tickInVoid() {
		this.discard();
	}

	public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
		return this.doesNotCollide(this.getBoundingBox().offset(offsetX, offsetY, offsetZ));
	}

	private boolean doesNotCollide(Box box) {
		return this.world.isSpaceEmpty(this, box) && !this.world.containsFluid(box);
	}

	public void setOnGround(boolean onGround) {
		this.onGround = onGround;
	}

	public boolean isOnGround() {
		return this.onGround;
	}

	public void move(MovementType movementType, Vec3d movement) {
		if (this.noClip) {
			this.setPosition(this.getX() + movement.x, this.getY() + movement.y, this.getZ() + movement.z);
		} else {
			this.wasOnFire = this.isOnFire();
			if (movementType == MovementType.PISTON) {
				movement = this.adjustMovementForPiston(movement);
				if (movement.equals(Vec3d.ZERO)) {
					return;
				}
			}

			this.world.getProfiler().push("move");
			if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
				movement = movement.multiply(this.movementMultiplier);
				this.movementMultiplier = Vec3d.ZERO;
				this.setVelocity(Vec3d.ZERO);
			}

			movement = this.adjustMovementForSneaking(movement, movementType);
			Vec3d vec3d = this.adjustMovementForCollisions(movement);
			if (vec3d.lengthSquared() > 1.0E-7) {
				this.setPosition(this.getX() + vec3d.x, this.getY() + vec3d.y, this.getZ() + vec3d.z);
			}

			this.world.getProfiler().pop();
			this.world.getProfiler().push("rest");
			this.horizontalCollision = !MathHelper.approximatelyEquals(movement.x, vec3d.x) || !MathHelper.approximatelyEquals(movement.z, vec3d.z);
			this.verticalCollision = movement.y != vec3d.y;
			this.onGround = this.verticalCollision && movement.y < 0.0;
			BlockPos blockPos = this.getLandingPos();
			BlockState blockState = this.world.getBlockState(blockPos);
			this.fall(vec3d.y, this.onGround, blockState, blockPos);
			if (!this.isRemoved()) {
				Vec3d vec3d2 = this.getVelocity();
				if (movement.x != vec3d.x) {
					this.setVelocity(0.0, vec3d2.y, vec3d2.z);
				}

				if (movement.z != vec3d.z) {
					this.setVelocity(vec3d2.x, vec3d2.y, 0.0);
				}

				Block block = blockState.getBlock();
				if (movement.y != vec3d.y) {
					block.onEntityLand(this.world, this);
				}

				if (this.onGround && !this.bypassesSteppingEffects()) {
					block.onSteppedOn(this.world, blockPos, blockState, this);
				}

				Entity.MoveEffect moveEffect = this.getMoveEffect();
				if (moveEffect.hasAny() && !this.hasVehicle()) {
					double d = vec3d.x;
					double e = vec3d.y;
					double f = vec3d.z;
					this.field_28627 = (float)((double)this.field_28627 + vec3d.length() * 0.6);
					if (!blockState.isIn(BlockTags.CLIMBABLE) && !blockState.isOf(Blocks.POWDER_SNOW)) {
						e = 0.0;
					}

					this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(squaredHorizontalLength(vec3d)) * 0.6);
					this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(d * d + e * e + f * f) * 0.6);
					if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
						this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
						if (this.isTouchingWater()) {
							if (moveEffect.playsSounds()) {
								Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
								float g = entity == this ? 0.35F : 0.4F;
								Vec3d vec3d3 = entity.getVelocity();
								float h = MathHelper.sqrt(vec3d3.x * vec3d3.x * 0.2F + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * 0.2F) * g;
								if (h > 1.0F) {
									h = 1.0F;
								}

								this.playSwimSound(h);
							}

							if (moveEffect.emitsGameEvents()) {
								this.emitGameEvent(GameEvent.SWIM);
							}
						} else {
							if (moveEffect.playsSounds()) {
								this.playStepSound(blockPos, blockState);
							}

							if (moveEffect.emitsGameEvents() && !blockState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
								this.emitGameEvent(GameEvent.STEP);
							}
						}
					} else if (blockState.isAir()) {
						this.addAirTravelEffects();
					}
				}

				try {
					this.checkBlockCollision();
				} catch (Throwable var19) {
					CrashReport crashReport = CrashReport.create(var19, "Checking entity block collision");
					CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
					this.populateCrashReport(crashReportSection);
					throw new CrashException(crashReport);
				}

				float i = this.getVelocityMultiplier();
				this.setVelocity(this.getVelocity().multiply((double)i, 1.0, (double)i));
				if (this.world.getStatesInBoxIfLoaded(this.getBoundingBox().contract(1.0E-6)).noneMatch(state -> state.isIn(BlockTags.FIRE) || state.isOf(Blocks.LAVA))
					&& this.fireTicks <= 0) {
					this.setFireTicks(-this.getBurningDuration());
				}

				if ((this.isWet() || this.inPowderSnow) && this.isOnFire()) {
					if (this.wasOnFire) {
						this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7F, 1.6F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
					}

					this.setFireTicks(-this.getBurningDuration());
				}

				this.world.getProfiler().pop();
			}
		}
	}

	/**
	 * Adds the effects of this entity when it travels in air, usually to the
	 * world the entity is in.
	 * 
	 * <p>This is only called when the entity {@linkplain #getMoveEffect() has
	 * any move effect}, from {@link #move(MovementType, Vec3d)}
	 */
	protected void addAirTravelEffects() {
		if (this.hasWings()) {
			this.addFlapEffects();
			if (this.getMoveEffect().emitsGameEvents()) {
				this.emitGameEvent(GameEvent.FLAP);
			}
		}
	}

	protected BlockPos getLandingPos() {
		int i = MathHelper.floor(this.pos.x);
		int j = MathHelper.floor(this.pos.y - 0.2F);
		int k = MathHelper.floor(this.pos.z);
		BlockPos blockPos = new BlockPos(i, j, k);
		if (this.world.getBlockState(blockPos).isAir()) {
			BlockPos blockPos2 = blockPos.down();
			BlockState blockState = this.world.getBlockState(blockPos2);
			if (blockState.isIn(BlockTags.FENCES) || blockState.isIn(BlockTags.WALLS) || blockState.getBlock() instanceof FenceGateBlock) {
				return blockPos2;
			}
		}

		return blockPos;
	}

	protected float getJumpVelocityMultiplier() {
		float f = this.world.getBlockState(this.getBlockPos()).getBlock().getJumpVelocityMultiplier();
		float g = this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
		return (double)f == 1.0 ? g : f;
	}

	protected float getVelocityMultiplier() {
		BlockState blockState = this.world.getBlockState(this.getBlockPos());
		float f = blockState.getBlock().getVelocityMultiplier();
		if (!blockState.isOf(Blocks.WATER) && !blockState.isOf(Blocks.BUBBLE_COLUMN)) {
			return (double)f == 1.0 ? this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getVelocityMultiplier() : f;
		} else {
			return f;
		}
	}

	protected BlockPos getVelocityAffectingPos() {
		return new BlockPos(this.pos.x, this.getBoundingBox().minY - 0.5000001, this.pos.z);
	}

	protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
		return movement;
	}

	protected Vec3d adjustMovementForPiston(Vec3d movement) {
		if (movement.lengthSquared() <= 1.0E-7) {
			return movement;
		} else {
			long l = this.world.getTime();
			if (l != this.pistonMovementTick) {
				Arrays.fill(this.pistonMovementDelta, 0.0);
				this.pistonMovementTick = l;
			}

			if (movement.x != 0.0) {
				double d = this.calculatePistonMovementFactor(Direction.Axis.X, movement.x);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
			} else if (movement.y != 0.0) {
				double d = this.calculatePistonMovementFactor(Direction.Axis.Y, movement.y);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, d, 0.0);
			} else if (movement.z != 0.0) {
				double d = this.calculatePistonMovementFactor(Direction.Axis.Z, movement.z);
				return Math.abs(d) <= 1.0E-5F ? Vec3d.ZERO : new Vec3d(0.0, 0.0, d);
			} else {
				return Vec3d.ZERO;
			}
		}
	}

	private double calculatePistonMovementFactor(Direction.Axis axis, double offsetFactor) {
		int i = axis.ordinal();
		double d = MathHelper.clamp(offsetFactor + this.pistonMovementDelta[i], -0.51, 0.51);
		offsetFactor = d - this.pistonMovementDelta[i];
		this.pistonMovementDelta[i] = d;
		return offsetFactor;
	}

	private Vec3d adjustMovementForCollisions(Vec3d movement) {
		Box box = this.getBoundingBox();
		ShapeContext shapeContext = ShapeContext.of(this);
		VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
		Stream<VoxelShape> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7)), BooleanBiFunction.AND)
			? Stream.empty()
			: Stream.of(voxelShape);
		Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, box.stretch(movement), entity -> true);
		ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(Stream.concat(stream2, stream));
		Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : adjustMovementForCollisions(this, movement, box, this.world, shapeContext, reusableStream);
		boolean bl = movement.x != vec3d.x;
		boolean bl2 = movement.y != vec3d.y;
		boolean bl3 = movement.z != vec3d.z;
		boolean bl4 = this.onGround || bl2 && movement.y < 0.0;
		if (this.stepHeight > 0.0F && bl4 && (bl || bl3)) {
			Vec3d vec3d2 = adjustMovementForCollisions(this, new Vec3d(movement.x, (double)this.stepHeight, movement.z), box, this.world, shapeContext, reusableStream);
			Vec3d vec3d3 = adjustMovementForCollisions(
				this, new Vec3d(0.0, (double)this.stepHeight, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, shapeContext, reusableStream
			);
			if (vec3d3.y < (double)this.stepHeight) {
				Vec3d vec3d4 = adjustMovementForCollisions(this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.world, shapeContext, reusableStream)
					.add(vec3d3);
				if (squaredHorizontalLength(vec3d4) > squaredHorizontalLength(vec3d2)) {
					vec3d2 = vec3d4;
				}
			}

			if (squaredHorizontalLength(vec3d2) > squaredHorizontalLength(vec3d)) {
				return vec3d2.add(
					adjustMovementForCollisions(this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.world, shapeContext, reusableStream)
				);
			}
		}

		return vec3d;
	}

	public static double squaredHorizontalLength(Vec3d vector) {
		return vector.x * vector.x + vector.z * vector.z;
	}

	public static Vec3d adjustMovementForCollisions(
		@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, ShapeContext context, ReusableStream<VoxelShape> collisions
	) {
		boolean bl = movement.x == 0.0;
		boolean bl2 = movement.y == 0.0;
		boolean bl3 = movement.z == 0.0;
		if ((!bl || !bl2) && (!bl || !bl3) && (!bl2 || !bl3)) {
			ReusableStream<VoxelShape> reusableStream = new ReusableStream<>(
				Stream.concat(collisions.stream(), world.getBlockCollisions(entity, entityBoundingBox.stretch(movement)))
			);
			return adjustMovementForCollisions(movement, entityBoundingBox, reusableStream);
		} else {
			return adjustSingleAxisMovementForCollisions(movement, entityBoundingBox, world, context, collisions);
		}
	}

	public static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, ReusableStream<VoxelShape> collisions) {
		double d = movement.x;
		double e = movement.y;
		double f = movement.z;
		if (e != 0.0) {
			e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions.stream(), e);
			if (e != 0.0) {
				entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions.stream(), f);
			if (f != 0.0) {
				entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = VoxelShapes.calculateMaxOffset(Direction.Axis.X, entityBoundingBox, collisions.stream(), d);
			if (!bl && d != 0.0) {
				entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions.stream(), f);
		}

		return new Vec3d(d, e, f);
	}

	public static Vec3d adjustSingleAxisMovementForCollisions(
		Vec3d movement, Box entityBoundingBox, WorldView world, ShapeContext context, ReusableStream<VoxelShape> collisions
	) {
		double d = movement.x;
		double e = movement.y;
		double f = movement.z;
		if (e != 0.0) {
			e = VoxelShapes.calculatePushVelocity(Direction.Axis.Y, entityBoundingBox, world, e, context, collisions.stream());
			if (e != 0.0) {
				entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
			}
		}

		boolean bl = Math.abs(d) < Math.abs(f);
		if (bl && f != 0.0) {
			f = VoxelShapes.calculatePushVelocity(Direction.Axis.Z, entityBoundingBox, world, f, context, collisions.stream());
			if (f != 0.0) {
				entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
			}
		}

		if (d != 0.0) {
			d = VoxelShapes.calculatePushVelocity(Direction.Axis.X, entityBoundingBox, world, d, context, collisions.stream());
			if (!bl && d != 0.0) {
				entityBoundingBox = entityBoundingBox.offset(d, 0.0, 0.0);
			}
		}

		if (!bl && f != 0.0) {
			f = VoxelShapes.calculatePushVelocity(Direction.Axis.Z, entityBoundingBox, world, f, context, collisions.stream());
		}

		return new Vec3d(d, e, f);
	}

	protected float calculateNextStepSoundDistance() {
		return (float)((int)this.distanceTraveled + 1);
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
		BlockPos blockPos = new BlockPos(box.minX + 0.001, box.minY + 0.001, box.minZ + 0.001);
		BlockPos blockPos2 = new BlockPos(box.maxX - 0.001, box.maxY - 0.001, box.maxZ - 0.001);
		if (this.world.isRegionLoaded(blockPos, blockPos2)) {
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int i = blockPos.getX(); i <= blockPos2.getX(); i++) {
				for (int j = blockPos.getY(); j <= blockPos2.getY(); j++) {
					for (int k = blockPos.getZ(); k <= blockPos2.getZ(); k++) {
						mutable.set(i, j, k);
						BlockState blockState = this.world.getBlockState(mutable);

						try {
							blockState.onEntityCollision(this.world, mutable, this);
							this.onBlockCollision(blockState);
						} catch (Throwable var12) {
							CrashReport crashReport = CrashReport.create(var12, "Colliding entity with block");
							CrashReportSection crashReportSection = crashReport.addElement("Block being collided with");
							CrashReportSection.addBlockInfo(crashReportSection, this.world, mutable, blockState);
							throw new CrashException(crashReport);
						}
					}
				}
			}
		}
	}

	protected void onBlockCollision(BlockState state) {
	}

	public void emitGameEvent(GameEvent event, @Nullable Entity entity, BlockPos pos) {
		this.world.emitGameEvent(entity, event, pos);
	}

	public void emitGameEvent(GameEvent event, @Nullable Entity entity) {
		this.emitGameEvent(event, entity, this.blockPos);
	}

	public void emitGameEvent(GameEvent event, BlockPos pos) {
		this.emitGameEvent(event, this, pos);
	}

	public void emitGameEvent(GameEvent event) {
		this.emitGameEvent(event, this.blockPos);
	}

	protected void playStepSound(BlockPos pos, BlockState state) {
		if (!state.getMaterial().isLiquid()) {
			if (state.isIn(BlockTags.CRYSTAL_SOUND_BLOCKS) && this.age >= this.prevAge + 20) {
				this.field_26997 = (float)((double)this.field_26997 * Math.pow(0.997F, (double)(this.age - this.prevAge)));
				this.field_26997 = Math.min(1.0F, this.field_26997 + 0.07F);
				float f = 0.5F + this.field_26997 * this.random.nextFloat() * 1.2F;
				float g = 0.1F + this.field_26997 * 1.2F;
				this.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, g, f);
				this.prevAge = this.age;
			}

			BlockState blockState = this.world.getBlockState(pos.up());
			BlockSoundGroup blockSoundGroup = blockState.isIn(BlockTags.INSIDE_STEP_SOUND_BLOCKS) ? blockState.getSoundGroup() : state.getSoundGroup();
			this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15F, blockSoundGroup.getPitch());
		}
	}

	protected void playSwimSound(float volume) {
		this.playSound(this.getSwimSound(), volume, 1.0F + (this.random.nextFloat() - this.random.nextFloat()) * 0.4F);
	}

	/**
	 * Adds the effects of this entity flapping, usually to the world the entity
	 * is in.
	 * 
	 * <p>The actual flapping logic should be done in {@link #tick()} instead.
	 * 
	 * <p>This is only called when the entity {@linkplain #hasWings() has wings}
	 * and the entity {@linkplain #getMoveEffect() has any move effect}, from
	 * {@link #addAirTravelEffects()}.
	 */
	protected void addFlapEffects() {
	}

	protected boolean hasWings() {
		return false;
	}

	public void playSound(SoundEvent sound, float volume, float pitch) {
		if (!this.isSilent()) {
			this.world.playSound(null, this.getX(), this.getY(), this.getZ(), sound, this.getSoundCategory(), volume, pitch);
		}
	}

	public boolean isSilent() {
		return this.dataTracker.get(SILENT);
	}

	public void setSilent(boolean silent) {
		this.dataTracker.set(SILENT, silent);
	}

	public boolean hasNoGravity() {
		return this.dataTracker.get(NO_GRAVITY);
	}

	public void setNoGravity(boolean noGravity) {
		this.dataTracker.set(NO_GRAVITY, noGravity);
	}

	/**
	 * Returns the possible effect(s) of an entity moving.
	 * 
	 * @implNote If an entity does not emit game events or play move sounds, this
	 * method should be overridden as returning a value other than
	 * {@linkplain Entity.MoveEffect#ALL ALL} allows skipping some movement logic
	 * and boost ticking performance.
	 */
	protected Entity.MoveEffect getMoveEffect() {
		return Entity.MoveEffect.ALL;
	}

	public boolean occludeVibrationSignals() {
		return false;
	}

	protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
		if (onGround) {
			if (this.fallDistance > 0.0F) {
				landedState.getBlock().onLandedUpon(this.world, landedState, landedPosition, this, this.fallDistance);
				if (!landedState.isIn(BlockTags.OCCLUDES_VIBRATION_SIGNALS)) {
					this.emitGameEvent(GameEvent.HIT_GROUND);
				}
			}

			this.fallDistance = 0.0F;
		} else if (heightDifference < 0.0) {
			this.fallDistance = (float)((double)this.fallDistance - heightDifference);
		}
	}

	public boolean isFireImmune() {
		return this.getType().isFireImmune();
	}

	public boolean handleFallDamage(float fallDistance, float damageMultiplier, DamageSource damageSource) {
		if (this.hasPassengers()) {
			for (Entity entity : this.getPassengerList()) {
				entity.handleFallDamage(fallDistance, damageMultiplier, damageSource);
			}
		}

		return false;
	}

	/**
	 * Returns whether this entity's hitbox is touching water fluid.
	 */
	public boolean isTouchingWater() {
		return this.touchingWater;
	}

	private boolean isBeingRainedOn() {
		BlockPos blockPos = this.getBlockPos();
		return this.world.hasRain(blockPos) || this.world.hasRain(new BlockPos((double)blockPos.getX(), this.getBoundingBox().maxY, (double)blockPos.getZ()));
	}

	private boolean isInsideBubbleColumn() {
		return this.world.getBlockState(this.getBlockPos()).isOf(Blocks.BUBBLE_COLUMN);
	}

	public boolean isTouchingWaterOrRain() {
		return this.isTouchingWater() || this.isBeingRainedOn();
	}

	/**
	 * Returns whether this entity is touching water, or is being rained on, or is inside a bubble column...
	 * 
	 * @see net.minecraft.entity.Entity#isTouchingWater()
	 * @see net.minecraft.entity.Entity#isBeingRainedOn()
	 * @see net.minecraft.entity.Entity#isInsideBubbleColumn()
	 */
	public boolean isWet() {
		return this.isTouchingWater() || this.isBeingRainedOn() || this.isInsideBubbleColumn();
	}

	public boolean isInsideWaterOrBubbleColumn() {
		return this.isTouchingWater() || this.isInsideBubbleColumn();
	}

	/**
	 * Returns whether this entity's hitbox is fully submerged in water.
	 */
	public boolean isSubmergedInWater() {
		return this.submergedInWater && this.isTouchingWater();
	}

	public void updateSwimming() {
		if (this.isSwimming()) {
			this.setSwimming(this.isSprinting() && this.isTouchingWater() && !this.hasVehicle());
		} else {
			this.setSwimming(this.isSprinting() && this.isSubmergedInWater() && !this.hasVehicle());
		}
	}

	protected boolean updateWaterState() {
		this.fluidHeight.clear();
		this.checkWaterState();
		double d = this.world.getDimension().isUltrawarm() ? 0.007 : 0.0023333333333333335;
		boolean bl = this.updateMovementInFluid(FluidTags.LAVA, d);
		return this.isTouchingWater() || bl;
	}

	void checkWaterState() {
		if (this.getVehicle() instanceof BoatEntity) {
			this.touchingWater = false;
		} else if (this.updateMovementInFluid(FluidTags.WATER, 0.014)) {
			if (!this.touchingWater && !this.firstUpdate) {
				this.onSwimmingStart();
			}

			this.fallDistance = 0.0F;
			this.touchingWater = true;
			this.extinguish();
		} else {
			this.touchingWater = false;
		}
	}

	private void updateSubmergedInWaterState() {
		this.submergedInWater = this.isSubmergedIn(FluidTags.WATER);
		this.submergedFluidTag = null;
		double d = this.getEyeY() - 0.11111111F;
		if (this.getVehicle() instanceof BoatEntity boatEntity
			&& !boatEntity.isSubmergedInWater()
			&& boatEntity.getBoundingBox().maxY >= d
			&& boatEntity.getBoundingBox().minY <= d) {
			return;
		}

		BlockPos blockPos = new BlockPos(this.getX(), d, this.getZ());
		FluidState fluidState = this.world.getFluidState(blockPos);

		for (Tag<Fluid> tag : FluidTags.getTags()) {
			if (fluidState.isIn(tag)) {
				double e = (double)((float)blockPos.getY() + fluidState.getHeight(this.world, blockPos));
				if (e > d) {
					this.submergedFluidTag = tag;
				}

				return;
			}
		}
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

		float h = (float)MathHelper.floor(this.getY());

		for (int i = 0; (float)i < 1.0F + this.dimensions.width * 20.0F; i++) {
			double d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
			double e = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
			this.world
				.addParticle(ParticleTypes.BUBBLE, this.getX() + d, (double)(h + 1.0F), this.getZ() + e, vec3d.x, vec3d.y - this.random.nextDouble() * 0.2F, vec3d.z);
		}

		for (int i = 0; (float)i < 1.0F + this.dimensions.width * 20.0F; i++) {
			double d = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
			double e = (this.random.nextDouble() * 2.0 - 1.0) * (double)this.dimensions.width;
			this.world.addParticle(ParticleTypes.SPLASH, this.getX() + d, (double)(h + 1.0F), this.getZ() + e, vec3d.x, vec3d.y, vec3d.z);
		}

		this.emitGameEvent(GameEvent.SPLASH);
	}

	protected BlockState getLandingBlockState() {
		return this.world.getBlockState(this.getLandingPos());
	}

	public boolean shouldSpawnSprintingParticles() {
		return this.isSprinting() && !this.isTouchingWater() && !this.isSpectator() && !this.isInSneakingPose() && !this.isInLava() && this.isAlive();
	}

	protected void spawnSprintingParticles() {
		int i = MathHelper.floor(this.getX());
		int j = MathHelper.floor(this.getY() - 0.2F);
		int k = MathHelper.floor(this.getZ());
		BlockPos blockPos = new BlockPos(i, j, k);
		BlockState blockState = this.world.getBlockState(blockPos);
		if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
			Vec3d vec3d = this.getVelocity();
			this.world
				.addParticle(
					new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState),
					this.getX() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width,
					this.getY() + 0.1,
					this.getZ() + (this.random.nextDouble() - 0.5) * (double)this.dimensions.width,
					vec3d.x * -4.0,
					1.5,
					vec3d.z * -4.0
				);
		}
	}

	public boolean isSubmergedIn(Tag<Fluid> fluidTag) {
		return this.submergedFluidTag == fluidTag;
	}

	public boolean isInLava() {
		return !this.firstUpdate && this.fluidHeight.getDouble(FluidTags.LAVA) > 0.0;
	}

	public void updateVelocity(float speed, Vec3d movementInput) {
		Vec3d vec3d = movementInputToVelocity(movementInput, speed, this.getYaw());
		this.setVelocity(this.getVelocity().add(vec3d));
	}

	private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
		double d = movementInput.lengthSquared();
		if (d < 1.0E-7) {
			return Vec3d.ZERO;
		} else {
			Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply((double)speed);
			float f = MathHelper.sin(yaw * (float) (Math.PI / 180.0));
			float g = MathHelper.cos(yaw * (float) (Math.PI / 180.0));
			return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
		}
	}

	public float getBrightnessAtEyes() {
		return this.world.isPosLoaded(this.getBlockX(), this.getBlockZ()) ? this.world.getBrightness(new BlockPos(this.getX(), this.getEyeY(), this.getZ())) : 0.0F;
	}

	public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.updatePosition(x, y, z);
		this.setYaw(yaw % 360.0F);
		this.setPitch(MathHelper.clamp(pitch, -90.0F, 90.0F) % 360.0F);
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
	}

	public void updatePosition(double x, double y, double z) {
		double d = MathHelper.clamp(x, -3.0E7, 3.0E7);
		double e = MathHelper.clamp(z, -3.0E7, 3.0E7);
		this.prevX = d;
		this.prevY = y;
		this.prevZ = e;
		this.setPosition(d, y, e);
	}

	public void refreshPositionAfterTeleport(Vec3d pos) {
		this.refreshPositionAfterTeleport(pos.x, pos.y, pos.z);
	}

	public void refreshPositionAfterTeleport(double x, double y, double z) {
		this.refreshPositionAndAngles(x, y, z, this.getYaw(), this.getPitch());
	}

	public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
		this.refreshPositionAndAngles((double)pos.getX() + 0.5, (double)pos.getY(), (double)pos.getZ() + 0.5, yaw, pitch);
	}

	public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
		this.setPos(x, y, z);
		this.setYaw(yaw);
		this.setPitch(pitch);
		this.resetPosition();
		this.refreshPosition();
	}

	public final void resetPosition() {
		double d = this.getX();
		double e = this.getY();
		double f = this.getZ();
		this.prevX = d;
		this.prevY = e;
		this.prevZ = f;
		this.lastRenderX = d;
		this.lastRenderY = e;
		this.lastRenderZ = f;
		this.prevYaw = this.getYaw();
		this.prevPitch = this.getPitch();
	}

	public float distanceTo(Entity entity) {
		float f = (float)(this.getX() - entity.getX());
		float g = (float)(this.getY() - entity.getY());
		float h = (float)(this.getZ() - entity.getZ());
		return MathHelper.sqrt(f * f + g * g + h * h);
	}

	public double squaredDistanceTo(double x, double y, double z) {
		double d = this.getX() - x;
		double e = this.getY() - y;
		double f = this.getZ() - z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceTo(Entity entity) {
		return this.squaredDistanceTo(entity.getPos());
	}

	public double squaredDistanceTo(Vec3d vector) {
		double d = this.getX() - vector.x;
		double e = this.getY() - vector.y;
		double f = this.getZ() - vector.z;
		return d * d + e * e + f * f;
	}

	public void onPlayerCollision(PlayerEntity player) {
	}

	public void pushAwayFrom(Entity entity) {
		if (!this.isConnectedThroughVehicle(entity)) {
			if (!entity.noClip && !this.noClip) {
				double d = entity.getX() - this.getX();
				double e = entity.getZ() - this.getZ();
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

	public void addVelocity(double deltaX, double deltaY, double deltaZ) {
		this.setVelocity(this.getVelocity().add(deltaX, deltaY, deltaZ));
		this.velocityDirty = true;
	}

	protected void scheduleVelocityUpdate() {
		this.velocityModified = true;
	}

	public boolean damage(DamageSource source, float amount) {
		if (this.isInvulnerableTo(source)) {
			return false;
		} else {
			this.scheduleVelocityUpdate();
			return true;
		}
	}

	public final Vec3d getRotationVec(float tickDelta) {
		return this.getRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
	}

	public float getPitch(float tickDelta) {
		return tickDelta == 1.0F ? this.getPitch() : MathHelper.lerp(tickDelta, this.prevPitch, this.getPitch());
	}

	public float getYaw(float tickDelta) {
		return tickDelta == 1.0F ? this.getYaw() : MathHelper.lerp(tickDelta, this.prevYaw, this.getYaw());
	}

	protected final Vec3d getRotationVector(float pitch, float yaw) {
		float f = pitch * (float) (Math.PI / 180.0);
		float g = -yaw * (float) (Math.PI / 180.0);
		float h = MathHelper.cos(g);
		float i = MathHelper.sin(g);
		float j = MathHelper.cos(f);
		float k = MathHelper.sin(f);
		return new Vec3d((double)(i * j), (double)(-k), (double)(h * j));
	}

	public final Vec3d getOppositeRotationVector(float tickDelta) {
		return this.getOppositeRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
	}

	protected final Vec3d getOppositeRotationVector(float pitch, float yaw) {
		return this.getRotationVector(pitch - 90.0F, yaw);
	}

	public final Vec3d getEyePos() {
		return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
	}

	public final Vec3d getCameraPosVec(float tickDelta) {
		double d = MathHelper.lerp((double)tickDelta, this.prevX, this.getX());
		double e = MathHelper.lerp((double)tickDelta, this.prevY, this.getY()) + (double)this.getStandingEyeHeight();
		double f = MathHelper.lerp((double)tickDelta, this.prevZ, this.getZ());
		return new Vec3d(d, e, f);
	}

	public Vec3d getClientCameraPosVec(float tickDelta) {
		return this.getCameraPosVec(tickDelta);
	}

	public final Vec3d getLerpedPos(float delta) {
		double d = MathHelper.lerp((double)delta, this.prevX, this.getX());
		double e = MathHelper.lerp((double)delta, this.prevY, this.getY());
		double f = MathHelper.lerp((double)delta, this.prevZ, this.getZ());
		return new Vec3d(d, e, f);
	}

	public HitResult raycast(double maxDistance, float tickDelta, boolean includeFluids) {
		Vec3d vec3d = this.getCameraPosVec(tickDelta);
		Vec3d vec3d2 = this.getRotationVec(tickDelta);
		Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
		return this.world
			.raycast(
				new RaycastContext(
					vec3d, vec3d3, RaycastContext.ShapeType.OUTLINE, includeFluids ? RaycastContext.FluidHandling.ANY : RaycastContext.FluidHandling.NONE, this
				)
			);
	}

	public boolean collides() {
		return false;
	}

	public boolean isPushable() {
		return false;
	}

	public void updateKilledAdvancementCriterion(Entity killer, int score, DamageSource damageSource) {
		if (killer instanceof ServerPlayerEntity) {
			Criteria.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)killer, this, damageSource);
		}
	}

	public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
		double d = this.getX() - cameraX;
		double e = this.getY() - cameraY;
		double f = this.getZ() - cameraZ;
		double g = d * d + e * e + f * f;
		return this.shouldRender(g);
	}

	public boolean shouldRender(double distance) {
		double d = this.getBoundingBox().getAverageSideLength();
		if (Double.isNaN(d)) {
			d = 1.0;
		}

		d *= 64.0 * renderDistanceMultiplier;
		return distance < d * d;
	}

	public boolean saveSelfNbt(NbtCompound nbt) {
		if (this.removalReason != null && !this.removalReason.shouldSave()) {
			return false;
		} else {
			String string = this.getSavedEntityId();
			if (string == null) {
				return false;
			} else {
				nbt.putString("id", string);
				this.writeNbt(nbt);
				return true;
			}
		}
	}

	public boolean saveNbt(NbtCompound nbt) {
		return this.hasVehicle() ? false : this.saveSelfNbt(nbt);
	}

	public NbtCompound writeNbt(NbtCompound nbt) {
		try {
			if (this.vehicle != null) {
				nbt.put("Pos", this.toNbtList(this.vehicle.getX(), this.getY(), this.vehicle.getZ()));
			} else {
				nbt.put("Pos", this.toNbtList(this.getX(), this.getY(), this.getZ()));
			}

			Vec3d vec3d = this.getVelocity();
			nbt.put("Motion", this.toNbtList(vec3d.x, vec3d.y, vec3d.z));
			nbt.put("Rotation", this.toNbtList(this.getYaw(), this.getPitch()));
			nbt.putFloat("FallDistance", this.fallDistance);
			nbt.putShort("Fire", (short)this.fireTicks);
			nbt.putShort("Air", (short)this.getAir());
			nbt.putBoolean("OnGround", this.onGround);
			nbt.putBoolean("Invulnerable", this.invulnerable);
			nbt.putInt("PortalCooldown", this.netherPortalCooldown);
			nbt.putUuid("UUID", this.getUuid());
			Text text = this.getCustomName();
			if (text != null) {
				nbt.putString("CustomName", Text.Serializer.toJson(text));
			}

			if (this.isCustomNameVisible()) {
				nbt.putBoolean("CustomNameVisible", this.isCustomNameVisible());
			}

			if (this.isSilent()) {
				nbt.putBoolean("Silent", this.isSilent());
			}

			if (this.hasNoGravity()) {
				nbt.putBoolean("NoGravity", this.hasNoGravity());
			}

			if (this.glowing) {
				nbt.putBoolean("Glowing", true);
			}

			int i = this.getFrozenTicks();
			if (i > 0) {
				nbt.putInt("TicksFrozen", this.getFrozenTicks());
			}

			if (!this.scoreboardTags.isEmpty()) {
				NbtList nbtList = new NbtList();

				for (String string : this.scoreboardTags) {
					nbtList.add(NbtString.of(string));
				}

				nbt.put("Tags", nbtList);
			}

			this.writeCustomDataToNbt(nbt);
			if (this.hasPassengers()) {
				NbtList nbtList = new NbtList();

				for (Entity entity : this.getPassengerList()) {
					NbtCompound nbtCompound = new NbtCompound();
					if (entity.saveSelfNbt(nbtCompound)) {
						nbtList.add(nbtCompound);
					}
				}

				if (!nbtList.isEmpty()) {
					nbt.put("Passengers", nbtList);
				}
			}

			return nbt;
		} catch (Throwable var9) {
			CrashReport crashReport = CrashReport.create(var9, "Saving entity NBT");
			CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
			this.populateCrashReport(crashReportSection);
			throw new CrashException(crashReport);
		}
	}

	public void readNbt(NbtCompound nbt) {
		try {
			NbtList nbtList = nbt.getList("Pos", NbtElement.DOUBLE_TYPE);
			NbtList nbtList2 = nbt.getList("Motion", NbtElement.DOUBLE_TYPE);
			NbtList nbtList3 = nbt.getList("Rotation", NbtElement.FLOAT_TYPE);
			double d = nbtList2.getDouble(0);
			double e = nbtList2.getDouble(1);
			double f = nbtList2.getDouble(2);
			this.setVelocity(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
			this.setPos(nbtList.getDouble(0), nbtList.getDouble(1), nbtList.getDouble(2));
			this.setYaw(nbtList3.getFloat(0));
			this.setPitch(nbtList3.getFloat(1));
			this.resetPosition();
			this.setHeadYaw(this.getYaw());
			this.setBodyYaw(this.getYaw());
			this.fallDistance = nbt.getFloat("FallDistance");
			this.fireTicks = nbt.getShort("Fire");
			if (nbt.contains("Air")) {
				this.setAir(nbt.getShort("Air"));
			}

			this.onGround = nbt.getBoolean("OnGround");
			this.invulnerable = nbt.getBoolean("Invulnerable");
			this.netherPortalCooldown = nbt.getInt("PortalCooldown");
			if (nbt.containsUuid("UUID")) {
				this.uuid = nbt.getUuid("UUID");
				this.uuidString = this.uuid.toString();
			}

			if (!Double.isFinite(this.getX()) || !Double.isFinite(this.getY()) || !Double.isFinite(this.getZ())) {
				throw new IllegalStateException("Entity has invalid position");
			} else if (Double.isFinite((double)this.getYaw()) && Double.isFinite((double)this.getPitch())) {
				this.refreshPosition();
				this.setRotation(this.getYaw(), this.getPitch());
				if (nbt.contains("CustomName", NbtElement.STRING_TYPE)) {
					String string = nbt.getString("CustomName");

					try {
						this.setCustomName(Text.Serializer.fromJson(string));
					} catch (Exception var14) {
						LOGGER.warn("Failed to parse entity custom name {}", string, var14);
					}
				}

				this.setCustomNameVisible(nbt.getBoolean("CustomNameVisible"));
				this.setSilent(nbt.getBoolean("Silent"));
				this.setNoGravity(nbt.getBoolean("NoGravity"));
				this.setGlowing(nbt.getBoolean("Glowing"));
				this.setFrozenTicks(nbt.getInt("TicksFrozen"));
				if (nbt.contains("Tags", NbtElement.LIST_TYPE)) {
					this.scoreboardTags.clear();
					NbtList nbtList4 = nbt.getList("Tags", NbtElement.STRING_TYPE);
					int i = Math.min(nbtList4.size(), 1024);

					for (int j = 0; j < i; j++) {
						this.scoreboardTags.add(nbtList4.getString(j));
					}
				}

				this.readCustomDataFromNbt(nbt);
				if (this.shouldSetPositionOnLoad()) {
					this.refreshPosition();
				}
			} else {
				throw new IllegalStateException("Entity has invalid rotation");
			}
		} catch (Throwable var15) {
			CrashReport crashReport = CrashReport.create(var15, "Loading entity NBT");
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

	protected abstract void readCustomDataFromNbt(NbtCompound nbt);

	protected abstract void writeCustomDataToNbt(NbtCompound nbt);

	protected NbtList toNbtList(double... values) {
		NbtList nbtList = new NbtList();

		for (double d : values) {
			nbtList.add(NbtDouble.of(d));
		}

		return nbtList;
	}

	protected NbtList toNbtList(float... values) {
		NbtList nbtList = new NbtList();

		for (float f : values) {
			nbtList.add(NbtFloat.of(f));
		}

		return nbtList;
	}

	@Nullable
	public ItemEntity dropItem(ItemConvertible item) {
		return this.dropItem(item, 0);
	}

	@Nullable
	public ItemEntity dropItem(ItemConvertible item, int yOffset) {
		return this.dropStack(new ItemStack(item), (float)yOffset);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack stack) {
		return this.dropStack(stack, 0.0F);
	}

	@Nullable
	public ItemEntity dropStack(ItemStack stack, float yOffset) {
		if (stack.isEmpty()) {
			return null;
		} else if (this.world.isClient) {
			return null;
		} else {
			ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY() + (double)yOffset, this.getZ(), stack);
			itemEntity.setToDefaultPickupDelay();
			this.world.spawnEntity(itemEntity);
			return itemEntity;
		}
	}

	public boolean isAlive() {
		return !this.isRemoved();
	}

	public boolean isInsideWall() {
		if (this.noClip) {
			return false;
		} else {
			float f = this.dimensions.width * 0.8F;
			Box box = Box.of(this.getEyePos(), (double)f, 1.0E-6, (double)f);
			return this.world.getBlockCollisions(this, box, (state, pos) -> state.shouldSuffocate(this.world, pos)).findAny().isPresent();
		}
	}

	/**
	 * Called when a player interacts with this entity.
	 * 
	 * @param player the player
	 * @param hand the hand the player used to interact with this entity
	 */
	public ActionResult interact(PlayerEntity player, Hand hand) {
		return ActionResult.PASS;
	}

	public boolean collidesWith(Entity other) {
		return other.isCollidable() && !this.isConnectedThroughVehicle(other);
	}

	public boolean isCollidable() {
		return false;
	}

	public void tickRiding() {
		this.setVelocity(Vec3d.ZERO);
		this.tick();
		if (this.hasVehicle()) {
			this.getVehicle().updatePassengerPosition(this);
		}
	}

	public void updatePassengerPosition(Entity passenger) {
		this.updatePassengerPosition(passenger, Entity::setPosition);
	}

	private void updatePassengerPosition(Entity passenger, Entity.PositionUpdater positionUpdater) {
		if (this.hasPassenger(passenger)) {
			double d = this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset();
			positionUpdater.accept(passenger, this.getX(), d, this.getZ());
		}
	}

	public void onPassengerLookAround(Entity passenger) {
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

	public boolean isLiving() {
		return this instanceof LivingEntity;
	}

	public boolean startRiding(Entity entity, boolean force) {
		for (Entity entity2 = entity; entity2.vehicle != null; entity2 = entity2.vehicle) {
			if (entity2.vehicle == this) {
				return false;
			}
		}

		if (force || this.canStartRiding(entity) && entity.canAddPassenger(this)) {
			if (this.hasVehicle()) {
				this.stopRiding();
			}

			this.setPose(EntityPose.STANDING);
			this.vehicle = entity;
			this.vehicle.addPassenger(this);
			return true;
		} else {
			return false;
		}
	}

	protected boolean canStartRiding(Entity entity) {
		return !this.isSneaking() && this.ridingCooldown <= 0;
	}

	protected boolean wouldPoseNotCollide(EntityPose pose) {
		return this.world.isSpaceEmpty(this, this.calculateBoundsForPose(pose).contract(1.0E-7));
	}

	public void removeAllPassengers() {
		for (int i = this.passengerList.size() - 1; i >= 0; i--) {
			((Entity)this.passengerList.get(i)).stopRiding();
		}
	}

	/**
	 * Dismounts the vehicle if present.
	 * <p>
	 * For players, will not trigger any networking changes. Use {@link #stopRiding()} instead.
	 * 
	 * @see #stopRiding()
	 */
	public void dismountVehicle() {
		if (this.vehicle != null) {
			Entity entity = this.vehicle;
			this.vehicle = null;
			entity.removePassenger(this);
		}
	}

	public void stopRiding() {
		this.dismountVehicle();
	}

	protected void addPassenger(Entity passenger) {
		if (passenger.getVehicle() != this) {
			throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
		} else {
			if (this.passengerList.isEmpty()) {
				this.passengerList = ImmutableList.of(passenger);
			} else {
				List<Entity> list = Lists.<Entity>newArrayList(this.passengerList);
				if (!this.world.isClient && passenger instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
					list.add(0, passenger);
				} else {
					list.add(passenger);
				}

				this.passengerList = ImmutableList.copyOf(list);
			}
		}
	}

	protected void removePassenger(Entity passenger) {
		if (passenger.getVehicle() == this) {
			throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
		} else {
			if (this.passengerList.size() == 1 && this.passengerList.get(0) == passenger) {
				this.passengerList = ImmutableList.of();
			} else {
				this.passengerList = (ImmutableList<Entity>)this.passengerList.stream().filter(entity2 -> entity2 != passenger).collect(ImmutableList.toImmutableList());
			}

			passenger.ridingCooldown = 60;
		}
	}

	protected boolean canAddPassenger(Entity passenger) {
		return this.passengerList.isEmpty();
	}

	public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
		this.setPosition(x, y, z);
		this.setRotation(yaw, pitch);
	}

	public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
		this.setHeadYaw(yaw);
	}

	public float getTargetingMargin() {
		return 0.0F;
	}

	public Vec3d getRotationVector() {
		return this.getRotationVector(this.getPitch(), this.getYaw());
	}

	public Vec2f getRotationClient() {
		return new Vec2f(this.getPitch(), this.getYaw());
	}

	public Vec3d getRotationVecClient() {
		return Vec3d.fromPolar(this.getRotationClient());
	}

	public void setInNetherPortal(BlockPos pos) {
		if (this.hasNetherPortalCooldown()) {
			this.resetNetherPortalCooldown();
		} else {
			if (!this.world.isClient && !pos.equals(this.lastNetherPortalPosition)) {
				this.lastNetherPortalPosition = pos.toImmutable();
			}

			this.inNetherPortal = true;
		}
	}

	protected void tickNetherPortal() {
		if (this.world instanceof ServerWorld) {
			int i = this.getMaxNetherPortalTime();
			ServerWorld serverWorld = (ServerWorld)this.world;
			if (this.inNetherPortal) {
				MinecraftServer minecraftServer = serverWorld.getServer();
				RegistryKey<World> registryKey = this.world.getRegistryKey() == World.NETHER ? World.OVERWORLD : World.NETHER;
				ServerWorld serverWorld2 = minecraftServer.getWorld(registryKey);
				if (serverWorld2 != null && minecraftServer.isNetherAllowed() && !this.hasVehicle() && this.netherPortalTime++ >= i) {
					this.world.getProfiler().push("portal");
					this.netherPortalTime = i;
					this.resetNetherPortalCooldown();
					this.moveToWorld(serverWorld2);
					this.world.getProfiler().pop();
				}

				this.inNetherPortal = false;
			} else {
				if (this.netherPortalTime > 0) {
					this.netherPortalTime -= 4;
				}

				if (this.netherPortalTime < 0) {
					this.netherPortalTime = 0;
				}
			}

			this.tickNetherPortalCooldown();
		}
	}

	public int getDefaultNetherPortalCooldown() {
		return 300;
	}

	public void setVelocityClient(double x, double y, double z) {
		this.setVelocity(x, y, z);
	}

	public void handleStatus(byte status) {
		switch (status) {
			case 53:
				HoneyBlock.addRegularParticles(this);
		}
	}

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

	public void equipStack(EquipmentSlot slot, ItemStack stack) {
	}

	public boolean isOnFire() {
		boolean bl = this.world != null && this.world.isClient;
		return !this.isFireImmune() && (this.fireTicks > 0 || bl && this.getFlag(ON_FIRE_FLAG_INDEX));
	}

	public boolean hasVehicle() {
		return this.getVehicle() != null;
	}

	public boolean hasPassengers() {
		return !this.passengerList.isEmpty();
	}

	public boolean canBeRiddenInWater() {
		return true;
	}

	public void setSneaking(boolean sneaking) {
		this.setFlag(SNEAKING_FLAG_INDEX, sneaking);
	}

	public boolean isSneaking() {
		return this.getFlag(SNEAKING_FLAG_INDEX);
	}

	public boolean bypassesSteppingEffects() {
		return this.isSneaking();
	}

	public boolean bypassesLandingEffects() {
		return this.isSneaking();
	}

	public boolean isSneaky() {
		return this.isSneaking();
	}

	public boolean isDescending() {
		return this.isSneaking();
	}

	/**
	 * Returns whether the entity is in a crouching pose.
	 * 
	 * <p>Compared to {@link #isSneaking()}, it only makes the entity appear
	 * crouching and does not bring other effects of sneaking, such as no less
	 * obvious name label rendering, no dismounting while riding, etc.
	 * 
	 * <p>This is used by vanilla for non-player entities to crouch, such as
	 * for foxes and cats.
	 */
	public boolean isInSneakingPose() {
		return this.getPose() == EntityPose.CROUCHING;
	}

	public boolean isSprinting() {
		return this.getFlag(SPRINTING_FLAG_INDEX);
	}

	public void setSprinting(boolean sprinting) {
		this.setFlag(SPRINTING_FLAG_INDEX, sprinting);
	}

	public boolean isSwimming() {
		return this.getFlag(SWIMMING_FLAG_INDEX);
	}

	public boolean isInSwimmingPose() {
		return this.getPose() == EntityPose.SWIMMING;
	}

	public boolean shouldLeaveSwimmingPose() {
		return this.isInSwimmingPose() && !this.isTouchingWater();
	}

	public void setSwimming(boolean swimming) {
		this.setFlag(SWIMMING_FLAG_INDEX, swimming);
	}

	public final boolean method_36361() {
		return this.glowing;
	}

	public final void setGlowing(boolean glowing) {
		this.glowing = glowing;
		this.setFlag(GLOWING_FLAG_INDEX, this.isGlowing());
	}

	public boolean isGlowing() {
		return this.world.isClient() ? this.getFlag(GLOWING_FLAG_INDEX) : this.glowing;
	}

	public boolean isInvisible() {
		return this.getFlag(INVISIBLE_FLAG_INDEX);
	}

	public boolean isInvisibleTo(PlayerEntity player) {
		if (player.isSpectator()) {
			return false;
		} else {
			AbstractTeam abstractTeam = this.getScoreboardTeam();
			return abstractTeam != null && player != null && player.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles()
				? false
				: this.isInvisible();
		}
	}

	/**
	 * Returns the game event handler for this entity.
	 * 
	 * <p>Subclasses interested in listening to game events as an entity should return a
	 * handler so the {@link net.minecraft.world.event.listener.GameEventListener listener}
	 * used to receive game events can be registered to the correct dispatchers.
	 * 
	 * @implNote The vanilla implementation always returns {@code null}.
	 */
	@Nullable
	public EntityGameEventHandler getGameEventHandler() {
		return null;
	}

	@Nullable
	public AbstractTeam getScoreboardTeam() {
		return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
	}

	public boolean isTeammate(Entity other) {
		return this.isTeamPlayer(other.getScoreboardTeam());
	}

	public boolean isTeamPlayer(AbstractTeam team) {
		return this.getScoreboardTeam() != null ? this.getScoreboardTeam().isEqual(team) : false;
	}

	public void setInvisible(boolean invisible) {
		this.setFlag(INVISIBLE_FLAG_INDEX, invisible);
	}

	protected boolean getFlag(int index) {
		return (this.dataTracker.get(FLAGS) & 1 << index) != 0;
	}

	protected void setFlag(int index, boolean value) {
		byte b = this.dataTracker.get(FLAGS);
		if (value) {
			this.dataTracker.set(FLAGS, (byte)(b | 1 << index));
		} else {
			this.dataTracker.set(FLAGS, (byte)(b & ~(1 << index)));
		}
	}

	public int getMaxAir() {
		return 300;
	}

	public int getAir() {
		return this.dataTracker.get(AIR);
	}

	public void setAir(int air) {
		this.dataTracker.set(AIR, air);
	}

	public int getFrozenTicks() {
		return this.dataTracker.get(FROZEN_TICKS);
	}

	public void setFrozenTicks(int frozenTicks) {
		this.dataTracker.set(FROZEN_TICKS, frozenTicks);
	}

	public float getFreezingScale() {
		int i = this.getMinFreezeDamageTicks();
		return (float)Math.min(this.getFrozenTicks(), i) / (float)i;
	}

	public boolean isFreezing() {
		return this.getFrozenTicks() >= this.getMinFreezeDamageTicks();
	}

	public int getMinFreezeDamageTicks() {
		return 140;
	}

	public void onStruckByLightning(ServerWorld world, LightningEntity lightning) {
		this.setFireTicks(this.fireTicks + 1);
		if (this.fireTicks == 0) {
			this.setOnFireFor(8);
		}

		this.damage(DamageSource.LIGHTNING_BOLT, 5.0F);
	}

	public void onBubbleColumnSurfaceCollision(boolean drag) {
		Vec3d vec3d = this.getVelocity();
		double d;
		if (drag) {
			d = Math.max(-0.9, vec3d.y - 0.03);
		} else {
			d = Math.min(1.8, vec3d.y + 0.1);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
	}

	public void onBubbleColumnCollision(boolean drag) {
		Vec3d vec3d = this.getVelocity();
		double d;
		if (drag) {
			d = Math.max(-0.3, vec3d.y - 0.03);
		} else {
			d = Math.min(0.7, vec3d.y + 0.06);
		}

		this.setVelocity(vec3d.x, d, vec3d.z);
		this.fallDistance = 0.0F;
	}

	public void onKilledOther(ServerWorld world, LivingEntity other) {
	}

	protected void pushOutOfBlocks(double x, double y, double z) {
		BlockPos blockPos = new BlockPos(x, y, z);
		Vec3d vec3d = new Vec3d(x - (double)blockPos.getX(), y - (double)blockPos.getY(), z - (double)blockPos.getZ());
		BlockPos.Mutable mutable = new BlockPos.Mutable();
		Direction direction = Direction.UP;
		double d = Double.MAX_VALUE;

		for (Direction direction2 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
			mutable.set(blockPos, direction2);
			if (!this.world.getBlockState(mutable).isFullCube(this.world, mutable)) {
				double e = vec3d.getComponentAlongAxis(direction2.getAxis());
				double f = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - e : e;
				if (f < d) {
					d = f;
					direction = direction2;
				}
			}
		}

		float g = this.random.nextFloat() * 0.2F + 0.1F;
		float h = (float)direction.getDirection().offset();
		Vec3d vec3d2 = this.getVelocity().multiply(0.75);
		if (direction.getAxis() == Direction.Axis.X) {
			this.setVelocity((double)(h * g), vec3d2.y, vec3d2.z);
		} else if (direction.getAxis() == Direction.Axis.Y) {
			this.setVelocity(vec3d2.x, (double)(h * g), vec3d2.z);
		} else if (direction.getAxis() == Direction.Axis.Z) {
			this.setVelocity(vec3d2.x, vec3d2.y, (double)(h * g));
		}
	}

	public void slowMovement(BlockState state, Vec3d multiplier) {
		this.fallDistance = 0.0F;
		this.movementMultiplier = multiplier;
	}

	private static Text removeClickEvents(Text textComponent) {
		MutableText mutableText = textComponent.copy().setStyle(textComponent.getStyle().withClickEvent(null));

		for (Text text : textComponent.getSiblings()) {
			mutableText.append(removeClickEvents(text));
		}

		return mutableText;
	}

	@Override
	public Text getName() {
		Text text = this.getCustomName();
		return text != null ? removeClickEvents(text) : this.getDefaultName();
	}

	protected Text getDefaultName() {
		return this.type.getName();
	}

	public boolean isPartOf(Entity entity) {
		return this == entity;
	}

	public float getHeadYaw() {
		return 0.0F;
	}

	public void setHeadYaw(float headYaw) {
	}

	public void setBodyYaw(float bodyYaw) {
	}

	public boolean isAttackable() {
		return true;
	}

	public boolean handleAttack(Entity attacker) {
		return false;
	}

	public String toString() {
		return String.format(
			Locale.ROOT,
			"%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]",
			this.getClass().getSimpleName(),
			this.getName().getString(),
			this.entityId,
			this.world == null ? "~NULL~" : this.world.toString(),
			this.getX(),
			this.getY(),
			this.getZ()
		);
	}

	public boolean isInvulnerableTo(DamageSource damageSource) {
		return this.isRemoved() || this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
	}

	public boolean isInvulnerable() {
		return this.invulnerable;
	}

	public void setInvulnerable(boolean invulnerable) {
		this.invulnerable = invulnerable;
	}

	public void copyPositionAndRotation(Entity entity) {
		this.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.getYaw(), entity.getPitch());
	}

	public void copyFrom(Entity original) {
		NbtCompound nbtCompound = original.writeNbt(new NbtCompound());
		nbtCompound.remove("Dimension");
		this.readNbt(nbtCompound);
		this.netherPortalCooldown = original.netherPortalCooldown;
		this.lastNetherPortalPosition = original.lastNetherPortalPosition;
	}

	/**
	 * Moves this entity to another world.
	 * 
	 * <p>Note all entities except server player entities are completely recreated at the destination.
	 * 
	 * @return the entity in the other world
	 */
	@Nullable
	public Entity moveToWorld(ServerWorld destination) {
		if (this.world instanceof ServerWorld && !this.isRemoved()) {
			this.world.getProfiler().push("changeDimension");
			this.detach();
			this.world.getProfiler().push("reposition");
			TeleportTarget teleportTarget = this.getTeleportTarget(destination);
			if (teleportTarget == null) {
				return null;
			} else {
				this.world.getProfiler().swap("reloading");
				Entity entity = this.getType().create(destination);
				if (entity != null) {
					entity.copyFrom(this);
					entity.refreshPositionAndAngles(teleportTarget.position.x, teleportTarget.position.y, teleportTarget.position.z, teleportTarget.yaw, entity.getPitch());
					entity.setVelocity(teleportTarget.velocity);
					destination.onDimensionChanged(entity);
					if (destination.getRegistryKey() == World.END) {
						ServerWorld.createEndSpawnPlatform(destination);
					}
				}

				this.removeFromDimension();
				this.world.getProfiler().pop();
				((ServerWorld)this.world).resetIdleTimeout();
				destination.resetIdleTimeout();
				this.world.getProfiler().pop();
				return entity;
			}
		} else {
			return null;
		}
	}

	protected void removeFromDimension() {
		this.setRemoved(Entity.RemovalReason.CHANGED_DIMENSION);
	}

	/**
	 * Determines a {@link TeleportTarget} for the entity
	 * based on its current and destination worlds, plus
	 * any portals that may be present.
	 */
	@Nullable
	protected TeleportTarget getTeleportTarget(ServerWorld destination) {
		boolean bl = this.world.getRegistryKey() == World.END && destination.getRegistryKey() == World.OVERWORLD;
		boolean bl2 = destination.getRegistryKey() == World.END;
		if (!bl && !bl2) {
			boolean bl3 = destination.getRegistryKey() == World.NETHER;
			if (this.world.getRegistryKey() != World.NETHER && !bl3) {
				return null;
			} else {
				WorldBorder worldBorder = destination.getWorldBorder();
				double d = Math.max(-2.9999872E7, worldBorder.getBoundWest() + 16.0);
				double e = Math.max(-2.9999872E7, worldBorder.getBoundNorth() + 16.0);
				double f = Math.min(2.9999872E7, worldBorder.getBoundEast() - 16.0);
				double g = Math.min(2.9999872E7, worldBorder.getBoundSouth() - 16.0);
				double h = DimensionType.getCoordinateScaleFactor(this.world.getDimension(), destination.getDimension());
				BlockPos blockPos2 = new BlockPos(MathHelper.clamp(this.getX() * h, d, f), this.getY(), MathHelper.clamp(this.getZ() * h, e, g));
				return (TeleportTarget)this.getPortalRect(destination, blockPos2, bl3)
					.map(
						rect -> {
							BlockState blockState = this.world.getBlockState(this.lastNetherPortalPosition);
							Direction.Axis axis;
							Vec3d vec3d;
							if (blockState.contains(Properties.HORIZONTAL_AXIS)) {
								axis = blockState.get(Properties.HORIZONTAL_AXIS);
								PortalUtil.Rectangle rectangle = PortalUtil.getLargestRectangle(
									this.lastNetherPortalPosition, axis, 21, Direction.Axis.Y, 21, blockPos -> this.world.getBlockState(blockPos) == blockState
								);
								vec3d = this.positionInPortal(axis, rectangle);
							} else {
								axis = Direction.Axis.X;
								vec3d = new Vec3d(0.5, 0.0, 0.0);
							}

							return AreaHelper.getNetherTeleportTarget(
								destination, rect, axis, vec3d, this.getDimensions(this.getPose()), this.getVelocity(), this.getYaw(), this.getPitch()
							);
						}
					)
					.orElse(null);
			}
		} else {
			BlockPos blockPos;
			if (bl2) {
				blockPos = ServerWorld.END_SPAWN_POS;
			} else {
				blockPos = destination.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, destination.getSpawnPos());
			}

			return new TeleportTarget(
				new Vec3d((double)blockPos.getX() + 0.5, (double)blockPos.getY(), (double)blockPos.getZ() + 0.5), this.getVelocity(), this.getYaw(), this.getPitch()
			);
		}
	}

	protected Vec3d positionInPortal(Direction.Axis portalAxis, PortalUtil.Rectangle portalRect) {
		return AreaHelper.entityPosInPortal(portalRect, portalAxis, this.getPos(), this.getDimensions(this.getPose()));
	}

	protected Optional<PortalUtil.Rectangle> getPortalRect(ServerWorld destWorld, BlockPos destPos, boolean destIsNether) {
		return destWorld.getPortalForcer().getPortalRect(destPos, destIsNether);
	}

	public boolean canUsePortals() {
		return true;
	}

	public float getEffectiveExplosionResistance(Explosion explosion, BlockView world, BlockPos pos, BlockState blockState, FluidState fluidState, float max) {
		return max;
	}

	public boolean canExplosionDestroyBlock(Explosion explosion, BlockView world, BlockPos pos, BlockState state, float explosionPower) {
		return true;
	}

	public int getSafeFallDistance() {
		return 3;
	}

	public boolean canAvoidTraps() {
		return false;
	}

	public void populateCrashReport(CrashReportSection section) {
		section.add("Entity Type", (CrashCallable<String>)(() -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")"));
		section.add("Entity ID", this.entityId);
		section.add("Entity Name", (CrashCallable<String>)(() -> this.getName().getString()));
		section.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
		section.add(
			"Entity's Block location",
			CrashReportSection.createPositionString(this.world, MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ()))
		);
		Vec3d vec3d = this.getVelocity();
		section.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
		section.add("Entity's Passengers", (CrashCallable<String>)(() -> this.getPassengerList().toString()));
		section.add("Entity's Vehicle", (CrashCallable<String>)(() -> String.valueOf(this.getVehicle())));
	}

	public boolean doesRenderOnFire() {
		return this.isOnFire() && !this.isSpectator();
	}

	public void setUuid(UUID uuid) {
		this.uuid = uuid;
		this.uuidString = this.uuid.toString();
	}

	@Override
	public UUID getUuid() {
		return this.uuid;
	}

	public String getUuidAsString() {
		return this.uuidString;
	}

	public String getEntityName() {
		return this.uuidString;
	}

	public boolean isPushedByFluids() {
		return true;
	}

	public static double getRenderDistanceMultiplier() {
		return renderDistanceMultiplier;
	}

	public static void setRenderDistanceMultiplier(double value) {
		renderDistanceMultiplier = value;
	}

	@Override
	public Text getDisplayName() {
		return Team.decorateName(this.getScoreboardTeam(), this.getName())
			.styled(style -> style.withHoverEvent(this.getHoverEvent()).withInsertion(this.getUuidAsString()));
	}

	public void setCustomName(@Nullable Text name) {
		this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(name));
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

	public void setCustomNameVisible(boolean visible) {
		this.dataTracker.set(NAME_VISIBLE, visible);
	}

	public boolean isCustomNameVisible() {
		return this.dataTracker.get(NAME_VISIBLE);
	}

	public final void teleport(double destX, double destY, double destZ) {
		if (this.world instanceof ServerWorld) {
			ChunkPos chunkPos = new ChunkPos(new BlockPos(destX, destY, destZ));
			((ServerWorld)this.world).getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 0, this.getId());
			this.world.getChunk(chunkPos.x, chunkPos.z);
			this.requestTeleport(destX, destY, destZ);
		}
	}

	public void requestTeleportAndDismount(double destX, double destY, double destZ) {
		this.requestTeleport(destX, destY, destZ);
	}

	public void requestTeleport(double destX, double destY, double destZ) {
		if (this.world instanceof ServerWorld) {
			this.refreshPositionAndAngles(destX, destY, destZ, this.getYaw(), this.getPitch());
			this.streamSelfAndPassengers().forEach(entity -> {
				for (Entity entity2 : entity.passengerList) {
					entity.updatePassengerPosition(entity2, Entity::refreshPositionAfterTeleport);
				}
			});
		}
	}

	public boolean shouldRenderName() {
		return this.isCustomNameVisible();
	}

	public void onTrackedDataSet(TrackedData<?> data) {
		if (POSE.equals(data)) {
			this.calculateDimensions();
		}
	}

	public void calculateDimensions() {
		EntityDimensions entityDimensions = this.dimensions;
		EntityPose entityPose = this.getPose();
		EntityDimensions entityDimensions2 = this.getDimensions(entityPose);
		this.dimensions = entityDimensions2;
		this.standingEyeHeight = this.getEyeHeight(entityPose, entityDimensions2);
		this.refreshPosition();
		boolean bl = (double)entityDimensions2.width <= 4.0 && (double)entityDimensions2.height <= 4.0;
		if (!this.world.isClient
			&& !this.firstUpdate
			&& !this.noClip
			&& bl
			&& (entityDimensions2.width > entityDimensions.width || entityDimensions2.height > entityDimensions.height)
			&& !(this instanceof PlayerEntity)) {
			Vec3d vec3d = this.getPos().add(0.0, (double)entityDimensions.height / 2.0, 0.0);
			double d = (double)Math.max(0.0F, entityDimensions2.width - entityDimensions.width) + 1.0E-6;
			double e = (double)Math.max(0.0F, entityDimensions2.height - entityDimensions.height) + 1.0E-6;
			VoxelShape voxelShape = VoxelShapes.cuboid(Box.of(vec3d, d, e, d));
			this.world
				.method_33594(this, voxelShape, vec3d, (double)entityDimensions2.width, (double)entityDimensions2.height, (double)entityDimensions2.width)
				.ifPresent(vec3dx -> this.setPosition(vec3dx.add(0.0, (double)(-entityDimensions2.height) / 2.0, 0.0)));
		}
	}

	public Direction getHorizontalFacing() {
		return Direction.fromRotation((double)this.getYaw());
	}

	public Direction getMovementDirection() {
		return this.getHorizontalFacing();
	}

	protected HoverEvent getHoverEvent() {
		return new HoverEvent(HoverEvent.Action.SHOW_ENTITY, new HoverEvent.EntityContent(this.getType(), this.getUuid(), this.getName()));
	}

	public boolean canBeSpectated(ServerPlayerEntity spectator) {
		return true;
	}

	@Override
	public final Box getBoundingBox() {
		return this.entityBounds;
	}

	public Box getVisibilityBoundingBox() {
		return this.getBoundingBox();
	}

	protected Box calculateBoundsForPose(EntityPose pos) {
		EntityDimensions entityDimensions = this.getDimensions(pos);
		float f = entityDimensions.width / 2.0F;
		Vec3d vec3d = new Vec3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
		Vec3d vec3d2 = new Vec3d(this.getX() + (double)f, this.getY() + (double)entityDimensions.height, this.getZ() + (double)f);
		return new Box(vec3d, vec3d2);
	}

	public final void setBoundingBox(Box boundingBox) {
		this.entityBounds = boundingBox;
	}

	protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
		return dimensions.height * 0.85F;
	}

	public float getEyeHeight(EntityPose pose) {
		return this.getEyeHeight(pose, this.getDimensions(pose));
	}

	public final float getStandingEyeHeight() {
		return this.standingEyeHeight;
	}

	public Vec3d getLeashOffset() {
		return new Vec3d(0.0, (double)this.getStandingEyeHeight(), (double)(this.getWidth() * 0.4F));
	}

	/**
	 * Obtains an item slot for command modification purpose. Used by commands
	 * like {@code /loot} or {@code /replaceitem}.
	 * 
	 * @see net.minecraft.command.argument.ItemSlotArgumentType
	 * 
	 * @param mappedIndex the mapped index as given by the item slot argument
	 */
	public CommandItemSlot getCommandItemSlot(int mappedIndex) {
		return CommandItemSlot.EMPTY;
	}

	@Override
	public void sendSystemMessage(Text message, UUID sender) {
	}

	public World getEntityWorld() {
		return this.world;
	}

	@Nullable
	public MinecraftServer getServer() {
		return this.world.getServer();
	}

	public ActionResult interactAt(PlayerEntity player, Vec3d hitPos, Hand hand) {
		return ActionResult.PASS;
	}

	public boolean isImmuneToExplosion() {
		return false;
	}

	public void dealDamage(LivingEntity attacker, Entity target) {
		if (target instanceof LivingEntity) {
			EnchantmentHelper.onUserDamaged((LivingEntity)target, attacker);
		}

		EnchantmentHelper.onTargetDamaged(attacker, target);
	}

	public void onStartedTrackingBy(ServerPlayerEntity player) {
	}

	public void onStoppedTrackingBy(ServerPlayerEntity player) {
	}

	public float applyRotation(BlockRotation rotation) {
		float f = MathHelper.wrapDegrees(this.getYaw());
		switch (rotation) {
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

	public float applyMirror(BlockMirror mirror) {
		float f = MathHelper.wrapDegrees(this.getYaw());
		switch (mirror) {
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

	@Nullable
	public Entity getPrimaryPassenger() {
		return null;
	}

	public final List<Entity> getPassengerList() {
		return this.passengerList;
	}

	@Nullable
	public Entity getFirstPassenger() {
		return this.passengerList.isEmpty() ? null : (Entity)this.passengerList.get(0);
	}

	public boolean hasPassenger(Entity passenger) {
		return this.passengerList.contains(passenger);
	}

	public boolean hasPassengerType(Predicate<Entity> predicate) {
		for (Entity entity : this.passengerList) {
			if (predicate.test(entity)) {
				return true;
			}
		}

		return false;
	}

	private Stream<Entity> streamIntoPassengers() {
		return this.passengerList.stream().flatMap(Entity::streamSelfAndPassengers);
	}

	@Override
	public Stream<Entity> streamSelfAndPassengers() {
		return Stream.concat(Stream.of(this), this.streamIntoPassengers());
	}

	@Override
	public Stream<Entity> streamPassengersAndSelf() {
		return Stream.concat(this.passengerList.stream().flatMap(Entity::streamPassengersAndSelf), Stream.of(this));
	}

	public Iterable<Entity> getPassengersDeep() {
		return () -> this.streamIntoPassengers().iterator();
	}

	public boolean hasPlayerRider() {
		return this.streamIntoPassengers().filter(entity -> entity instanceof PlayerEntity).count() == 1L;
	}

	/**
	 * Gets the lowest entity this entity is riding.
	 */
	public Entity getRootVehicle() {
		Entity entity = this;

		while (entity.hasVehicle()) {
			entity = entity.getVehicle();
		}

		return entity;
	}

	/**
	 * Checks if this entity and another entity share the same root vehicle.
	 * 
	 * @param entity the other entity
	 */
	public boolean isConnectedThroughVehicle(Entity entity) {
		return this.getRootVehicle() == entity.getRootVehicle();
	}

	public boolean hasPassengerDeep(Entity passenger) {
		return this.streamIntoPassengers().anyMatch(entity -> entity == passenger);
	}

	public boolean isLogicalSideForUpdatingMovement() {
		Entity entity = this.getPrimaryPassenger();
		return entity instanceof PlayerEntity ? ((PlayerEntity)entity).isMainPlayer() : !this.world.isClient;
	}

	protected static Vec3d getPassengerDismountOffset(double vehicleWidth, double passengerWidth, float passengerYaw) {
		double d = (vehicleWidth + passengerWidth + 1.0E-5F) / 2.0;
		float f = -MathHelper.sin(passengerYaw * (float) (Math.PI / 180.0));
		float g = MathHelper.cos(passengerYaw * (float) (Math.PI / 180.0));
		float h = Math.max(Math.abs(f), Math.abs(g));
		return new Vec3d((double)f * d / (double)h, 0.0, (double)g * d / (double)h);
	}

	public Vec3d updatePassengerForDismount(LivingEntity passenger) {
		return new Vec3d(this.getX(), this.getBoundingBox().maxY, this.getZ());
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

	/**
	 * Creates a command source which represents this entity.
	 */
	public ServerCommandSource getCommandSource() {
		return new ServerCommandSource(
			this,
			this.getPos(),
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

	public boolean hasPermissionLevel(int permissionLevel) {
		return this.getPermissionLevel() >= permissionLevel;
	}

	@Override
	public boolean shouldReceiveFeedback() {
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

	public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
		Vec3d vec3d = anchorPoint.positionAt(this);
		double d = target.x - vec3d.x;
		double e = target.y - vec3d.y;
		double f = target.z - vec3d.z;
		double g = (double)MathHelper.sqrt(d * d + f * f);
		this.setPitch(MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 180.0F / (float)Math.PI))));
		this.setYaw(MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 180.0F / (float)Math.PI) - 90.0F));
		this.setHeadYaw(this.getYaw());
		this.prevPitch = this.getPitch();
		this.prevYaw = this.getYaw();
	}

	public boolean updateMovementInFluid(Tag<Fluid> tag, double d) {
		if (this.isRegionUnloaded()) {
			return false;
		} else {
			Box box = this.getBoundingBox().contract(0.001);
			int i = MathHelper.floor(box.minX);
			int j = MathHelper.ceil(box.maxX);
			int k = MathHelper.floor(box.minY);
			int l = MathHelper.ceil(box.maxY);
			int m = MathHelper.floor(box.minZ);
			int n = MathHelper.ceil(box.maxZ);
			double e = 0.0;
			boolean bl = this.isPushedByFluids();
			boolean bl2 = false;
			Vec3d vec3d = Vec3d.ZERO;
			int o = 0;
			BlockPos.Mutable mutable = new BlockPos.Mutable();

			for (int p = i; p < j; p++) {
				for (int q = k; q < l; q++) {
					for (int r = m; r < n; r++) {
						mutable.set(p, q, r);
						FluidState fluidState = this.world.getFluidState(mutable);
						if (fluidState.isIn(tag)) {
							double f = (double)((float)q + fluidState.getHeight(this.world, mutable));
							if (f >= box.minY) {
								bl2 = true;
								e = Math.max(f - box.minY, e);
								if (bl) {
									Vec3d vec3d2 = fluidState.getVelocity(this.world, mutable);
									if (e < 0.4) {
										vec3d2 = vec3d2.multiply(e);
									}

									vec3d = vec3d.add(vec3d2);
									o++;
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

				Vec3d vec3d3 = this.getVelocity();
				vec3d = vec3d.multiply(d * 1.0);
				double g = 0.003;
				if (Math.abs(vec3d3.x) < 0.003 && Math.abs(vec3d3.z) < 0.003 && vec3d.length() < 0.0045000000000000005) {
					vec3d = vec3d.normalize().multiply(0.0045000000000000005);
				}

				this.setVelocity(this.getVelocity().add(vec3d));
			}

			this.fluidHeight.put(tag, e);
			return bl2;
		}
	}

	/**
	 * Returns whether any part of this entity's bounding box is in an unloaded
	 * region of the world the entity is in.
	 * 
	 * @implSpec This implementation expands this entity's bounding box by 1 in
	 * each axis and checks whether the expanded box's smallest enclosing
	 * axis-aligned integer box is fully loaded in the world.
	 */
	public boolean isRegionUnloaded() {
		Box box = this.getBoundingBox().expand(1.0);
		int i = MathHelper.floor(box.minX);
		int j = MathHelper.ceil(box.maxX);
		int k = MathHelper.floor(box.minZ);
		int l = MathHelper.ceil(box.maxZ);
		return !this.world.isRegionLoaded(i, k, j, l);
	}

	public double getFluidHeight(Tag<Fluid> fluid) {
		return this.fluidHeight.getDouble(fluid);
	}

	/**
	 * Returns the minimum submerged height of this entity in fluid so that it
	 * would be affected by fluid physics.
	 * 
	 * @apiNote This is also used by living entities for checking whether to
	 * start swimming.
	 * 
	 * @implSpec This implementation returns {@code 0.4} if its
	 * {@linkplain #getStandingEyeHeight standing eye height} is larger than
	 * {@code 0.4}; otherwise it returns {@code 0.0} for shorter entities.
	 * 
	 * @implNote The swim height of 0 allows short entities like baby animals
	 * to start swimming to avoid suffocation.
	 */
	public double getSwimHeight() {
		return (double)this.getStandingEyeHeight() < 0.4 ? 0.0 : 0.4;
	}

	public final float getWidth() {
		return this.dimensions.width;
	}

	public final float getHeight() {
		return this.dimensions.height;
	}

	public abstract Packet<?> createSpawnPacket();

	public EntityDimensions getDimensions(EntityPose pose) {
		return this.type.getDimensions();
	}

	public Vec3d getPos() {
		return this.pos;
	}

	@Override
	public BlockPos getBlockPos() {
		return this.blockPos;
	}

	public BlockState method_36601() {
		return this.world.getBlockState(this.getBlockPos());
	}

	public BlockPos getCameraBlockPos() {
		return new BlockPos(this.getCameraPosVec(1.0F));
	}

	public ChunkPos getChunkPos() {
		return new ChunkPos(this.blockPos);
	}

	public Vec3d getVelocity() {
		return this.velocity;
	}

	public void setVelocity(Vec3d velocity) {
		this.velocity = velocity;
	}

	public void setVelocity(double x, double y, double z) {
		this.setVelocity(new Vec3d(x, y, z));
	}

	public final int getBlockX() {
		return this.blockPos.getX();
	}

	public final double getX() {
		return this.pos.x;
	}

	public double offsetX(double widthScale) {
		return this.pos.x + (double)this.getWidth() * widthScale;
	}

	public double getParticleX(double widthScale) {
		return this.offsetX((2.0 * this.random.nextDouble() - 1.0) * widthScale);
	}

	public final int getBlockY() {
		return this.blockPos.getY();
	}

	public final double getY() {
		return this.pos.y;
	}

	public double getBodyY(double heightScale) {
		return this.pos.y + (double)this.getHeight() * heightScale;
	}

	public double getRandomBodyY() {
		return this.getBodyY(this.random.nextDouble());
	}

	public double getEyeY() {
		return this.pos.y + (double)this.standingEyeHeight;
	}

	public final int getBlockZ() {
		return this.blockPos.getZ();
	}

	public final double getZ() {
		return this.pos.z;
	}

	public double offsetZ(double widthScale) {
		return this.pos.z + (double)this.getWidth() * widthScale;
	}

	public double getParticleZ(double widthScale) {
		return this.offsetZ((2.0 * this.random.nextDouble() - 1.0) * widthScale);
	}

	public final void setPos(double x, double y, double z) {
		if (this.pos.x != x || this.pos.y != y || this.pos.z != z) {
			this.pos = new Vec3d(x, y, z);
			int i = MathHelper.floor(x);
			int j = MathHelper.floor(y);
			int k = MathHelper.floor(z);
			if (i != this.blockPos.getX() || j != this.blockPos.getY() || k != this.blockPos.getZ()) {
				this.blockPos = new BlockPos(i, j, k);
			}

			this.entityChangeListener.updateEntityPosition();
			EntityGameEventHandler entityGameEventHandler = this.getGameEventHandler();
			if (entityGameEventHandler != null) {
				entityGameEventHandler.onEntitySetPos(this.world);
			}
		}
	}

	public void checkDespawn() {
	}

	public Vec3d method_30951(float f) {
		return this.getLerpedPos(f).add(0.0, (double)this.standingEyeHeight * 0.7, 0.0);
	}

	public void onSpawnPacket(EntitySpawnS2CPacket packet) {
		int i = packet.getId();
		double d = packet.getX();
		double e = packet.getY();
		double f = packet.getZ();
		this.updateTrackedPosition(d, e, f);
		this.refreshPositionAfterTeleport(d, e, f);
		this.setPitch((float)(packet.getPitch() * 360) / 256.0F);
		this.setYaw((float)(packet.getYaw() * 360) / 256.0F);
		this.setEntityId(i);
		this.setUuid(packet.getUuid());
	}

	@Nullable
	public ItemStack getPickBlockStack() {
		return null;
	}

	public void setInPowderSnow(boolean inPowderSnow) {
		this.inPowderSnow = inPowderSnow;
	}

	public boolean canFreeze() {
		return !EntityTypeTags.FREEZE_IMMUNE_ENTITY_TYPES.contains(this.getType());
	}

	public float getYaw() {
		return this.yaw;
	}

	public void setYaw(float yaw) {
		if (!Float.isFinite(yaw)) {
			throw new IllegalStateException("Invalid entity rotation");
		} else {
			this.yaw = yaw;
		}
	}

	public float getPitch() {
		return this.pitch;
	}

	public void setPitch(float pitch) {
		if (!Float.isFinite(pitch)) {
			throw new IllegalStateException("Invalid entity rotation");
		} else {
			this.pitch = pitch;
		}
	}

	public final boolean isRemoved() {
		return this.removalReason != null;
	}

	@Nullable
	public Entity.RemovalReason getRemovalReason() {
		return this.removalReason;
	}

	@Override
	public final void setRemoved(Entity.RemovalReason reason) {
		if (this.removalReason == null) {
			this.removalReason = reason;
		}

		if (this.removalReason.shouldDestroy()) {
			this.stopRiding();
		}

		this.getPassengerList().forEach(Entity::stopRiding);
		this.entityChangeListener.remove(reason);
	}

	protected void unsetRemoved() {
		this.removalReason = null;
	}

	@Override
	public void setListener(EntityChangeListener listener) {
		this.entityChangeListener = listener;
	}

	@Override
	public boolean shouldSave() {
		if (this.removalReason != null && !this.removalReason.shouldSave()) {
			return false;
		} else {
			return this.hasVehicle() ? false : !this.hasPassengers() || !this.hasPlayerRider();
		}
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	/**
	 * The move effect represents possible effects of an entity moving, such as
	 * playing sounds, emitting game events, none, or both.
	 * 
	 * @see Entity#getMoveEffect()
	 */
	public static enum MoveEffect {
		NONE(false, false),
		SOUNDS(true, false),
		EVENTS(false, true),
		ALL(true, true);

		final boolean sounds;
		final boolean events;

		private MoveEffect(boolean sounds, boolean events) {
			this.sounds = sounds;
			this.events = events;
		}

		/**
		 * Returns whether this means an entity may emit game events or play sounds
		 * as it moves.
		 */
		public boolean hasAny() {
			return this.events || this.sounds;
		}

		/**
		 * Returns whether this means an entity may emit game events as it moves.
		 */
		public boolean emitsGameEvents() {
			return this.events;
		}

		/**
		 * Returns whether this means an entity may play sounds as it moves.
		 */
		public boolean playsSounds() {
			return this.sounds;
		}
	}

	@FunctionalInterface
	public interface PositionUpdater {
		void accept(Entity entity, double x, double y, double z);
	}

	public static enum RemovalReason {
		/**
		 * The entity is killed.
		 */
		KILLED(true, false),
		DISCARDED(true, false),
		/**
		 * The entity is unloaded to chunk.
		 * <p>
		 * The entity should be saved.
		 */
		UNLOADED_TO_CHUNK(false, true),
		UNLOADED_WITH_PLAYER(false, false),
		/**
		 * The entity changed dimension.
		 */
		CHANGED_DIMENSION(false, false);

		private final boolean destroy;
		private final boolean save;

		private RemovalReason(boolean destroy, boolean save) {
			this.destroy = destroy;
			this.save = save;
		}

		/**
		 * Returns whether the entity should be destroyed or not.
		 * <p>
		 * If an entity should be destroyed, then the entity should not be re-used and any external data on the entity will be cleared.
		 */
		public boolean shouldDestroy() {
			return this.destroy;
		}

		/**
		 * Returns whether the entity should be saved or not.
		 */
		public boolean shouldSave() {
			return this.save;
		}
	}
}
