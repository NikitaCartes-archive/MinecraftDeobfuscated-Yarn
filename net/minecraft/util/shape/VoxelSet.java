/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.BitSetVoxelSet;

public abstract class VoxelSet {
    private static final Direction.Axis[] AXES = Direction.Axis.values();
    protected final int sizeX;
    protected final int sizeY;
    protected final int sizeZ;

    protected VoxelSet(int sizeX, int sizeY, int sizeZ) {
        if (sizeX < 0 || sizeY < 0 || sizeZ < 0) {
            throw new IllegalArgumentException("Need all positive sizes: x: " + sizeX + ", y: " + sizeY + ", z: " + sizeZ);
        }
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        this.sizeZ = sizeZ;
    }

    public boolean inBoundsAndContains(AxisCycleDirection cycle, int x, int y, int z) {
        return this.inBoundsAndContains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
    }

    public boolean inBoundsAndContains(int x, int y, int z) {
        if (x < 0 || y < 0 || z < 0) {
            return false;
        }
        if (x >= this.sizeX || y >= this.sizeY || z >= this.sizeZ) {
            return false;
        }
        return this.contains(x, y, z);
    }

    public boolean contains(AxisCycleDirection cycle, int x, int y, int z) {
        return this.contains(cycle.choose(x, y, z, Direction.Axis.X), cycle.choose(x, y, z, Direction.Axis.Y), cycle.choose(x, y, z, Direction.Axis.Z));
    }

    public abstract boolean contains(int var1, int var2, int var3);

    public abstract void set(int var1, int var2, int var3);

    public boolean isEmpty() {
        for (Direction.Axis axis : AXES) {
            if (this.getMin(axis) < this.getMax(axis)) continue;
            return true;
        }
        return false;
    }

    public abstract int getMin(Direction.Axis var1);

    public abstract int getMax(Direction.Axis var1);

    public int getStartingAxisCoord(Direction.Axis axis, int from, int to) {
        int i = this.getSize(axis);
        if (from < 0 || to < 0) {
            return i;
        }
        Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
        Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
        if (from >= this.getSize(axis2) || to >= this.getSize(axis3)) {
            return i;
        }
        AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.X, axis);
        for (int j = 0; j < i; ++j) {
            if (!this.contains(axisCycleDirection, j, from, to)) continue;
            return j;
        }
        return i;
    }

    public int getEndingAxisCoord(Direction.Axis axis, int from, int to) {
        if (from < 0 || to < 0) {
            return 0;
        }
        Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
        Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
        if (from >= this.getSize(axis2) || to >= this.getSize(axis3)) {
            return 0;
        }
        int i = this.getSize(axis);
        AxisCycleDirection axisCycleDirection = AxisCycleDirection.between(Direction.Axis.X, axis);
        for (int j = i - 1; j >= 0; --j) {
            if (!this.contains(axisCycleDirection, j, from, to)) continue;
            return j + 1;
        }
        return 0;
    }

    public int getSize(Direction.Axis axis) {
        return axis.choose(this.sizeX, this.sizeY, this.sizeZ);
    }

    public int getXSize() {
        return this.getSize(Direction.Axis.X);
    }

    public int getYSize() {
        return this.getSize(Direction.Axis.Y);
    }

    public int getZSize() {
        return this.getSize(Direction.Axis.Z);
    }

    public void forEachEdge(PositionBiConsumer positionBiConsumer, boolean bl) {
        this.forEachEdge(positionBiConsumer, AxisCycleDirection.NONE, bl);
        this.forEachEdge(positionBiConsumer, AxisCycleDirection.FORWARD, bl);
        this.forEachEdge(positionBiConsumer, AxisCycleDirection.BACKWARD, bl);
    }

    private void forEachEdge(PositionBiConsumer positionBiConsumer, AxisCycleDirection direction, boolean bl) {
        AxisCycleDirection axisCycleDirection = direction.opposite();
        int i = this.getSize(axisCycleDirection.cycle(Direction.Axis.X));
        int j = this.getSize(axisCycleDirection.cycle(Direction.Axis.Y));
        int k = this.getSize(axisCycleDirection.cycle(Direction.Axis.Z));
        for (int l = 0; l <= i; ++l) {
            for (int m = 0; m <= j; ++m) {
                int n = -1;
                for (int o = 0; o <= k; ++o) {
                    int p = 0;
                    int q = 0;
                    for (int r = 0; r <= 1; ++r) {
                        for (int s = 0; s <= 1; ++s) {
                            if (!this.inBoundsAndContains(axisCycleDirection, l + r - 1, m + s - 1, o)) continue;
                            ++p;
                            q ^= r ^ s;
                        }
                    }
                    if (p == 1 || p == 3 || p == 2 && !(q & true)) {
                        if (bl) {
                            if (n != -1) continue;
                            n = o;
                            continue;
                        }
                        positionBiConsumer.consume(axisCycleDirection.choose(l, m, o, Direction.Axis.X), axisCycleDirection.choose(l, m, o, Direction.Axis.Y), axisCycleDirection.choose(l, m, o, Direction.Axis.Z), axisCycleDirection.choose(l, m, o + 1, Direction.Axis.X), axisCycleDirection.choose(l, m, o + 1, Direction.Axis.Y), axisCycleDirection.choose(l, m, o + 1, Direction.Axis.Z));
                        continue;
                    }
                    if (n == -1) continue;
                    positionBiConsumer.consume(axisCycleDirection.choose(l, m, n, Direction.Axis.X), axisCycleDirection.choose(l, m, n, Direction.Axis.Y), axisCycleDirection.choose(l, m, n, Direction.Axis.Z), axisCycleDirection.choose(l, m, o, Direction.Axis.X), axisCycleDirection.choose(l, m, o, Direction.Axis.Y), axisCycleDirection.choose(l, m, o, Direction.Axis.Z));
                    n = -1;
                }
            }
        }
    }

    public void forEachBox(PositionBiConsumer consumer, boolean largest) {
        BitSetVoxelSet.method_31941(this, consumer, largest);
    }

    public void forEachDirection(PositionConsumer positionConsumer) {
        this.forEachDirection(positionConsumer, AxisCycleDirection.NONE);
        this.forEachDirection(positionConsumer, AxisCycleDirection.FORWARD);
        this.forEachDirection(positionConsumer, AxisCycleDirection.BACKWARD);
    }

    private void forEachDirection(PositionConsumer positionConsumer, AxisCycleDirection direction) {
        AxisCycleDirection axisCycleDirection = direction.opposite();
        Direction.Axis axis = axisCycleDirection.cycle(Direction.Axis.Z);
        int i = this.getSize(axisCycleDirection.cycle(Direction.Axis.X));
        int j = this.getSize(axisCycleDirection.cycle(Direction.Axis.Y));
        int k = this.getSize(axis);
        Direction direction2 = Direction.from(axis, Direction.AxisDirection.NEGATIVE);
        Direction direction3 = Direction.from(axis, Direction.AxisDirection.POSITIVE);
        for (int l = 0; l < i; ++l) {
            for (int m = 0; m < j; ++m) {
                boolean bl = false;
                for (int n = 0; n <= k; ++n) {
                    boolean bl2;
                    boolean bl3 = bl2 = n != k && this.contains(axisCycleDirection, l, m, n);
                    if (!bl && bl2) {
                        positionConsumer.consume(direction2, axisCycleDirection.choose(l, m, n, Direction.Axis.X), axisCycleDirection.choose(l, m, n, Direction.Axis.Y), axisCycleDirection.choose(l, m, n, Direction.Axis.Z));
                    }
                    if (bl && !bl2) {
                        positionConsumer.consume(direction3, axisCycleDirection.choose(l, m, n - 1, Direction.Axis.X), axisCycleDirection.choose(l, m, n - 1, Direction.Axis.Y), axisCycleDirection.choose(l, m, n - 1, Direction.Axis.Z));
                    }
                    bl = bl2;
                }
            }
        }
    }

    public static interface PositionBiConsumer {
        public void consume(int var1, int var2, int var3, int var4, int var5, int var6);
    }

    public static interface PositionConsumer {
        public void consume(Direction var1, int var2, int var3, int var4);
    }
}

