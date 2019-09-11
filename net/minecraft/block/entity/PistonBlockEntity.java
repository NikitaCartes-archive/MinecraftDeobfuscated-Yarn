/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.block.entity;

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
import net.minecraft.state.property.Properties;
import net.minecraft.util.TagHelper;
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
        List<Entity> list2 = this.world.getEntities(null, this.extendBox(box, direction, d).union(box));
        if (list2.isEmpty()) {
            return;
        }
        boolean bl = this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK;
        for (int i = 0; i < list2.size(); ++i) {
            Box box3;
            Box box2;
            Entity entity = list2.get(i);
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
            double j = 0.0;
            for (int k = 0; !(k >= list.size() || (box2 = this.extendBox(this.offsetHeadBox(list.get(k)), direction, d)).intersects(box3 = entity.getBoundingBox()) && (j = Math.max(j, this.getIntersectionSize(box2, direction, box3))) >= d); ++k) {
            }
            if (j <= 0.0) continue;
            j = Math.min(j, d) + 0.01;
            field_12205.set(direction);
            entity.move(MovementType.PISTON, new Vec3d(j * (double)direction.getOffsetX(), j * (double)direction.getOffsetY(), j * (double)direction.getOffsetZ()));
            field_12205.set(null);
            if (this.extending || !this.source) continue;
            this.push(entity, direction, d);
        }
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

    private double getIntersectionSize(Box box, Direction direction, Box box2) {
        switch (direction.getAxis()) {
            case X: {
                return PistonBlockEntity.getXIntersectionSize(box, direction, box2);
            }
            default: {
                return PistonBlockEntity.getYIntersectionSize(box, direction, box2);
            }
            case Z: 
        }
        return PistonBlockEntity.getZIntersectionSize(box, direction, box2);
    }

    private Box offsetHeadBox(Box box) {
        double d = this.getAmountExtended(this.progress);
        return box.offset((double)this.pos.getX() + d * (double)this.facing.getOffsetX(), (double)this.pos.getY() + d * (double)this.facing.getOffsetY(), (double)this.pos.getZ() + d * (double)this.facing.getOffsetZ());
    }

    private Box extendBox(Box box, Direction direction, double d) {
        double e = d * (double)direction.getDirection().offset();
        double f = Math.min(e, 0.0);
        double g = Math.max(e, 0.0);
        switch (direction) {
            case WEST: {
                return new Box(box.minX + f, box.minY, box.minZ, box.minX + g, box.maxY, box.maxZ);
            }
            case EAST: {
                return new Box(box.maxX + f, box.minY, box.minZ, box.maxX + g, box.maxY, box.maxZ);
            }
            case DOWN: {
                return new Box(box.minX, box.minY + f, box.minZ, box.maxX, box.minY + g, box.maxZ);
            }
            default: {
                return new Box(box.minX, box.maxY + f, box.minZ, box.maxX, box.maxY + g, box.maxZ);
            }
            case NORTH: {
                return new Box(box.minX, box.minY, box.minZ + f, box.maxX, box.maxY, box.minZ + g);
            }
            case SOUTH: 
        }
        return new Box(box.minX, box.minY, box.maxZ + f, box.maxX, box.maxY, box.maxZ + g);
    }

    private void push(Entity entity, Direction direction, double d) {
        double f;
        Direction direction2;
        double e;
        Box box2;
        Box box = entity.getBoundingBox();
        if (box.intersects(box2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos)) && Math.abs((e = this.getIntersectionSize(box2, direction2 = direction.getOpposite(), box) + 0.01) - (f = this.getIntersectionSize(box2, direction2, box.intersection(box2)) + 0.01)) < 0.01) {
            e = Math.min(e, d) + 0.01;
            field_12205.set(direction);
            entity.move(MovementType.PISTON, new Vec3d(e * (double)direction2.getOffsetX(), e * (double)direction2.getOffsetY(), e * (double)direction2.getOffsetZ()));
            field_12205.set(null);
        }
    }

    private static double getXIntersectionSize(Box box, Direction direction, Box box2) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return box.maxX - box2.minX;
        }
        return box2.maxX - box.minX;
    }

    private static double getYIntersectionSize(Box box, Direction direction, Box box2) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return box.maxY - box2.minY;
        }
        return box2.maxY - box.minY;
    }

    private static double getZIntersectionSize(Box box, Direction direction, Box box2) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return box.maxZ - box2.minZ;
        }
        return box2.maxZ - box.minZ;
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
        this.progress = f;
        if (this.progress >= 1.0f) {
            this.progress = 1.0f;
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.pushedBlock = TagHelper.deserializeBlockState(compoundTag.getCompound("blockState"));
        this.facing = Direction.byId(compoundTag.getInt("facing"));
        this.lastProgress = this.progress = compoundTag.getFloat("progress");
        this.extending = compoundTag.getBoolean("extending");
        this.source = compoundTag.getBoolean("source");
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.put("blockState", TagHelper.serializeBlockState(this.pushedBlock));
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

