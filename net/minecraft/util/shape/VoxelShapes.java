/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.shape;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.class_4538;
import net.minecraft.entity.EntityContext;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.shape.ArrayVoxelShape;
import net.minecraft.util.shape.BitSetVoxelSet;
import net.minecraft.util.shape.DisjointDoubleListPair;
import net.minecraft.util.shape.DoubleListPair;
import net.minecraft.util.shape.FractionalDoubleList;
import net.minecraft.util.shape.FractionalDoubleListPair;
import net.minecraft.util.shape.IdentityListMerger;
import net.minecraft.util.shape.SimpleDoubleListPair;
import net.minecraft.util.shape.SimpleVoxelShape;
import net.minecraft.util.shape.SliceVoxelShape;
import net.minecraft.util.shape.VoxelSet;
import net.minecraft.util.shape.VoxelShape;

public final class VoxelShapes {
    private static final VoxelShape FULL_CUBE = SystemUtil.get(() -> {
        BitSetVoxelSet voxelSet = new BitSetVoxelSet(1, 1, 1);
        ((VoxelSet)voxelSet).set(0, 0, 0, true, true);
        return new SimpleVoxelShape(voxelSet);
    });
    public static final VoxelShape UNBOUNDED = VoxelShapes.cuboid(Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY);
    private static final VoxelShape EMPTY = new ArrayVoxelShape((VoxelSet)new BitSetVoxelSet(0, 0, 0), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0}));

    public static VoxelShape empty() {
        return EMPTY;
    }

    public static VoxelShape fullCube() {
        return FULL_CUBE;
    }

    public static VoxelShape cuboid(double d, double e, double f, double g, double h, double i) {
        return VoxelShapes.cuboid(new Box(d, e, f, g, h, i));
    }

    public static VoxelShape cuboid(Box box) {
        int i = VoxelShapes.findRequiredBitResolution(box.minX, box.maxX);
        int j = VoxelShapes.findRequiredBitResolution(box.minY, box.maxY);
        int k = VoxelShapes.findRequiredBitResolution(box.minZ, box.maxZ);
        if (i < 0 || j < 0 || k < 0) {
            return new ArrayVoxelShape(VoxelShapes.FULL_CUBE.voxels, new double[]{box.minX, box.maxX}, new double[]{box.minY, box.maxY}, new double[]{box.minZ, box.maxZ});
        }
        if (i == 0 && j == 0 && k == 0) {
            return box.contains(0.5, 0.5, 0.5) ? VoxelShapes.fullCube() : VoxelShapes.empty();
        }
        int l = 1 << i;
        int m = 1 << j;
        int n = 1 << k;
        int o = (int)Math.round(box.minX * (double)l);
        int p = (int)Math.round(box.maxX * (double)l);
        int q = (int)Math.round(box.minY * (double)m);
        int r = (int)Math.round(box.maxY * (double)m);
        int s = (int)Math.round(box.minZ * (double)n);
        int t = (int)Math.round(box.maxZ * (double)n);
        BitSetVoxelSet bitSetVoxelSet = new BitSetVoxelSet(l, m, n, o, q, s, p, r, t);
        for (long u = (long)o; u < (long)p; ++u) {
            for (long v = (long)q; v < (long)r; ++v) {
                for (long w = (long)s; w < (long)t; ++w) {
                    bitSetVoxelSet.set((int)u, (int)v, (int)w, false, true);
                }
            }
        }
        return new SimpleVoxelShape(bitSetVoxelSet);
    }

    private static int findRequiredBitResolution(double d, double e) {
        if (d < -1.0E-7 || e > 1.0000001) {
            return -1;
        }
        for (int i = 0; i <= 3; ++i) {
            boolean bl2;
            double f = d * (double)(1 << i);
            double g = e * (double)(1 << i);
            boolean bl = Math.abs(f - Math.floor(f)) < 1.0E-7;
            boolean bl3 = bl2 = Math.abs(g - Math.floor(g)) < 1.0E-7;
            if (!bl || !bl2) continue;
            return i;
        }
        return -1;
    }

    protected static long lcm(int i, int j) {
        return (long)i * (long)(j / IntMath.gcd(i, j));
    }

    public static VoxelShape union(VoxelShape voxelShape, VoxelShape voxelShape2) {
        return VoxelShapes.combineAndSimplify(voxelShape, voxelShape2, BooleanBiFunction.OR);
    }

    public static VoxelShape union(VoxelShape voxelShape, VoxelShape ... voxelShapes) {
        return Arrays.stream(voxelShapes).reduce(voxelShape, VoxelShapes::union);
    }

    public static VoxelShape combineAndSimplify(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
        return VoxelShapes.combine(voxelShape, voxelShape2, booleanBiFunction).simplify();
    }

    public static VoxelShape combine(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
        if (booleanBiFunction.apply(false, false)) {
            throw SystemUtil.throwOrPause(new IllegalArgumentException());
        }
        if (voxelShape == voxelShape2) {
            return booleanBiFunction.apply(true, true) ? voxelShape : VoxelShapes.empty();
        }
        boolean bl = booleanBiFunction.apply(true, false);
        boolean bl2 = booleanBiFunction.apply(false, true);
        if (voxelShape.isEmpty()) {
            return bl2 ? voxelShape2 : VoxelShapes.empty();
        }
        if (voxelShape2.isEmpty()) {
            return bl ? voxelShape : VoxelShapes.empty();
        }
        DoubleListPair doubleListPair = VoxelShapes.createListPair(1, voxelShape.getPointPositions(Direction.Axis.X), voxelShape2.getPointPositions(Direction.Axis.X), bl, bl2);
        DoubleListPair doubleListPair2 = VoxelShapes.createListPair(doubleListPair.getMergedList().size() - 1, voxelShape.getPointPositions(Direction.Axis.Y), voxelShape2.getPointPositions(Direction.Axis.Y), bl, bl2);
        DoubleListPair doubleListPair3 = VoxelShapes.createListPair((doubleListPair.getMergedList().size() - 1) * (doubleListPair2.getMergedList().size() - 1), voxelShape.getPointPositions(Direction.Axis.Z), voxelShape2.getPointPositions(Direction.Axis.Z), bl, bl2);
        BitSetVoxelSet bitSetVoxelSet = BitSetVoxelSet.combine(voxelShape.voxels, voxelShape2.voxels, doubleListPair, doubleListPair2, doubleListPair3, booleanBiFunction);
        if (doubleListPair instanceof FractionalDoubleListPair && doubleListPair2 instanceof FractionalDoubleListPair && doubleListPair3 instanceof FractionalDoubleListPair) {
            return new SimpleVoxelShape(bitSetVoxelSet);
        }
        return new ArrayVoxelShape((VoxelSet)bitSetVoxelSet, doubleListPair.getMergedList(), doubleListPair2.getMergedList(), doubleListPair3.getMergedList());
    }

    public static boolean matchesAnywhere(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
        if (booleanBiFunction.apply(false, false)) {
            throw SystemUtil.throwOrPause(new IllegalArgumentException());
        }
        if (voxelShape == voxelShape2) {
            return booleanBiFunction.apply(true, true);
        }
        if (voxelShape.isEmpty()) {
            return booleanBiFunction.apply(false, !voxelShape2.isEmpty());
        }
        if (voxelShape2.isEmpty()) {
            return booleanBiFunction.apply(!voxelShape.isEmpty(), false);
        }
        boolean bl = booleanBiFunction.apply(true, false);
        boolean bl2 = booleanBiFunction.apply(false, true);
        for (Direction.Axis axis : AxisCycleDirection.AXES) {
            if (voxelShape.getMaximum(axis) < voxelShape2.getMinimum(axis) - 1.0E-7) {
                return bl || bl2;
            }
            if (!(voxelShape2.getMaximum(axis) < voxelShape.getMinimum(axis) - 1.0E-7)) continue;
            return bl || bl2;
        }
        DoubleListPair doubleListPair = VoxelShapes.createListPair(1, voxelShape.getPointPositions(Direction.Axis.X), voxelShape2.getPointPositions(Direction.Axis.X), bl, bl2);
        DoubleListPair doubleListPair2 = VoxelShapes.createListPair(doubleListPair.getMergedList().size() - 1, voxelShape.getPointPositions(Direction.Axis.Y), voxelShape2.getPointPositions(Direction.Axis.Y), bl, bl2);
        DoubleListPair doubleListPair3 = VoxelShapes.createListPair((doubleListPair.getMergedList().size() - 1) * (doubleListPair2.getMergedList().size() - 1), voxelShape.getPointPositions(Direction.Axis.Z), voxelShape2.getPointPositions(Direction.Axis.Z), bl, bl2);
        return VoxelShapes.matchesAnywhere(doubleListPair, doubleListPair2, doubleListPair3, voxelShape.voxels, voxelShape2.voxels, booleanBiFunction);
    }

    private static boolean matchesAnywhere(DoubleListPair doubleListPair, DoubleListPair doubleListPair2, DoubleListPair doubleListPair3, VoxelSet voxelSet, VoxelSet voxelSet2, BooleanBiFunction booleanBiFunction) {
        return !doubleListPair.forAllOverlappingSections((i, j, k2) -> doubleListPair2.forAllOverlappingSections((k, l, m2) -> doubleListPair3.forAllOverlappingSections((m, n, o) -> !booleanBiFunction.apply(voxelSet.inBoundsAndContains(i, k, m), voxelSet2.inBoundsAndContains(j, l, n)))));
    }

    public static double calculateMaxOffset(Direction.Axis axis, Box box, Stream<VoxelShape> stream, double d) {
        Iterator iterator = stream.iterator();
        while (iterator.hasNext()) {
            if (Math.abs(d) < 1.0E-7) {
                return 0.0;
            }
            d = ((VoxelShape)iterator.next()).method_1108(axis, box, d);
        }
        return d;
    }

    public static double method_17945(Direction.Axis axis, Box box, class_4538 arg, double d, EntityContext entityContext, Stream<VoxelShape> stream) {
        return VoxelShapes.method_17944(box, arg, d, entityContext, AxisCycleDirection.between(axis, Direction.Axis.Z), stream);
    }

    private static double method_17944(Box box, class_4538 arg, double d, EntityContext entityContext, AxisCycleDirection axisCycleDirection, Stream<VoxelShape> stream) {
        if (box.getXLength() < 1.0E-6 || box.getYLength() < 1.0E-6 || box.getZLength() < 1.0E-6) {
            return d;
        }
        if (Math.abs(d) < 1.0E-7) {
            return 0.0;
        }
        AxisCycleDirection axisCycleDirection2 = axisCycleDirection.opposite();
        Direction.Axis axis = axisCycleDirection2.cycle(Direction.Axis.X);
        Direction.Axis axis2 = axisCycleDirection2.cycle(Direction.Axis.Y);
        Direction.Axis axis3 = axisCycleDirection2.cycle(Direction.Axis.Z);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        int i = MathHelper.floor(box.getMin(axis) - 1.0E-7) - 1;
        int j = MathHelper.floor(box.getMax(axis) + 1.0E-7) + 1;
        int k = MathHelper.floor(box.getMin(axis2) - 1.0E-7) - 1;
        int l = MathHelper.floor(box.getMax(axis2) + 1.0E-7) + 1;
        double e = box.getMin(axis3) - 1.0E-7;
        double f = box.getMax(axis3) + 1.0E-7;
        boolean bl = d > 0.0;
        int m = bl ? MathHelper.floor(box.getMax(axis3) - 1.0E-7) - 1 : MathHelper.floor(box.getMin(axis3) + 1.0E-7) + 1;
        int n = VoxelShapes.method_17943(d, e, f);
        int o = bl ? 1 : -1;
        int p = m;
        while (bl ? p <= n : p >= n) {
            for (int q = i; q <= j; ++q) {
                for (int r = k; r <= l; ++r) {
                    int s = 0;
                    if (q == i || q == j) {
                        ++s;
                    }
                    if (r == k || r == l) {
                        ++s;
                    }
                    if (p == m || p == n) {
                        ++s;
                    }
                    if (s >= 3) continue;
                    mutable.set(axisCycleDirection2, q, r, p);
                    BlockState blockState = arg.getBlockState(mutable);
                    if (s == 1 && !blockState.method_17900() || s == 2 && blockState.getBlock() != Blocks.MOVING_PISTON) continue;
                    d = blockState.getCollisionShape(arg, mutable, entityContext).method_1108(axis3, box.offset(-mutable.getX(), -mutable.getY(), -mutable.getZ()), d);
                    if (Math.abs(d) < 1.0E-7) {
                        return 0.0;
                    }
                    n = VoxelShapes.method_17943(d, e, f);
                }
            }
            p += o;
        }
        double[] ds = new double[]{d};
        stream.forEach(voxelShape -> {
            ds[0] = voxelShape.method_1108(axis3, box, ds[0]);
        });
        return ds[0];
    }

    private static int method_17943(double d, double e, double f) {
        return d > 0.0 ? MathHelper.floor(f + d) + 1 : MathHelper.floor(e + d) - 1;
    }

    @Environment(value=EnvType.CLIENT)
    public static boolean method_1083(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
        if (voxelShape == VoxelShapes.fullCube() && voxelShape2 == VoxelShapes.fullCube()) {
            return true;
        }
        if (voxelShape2.isEmpty()) {
            return false;
        }
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getDirection();
        VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
        VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
        BooleanBiFunction booleanBiFunction = axisDirection == Direction.AxisDirection.POSITIVE ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
        return DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7) && DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7) && !VoxelShapes.matchesAnywhere(new SliceVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape4, axis, 0), booleanBiFunction);
    }

    public static VoxelShape method_16344(VoxelShape voxelShape, Direction direction) {
        int i;
        boolean bl;
        if (voxelShape == VoxelShapes.fullCube()) {
            return VoxelShapes.fullCube();
        }
        Direction.Axis axis = direction.getAxis();
        if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
            bl = DoubleMath.fuzzyEquals(voxelShape.getMaximum(axis), 1.0, 1.0E-7);
            i = voxelShape.voxels.getSize(axis) - 1;
        } else {
            bl = DoubleMath.fuzzyEquals(voxelShape.getMinimum(axis), 0.0, 1.0E-7);
            i = 0;
        }
        if (!bl) {
            return VoxelShapes.empty();
        }
        return new SliceVoxelShape(voxelShape, axis, i);
    }

    public static boolean adjacentSidesCoverSquare(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
        VoxelShape voxelShape4;
        if (voxelShape == VoxelShapes.fullCube() || voxelShape2 == VoxelShapes.fullCube()) {
            return true;
        }
        Direction.Axis axis = direction.getAxis();
        Direction.AxisDirection axisDirection = direction.getDirection();
        VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
        VoxelShape voxelShape5 = voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
        if (!DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)) {
            voxelShape3 = VoxelShapes.empty();
        }
        if (!DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)) {
            voxelShape4 = VoxelShapes.empty();
        }
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), VoxelShapes.combine(new SliceVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape4, axis, 0), BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
    }

    public static boolean unionCoversFullCube(VoxelShape voxelShape, VoxelShape voxelShape2) {
        if (voxelShape == VoxelShapes.fullCube() || voxelShape2 == VoxelShapes.fullCube()) {
            return true;
        }
        if (voxelShape.isEmpty() && voxelShape2.isEmpty()) {
            return false;
        }
        return !VoxelShapes.matchesAnywhere(VoxelShapes.fullCube(), VoxelShapes.combine(voxelShape, voxelShape2, BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
    }

    @VisibleForTesting
    protected static DoubleListPair createListPair(int i, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
        long l;
        int j = doubleList.size() - 1;
        int k = doubleList2.size() - 1;
        if (doubleList instanceof FractionalDoubleList && doubleList2 instanceof FractionalDoubleList && (long)i * (l = VoxelShapes.lcm(j, k)) <= 256L) {
            return new FractionalDoubleListPair(j, k);
        }
        if (doubleList.getDouble(j) < doubleList2.getDouble(0) - 1.0E-7) {
            return new DisjointDoubleListPair(doubleList, doubleList2, false);
        }
        if (doubleList2.getDouble(k) < doubleList.getDouble(0) - 1.0E-7) {
            return new DisjointDoubleListPair(doubleList2, doubleList, true);
        }
        if (j == k && Objects.equals(doubleList, doubleList2)) {
            if (doubleList instanceof IdentityListMerger) {
                return (DoubleListPair)((Object)doubleList);
            }
            if (doubleList2 instanceof IdentityListMerger) {
                return (DoubleListPair)((Object)doubleList2);
            }
            return new IdentityListMerger(doubleList);
        }
        return new SimpleDoubleListPair(doubleList, doubleList2, bl, bl2);
    }

    public static interface BoxConsumer {
        public void consume(double var1, double var3, double var5, double var7, double var9, double var11);
    }
}

