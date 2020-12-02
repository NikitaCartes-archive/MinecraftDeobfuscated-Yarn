/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.vehicle;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.datafixers.util.Pair;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.AbstractRailBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PoweredRailBlock;
import net.minecraft.block.enums.RailShape;
import net.minecraft.class_5459;
import net.minecraft.entity.Dismounting;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityDimensions;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.IronGolemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.BoatEntity;
import net.minecraft.entity.vehicle.ChestMinecartEntity;
import net.minecraft.entity.vehicle.CommandBlockMinecartEntity;
import net.minecraft.entity.vehicle.FurnaceMinecartEntity;
import net.minecraft.entity.vehicle.HopperMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.entity.vehicle.SpawnerMinecartEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.network.Packet;
import net.minecraft.network.packet.s2c.play.EntitySpawnS2CPacket;
import net.minecraft.predicate.entity.EntityPredicates;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractMinecartEntity
extends Entity {
    private static final TrackedData<Integer> DAMAGE_WOBBLE_TICKS = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> DAMAGE_WOBBLE_SIDE = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Float> DAMAGE_WOBBLE_STRENGTH = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.FLOAT);
    private static final TrackedData<Integer> CUSTOM_BLOCK_ID = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Integer> CUSTOM_BLOCK_OFFSET = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.INTEGER);
    private static final TrackedData<Boolean> CUSTOM_BLOCK_PRESENT = DataTracker.registerData(AbstractMinecartEntity.class, TrackedDataHandlerRegistry.BOOLEAN);
    private static final ImmutableMap<EntityPose, ImmutableList<Integer>> DISMOUNT_FREE_Y_SPACES_NEEDED = ImmutableMap.of(EntityPose.STANDING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(-1)), EntityPose.CROUCHING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1), Integer.valueOf(-1)), EntityPose.SWIMMING, ImmutableList.of(Integer.valueOf(0), Integer.valueOf(1)));
    private boolean yawFlipped;
    private static final Map<RailShape, Pair<Vec3i, Vec3i>> ADJACENT_RAIL_POSITIONS_BY_SHAPE = Util.make(Maps.newEnumMap(RailShape.class), enumMap -> {
        Vec3i vec3i = Direction.WEST.getVector();
        Vec3i vec3i2 = Direction.EAST.getVector();
        Vec3i vec3i3 = Direction.NORTH.getVector();
        Vec3i vec3i4 = Direction.SOUTH.getVector();
        Vec3i vec3i5 = vec3i.down();
        Vec3i vec3i6 = vec3i2.down();
        Vec3i vec3i7 = vec3i3.down();
        Vec3i vec3i8 = vec3i4.down();
        enumMap.put(RailShape.NORTH_SOUTH, Pair.of(vec3i3, vec3i4));
        enumMap.put(RailShape.EAST_WEST, Pair.of(vec3i, vec3i2));
        enumMap.put(RailShape.ASCENDING_EAST, Pair.of(vec3i5, vec3i2));
        enumMap.put(RailShape.ASCENDING_WEST, Pair.of(vec3i, vec3i6));
        enumMap.put(RailShape.ASCENDING_NORTH, Pair.of(vec3i3, vec3i8));
        enumMap.put(RailShape.ASCENDING_SOUTH, Pair.of(vec3i7, vec3i4));
        enumMap.put(RailShape.SOUTH_EAST, Pair.of(vec3i4, vec3i2));
        enumMap.put(RailShape.SOUTH_WEST, Pair.of(vec3i4, vec3i));
        enumMap.put(RailShape.NORTH_WEST, Pair.of(vec3i3, vec3i));
        enumMap.put(RailShape.NORTH_EAST, Pair.of(vec3i3, vec3i2));
    });
    private int clientInterpolationSteps;
    private double clientX;
    private double clientY;
    private double clientZ;
    private double clientYaw;
    private double clientPitch;
    @Environment(value=EnvType.CLIENT)
    private double clientXVelocity;
    @Environment(value=EnvType.CLIENT)
    private double clientYVelocity;
    @Environment(value=EnvType.CLIENT)
    private double clientZVelocity;

    protected AbstractMinecartEntity(EntityType<?> entityType, World world) {
        super(entityType, world);
        this.inanimate = true;
    }

    protected AbstractMinecartEntity(EntityType<?> type, World world, double x, double y, double z) {
        this(type, world);
        this.updatePosition(x, y, z);
        this.prevX = x;
        this.prevY = y;
        this.prevZ = z;
    }

    public static AbstractMinecartEntity create(World world, double x, double y, double z, Type type) {
        if (type == Type.CHEST) {
            return new ChestMinecartEntity(world, x, y, z);
        }
        if (type == Type.FURNACE) {
            return new FurnaceMinecartEntity(world, x, y, z);
        }
        if (type == Type.TNT) {
            return new TntMinecartEntity(world, x, y, z);
        }
        if (type == Type.SPAWNER) {
            return new SpawnerMinecartEntity(world, x, y, z);
        }
        if (type == Type.HOPPER) {
            return new HopperMinecartEntity(world, x, y, z);
        }
        if (type == Type.COMMAND_BLOCK) {
            return new CommandBlockMinecartEntity(world, x, y, z);
        }
        return new MinecartEntity(world, x, y, z);
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
        this.dataTracker.startTracking(CUSTOM_BLOCK_ID, Block.getRawIdFromState(Blocks.AIR.getDefaultState()));
        this.dataTracker.startTracking(CUSTOM_BLOCK_OFFSET, 6);
        this.dataTracker.startTracking(CUSTOM_BLOCK_PRESENT, false);
    }

    @Override
    public boolean collidesWith(Entity other) {
        return BoatEntity.method_30959(this, other);
    }

    @Override
    public boolean isPushable() {
        return true;
    }

    @Override
    protected Vec3d method_30633(Direction.Axis axis, class_5459.class_5460 arg) {
        return LivingEntity.method_31079(super.method_30633(axis, arg));
    }

    @Override
    public double getMountedHeightOffset() {
        return 0.0;
    }

    @Override
    public Vec3d updatePassengerForDismount(LivingEntity passenger) {
        Direction direction = this.getMovementDirection();
        if (direction.getAxis() == Direction.Axis.Y) {
            return super.updatePassengerForDismount(passenger);
        }
        int[][] is = Dismounting.getDismountOffsets(direction);
        BlockPos blockPos2 = this.getBlockPos();
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        ImmutableList<EntityPose> immutableList = passenger.getPoses();
        for (EntityPose entityPose : immutableList) {
            EntityDimensions entityDimensions = passenger.getDimensions(entityPose);
            float f = Math.min(entityDimensions.width, 1.0f) / 2.0f;
            Iterator iterator = DISMOUNT_FREE_Y_SPACES_NEEDED.get((Object)entityPose).iterator();
            while (iterator.hasNext()) {
                int i = (Integer)iterator.next();
                for (int[] js : is) {
                    Vec3d vec3d;
                    Box box;
                    mutable.set(blockPos2.getX() + js[0], blockPos2.getY() + i, blockPos2.getZ() + js[1]);
                    double d = this.world.getDismountHeight(Dismounting.getCollisionShape(this.world, mutable), () -> Dismounting.getCollisionShape(this.world, (BlockPos)mutable.down()));
                    if (!Dismounting.canDismountInBlock(d) || !Dismounting.canPlaceEntityAt(this.world, passenger, (box = new Box(-f, 0.0, -f, f, entityDimensions.height, f)).offset(vec3d = Vec3d.ofCenter(mutable, d)))) continue;
                    passenger.setPose(entityPose);
                    return vec3d;
                }
            }
        }
        double e = this.getBoundingBox().maxY;
        mutable.set((double)blockPos2.getX(), e, (double)blockPos2.getZ());
        for (EntityPose entityPose2 : immutableList) {
            double g = passenger.getDimensions((EntityPose)entityPose2).height;
            int j = MathHelper.ceil(e - (double)mutable.getY() + g);
            double h = Dismounting.getCeilingHeight(mutable, j, blockPos -> this.world.getBlockState((BlockPos)blockPos).getCollisionShape(this.world, (BlockPos)blockPos));
            if (!(e + g <= h)) continue;
            passenger.setPose(entityPose2);
            break;
        }
        return super.updatePassengerForDismount(passenger);
    }

    @Override
    public boolean damage(DamageSource source, float amount) {
        boolean bl;
        if (this.world.isClient || this.isRemoved()) {
            return true;
        }
        if (this.isInvulnerableTo(source)) {
            return false;
        }
        this.method_32875(source.getAttacker(), GameEvent.ENTITY_HIT);
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.scheduleVelocityUpdate();
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + amount * 10.0f);
        boolean bl2 = bl = source.getAttacker() instanceof PlayerEntity && ((PlayerEntity)source.getAttacker()).getAbilities().creativeMode;
        if (bl || this.getDamageWobbleStrength() > 40.0f) {
            this.removeAllPassengers();
            if (!bl || this.hasCustomName()) {
                this.dropItems(source);
            } else {
                this.discard();
            }
        }
        return true;
    }

    @Override
    protected float getVelocityMultiplier() {
        BlockState blockState = this.world.getBlockState(this.getBlockPos());
        if (blockState.isIn(BlockTags.RAILS)) {
            return 1.0f;
        }
        return super.getVelocityMultiplier();
    }

    public void dropItems(DamageSource damageSource) {
        this.remove(Entity.RemovalReason.KILLED);
        if (this.world.getGameRules().getBoolean(GameRules.DO_ENTITY_DROPS)) {
            ItemStack itemStack = new ItemStack(Items.MINECART);
            if (this.hasCustomName()) {
                itemStack.setCustomName(this.getCustomName());
            }
            this.dropStack(itemStack);
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void animateDamage() {
        this.setDamageWobbleSide(-this.getDamageWobbleSide());
        this.setDamageWobbleTicks(10);
        this.setDamageWobbleStrength(this.getDamageWobbleStrength() + this.getDamageWobbleStrength() * 10.0f);
    }

    @Override
    public boolean collides() {
        return !this.isRemoved();
    }

    private static Pair<Vec3i, Vec3i> getAdjacentRailPositionsByShape(RailShape shape) {
        return ADJACENT_RAIL_POSITIONS_BY_SHAPE.get(shape);
    }

    @Override
    public Direction getMovementDirection() {
        return this.yawFlipped ? this.getHorizontalFacing().getOpposite().rotateYClockwise() : this.getHorizontalFacing().rotateYClockwise();
    }

    @Override
    public void tick() {
        double m;
        BlockPos blockPos;
        BlockState blockState;
        int k;
        int j;
        int i;
        if (this.getDamageWobbleTicks() > 0) {
            this.setDamageWobbleTicks(this.getDamageWobbleTicks() - 1);
        }
        if (this.getDamageWobbleStrength() > 0.0f) {
            this.setDamageWobbleStrength(this.getDamageWobbleStrength() - 1.0f);
        }
        this.method_31473();
        this.tickNetherPortal();
        if (this.world.isClient) {
            if (this.clientInterpolationSteps > 0) {
                double d = this.getX() + (this.clientX - this.getX()) / (double)this.clientInterpolationSteps;
                double e = this.getY() + (this.clientY - this.getY()) / (double)this.clientInterpolationSteps;
                double f = this.getZ() + (this.clientZ - this.getZ()) / (double)this.clientInterpolationSteps;
                double g = MathHelper.wrapDegrees(this.clientYaw - (double)this.yaw);
                this.yaw = (float)((double)this.yaw + g / (double)this.clientInterpolationSteps);
                this.pitch = (float)((double)this.pitch + (this.clientPitch - (double)this.pitch) / (double)this.clientInterpolationSteps);
                --this.clientInterpolationSteps;
                this.updatePosition(d, e, f);
                this.setRotation(this.yaw, this.pitch);
            } else {
                this.refreshPosition();
                this.setRotation(this.yaw, this.pitch);
            }
            return;
        }
        if (!this.hasNoGravity()) {
            double d = this.isTouchingWater() ? -0.005 : -0.04;
            this.setVelocity(this.getVelocity().add(0.0, d, 0.0));
        }
        if (this.world.getBlockState(new BlockPos(i = MathHelper.floor(this.getX()), (j = MathHelper.floor(this.getY())) - 1, k = MathHelper.floor(this.getZ()))).isIn(BlockTags.RAILS)) {
            --j;
        }
        if (AbstractRailBlock.isRail(blockState = this.world.getBlockState(blockPos = new BlockPos(i, j, k)))) {
            this.moveOnRail(blockPos, blockState);
            if (blockState.isOf(Blocks.ACTIVATOR_RAIL)) {
                this.onActivatorRail(i, j, k, blockState.get(PoweredRailBlock.POWERED));
            }
        } else {
            this.moveOffRail();
        }
        this.checkBlockCollision();
        this.pitch = 0.0f;
        double h = this.prevX - this.getX();
        double l = this.prevZ - this.getZ();
        if (h * h + l * l > 0.001) {
            this.yaw = (float)(MathHelper.atan2(l, h) * 180.0 / Math.PI);
            if (this.yawFlipped) {
                this.yaw += 180.0f;
            }
        }
        if ((m = (double)MathHelper.wrapDegrees(this.yaw - this.prevYaw)) < -170.0 || m >= 170.0) {
            this.yaw += 180.0f;
            this.yawFlipped = !this.yawFlipped;
        }
        this.setRotation(this.yaw, this.pitch);
        if (this.getMinecartType() == Type.RIDEABLE && AbstractMinecartEntity.squaredHorizontalLength(this.getVelocity()) > 0.01) {
            List<Entity> list = this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2f, 0.0, 0.2f), EntityPredicates.canBePushedBy(this));
            if (!list.isEmpty()) {
                for (int n = 0; n < list.size(); ++n) {
                    Entity entity = list.get(n);
                    if (entity instanceof PlayerEntity || entity instanceof IronGolemEntity || entity instanceof AbstractMinecartEntity || this.hasPassengers() || entity.hasVehicle()) {
                        entity.pushAwayFrom(this);
                        continue;
                    }
                    entity.startRiding(this);
                }
            }
        } else {
            for (Entity entity2 : this.world.getOtherEntities(this, this.getBoundingBox().expand(0.2f, 0.0, 0.2f))) {
                if (this.hasPassenger(entity2) || !entity2.isPushable() || !(entity2 instanceof AbstractMinecartEntity)) continue;
                entity2.pushAwayFrom(this);
            }
        }
        this.updateWaterState();
        if (this.isInLava()) {
            this.setOnFireFromLava();
            this.fallDistance *= 0.5f;
        }
        this.firstUpdate = false;
    }

    protected double getMaxOffRailSpeed() {
        return (this.isTouchingWater() ? 4.0 : 8.0) / 20.0;
    }

    public void onActivatorRail(int x, int y, int z, boolean powered) {
    }

    protected void moveOffRail() {
        double d = this.getMaxOffRailSpeed();
        Vec3d vec3d = this.getVelocity();
        this.setVelocity(MathHelper.clamp(vec3d.x, -d, d), vec3d.y, MathHelper.clamp(vec3d.z, -d, d));
        if (this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.5));
        }
        this.move(MovementType.SELF, this.getVelocity());
        if (!this.onGround) {
            this.setVelocity(this.getVelocity().multiply(0.95));
        }
    }

    protected void moveOnRail(BlockPos pos, BlockState state) {
        double w;
        Vec3d vec3d5;
        double u;
        double t;
        double s;
        this.fallDistance = 0.0f;
        double d = this.getX();
        double e = this.getY();
        double f = this.getZ();
        Vec3d vec3d = this.snapPositionToRail(d, e, f);
        e = pos.getY();
        boolean bl = false;
        boolean bl2 = false;
        if (state.isOf(Blocks.POWERED_RAIL)) {
            bl = state.get(PoweredRailBlock.POWERED);
            bl2 = !bl;
        }
        double g = 0.0078125;
        if (this.isTouchingWater()) {
            g *= 0.2;
        }
        Vec3d vec3d2 = this.getVelocity();
        RailShape railShape = state.get(((AbstractRailBlock)state.getBlock()).getShapeProperty());
        switch (railShape) {
            case ASCENDING_EAST: {
                this.setVelocity(vec3d2.add(-g, 0.0, 0.0));
                e += 1.0;
                break;
            }
            case ASCENDING_WEST: {
                this.setVelocity(vec3d2.add(g, 0.0, 0.0));
                e += 1.0;
                break;
            }
            case ASCENDING_NORTH: {
                this.setVelocity(vec3d2.add(0.0, 0.0, g));
                e += 1.0;
                break;
            }
            case ASCENDING_SOUTH: {
                this.setVelocity(vec3d2.add(0.0, 0.0, -g));
                e += 1.0;
            }
        }
        vec3d2 = this.getVelocity();
        Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
        Vec3i vec3i = pair.getFirst();
        Vec3i vec3i2 = pair.getSecond();
        double h = vec3i2.getX() - vec3i.getX();
        double i = vec3i2.getZ() - vec3i.getZ();
        double j = Math.sqrt(h * h + i * i);
        double k = vec3d2.x * h + vec3d2.z * i;
        if (k < 0.0) {
            h = -h;
            i = -i;
        }
        double l = Math.min(2.0, Math.sqrt(AbstractMinecartEntity.squaredHorizontalLength(vec3d2)));
        vec3d2 = new Vec3d(l * h / j, vec3d2.y, l * i / j);
        this.setVelocity(vec3d2);
        Entity entity = this.getFirstPassenger();
        if (entity instanceof PlayerEntity) {
            Vec3d vec3d3 = entity.getVelocity();
            double m = AbstractMinecartEntity.squaredHorizontalLength(vec3d3);
            double n = AbstractMinecartEntity.squaredHorizontalLength(this.getVelocity());
            if (m > 1.0E-4 && n < 0.01) {
                this.setVelocity(this.getVelocity().add(vec3d3.x * 0.1, 0.0, vec3d3.z * 0.1));
                bl2 = false;
            }
        }
        if (bl2) {
            double o = Math.sqrt(AbstractMinecartEntity.squaredHorizontalLength(this.getVelocity()));
            if (o < 0.03) {
                this.setVelocity(Vec3d.ZERO);
            } else {
                this.setVelocity(this.getVelocity().multiply(0.5, 0.0, 0.5));
            }
        }
        double o = (double)pos.getX() + 0.5 + (double)vec3i.getX() * 0.5;
        double p = (double)pos.getZ() + 0.5 + (double)vec3i.getZ() * 0.5;
        double q = (double)pos.getX() + 0.5 + (double)vec3i2.getX() * 0.5;
        double r = (double)pos.getZ() + 0.5 + (double)vec3i2.getZ() * 0.5;
        h = q - o;
        i = r - p;
        if (h == 0.0) {
            s = f - (double)pos.getZ();
        } else if (i == 0.0) {
            s = d - (double)pos.getX();
        } else {
            t = d - o;
            u = f - p;
            s = (t * h + u * i) * 2.0;
        }
        d = o + h * s;
        f = p + i * s;
        this.updatePosition(d, e, f);
        t = this.hasPassengers() ? 0.75 : 1.0;
        u = this.getMaxOffRailSpeed();
        vec3d2 = this.getVelocity();
        this.move(MovementType.SELF, new Vec3d(MathHelper.clamp(t * vec3d2.x, -u, u), 0.0, MathHelper.clamp(t * vec3d2.z, -u, u)));
        if (vec3i.getY() != 0 && MathHelper.floor(this.getX()) - pos.getX() == vec3i.getX() && MathHelper.floor(this.getZ()) - pos.getZ() == vec3i.getZ()) {
            this.updatePosition(this.getX(), this.getY() + (double)vec3i.getY(), this.getZ());
        } else if (vec3i2.getY() != 0 && MathHelper.floor(this.getX()) - pos.getX() == vec3i2.getX() && MathHelper.floor(this.getZ()) - pos.getZ() == vec3i2.getZ()) {
            this.updatePosition(this.getX(), this.getY() + (double)vec3i2.getY(), this.getZ());
        }
        this.applySlowdown();
        Vec3d vec3d4 = this.snapPositionToRail(this.getX(), this.getY(), this.getZ());
        if (vec3d4 != null && vec3d != null) {
            double v = (vec3d.y - vec3d4.y) * 0.05;
            vec3d5 = this.getVelocity();
            w = Math.sqrt(AbstractMinecartEntity.squaredHorizontalLength(vec3d5));
            if (w > 0.0) {
                this.setVelocity(vec3d5.multiply((w + v) / w, 1.0, (w + v) / w));
            }
            this.updatePosition(this.getX(), vec3d4.y, this.getZ());
        }
        int x = MathHelper.floor(this.getX());
        int y = MathHelper.floor(this.getZ());
        if (x != pos.getX() || y != pos.getZ()) {
            vec3d5 = this.getVelocity();
            w = Math.sqrt(AbstractMinecartEntity.squaredHorizontalLength(vec3d5));
            this.setVelocity(w * (double)(x - pos.getX()), vec3d5.y, w * (double)(y - pos.getZ()));
        }
        if (bl) {
            vec3d5 = this.getVelocity();
            w = Math.sqrt(AbstractMinecartEntity.squaredHorizontalLength(vec3d5));
            if (w > 0.01) {
                double z = 0.06;
                this.setVelocity(vec3d5.add(vec3d5.x / w * 0.06, 0.0, vec3d5.z / w * 0.06));
            } else {
                Vec3d vec3d6 = this.getVelocity();
                double aa = vec3d6.x;
                double ab = vec3d6.z;
                if (railShape == RailShape.EAST_WEST) {
                    if (this.willHitBlockAt(pos.west())) {
                        aa = 0.02;
                    } else if (this.willHitBlockAt(pos.east())) {
                        aa = -0.02;
                    }
                } else if (railShape == RailShape.NORTH_SOUTH) {
                    if (this.willHitBlockAt(pos.north())) {
                        ab = 0.02;
                    } else if (this.willHitBlockAt(pos.south())) {
                        ab = -0.02;
                    }
                } else {
                    return;
                }
                this.setVelocity(aa, vec3d6.y, ab);
            }
        }
    }

    private boolean willHitBlockAt(BlockPos pos) {
        return this.world.getBlockState(pos).isSolidBlock(this.world, pos);
    }

    protected void applySlowdown() {
        double d = this.hasPassengers() ? 0.997 : 0.96;
        Vec3d vec3d = this.getVelocity();
        vec3d = vec3d.multiply(d, 0.0, d);
        if (this.isTouchingWater()) {
            vec3d = vec3d.multiply(0.95f);
        }
        this.setVelocity(vec3d);
    }

    /**
     * This method is used to determine the minecart's render orientation, by computing a position along the rail slightly before and slightly after the minecart's actual position.
     */
    @Nullable
    @Environment(value=EnvType.CLIENT)
    public Vec3d snapPositionToRailWithOffset(double x, double y, double z, double offset) {
        BlockState blockState;
        int k;
        int j;
        int i = MathHelper.floor(x);
        if (this.world.getBlockState(new BlockPos(i, (j = MathHelper.floor(y)) - 1, k = MathHelper.floor(z))).isIn(BlockTags.RAILS)) {
            --j;
        }
        if (AbstractRailBlock.isRail(blockState = this.world.getBlockState(new BlockPos(i, j, k)))) {
            RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
            y = j;
            if (railShape.isAscending()) {
                y = j + 1;
            }
            Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
            Vec3i vec3i = pair.getFirst();
            Vec3i vec3i2 = pair.getSecond();
            double d = vec3i2.getX() - vec3i.getX();
            double e = vec3i2.getZ() - vec3i.getZ();
            double f = Math.sqrt(d * d + e * e);
            if (vec3i.getY() != 0 && MathHelper.floor(x += (d /= f) * offset) - i == vec3i.getX() && MathHelper.floor(z += (e /= f) * offset) - k == vec3i.getZ()) {
                y += (double)vec3i.getY();
            } else if (vec3i2.getY() != 0 && MathHelper.floor(x) - i == vec3i2.getX() && MathHelper.floor(z) - k == vec3i2.getZ()) {
                y += (double)vec3i2.getY();
            }
            return this.snapPositionToRail(x, y, z);
        }
        return null;
    }

    @Nullable
    public Vec3d snapPositionToRail(double x, double y, double z) {
        BlockState blockState;
        int k;
        int j;
        int i = MathHelper.floor(x);
        if (this.world.getBlockState(new BlockPos(i, (j = MathHelper.floor(y)) - 1, k = MathHelper.floor(z))).isIn(BlockTags.RAILS)) {
            --j;
        }
        if (AbstractRailBlock.isRail(blockState = this.world.getBlockState(new BlockPos(i, j, k)))) {
            double p;
            RailShape railShape = blockState.get(((AbstractRailBlock)blockState.getBlock()).getShapeProperty());
            Pair<Vec3i, Vec3i> pair = AbstractMinecartEntity.getAdjacentRailPositionsByShape(railShape);
            Vec3i vec3i = pair.getFirst();
            Vec3i vec3i2 = pair.getSecond();
            double d = (double)i + 0.5 + (double)vec3i.getX() * 0.5;
            double e = (double)j + 0.0625 + (double)vec3i.getY() * 0.5;
            double f = (double)k + 0.5 + (double)vec3i.getZ() * 0.5;
            double g = (double)i + 0.5 + (double)vec3i2.getX() * 0.5;
            double h = (double)j + 0.0625 + (double)vec3i2.getY() * 0.5;
            double l = (double)k + 0.5 + (double)vec3i2.getZ() * 0.5;
            double m = g - d;
            double n = (h - e) * 2.0;
            double o = l - f;
            if (m == 0.0) {
                p = z - (double)k;
            } else if (o == 0.0) {
                p = x - (double)i;
            } else {
                double q = x - d;
                double r = z - f;
                p = (q * m + r * o) * 2.0;
            }
            x = d + m * p;
            y = e + n * p;
            z = f + o * p;
            if (n < 0.0) {
                y += 1.0;
            } else if (n > 0.0) {
                y += 0.5;
            }
            return new Vec3d(x, y, z);
        }
        return null;
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public Box getVisibilityBoundingBox() {
        Box box = this.getBoundingBox();
        if (this.hasCustomBlock()) {
            return box.expand((double)Math.abs(this.getBlockOffset()) / 16.0);
        }
        return box;
    }

    @Override
    protected void readCustomDataFromTag(CompoundTag tag) {
        if (tag.getBoolean("CustomDisplayTile")) {
            this.setCustomBlock(NbtHelper.toBlockState(tag.getCompound("DisplayState")));
            this.setCustomBlockOffset(tag.getInt("DisplayOffset"));
        }
    }

    @Override
    protected void writeCustomDataToTag(CompoundTag tag) {
        if (this.hasCustomBlock()) {
            tag.putBoolean("CustomDisplayTile", true);
            tag.put("DisplayState", NbtHelper.fromBlockState(this.getContainedBlock()));
            tag.putInt("DisplayOffset", this.getBlockOffset());
        }
    }

    @Override
    public void pushAwayFrom(Entity entity) {
        double e;
        if (this.world.isClient) {
            return;
        }
        if (entity.noClip || this.noClip) {
            return;
        }
        if (this.hasPassenger(entity)) {
            return;
        }
        double d = entity.getX() - this.getX();
        double f = d * d + (e = entity.getZ() - this.getZ()) * e;
        if (f >= (double)1.0E-4f) {
            f = MathHelper.sqrt(f);
            d /= f;
            e /= f;
            double g = 1.0 / f;
            if (g > 1.0) {
                g = 1.0;
            }
            d *= g;
            e *= g;
            d *= (double)0.1f;
            e *= (double)0.1f;
            d *= (double)(1.0f - this.pushSpeedReduction);
            e *= (double)(1.0f - this.pushSpeedReduction);
            d *= 0.5;
            e *= 0.5;
            if (entity instanceof AbstractMinecartEntity) {
                Vec3d vec3d2;
                double i;
                double h = entity.getX() - this.getX();
                Vec3d vec3d = new Vec3d(h, 0.0, i = entity.getZ() - this.getZ()).normalize();
                double j = Math.abs(vec3d.dotProduct(vec3d2 = new Vec3d(MathHelper.cos(this.yaw * ((float)Math.PI / 180)), 0.0, MathHelper.sin(this.yaw * ((float)Math.PI / 180))).normalize()));
                if (j < (double)0.8f) {
                    return;
                }
                Vec3d vec3d3 = this.getVelocity();
                Vec3d vec3d4 = entity.getVelocity();
                if (((AbstractMinecartEntity)entity).getMinecartType() == Type.FURNACE && this.getMinecartType() != Type.FURNACE) {
                    this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
                    this.addVelocity(vec3d4.x - d, 0.0, vec3d4.z - e);
                    entity.setVelocity(vec3d4.multiply(0.95, 1.0, 0.95));
                } else if (((AbstractMinecartEntity)entity).getMinecartType() != Type.FURNACE && this.getMinecartType() == Type.FURNACE) {
                    entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
                    entity.addVelocity(vec3d3.x + d, 0.0, vec3d3.z + e);
                    this.setVelocity(vec3d3.multiply(0.95, 1.0, 0.95));
                } else {
                    double k = (vec3d4.x + vec3d3.x) / 2.0;
                    double l = (vec3d4.z + vec3d3.z) / 2.0;
                    this.setVelocity(vec3d3.multiply(0.2, 1.0, 0.2));
                    this.addVelocity(k - d, 0.0, l - e);
                    entity.setVelocity(vec3d4.multiply(0.2, 1.0, 0.2));
                    entity.addVelocity(k + d, 0.0, l + e);
                }
            } else {
                this.addVelocity(-d, 0.0, -e);
                entity.addVelocity(d / 4.0, 0.0, e / 4.0);
            }
        }
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void updateTrackedPositionAndAngles(double x, double y, double z, float yaw, float pitch, int interpolationSteps, boolean interpolate) {
        this.clientX = x;
        this.clientY = y;
        this.clientZ = z;
        this.clientYaw = yaw;
        this.clientPitch = pitch;
        this.clientInterpolationSteps = interpolationSteps + 2;
        this.setVelocity(this.clientXVelocity, this.clientYVelocity, this.clientZVelocity);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public void setVelocityClient(double x, double y, double z) {
        this.clientXVelocity = x;
        this.clientYVelocity = y;
        this.clientZVelocity = z;
        this.setVelocity(this.clientXVelocity, this.clientYVelocity, this.clientZVelocity);
    }

    public void setDamageWobbleStrength(float f) {
        this.dataTracker.set(DAMAGE_WOBBLE_STRENGTH, Float.valueOf(f));
    }

    public float getDamageWobbleStrength() {
        return this.dataTracker.get(DAMAGE_WOBBLE_STRENGTH).floatValue();
    }

    public void setDamageWobbleTicks(int wobbleTicks) {
        this.dataTracker.set(DAMAGE_WOBBLE_TICKS, wobbleTicks);
    }

    public int getDamageWobbleTicks() {
        return this.dataTracker.get(DAMAGE_WOBBLE_TICKS);
    }

    public void setDamageWobbleSide(int wobbleSide) {
        this.dataTracker.set(DAMAGE_WOBBLE_SIDE, wobbleSide);
    }

    public int getDamageWobbleSide() {
        return this.dataTracker.get(DAMAGE_WOBBLE_SIDE);
    }

    public abstract Type getMinecartType();

    public BlockState getContainedBlock() {
        if (!this.hasCustomBlock()) {
            return this.getDefaultContainedBlock();
        }
        return Block.getStateFromRawId(this.getDataTracker().get(CUSTOM_BLOCK_ID));
    }

    public BlockState getDefaultContainedBlock() {
        return Blocks.AIR.getDefaultState();
    }

    public int getBlockOffset() {
        if (!this.hasCustomBlock()) {
            return this.getDefaultBlockOffset();
        }
        return this.getDataTracker().get(CUSTOM_BLOCK_OFFSET);
    }

    public int getDefaultBlockOffset() {
        return 6;
    }

    public void setCustomBlock(BlockState state) {
        this.getDataTracker().set(CUSTOM_BLOCK_ID, Block.getRawIdFromState(state));
        this.setCustomBlockPresent(true);
    }

    public void setCustomBlockOffset(int offset) {
        this.getDataTracker().set(CUSTOM_BLOCK_OFFSET, offset);
        this.setCustomBlockPresent(true);
    }

    public boolean hasCustomBlock() {
        return this.getDataTracker().get(CUSTOM_BLOCK_PRESENT);
    }

    public void setCustomBlockPresent(boolean present) {
        this.getDataTracker().set(CUSTOM_BLOCK_PRESENT, present);
    }

    @Override
    public Packet<?> createSpawnPacket() {
        return new EntitySpawnS2CPacket(this);
    }

    @Override
    @Environment(value=EnvType.CLIENT)
    public ItemStack getPickBlockStack() {
        Item item;
        switch (this.getMinecartType()) {
            case FURNACE: {
                item = Items.FURNACE_MINECART;
                break;
            }
            case CHEST: {
                item = Items.CHEST_MINECART;
                break;
            }
            case TNT: {
                item = Items.TNT_MINECART;
                break;
            }
            case HOPPER: {
                item = Items.HOPPER_MINECART;
                break;
            }
            case COMMAND_BLOCK: {
                item = Items.COMMAND_BLOCK_MINECART;
                break;
            }
            default: {
                item = Items.MINECART;
            }
        }
        return new ItemStack(item);
    }

    public static enum Type {
        RIDEABLE,
        CHEST,
        FURNACE,
        TNT,
        SPAWNER,
        HOPPER,
        COMMAND_BLOCK;

    }
}

