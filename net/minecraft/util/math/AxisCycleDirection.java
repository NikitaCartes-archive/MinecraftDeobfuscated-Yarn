/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.minecraft.util.math.Direction;

public enum AxisCycleDirection {
    NONE{

        @Override
        public int choose(int i, int j, int k, Direction.Axis axis) {
            return axis.choose(i, j, k);
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
        public int choose(int i, int j, int k, Direction.Axis axis) {
            return axis.choose(k, i, j);
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
        public int choose(int i, int j, int k, Direction.Axis axis) {
            return axis.choose(j, k, i);
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

    public abstract Direction.Axis cycle(Direction.Axis var1);

    public abstract AxisCycleDirection opposite();

    public static AxisCycleDirection between(Direction.Axis axis, Direction.Axis axis2) {
        return VALUES[Math.floorMod(axis2.ordinal() - axis.ordinal(), 3)];
    }

    static {
        AXES = Direction.Axis.values();
        VALUES = AxisCycleDirection.values();
    }
}

