/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Iterator;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.PistonBlock;
import net.minecraft.block.PistonHeadBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.enums.PistonType;
import net.minecraft.block.piston.PistonBehavior;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryWrapper;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * A piston block entity represents the block being pushed by a piston.
 */
public class PistonBlockEntity
extends BlockEntity {
    private static final int field_31382 = 2;
    private static final double field_31383 = 0.01;
    public static final double field_31381 = 0.51;
    private BlockState pushedBlock = Blocks.AIR.getDefaultState();
    private Direction facing;
    private boolean extending;
    private boolean source;
    private static final ThreadLocal<Direction> field_12205 = ThreadLocal.withInitial(() -> null);
    private float progress;
    private float lastProgress;
    private long savedWorldTime;
    private int field_26705;

    public PistonBlockEntity(BlockPos pos, BlockState state) {
        super(BlockEntityType.PISTON, pos, state);
    }

    public PistonBlockEntity(BlockPos pos, BlockState state, BlockState pushedBlock, Direction facing, boolean extending, boolean source) {
        this(pos, state);
        this.pushedBlock = pushedBlock;
        this.facing = facing;
        this.extending = extending;
        this.source = source;
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return this.createNbt();
    }

    public boolean isExtending() {
        return this.extending;
    }

    public Direction getFacing() {
        return this.facing;
    }

    public boolean isSource() {
        return this.source;
    }

    public float getProgress(float tickDelta) {
        if (tickDelta > 1.0f) {
            tickDelta = 1.0f;
        }
        return MathHelper.lerp(tickDelta, this.lastProgress, this.progress);
    }

    public float getRenderOffsetX(float tickDelta) {
        return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    public float getRenderOffsetY(float tickDelta) {
        return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    public float getRenderOffsetZ(float tickDelta) {
        return (float)this.facing.getOffsetZ() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    private float getAmountExtended(float progress) {
        return this.extending ? progress - 1.0f : 1.0f - progress;
    }

    private BlockState getHeadBlockState() {
        if (!this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock) {
            return (BlockState)((BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.SHORT, this.progress > 0.25f)).with(PistonHeadBlock.TYPE, this.pushedBlock.isOf(Blocks.STICKY_PISTON) ? PistonType.STICKY : PistonType.DEFAULT)).with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING));
        }
        return this.pushedBlock;
    }

    private static void pushEntities(World world, BlockPos pos, float f, PistonBlockEntity blockEntity) {
        Direction direction = blockEntity.getMovementDirection();
        double d = f - blockEntity.progress;
        VoxelShape voxelShape = blockEntity.getHeadBlockState().getCollisionShape(world, pos);
        if (voxelShape.isEmpty()) {
            return;
        }
        Box box = PistonBlockEntity.offsetHeadBox(pos, voxelShape.getBoundingBox(), blockEntity);
        List<Entity> list = world.getOtherEntities(null, Boxes.stretch(box, direction, d).union(box));
        if (list.isEmpty()) {
            return;
        }
        List<Box> list2 = voxelShape.getBoundingBoxes();
        boolean bl = blockEntity.pushedBlock.isOf(Blocks.SLIME_BLOCK);
        for (Entity entity : list) {
            Box box4;
            Box box2;
            Box box3;
            if (entity.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            if (bl) {
                if (entity instanceof ServerPlayerEntity) continue;
                Vec3d vec3d = entity.getVelocity();
                double e = vec3d.x;
                double g = vec3d.y;
                double h = vec3d.z;
                switch (direction.getAxis()) {
                    case X: {
                        e = direction.getOffsetX();
                        break;
                    }
                    case Y: {
                        g = direction.getOffsetY();
                        break;
                    }
                    case Z: {
                        h = direction.getOffsetZ();
                    }
                }
                entity.setVelocity(e, g, h);
            }
            double i = 0.0;
            Iterator<Box> iterator = list2.iterator();
            while (!(!iterator.hasNext() || (box3 = Boxes.stretch(PistonBlockEntity.offsetHeadBox(pos, box2 = iterator.next(), blockEntity), direction, d)).intersects(box4 = entity.getBoundingBox()) && (i = Math.max(i, PistonBlockEntity.getIntersectionSize(box3, direction, box4))) >= d)) {
            }
            if (i <= 0.0) continue;
            i = Math.min(i, d) + 0.01;
            PistonBlockEntity.moveEntity(direction, entity, i, direction);
            if (blockEntity.extending || !blockEntity.source) continue;
            PistonBlockEntity.push(pos, entity, direction, d);
        }
    }

    private static void moveEntity(Direction direction, Entity entity, double d, Direction movementDirection) {
        field_12205.set(direction);
        entity.move(MovementType.PISTON, new Vec3d(d * (double)movementDirection.getOffsetX(), d * (double)movementDirection.getOffsetY(), d * (double)movementDirection.getOffsetZ()));
        field_12205.set(null);
    }

    private static void moveEntitiesInHoneyBlock(World world, BlockPos pos, float f, PistonBlockEntity blockEntity) {
        if (!blockEntity.isPushingHoneyBlock()) {
            return;
        }
        Direction direction = blockEntity.getMovementDirection();
        if (!direction.getAxis().isHorizontal()) {
            return;
        }
        double d = blockEntity.pushedBlock.getCollisionShape(world, pos).getMax(Direction.Axis.Y);
        Box box = PistonBlockEntity.offsetHeadBox(pos, new Box(0.0, d, 0.0, 1.0, 1.5000000999999998, 1.0), blockEntity);
        double e = f - blockEntity.progress;
        List<Entity> list = world.getOtherEntities(null, box, entity -> PistonBlockEntity.canMoveEntity(box, entity));
        for (Entity entity2 : list) {
            PistonBlockEntity.moveEntity(direction, entity2, e, direction);
        }
    }

    private static boolean canMoveEntity(Box box, Entity entity) {
        return entity.getPistonBehavior() == PistonBehavior.NORMAL && entity.isOnGround() && entity.getX() >= box.minX && entity.getX() <= box.maxX && entity.getZ() >= box.minZ && entity.getZ() <= box.maxZ;
    }

    private boolean isPushingHoneyBlock() {
        return this.pushedBlock.isOf(Blocks.HONEY_BLOCK);
    }

    public Direction getMovementDirection() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }

    private static double getIntersectionSize(Box box, Direction direction, Box box2) {
        switch (direction) {
            case EAST: {
                return box.maxX - box2.minX;
            }
            case WEST: {
                return box2.maxX - box.minX;
            }
            default: {
                return box.maxY - box2.minY;
            }
            case DOWN: {
                return box2.maxY - box.minY;
            }
            case SOUTH: {
                return box.maxZ - box2.minZ;
            }
            case NORTH: 
        }
        return box2.maxZ - box.minZ;
    }

    private static Box offsetHeadBox(BlockPos pos, Box box, PistonBlockEntity blockEntity) {
        double d = blockEntity.getAmountExtended(blockEntity.progress);
        return box.offset((double)pos.getX() + d * (double)blockEntity.facing.getOffsetX(), (double)pos.getY() + d * (double)blockEntity.facing.getOffsetY(), (double)pos.getZ() + d * (double)blockEntity.facing.getOffsetZ());
    }

    private static void push(BlockPos pos, Entity entity, Direction direction, double amount) {
        double e;
        Direction direction2;
        double d;
        Box box2;
        Box box = entity.getBoundingBox();
        if (box.intersects(box2 = VoxelShapes.fullCube().getBoundingBox().offset(pos)) && Math.abs((d = PistonBlockEntity.getIntersectionSize(box2, direction2 = direction.getOpposite(), box) + 0.01) - (e = PistonBlockEntity.getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01)) < 0.01) {
            d = Math.min(d, amount) + 0.01;
            PistonBlockEntity.moveEntity(direction, entity, d, direction2);
        }
    }

    public BlockState getPushedBlock() {
        return this.pushedBlock;
    }

    public void finish() {
        if (this.world != null && (this.lastProgress < 1.0f || this.world.isClient)) {
            this.lastProgress = this.progress = 1.0f;
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.world.getBlockState(this.pos).isOf(Blocks.MOVING_PISTON)) {
                BlockState blockState = this.source ? Blocks.AIR.getDefaultState() : Block.postProcessState(this.pushedBlock, this.world, this.pos);
                this.world.setBlockState(this.pos, blockState, Block.NOTIFY_ALL);
                this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
            }
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, PistonBlockEntity blockEntity) {
        blockEntity.savedWorldTime = world.getTime();
        blockEntity.lastProgress = blockEntity.progress;
        if (blockEntity.lastProgress >= 1.0f) {
            if (world.isClient && blockEntity.field_26705 < 5) {
                ++blockEntity.field_26705;
                return;
            }
            world.removeBlockEntity(pos);
            blockEntity.markRemoved();
            if (world.getBlockState(pos).isOf(Blocks.MOVING_PISTON)) {
                BlockState blockState = Block.postProcessState(blockEntity.pushedBlock, world, pos);
                if (blockState.isAir()) {
                    world.setBlockState(pos, blockEntity.pushedBlock, Block.NO_REDRAW | Block.FORCE_STATE | Block.MOVED);
                    Block.replace(blockEntity.pushedBlock, blockState, world, pos, 3);
                } else {
                    if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED).booleanValue()) {
                        blockState = (BlockState)blockState.with(Properties.WATERLOGGED, false);
                    }
                    world.setBlockState(pos, blockState, Block.NOTIFY_ALL | Block.MOVED);
                    world.updateNeighbor(pos, blockState.getBlock(), pos);
                }
            }
            return;
        }
        float f = blockEntity.progress + 0.5f;
        PistonBlockEntity.pushEntities(world, pos, f, blockEntity);
        PistonBlockEntity.moveEntitiesInHoneyBlock(world, pos, f, blockEntity);
        blockEntity.progress = f;
        if (blockEntity.progress >= 1.0f) {
            blockEntity.progress = 1.0f;
        }
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        RegistryWrapper.Impl<Block> registryEntryLookup = this.world != null ? this.world.createCommandRegistryWrapper(Registry.BLOCK_KEY) : Registry.BLOCK.getReadOnlyWrapper();
        this.pushedBlock = NbtHelper.toBlockState(registryEntryLookup, nbt.getCompound("blockState"));
        this.facing = Direction.byId(nbt.getInt("facing"));
        this.lastProgress = this.progress = nbt.getFloat("progress");
        this.extending = nbt.getBoolean("extending");
        this.source = nbt.getBoolean("source");
    }

    @Override
    protected void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
        nbt.putInt("facing", this.facing.getId());
        nbt.putFloat("progress", this.lastProgress);
        nbt.putBoolean("extending", this.extending);
        nbt.putBoolean("source", this.source);
    }

    public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
        VoxelShape voxelShape = !this.extending && this.source && this.pushedBlock.getBlock() instanceof PistonBlock ? ((BlockState)this.pushedBlock.with(PistonBlock.EXTENDED, true)).getCollisionShape(world, pos) : VoxelShapes.empty();
        Direction direction = field_12205.get();
        if ((double)this.progress < 1.0 && direction == this.getMovementDirection()) {
            return voxelShape;
        }
        BlockState blockState = this.isSource() ? (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, this.facing)).with(PistonHeadBlock.SHORT, this.extending != 1.0f - this.progress < 0.25f) : this.pushedBlock;
        float f = this.getAmountExtended(this.progress);
        double d = (float)this.facing.getOffsetX() * f;
        double e = (float)this.facing.getOffsetY() * f;
        double g = (float)this.facing.getOffsetZ() * f;
        return VoxelShapes.union(voxelShape, blockState.getCollisionShape(world, pos).offset(d, e, g));
    }

    public long getSavedWorldTime() {
        return this.savedWorldTime;
    }

    @Override
    public void setWorld(World world) {
        super.setWorld(world);
        if (world.createCommandRegistryWrapper(Registry.BLOCK_KEY).getOptional(this.pushedBlock.getBlock().getRegistryEntry().registryKey()).isEmpty()) {
            this.pushedBlock = Blocks.AIR.getDefaultState();
        }
    }
}

