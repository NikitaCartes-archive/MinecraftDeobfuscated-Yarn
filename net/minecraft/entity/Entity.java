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
import net.minecraft.block.PortalBlock;
import net.minecraft.block.pattern.BlockPattern;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.client.network.packet.EntityS2CPacket;
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
import net.minecraft.world.LightType;
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
    public double prevRenderX;
    public double prevRenderY;
    public double prevRenderZ;
    public float stepHeight;
    public boolean noClip;
    public float pushSpeedReduction;
    protected final Random random = new Random();
    public int age;
    private int fireTicks = -this.getBurningDuration();
    protected boolean insideWater;
    protected double waterHeight;
    protected boolean inWater;
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
    private final Set<String> scoreboardTags = Sets.newHashSet();
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
        if (this.scoreboardTags.size() >= 1024) {
            return false;
        }
        return this.scoreboardTags.add(string);
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
        if (object instanceof Entity) {
            return ((Entity)object).entityId == this.entityId;
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
            this.setPosition(this.getX(), d, this.getZ());
            if (this.world.doesNotCollide(this)) break;
        }
        this.setVelocity(Vec3d.ZERO);
        this.pitch = 0.0f;
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
        this.yaw = f % 360.0f;
        this.pitch = g % 360.0f;
    }

    public void setPosition(double d, double e, double f) {
        this.setPos(d, e, f);
        float g = this.dimensions.width / 2.0f;
        float h = this.dimensions.height;
        this.setBoundingBox(new Box(d - (double)g, e, f - (double)g, d + (double)g, e + (double)h, f + (double)g));
    }

    protected void method_23311() {
        this.setPosition(this.x, this.y, this.z);
    }

    @Environment(value=EnvType.CLIENT)
    public void changeLookDirection(double d, double e) {
        double f = e * 0.15;
        double g = d * 0.15;
        this.pitch = (float)((double)this.pitch + f);
        this.yaw = (float)((double)this.yaw + g);
        this.pitch = MathHelper.clamp(this.pitch, -90.0f, 90.0f);
        this.prevPitch = (float)((double)this.prevPitch + f);
        this.prevYaw = (float)((double)this.prevYaw + g);
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
        this.tickPortal();
        this.attemptSprintingParticles();
        this.updateInWater();
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

    protected void tickPortalCooldown() {
        if (this.portalCooldown > 0) {
            --this.portalCooldown;
        }
    }

    public int getMaxPortalTime() {
        return 1;
    }

    protected void setOnFireFromLava() {
        if (this.isFireImmune()) {
            return;
        }
        this.setOnFireFor(15);
        this.damage(DamageSource.LAVA, 4.0f);
    }

    public void setOnFireFor(int i) {
        int j = i * 20;
        if (this instanceof LivingEntity) {
            j = ProtectionEnchantment.transformFireDuration((LivingEntity)this, j);
        }
        if (this.fireTicks < j) {
            this.fireTicks = j;
        }
    }

    public void setFireTicks(int i) {
        this.fireTicks = i;
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

    public boolean doesNotCollide(double d, double e, double f) {
        return this.doesNotCollide(this.getBoundingBox().offset(d, e, f));
    }

    private boolean doesNotCollide(Box box) {
        return this.world.doesNotCollide(this, box) && !this.world.containsFluid(box);
    }

    public void move(MovementType movementType, Vec3d vec3d) {
        Vec3d vec3d2;
        if (this.noClip) {
            this.setBoundingBox(this.getBoundingBox().offset(vec3d));
            this.moveToBoundingBoxCenter();
            return;
        }
        if (movementType == MovementType.PISTON && (vec3d = this.adjustMovementForPiston(vec3d)).equals(Vec3d.ZERO)) {
            return;
        }
        this.world.getProfiler().push("move");
        if (this.movementMultiplier.lengthSquared() > 1.0E-7) {
            vec3d = vec3d.multiply(this.movementMultiplier);
            this.movementMultiplier = Vec3d.ZERO;
        }
        if ((vec3d2 = this.adjustMovementForCollisions(vec3d = this.adjustMovementForSneaking(vec3d, movementType))).lengthSquared() > 1.0E-7) {
            this.setBoundingBox(this.getBoundingBox().offset(vec3d2));
            this.moveToBoundingBoxCenter();
        }
        this.world.getProfiler().pop();
        this.world.getProfiler().push("rest");
        this.horizontalCollision = !MathHelper.approximatelyEquals(vec3d.x, vec3d2.x) || !MathHelper.approximatelyEquals(vec3d.z, vec3d2.z);
        this.verticalCollision = vec3d.y != vec3d2.y;
        this.onGround = this.verticalCollision && vec3d.y < 0.0;
        this.collided = this.horizontalCollision || this.verticalCollision;
        BlockPos blockPos = this.method_23312();
        BlockState blockState = this.world.getBlockState(blockPos);
        this.fall(vec3d2.y, this.onGround, blockState, blockPos);
        Vec3d vec3d3 = this.getVelocity();
        if (vec3d.x != vec3d2.x) {
            this.setVelocity(0.0, vec3d3.y, vec3d3.z);
        }
        if (vec3d.z != vec3d2.z) {
            this.setVelocity(vec3d3.x, vec3d3.y, 0.0);
        }
        Block block = blockState.getBlock();
        if (vec3d.y != vec3d2.y) {
            block.onEntityLand(this.world, this);
        }
        if (this.onGround && !this.method_21749()) {
            block.onSteppedOn(this.world, blockPos, this);
        }
        if (this.canClimb() && !this.hasVehicle()) {
            double d = vec3d2.x;
            double e = vec3d2.y;
            double f = vec3d2.z;
            if (block != Blocks.LADDER && block != Blocks.SCAFFOLDING) {
                e = 0.0;
            }
            this.horizontalSpeed = (float)((double)this.horizontalSpeed + (double)MathHelper.sqrt(Entity.squaredHorizontalLength(vec3d2)) * 0.6);
            this.distanceTraveled = (float)((double)this.distanceTraveled + (double)MathHelper.sqrt(d * d + e * e + f * f) * 0.6);
            if (this.distanceTraveled > this.nextStepSoundDistance && !blockState.isAir()) {
                this.nextStepSoundDistance = this.calculateNextStepSoundDistance();
                if (this.isInsideWater()) {
                    Entity entity = this.hasPassengers() && this.getPrimaryPassenger() != null ? this.getPrimaryPassenger() : this;
                    float g = entity == this ? 0.35f : 0.4f;
                    Vec3d vec3d4 = entity.getVelocity();
                    float h = MathHelper.sqrt(vec3d4.x * vec3d4.x * (double)0.2f + vec3d4.y * vec3d4.y + vec3d4.z * vec3d4.z * (double)0.2f) * g;
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
        this.setVelocity(this.getVelocity().multiply(this.method_23326(), 1.0, this.method_23326()));
        boolean bl = this.isTouchingWater();
        if (this.world.doesAreaContainFireSource(this.getBoundingBox().contract(0.001))) {
            if (!bl) {
                ++this.fireTicks;
                if (this.fireTicks == 0) {
                    this.setOnFireFor(8);
                }
            }
            this.burn(1);
        } else if (this.fireTicks <= 0) {
            this.fireTicks = -this.getBurningDuration();
        }
        if (bl && this.isOnFire()) {
            this.playSound(SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, 0.7f, 1.6f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
            this.fireTicks = -this.getBurningDuration();
        }
        this.world.getProfiler().pop();
    }

    protected BlockPos method_23312() {
        BlockPos blockPos2;
        BlockState blockState;
        Block block;
        int k;
        int j;
        int i = MathHelper.floor(this.x);
        BlockPos blockPos = new BlockPos(i, j = MathHelper.floor(this.y - (double)0.2f), k = MathHelper.floor(this.z));
        if (this.world.getBlockState(blockPos).isAir() && ((block = (blockState = this.world.getBlockState(blockPos2 = blockPos.method_10074())).getBlock()).matches(BlockTags.FENCES) || block.matches(BlockTags.WALLS) || block instanceof FenceGateBlock)) {
            return blockPos2;
        }
        return blockPos;
    }

    protected float method_23313() {
        float f = this.world.getBlockState(new BlockPos(this)).getBlock().method_23350();
        float g = this.world.getBlockState(this.method_23314()).getBlock().method_23350();
        return (double)f == 1.0 ? g : f;
    }

    private float method_23326() {
        float f = this.world.getBlockState(new BlockPos(this)).getBlock().method_23349();
        float g = this.world.getBlockState(this.method_23314()).getBlock().method_23349();
        return (double)f == 1.0 ? g : f;
    }

    protected BlockPos method_23314() {
        return new BlockPos(this.x, this.getBoundingBox().y1 - 0.5000001, this.z);
    }

    protected Vec3d adjustMovementForSneaking(Vec3d vec3d, MovementType movementType) {
        return vec3d;
    }

    protected Vec3d adjustMovementForPiston(Vec3d vec3d) {
        if (vec3d.lengthSquared() <= 1.0E-7) {
            return vec3d;
        }
        long l = this.world.getTime();
        if (l != this.pistonMovementTick) {
            Arrays.fill(this.pistonMovementDelta, 0.0);
            this.pistonMovementTick = l;
        }
        if (vec3d.x != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.X, vec3d.x);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(d, 0.0, 0.0);
        }
        if (vec3d.y != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.Y, vec3d.y);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, d, 0.0);
        }
        if (vec3d.z != 0.0) {
            double d = this.calculatePistonMovementFactor(Direction.Axis.Z, vec3d.z);
            return Math.abs(d) <= (double)1.0E-5f ? Vec3d.ZERO : new Vec3d(0.0, 0.0, d);
        }
        return Vec3d.ZERO;
    }

    private double calculatePistonMovementFactor(Direction.Axis axis, double d) {
        int i = axis.ordinal();
        double e = MathHelper.clamp(d + this.pistonMovementDelta[i], -0.51, 0.51);
        d = e - this.pistonMovementDelta[i];
        this.pistonMovementDelta[i] = e;
        return d;
    }

    private Vec3d adjustMovementForCollisions(Vec3d vec3d) {
        boolean bl4;
        Box box = this.getBoundingBox();
        EntityContext entityContext = EntityContext.of(this);
        VoxelShape voxelShape = this.world.getWorldBorder().asVoxelShape();
        Stream<Object> stream = VoxelShapes.matchesAnywhere(voxelShape, VoxelShapes.cuboid(box.contract(1.0E-7)), BooleanBiFunction.AND) ? Stream.empty() : Stream.of(voxelShape);
        Stream<VoxelShape> stream2 = this.world.getEntityCollisions(this, box.stretch(vec3d), ImmutableSet.of());
        ReusableStream<VoxelShape> reusableStream = new ReusableStream<VoxelShape>(Stream.concat(stream2, stream));
        Vec3d vec3d2 = vec3d.lengthSquared() == 0.0 ? vec3d : Entity.adjustMovementForCollisions(this, vec3d, box, this.world, entityContext, reusableStream);
        boolean bl = vec3d.x != vec3d2.x;
        boolean bl2 = vec3d.y != vec3d2.y;
        boolean bl3 = vec3d.z != vec3d2.z;
        boolean bl5 = bl4 = this.onGround || bl2 && vec3d.y < 0.0;
        if (this.stepHeight > 0.0f && bl4 && (bl || bl3)) {
            Vec3d vec3d5;
            Vec3d vec3d3 = Entity.adjustMovementForCollisions(this, new Vec3d(vec3d.x, this.stepHeight, vec3d.z), box, this.world, entityContext, reusableStream);
            Vec3d vec3d4 = Entity.adjustMovementForCollisions(this, new Vec3d(0.0, this.stepHeight, 0.0), box.stretch(vec3d.x, 0.0, vec3d.z), this.world, entityContext, reusableStream);
            if (vec3d4.y < (double)this.stepHeight && Entity.squaredHorizontalLength(vec3d5 = Entity.adjustMovementForCollisions(this, new Vec3d(vec3d.x, 0.0, vec3d.z), box.offset(vec3d4), this.world, entityContext, reusableStream).add(vec3d4)) > Entity.squaredHorizontalLength(vec3d3)) {
                vec3d3 = vec3d5;
            }
            if (Entity.squaredHorizontalLength(vec3d3) > Entity.squaredHorizontalLength(vec3d2)) {
                return vec3d3.add(Entity.adjustMovementForCollisions(this, new Vec3d(0.0, -vec3d3.y + vec3d.y, 0.0), box.offset(vec3d3), this.world, entityContext, reusableStream));
            }
        }
        return vec3d2;
    }

    public static double squaredHorizontalLength(Vec3d vec3d) {
        return vec3d.x * vec3d.x + vec3d.z * vec3d.z;
    }

    public static Vec3d adjustMovementForCollisions(@Nullable Entity entity, Vec3d vec3d, Box box, World world, EntityContext entityContext, ReusableStream<VoxelShape> reusableStream) {
        boolean bl3;
        boolean bl = vec3d.x == 0.0;
        boolean bl2 = vec3d.y == 0.0;
        boolean bl4 = bl3 = vec3d.z == 0.0;
        if (bl && bl2 || bl && bl3 || bl2 && bl3) {
            return Entity.adjustSingleAxisMovementForCollisions(vec3d, box, world, entityContext, reusableStream);
        }
        ReusableStream<VoxelShape> reusableStream2 = new ReusableStream<VoxelShape>(Stream.concat(reusableStream.stream(), world.getBlockCollisions(entity, box.stretch(vec3d))));
        return Entity.adjustMovementForCollisions(vec3d, box, reusableStream2);
    }

    public static Vec3d adjustMovementForCollisions(Vec3d vec3d, Box box, ReusableStream<VoxelShape> reusableStream) {
        boolean bl;
        double d = vec3d.x;
        double e = vec3d.y;
        double f = vec3d.z;
        if (e != 0.0 && (e = VoxelShapes.calculateMaxOffset(Direction.Axis.Y, box, reusableStream.stream(), e)) != 0.0) {
            box = box.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.calculateMaxOffset(Direction.Axis.Z, box, reusableStream.stream(), f)) != 0.0) {
            box = box.offset(0.0, 0.0, f);
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

    public static Vec3d adjustSingleAxisMovementForCollisions(Vec3d vec3d, Box box, WorldView worldView, EntityContext entityContext, ReusableStream<VoxelShape> reusableStream) {
        boolean bl;
        double d = vec3d.x;
        double e = vec3d.y;
        double f = vec3d.z;
        if (e != 0.0 && (e = VoxelShapes.method_17945(Direction.Axis.Y, box, worldView, e, entityContext, reusableStream.stream())) != 0.0) {
            box = box.offset(0.0, e, 0.0);
        }
        boolean bl2 = bl = Math.abs(d) < Math.abs(f);
        if (bl && f != 0.0 && (f = VoxelShapes.method_17945(Direction.Axis.Z, box, worldView, f, entityContext, reusableStream.stream())) != 0.0) {
            box = box.offset(0.0, 0.0, f);
        }
        if (d != 0.0) {
            d = VoxelShapes.method_17945(Direction.Axis.X, box, worldView, d, entityContext, reusableStream.stream());
            if (!bl && d != 0.0) {
                box = box.offset(d, 0.0, 0.0);
            }
        }
        if (!bl && f != 0.0) {
            f = VoxelShapes.method_17945(Direction.Axis.Z, box, worldView, f, entityContext, reusableStream.stream());
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
                            pooledMutable3.method_10113(i, j, k);
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

    protected void onBlockCollision(BlockState blockState) {
    }

    protected void playStepSound(BlockPos blockPos, BlockState blockState) {
        if (blockState.getMaterial().isLiquid()) {
            return;
        }
        BlockState blockState2 = this.world.getBlockState(blockPos.up());
        BlockSoundGroup blockSoundGroup = blockState2.getBlock() == Blocks.SNOW ? blockState2.getSoundGroup() : blockState.getSoundGroup();
        this.playSound(blockSoundGroup.getStepSound(), blockSoundGroup.getVolume() * 0.15f, blockSoundGroup.getPitch());
    }

    protected void playSwimSound(float f) {
        this.playSound(this.getSwimSound(), f, 1.0f + (this.random.nextFloat() - this.random.nextFloat()) * 0.4f);
    }

    protected float playFlySound(float f) {
        return 0.0f;
    }

    protected boolean hasWings() {
        return false;
    }

    public void playSound(SoundEvent soundEvent, float f, float g) {
        if (!this.isSilent()) {
            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), soundEvent, this.getSoundCategory(), f, g);
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
            if (this.fallDistance > 0.0f) {
                blockState.getBlock().onLandedUpon(this.world, blockPos, this, this.fallDistance);
            }
            this.fallDistance = 0.0f;
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
            this.damage(DamageSource.IN_FIRE, i);
        }
    }

    public final boolean isFireImmune() {
        return this.getType().isFireImmune();
    }

    public boolean handleFallDamage(float f, float g) {
        if (this.hasPassengers()) {
            for (Entity entity : this.getPassengerList()) {
                entity.handleFallDamage(f, g);
            }
        }
        return false;
    }

    public boolean isInsideWater() {
        return this.insideWater;
    }

    private boolean isBeingRainedOn() {
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.getEntityPos(this);){
            boolean bl = this.world.hasRain(pooledMutable) || this.world.hasRain(pooledMutable.method_10112(this.getX(), this.getY() + (double)this.dimensions.height, this.getZ()));
            return bl;
        }
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
            this.fallDistance = 0.0f;
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
        if (this.isSprinting() && !this.isInsideWater()) {
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

    public boolean isInFluid(Tag<Fluid> tag) {
        return this.isSubmergedIn(tag, false);
    }

    public boolean isSubmergedIn(Tag<Fluid> tag, boolean bl) {
        if (this.getVehicle() instanceof BoatEntity) {
            return false;
        }
        double d = this.method_23320();
        BlockPos blockPos = new BlockPos(this.getX(), d, this.getZ());
        if (bl && !this.world.isChunkLoaded(blockPos.getX() >> 4, blockPos.getZ() >> 4)) {
            return false;
        }
        FluidState fluidState = this.world.getFluidState(blockPos);
        return fluidState.matches(tag) && d < (double)((float)blockPos.getY() + (fluidState.getHeight(this.world, blockPos) + 0.11111111f));
    }

    public void setInLava() {
        this.inLava = true;
    }

    public boolean isInLava() {
        return this.inLava;
    }

    public void updateVelocity(float f, Vec3d vec3d) {
        Vec3d vec3d2 = Entity.movementInputToVelocity(vec3d, f, this.yaw);
        this.setVelocity(this.getVelocity().add(vec3d2));
    }

    private static Vec3d movementInputToVelocity(Vec3d vec3d, float f, float g) {
        double d = vec3d.lengthSquared();
        if (d < 1.0E-7) {
            return Vec3d.ZERO;
        }
        Vec3d vec3d2 = (d > 1.0 ? vec3d.normalize() : vec3d).multiply(f);
        float h = MathHelper.sin(g * ((float)Math.PI / 180));
        float i = MathHelper.cos(g * ((float)Math.PI / 180));
        return new Vec3d(vec3d2.x * (double)i - vec3d2.z * (double)h, vec3d2.y, vec3d2.z * (double)i + vec3d2.x * (double)h);
    }

    @Environment(value=EnvType.CLIENT)
    public int getLightmapCoordinates() {
        if (this.isOnFire()) {
            return 15;
        }
        return this.world.getLightLevel(LightType.BLOCK, new BlockPos(this.getX(), this.getY(), this.getZ()));
    }

    public float getBrightnessAtEyes() {
        BlockPos.Mutable mutable = new BlockPos.Mutable(this.getX(), 0.0, this.getZ());
        if (this.world.isChunkLoaded(mutable)) {
            mutable.setY(MathHelper.floor(this.method_23320()));
            return this.world.getBrightness(mutable);
        }
        return 0.0f;
    }

    public void setWorld(World world) {
        this.world = world;
    }

    public void setPositionAnglesAndUpdate(double d, double e, double f, float g, float h) {
        double i = MathHelper.clamp(d, -3.0E7, 3.0E7);
        double j = MathHelper.clamp(f, -3.0E7, 3.0E7);
        this.prevX = i;
        this.prevY = e;
        this.prevZ = j;
        this.setPosition(i, e, j);
        this.yaw = g % 360.0f;
        this.pitch = MathHelper.clamp(h, -90.0f, 90.0f) % 360.0f;
        this.prevYaw = this.yaw;
        this.prevPitch = this.pitch;
    }

    public void setPositionAndAngles(BlockPos blockPos, float f, float g) {
        this.setPositionAndAngles((double)blockPos.getX() + 0.5, blockPos.getY(), (double)blockPos.getZ() + 0.5, f, g);
    }

    public void setPositionAndAngles(double d, double e, double f, float g, float h) {
        this.method_22862(d, e, f);
        this.yaw = g;
        this.pitch = h;
        this.method_23311();
    }

    public void method_22862(double d, double e, double f) {
        this.setPos(d, e, f);
        this.prevX = d;
        this.prevY = e;
        this.prevZ = f;
        this.prevRenderX = d;
        this.prevRenderY = e;
        this.prevRenderZ = f;
    }

    public float distanceTo(Entity entity) {
        float f = (float)(this.getX() - entity.getX());
        float g = (float)(this.getY() - entity.getY());
        float h = (float)(this.getZ() - entity.getZ());
        return MathHelper.sqrt(f * f + g * g + h * h);
    }

    public double squaredDistanceTo(double d, double e, double f) {
        double g = this.getX() - d;
        double h = this.getY() - e;
        double i = this.getZ() - f;
        return g * g + h * h + i * i;
    }

    public double squaredDistanceTo(Entity entity) {
        return this.squaredDistanceTo(entity.getPos());
    }

    public double squaredDistanceTo(Vec3d vec3d) {
        double d = this.getX() - vec3d.x;
        double e = this.getY() - vec3d.y;
        double f = this.getZ() - vec3d.z;
        return d * d + e * e + f * f;
    }

    public void onPlayerCollision(PlayerEntity playerEntity) {
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
        }
        this.scheduleVelocityUpdate();
        return false;
    }

    public final Vec3d getRotationVec(float f) {
        return this.getRotationVector(this.getPitch(f), this.getYaw(f));
    }

    public float getPitch(float f) {
        if (f == 1.0f) {
            return this.pitch;
        }
        return MathHelper.lerp(f, this.prevPitch, this.pitch);
    }

    public float getYaw(float f) {
        if (f == 1.0f) {
            return this.yaw;
        }
        return MathHelper.lerp(f, this.prevYaw, this.yaw);
    }

    protected final Vec3d getRotationVector(float f, float g) {
        float h = f * ((float)Math.PI / 180);
        float i = -g * ((float)Math.PI / 180);
        float j = MathHelper.cos(i);
        float k = MathHelper.sin(i);
        float l = MathHelper.cos(h);
        float m = MathHelper.sin(h);
        return new Vec3d(k * l, -m, j * l);
    }

    public final Vec3d getOppositeRotationVector(float f) {
        return this.getOppositeRotationVector(this.getPitch(f), this.getYaw(f));
    }

    protected final Vec3d getOppositeRotationVector(float f, float g) {
        return this.getRotationVector(f - 90.0f, g);
    }

    public Vec3d getCameraPosVec(float f) {
        if (f == 1.0f) {
            return new Vec3d(this.getX(), this.method_23320(), this.getZ());
        }
        double d = MathHelper.lerp((double)f, this.prevX, this.getX());
        double e = MathHelper.lerp((double)f, this.prevY, this.getY()) + (double)this.getStandingEyeHeight();
        double g = MathHelper.lerp((double)f, this.prevZ, this.getZ());
        return new Vec3d(d, e, g);
    }

    public HitResult rayTrace(double d, float f, boolean bl) {
        Vec3d vec3d = this.getCameraPosVec(f);
        Vec3d vec3d2 = this.getRotationVec(f);
        Vec3d vec3d3 = vec3d.add(vec3d2.x * d, vec3d2.y * d, vec3d2.z * d);
        return this.world.rayTrace(new RayTraceContext(vec3d, vec3d3, RayTraceContext.ShapeType.OUTLINE, bl ? RayTraceContext.FluidHandling.ANY : RayTraceContext.FluidHandling.NONE, this));
    }

    public boolean collides() {
        return false;
    }

    public boolean isPushable() {
        return false;
    }

    public void updateKilledAdvancementCriterion(Entity entity, int i, DamageSource damageSource) {
        if (entity instanceof ServerPlayerEntity) {
            Criterions.ENTITY_KILLED_PLAYER.trigger((ServerPlayerEntity)entity, this, damageSource);
        }
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderFrom(double d, double e, double f) {
        double g = this.getX() - d;
        double h = this.getY() - e;
        double i = this.getZ() - f;
        double j = g * g + h * h + i * i;
        return this.shouldRenderAtDistance(j);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderAtDistance(double d) {
        double e = this.getBoundingBox().getAverageSideLength();
        if (Double.isNaN(e)) {
            e = 1.0;
        }
        return d < (e *= 64.0 * renderDistanceMultiplier) * e;
    }

    public boolean saveSelfToTag(CompoundTag compoundTag) {
        String string = this.getSavedEntityId();
        if (this.removed || string == null) {
            return false;
        }
        compoundTag.putString("id", string);
        this.toTag(compoundTag);
        return true;
    }

    public boolean saveToTag(CompoundTag compoundTag) {
        if (this.hasVehicle()) {
            return false;
        }
        return this.saveSelfToTag(compoundTag);
    }

    public CompoundTag toTag(CompoundTag compoundTag) {
        try {
            ListTag listTag;
            compoundTag.put("Pos", this.toListTag(this.getX(), this.getY(), this.getZ()));
            Vec3d vec3d = this.getVelocity();
            compoundTag.put("Motion", this.toListTag(vec3d.x, vec3d.y, vec3d.z));
            compoundTag.put("Rotation", this.toListTag(this.yaw, this.pitch));
            compoundTag.putFloat("FallDistance", this.fallDistance);
            compoundTag.putShort("Fire", (short)this.fireTicks);
            compoundTag.putShort("Air", (short)this.getAir());
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
                listTag = new ListTag();
                for (String string : this.scoreboardTags) {
                    listTag.add(StringTag.of(string));
                }
                compoundTag.put("Tags", listTag);
            }
            this.writeCustomDataToTag(compoundTag);
            if (this.hasPassengers()) {
                listTag = new ListTag();
                for (Entity entity : this.getPassengerList()) {
                    CompoundTag compoundTag2;
                    if (!entity.saveSelfToTag(compoundTag2 = new CompoundTag())) continue;
                    listTag.add(compoundTag2);
                }
                if (!listTag.isEmpty()) {
                    compoundTag.put("Passengers", listTag);
                }
            }
        } catch (Throwable throwable) {
            CrashReport crashReport = CrashReport.create(throwable, "Saving entity NBT");
            CrashReportSection crashReportSection = crashReport.addElement("Entity being saved");
            this.populateCrashReport(crashReportSection);
            throw new CrashException(crashReport);
        }
        return compoundTag;
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
            this.method_22862(listTag.getDouble(0), listTag.getDouble(1), listTag.getDouble(2));
            this.yaw = listTag3.getFloat(0);
            this.pitch = listTag3.getFloat(1);
            this.prevYaw = this.yaw;
            this.prevPitch = this.pitch;
            this.setHeadYaw(this.yaw);
            this.setYaw(this.yaw);
            this.fallDistance = compoundTag.getFloat("FallDistance");
            this.fireTicks = compoundTag.getShort("Fire");
            this.setAir(compoundTag.getShort("Air"));
            this.onGround = compoundTag.getBoolean("OnGround");
            if (compoundTag.contains("Dimension")) {
                this.dimension = DimensionType.byRawId(compoundTag.getInt("Dimension"));
            }
            this.invulnerable = compoundTag.getBoolean("Invulnerable");
            this.portalCooldown = compoundTag.getInt("PortalCooldown");
            if (compoundTag.containsUuid("UUID")) {
                this.uuid = compoundTag.getUuid("UUID");
                this.uuidString = this.uuid.toString();
            }
            if (!(Double.isFinite(this.getX()) && Double.isFinite(this.getY()) && Double.isFinite(this.getZ()))) {
                throw new IllegalStateException("Entity has invalid position");
            }
            if (!Double.isFinite(this.yaw) || !Double.isFinite(this.pitch)) {
                throw new IllegalStateException("Entity has invalid rotation");
            }
            this.method_23311();
            this.setRotation(this.yaw, this.pitch);
            if (compoundTag.contains("CustomName", 8)) {
                this.setCustomName(Text.Serializer.fromJson(compoundTag.getString("CustomName")));
            }
            this.setCustomNameVisible(compoundTag.getBoolean("CustomNameVisible"));
            this.setSilent(compoundTag.getBoolean("Silent"));
            this.setNoGravity(compoundTag.getBoolean("NoGravity"));
            this.setGlowing(compoundTag.getBoolean("Glowing"));
            if (compoundTag.contains("Tags", 9)) {
                this.scoreboardTags.clear();
                ListTag listTag4 = compoundTag.getList("Tags", 8);
                int i = Math.min(listTag4.size(), 1024);
                for (int j = 0; j < i; ++j) {
                    this.scoreboardTags.add(listTag4.getString(j));
                }
            }
            this.readCustomDataFromTag(compoundTag);
            if (this.shouldSetPositionOnLoad()) {
                this.method_23311();
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

    protected ListTag toListTag(double ... ds) {
        ListTag listTag = new ListTag();
        for (double d : ds) {
            listTag.add(DoubleTag.of(d));
        }
        return listTag;
    }

    protected ListTag toListTag(float ... fs) {
        ListTag listTag = new ListTag();
        for (float f : fs) {
            listTag.add(FloatTag.of(f));
        }
        return listTag;
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible itemConvertible) {
        return this.dropItem(itemConvertible, 0);
    }

    @Nullable
    public ItemEntity dropItem(ItemConvertible itemConvertible, int i) {
        return this.dropStack(new ItemStack(itemConvertible), i);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack itemStack) {
        return this.dropStack(itemStack, 0.0f);
    }

    @Nullable
    public ItemEntity dropStack(ItemStack itemStack, float f) {
        if (itemStack.isEmpty()) {
            return null;
        }
        if (this.world.isClient) {
            return null;
        }
        ItemEntity itemEntity = new ItemEntity(this.world, this.getX(), this.getY() + (double)f, this.getZ(), itemStack);
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
                pooledMutable.method_10113(k, j, l);
                if (!this.world.getBlockState(pooledMutable).canSuffocate(this.world, pooledMutable)) continue;
                boolean bl = true;
                return bl;
            }
        }
        return false;
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
        if (!this.hasVehicle()) {
            return;
        }
        this.getVehicle().updatePassengerPosition(this);
    }

    public void updatePassengerPosition(Entity entity) {
        if (!this.hasPassenger(entity)) {
            return;
        }
        entity.setPosition(this.getX(), this.getY() + this.getMountedHeightOffset() + entity.getHeightOffset(), this.getZ());
    }

    @Environment(value=EnvType.CLIENT)
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

    @Environment(value=EnvType.CLIENT)
    public boolean isLiving() {
        return this instanceof LivingEntity;
    }

    public boolean startRiding(Entity entity, boolean bl) {
        Entity entity2 = entity;
        while (entity2.vehicle != null) {
            if (entity2.vehicle == this) {
                return false;
            }
            entity2 = entity2.vehicle;
        }
        if (!(bl || this.canStartRiding(entity) && entity.canAddPassenger(this))) {
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

    protected boolean wouldPoseNotCollide(EntityPose entityPose) {
        return this.world.doesNotCollide(this, this.calculateBoundsForPose(entityPose));
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

    protected void addPassenger(Entity entity) {
        if (entity.getVehicle() != this) {
            throw new IllegalStateException("Use x.startRiding(y), not y.addPassenger(x)");
        }
        if (!this.world.isClient && entity instanceof PlayerEntity && !(this.getPrimaryPassenger() instanceof PlayerEntity)) {
            this.passengerList.add(0, entity);
        } else {
            this.passengerList.add(entity);
        }
    }

    protected void removePassenger(Entity entity) {
        if (entity.getVehicle() == this) {
            throw new IllegalStateException("Use x.stopRiding(y), not y.removePassenger(x)");
        }
        this.passengerList.remove(entity);
        entity.ridingCooldown = 60;
    }

    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengerList().size() < 1;
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.setPosition(d, e, f);
        this.setRotation(g, h);
    }

    @Environment(value=EnvType.CLIENT)
    public void updateTrackedHeadRotation(float f, int i) {
        this.setHeadYaw(f);
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

    public void setInPortal(BlockPos blockPos) {
        if (this.portalCooldown > 0) {
            this.portalCooldown = this.getDefaultPortalCooldown();
            return;
        }
        if (!this.world.isClient && !blockPos.equals(this.lastPortalPosition)) {
            this.lastPortalPosition = new BlockPos(blockPos);
            PortalBlock cfr_ignored_0 = (PortalBlock)Blocks.NETHER_PORTAL;
            BlockPattern.Result result = PortalBlock.findPortal(this.world, this.lastPortalPosition);
            double d = result.getForwards().getAxis() == Direction.Axis.X ? (double)result.getFrontTopLeft().getZ() : (double)result.getFrontTopLeft().getX();
            double e = Math.abs(MathHelper.minusDiv((result.getForwards().getAxis() == Direction.Axis.X ? this.getZ() : this.getX()) - (double)(result.getForwards().rotateYClockwise().getDirection() == Direction.AxisDirection.NEGATIVE ? 1 : 0), d, d - (double)result.getWidth()));
            double f = MathHelper.minusDiv(this.getY() - 1.0, result.getFrontTopLeft().getY(), result.getFrontTopLeft().getY() - result.getHeight());
            this.lastPortalDirectionVector = new Vec3d(e, f, 0.0);
            this.lastPortalDirection = result.getForwards();
        }
        this.inPortal = true;
    }

    protected void tickPortal() {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
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

    public int getDefaultPortalCooldown() {
        return 300;
    }

    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double d, double e, double f) {
        this.setVelocity(d, e, f);
    }

    @Environment(value=EnvType.CLIENT)
    public void handleStatus(byte b) {
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

    public void equipStack(EquipmentSlot equipmentSlot, ItemStack itemStack) {
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

    public void setSneaking(boolean bl) {
        this.setFlag(1, bl);
    }

    public boolean isSneaking() {
        return this.getFlag(1);
    }

    public boolean method_21749() {
        return this.isSneaking();
    }

    public boolean method_21750() {
        return this.isSneaking();
    }

    public boolean method_21751() {
        return this.isSneaking();
    }

    public boolean method_21752() {
        return this.isSneaking();
    }

    public boolean isInSneakingPose() {
        return this.getPose() == EntityPose.CROUCHING;
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

    @Environment(value=EnvType.CLIENT)
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

    @Environment(value=EnvType.CLIENT)
    public boolean canSeePlayer(PlayerEntity playerEntity) {
        if (playerEntity.isSpectator()) {
            return false;
        }
        AbstractTeam abstractTeam = this.getScoreboardTeam();
        if (abstractTeam != null && playerEntity != null && playerEntity.getScoreboardTeam() == abstractTeam && abstractTeam.shouldShowFriendlyInvisibles()) {
            return false;
        }
        return this.isInvisible();
    }

    @Nullable
    public AbstractTeam getScoreboardTeam() {
        return this.world.getScoreboard().getPlayerTeam(this.getEntityName());
    }

    public boolean isTeammate(Entity entity) {
        return this.isTeamPlayer(entity.getScoreboardTeam());
    }

    public boolean isTeamPlayer(AbstractTeam abstractTeam) {
        if (this.getScoreboardTeam() != null) {
            return this.getScoreboardTeam().isEqual(abstractTeam);
        }
        return false;
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

    public int getMaxAir() {
        return 300;
    }

    public int getAir() {
        return this.dataTracker.get(AIR);
    }

    public void setAir(int i) {
        this.dataTracker.set(AIR, i);
    }

    public void onStruckByLightning(LightningEntity lightningEntity) {
        ++this.fireTicks;
        if (this.fireTicks == 0) {
            this.setOnFireFor(8);
        }
        this.damage(DamageSource.LIGHTNING_BOLT, 5.0f);
    }

    public void onBubbleColumnSurfaceCollision(boolean bl) {
        Vec3d vec3d = this.getVelocity();
        double d = bl ? Math.max(-0.9, vec3d.y - 0.03) : Math.min(1.8, vec3d.y + 0.1);
        this.setVelocity(vec3d.x, d, vec3d.z);
    }

    public void onBubbleColumnCollision(boolean bl) {
        Vec3d vec3d = this.getVelocity();
        double d = bl ? Math.max(-0.3, vec3d.y - 0.03) : Math.min(0.7, vec3d.y + 0.06);
        this.setVelocity(vec3d.x, d, vec3d.z);
        this.fallDistance = 0.0f;
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
            double i;
            mutable.set(blockPos).setOffset(direction2);
            if (this.world.getBlockState(mutable).isFullCube(this.world, mutable)) continue;
            double h = vec3d.getComponentAlongAxis(direction2.getAxis());
            double d2 = i = direction2.getDirection() == Direction.AxisDirection.POSITIVE ? 1.0 - h : h;
            if (!(i < g)) continue;
            g = i;
            direction = direction2;
        }
        float j = this.random.nextFloat() * 0.2f + 0.1f;
        float k = direction.getDirection().offset();
        Vec3d vec3d2 = this.getVelocity().multiply(0.75);
        if (direction.getAxis() == Direction.Axis.X) {
            this.setVelocity(k * j, vec3d2.y, vec3d2.z);
        } else if (direction.getAxis() == Direction.Axis.Y) {
            this.setVelocity(vec3d2.x, k * j, vec3d2.z);
        } else if (direction.getAxis() == Direction.Axis.Z) {
            this.setVelocity(vec3d2.x, vec3d2.y, k * j);
        }
    }

    public void slowMovement(BlockState blockState, Vec3d vec3d) {
        this.fallDistance = 0.0f;
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
            Entity.removeClickEvents(text2);
            return text2;
        }
        return this.method_23315();
    }

    protected Text method_23315() {
        return this.type.getName();
    }

    public boolean isPartOf(Entity entity) {
        return this == entity;
    }

    public float getHeadYaw() {
        return 0.0f;
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
        return String.format(Locale.ROOT, "%s['%s'/%d, l='%s', x=%.2f, y=%.2f, z=%.2f]", this.getClass().getSimpleName(), this.getName().asString(), this.entityId, this.world == null ? "~NULL~" : this.world.getLevelProperties().getLevelName(), this.getX(), this.getY(), this.getZ());
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
        this.setPositionAndAngles(entity.getX(), entity.getY(), entity.getZ(), entity.yaw, entity.pitch);
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
        BlockPos blockPos;
        if (this.world.isClient || this.removed) {
            return null;
        }
        this.world.getProfiler().push("changeDimension");
        MinecraftServer minecraftServer = this.getServer();
        DimensionType dimensionType2 = this.dimension;
        ServerWorld serverWorld = minecraftServer.getWorld(dimensionType2);
        ServerWorld serverWorld2 = minecraftServer.getWorld(dimensionType);
        this.dimension = dimensionType;
        this.detach();
        this.world.getProfiler().push("reposition");
        Vec3d vec3d = this.getVelocity();
        float f = 0.0f;
        if (dimensionType2 == DimensionType.THE_END && dimensionType == DimensionType.OVERWORLD) {
            blockPos = serverWorld2.getTopPosition(Heightmap.Type.MOTION_BLOCKING_NO_LEAVES, serverWorld2.getSpawnPos());
        } else if (dimensionType == DimensionType.THE_END) {
            blockPos = serverWorld2.getForcedSpawnPoint();
        } else {
            double d = this.getX();
            double e = this.getZ();
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
            blockPos = new BlockPos(d, this.getY(), e);
            BlockPattern.TeleportTarget teleportTarget = serverWorld2.getPortalForcer().getPortal(blockPos, vec3d, this.getLastPortalDirection(), vec3d2.x, vec3d2.y, this instanceof PlayerEntity);
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
            ((Entity)entity).setPositionAndAngles(blockPos, ((Entity)entity).yaw + f, ((Entity)entity).pitch);
            ((Entity)entity).setVelocity(vec3d);
            serverWorld2.method_18769((Entity)entity);
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

    public float getEffectiveExplosionResistance(Explosion explosion, BlockView blockView, BlockPos blockPos, BlockState blockState, FluidState fluidState, float f) {
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
        crashReportSection.add("Entity Type", () -> EntityType.getId(this.getType()) + " (" + this.getClass().getCanonicalName() + ")");
        crashReportSection.add("Entity ID", this.entityId);
        crashReportSection.add("Entity Name", () -> this.getName().getString());
        crashReportSection.add("Entity's Exact location", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", this.getX(), this.getY(), this.getZ()));
        crashReportSection.add("Entity's Block location", CrashReportSection.createPositionString(MathHelper.floor(this.getX()), MathHelper.floor(this.getY()), MathHelper.floor(this.getZ())));
        Vec3d vec3d = this.getVelocity();
        crashReportSection.add("Entity's Momentum", String.format(Locale.ROOT, "%.2f, %.2f, %.2f", vec3d.x, vec3d.y, vec3d.z));
        crashReportSection.add("Entity's Passengers", () -> this.getPassengerList().toString());
        crashReportSection.add("Entity's Vehicle", () -> this.getVehicle().toString());
    }

    @Environment(value=EnvType.CLIENT)
    public boolean doesRenderOnFire() {
        return this.isOnFire() && !this.isSpectator();
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

    @Environment(value=EnvType.CLIENT)
    public static double getRenderDistanceMultiplier() {
        return renderDistanceMultiplier;
    }

    @Environment(value=EnvType.CLIENT)
    public static void setRenderDistanceMultiplier(double d) {
        renderDistanceMultiplier = d;
    }

    @Override
    public Text getDisplayName() {
        return Team.modifyText(this.getScoreboardTeam(), this.getName()).styled(style -> style.setHoverEvent(this.getHoverEvent()).setInsertion(this.getUuidAsString()));
    }

    public void setCustomName(@Nullable Text text) {
        this.dataTracker.set(CUSTOM_NAME, Optional.ofNullable(text));
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

    public void setCustomNameVisible(boolean bl) {
        this.dataTracker.set(NAME_VISIBLE, bl);
    }

    public boolean isCustomNameVisible() {
        return this.dataTracker.get(NAME_VISIBLE);
    }

    public final void teleport(double d, double e, double f) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        ChunkPos chunkPos = new ChunkPos(new BlockPos(d, e, f));
        ((ServerWorld)this.world).method_14178().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 0, this.getEntityId());
        this.world.method_8497(chunkPos.x, chunkPos.z);
        this.requestTeleport(d, e, f);
    }

    public void requestTeleport(double d, double e, double f) {
        if (!(this.world instanceof ServerWorld)) {
            return;
        }
        this.teleportRequested = true;
        this.setPositionAndAngles(d, e, f, this.yaw, this.pitch);
        ((ServerWorld)this.world).checkChunk(this);
    }

    @Environment(value=EnvType.CLIENT)
    public boolean shouldRenderName() {
        return this.isCustomNameVisible();
    }

    public void onTrackedDataSet(TrackedData<?> trackedData) {
        if (POSE.equals(trackedData)) {
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

    public boolean canBeSpectated(ServerPlayerEntity serverPlayerEntity) {
        return true;
    }

    public Box getBoundingBox() {
        return this.entityBounds;
    }

    @Environment(value=EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        return this.getBoundingBox();
    }

    protected Box calculateBoundsForPose(EntityPose entityPose) {
        EntityDimensions entityDimensions = this.getDimensions(entityPose);
        float f = entityDimensions.width / 2.0f;
        Vec3d vec3d = new Vec3d(this.getX() - (double)f, this.getY(), this.getZ() - (double)f);
        Vec3d vec3d2 = new Vec3d(this.getX() + (double)f, this.getY() + (double)entityDimensions.height, this.getZ() + (double)f);
        return new Box(vec3d, vec3d2);
    }

    public void setBoundingBox(Box box) {
        this.entityBounds = box;
    }

    protected float getEyeHeight(EntityPose entityPose, EntityDimensions entityDimensions) {
        return entityDimensions.height * 0.85f;
    }

    @Environment(value=EnvType.CLIENT)
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
        return this.getPos();
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

    public float applyMirror(BlockMirror blockMirror) {
        float f = MathHelper.wrapDegrees(this.yaw);
        switch (blockMirror) {
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

    public boolean hasPassenger(Entity entity) {
        for (Entity entity2 : this.getPassengerList()) {
            if (!entity2.equals(entity)) continue;
            return true;
        }
        return false;
    }

    public boolean hasPassengerType(Class<? extends Entity> class_) {
        for (Entity entity : this.getPassengerList()) {
            if (!class_.isAssignableFrom(entity.getClass())) continue;
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

    public boolean hasPlayerRider() {
        HashSet<Entity> set = Sets.newHashSet();
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
            if (!entity2.hasPassengerDeep(entity)) continue;
            return true;
        }
        return false;
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
                        pooledMutable.method_10113(p, q, r);
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

    public final double getX() {
        return this.x;
    }

    public double method_23316(double d) {
        return this.x + (double)this.getWidth() * d;
    }

    public double method_23322(double d) {
        return this.method_23316((2.0 * this.random.nextDouble() + 1.0) * d);
    }

    public final double getY() {
        return this.y;
    }

    public double getHeightAt(double d) {
        return this.y + (double)this.getHeight() * d;
    }

    public double method_23319() {
        return this.getHeightAt(this.random.nextDouble());
    }

    public double method_23320() {
        return this.y + (double)this.standingEyeHeight;
    }

    public final double getZ() {
        return this.z;
    }

    public double method_23324(double d) {
        return this.z + (double)this.getWidth() * d;
    }

    public double method_23325(double d) {
        return this.method_23324((2.0 * this.random.nextDouble() + 1.0) * d);
    }

    public void setPos(double d, double e, double f) {
        this.x = d;
        this.y = e;
        this.z = f;
    }

    public void checkDespawn() {
    }
}

