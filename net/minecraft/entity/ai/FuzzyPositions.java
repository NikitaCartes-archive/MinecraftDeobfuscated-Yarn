/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.entity.ai;

import com.google.common.annotations.VisibleForTesting;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.function.ToDoubleFunction;
import net.minecraft.entity.mob.PathAwareEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import org.jetbrains.annotations.Nullable;

public class FuzzyPositions {
    private static final int GAUSS_RANGE = 10;

    /**
     * Creates a fuzzy offset position within the given horizontal and vertical
     * ranges.
     */
    public static BlockPos localFuzz(Random random, int horizontalRange, int verticalRange) {
        int i = random.nextInt(2 * horizontalRange + 1) - horizontalRange;
        int j = random.nextInt(2 * verticalRange + 1) - verticalRange;
        int k = random.nextInt(2 * horizontalRange + 1) - horizontalRange;
        return new BlockPos(i, j, k);
    }

    /**
     * Tries to create a fuzzy offset position from the direction and the angle
     * range given. It fulfills the constraints given by {@code horizontalRange}
     * and {@code verticalRange} and returns {@code null} if it cannot do so.
     */
    @Nullable
    public static BlockPos localFuzz(Random random, int horizontalRange, int verticalRange, int startHeight, double directionX, double directionZ, double angleRange) {
        double d = MathHelper.atan2(directionZ, directionX) - 1.5707963705062866;
        double e = d + (double)(2.0f * random.nextFloat() - 1.0f) * angleRange;
        double f = Math.sqrt(random.nextDouble()) * (double)MathHelper.SQUARE_ROOT_OF_TWO * (double)horizontalRange;
        double g = -f * Math.sin(e);
        double h = f * Math.cos(e);
        if (Math.abs(g) > (double)horizontalRange || Math.abs(h) > (double)horizontalRange) {
            return null;
        }
        int i = random.nextInt(2 * verticalRange + 1) - verticalRange + startHeight;
        return BlockPos.ofFloored(g, i, h);
    }

    /**
     * Returns the closest position higher than the input {@code pos} that does
     * not fulfill {@code condition}, or a position with y set to {@code maxY}.
     */
    @VisibleForTesting
    public static BlockPos upWhile(BlockPos pos, int maxY, Predicate<BlockPos> condition) {
        if (condition.test(pos)) {
            BlockPos blockPos = pos.up();
            while (blockPos.getY() < maxY && condition.test(blockPos)) {
                blockPos = blockPos.up();
            }
            return blockPos;
        }
        return pos;
    }

    /**
     * Returns the {@code extraAbove + 1}th closest position higher than the
     * input {@code pos} that does not fulfill {@code condition}, or a
     * position with y set to {@code maxY}.
     */
    @VisibleForTesting
    public static BlockPos upWhile(BlockPos pos, int extraAbove, int max, Predicate<BlockPos> condition) {
        if (extraAbove < 0) {
            throw new IllegalArgumentException("aboveSolidAmount was " + extraAbove + ", expected >= 0");
        }
        if (condition.test(pos)) {
            BlockPos blockPos3;
            BlockPos blockPos = pos.up();
            while (blockPos.getY() < max && condition.test(blockPos)) {
                blockPos = blockPos.up();
            }
            BlockPos blockPos2 = blockPos;
            while (blockPos2.getY() < max && blockPos2.getY() - blockPos.getY() < extraAbove && !condition.test(blockPos3 = blockPos2.up())) {
                blockPos2 = blockPos3;
            }
            return blockPos2;
        }
        return pos;
    }

    /**
     * Calls {@link #guessBest(Supplier, ToDoubleFunction)} with the {@code entity}'s
     * path finding favor as the {@code scorer}.
     */
    @Nullable
    public static Vec3d guessBestPathTarget(PathAwareEntity entity, Supplier<BlockPos> factory) {
        return FuzzyPositions.guessBest(factory, entity::getPathfindingFavor);
    }

    /**
     * Returns the {@link Vec3d#ofBottomCenter(BlockPos) bottom center} of a highest scoring
     * position, as determined by {@code scorer}, out of 10 tries on positions obtained from
     * {@code factory}.
     */
    @Nullable
    public static Vec3d guessBest(Supplier<BlockPos> factory, ToDoubleFunction<BlockPos> scorer) {
        double d = Double.NEGATIVE_INFINITY;
        BlockPos blockPos = null;
        for (int i = 0; i < 10; ++i) {
            double e;
            BlockPos blockPos2 = factory.get();
            if (blockPos2 == null || !((e = scorer.applyAsDouble(blockPos2)) > d)) continue;
            d = e;
            blockPos = blockPos2;
        }
        return blockPos != null ? Vec3d.ofBottomCenter(blockPos) : null;
    }

    /**
     * Adjusts the input {@code fuzz} slightly toward the given {@code entity}'s
     * {@link net.minecraft.entity.mob.MobEntity#getPositionTarget() position target}
     * if it exists.
     */
    public static BlockPos towardTarget(PathAwareEntity entity, int horizontalRange, Random random, BlockPos fuzz) {
        int i = fuzz.getX();
        int j = fuzz.getZ();
        if (entity.hasPositionTarget() && horizontalRange > 1) {
            BlockPos blockPos = entity.getPositionTarget();
            i = entity.getX() > (double)blockPos.getX() ? (i -= random.nextInt(horizontalRange / 2)) : (i += random.nextInt(horizontalRange / 2));
            j = entity.getZ() > (double)blockPos.getZ() ? (j -= random.nextInt(horizontalRange / 2)) : (j += random.nextInt(horizontalRange / 2));
        }
        return BlockPos.ofFloored((double)i + entity.getX(), (double)fuzz.getY() + entity.getY(), (double)j + entity.getZ());
    }
}

