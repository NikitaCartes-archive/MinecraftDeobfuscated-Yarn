/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.google.common.collect.Lists;
import java.util.List;
import net.minecraft.util.math.BlockPos;

/**
 * Represents a density field in an area. Consider visualizing it like real
 * life gravity's distortion of space.
 */
public class GravityField {
    private final List<Point> points = Lists.newArrayList();

    /**
     * Adds a point to the gravity field.
     */
    public void addPoint(BlockPos pos, double mass) {
        if (mass != 0.0) {
            this.points.add(new Point(pos, mass));
        }
    }

    /**
     * Calculate the gravity on a potential point at {@code pos} with {@code mass}.
     */
    public double calculate(BlockPos pos, double mass) {
        if (mass == 0.0) {
            return 0.0;
        }
        double d = 0.0;
        for (Point point : this.points) {
            d += point.getGravityFactor(pos);
        }
        return d * mass;
    }

    static class Point {
        private final BlockPos pos;
        private final double mass;

        public Point(BlockPos pos, double mass) {
            this.pos = pos;
            this.mass = mass;
        }

        public double getGravityFactor(BlockPos pos) {
            double d = this.pos.getSquaredDistance(pos);
            if (d == 0.0) {
                return Double.POSITIVE_INFINITY;
            }
            return this.mass / Math.sqrt(d);
        }
    }
}

