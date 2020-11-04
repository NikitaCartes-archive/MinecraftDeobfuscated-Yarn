/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

import java.util.Iterator;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
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
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Boxes;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

/**
 * A piston block entity represents the block being pushed by a piston.
 */
public class PistonBlockEntity
extends BlockEntity {
    private BlockState pushedBlock;
    private Direction facing;
    private boolean extending;
    private boolean source;
    private static final ThreadLocal<Direction> field_12205 = ThreadLocal.withInitial(() -> null);
    private float progress;
    private float lastProgress;
    private long savedWorldTime;
    private int field_26705;

    public PistonBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(BlockEntityType.PISTON, blockPos, blockState);
    }

    public PistonBlockEntity(BlockPos blockPos, BlockState blockState, BlockState blockState2, Direction direction, boolean bl, boolean bl2) {
        this(blockPos, blockState);
        this.pushedBlock = blockState2;
        this.facing = direction;
        this.extending = bl;
        this.source = bl2;
    }

    @Override
    public CompoundTag toInitialChunkDataTag() {
        return this.toTag(new CompoundTag());
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

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetX(float tickDelta) {
        return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetY(float tickDelta) {
        return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(tickDelta));
    }

    @Environment(value=EnvType.CLIENT)
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

    private static void pushEntities(World world, BlockPos blockPos, float f, PistonBlockEntity pistonBlockEntity) {
        Direction direction = pistonBlockEntity.getMovementDirection();
        double d = f - pistonBlockEntity.progress;
        VoxelShape voxelShape = pistonBlockEntity.getHeadBlockState().getCollisionShape(world, blockPos);
        if (voxelShape.isEmpty()) {
            return;
        }
        Box box = PistonBlockEntity.offsetHeadBox(blockPos, voxelShape.getBoundingBox(), pistonBlockEntity);
        List<Entity> list = world.getOtherEntities(null, Boxes.stretch(box, direction, d).union(box));
        if (list.isEmpty()) {
            return;
        }
        List<Box> list2 = voxelShape.getBoundingBoxes();
        boolean bl = pistonBlockEntity.pushedBlock.isOf(Blocks.SLIME_BLOCK);
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
            while (!(!iterator.hasNext() || (box3 = Boxes.stretch(PistonBlockEntity.offsetHeadBox(blockPos, box2 = iterator.next(), pistonBlockEntity), direction, d)).intersects(box4 = entity.getBoundingBox()) && (i = Math.max(i, PistonBlockEntity.getIntersectionSize(box3, direction, box4))) >= d)) {
            }
            if (i <= 0.0) continue;
            i = Math.min(i, d) + 0.01;
            PistonBlockEntity.method_23672(direction, entity, i, direction);
            if (pistonBlockEntity.extending || !pistonBlockEntity.source) continue;
            PistonBlockEntity.push(blockPos, entity, direction, d);
        }
    }

    private static void method_23672(Direction direction, Entity entity, double d, Direction direction2) {
        field_12205.set(direction);
        entity.move(MovementType.PISTON, new Vec3d(d * (double)direction2.getOffsetX(), d * (double)direction2.getOffsetY(), d * (double)direction2.getOffsetZ()));
        field_12205.set(null);
    }

    private static void method_23674(World world, BlockPos blockPos, float f, PistonBlockEntity pistonBlockEntity) {
        if (!pistonBlockEntity.isPushingHoneyBlock()) {
            return;
        }
        Direction direction = pistonBlockEntity.getMovementDirection();
        if (!direction.getAxis().isHorizontal()) {
            return;
        }
        double d = pistonBlockEntity.pushedBlock.getCollisionShape(world, blockPos).getMax(Direction.Axis.Y);
        Box box = PistonBlockEntity.offsetHeadBox(blockPos, new Box(0.0, d, 0.0, 1.0, 1.5000000999999998, 1.0), pistonBlockEntity);
        double e = f - pistonBlockEntity.progress;
        List<Entity> list = world.getOtherEntities(null, box, entity -> PistonBlockEntity.method_23671(box, entity));
        for (Entity entity2 : list) {
            PistonBlockEntity.method_23672(direction, entity2, e, direction);
        }
    }

    private static boolean method_23671(Box box, Entity entity) {
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

    private static Box offsetHeadBox(BlockPos blockPos, Box box, PistonBlockEntity pistonBlockEntity) {
        double d = pistonBlockEntity.getAmountExtended(pistonBlockEntity.progress);
        return box.offset((double)blockPos.getX() + d * (double)pistonBlockEntity.facing.getOffsetX(), (double)blockPos.getY() + d * (double)pistonBlockEntity.facing.getOffsetY(), (double)blockPos.getZ() + d * (double)pistonBlockEntity.facing.getOffsetZ());
    }

    private static void push(BlockPos blockPos, Entity entity, Direction direction, double amount) {
        double e;
        Direction direction2;
        double d;
        Box box2;
        Box box = entity.getBoundingBox();
        if (box.intersects(box2 = VoxelShapes.fullCube().getBoundingBox().offset(blockPos)) && Math.abs((d = PistonBlockEntity.getIntersectionSize(box2, direction2 = direction.getOpposite(), box) + 0.01) - (e = PistonBlockEntity.getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01)) < 0.01) {
            d = Math.min(d, amount) + 0.01;
            PistonBlockEntity.method_23672(direction, entity, d, direction2);
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
                this.world.setBlockState(this.pos, blockState, 3);
                this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
            }
        }
    }

    public static void tick(World world, BlockPos blockPos, BlockState blockState, PistonBlockEntity pistonBlockEntity) {
        pistonBlockEntity.savedWorldTime = world.getTime();
        pistonBlockEntity.lastProgress = pistonBlockEntity.progress;
        if (pistonBlockEntity.lastProgress >= 1.0f) {
            if (world.isClient && pistonBlockEntity.field_26705 < 5) {
                ++pistonBlockEntity.field_26705;
                return;
            }
            world.removeBlockEntity(blockPos);
            pistonBlockEntity.markRemoved();
            if (pistonBlockEntity.pushedBlock != null && world.getBlockState(blockPos).isOf(Blocks.MOVING_PISTON)) {
                BlockState blockState2 = Block.postProcessState(pistonBlockEntity.pushedBlock, world, blockPos);
                if (blockState2.isAir()) {
                    world.setBlockState(blockPos, pistonBlockEntity.pushedBlock, 84);
                    Block.replace(pistonBlockEntity.pushedBlock, blockState2, world, blockPos, 3);
                } else {
                    if (blockState2.contains(Properties.WATERLOGGED) && blockState2.get(Properties.WATERLOGGED).booleanValue()) {
                        blockState2 = (BlockState)blockState2.with(Properties.WATERLOGGED, false);
                    }
                    world.setBlockState(blockPos, blockState2, 67);
                    world.updateNeighbor(blockPos, blockState2.getBlock(), blockPos);
                }
            }
            return;
        }
        float f = pistonBlockEntity.progress + 0.5f;
        PistonBlockEntity.pushEntities(world, blockPos, f, pistonBlockEntity);
        PistonBlockEntity.method_23674(world, blockPos, f, pistonBlockEntity);
        pistonBlockEntity.progress = f;
        if (pistonBlockEntity.progress >= 1.0f) {
            pistonBlockEntity.progress = 1.0f;
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.pushedBlock = NbtHelper.toBlockState(compoundTag.getCompound("blockState"));
        this.facing = Direction.byId(compoundTag.getInt("facing"));
        this.lastProgress = this.progress = compoundTag.getFloat("progress");
        this.extending = compoundTag.getBoolean("extending");
        this.source = compoundTag.getBoolean("source");
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        super.toTag(tag);
        tag.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
        tag.putInt("facing", this.facing.getId());
        tag.putFloat("progress", this.lastProgress);
        tag.putBoolean("extending", this.extending);
        tag.putBoolean("source", this.source);
        return tag;
    }

    public VoxelShape getCollisionShape(BlockView world, BlockPos pos) {
        VoxelShape voxelShape = !this.extending && this.source ? ((BlockState)this.pushedBlock.with(PistonBlock.EXTENDED, true)).getCollisionShape(world, pos) : VoxelShapes.empty();
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
    @Environment(value=EnvType.CLIENT)
    public double getSquaredRenderDistance() {
        return 68.0;
    }
}

