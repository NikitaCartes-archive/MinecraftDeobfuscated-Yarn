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
import net.minecraft.class_4623;
import net.minecraft.entity.Entity;
import net.minecraft.entity.MovementType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtHelper;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class PistonBlockEntity
extends BlockEntity
implements Tickable {
    private BlockState pushedBlock;
    private Direction facing;
    private boolean extending;
    private boolean source;
    private static final ThreadLocal<Direction> field_12205 = ThreadLocal.withInitial(() -> null);
    private float progress;
    private float lastProgress;
    private long savedWorldTime;

    public PistonBlockEntity() {
        super(BlockEntityType.PISTON);
    }

    public PistonBlockEntity(BlockState blockState, Direction direction, boolean bl, boolean bl2) {
        this();
        this.pushedBlock = blockState;
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

    public float getProgress(float f) {
        if (f > 1.0f) {
            f = 1.0f;
        }
        return MathHelper.lerp(f, this.lastProgress, this.progress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetX(float f) {
        return (float)this.facing.getOffsetX() * this.getAmountExtended(this.getProgress(f));
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetY(float f) {
        return (float)this.facing.getOffsetY() * this.getAmountExtended(this.getProgress(f));
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetZ(float f) {
        return (float)this.facing.getOffsetZ() * this.getAmountExtended(this.getProgress(f));
    }

    private float getAmountExtended(float f) {
        return this.extending ? f - 1.0f : 1.0f - f;
    }

    private BlockState getHeadBlockState() {
        if (!this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock) {
            return (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT)).with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING));
        }
        return this.pushedBlock;
    }

    private void pushEntities(float f) {
        Direction direction = this.getMovementDirection();
        double d = f - this.progress;
        VoxelShape voxelShape = this.getHeadBlockState().getCollisionShape(this.world, this.getPos());
        if (voxelShape.isEmpty()) {
            return;
        }
        List<Box> list = voxelShape.getBoundingBoxes();
        Box box = this.offsetHeadBox(this.getApproximateHeadBox(list));
        List<Entity> list2 = this.world.getEntities(null, class_4623.method_23362(box, direction, d).union(box));
        if (list2.isEmpty()) {
            return;
        }
        boolean bl = this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK;
        for (Entity entity : list2) {
            Box box4;
            Box box2;
            Box box3;
            if (entity.getPistonBehavior() == PistonBehavior.IGNORE) continue;
            if (bl) {
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
            Iterator<Box> iterator = list.iterator();
            while (!(!iterator.hasNext() || (box3 = class_4623.method_23362(this.offsetHeadBox(box2 = iterator.next()), direction, d)).intersects(box4 = entity.getBoundingBox()) && (i = Math.max(i, PistonBlockEntity.getIntersectionSize(box3, direction, box4))) >= d)) {
            }
            if (i <= 0.0) continue;
            i = Math.min(i, d) + 0.01;
            PistonBlockEntity.method_23672(direction, entity, i, direction);
            if (this.extending || !this.source) continue;
            this.push(entity, direction, d);
        }
    }

    private static void method_23672(Direction direction, Entity entity, double d, Direction direction2) {
        field_12205.set(direction);
        entity.move(MovementType.PISTON, new Vec3d(d * (double)direction2.getOffsetX(), d * (double)direction2.getOffsetY(), d * (double)direction2.getOffsetZ()));
        field_12205.set(null);
    }

    private void method_23674(float f) {
        if (!this.method_23364()) {
            return;
        }
        Direction direction = this.getMovementDirection();
        if (!direction.getAxis().isHorizontal()) {
            return;
        }
        double d = this.pushedBlock.getCollisionShape(this.world, this.pos).getMaximum(Direction.Axis.Y);
        Box box = this.offsetHeadBox(new Box(0.0, d, 0.0, 1.0, 1.5000000999999998, 1.0));
        double e = f - this.progress;
        List<Entity> list = this.world.getEntities((Entity)null, box, entity -> PistonBlockEntity.method_23671(box, entity));
        for (Entity entity2 : list) {
            PistonBlockEntity.method_23672(direction, entity2, e, direction);
        }
    }

    private static boolean method_23671(Box box, Entity entity) {
        return entity.getPistonBehavior() == PistonBehavior.NORMAL && entity.onGround && entity.getX() >= box.minX && entity.getX() <= box.maxX && entity.getZ() >= box.minZ && entity.getZ() <= box.maxZ;
    }

    private boolean method_23364() {
        return this.pushedBlock.getBlock() == Blocks.HONEY_BLOCK;
    }

    public Direction getMovementDirection() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }

    private Box getApproximateHeadBox(List<Box> list) {
        double d = 0.0;
        double e = 0.0;
        double f = 0.0;
        double g = 1.0;
        double h = 1.0;
        double i = 1.0;
        for (Box box : list) {
            d = Math.min(box.minX, d);
            e = Math.min(box.minY, e);
            f = Math.min(box.minZ, f);
            g = Math.max(box.maxX, g);
            h = Math.max(box.maxY, h);
            i = Math.max(box.maxZ, i);
        }
        return new Box(d, e, f, g, h, i);
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

    private Box offsetHeadBox(Box box) {
        double d = this.getAmountExtended(this.progress);
        return box.offset((double)this.pos.getX() + d * (double)this.facing.getOffsetX(), (double)this.pos.getY() + d * (double)this.facing.getOffsetY(), (double)this.pos.getZ() + d * (double)this.facing.getOffsetZ());
    }

    private void push(Entity entity, Direction direction, double d) {
        double f;
        Direction direction2;
        double e;
        Box box2;
        Box box = entity.getBoundingBox();
        if (box.intersects(box2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos)) && Math.abs((e = PistonBlockEntity.getIntersectionSize(box2, direction2 = direction.getOpposite(), box) + 0.01) - (f = PistonBlockEntity.getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01)) < 0.01) {
            e = Math.min(e, d) + 0.01;
            PistonBlockEntity.method_23672(direction, entity, e, direction2);
        }
    }

    public BlockState getPushedBlock() {
        return this.pushedBlock;
    }

    public void finish() {
        if (this.lastProgress < 1.0f && this.world != null) {
            this.lastProgress = this.progress = 1.0f;
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
                BlockState blockState = this.source ? Blocks.AIR.getDefaultState() : Block.getRenderingState(this.pushedBlock, this.world, this.pos);
                this.world.setBlockState(this.pos, blockState, 3);
                this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
            }
        }
    }

    @Override
    public void tick() {
        this.savedWorldTime = this.world.getTime();
        this.lastProgress = this.progress;
        if (this.lastProgress >= 1.0f) {
            this.world.removeBlockEntity(this.pos);
            this.markRemoved();
            if (this.pushedBlock != null && this.world.getBlockState(this.pos).getBlock() == Blocks.MOVING_PISTON) {
                BlockState blockState = Block.getRenderingState(this.pushedBlock, this.world, this.pos);
                if (blockState.isAir()) {
                    this.world.setBlockState(this.pos, this.pushedBlock, 84);
                    Block.replaceBlock(this.pushedBlock, blockState, this.world, this.pos, 3);
                } else {
                    if (blockState.contains(Properties.WATERLOGGED) && blockState.get(Properties.WATERLOGGED).booleanValue()) {
                        blockState = (BlockState)blockState.with(Properties.WATERLOGGED, false);
                    }
                    this.world.setBlockState(this.pos, blockState, 67);
                    this.world.updateNeighbor(this.pos, blockState.getBlock(), this.pos);
                }
            }
            return;
        }
        float f = this.progress + 0.5f;
        this.pushEntities(f);
        this.method_23674(f);
        this.progress = f;
        if (this.progress >= 1.0f) {
            this.progress = 1.0f;
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
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.put("blockState", NbtHelper.fromBlockState(this.pushedBlock));
        compoundTag.putInt("facing", this.facing.getId());
        compoundTag.putFloat("progress", this.lastProgress);
        compoundTag.putBoolean("extending", this.extending);
        compoundTag.putBoolean("source", this.source);
        return compoundTag;
    }

    public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
        VoxelShape voxelShape = !this.extending && this.source ? ((BlockState)this.pushedBlock.with(PistonBlock.EXTENDED, true)).getCollisionShape(blockView, blockPos) : VoxelShapes.empty();
        Direction direction = field_12205.get();
        if ((double)this.progress < 1.0 && direction == this.getMovementDirection()) {
            return voxelShape;
        }
        BlockState blockState = this.isSource() ? (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, this.facing)).with(PistonHeadBlock.SHORT, this.extending != 1.0f - this.progress < 4.0f) : this.pushedBlock;
        float f = this.getAmountExtended(this.progress);
        double d = (float)this.facing.getOffsetX() * f;
        double e = (float)this.facing.getOffsetY() * f;
        double g = (float)this.facing.getOffsetZ() * f;
        return VoxelShapes.union(voxelShape, blockState.getCollisionShape(blockView, blockPos).offset(d, e, g));
    }

    public long getSavedWorldTime() {
        return this.savedWorldTime;
    }
}

