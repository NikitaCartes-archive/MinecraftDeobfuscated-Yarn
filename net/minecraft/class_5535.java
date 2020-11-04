/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.google.common.annotations.VisibleForTesting;
import java.util.Random;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

public class class_5535 {
    public static BlockPos method_31541(Random random, int i, int j) {
        int k = random.nextInt(2 * i + 1) - i;
        int l = random.nextInt(2 * j + 1) - j;
        int m = random.nextInt(2 * i + 1) - i;
        return new BlockPos(k, l, m);
    }

    @Nullable
    public static BlockPos method_31542(Random random, int i, int j, int k, double d, double e, double f) {
        double g = MathHelper.atan2(e, d) - 1.5707963705062866;
        double h = g + (double)(2.0f * random.nextFloat() - 1.0f) * f;
        double l = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)i;
        double m = -l * Math.sin(h);
        double n = l * Math.cos(h);
        if (Math.abs(m) > (double)i || Math.abs(n) > (double)i) {
            return null;
        }
        int o = random.nextInt(2 * j + 1) - j + k;
        return new BlockPos(m, (double)o, n);
    }

    @VisibleForTesting
    public static BlockPos method_31540(BlockPos blockPos, int i, Predicate<BlockPos> predicate) {
        if (predicate.test(blockPos)) {
            BlockPos blockPos2 = blockPos.up();
            while (blockPos2.getY() < i && predicate.test(blockPos2)) {
                blockPos2 = blockPos2.up();
            }
            return blockPos2;
        }
        return blockPos;
    }

    @VisibleForTesting
    public static BlockPos method_31539(BlockPos blockPos, int i, int j, Predicate<BlockPos> predicate) {
        if (i < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + i + ", expected >= 0");
        }
        if (predicate.test(blockPos)) {
            BlockPos blockPos4;
            BlockPos blockPos2 = blockPos.up();
            while (blockPos2.getY() < j && predicate.test(blockPos2)) {
                blockPos2 = blockPos2.up();
            }
            BlockPos blockPos3 = blockPos2;
            while (blockPos3.getY() < j && blockPos3.getY() - blockPos2.getY() < i && !predicate.test(blockPos4 = blockPos3.up())) {
                blockPos3 = blockPos4;
            }
            return blockPos3;
        }
        return blockPos;
    }

    @Nullable
    public static Vec3d method_31538(PathAwareEntity pathAwareEntity, Supplier<BlockPos> supplier) {
        return class_5535.method_31543(supplier, pathAwareEntity::getPathfindingFavor);
    }

    @Nullable
    public static Vec3d method_31543(Supplier<BlockPos> supplier, ToDoubleFunction<BlockPos> toDoubleFunction) {
        double d = Double.NEGATIVE_INFINITY;
        BlockPos blockPos = null;
        for (int i = 0; i < 10; ++i) {
            double e;
            BlockPos blockPos2 = supplier.get();
            if (blockPos2 == null || !((e = toDoubleFunction.applyAsDouble(blockPos2)) > d)) continue;
            d = e;
            blockPos = blockPos2;
        }
        return blockPos != null ? Vec3d.ofBottomCenter(blockPos) : null;
    }

    public static BlockPos method_31537(PathAwareEntity pathAwareEntity, int i, Random random, BlockPos blockPos) {
        int j = blockPos.getX();
        int k = blockPos.getZ();
        if (pathAwareEntity.hasPositionTarget() && i > 1) {
            BlockPos blockPos2 = pathAwareEntity.getPositionTarget();
            j = pathAwareEntity.getX() > (double)blockPos2.getX() ? (j -= random.nextInt(i / 2)) : (j += random.nextInt(i / 2));
            k = pathAwareEntity.getZ() > (double)blockPos2.getZ() ? (k -= random.nextInt(i / 2)) : (k += random.nextInt(i / 2));
        }
        return new BlockPos((double)j + pathAwareEntity.getX(), (double)blockPos.getY() + pathAwareEntity.getY(), (double)k + pathAwareEntity.getZ());
    }
}

