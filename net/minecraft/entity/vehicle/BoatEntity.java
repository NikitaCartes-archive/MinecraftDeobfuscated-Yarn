/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.vehicle;

import java.util.List;
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
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class BoatEntity
extends Entity {
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
    private Location location;
    private Location lastLocation;
    private double fallVelocity;
    private boolean onBubbleColumnSurface;
    private boolean bubbleColumnIsDrag;
    private float bubbleWobbleStrength;
    private float bubbleWobble;
    private float lastBubbleWobble;

    public BoatEntity(EntityType<? extends BoatEntity> entityType, World world) {
        super(entityType, world);
        this.inanimate = true;
    }

    public BoatEntity(World world, double d, double e, double f) {
        this((EntityType<? extends BoatEntity>)EntityType.BOAT, world);
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
        this.dataTracker.startTracking(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(0.0f));
        this.dataTracker.startTracking(BOAT_TYPE, Type.OAK.ordinal());
        this.dataTracker.startTracking(LEFT_PADDLE_MOVING, false);
        this.dataTracker.startTracking(RIGHT_PADDLE_MOVING, false);
        this.dataTracker.startTracking(BUBBLE_WOBBLE_TICKS, 0);
    }

    @Override
    @Nullable
    public Box getHardCollisionBox(Entity entity) {
        if (entity.isPushable()) {
            return entity.getBoundingBox();
        }
        return null;
    }

    @Override
    @Nullable
    public Box getCollisionBox() {
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
        boolean bl;
        if (this.isInvulnerableTo(damageSource)) {
            return false;
        }
        if (this.world.isClient || this.removed) {
            return true;
        }
        if (damageSource instanceof ProjectileDamageSource && damageSource.getAttacker() != null && this.hasPassenger(damageSource.getAttacker())) {
            return false;
        }
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + f * 10.0f);
        this.scheduleVelocityUpdate();
        boolean bl2 = bl = damageSource.getAttacker() instanceof PlayerEntity && ((PlayerEntity)damageSource.getAttacker()).abilities.creativeMode;
        if (bl || this.getDamageWobbleStrength() > 40.0f) {
            if (!bl && this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                this.dropItem(this.asItem());
            }
            this.remove();
        }
        return true;
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
        this.world.addParticle(ParticleTypes.SPLASH, this.x + (double)this.random.nextFloat(), this.y + 0.7, this.z + (double)this.random.nextFloat(), 0.0, 0.0, 0.0);
        if (this.random.nextInt(20) == 0) {
            this.world.playSound(this.x, this.y, this.z, this.getSplashSound(), this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat(), false);
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
            default: {
                return Items.OAK_BOAT;
            }
            case SPRUCE: {
                return Items.SPRUCE_BOAT;
            }
            case BIRCH: {
                return Items.BIRCH_BOAT;
            }
            case JUNGLE: {
                return Items.JUNGLE_BOAT;
            }
            case ACACIA: {
                return Items.ACACIA_BOAT;
            }
            case DARK_OAK: 
        }
        return Items.DARK_OAK_BOAT;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void animateDamage() {
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() * 11.0f);
    }

    @Override
    public boolean collides() {
        return !this.removed;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double d, double e, double f, float g, float h, int i, boolean bl) {
        this.field_7686 = d;
        this.field_7700 = e;
        this.field_7685 = f;
        this.field_7699 = g;
        this.field_7684 = h;
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
        this.ticksUnderwater = this.location == Location.UNDER_WATER || this.location == Location.UNDER_FLOWING_WATER ? (this.ticksUnderwater += 1.0f) : 0.0f;
        if (!this.world.isClient && this.ticksUnderwater >= 60.0f) {
            this.removeAllPassengers();
        }
        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
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
            this.move(MovementType.SELF, this.getVelocity());
        } else {
            this.setVelocity(Vec3d.ZERO);
        }
        this.handleBubbleColumn();
        for (int i = 0; i <= 1; ++i) {
            if (this.isPaddleMoving(i)) {
                SoundEvent soundEvent;
                if (!this.isSilent() && (double)(this.paddlePhases[i] % ((float)Math.PI * 2)) <= 0.7853981852531433 && ((double)this.paddlePhases[i] + (double)0.3926991f) % 6.2831854820251465 >= 0.7853981852531433 && (soundEvent = this.getPaddleSoundEvent()) != null) {
                    Vec3d vec3d = this.getRotationVec(1.0f);
                    double d = i == 1 ? -vec3d.z : vec3d.z;
                    double e = i == 1 ? vec3d.x : -vec3d.x;
                    this.world.playSound(null, this.x + d, this.y, this.z + e, soundEvent, this.getSoundCategory(), 1.0f, 0.8f + 0.4f * this.random.nextFloat());
                }
                int n = i;
                this.paddlePhases[n] = (float)((double)this.paddlePhases[n] + (double)0.3926991f);
                continue;
            }
            this.paddlePhases[i] = 0.0f;
        }
        this.checkBlockCollision();
        List<Entity> list = this.world.getEntities(this, this.getBoundingBox().expand(0.2f, -0.01f, 0.2f), EntityPredicates.canBePushedBy(this));
        if (!list.isEmpty()) {
            boolean bl = !this.world.isClient && !(this.getPrimaryPassenger() instanceof PlayerEntity);
            for (int j = 0; j < list.size(); ++j) {
                Entity entity = list.get(j);
                if (entity.hasPassenger(this)) continue;
                if (bl && this.getPassengerList().size() < 2 && !entity.hasVehicle() && entity.getWidth() < this.getWidth() && entity instanceof LivingEntity && !(entity instanceof WaterCreatureEntity) && !(entity instanceof PlayerEntity)) {
                    entity.startRiding(this);
                    continue;
                }
                this.pushAwayFrom(entity);
            }
        }
    }

    private void handleBubbleColumn() {
        if (this.world.isClient) {
            int i = this.getBubbleWobbleTicks();
            this.bubbleWobbleStrength = i > 0 ? (this.bubbleWobbleStrength += 0.05f) : (this.bubbleWobbleStrength -= 0.1f);
            this.bubbleWobbleStrength = MathHelper.clamp(this.bubbleWobbleStrength, 0.0f, 1.0f);
            this.lastBubbleWobble = this.bubbleWobble;
            this.bubbleWobble = 10.0f * (float)Math.sin(0.5f * (float)this.world.getTime()) * this.bubbleWobbleStrength;
        } else {
            int i;
            if (!this.onBubbleColumnSurface) {
                this.setBubbleWobbleTicks(0);
            }
            if ((i = this.getBubbleWobbleTicks()) > 0) {
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
            case IN_WATER: 
            case UNDER_WATER: 
            case UNDER_FLOWING_WATER: {
                return SoundEvents.ENTITY_BOAT_PADDLE_WATER;
            }
            case ON_LAND: {
                return SoundEvents.ENTITY_BOAT_PADDLE_LAND;
            }
        }
        return null;
    }

    private void method_7555() {
        if (this.field_7708 <= 0 || this.isLogicalSideForUpdatingMovement()) {
            return;
        }
        double d = this.x + (this.field_7686 - this.x) / (double)this.field_7708;
        double e = this.y + (this.field_7700 - this.y) / (double)this.field_7708;
        double f = this.z + (this.field_7685 - this.z) / (double)this.field_7708;
        double g = MathHelper.wrapDegrees(this.field_7699 - (double)this.yaw);
        this.yaw = (float)((double)this.yaw + g / (double)this.field_7708);
        this.pitch = (float)((double)this.pitch + (this.field_7684 - (double)this.pitch) / (double)this.field_7708);
        --this.field_7708;
        this.setPosition(d, e, f);
        this.setRotation(this.yaw, this.pitch);
    }

    public void setPaddleMovings(boolean bl, boolean bl2) {
        this.dataTracker.set(LEFT_PADDLE_MOVING, bl);
        this.dataTracker.set(RIGHT_PADDLE_MOVING, bl2);
    }

    @Environment(value=EnvType.CLIENT)
    public float interpolatePaddlePhase(int i, float f) {
        if (this.isPaddleMoving(i)) {
            return (float)MathHelper.clampedLerp((double)this.paddlePhases[i] - (double)0.3926991f, this.paddlePhases[i], f);
        }
        return 0.0f;
    }

    private Location checkLocation() {
        Location location = this.getUnderWaterLocation();
        if (location != null) {
            this.waterLevel = this.getBoundingBox().maxY;
            return location;
        }
        if (this.checKBoatInWater()) {
            return Location.IN_WATER;
        }
        float f = this.method_7548();
        if (f > 0.0f) {
            this.field_7714 = f;
            return Location.ON_LAND;
        }
        return Location.IN_AIR;
    }

    public float method_7544() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(box.maxY - this.fallVelocity);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            block12: for (int o = k; o < l; ++o) {
                float f = 0.0f;
                for (int p = i; p < j; ++p) {
                    for (int q = m; q < n; ++q) {
                        pooledMutable.method_10113(p, o, q);
                        FluidState fluidState = this.world.getFluidState(pooledMutable);
                        if (fluidState.matches(FluidTags.WATER)) {
                            f = Math.max(f, fluidState.getHeight(this.world, pooledMutable));
                        }
                        if (f >= 1.0f) continue block12;
                    }
                }
                if (!(f < 1.0f)) continue;
                float f2 = (float)pooledMutable.getY() + f;
                return f2;
            }
            float f = l + 1;
            return f;
        }
    }

    public float method_7548() {
        Box box = this.getBoundingBox();
        Box box2 = new Box(box.minX, box.minY - 0.001, box.minZ, box.maxX, box.minY, box.maxZ);
        int i = MathHelper.floor(box2.minX) - 1;
        int j = MathHelper.ceil(box2.maxX) + 1;
        int k = MathHelper.floor(box2.minY) - 1;
        int l = MathHelper.ceil(box2.maxY) + 1;
        int m = MathHelper.floor(box2.minZ) - 1;
        int n = MathHelper.ceil(box2.maxZ) + 1;
        VoxelShape voxelShape = VoxelShapes.cuboid(box2);
        float f = 0.0f;
        int o = 0;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int p = i; p < j; ++p) {
                for (int q = m; q < n; ++q) {
                    int r = (p == i || p == j - 1 ? 1 : 0) + (q == m || q == n - 1 ? 1 : 0);
                    if (r == 2) continue;
                    for (int s = k; s < l; ++s) {
                        if (r > 0 && (s == k || s == l - 1)) continue;
                        pooledMutable.method_10113(p, s, q);
                        BlockState blockState = this.world.getBlockState(pooledMutable);
                        if (blockState.getBlock() instanceof LilyPadBlock || !VoxelShapes.matchesAnywhere(blockState.getCollisionShape(this.world, pooledMutable).offset(p, s, q), voxelShape, BooleanBiFunction.AND)) continue;
                        f += blockState.getBlock().getSlipperiness();
                        ++o;
                    }
                }
            }
        }
        return f / (float)o;
    }

    private boolean checKBoatInWater() {
        Box box = this.getBoundingBox();
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.minY);
        int l = MathHelper.ceil(box.minY + 0.001);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        this.waterLevel = Double.MIN_VALUE;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int o = i; o < j; ++o) {
                for (int p = k; p < l; ++p) {
                    for (int q = m; q < n; ++q) {
                        pooledMutable.method_10113(o, p, q);
                        FluidState fluidState = this.world.getFluidState(pooledMutable);
                        if (!fluidState.matches(FluidTags.WATER)) continue;
                        float f = (float)p + fluidState.getHeight(this.world, pooledMutable);
                        this.waterLevel = Math.max((double)f, this.waterLevel);
                        bl |= box.minY < (double)f;
                    }
                }
            }
        }
        return bl;
    }

    @Nullable
    private Location getUnderWaterLocation() {
        Box box = this.getBoundingBox();
        double d = box.maxY + 0.001;
        int i = MathHelper.floor(box.minX);
        int j = MathHelper.ceil(box.maxX);
        int k = MathHelper.floor(box.maxY);
        int l = MathHelper.ceil(d);
        int m = MathHelper.floor(box.minZ);
        int n = MathHelper.ceil(box.maxZ);
        boolean bl = false;
        try (BlockPos.PooledMutable pooledMutable = BlockPos.PooledMutable.get();){
            for (int o = i; o < j; ++o) {
                for (int p = k; p < l; ++p) {
                    for (int q = m; q < n; ++q) {
                        pooledMutable.method_10113(o, p, q);
                        FluidState fluidState = this.world.getFluidState(pooledMutable);
                        if (!fluidState.matches(FluidTags.WATER) || !(d < (double)((float)pooledMutable.getY() + fluidState.getHeight(this.world, pooledMutable)))) continue;
                        if (fluidState.isStill()) {
                            bl = true;
                            continue;
                        }
                        Location location = Location.UNDER_FLOWING_WATER;
                        return location;
                    }
                }
            }
        }
        return bl ? Location.UNDER_WATER : null;
    }

    private void updateVelocity() {
        double d = -0.04f;
        double e = this.hasNoGravity() ? 0.0 : (double)-0.04f;
        double f = 0.0;
        this.velocityDecay = 0.05f;
        if (this.lastLocation == Location.IN_AIR && this.location != Location.IN_AIR && this.location != Location.ON_LAND) {
            this.waterLevel = this.getBoundingBox().minY + (double)this.getHeight();
            this.setPosition(this.x, (double)(this.method_7544() - this.getHeight()) + 0.101, this.z);
            this.setVelocity(this.getVelocity().multiply(1.0, 0.0, 1.0));
            this.fallVelocity = 0.0;
            this.location = Location.IN_WATER;
        } else {
            if (this.location == Location.IN_WATER) {
                f = (this.waterLevel - this.getBoundingBox().minY) / (double)this.getHeight();
                this.velocityDecay = 0.9f;
            } else if (this.location == Location.UNDER_FLOWING_WATER) {
                e = -7.0E-4;
                this.velocityDecay = 0.9f;
            } else if (this.location == Location.UNDER_WATER) {
                f = 0.01f;
                this.velocityDecay = 0.45f;
            } else if (this.location == Location.IN_AIR) {
                this.velocityDecay = 0.9f;
            } else if (this.location == Location.ON_LAND) {
                this.velocityDecay = this.field_7714;
                if (this.getPrimaryPassenger() instanceof PlayerEntity) {
                    this.field_7714 /= 2.0f;
                }
            }
            Vec3d vec3d = this.getVelocity();
            this.setVelocity(vec3d.x * (double)this.velocityDecay, vec3d.y + e, vec3d.z * (double)this.velocityDecay);
            this.yawVelocity *= this.velocityDecay;
            if (f > 0.0) {
                Vec3d vec3d2 = this.getVelocity();
                this.setVelocity(vec3d2.x, (vec3d2.y + f * 0.06153846016296973) * 0.75, vec3d2.z);
            }
        }
    }

    private void updatePaddles() {
        if (!this.hasPassengers()) {
            return;
        }
        float f = 0.0f;
        if (this.pressingLeft) {
            this.yawVelocity -= 1.0f;
        }
        if (this.pressingRight) {
            this.yawVelocity += 1.0f;
        }
        if (this.pressingRight != this.pressingLeft && !this.pressingForward && !this.pressingBack) {
            f += 0.005f;
        }
        this.yaw += this.yawVelocity;
        if (this.pressingForward) {
            f += 0.04f;
        }
        if (this.pressingBack) {
            f -= 0.005f;
        }
        this.setVelocity(this.getVelocity().add(MathHelper.sin(-this.yaw * ((float)Math.PI / 180)) * f, 0.0, MathHelper.cos(this.yaw * ((float)Math.PI / 180)) * f));
        this.setPaddleMovings(this.pressingRight && !this.pressingLeft || this.pressingForward, this.pressingLeft && !this.pressingRight || this.pressingForward);
    }

    @Override
    public void updatePassengerPosition(Entity entity) {
        if (!this.hasPassenger(entity)) {
            return;
        }
        float f = 0.0f;
        float g = (float)((this.removed ? (double)0.01f : this.getMountedHeightOffset()) + entity.getHeightOffset());
        if (this.getPassengerList().size() > 1) {
            int i = this.getPassengerList().indexOf(entity);
            f = i == 0 ? 0.2f : -0.6f;
            if (entity instanceof AnimalEntity) {
                f = (float)((double)f + 0.2);
            }
        }
        Vec3d vec3d = new Vec3d(f, 0.0, 0.0).rotateY(-this.yaw * ((float)Math.PI / 180) - 1.5707964f);
        entity.setPosition(this.x + vec3d.x, this.y + (double)g, this.z + vec3d.z);
        entity.yaw += this.yawVelocity;
        entity.setHeadYaw(entity.getHeadYaw() + this.yawVelocity);
        this.copyEntityData(entity);
        if (entity instanceof AnimalEntity && this.getPassengerList().size() > 1) {
            int j = entity.getEntityId() % 2 == 0 ? 90 : 270;
            entity.setYaw(((AnimalEntity)entity).bodyYaw + (float)j);
            entity.setHeadYaw(entity.getHeadYaw() + (float)j);
        }
    }

    protected void copyEntityData(Entity entity) {
        entity.setYaw(this.yaw);
        float f = MathHelper.wrapDegrees(entity.yaw - this.yaw);
        float g = MathHelper.clamp(f, -105.0f, 105.0f);
        entity.prevYaw += g - f;
        entity.yaw += g - f;
        entity.setHeadYaw(entity.yaw);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
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
            this.setBoatType(Type.getType(compoundTag.getString("Type")));
        }
    }

    @Override
    public boolean interact(PlayerEntity playerEntity, Hand hand) {
        if (playerEntity.shouldCancelInteraction()) {
            return false;
        }
        if (!this.world.isClient && this.ticksUnderwater < 60.0f) {
            playerEntity.startRiding(this);
        }
        return true;
    }

    @Override
    protected void fall(double d, boolean bl, BlockState blockState, BlockPos blockPos) {
        this.fallVelocity = this.getVelocity().y;
        if (this.hasVehicle()) {
            return;
        }
        if (bl) {
            if (this.fallDistance > 3.0f) {
                if (this.location != Location.ON_LAND) {
                    this.fallDistance = 0.0f;
                    return;
                }
                this.handleFallDamage(this.fallDistance, 1.0f);
                if (!this.world.isClient && !this.removed) {
                    this.remove();
                    if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
                        int i;
                        for (i = 0; i < 3; ++i) {
                            this.dropItem(this.getBoatType().getBaseBlock());
                        }
                        for (i = 0; i < 2; ++i) {
                            this.dropItem(Items.STICK);
                        }
                    }
                }
            }
            this.fallDistance = 0.0f;
        } else if (!this.world.getFluidState(new BlockPos(this).down()).matches(FluidTags.WATER) && d < 0.0) {
            this.fallDistance = (float)((double)this.fallDistance - d);
        }
    }

    public boolean isPaddleMoving(int i) {
        return this.dataTracker.get(i == 0 ? LEFT_PADDLE_MOVING : RIGHT_PADDLE_MOVING) != false && this.getPrimaryPassenger() != null;
    }

    public void setDamageWobbleStrength(float f) {
        this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(f));
    }

    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH).floatValue();
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

    @Environment(value=EnvType.CLIENT)
    public float interpolateBubbleWobble(float f) {
        return MathHelper.lerp(f, this.lastBubbleWobble, this.bubbleWobble);
    }

    public void setDamageWobbleSide(int i) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, i);
    }

    public int getDamageWobbleSide() {
        return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }

    public void setBoatType(Type type) {
        this.dataTracker.set(BOAT_TYPE, type.ordinal());
    }

    public Type getBoatType() {
        return Type.getType(this.dataTracker.get(BOAT_TYPE));
    }

    @Override
    protected boolean canAddPassenger(Entity entity) {
        return this.getPassengerList().size() < 2 && !this.isInFluid(FluidTags.WATER);
    }

    @Override
    @Nullable
    public Entity getPrimaryPassenger() {
        List<Entity> list = this.getPassengerList();
        return list.isEmpty() ? null : list.get(0);
    }

    @Environment(value=EnvType.CLIENT)
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

    public static enum Type {
        OAK(Blocks.OAK_PLANKS, "oak"),
        SPRUCE(Blocks.SPRUCE_PLANKS, "spruce"),
        BIRCH(Blocks.BIRCH_PLANKS, "birch"),
        JUNGLE(Blocks.JUNGLE_PLANKS, "jungle"),
        ACACIA(Blocks.ACACIA_PLANKS, "acacia"),
        DARK_OAK(Blocks.DARK_OAK_PLANKS, "dark_oak");

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

        public static Type getType(int i) {
            Type[] types = Type.values();
            if (i < 0 || i >= types.length) {
                i = 0;
            }
            return types[i];
        }

        public static Type getType(String string) {
            Type[] types = Type.values();
            for (int i = 0; i < types.length; ++i) {
                if (!types[i].getName().equals(string)) continue;
                return types[i];
            }
            return types[0];
        }
    }

    public static enum Location {
        IN_WATER,
        UNDER_WATER,
        UNDER_FLOWING_WATER,
        ON_LAND,
        IN_AIR;

    }
}

