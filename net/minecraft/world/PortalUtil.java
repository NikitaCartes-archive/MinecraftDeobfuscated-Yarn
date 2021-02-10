/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.world;

import com.google.common.annotations.VisibleForTesting;
import com.mojang.datafixers.util.Pair;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import java.util.function.Predicate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

public class PortalUtil {
    /**
     * Gets the largest rectangle of blocks along two axes for which all blocks meet a predicate.
     * Used for getting rectangles of Nether portal blocks.
     */
    public static Rectangle getLargestRectangle(BlockPos center, Direction.Axis primaryAxis, int primaryMaxBlocks, Direction.Axis secondaryAxis, int secondaryMaxBlocks, Predicate<BlockPos> predicate) {
        IntBounds intBounds;
        int m;
        BlockPos.Mutable mutable = center.mutableCopy();
        Direction direction = Direction.get(Direction.AxisDirection.NEGATIVE, primaryAxis);
        Direction direction2 = direction.getOpposite();
        Direction direction3 = Direction.get(Direction.AxisDirection.NEGATIVE, secondaryAxis);
        Direction direction4 = direction3.getOpposite();
        int i = PortalUtil.moveWhile(predicate, mutable.set(center), direction, primaryMaxBlocks);
        int j = PortalUtil.moveWhile(predicate, mutable.set(center), direction2, primaryMaxBlocks);
        int k = i;
        IntBounds[] intBoundss = new IntBounds[k + 1 + j];
        intBoundss[k] = new IntBounds(PortalUtil.moveWhile(predicate, mutable.set(center), direction3, secondaryMaxBlocks), PortalUtil.moveWhile(predicate, mutable.set(center), direction4, secondaryMaxBlocks));
        int l = intBoundss[k].min;
        for (m = 1; m <= i; ++m) {
            intBounds = intBoundss[k - (m - 1)];
            intBoundss[k - m] = new IntBounds(PortalUtil.moveWhile(predicate, mutable.set(center).move(direction, m), direction3, intBounds.min), PortalUtil.moveWhile(predicate, mutable.set(center).move(direction, m), direction4, intBounds.max));
        }
        for (m = 1; m <= j; ++m) {
            intBounds = intBoundss[k + m - 1];
            intBoundss[k + m] = new IntBounds(PortalUtil.moveWhile(predicate, mutable.set(center).move(direction2, m), direction3, intBounds.min), PortalUtil.moveWhile(predicate, mutable.set(center).move(direction2, m), direction4, intBounds.max));
        }
        m = 0;
        int n = 0;
        int o = 0;
        int p = 0;
        int[] is = new int[intBoundss.length];
        for (int q = l; q >= 0; --q) {
            int t;
            int s;
            IntBounds intBounds2;
            for (int r = 0; r < intBoundss.length; ++r) {
                intBounds2 = intBoundss[r];
                s = l - intBounds2.min;
                t = l + intBounds2.max;
                is[r] = q >= s && q <= t ? t + 1 - q : 0;
            }
            Pair<IntBounds, Integer> pair = PortalUtil.findLargestRectangle(is);
            intBounds2 = pair.getFirst();
            s = 1 + intBounds2.max - intBounds2.min;
            t = pair.getSecond();
            if (s * t <= o * p) continue;
            m = intBounds2.min;
            n = q;
            o = s;
            p = t;
        }
        return new Rectangle(center.offset(primaryAxis, m - k).offset(secondaryAxis, n - l), o, p);
    }

    private static int moveWhile(Predicate<BlockPos> predicate, BlockPos.Mutable mutable, Direction direction, int max) {
        int i;
        for (i = 0; i < max && predicate.test(mutable.move(direction)); ++i) {
        }
        return i;
    }

    /**
     * Finds the largest rectangle within a histogram, where the vertical bars each have
     * width 1 and height specified in {@code heights}.
     * 
     * @implNote This implementation solves the problem using a stack. The
     * stack maintains a collection of height limits of rectangles that may grow as the
     * array iteration continues. When a new height is encountered, each position {@code p}
     * in the stack would be popped if the rectangle with height limit at position {@code
     * p} can no longer extend right. The popped rectangle becomes the return value if it
     * has a larger area than the current candidate.
     * 
     * <p>When the rectangle area is calculated, the range is between {@code p0 + 1}, where
     * {@code p0} is the current top of stack after popping rectangles that can no longer
     * extend, and the current iterated position {@code i}.
     * 
     * @return the base of the rectangle as an inclusive range and the height of the
     * rectangle packed in a pair
     * @see <a href="https://leetcode.com/problems/largest-rectangle-in-histogram">Largest
     * Rectangle in Histogram - LeetCode</a>
     * 
     * @param heights the heights of bars in the histogram
     */
    @VisibleForTesting
    static Pair<IntBounds, Integer> findLargestRectangle(int[] heights) {
        int i = 0;
        int j = 0;
        int k = 0;
        IntArrayList intStack = new IntArrayList();
        intStack.push(0);
        for (int l = 1; l <= heights.length; ++l) {
            int m;
            int n = m = l == heights.length ? 0 : heights[l];
            while (!intStack.isEmpty()) {
                int n2 = heights[intStack.topInt()];
                if (m >= n2) {
                    intStack.push(l);
                    break;
                }
                intStack.popInt();
                int o = intStack.isEmpty() ? 0 : intStack.topInt() + 1;
                if (n2 * (l - o) <= k * (j - i)) continue;
                j = l;
                i = o;
                k = n2;
            }
            if (!intStack.isEmpty()) continue;
            intStack.push(l);
        }
        return new Pair<IntBounds, Integer>(new IntBounds(i, j - 1), k);
    }

    public static class Rectangle {
        public final BlockPos lowerLeft;
        public final int width;
        public final int height;

        public Rectangle(BlockPos lowerLeft, int width, int height) {
            this.lowerLeft = lowerLeft;
            this.width = width;
            this.height = height;
        }
    }

    public static class IntBounds {
        public final int min;
        public final int max;

        public IntBounds(int min, int max) {
            this.min = min;
            this.max = max;
        }

        public String toString() {
            return "IntBounds{min=" + this.min + ", max=" + this.max + '}';
        }
    }
}

