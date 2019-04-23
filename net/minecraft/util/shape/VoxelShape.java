/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.ArrayList;
import java.util.List;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.OffsetDoubleList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.SliceVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShapes;
import org.jetbrains.annotations.Nullable;

public abstract class VoxelShape {
    protected final VoxelSet voxels;

    VoxelShape(VoxelSet voxelSet) {
        this.voxels = voxelSet;
    }

    public double getMinimum(Direction.Axis axis) {
        int i = this.voxels.getMin(axis);
        if (i >= this.voxels.getSize(axis)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.getCoord(axis, i);
    }

    public double getMaximum(Direction.Axis axis) {
        int i = this.voxels.getMax(axis);
        if (i <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getCoord(axis, i);
    }

    public BoundingBox getBoundingBox() {
        if (this.isEmpty()) {
            throw new UnsupportedOperationException("No bounds for empty shape.");
        }
        return new BoundingBox(this.getMinimum(Direction.Axis.X), this.getMinimum(Direction.Axis.Y), this.getMinimum(Direction.Axis.Z), this.getMaximum(Direction.Axis.X), this.getMaximum(Direction.Axis.Y), this.getMaximum(Direction.Axis.Z));
    }

    protected double getCoord(Direction.Axis axis, int i) {
        return this.getIncludedPoints(axis).getDouble(i);
    }

    protected abstract DoubleList getIncludedPoints(Direction.Axis var1);

    public boolean isEmpty() {
        return this.voxels.isEmpty();
    }

    public VoxelShape offset(double d, double e, double f) {
        if (this.isEmpty()) {
            return VoxelShapes.empty();
        }
        return new ArrayVoxelShape(this.voxels, new OffsetDoubleList(this.getIncludedPoints(Direction.Axis.X), d), new OffsetDoubleList(this.getIncludedPoints(Direction.Axis.Y), e), new OffsetDoubleList(this.getIncludedPoints(Direction.Axis.Z), f));
    }

    public VoxelShape simplify() {
        VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty()};
        this.forEachBox((d, e, f, g, h, i) -> {
            voxelShapes[0] = VoxelShapes.combine(voxelShapes[0], VoxelShapes.cuboid(d, e, f, g, h, i), BooleanBiFunction.OR);
        });
        return voxelShapes[0];
    }

    @Environment(value=EnvType.CLIENT)
    public void forEachEdge(VoxelShapes.BoxConsumer boxConsumer) {
        this.voxels.forEachEdge((i, j, k, l, m, n) -> boxConsumer.consume(this.getCoord(Direction.Axis.X, i), this.getCoord(Direction.Axis.Y, j), this.getCoord(Direction.Axis.Z, k), this.getCoord(Direction.Axis.X, l), this.getCoord(Direction.Axis.Y, m), this.getCoord(Direction.Axis.Z, n)), true);
    }

    public void forEachBox(VoxelShapes.BoxConsumer boxConsumer) {
        this.voxels.forEachBox((i, j, k, l, m, n) -> boxConsumer.consume(this.getCoord(Direction.Axis.X, i), this.getCoord(Direction.Axis.Y, j), this.getCoord(Direction.Axis.Z, k), this.getCoord(Direction.Axis.X, l), this.getCoord(Direction.Axis.Y, m), this.getCoord(Direction.Axis.Z, n)), true);
    }

    public List<BoundingBox> getBoundingBoxes() {
        ArrayList<BoundingBox> list = Lists.newArrayList();
        this.forEachBox((d, e, f, g, h, i) -> list.add(new BoundingBox(d, e, f, g, h, i)));
        return list;
    }

    @Environment(value=EnvType.CLIENT)
    public double method_1093(Direction.Axis axis, double d, double e) {
        int j;
        Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
        Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
        int i = this.getCoordIndex(axis2, d);
        int k = this.voxels.method_1043(axis, i, j = this.getCoordIndex(axis3, e));
        if (k >= this.voxels.getSize(axis)) {
            return Double.POSITIVE_INFINITY;
        }
        return this.getCoord(axis, k);
    }

    @Environment(value=EnvType.CLIENT)
    public double method_1102(Direction.Axis axis, double d, double e) {
        int j;
        Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
        Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
        int i = this.getCoordIndex(axis2, d);
        int k = this.voxels.method_1058(axis, i, j = this.getCoordIndex(axis3, e));
        if (k <= 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return this.getCoord(axis, k);
    }

    protected int getCoordIndex(Direction.Axis axis, double d) {
        return MathHelper.binarySearch(0, this.voxels.getSize(axis) + 1, i -> {
            if (i < 0) {
                return false;
            }
            if (i > this.voxels.getSize(axis)) {
                return true;
            }
            return d < this.getCoord(axis, i);
        }) - 1;
    }

    protected boolean contains(double d, double e, double f) {
        return this.voxels.inBoundsAndContains(this.getCoordIndex(Direction.Axis.X, d), this.getCoordIndex(Direction.Axis.Y, e), this.getCoordIndex(Direction.Axis.Z, f));
    }

    @Nullable
    public BlockHitResult rayTrace(Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos) {
        if (this.isEmpty()) {
            return null;
        }
        Vec3d vec3d3 = vec3d2.subtract(vec3d);
        if (vec3d3.lengthSquared() < 1.0E-7) {
            return null;
        }
        Vec3d vec3d4 = vec3d.add(vec3d3.multiply(0.001));
        if (this.contains(vec3d4.x - (double)blockPos.getX(), vec3d4.y - (double)blockPos.getY(), vec3d4.z - (double)blockPos.getZ())) {
            return new BlockHitResult(vec3d4, Direction.getFacing(vec3d3.x, vec3d3.y, vec3d3.z).getOpposite(), blockPos, true);
        }
        return BoundingBox.rayTrace(this.getBoundingBoxes(), vec3d, vec3d2, blockPos);
    }

    public VoxelShape getFace(Direction direction) {
        if (this.isEmpty() || this == VoxelShapes.fullCube()) {
            return this;
        }
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getDirection();
        DoubleList doubleList = this.getIncludedPoints(axis);
        if (doubleList.size() == 2 && DoubleMath.fuzzyEquals(doubleList.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(doubleList.getDouble(1), 1.0, 1.0E-7)) {
            return this;
        }
        int i = this.getCoordIndex(axis, axisDirection == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
        return new SliceVoxelShape(this, axis, i);
    }

    public double method_1108(Direction.Axis axis, BoundingBox boundingBox, double d) {
        return this.method_1103(AxisCycleDirection.between(axis, Direction.Axis.X), boundingBox, d);
    }

    protected double method_1103(AxisCycleDirection axisCycleDirection, BoundingBox boundingBox, double d) {
        block11: {
            int n;
            int l;
            double f;
            Direction.Axis axis;
            AxisCycleDirection axisCycleDirection2;
            block10: {
                if (this.isEmpty()) {
                    return d;
                }
                if (Math.abs(d) < 1.0E-7) {
                    return 0.0;
                }
                axisCycleDirection2 = axisCycleDirection.opposite();
                axis = axisCycleDirection2.cycle(Direction.Axis.X);
                Direction.Axis axis2 = axisCycleDirection2.cycle(Direction.Axis.Y);
                Direction.Axis axis3 = axisCycleDirection2.cycle(Direction.Axis.Z);
                double e = boundingBox.getMax(axis);
                f = boundingBox.getMin(axis);
                int i = this.getCoordIndex(axis, f + 1.0E-7);
                int j = this.getCoordIndex(axis, e - 1.0E-7);
                int k = Math.max(0, this.getCoordIndex(axis2, boundingBox.getMin(axis2) + 1.0E-7));
                l = Math.min(this.voxels.getSize(axis2), this.getCoordIndex(axis2, boundingBox.getMax(axis2) - 1.0E-7) + 1);
                int m = Math.max(0, this.getCoordIndex(axis3, boundingBox.getMin(axis3) + 1.0E-7));
                n = Math.min(this.voxels.getSize(axis3), this.getCoordIndex(axis3, boundingBox.getMax(axis3) - 1.0E-7) + 1);
                int o = this.voxels.getSize(axis);
                if (!(d > 0.0)) break block10;
                for (int p = j + 1; p < o; ++p) {
                    for (int q = k; q < l; ++q) {
                        for (int r = m; r < n; ++r) {
                            if (!this.voxels.inBoundsAndContains(axisCycleDirection2, p, q, r)) continue;
                            double g = this.getCoord(axis, p) - e;
                            if (g >= -1.0E-7) {
                                d = Math.min(d, g);
                            }
                            return d;
                        }
                    }
                }
                break block11;
            }
            if (!(d < 0.0)) break block11;
            for (int p = i - 1; p >= 0; --p) {
                for (int q = k; q < l; ++q) {
                    for (int r = m; r < n; ++r) {
                        if (!this.voxels.inBoundsAndContains(axisCycleDirection2, p, q, r)) continue;
                        double g = this.getCoord(axis, p + 1) - f;
                        if (g <= 1.0E-7) {
                            d = Math.max(d, g);
                        }
                        return d;
                    }
                }
            }
        }
        return d;
    }

    public String toString() {
        return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
    }
}

