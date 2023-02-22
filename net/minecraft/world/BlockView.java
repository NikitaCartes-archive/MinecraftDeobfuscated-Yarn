/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockStateRaycastContext;
import net.minecraft.world.HeightLimitView;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a scoped, read-only view of block states, fluid states and block entities.
 */
public interface BlockView
extends HeightLimitView {
    /**
     * {@return the block entity at {@code pos}, or {@code null} if there is none}
     */
    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    default public <T extends BlockEntity> Optional<T> getBlockEntity(BlockPos pos, BlockEntityType<T> type) {
        BlockEntity blockEntity = this.getBlockEntity(pos);
        if (blockEntity == null || blockEntity.getType() != type) {
            return Optional.empty();
        }
        return Optional.of(blockEntity);
    }

    /**
     * {@return the block state at {@code pos}}
     * 
     * @implNote This returns the block state for {@link net.minecraft.block.Blocks#VOID_AIR}
     * if the Y coordinate is outside the height limit.
     */
    public BlockState getBlockState(BlockPos var1);

    /**
     * {@return the fluid state at {@code pos}}
     * 
     * @implNote This returns the fluid state for {@link net.minecraft.fluid.Fluids#EMPTY}
     * if the Y coordinate is outside the height limit.
     */
    public FluidState getFluidState(BlockPos var1);

    default public int getLuminance(BlockPos pos) {
        return this.getBlockState(pos).getLuminance();
    }

    default public int getMaxLightLevel() {
        return 15;
    }

    default public Stream<BlockState> getStatesInBox(Box box) {
        return BlockPos.stream(box).map(this::getBlockState);
    }

    default public BlockHitResult raycast(BlockStateRaycastContext context2) {
        return BlockView.raycast(context2.getStart(), context2.getEnd(), context2, (context, pos) -> {
            BlockState blockState = this.getBlockState((BlockPos)pos);
            Vec3d vec3d = context.getStart().subtract(context.getEnd());
            return context.getStatePredicate().test(blockState) ? new BlockHitResult(context.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(context.getEnd()), false) : null;
        }, context -> {
            Vec3d vec3d = context.getStart().subtract(context.getEnd());
            return BlockHitResult.createMissed(context.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(context.getEnd()));
        });
    }

    default public BlockHitResult raycast(RaycastContext context2) {
        return BlockView.raycast(context2.getStart(), context2.getEnd(), context2, (context, pos) -> {
            BlockState blockState = this.getBlockState((BlockPos)pos);
            FluidState fluidState = this.getFluidState((BlockPos)pos);
            Vec3d vec3d = context.getStart();
            Vec3d vec3d2 = context.getEnd();
            VoxelShape voxelShape = context.getBlockShape(blockState, this, (BlockPos)pos);
            BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, (BlockPos)pos, voxelShape, blockState);
            VoxelShape voxelShape2 = context.getFluidShape(fluidState, this, (BlockPos)pos);
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, (BlockPos)pos);
            double d = blockHitResult == null ? Double.MAX_VALUE : context.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : context.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, context -> {
            Vec3d vec3d = context.getStart().subtract(context.getEnd());
            return BlockHitResult.createMissed(context.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), BlockPos.ofFloored(context.getEnd()));
        });
    }

    @Nullable
    default public BlockHitResult raycastBlock(Vec3d start, Vec3d end, BlockPos pos, VoxelShape shape, BlockState state) {
        BlockHitResult blockHitResult2;
        BlockHitResult blockHitResult = shape.raycast(start, end, pos);
        if (blockHitResult != null && (blockHitResult2 = state.getRaycastShape(this, pos).raycast(start, end, pos)) != null && blockHitResult2.getPos().subtract(start).lengthSquared() < blockHitResult.getPos().subtract(start).lengthSquared()) {
            return blockHitResult.withSide(blockHitResult2.getSide());
        }
        return blockHitResult;
    }

    default public double getDismountHeight(VoxelShape blockCollisionShape, Supplier<VoxelShape> belowBlockCollisionShapeGetter) {
        if (!blockCollisionShape.isEmpty()) {
            return blockCollisionShape.getMax(Direction.Axis.Y);
        }
        double d = belowBlockCollisionShapeGetter.get().getMax(Direction.Axis.Y);
        if (d >= 1.0) {
            return d - 1.0;
        }
        return Double.NEGATIVE_INFINITY;
    }

    default public double getDismountHeight(BlockPos pos) {
        return this.getDismountHeight(this.getBlockState(pos).getCollisionShape(this, pos), () -> {
            BlockPos blockPos2 = pos.down();
            return this.getBlockState(blockPos2).getCollisionShape(this, blockPos2);
        });
    }

    public static <T, C> T raycast(Vec3d start, Vec3d end, C context, BiFunction<C, BlockPos, T> blockHitFactory, Function<C, T> missFactory) {
        int l;
        int k;
        if (start.equals(end)) {
            return missFactory.apply(context);
        }
        double d = MathHelper.lerp(-1.0E-7, end.x, start.x);
        double e = MathHelper.lerp(-1.0E-7, end.y, start.y);
        double f = MathHelper.lerp(-1.0E-7, end.z, start.z);
        double g = MathHelper.lerp(-1.0E-7, start.x, end.x);
        double h = MathHelper.lerp(-1.0E-7, start.y, end.y);
        double i = MathHelper.lerp(-1.0E-7, start.z, end.z);
        int j = MathHelper.floor(g);
        BlockPos.Mutable mutable = new BlockPos.Mutable(j, k = MathHelper.floor(h), l = MathHelper.floor(i));
        T object = blockHitFactory.apply(context, mutable);
        if (object != null) {
            return object;
        }
        double m = d - g;
        double n = e - h;
        double o = f - i;
        int p = MathHelper.sign(m);
        int q = MathHelper.sign(n);
        int r = MathHelper.sign(o);
        double s = p == 0 ? Double.MAX_VALUE : (double)p / m;
        double t = q == 0 ? Double.MAX_VALUE : (double)q / n;
        double u = r == 0 ? Double.MAX_VALUE : (double)r / o;
        double v = s * (p > 0 ? 1.0 - MathHelper.fractionalPart(g) : MathHelper.fractionalPart(g));
        double w = t * (q > 0 ? 1.0 - MathHelper.fractionalPart(h) : MathHelper.fractionalPart(h));
        double x = u * (r > 0 ? 1.0 - MathHelper.fractionalPart(i) : MathHelper.fractionalPart(i));
        while (v <= 1.0 || w <= 1.0 || x <= 1.0) {
            T object2;
            if (v < w) {
                if (v < x) {
                    j += p;
                    v += s;
                } else {
                    l += r;
                    x += u;
                }
            } else if (w < x) {
                k += q;
                w += t;
            } else {
                l += r;
                x += u;
            }
            if ((object2 = blockHitFactory.apply(context, mutable.set(j, k, l))) == null) continue;
            return object2;
        }
        return missFactory.apply(context);
    }
}

