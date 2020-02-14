/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.advancement.criterion.Criterions;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FenceGateBlock;
import net.minecraft.block.HoneyBlock;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.command.arguments.EntityAnchorArgumentType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EntityContext;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
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
import net.minecraft.network.packet.s2c.play.EntityS2CPacket;
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
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.explosion.Explosion;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

public abstract class Entity
implements Nameable,
CommandOutput {
    protected static final Logger LOGGER = LogManager.getLogger();
    private static final AtomicInteger MAX_ENTITY_ID = new AtomicInteger();
    private static final List<ItemStack> EMPTY_STACK_LIST = Collections.emptyList();
    private static final Box NULL_BOX = new Box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
    private static double renderDistanceMultiplier = 1.0;
    private final EntityType<?> type;
    private int entityId = MAX_ENTITY_ID.incrementAndGet();
    public boolean inanimate;
    private final List<Entity> passengerList = Lists.newArrayList();
    protected int ridingCooldown;
    @Nullable
    private Entity vehicle;
    public boolean teleporting;
    public World world;
    public double prevX;
    public double prevY;
    public double prevZ;
    private double x;
    private double y;
    private double z;
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
    public float distanceTraveled;
    public float fallDistance;
    private float nextStepSoundDistance = 1.0f;
    private float nextFlySoundDistance = 1.0f;
    public double lastRenderX;
    public double lastRenderY;
    public double lastRenderZ;
    public float stepHeight;
    public boolean noClip;
    public float pushSpeedReduction;
    protected final Random random = new Random();
    public int age;
    private int fireTicks = -this.getBurningDuration();
    protected boolean touchingWater;
    protected double waterHeight;
    protected boolean submergedInWater;
    protected boolean inLava;
    public int timeUntilRegen;
    protected boolean firstUpdate = true;
    protected final DataTracker dataTracker;
    protected static final TrackedData<Byte> FLAGS = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BYTE);
    private static final TrackedData<Integer> AIR = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Optional<Text>> CUSTOM_NAME = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.OPTIONAL_TEXT_COMPONENT);
    private static final TrackedData<Boolean> NAME_VISIBLE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> SILENT = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final TrackedData<Boolean> NO_GRAVITY = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.BOOLEAN);
    protected static final TrackedData<EntityPose> POSE = DataTracker.registerData(Entity.class, TrackedDataHandlerRegistry.ENTITY_POSE);
    public boolean updateNeeded;
    public int chunkX;
    public int chunkY;
    public int chunkZ;
    public long trackedX;
    public long trackedY;
    public long trackedZ;
    public boolean ignoreCameraFrustum;
    public boolean velocityDirty;
    public int netherPortalCooldown;
    protected boolean inNetherPortal;
    protected int netherPortalTime;
    public DimensionType dimension;
    protected BlockPos lastNetherPortalPosition;
    protected Vec3d lastNetherPortalDirectionVector;
    protected Direction lastNetherPortalDirection;
    private boolean invulnerable;
    protected UUID uuid = MathHelper.randomUuid(this.random);
    protected String uuidString = this.uuid.toString();
    protected boolean glowing;
    private final Set<String> scoreboardTags = Sets.newHashSet();
    private boolean teleportRequested;
    private final double[] pistonMovementDelta = new double[]{0.0, 0.0, 0.0};
    private long pistonMovementTick;
    private EntityDimensions dimensions;
    private float standingEyeHeight;

    public Entity(EntityType<?> type, World world) {
        this.type = type;
        this.world = world;
        this.dimensions = type.getDimensions();
        this.updatePosition(0.0, 0.0, 0.0);
        if (world != null) {
            this.dimension = world.dimension.getType();
        }
        this.dataTracker = new DataTracker(this);
        this.dataTracker.startTracking(FLAGS, (byte)0);
        this.dataTracker.startTracking(AIR, this.getMaxAir());
        this.dataTracker.startTracking(NAME_VISIBLE, false);
        this.dataTracker.startTracking(CUSTOM_NAME, Optional.empty());
        this.dataTracker.startTracking(SILENT, false);
        this.dataTracker.startTracking(NO_GRAVITY, false);
        this.dataTracker.startTracking(POSE, EntityPose.STANDING);
        this.initDataTracker();
        this.standingEyeHeight = this.getEyeHeight(EntityPose.STANDING, this.dimensions);
    }

    @Environment(value=EnvType.CLIENT)
    public int getTeamColorValue() {
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        if (abstractTeam != null && abstractTeam.getColor().getColorValue() != null) {
            return abstractTeam.getColor().getColorValue();
        }
        return 0xFFFFFF;
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

    public void updateTrackedPosition(double x, double y, double z) {
        this.trackedX = EntityS2CPacket.encodePacketCoordinate(x);
        this.trackedY = EntityS2CPacket.encodePacketCoordinate(y);
        this.trackedZ = EntityS2CPacket.encodePacketCoordinate(z);
    }

    public EntityType<?> getType() {
        return this.type;
    }

    public int getEntityId() {
        return this.entityId;
    }

    public void setEntityId(int id) {
        this.entityId = id;
    }

    public Set<String> getScoreboardTags() {
        return this.scoreboardTags;
    }

    public boolean addScoreboardTag(String tag) {
        if (this.scoreboardTags.size() >= 1024) {
            return false;
        }
        return this.scoreboardTags.add(tag);
    }

    public boolean removeScoreboardTag(String tag) {
        return this.scoreboardTags.remove(tag);
    }

    public void kill() {
        this.remove();
    }

    protected abstract void initDataTracker();

    public DataTracker getDataTracker() {
        return this.dataTracker;
    }

    public boolean equals(Object o) {
        if (o instanceof Entity) {
            return ((Entity)o).entityId == this.entityId;
        }
        return false;
    }

    public int hashCode() {
        return this.entityId;
    }

    @Environment(value=EnvType.CLIENT)
    protected void afterSpawn() {
        if (this.world == null) {
            return;
        }
        for (double d = this.getY(); d > 0.0 && d < 256.0; d += 1.0) {
            this.updatePosition(this.getX(), d, this.getZ());
            if (this.world.doesNotCollide(this)) break;
        }
        this.setVelocity(Vec3d.ZERO);
        this.pitch = 0.0f;
    }

    public void remove() {
        this.removed = true;
    }

    protected void setPose(EntityPose pose) {
        this.dataTracker.set(POSE, pose);
    }

    public EntityPose getPose() {
        return this.dataTracker.get(POSE);
    }

    public boolean method_24516(Entity entity, double d) {
        double e = entity.x - this.x;
        double f = entity.y - this.y;
        double g = entity.z - this.z;
        return e * e + f * f + g * g < d * d;
    }

    public BlockPos method_24515() {
        return new BlockPos(this);
    }

    protected void setRotation(float yaw, float pitch) {
        this.yaw = yaw % 360.0f;
        this.pitch = pitch % 360.0f;
    }

    public void updatePosition(double x, double y, double z) {
        this.setPos(x, y, z);
        float f = this.dimensions.width / 2.0f;
        float g = this.dimensions.height;
        this.setBoundingBox(new Box(x - (double)f, y, z - (double)f, x + (double)f, y + (double)g, z + (double)f));
    }

    protected void refreshPosition() {
        this.updatePosition(this.x, this.y, this.z);
    }

    @Environment(value=EnvType.CLIENT)
    public void changeLookDirection(double cursorDeltaX, double cursorDeltaY) {
        double d = cursorDeltaY * 0.15;
        double e = cursorDeltaX * 0.15;
        this.pitch = (float)((double)this.pitch + d);
        this.yaw = (float)((double)this.yaw + e);
        this.pitch = MathHelper.clamp(this.pitch, -90.0f, 90.0f);
        this.prevPitch = (float)((double)this.prevPitch + d);
        this.prevYaw = (float)((double)this.prevYaw + e);
        this.prevPitch = MathHelper.clamp(this.prevPitch, -90.0f, 90.0f);
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
            --this.ridingCooldown;
        }
        this.prevHorizontalSpeed = this.horizontalSpeed;
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
        this.tickNetherPortal();
        this.attemptSprintingParticles();
        this.updateWaterState();
        if (this.world.isClient) {
            this.extinguish();
        } else if (this.fireTicks > 0) {
            if (this.isFireImmune()) {
                this.fireTicks -= 4;
                if (this.fireTicks < 0) {
                    this.extinguish();
                }
            } else {
                if (this.fireTicks % 20 == 0) {
                    this.damage(DamageSource.ON_FIRE, 1.0f);
                }
                --this.fireTicks;
            }
        }
        if (this.isInLava()) {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5f;
        }
        if (this.getY() < -64.0) {
            this.destroy();
        }
        if (!this.world.isClient) {
            this.setFlag(0, this.fireTicks > 0);
        }
        this.firstUpdate = false;
        this.world.getProfiler().pop();
    }

    protected void tickNetherPortalCooldown() {
        if (this.netherPortalCooldown > 0) {
            --this.netherPortalCooldown;
        }
    }

    public int getMaxNetherPortalTime() {
        return 1;
    }

    protected void setOnFireFromLava() {
        if (this.isFireImmune()) {
            return;
        }
        this.setOnFireFor(15);
        this.damage(DamageSource.LAVA, 4.0f);
    }

    public void setOnFireFor(int seconds) {
        int i = seconds * 20;
        if (this instanceof LivingEntity) {
            i = ProtectionEnchantment.transformFireDuration((LivingEntity)this, i);
        }
        if (this.fireTicks < i) {
            this.fireTicks = i;
        }
    }

    public void setFireTicks(int ticks) {
        this.fireTicks = ticks;
    }

    public int getFireTicks() {
        return this.fireTicks;
    }

    public void extinguish() {
        this.fireTicks = 0;
    }

    protected void destroy() {
        this.remove();
    }

    public boolean doesNotCollide(double offsetX, double offsetY, double offsetZ) {
        return this.doesNotCollide(this.getBoundingBox().offset(offsetX, offsetY, offsetZ));
    }

    private boolean doesNotCollide(Box box) {
        return this.world.doesNotCollide(this, box) && !this.world.containsFluid(box);
    }

    public void move(MovementType type, Vec3d movement) {
        Vec3d vec3d;
        if (this.noClip) {
            this.setBoundingBox(this.getBoundingBox().offset(movement));
            this.moveToBoundingBoxCenter();
            return;
        }
        if (type == MovementType.PISTON && (movement = this.adjustMovementForPiston(movement)).equals(Vec3d.ZERO)) {
            return;
        }
        this.world.getProfiler().push("move");
        if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
            movement = movement.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
            this.setVelocity(Vec3d.ZERO);
        }
        if ((vec3d = this.adjustMovementForCollisions(movement = this.adjustMovementForSneaking(movement, type))).lengthSquared() > 1.0E-7) {
            this.setBoundingBox(this.getBoundingBox().offset(vec3d));
            this.moveToBoundingBoxCenter();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rest");
        this.horizontalCollision = !MathHelper.approximatelyEquals(movement.x, vec3d.x) || !MathHelper.approximatelyEquals(movement.z, vec3d.z);
        this.verticalCollision = movement.y != vec3d.y;
        this.onGround = this.verticalCollision && movement.y < 0.0;
        this.collided = this.horizontalCollision || this.verticalCollision;
        BlockPos blockPos = this.getLandingPos();
        BlockState blockState = this.world.getBlockState(blockPos);
        this.fall(vec3d.y, this.onGround, blockState, blockPos);
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
            block.onSteppedOn(this.world, blockPos, this);
        }
        if (this.canClimb() && !this.hasVehicle()) {
            double d = vec3d.x;
            double e = vec3d.y;
            double f = vec3d.z;
            if (block != Blocks.LADDER && block != Blocks.SCAFFOLDING) {
                e = 0.0;
            }
            this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d)) * 0.6);
            this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(d * d + e * e + f * f) * 0.6);
            if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
                this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                if (this.isTouchingWater()) {
                    Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
                    float g = entity == this ? 0.35f : 0.4f;
                    Vec3d vec3d3 = entity.getVelocity();
                    float h = MathHelper.sqrt(vec3d3.x * vec3d3.x * (double)0.2f + vec3d3.y * vec3d3.y + vec3d3.z * vec3d3.z * (double)0.2f) * g;
                    if (h > 1.0f) {
                        h = 1.0f;
                    }
                    this.playSwimSound(h);
                } else {
                    this.playStepSound(blockPos, blockState);
                }
            } else if (this.distanceTraveled > this.nextFlySoundDistance && this.hasWings() && blockState.isAir()) {
                this.nextFlySoundDistance = this.playFlySound(this.distanceTraveled);
            }
        }
        try {
            this.inLava = false;
            this.checkBlockCollision();
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Checking entity block collision");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being checked for collision");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
        this.setVelocity(this.getVelocity().multiply(this.getVelocityMultiplier(), 1.0, this.getVelocityMultiplier()));
        if (!this.world.doesAreaContainFireSource(this.getBoundingBox().contract(0.001)) && this.fireTicks <= 0) {
            this.fireTicks = -this.getBurningDuration();
        }
        if (this.isWet() && this.isOnFire()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.fireTicks = -this.getBurningDuration();
        }
        this.world.getProfiler().pop();
    }

    protected BlockPos getLandingPos() {
        BlockPos blockPos2;
        BlockState blockState;
        Block block;
        int k;
        int j;
        int i = MathHelper.floor(this.x);
        BlockPos blockPos = new BlockPos(i, j = MathHelper.floor(this.y - (double)0.2f), k = MathHelper.floor(this.z));
        if (this.world.getBlockState(blockPos).isAir() && ((block = (blockState = this.world.getBlockState(blockPos2 = blockPos.down())).getBlock()).isIn(BlockTags.FENCES) || block.isIn(BlockTags.WALLS) || block instanceof FenceGateBlock)) {
            return blockPos2;
        }
        return blockPos;
    }

    protected float getJumpVelocityMultiplier() {
        float f = this.world.getBlockState(new BlockPos(this)).getBlock().getJumpVelocityMultiplier();
        float g = this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getJumpVelocityMultiplier();
        return (double)f == 1.0 ? g : f;
    }

    protected float getVelocityMultiplier() {
        Block block = this.world.getBlockState(new BlockPos(this)).getBlock();
        float f = block.getVelocityMultiplier();
        if (block == Blocks.WATER || block == Blocks.BUBBLE_COLUMN) {
            return f;
        }
        return (double)f == 1.0 ? this.world.getBlockState(this.getVelocityAffectingPos()).getBlock().getVelocityMultiplier() : f;
    }

    protected BlockPos getVelocityAffectingPos() {
        return new BlockPos(this.x, this.getBoundingBox().y1 - 0.5000001, this.z);
    }

    protected Vec3d adjustMovementForSneaking(Vec3d movement, MovementType type) {
        return movement;
    }

    protected Vec3d adjustMovementForPiston(Vec3d movement) {
        if (movement.lengthSquared() <= 1.0E-7) {
            return movement;
        }
        long l = this.world.getTime();
        if (l != this.pistonMovementTick) {
            Arrays.fill(this.pistonMovementDelta, 0.0);
            this.pistonMovementTick = l;
        }
        if (movement.x != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.X, movement.x);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
        }
        if (movement.y != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.Y, movement.y);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, d, 0.0);
        }
        if (movement.z != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.Z, movement.z);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, 0.0, d);
        }
        return Vec3d.ZERO;
    }

    private double calculatePistonMovementFactor(Direction.Axis axis, double offsetFactor) {
        int i = axis.ordinal();
        double d = MathHelper.clamp(offsetFactor + this.pistonMovementDelta[i], -0.51, 0.51);
        offsetFactor = d - this.pistonMovementDelta[i];
        this.pistonMovementDelta[i] = d;
        return offsetFactor;
    }

    private Vec3d adjustMovementForCollisions(Vec3d movement) {
        boolean bl4;
        Box box = this.getBoundingBox();
        EntityContext entityContext = EntityContext.of(this);
        VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
        Stream<Object> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(voxelShape);
        Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, box.stretch(movement), ImmutableSet.of());
        ReusableStream<VoxelShape> reusableStream = new ReusableStream<VoxelShape>(Stream.concat(stream2, stream));
        Vec3d vec3d = movement.lengthSquared() == 0.0 ? movement : Entity.adjustMovementForCollisions(this, movement, box, this.world, entityContext, reusableStream);
        boolean bl = movement.x != vec3d.x;
        boolean bl2 = movement.y != vec3d.y;
        boolean bl3 = movement.z != vec3d.z;
        boolean bl5 = bl4 = this.onGround || bl2 && movement.y < 0.0;
        if (this.stepHeight > 0.0f && bl4 && (bl || bl3)) {
            Vec3d vec3d4;
            Vec3d vec3d2 = Entity.adjustMovementForCollisions(this, new Vec3d(movement.x, this.stepHeight, movement.z), box, this.world, entityContext, reusableStream);
            Vec3d vec3d3 = Entity.adjustMovementForCollisions(this, new Vec3d(0.0, this.stepHeight, 0.0), box.stretch(movement.x, 0.0, movement.z), this.world, entityContext, reusableStream);
            if (vec3d3.y < (double)this.stepHeight && Entity.squaredHorizontalLength(vec3d4 = Entity.adjustMovementForCollisions(this, new Vec3d(movement.x, 0.0, movement.z), box.offset(vec3d3), this.world, entityContext, reusableStream).add(vec3d3)) > Entity.squaredHorizontalLength(vec3d2)) {
                vec3d2 = vec3d4;
            }
            if (Entity.squaredHorizontalLength(vec3d2) > Entity.squaredHorizontalLength(vec3d)) {
                return vec3d2.add(Entity.adjustMovementForCollisions(this, new Vec3d(0.0, -vec3d2.y + movement.y, 0.0), box.offset(vec3d2), this.world, entityContext, reusableStream));
            }
        }
        return vec3d;
    }

    public static double squaredHorizontalLength(Vec3d vector) {
        return vector.x * vector.x + vector.z * vector.z;
    }

    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d movement, Box entityBoundingBox, World world, EntityContext context, ReusableStream<VoxelShape> collisions) {
        boolean bl3;
        boolean bl = movement.x == 0.0;
        boolean bl2 = movement.y == 0.0;
        boolean bl4 = bl3 = movement.z == 0.0;
        if (bl && bl2 || bl && bl3 || bl2 && bl3) {
            return Entity.adjustSingleAxisMovementForCollisions(movement, entityBoundingBox, world, context, collisions);
        }
        ReusableStream<VoxelShape> reusableStream = new ReusableStream<VoxelShape>(Stream.concat(collisions.stream(), world.getBlockCollisions(entity, entityBoundingBox.stretch(movement))));
        return Entity.adjustMovementForCollisions(movement, entityBoundingBox, reusableStream);
    }

    public static Vec3d adjustMovementForCollisions(Vec3d movement, Box entityBoundingBox, ReusableStream<VoxelShape> collisions) {
        boolean bl;
        double d = movement.x;
        double e = movement.y;
        double f = movement.z;
        if (e != 0.0 && (e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, entityBoundingBox, collisions.stream(), e)) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, entityBoundingBox, collisions.stream(), f)) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
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

    public static Vec3d adjustSingleAxisMovementForCollisions(Vec3d movement, Box entityBoundingBox, WorldView world, EntityContext context, ReusableStream<VoxelShape> collisions) {
        boolean bl;
        double d = movement.x;
        double e = movement.y;
        double f = movement.z;
        if (e != 0.0 && (e = VoxelShapes.calculatePushVelocity(Direction.Axis.Y, entityBoundingBox, world, e, context, collisions.stream())) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.calculatePushVelocity(Direction.Axis.Z, entityBoundingBox, world, f, context, collisions.stream())) != 0.0) {
            entityBoundingBox = entityBoundingBox.offset(0.0, 0.0, f);
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
        return (int)this.distanceTraveled + 1;
    }

    public void moveToBoundingBoxCenter() {
        Box box = this.getBoundingBox();
        this.setPos((box.x1 + box.x2) / 2.0, box.y1, (box.z1 + box.z2) / 2.0);
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
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get(box.x1 + 0.001, box.y1 + 0.001, box.z1 + 0.001);
             BlockPos.PooledMutable pooledMutable2 = BlockPos.PooledMutable.get(box.x2 - 0.001, box.y2 - 0.001, box.z2 - 0.001);
             BlockPos.PooledMutable pooledMutable3 = BlockPos.PooledMutable.get();){
            if (this.world.isRegionLoaded(pooledMutable, pooledMutable2)) {
                for (int i = pooledMutable.getX(); i <= pooledMutable2.getX(); ++i) {
                    for (int j = pooledMutable.getY(); j <= pooledMutable2.getY(); ++j) {
                        for (int k = pooledMutable.getZ(); k <= pooledMutable2.getZ(); ++k) {
                            pooledMutable3.set(i, j, k);
                            BlockState blockState = this.world.getBlockState(pooledMutable3);
                            try {
                                blockState.onEntityCollision(this.world, pooledMutable3, this);
                                this.onBlockCollision(blockState);
                                continue;
                            } catch (Throwable throwable) {
                                CrashReport crashReport = CrashReport.create(throwable, "Colliding entity with block");
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

    protected void onBlockCollision(BlockState state) {
    }

    protected void playStepSound(BlockPos pos, BlockState state) {
        if (state.getMaterial().isLiquid()) {
            return;
        }
        BlockState blockState = this.world.getBlockState(pos.up());
        BlockSoundGroup blockSoundGroup = blockState.getBlock() == Blocks.SNOW ? blockState.getSoundGroup() : state.getSoundGroup();
        this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
    }

    protected void playSwimSound(float volume) {
        this.playSound(this.getSwimSound(), volume, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    protected float playFlySound(float distance) {
        return 0.0f;
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

    protected boolean canClimb() {
        return true;
    }

    protected void fall(double heightDifference, boolean onGround, BlockState landedState, BlockPos landedPosition) {
        if (onGround) {
            if (this.fallDistance > 0.0f) {
                landedState.getBlock().onLandedUpon(this.world, landedPosition, this, this.fallDistance);
            }
            this.fallDistance = 0.0f;
        } else if (heightDifference < 0.0) {
            this.fallDistance = (float)((double)this.fallDistance - heightDifference);
        }
    }

    @Nullable
    public Box getCollisionBox() {
        return null;
    }

    public final boolean isFireImmune() {
        return this.getType().isFireImmune();
    }

    public boolean handleFallDamage(float fallDistance, float damageMultiplier) {
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengerList()) {
                entity.handleFallDamage(fallDistance, damageMultiplier);
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
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.getEntityPos(this);){
            boolean bl = this.world.hasRain(pooledMutable) || this.world.hasRain(pooledMutable.set(this.getX(), this.getY() + (double)this.dimensions.height, this.getZ()));
            return bl;
        }
    }

    private boolean isInsideBubbleColumn() {
        return this.world.getBlockState(new BlockPos(this)).getBlock() == Blocks.BUBBLE_COLUMN;
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

    private void updateWaterState() {
        this.checkWaterState();
        this.updateSubmergedInWaterState();
        this.updateSwimming();
    }

    public void updateSwimming() {
        if (this.isSwimming()) {
            this.setSwimming(this.isSprinting() && this.isTouchingWater() && !this.hasVehicle());
        } else {
            this.setSwimming(this.isSprinting() && this.isSubmergedInWater() && !this.hasVehicle());
        }
    }

    public boolean checkWaterState() {
        if (this.getVehicle() instanceof BoatEntity) {
            this.touchingWater = false;
        } else if (this.updateMovementInFluid(FluidTags.WATER)) {
            if (!this.touchingWater && !this.firstUpdate) {
                this.onSwimmingStart();
            }
            this.fallDistance = 0.0f;
            this.touchingWater = true;
            this.extinguish();
        } else {
            this.touchingWater = false;
        }
        return this.touchingWater;
    }

    private void updateSubmergedInWaterState() {
        this.submergedInWater = this.isSubmergedIn(FluidTags.WATER, true);
    }

    protected void onSwimmingStart() {
        float k;
        float j;
        Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
        float f = entity == this ? 0.2f : 0.9f;
        Vec3d vec3d = entity.getVelocity();
        float g = MathHelper.sqrt(vec3d.x * vec3d.x * (double)0.2f + vec3d.y * vec3d.y + vec3d.z * vec3d.z * (double)0.2f) * f;
        if (g > 1.0f) {
            g = 1.0f;
        }
        if ((double)g < 0.25) {
            this.playSound(this.getSplashSound(), g, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        } else {
            this.playSound(this.getHighSpeedSplashSound(), g, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
        }
        float h = MathHelper.floor(this.getY());
        int i = 0;
        while ((float)i < 1.0f + this.dimensions.width * 20.0f) {
            j = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            k = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            this.world.addParticle(ParticleTypes.BUBBLE, this.getX() + (double)j, h + 1.0f, this.getZ() + (double)k, vec3d.x, vec3d.y - (double)(this.random.nextFloat() * 0.2f), vec3d.z);
            ++i;
        }
        i = 0;
        while ((float)i < 1.0f + this.dimensions.width * 20.0f) {
            j = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            k = (this.random.nextFloat() * 2.0f - 1.0f) * this.dimensions.width;
            this.world.addParticle(ParticleTypes.SPLASH, this.getX() + (double)j, h + 1.0f, this.getZ() + (double)k, vec3d.x, vec3d.y, vec3d.z);
            ++i;
        }
    }

    public void attemptSprintingParticles() {
        if (this.isSprinting() && !this.isTouchingWater()) {
            this.spawnSprintingParticles();
        }
    }

    protected void spawnSprintingParticles() {
        int k;
        int j;
        int i = MathHelper.floor(this.getX());
        BlockPos blockPos = new BlockPos(i, j = MathHelper.floor(this.getY() - (double)0.2f), k = MathHelper.floor(this.getZ()));
        BlockState blockState = this.world.getBlockState(blockPos);
        if (blockState.getRenderType() != BlockRenderType.INVISIBLE) {
            Vec3d vec3d = this.getVelocity();
            this.world.addParticle(new BlockStateParticleEffect(ParticleTypes.BLOCK, blockState), this.getX() + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width, this.getY() + 0.1, this.getZ() + ((double)this.random.nextFloat() - 0.5) * (double)this.dimensions.width, vec3d.x * -4.0, 1.5, vec3d.z * -4.0);
        }
    }

    public boolean isInFluid(Tag<Fluid> fluidTag) {
        return this.isSubmergedIn(fluidTag, false);
    }

    public boolean isSubmergedIn(Tag<Fluid> fluidTag, boolean requireLoadedChunk) {
        if (this.getVehicle() instanceof BoatEntity) {
            return false;
        }
        double d = this.getEyeY();
        BlockPos blockPos = new BlockPos(this.getX(), d, this.getZ());
        if (requireLoadedChunk && !this.world.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            return false;
        }
        FluidState fluidState = this.world.getFluidState(blockPos);
        return fluidState.matches(fluidTag) && d < (double)((float)blockPos.getY() + (fluidState.getHeight(this.world, blockPos) + 0.11111111f));
    }

    public void setInLava() {
        this.inLava = true;
    }

    public boolean isInLava() {
        return this.inLava;
    }

    public void updateVelocity(float speed, Vec3d movementInput) {
        Vec3d vec3d = Entity.movementInputToVelocity(movementInput, speed, this.yaw);
        this.setVelocity(this.getVelocity().add(vec3d));
    }

    private static Vec3d movementInputToVelocity(Vec3d movementInput, float speed, float yaw) {
        double d = movementInput.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d = (d > 1.0 ? movementInput.normalize() : movementInput).multiply(speed);
        float f = MathHelper.sin(yaw * ((float)Math.PI / 180));
        float g = MathHelper.cos(yaw * ((float)Math.PI / 180));
        return new Vec3d(vec3d.x * (double)g - vec3d.z * (double)f, vec3d.y, vec3d.z * (double)g + vec3d.x * (double)f);
    }

    public float getBrightnessAtEyes() {
        BlockPos.Mutable mutable = new BlockPos.Mutable(this.getX(), 0.0, this.getZ());
        if (this.world.isChunkLoaded(mutable)) {
            mutable.setY(MathHelper.floor(this.getEyeY()));
            return this.world.getBrightness(mutable);
        }
        return 0.0f;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void updatePositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        double d = MathHelper.clamp(x, -3.0E7, 3.0E7);
        double e = MathHelper.clamp(z, -3.0E7, 3.0E7);
        this.prevX = d;
        this.prevY = y;
        this.prevZ = e;
        this.updatePosition(d, y, e);
        this.yaw = yaw % 360.0f;
        this.pitch = MathHelper.clamp(pitch, -90.0f, 90.0f) % 360.0f;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void refreshPositionAndAngles(BlockPos pos, float yaw, float pitch) {
        this.refreshPositionAndAngles((double)pos.getX() + 0.5, pos.getY(), (double)pos.getZ() + 0.5, yaw, pitch);
    }

    public void refreshPositionAndAngles(double x, double y, double z, float yaw, float pitch) {
        this.resetPosition(x, y, z);
        this.yaw = yaw;
        this.pitch = pitch;
        this.refreshPosition();
    }

    public void resetPosition(double x, double y, double z) {
        this.setPos(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
        this.lastRenderX = x;
        this.lastRenderY = y;
        this.lastRenderZ = z;
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
        double e;
        if (this.isConnectedThroughVehicle(entity)) {
            return;
        }
        if (entity.noClip || this.noClip) {
            return;
        }
        double d = entity.getX() - this.getX();
        double f = MathHelper.absMax(d, e = entity.getZ() - this.getZ());
        if (f >= (double)0.01f) {
            f = MathHelper.sqrt(f);
            d /= f;
            e /= f;
            double g = 1.0 / f;
            if (g > 1.0) {
                g = 1.0;
            }
            d *= g;
            e *= g;
            d *= (double)0.05f;
            e *= (double)0.05f;
            d *= (double)(1.0f - this.pushSpeedReduction);
            e *= (double)(1.0f - this.pushSpeedReduction);
            if (!this.hasPassengers()) {
                this.addVelocity(-d, 0.0, -e);
            }
            if (!entity.hasPassengers()) {
                entity.addVelocity(d, 0.0, e);
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
        }
        this.scheduleVelocityUpdate();
        return false;
    }

    public final Vec3d getRotationVec(float tickDelta) {
        return this.getRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
    }

    public float getPitch(float tickDelta) {
        if (tickDelta == 1.0f) {
            return this.pitch;
        }
        return MathHelper.lerp(tickDelta, this.prevPitch, this.pitch);
    }

    public float getYaw(float tickDelta) {
        if (tickDelta == 1.0f) {
            return this.yaw;
        }
        return MathHelper.lerp(tickDelta, this.prevYaw, this.yaw);
    }

    protected final Vec3d getRotationVector(float pitch, float yaw) {
        float f = pitch * ((float)Math.PI / 180);
        float g = -yaw * ((float)Math.PI / 180);
        float h = MathHelper.cos(g);
        float i = MathHelper.sin(g);
        float j = MathHelper.cos(f);
        float k = MathHelper.sin(f);
        return new Vec3d(i * j, -k, h * j);
    }

    public final Vec3d getOppositeRotationVector(float tickDelta) {
        return this.getOppositeRotationVector(this.getPitch(tickDelta), this.getYaw(tickDelta));
    }

    protected final Vec3d getOppositeRotationVector(float pitch, float yaw) {
        return this.getRotationVector(pitch - 90.0f, yaw);
    }

    public final Vec3d getCameraPosVec(float tickDelta) {
        if (tickDelta == 1.0f) {
            return new Vec3d(this.getX(), this.getEyeY(), this.getZ());
        }
        double d = MathHelper.lerp((double)tickDelta, this.prevX, this.getX());
        double e = MathHelper.lerp((double)tickDelta, this.prevY, this.getY()) + (double)this.getStandingEyeHeight();
        double f = MathHelper.lerp((double)tickDelta, this.prevZ, this.getZ());
        return new Vec3d(d, e, f);
    }

    public HitResult rayTrace(double maxDistance, float tickDelta, boolean includeFluids) {
        Vec3d vec3d = this.getCameraPosVec(tickDelta);
        Vec3d vec3d2 = this.getRotationVec(tickDelta);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * maxDistance, vec3d2.y * maxDistance, vec3d2.z * maxDistance);
        return this.world.rayTrace(new RayTraceContext(vec3d, vec3d3, RayTraceContext.ShapeType.OUTLINE, includeFluids ? RayTraceContext.FluidHandling.ANY : RayTraceContext.FluidHandling.NONE, this));
    }

    public boolean collides() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void updateKilledAdvancementCriterion(Entity killer, int score, DamageSource damageSource) {
        if (killer instanceof ServerPlayerEntity) {
            Criterions.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)killer, this, damageSource);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double cameraX, double cameraY, double cameraZ) {
        double d = this.getX() - cameraX;
        double e = this.getY() - cameraY;
        double f = this.getZ() - cameraZ;
        double g = d * d + e * e + f * f;
        return this.shouldRender(g);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRender(double distance) {
        double d = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(d)) {
            d = 1.0;
        }
        return distance < (d *= 64.0 * renderDistanceMultiplier) * d;
    }

    public boolean saveSelfToTag(CompoundTag tag) {
        String string = this.getSavedEntityId();
        if (this.removed || string == null) {
            return false;
        }
        tag.putString("id", string);
        this.toTag(tag);
        return true;
    }

    public boolean saveToTag(CompoundTag tag) {
        if (this.hasVehicle()) {
            return false;
        }
        return this.saveSelfToTag(tag);
    }

    public CompoundTag toTag(CompoundTag tag) {
        try {
            ListTag listTag;
            tag.put("Pos", this.toListTag(this.getX(), this.getY(), this.getZ()));
            Vec3d vec3d = this.getVelocity();
            tag.put("Motion", this.toListTag(vec3d.x, vec3d.y, vec3d.z));
            tag.put("Rotation", this.toListTag(this.yaw, this.pitch));
            tag.putFloat("FallDistance", this.fallDistance);
            tag.putShort("Fire", (short)this.fireTicks);
            tag.putShort("Air", (short)this.getAir());
            tag.putBoolean("OnGround", this.onGround);
            tag.putInt("Dimension", this.dimension.getRawId());
            tag.putBoolean("Invulnerable", this.invulnerable);
            tag.putInt("PortalCooldown", this.netherPortalCooldown);
            tag.putUuid("UUID", this.getUuid());
            Text text = this.getCustomName();
            if (text != null) {
                tag.putString("CustomName", Text.Serializer.toJson(text));
            }
            if (this.isCustomNameVisible()) {
                tag.putBoolean("CustomNameVisible", this.isCustomNameVisible());
            }
            if (this.isSilent()) {
                tag.putBoolean("Silent", this.isSilent());
            }
            if (this.hasNoGravity()) {
                tag.putBoolean("NoGravity", this.hasNoGravity());
            }
            if (this.glowing) {
                tag.putBoolean("Glowing", this.glowing);
            }
            if (!this.scoreboardTags.isEmpty()) {
                listTag = new ListTag();
                for (String string : this.scoreboardTags) {
                    listTag.add(StringTag.of(string));
                }
                tag.put("Tags", listTag);
            }
            this.writeCustomDataToTag(tag);
            if (this.hasPassengers()) {
                listTag = new ListTag();
                for (Entity entity : this.getPassengerList()) {
                    CompoundTag compoundTag;
                    if (!entity.saveSelfToTag(compoundTag = new CompoundTag())) continue;
                    listTag.add(compoundTag);
                }
                if (!listTag.isEmpty()) {
                    tag.put("Passengers", listTag);
                }
            }
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Saving entity NBT");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
        return tag;
    }

    public void fromTag(CompoundTag tag) {
        try {
            ListTag listTag = tag.getList("Pos", 6);
            ListTag listTag2 = tag.getList("Motion", 6);
            ListTag listTag3 = tag.getList("Rotation", 5);
            double d = listTag2.getDouble(0);
            double e = listTag2.getDouble(1);
            double f = listTag2.getDouble(2);
            this.setVelocity(Math.abs(d) > 10.0 ? 0.0 : d, Math.abs(e) > 10.0 ? 0.0 : e, Math.abs(f) > 10.0 ? 0.0 : f);
            this.resetPosition(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
            this.yaw = listTag3.getFloat(0);
            this.pitch = listTag3.getFloat(1);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
            this.setHeadYaw(this.yaw);
            this.setYaw(this.yaw);
            this.fallDistance = tag.getFloat("FallDistance");
            this.fireTicks = tag.getShort("Fire");
            this.setAir(tag.getShort("Air"));
            this.onGround = tag.getBoolean("OnGround");
            if (tag.contains("Dimension")) {
                this.dimension = DimensionType.byRawId(tag.getInt("Dimension"));
            }
            this.invulnerable = tag.getBoolean("Invulnerable");
            this.netherPortalCooldown = tag.getInt("PortalCooldown");
            if (tag.containsUuid("UUID")) {
                this.uuid = tag.getUuid("UUID");
                this.uuidString = this.uuid.toString();
            }
            if (!(Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ()))) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite(this.yaw) || !Double.isFinite(this.pitch)) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.refreshPosition();
            this.setRotation(this.yaw, this.pitch);
            if (tag.contains("CustomName", 8)) {
                this.setCustomName(Text.Serializer.fromJson(tag.getString("CustomName")));
            }
            this.setCustomNameVisible(tag.getBoolean("CustomNameVisible"));
            this.setSilent(tag.getBoolean("Silent"));
            this.setNoGravity(tag.getBoolean("NoGravity"));
            this.setGlowing(tag.getBoolean("Glowing"));
            if (tag.contains("Tags", 9)) {
                this.scoreboardTags.clear();
                ListTag listTag4 = tag.getList("Tags", 8);
                int i = Math.min(listTag4.size(), 1024);
                for (int j = 0; j < i; ++j) {
                    this.scoreboardTags.add(listTag4.getString(j));
                }
            }
            this.readCustomDataFromTag(tag);
            if (this.shouldSetPositionOnLoad()) {
                this.refreshPosition();
            }
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Loading entity NBT");
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
        return !entityType.isSaveable() || identifier == null ? null : identifier.toString();
    }

    protected abstract void readCustomDataFromTag(CompoundTag var1);

    protected abstract void writeCustomDataToTag(CompoundTag var1);

    protected ListTag toListTag(double ... values) {
        ListTag listTag = new ListTag();
        for (double d : values) {
            listTag.add(DoubleTag.of(d));
        }
        return listTag;
    }

    protected ListTag toListTag(float ... values) {
        ListTag listTag = new ListTag();
        for (float f : values) {
            listTag.add(FloatTag.of(f));
        }
        return listTag;
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible item) {
        return this.dropItem(item, 0);
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible item, int yOffset) {
        return this.dropStack(new ItemStack(item), yOffset);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack stack) {
        return this.dropStack(stack, 0.0f);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack stack, float yOffset) {
        if (stack.isEmpty()) {
            return null;
        }
        if (this.world.isClient) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY() + (double)yOffset, this.getZ(), stack);
        itemEntity.setToDefaultPickupDelay();
        this.world.spawnEntity(itemEntity);
        return itemEntity;
    }

    public boolean isAlive() {
        return !this.removed;
    }

    public boolean isInsideWall() {
        if (this.noClip) {
            return false;
        }
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int i = 0; i < 8; ++i) {
                int j = MathHelper.floor(this.getY() + (double)(((float)((i >> 0) % 2) - 0.5f) * 0.1f) + (double)this.standingEyeHeight);
                int k = MathHelper.floor(this.getX() + (double)(((float)((i >> 1) % 2) - 0.5f) * this.dimensions.width * 0.8f));
                int l = MathHelper.floor(this.getZ() + (double)(((float)((i >> 2) % 2) - 0.5f) * this.dimensions.width * 0.8f));
                if (pooledMutable.getX() == k && pooledMutable.getY() == j && pooledMutable.getZ() == l) continue;
                pooledMutable.set(k, j, l);
                if (!this.world.getBlockState(pooledMutable).canSuffocate(this.world, pooledMutable)) continue;
                boolean bl = true;
                return bl;
            }
        }
        return false;
    }

    public boolean interact(PlayerEntity player, Hand hand) {
        return false;
    }

    @Nullable
    public Box getHardCollisionBox(Entity collidingEntity) {
        return null;
    }

    public void tickRiding() {
        this.setVelocity(Vec3d.ZERO);
        this.tick();
        if (!this.hasVehicle()) {
            return;
        }
        this.getVehicle().updatePassengerPosition(this);
    }

    public void updatePassengerPosition(Entity passenger) {
        this.updatePassengerPosition(passenger, Entity::updatePosition);
    }

    public void updatePassengerPosition(Entity passenger, PositionUpdater positionUpdater) {
        if (!this.hasPassenger(passenger)) {
            return;
        }
        positionUpdater.accept(passenger, this.getX(), this.getY() + this.getMountedHeightOffset() + passenger.getHeightOffset(), this.getZ());
    }

    @Environment(value=EnvType.CLIENT)
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

    @Environment(value=EnvType.CLIENT)
    public boolean isLiving() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity entity, boolean force) {
        Entity entity2 = entity;
        while (entity2.vehicle != null) {
            if (entity2.vehicle == this) {
                return false;
            }
            entity2 = entity2.vehicle;
        }
        if (!(force || this.canStartRiding(entity) && entity.canAddPassenger(this))) {
            return false;
        }
        if (this.hasVehicle()) {
            this.stopRiding();
        }
        this.vehicle = entity;
        this.vehicle.addPassenger(this);
        return true;
    }

    protected boolean canStartRiding(Entity entity) {
        return this.ridingCooldown <= 0;
    }

    protected boolean wouldPoseNotCollide(EntityPose pose) {
        return this.world.doesNotCollide(this, this.calculateBoundsForPose(pose));
    }

    public void removeAllPassengers() {
        for (int i = this.passengerList.size() - 1; i >= 0; --i) {
            this.passengerList.get(i).stopRiding();
        }
    }

    public void stopRiding() {
        if (this.vehicle != null) {
            Entity entity = this.vehicle;
            this.vehicle = null;
            entity.removePassenger(this);
        }
    }

    protected void addPassenger(Entity passenger) {
        if (passenger.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.world.isClient && passenger instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
            this.passengerList.add(0, passenger);
        } else {
            this.passengerList.add(passenger);
        }
    }

    protected void removePassenger(Entity passenger) {
        if (passenger.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengerList.remove(passenger);
        passenger.ridingCooldown = 60;
    }

    protected boolean canAddPassenger(Entity passenger) {
        return this.getPassengerList().size() < 1;
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.updatePosition(x, y, z);
        this.setRotation(yaw, pitch);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedHeadRotation(float yaw, int interpolationSteps) {
        this.setHeadYaw(yaw);
    }

    public float getTargetingMargin() {
        return 0.0f;
    }

    public Vec3d getRotationVector() {
        return this.getRotationVector(this.pitch, this.yaw);
    }

    public Vec2f getRotationClient() {
        return new Vec2f(this.pitch, this.yaw);
    }

    @Environment(value=EnvType.CLIENT)
    public Vec3d getRotationVecClient() {
        return Vec3d.fromPolar(this.getRotationClient());
    }

    public void setInNetherPortal(BlockPos pos) {
        if (this.netherPortalCooldown > 0) {
            this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
            return;
        }
        if (!this.world.isClient && !pos.equals(this.lastNetherPortalPosition)) {
            this.lastNetherPortalPosition = new BlockPos(pos);
            NetherPortalBlock cfr_ignored_0 = (NetherPortalBlock)Blocks.NETHER_PORTAL;
            BlockPattern.Result result = NetherPortalBlock.findPortal(this.world, this.lastNetherPortalPosition);
            double d = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
            double e = Math.abs(MathHelper.minusDiv((result.getForwards().getAxis() == Direction.Axis.X ? this.getZ() : this.getX()) - (double)(result.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d, d - (double)result.getWidth()));
            double f = MathHelper.minusDiv(this.getY() - 1.0, result.getFrontTopLeft().getY(), result.getFrontTopLeft().getY() - result.getHeight());
            this.lastNetherPortalDirectionVector = new Vec3d(e, f, 0.0);
            this.lastNetherPortalDirection = result.getForwards();
        }
        this.inNetherPortal = true;
    }

    protected void tickNetherPortal() {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        int i = this.getMaxNetherPortalTime();
        if (this.inNetherPortal) {
            if (this.world.getServer().isNetherAllowed() && !this.hasVehicle() && this.netherPortalTime++ >= i) {
                this.world.getProfiler().push("portal");
                this.netherPortalTime = i;
                this.netherPortalCooldown = this.getDefaultNetherPortalCooldown();
                this.changeDimension(this.world.dimension.getType() == DimensionType.THE_NETHER ? DimensionType.OVERWORLD : DimensionType.THE_NETHER);
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

    public int getDefaultNetherPortalCooldown() {
        return 300;
    }

    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.setVelocity(x, y, z);
    }

    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte status) {
        switch (status) {
            case 53: {
                HoneyBlock.addRegularParticles(this);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
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
        return !this.isFireImmune() && (this.fireTicks > 0 || bl && this.getFlag(0));
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

    public void setSneaking(boolean sneaking) {
        this.setFlag(1, sneaking);
    }

    public boolean isSneaking() {
        return this.getFlag(1);
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

    public boolean isInSneakingPose() {
        return this.getPose() == EntityPose.CROUCHING;
    }

    public boolean isSprinting() {
        return this.getFlag(3);
    }

    public void setSprinting(boolean sprinting) {
        this.setFlag(3, sprinting);
    }

    public boolean isSwimming() {
        return this.getFlag(4);
    }

    public boolean isInSwimmingPose() {
        return this.getPose() == EntityPose.SWIMMING;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldLeaveSwimmingPose() {
        return this.isInSwimmingPose() && !this.isTouchingWater();
    }

    public void setSwimming(boolean swimming) {
        this.setFlag(4, swimming);
    }

    public boolean isGlowing() {
        return this.glowing || this.world.isClient && this.getFlag(6);
    }

    public void setGlowing(boolean glowing) {
        this.glowing = glowing;
        if (!this.world.isClient) {
            this.setFlag(6, this.glowing);
        }
    }

    public boolean isInvisible() {
        return this.getFlag(5);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean isInvisibleTo(PlayerEntity player) {
        if (player.isSpectator()) {
            return false;
        }
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        if (abstractTeam != null && player != null && player.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles()) {
            return false;
        }
        return this.isInvisible();
    }

    @Nullable
    public AbstractTeam getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
    }

    public boolean isTeammate(Entity other) {
        return this.isTeamPlayer(other.getScoreboardTeam());
    }

    public boolean isTeamPlayer(AbstractTeam team) {
        if (this.getScoreboardTeam() != null) {
            return this.getScoreboardTeam().isEqual(team);
        }
        return false;
    }

    public void setInvisible(boolean invisible) {
        this.setFlag(5, invisible);
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

    public void onStruckByLightning(LightningEntity lightning) {
        ++this.fireTicks;
        if (this.fireTicks == 0) {
            this.setOnFireFor(8);
        }
        this.damage(DamageSource.LIGHTNING_BOLT, 5.0f);
    }

    public void onBubbleColumnSurfaceCollision(boolean drag) {
        Vec3d vec3d = this.getVelocity();
        double d = drag ? Math.max(-0.9, vec3d.y - 0.03) : Math.min(1.8, vec3d.y + 0.1);
        this.setVelocity(vec3d.x, d, vec3d.z);
    }

    public void onBubbleColumnCollision(boolean drag) {
        Vec3d vec3d = this.getVelocity();
        double d = drag ? Math.max(-0.3, vec3d.y - 0.03) : Math.min(0.7, vec3d.y + 0.06);
        this.setVelocity(vec3d.x, d, vec3d.z);
        this.fallDistance = 0.0f;
    }

    public void onKilledOther(LivingEntity other) {
    }

    protected void pushOutOfBlocks(double x, double y, double z) {
        BlockPos blockPos = new BlockPos(x, y, z);
        Vec3d vec3d = new Vec3d(x - (double)blockPos.getX(), y - (double)blockPos.getY(), z - (double)blockPos.getZ());
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        Direction direction = Direction.UP;
        double d = Double.MAX_VALUE;
        for (Direction direction2 : new Direction[]{Direction.NORTH, Direction.SOUTH, Direction.WEST, Direction.EAST, Direction.UP}) {
            double f;
            mutable.set(blockPos).setOffset(direction2);
            if (this.world.getBlockState(mutable).isFullCube(this.world, mutable)) continue;
            double e = vec3d.getComponentAlongAxis(direction2.getAxis());
            double d2 = f = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - e : e;
            if (!(f < d)) continue;
            d = f;
            direction = direction2;
        }
        float g = this.random.nextFloat() * 0.2f + 0.1f;
        float h = direction.getDirection().offset();
        Vec3d vec3d2 = this.getVelocity().multiply(0.75);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setVelocity(h * g, vec3d2.y, vec3d2.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setVelocity(vec3d2.x, h * g, vec3d2.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setVelocity(vec3d2.x, vec3d2.y, h * g);
        }
    }

    public void slowMovement(BlockState state, Vec3d multiplier) {
        this.fallDistance = 0.0f;
        this.movementMultiplier = multiplier;
    }

    private static void removeClickEvents(Text textComponent) {
        textComponent.styled(style -> style.setClickEvent(null)).getSiblings().forEach(Entity::removeClickEvents);
    }

    @Override
    public Text getName() {
        Text text = this.getCustomName();
        if (text != null) {
            Text text2 = text.deepCopy();
            Entity.removeClickEvents(text2);
            return text2;
        }
        return this.getDefaultName();
    }

    protected Text getDefaultName() {
        return this.type.getName();
    }

    public boolean isPartOf(Entity entity) {
        return this == entity;
    }

    public float getHeadYaw() {
        return 0.0f;
    }

    public void setHeadYaw(float headYaw) {
    }

    public void setYaw(float yaw) {
    }

    public boolean isAttackable() {
        return true;
    }

    public boolean handleAttack(Entity attacker) {
        return false;
    }

    public String toString() {
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().asString(), this.entityId, this.world == null ? "~NULL~" : this.world.getLevelProperties().getLevelName(), this.getX(), this.getY(), this.getZ());
    }

    public boolean isInvulnerableTo(DamageSource damageSource) {
        return this.invulnerable && damageSource != DamageSource.OUT_OF_WORLD && !damageSource.isSourceCreativePlayer();
    }

    public boolean isInvulnerable() {
        return this.invulnerable;
    }

    public void setInvulnerable(boolean invulnerable) {
        this.invulnerable = invulnerable;
    }

    public void copyPositionAndRotation(Entity entity) {
        this.refreshPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
    }

    public void copyFrom(Entity original) {
        CompoundTag compoundTag = original.toTag(new CompoundTag());
        compoundTag.remove("Dimension");
        this.fromTag(compoundTag);
        this.netherPortalCooldown = original.netherPortalCooldown;
        this.lastNetherPortalPosition = original.lastNetherPortalPosition;
        this.lastNetherPortalDirectionVector = original.lastNetherPortalDirectionVector;
        this.lastNetherPortalDirection = original.lastNetherPortalDirection;
    }

    @Nullable
    public Entity changeDimension(DimensionType newDimension) {
        BlockPos blockPos;
        if (this.world.isClient || this.removed) {
            return null;
        }
        this.world.getProfiler().push("changeDimension");
        MinecraftServer minecraftServer = this.getServer();
        DimensionType dimensionType = this.dimension;
        ServerWorld serverWorld = minecraftServer.getWorld(dimensionType);
        ServerWorld serverWorld2 = minecraftServer.getWorld(newDimension);
        this.dimension = newDimension;
        this.detach();
        this.world.getProfiler().push("reposition");
        Vec3d vec3d = this.getVelocity();
        float f = 0.0f;
        if (dimensionType == DimensionType.THE_END && newDimension == DimensionType.OVERWORLD) {
            blockPos = serverWorld2.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverWorld2.getSpawnPos());
        } else if (newDimension == DimensionType.THE_END) {
            blockPos = serverWorld2.getForcedSpawnPoint();
        } else {
            double d = this.getX();
            double e = this.getZ();
            double g = 8.0;
            if (dimensionType == DimensionType.OVERWORLD && newDimension == DimensionType.THE_NETHER) {
                d /= 8.0;
                e /= 8.0;
            } else if (dimensionType == DimensionType.THE_NETHER && newDimension == DimensionType.OVERWORLD) {
                d *= 8.0;
                e *= 8.0;
            }
            double h = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundWest() + 16.0);
            double i = Math.min(-2.9999872E7, serverWorld2.getWorldBorder().getBoundNorth() + 16.0);
            double j = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundEast() - 16.0);
            double k = Math.min(2.9999872E7, serverWorld2.getWorldBorder().getBoundSouth() - 16.0);
            d = MathHelper.clamp(d, h, j);
            e = MathHelper.clamp(e, i, k);
            Vec3d vec3d2 = this.getLastNetherPortalDirectionVector();
            blockPos = new BlockPos(d, this.getY(), e);
            BlockPattern.TeleportTarget teleportTarget = serverWorld2.getPortalForcer().getPortal(blockPos, vec3d, this.getLastNetherPortalDirection(), vec3d2.x, vec3d2.y, this instanceof PlayerEntity);
            if (teleportTarget == null) {
                return null;
            }
            blockPos = new BlockPos(teleportTarget.pos);
            vec3d = teleportTarget.velocity;
            f = teleportTarget.yaw;
        }
        this.world.getProfiler().swap("reloading");
        Object entity = this.getType().create(serverWorld2);
        if (entity != null) {
            ((Entity)entity).copyFrom(this);
            ((Entity)entity).refreshPositionAndAngles(blockPos, ((Entity)entity).yaw + f, ((Entity)entity).pitch);
            ((Entity)entity).setVelocity(vec3d);
            serverWorld2.onDimensionChanged((Entity)entity);
        }
        this.removed = true;
        this.world.getProfiler().pop();
        serverWorld.resetIdleTimeout();
        serverWorld2.resetIdleTimeout();
        this.world.getProfiler().pop();
        return entity;
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

    public Vec3d getLastNetherPortalDirectionVector() {
        return this.lastNetherPortalDirectionVector;
    }

    public Direction getLastNetherPortalDirection() {
        return this.lastNetherPortalDirection;
    }

    public boolean canAvoidTraps() {
        return false;
    }

    public void populateCrashReport(CrashReportSection section) {
        section.add("Entity Type", () -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        section.add("Entity ID", this.entityId);
        section.add("Entity Name", () -> this.getName().getString());
        section.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
        section.add("Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
        Vec3d vec3d = this.getVelocity();
        section.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
        section.add("Entity's Passengers", () -> this.getPassengerList().toString());
        section.add("Entity's Vehicle", () -> this.getVehicle().toString());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        return this.isOnFire() && !this.isSpectator();
    }

    public void setUuid(UUID uuid) {
        this.uuid = uuid;
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

    @Environment(value=EnvType.CLIENT)
    public static double getRenderDistanceMultiplier() {
        return renderDistanceMultiplier;
    }

    @Environment(value=EnvType.CLIENT)
    public static void setRenderDistanceMultiplier(double value) {
        renderDistanceMultiplier = value;
    }

    @Override
    public Text getDisplayName() {
        return Team.modifyText(this.getScoreboardTeam(), this.getName()).styled(style -> style.setHoverEvent(this.getHoverEvent()).setInsertion(this.getUuidAsString()));
    }

    public void setCustomName(@Nullable Text name) {
        this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(name));
    }

    @Override
    @Nullable
    public Text getCustomName() {
        return this.dataTracker.get(CUSTOM_NAME).orElse(null);
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
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        ChunkPos chunkPos = new ChunkPos(new BlockPos(destX, destY, destZ));
        ((ServerWorld)this.world).getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 0, this.getEntityId());
        this.world.getChunk(chunkPos.x, chunkPos.z);
        this.requestTeleport(destX, destY, destZ);
    }

    public void requestTeleport(double destX, double destY, double destZ) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        ServerWorld serverWorld = (ServerWorld)this.world;
        this.refreshPositionAndAngles(destX, destY, destZ, this.yaw, this.pitch);
        this.streamPassengersRecursively().forEach(entity -> {
            serverWorld.checkChunk((Entity)entity);
            entity.teleportRequested = true;
            entity.updatePositionsRecursively(Entity::positAfterTeleport);
        });
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    public void onTrackedDataSet(TrackedData<?> data) {
        if (POSE.equals(data)) {
            this.calculateDimensions();
        }
    }

    public void calculateDimensions() {
        EntityDimensions entityDimensions2;
        EntityDimensions entityDimensions = this.dimensions;
        EntityPose entityPose = this.getPose();
        this.dimensions = entityDimensions2 = this.getDimensions(entityPose);
        this.standingEyeHeight = this.getEyeHeight(entityPose, entityDimensions2);
        if (entityDimensions2.width < entityDimensions.width) {
            double d = (double)entityDimensions2.width / 2.0;
            this.setBoundingBox(new Box(this.getX() - d, this.getY(), this.getZ() - d, this.getX() + d, this.getY() + (double)entityDimensions2.height, this.getZ() + d));
            return;
        }
        Box box = this.getBoundingBox();
        this.setBoundingBox(new Box(box.x1, box.y1, box.z1, box.x1 + (double)entityDimensions2.width, box.y1 + (double)entityDimensions2.height, box.z1 + (double)entityDimensions2.width));
        if (entityDimensions2.width > entityDimensions.width && !this.firstUpdate && !this.world.isClient) {
            float f = entityDimensions.width - entityDimensions2.width;
            this.move(MovementType.SELF, new Vec3d(f, 0.0, f));
        }
    }

    public Direction getHorizontalFacing() {
        return Direction.fromRotation(this.yaw);
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

    public boolean canBeSpectated(ServerPlayerEntity spectator) {
        return true;
    }

    public Box getBoundingBox() {
        return this.entityBounds;
    }

    @Environment(value=EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        return this.getBoundingBox();
    }

    protected Box calculateBoundsForPose(EntityPose pos) {
        EntityDimensions entityDimensions = this.getDimensions(pos);
        float f = entityDimensions.width / 2.0f;
        Vec3d vec3d = new Vec3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
        Vec3d vec3d2 = new Vec3d(this.getX() + (double)f, this.getY() + (double)entityDimensions.height, this.getZ() + (double)f);
        return new Box(vec3d, vec3d2);
    }

    public void setBoundingBox(Box boundingBox) {
        this.entityBounds = boundingBox;
    }

    protected float getEyeHeight(EntityPose pose, EntityDimensions dimensions) {
        return dimensions.height * 0.85f;
    }

    @Environment(value=EnvType.CLIENT)
    public float getEyeHeight(EntityPose pose) {
        return this.getEyeHeight(pose, this.getDimensions(pose));
    }

    public final float getStandingEyeHeight() {
        return this.standingEyeHeight;
    }

    public boolean equip(int slot, ItemStack item) {
        return false;
    }

    @Override
    public void sendMessage(Text message) {
    }

    public BlockPos getBlockPos() {
        return new BlockPos(this);
    }

    public Vec3d getPosVector() {
        return this.getPos();
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

    protected void dealDamage(LivingEntity attacker, Entity target) {
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
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (rotation) {
            case CLOCKWISE_180: {
                return f + 180.0f;
            }
            case COUNTERCLOCKWISE_90: {
                return f + 270.0f;
            }
            case CLOCKWISE_90: {
                return f + 90.0f;
            }
        }
        return f;
    }

    public float applyMirror(BlockMirror mirror) {
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (mirror) {
            case LEFT_RIGHT: {
                return -f;
            }
            case FRONT_BACK: {
                return 180.0f - f;
            }
        }
        return f;
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
        if (this.passengerList.isEmpty()) {
            return Collections.emptyList();
        }
        return Lists.newArrayList(this.passengerList);
    }

    public boolean hasPassenger(Entity passenger) {
        for (Entity entity : this.getPassengerList()) {
            if (!entity.equals(passenger)) continue;
            return true;
        }
        return false;
    }

    public boolean hasPassengerType(Class<? extends Entity> clazz) {
        for (Entity entity : this.getPassengerList()) {
            if (!clazz.isAssignableFrom(entity.getClass())) continue;
            return true;
        }
        return false;
    }

    public Collection<Entity> getPassengersDeep() {
        HashSet<Entity> set = Sets.newHashSet();
        for (Entity entity : this.getPassengerList()) {
            set.add(entity);
            entity.collectPassengers(false, set);
        }
        return set;
    }

    public Stream<Entity> streamPassengersRecursively() {
        return Stream.concat(Stream.of(this), this.passengerList.stream().flatMap(Entity::streamPassengersRecursively));
    }

    public boolean hasPlayerRider() {
        HashSet<Entity> set = Sets.newHashSet();
        this.collectPassengers(true, set);
        return set.size() == 1;
    }

    private void collectPassengers(boolean playersOnly, Set<Entity> output) {
        for (Entity entity : this.getPassengerList()) {
            if (!playersOnly || ServerPlayerEntity.class.isAssignableFrom(entity.getClass())) {
                output.add(entity);
            }
            entity.collectPassengers(playersOnly, output);
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

    public boolean hasPassengerDeep(Entity passenger) {
        for (Entity entity : this.getPassengerList()) {
            if (entity.equals(passenger)) {
                return true;
            }
            if (!entity.hasPassengerDeep(passenger)) continue;
            return true;
        }
        return false;
    }

    public void updatePositionsRecursively(PositionUpdater positionUpdater) {
        for (Entity entity : this.passengerList) {
            this.updatePassengerPosition(entity, positionUpdater);
        }
    }

    public boolean isLogicalSideForUpdatingMovement() {
        Entity entity = this.getPrimaryPassenger();
        if (entity instanceof PlayerEntity) {
            return ((PlayerEntity)entity).isMainPlayer();
        }
        return !this.world.isClient;
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
        return new ServerCommandSource(this, this.getPos(), this.getRotationClient(), this.world instanceof ServerWorld ? (ServerWorld)this.world : null, this.getPermissionLevel(), this.getName().getString(), this.getDisplayName(), this.world.getServer(), this);
    }

    protected int getPermissionLevel() {
        return 0;
    }

    public boolean allowsPermissionLevel(int permissionLevel) {
        return this.getPermissionLevel() >= permissionLevel;
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

    public void lookAt(EntityAnchorArgumentType.EntityAnchor anchorPoint, Vec3d target) {
        Vec3d vec3d = anchorPoint.positionAt(this);
        double d = target.x - vec3d.x;
        double e = target.y - vec3d.y;
        double f = target.z - vec3d.z;
        double g = MathHelper.sqrt(d * d + f * f);
        this.pitch = MathHelper.wrapDegrees((float)(-(MathHelper.atan2(e, g) * 57.2957763671875)));
        this.yaw = MathHelper.wrapDegrees((float)(MathHelper.atan2(f, d) * 57.2957763671875) - 90.0f);
        this.setHeadYaw(this.yaw);
        this.prevPitch = this.pitch;
        this.prevYaw = this.yaw;
    }

    public boolean updateMovementInFluid(Tag<Fluid> tag) {
        int n;
        Box box = this.getBoundingBox().contract(0.001);
        int i = MathHelper.floor(box.x1);
        int j = MathHelper.ceil(box.x2);
        int k = MathHelper.floor(box.y1);
        int l = MathHelper.ceil(box.y2);
        int m = MathHelper.floor(box.z1);
        if (!this.world.isRegionLoaded(i, k, m, j, l, n = MathHelper.ceil(box.z2))) {
            return false;
        }
        double d = 0.0;
        boolean bl = this.canFly();
        boolean bl2 = false;
        Vec3d vec3d = Vec3d.ZERO;
        int o = 0;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int p = i; p < j; ++p) {
                for (int q = k; q < l; ++q) {
                    for (int r = m; r < n; ++r) {
                        double e;
                        pooledMutable.set(p, q, r);
                        FluidState fluidState = this.world.getFluidState(pooledMutable);
                        if (!fluidState.matches(tag) || !((e = (double)((float)q + fluidState.getHeight(this.world, pooledMutable))) >= box.y1)) continue;
                        bl2 = true;
                        d = Math.max(e - box.y1, d);
                        if (!bl) continue;
                        Vec3d vec3d2 = fluidState.getVelocity(this.world, pooledMutable);
                        if (d < 0.4) {
                            vec3d2 = vec3d2.multiply(d);
                        }
                        vec3d = vec3d.add(vec3d2);
                        ++o;
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

    public EntityDimensions getDimensions(EntityPose pose) {
        return this.type.getDimensions();
    }

    public Vec3d getPos() {
        return new Vec3d(this.x, this.y, this.z);
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

    public final double getX() {
        return this.x;
    }

    public double offsetX(double widthScale) {
        return this.x + (double)this.getWidth() * widthScale;
    }

    public double getParticleX(double widthScale) {
        return this.offsetX((2.0 * this.random.nextDouble() - 1.0) * widthScale);
    }

    public final double getY() {
        return this.y;
    }

    public double getBodyY(double heightScale) {
        return this.y + (double)this.getHeight() * heightScale;
    }

    public double getRandomBodyY() {
        return this.getBodyY(this.random.nextDouble());
    }

    public double getEyeY() {
        return this.y + (double)this.standingEyeHeight;
    }

    public final double getZ() {
        return this.z;
    }

    public double offsetZ(double widthScale) {
        return this.z + (double)this.getWidth() * widthScale;
    }

    public double getParticleZ(double widthScale) {
        return this.offsetZ((2.0 * this.random.nextDouble() - 1.0) * widthScale);
    }

    public void setPos(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public void checkDespawn() {
    }

    public void positAfterTeleport(double x, double y, double z) {
        this.refreshPositionAndAngles(x, y, z, this.yaw, this.pitch);
    }

    @FunctionalInterface
    public static interface PositionUpdater {
        public void accept(Entity var1, double var2, double var4, double var6);
    }
}

