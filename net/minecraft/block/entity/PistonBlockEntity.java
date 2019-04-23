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
import net.minecraft.util.math.BoundingBox;
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
    private static final ThreadLocal<Direction> field_12205 = new ThreadLocal<Direction>(){

        protected Direction method_11516() {
            return null;
        }

        @Override
        protected /* synthetic */ Object initialValue() {
            return this.method_11516();
        }
    };
    private float nextProgress;
    private float progress;
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
        return MathHelper.lerp(f, this.progress, this.nextProgress);
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetX(float f) {
        return (float)this.facing.getOffsetX() * this.method_11504(this.getProgress(f));
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetY(float f) {
        return (float)this.facing.getOffsetY() * this.method_11504(this.getProgress(f));
    }

    @Environment(value=EnvType.CLIENT)
    public float getRenderOffsetZ(float f) {
        return (float)this.facing.getOffsetZ() * this.method_11504(this.getProgress(f));
    }

    private float method_11504(float f) {
        return this.extending ? f - 1.0f : 1.0f - f;
    }

    private BlockState method_11496() {
        if (!this.isExtending() && this.isSource() && this.pushedBlock.getBlock() instanceof PistonBlock) {
            return (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.TYPE, this.pushedBlock.getBlock() == Blocks.STICKY_PISTON ? PistonType.STICKY : PistonType.DEFAULT)).with(PistonHeadBlock.FACING, this.pushedBlock.get(PistonBlock.FACING));
        }
        return this.pushedBlock;
    }

    private void method_11503(float f) {
        Direction direction = this.method_11506();
        double d = f - this.nextProgress;
        VoxelShape voxelShape = this.method_11496().getCollisionShape(this.world, this.getPos());
        if (voxelShape.isEmpty()) {
            return;
        }
        List<BoundingBox> list = voxelShape.getBoundingBoxes();
        BoundingBox boundingBox = this.method_11500(this.method_11509(list));
        List<Entity> list2 = this.world.getEntities((Entity)null, this.method_11502(boundingBox, direction, d).union(boundingBox));
        if (list2.isEmpty()) {
            return;
        }
        boolean bl = this.pushedBlock.getBlock() == Blocks.SLIME_BLOCK;
        for (int i = 0; i < list2.size(); ++i) {
            BoundingBox boundingBox3;
            BoundingBox boundingBox2;
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
            for (int k = 0; !(k >= list.size() || (boundingBox2 = this.method_11502(this.method_11500(list.get(k)), direction, d)).intersects(boundingBox3 = entity.getBoundingBox()) && (j = Math.max(j, this.method_11497(boundingBox2, direction, boundingBox3))) >= d); ++k) {
            }
            if (j <= 0.0) continue;
            j = Math.min(j, d) + 0.01;
            field_12205.set(direction);
            entity.move(MovementType.PISTON, new Vec3d(j * (double)direction.getOffsetX(), j * (double)direction.getOffsetY(), j * (double)direction.getOffsetZ()));
            field_12205.set(null);
            if (this.extending || !this.source) continue;
            this.method_11514(entity, direction, d);
        }
    }

    public Direction method_11506() {
        return this.extending ? this.facing : this.facing.getOpposite();
    }

    private BoundingBox method_11509(List<BoundingBox> list) {
        double d = 0.0;
        double e = 0.0;
        double f = 0.0;
        double g = 1.0;
        double h = 1.0;
        double i = 1.0;
        for (BoundingBox boundingBox : list) {
            d = Math.min(boundingBox.minX, d);
            e = Math.min(boundingBox.minY, e);
            f = Math.min(boundingBox.minZ, f);
            g = Math.max(boundingBox.maxX, g);
            h = Math.max(boundingBox.maxY, h);
            i = Math.max(boundingBox.maxZ, i);
        }
        return new BoundingBox(d, e, f, g, h, i);
    }

    private double method_11497(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
        switch (direction.getAxis()) {
            case X: {
                return PistonBlockEntity.method_11493(boundingBox, direction, boundingBox2);
            }
            default: {
                return PistonBlockEntity.method_11510(boundingBox, direction, boundingBox2);
            }
            case Z: 
        }
        return PistonBlockEntity.method_11505(boundingBox, direction, boundingBox2);
    }

    private BoundingBox method_11500(BoundingBox boundingBox) {
        double d = this.method_11504(this.nextProgress);
        return boundingBox.offset((double)this.pos.getX() + d * (double)this.facing.getOffsetX(), (double)this.pos.getY() + d * (double)this.facing.getOffsetY(), (double)this.pos.getZ() + d * (double)this.facing.getOffsetZ());
    }

    private BoundingBox method_11502(BoundingBox boundingBox, Direction direction, double d) {
        double e = d * (double)direction.getDirection().offset();
        double f = Math.min(e, 0.0);
        double g = Math.max(e, 0.0);
        switch (direction) {
            case WEST: {
                return new BoundingBox(boundingBox.minX + f, boundingBox.minY, boundingBox.minZ, boundingBox.minX + g, boundingBox.maxY, boundingBox.maxZ);
            }
            case EAST: {
                return new BoundingBox(boundingBox.maxX + f, boundingBox.minY, boundingBox.minZ, boundingBox.maxX + g, boundingBox.maxY, boundingBox.maxZ);
            }
            case DOWN: {
                return new BoundingBox(boundingBox.minX, boundingBox.minY + f, boundingBox.minZ, boundingBox.maxX, boundingBox.minY + g, boundingBox.maxZ);
            }
            default: {
                return new BoundingBox(boundingBox.minX, boundingBox.maxY + f, boundingBox.minZ, boundingBox.maxX, boundingBox.maxY + g, boundingBox.maxZ);
            }
            case NORTH: {
                return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.minZ + f, boundingBox.maxX, boundingBox.maxY, boundingBox.minZ + g);
            }
            case SOUTH: 
        }
        return new BoundingBox(boundingBox.minX, boundingBox.minY, boundingBox.maxZ + f, boundingBox.maxX, boundingBox.maxY, boundingBox.maxZ + g);
    }

    private void method_11514(Entity entity, Direction direction, double d) {
        double f;
        Direction direction2;
        double e;
        BoundingBox boundingBox2;
        BoundingBox boundingBox = entity.getBoundingBox();
        if (boundingBox.intersects(boundingBox2 = VoxelShapes.fullCube().getBoundingBox().offset(this.pos)) && Math.abs((e = this.method_11497(boundingBox2, direction2 = direction.getOpposite(), boundingBox) + 0.01) - (f = this.method_11497(boundingBox2, direction2, boundingBox.intersection(boundingBox2)) + 0.01)) < 0.01) {
            e = Math.min(e, d) + 0.01;
            field_12205.set(direction);
            entity.move(MovementType.PISTON, new Vec3d(e * (double)direction2.getOffsetX(), e * (double)direction2.getOffsetY(), e * (double)direction2.getOffsetZ()));
            field_12205.set(null);
        }
    }

    private static double method_11493(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return boundingBox.maxX - boundingBox2.minX;
        }
        return boundingBox2.maxX - boundingBox.minX;
    }

    private static double method_11510(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return boundingBox.maxY - boundingBox2.minY;
        }
        return boundingBox2.maxY - boundingBox.minY;
    }

    private static double method_11505(BoundingBox boundingBox, Direction direction, BoundingBox boundingBox2) {
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            return boundingBox.maxZ - boundingBox2.minZ;
        }
        return boundingBox2.maxZ - boundingBox.minZ;
    }

    public BlockState getPushedBlock() {
        return this.pushedBlock;
    }

    public void finish() {
        if (this.progress < 1.0f && this.world != null) {
            this.progress = this.nextProgress = 1.0f;
            this.world.removeBlockEntity(this.pos);
            this.invalidate();
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
        this.progress = this.nextProgress;
        if (this.progress >= 1.0f) {
            this.world.removeBlockEntity(this.pos);
            this.invalidate();
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
        float f = this.nextProgress + 0.5f;
        this.method_11503(f);
        this.nextProgress = f;
        if (this.nextProgress >= 1.0f) {
            this.nextProgress = 1.0f;
        }
    }

    @Override
    public void fromTag(CompoundTag compoundTag) {
        super.fromTag(compoundTag);
        this.pushedBlock = TagHelper.deserializeBlockState(compoundTag.getCompound("blockState"));
        this.facing = Direction.byId(compoundTag.getInt("facing"));
        this.progress = this.nextProgress = compoundTag.getFloat("progress");
        this.extending = compoundTag.getBoolean("extending");
        this.source = compoundTag.getBoolean("source");
    }

    @Override
    public CompoundTag toTag(CompoundTag compoundTag) {
        super.toTag(compoundTag);
        compoundTag.put("blockState", TagHelper.serializeBlockState(this.pushedBlock));
        compoundTag.putInt("facing", this.facing.getId());
        compoundTag.putFloat("progress", this.progress);
        compoundTag.putBoolean("extending", this.extending);
        compoundTag.putBoolean("source", this.source);
        return compoundTag;
    }

    public VoxelShape getCollisionShape(BlockView blockView, BlockPos blockPos) {
        VoxelShape voxelShape = !this.extending && this.source ? ((BlockState)this.pushedBlock.with(PistonBlock.EXTENDED, true)).getCollisionShape(blockView, blockPos) : VoxelShapes.empty();
        Direction direction = field_12205.get();
        if ((double)this.nextProgress < 1.0 && direction == this.method_11506()) {
            return voxelShape;
        }
        BlockState blockState = this.isSource() ? (BlockState)((BlockState)Blocks.PISTON_HEAD.getDefaultState().with(PistonHeadBlock.FACING, this.facing)).with(PistonHeadBlock.SHORT, this.extending != 1.0f - this.nextProgress < 4.0f) : this.pushedBlock;
        float f = this.method_11504(this.nextProgress);
        double d = (float)this.facing.getOffsetX() * f;
        double e = (float)this.facing.getOffsetY() * f;
        double g = (float)this.facing.getOffsetZ() * f;
        return VoxelShapes.union(voxelShape, blockState.getCollisionShape(blockView, blockPos).offset(d, e, g));
    }

    public long getSavedWorldTime() {
        return this.savedWorldTime;
    }
}

