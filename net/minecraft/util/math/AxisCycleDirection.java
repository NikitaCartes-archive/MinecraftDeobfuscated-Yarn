/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Direction;

/*
 * Uses 'sealed' constructs - enablewith --sealed true
 */
public enum AxisCycleDirection {
    NONE{

        @Override
        public int choose(int x, int y, int z, Direction.Axis axis) {
            return axis.choose(x, y, z);
        }

        @Override
        public double choose(double x, double y, double z, Direction.Axis axis) {
            return axis.choose(x, y, z);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis axis) {
            return axis;
        }

        @Override
        public AxisCycleDirection opposite() {
            return this;
        }
    }
    ,
    FORWARD{

        @Override
        public int choose(int x, int y, int z, Direction.Axis axis) {
            return axis.choose(z, x, y);
        }

        @Override
        public double choose(double x, double y, double z, Direction.Axis axis) {
            return axis.choose(z, x, y);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis axis) {
            return AXES[Math.floorMod(axis.ordinal() + 1, 3)];
        }

        @Override
        public AxisCycleDirection opposite() {
            return BACKWARD;
        }
    }
    ,
    BACKWARD{

        @Override
        public int choose(int x, int y, int z, Direction.Axis axis) {
            return axis.choose(y, z, x);
        }

        @Override
        public double choose(double x, double y, double z, Direction.Axis axis) {
            return axis.choose(y, z, x);
        }

        @Override
        public Direction.Axis cycle(Direction.Axis axis) {
            return AXES[Math.floorMod(axis.ordinal() - 1, 3)];
        }

        @Override
        public AxisCycleDirection opposite() {
            return FORWARD;
        }
    };

    public static final Direction.Axis[] AXES;
    public static final AxisCycleDirection[] VALUES;

    public abstract int choose(int var1, int var2, int var3, Direction.Axis var4);

    public abstract double choose(double var1, double var3, double var5, Direction.Axis var7);

    public abstract Direction.Axis cycle(Direction.Axis var1);

    public abstract AxisCycleDirection opposite();

    public static AxisCycleDirection between(Direction.Axis from, Direction.Axis to) {
        return VALUES[Math.floorMod(to.ordinal() - from.ordinal(), 3)];
    }

    static {
        AXES = Direction.Axis.values();
        VALUES = AxisCycleDirection.values();
    }
}

