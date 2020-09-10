/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Stream;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RaycastContext;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a scoped, read-only view of block states, fluid states and block entities.
 */
public interface BlockView {
    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    public BlockState getBlockState(BlockPos var1);

    public FluidState getFluidState(BlockPos var1);

    default public int getLuminance(BlockPos pos) {
        return this.getBlockState(pos).getLuminance();
    }

    default public int getMaxLightLevel() {
        return 15;
    }

    default public int getHeight() {
        return 256;
    }

    default public Stream<BlockState> method_29546(Box box) {
        return BlockPos.stream(box).map(this::getBlockState);
    }

    default public BlockHitResult raycast(RaycastContext context) {
        return BlockView.raycast(context, (raycastContext, blockPos) -> {
            BlockState blockState = this.getBlockState((BlockPos)blockPos);
            FluidState fluidState = this.getFluidState((BlockPos)blockPos);
            Vec3d vec3d = raycastContext.getStart();
            Vec3d vec3d2 = raycastContext.getEnd();
            VoxelShape voxelShape = raycastContext.getBlockShape(blockState, this, (BlockPos)blockPos);
            BlockHitResult blockHitResult = this.raycastBlock(vec3d, vec3d2, (BlockPos)blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = raycastContext.getFluidShape(fluidState, this, (BlockPos)blockPos);
            BlockHitResult blockHitResult2 = voxelShape2.raycast(vec3d, vec3d2, (BlockPos)blockPos);
            double d = blockHitResult == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : raycastContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, raycastContext -> {
            Vec3d vec3d = raycastContext.getStart().subtract(raycastContext.getEnd());
            return BlockHitResult.createMissed(raycastContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(raycastContext.getEnd()));
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

    public static <T> T raycast(RaycastContext raycastContext, BiFunction<RaycastContext, BlockPos, T> context, Function<RaycastContext, T> blockRaycaster) {
        int l;
        int k;
        Vec3d vec3d2;
        Vec3d vec3d = raycastContext.getStart();
        if (vec3d.equals(vec3d2 = raycastContext.getEnd())) {
            return blockRaycaster.apply(raycastContext);
        }
        double d = MathHelper.lerp(-1.0E-7, vec3d2.x, vec3d.x);
        double e = MathHelper.lerp(-1.0E-7, vec3d2.y, vec3d.y);
        double f = MathHelper.lerp(-1.0E-7, vec3d2.z, vec3d.z);
        double g = MathHelper.lerp(-1.0E-7, vec3d.x, vec3d2.x);
        double h = MathHelper.lerp(-1.0E-7, vec3d.y, vec3d2.y);
        double i = MathHelper.lerp(-1.0E-7, vec3d.z, vec3d2.z);
        int j = MathHelper.floor(g);
        BlockPos.Mutable mutable = new BlockPos.Mutable(j, k = MathHelper.floor(h), l = MathHelper.floor(i));
        T object = context.apply(raycastContext, mutable);
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
            if ((object2 = context.apply(raycastContext, mutable.set(j, k, l))) == null) continue;
            return object2;
        }
        return blockRaycaster.apply(raycastContext);
    }
}

