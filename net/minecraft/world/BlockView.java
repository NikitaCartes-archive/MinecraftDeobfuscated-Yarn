/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.RayTraceContext;
import org.jetbrains.annotations.Nullable;

public interface BlockView {
    @Nullable
    public BlockEntity getBlockEntity(BlockPos var1);

    public BlockState getBlockState(BlockPos var1);

    public FluidState getFluidState(BlockPos var1);

    default public int getLuminance(BlockPos blockPos) {
        return this.getBlockState(blockPos).getLuminance();
    }

    default public int getMaxLightLevel() {
        return 15;
    }

    default public int getHeight() {
        return 256;
    }

    default public BlockHitResult rayTrace(RayTraceContext rayTraceContext2) {
        return BlockView.rayTrace(rayTraceContext2, (rayTraceContext, blockPos) -> {
            BlockState blockState = this.getBlockState((BlockPos)blockPos);
            FluidState fluidState = this.getFluidState((BlockPos)blockPos);
            Vec3d vec3d = rayTraceContext.getStart();
            Vec3d vec3d2 = rayTraceContext.getEnd();
            VoxelShape voxelShape = rayTraceContext.getBlockShape(blockState, this, (BlockPos)blockPos);
            BlockHitResult blockHitResult = this.rayTraceBlock(vec3d, vec3d2, (BlockPos)blockPos, voxelShape, blockState);
            VoxelShape voxelShape2 = rayTraceContext.getFluidShape(fluidState, this, (BlockPos)blockPos);
            BlockHitResult blockHitResult2 = voxelShape2.rayTrace(vec3d, vec3d2, (BlockPos)blockPos);
            double d = blockHitResult == null ? Double.MAX_VALUE : rayTraceContext.getStart().squaredDistanceTo(blockHitResult.getPos());
            double e = blockHitResult2 == null ? Double.MAX_VALUE : rayTraceContext.getStart().squaredDistanceTo(blockHitResult2.getPos());
            return d <= e ? blockHitResult : blockHitResult2;
        }, rayTraceContext -> {
            Vec3d vec3d = rayTraceContext.getStart().subtract(rayTraceContext.getEnd());
            return BlockHitResult.createMissed(rayTraceContext.getEnd(), Direction.getFacing(vec3d.x, vec3d.y, vec3d.z), new BlockPos(rayTraceContext.getEnd()));
        });
    }

    @Nullable
    default public BlockHitResult rayTraceBlock(Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos, VoxelShape voxelShape, BlockState blockState) {
        BlockHitResult blockHitResult2;
        BlockHitResult blockHitResult = voxelShape.rayTrace(vec3d, vec3d2, blockPos);
        if (blockHitResult != null && (blockHitResult2 = blockState.getRayTraceShape(this, blockPos).rayTrace(vec3d, vec3d2, blockPos)) != null && blockHitResult2.getPos().subtract(vec3d).lengthSquared() < blockHitResult.getPos().subtract(vec3d).lengthSquared()) {
            return blockHitResult.withSide(blockHitResult2.getSide());
        }
        return blockHitResult;
    }

    public static <T> T rayTrace(RayTraceContext rayTraceContext, BiFunction<RayTraceContext, BlockPos, T> biFunction, Function<RayTraceContext, T> function) {
        int l;
        int k;
        Vec3d vec3d2;
        Vec3d vec3d = rayTraceContext.getStart();
        if (vec3d.equals(vec3d2 = rayTraceContext.getEnd())) {
            return function.apply(rayTraceContext);
        }
        double d = MathHelper.lerp(-1.0E-7, vec3d2.x, vec3d.x);
        double e = MathHelper.lerp(-1.0E-7, vec3d2.y, vec3d.y);
        double f = MathHelper.lerp(-1.0E-7, vec3d2.z, vec3d.z);
        double g = MathHelper.lerp(-1.0E-7, vec3d.x, vec3d2.x);
        double h = MathHelper.lerp(-1.0E-7, vec3d.y, vec3d2.y);
        double i = MathHelper.lerp(-1.0E-7, vec3d.z, vec3d2.z);
        int j = MathHelper.floor(g);
        BlockPos.Mutable mutable = new BlockPos.Mutable(j, k = MathHelper.floor(h), l = MathHelper.floor(i));
        T object = biFunction.apply(rayTraceContext, mutable);
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
            if ((object2 = biFunction.apply(rayTraceContext, mutable.set(j, k, l))) == null) continue;
            return object2;
        }
        return function.apply(rayTraceContext);
    }
}

