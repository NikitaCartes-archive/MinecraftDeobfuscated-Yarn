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
import net.minecraft.entity.EntityContext;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;

public final class VoxelShapes {
	private static final VoxelShape field_1385 = SystemUtil.get(() -> {
		VoxelSet voxelSet = new BitSetVoxelSet(1, 1, 1);
		voxelSet.set(0, 0, 0, true, true);
		return new SimpleVoxelShape(voxelSet);
	});
	public static final VoxelShape field_17669 = method_1081(
		Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
	);
	private static final VoxelShape field_1384 = new ArrayVoxelShape(
		new BitSetVoxelSet(0, 0, 0), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0})
	);

	public static VoxelShape method_1073() {
		return field_1384;
	}

	public static VoxelShape method_1077() {
		return field_1385;
	}

	public static VoxelShape method_1081(double d, double e, double f, double g, double h, double i) {
		return method_1078(new Box(d, e, f, g, h, i));
	}

	public static VoxelShape method_1078(Box box) {
		int i = findRequiredBitResolution(box.minX, box.maxX);
		int j = findRequiredBitResolution(box.minY, box.maxY);
		int k = findRequiredBitResolution(box.minZ, box.maxZ);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return box.contains(0.5, 0.5, 0.5) ? method_1077() : method_1073();
			} else {
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

				for (long u = (long)o; u < (long)p; u++) {
					for (long v = (long)q; v < (long)r; v++) {
						for (long w = (long)s; w < (long)t; w++) {
							bitSetVoxelSet.set((int)u, (int)v, (int)w, false, true);
						}
					}
				}

				return new SimpleVoxelShape(bitSetVoxelSet);
			}
		} else {
			return new ArrayVoxelShape(field_1385.voxels, new double[]{box.minX, box.maxX}, new double[]{box.minY, box.maxY}, new double[]{box.minZ, box.maxZ});
		}
	}

	private static int findRequiredBitResolution(double d, double e) {
		if (!(d < -1.0E-7) && !(e > 1.0000001)) {
			for (int i = 0; i <= 3; i++) {
				double f = d * (double)(1 << i);
				double g = e * (double)(1 << i);
				boolean bl = Math.abs(f - Math.floor(f)) < 1.0E-7;
				boolean bl2 = Math.abs(g - Math.floor(g)) < 1.0E-7;
				if (bl && bl2) {
					return i;
				}
			}

			return -1;
		} else {
			return -1;
		}
	}

	protected static long lcm(int i, int j) {
		return (long)i * (long)(j / IntMath.gcd(i, j));
	}

	public static VoxelShape method_1084(VoxelShape voxelShape, VoxelShape voxelShape2) {
		return method_1072(voxelShape, voxelShape2, BooleanBiFunction.OR);
	}

	public static VoxelShape method_17786(VoxelShape voxelShape, VoxelShape... voxelShapes) {
		return (VoxelShape)Arrays.stream(voxelShapes).reduce(voxelShape, VoxelShapes::method_1084);
	}

	public static VoxelShape method_1072(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		return method_1082(voxelShape, voxelShape2, booleanBiFunction).simplify();
	}

	public static VoxelShape method_1082(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		if (booleanBiFunction.apply(false, false)) {
			throw new IllegalArgumentException();
		} else if (voxelShape == voxelShape2) {
			return booleanBiFunction.apply(true, true) ? voxelShape : method_1073();
		} else {
			boolean bl = booleanBiFunction.apply(true, false);
			boolean bl2 = booleanBiFunction.apply(false, true);
			if (voxelShape.isEmpty()) {
				return bl2 ? voxelShape2 : method_1073();
			} else if (voxelShape2.isEmpty()) {
				return bl ? voxelShape : method_1073();
			} else {
				DoubleListPair doubleListPair = createListPair(1, voxelShape.getPointPositions(Direction.Axis.X), voxelShape2.getPointPositions(Direction.Axis.X), bl, bl2);
				DoubleListPair doubleListPair2 = createListPair(
					doubleListPair.getMergedList().size() - 1, voxelShape.getPointPositions(Direction.Axis.Y), voxelShape2.getPointPositions(Direction.Axis.Y), bl, bl2
				);
				DoubleListPair doubleListPair3 = createListPair(
					(doubleListPair.getMergedList().size() - 1) * (doubleListPair2.getMergedList().size() - 1),
					voxelShape.getPointPositions(Direction.Axis.Z),
					voxelShape2.getPointPositions(Direction.Axis.Z),
					bl,
					bl2
				);
				BitSetVoxelSet bitSetVoxelSet = BitSetVoxelSet.method_1040(
					voxelShape.voxels, voxelShape2.voxels, doubleListPair, doubleListPair2, doubleListPair3, booleanBiFunction
				);
				return (VoxelShape)(doubleListPair instanceof FractionalDoubleListPair
						&& doubleListPair2 instanceof FractionalDoubleListPair
						&& doubleListPair3 instanceof FractionalDoubleListPair
					? new SimpleVoxelShape(bitSetVoxelSet)
					: new ArrayVoxelShape(bitSetVoxelSet, doubleListPair.getMergedList(), doubleListPair2.getMergedList(), doubleListPair3.getMergedList()));
			}
		}
	}

	public static boolean method_1074(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		if (booleanBiFunction.apply(false, false)) {
			throw new IllegalArgumentException();
		} else if (voxelShape == voxelShape2) {
			return booleanBiFunction.apply(true, true);
		} else if (voxelShape.isEmpty()) {
			return booleanBiFunction.apply(false, !voxelShape2.isEmpty());
		} else if (voxelShape2.isEmpty()) {
			return booleanBiFunction.apply(!voxelShape.isEmpty(), false);
		} else {
			boolean bl = booleanBiFunction.apply(true, false);
			boolean bl2 = booleanBiFunction.apply(false, true);

			for (Direction.Axis axis : AxisCycleDirection.AXES) {
				if (voxelShape.getMaximum(axis) < voxelShape2.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}

				if (voxelShape2.getMaximum(axis) < voxelShape.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}
			}

			DoubleListPair doubleListPair = createListPair(1, voxelShape.getPointPositions(Direction.Axis.X), voxelShape2.getPointPositions(Direction.Axis.X), bl, bl2);
			DoubleListPair doubleListPair2 = createListPair(
				doubleListPair.getMergedList().size() - 1, voxelShape.getPointPositions(Direction.Axis.Y), voxelShape2.getPointPositions(Direction.Axis.Y), bl, bl2
			);
			DoubleListPair doubleListPair3 = createListPair(
				(doubleListPair.getMergedList().size() - 1) * (doubleListPair2.getMergedList().size() - 1),
				voxelShape.getPointPositions(Direction.Axis.Z),
				voxelShape2.getPointPositions(Direction.Axis.Z),
				bl,
				bl2
			);
			return matchesAnywhere(doubleListPair, doubleListPair2, doubleListPair3, voxelShape.voxels, voxelShape2.voxels, booleanBiFunction);
		}
	}

	private static boolean matchesAnywhere(
		DoubleListPair doubleListPair,
		DoubleListPair doubleListPair2,
		DoubleListPair doubleListPair3,
		VoxelSet voxelSet,
		VoxelSet voxelSet2,
		BooleanBiFunction booleanBiFunction
	) {
		return !doubleListPair.forAllOverlappingSections(
			(i, j, k) -> doubleListPair2.forAllOverlappingSections(
					(kx, l, m) -> doubleListPair3.forAllOverlappingSections(
							(mx, n, o) -> !booleanBiFunction.apply(voxelSet.inBoundsAndContains(i, kx, mx), voxelSet2.inBoundsAndContains(j, l, n))
						)
				)
		);
	}

	public static double calculateMaxOffset(Direction.Axis axis, Box box, Stream<VoxelShape> stream, double d) {
		Iterator<VoxelShape> iterator = stream.iterator();

		while (iterator.hasNext()) {
			if (Math.abs(d) < 1.0E-7) {
				return 0.0;
			}

			d = ((VoxelShape)iterator.next()).method_1108(axis, box, d);
		}

		return d;
	}

	public static double method_17945(Direction.Axis axis, Box box, ViewableWorld viewableWorld, double d, EntityContext entityContext, Stream<VoxelShape> stream) {
		return method_17944(box, viewableWorld, d, entityContext, AxisCycleDirection.between(axis, Direction.Axis.Z), stream);
	}

	private static double method_17944(
		Box box, ViewableWorld viewableWorld, double d, EntityContext entityContext, AxisCycleDirection axisCycleDirection, Stream<VoxelShape> stream
	) {
		if (box.getXSize() < 1.0E-6 || box.getYSize() < 1.0E-6 || box.getZSize() < 1.0E-6) {
			return d;
		} else if (Math.abs(d) < 1.0E-7) {
			return 0.0;
		} else {
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
			int n = method_17943(d, e, f);
			int o = bl ? 1 : -1;

			for (int p = m; bl ? p <= n : p >= n; p += o) {
				for (int q = i; q <= j; q++) {
					for (int r = k; r <= l; r++) {
						int s = 0;
						if (q == i || q == j) {
							s++;
						}

						if (r == k || r == l) {
							s++;
						}

						if (p == m || p == n) {
							s++;
						}

						if (s < 3) {
							mutable.method_17965(axisCycleDirection2, q, r, p);
							BlockState blockState = viewableWorld.method_8320(mutable);
							if ((s != 1 || blockState.method_17900()) && (s != 2 || blockState.getBlock() == Blocks.field_10008)) {
								d = blockState.method_16337(viewableWorld, mutable, entityContext)
									.method_1108(axis3, box.offset((double)(-mutable.getX()), (double)(-mutable.getY()), (double)(-mutable.getZ())), d);
								if (Math.abs(d) < 1.0E-7) {
									return 0.0;
								}

								n = method_17943(d, e, f);
							}
						}
					}
				}
			}

			double[] ds = new double[]{d};
			stream.forEach(voxelShape -> ds[0] = voxelShape.method_1108(axis3, box, ds[0]));
			return ds[0];
		}
	}

	private static int method_17943(double d, double e, double f) {
		return d > 0.0 ? MathHelper.floor(f + d) + 1 : MathHelper.floor(e + d) - 1;
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_1083(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape == method_1077() && voxelShape2 == method_1077()) {
			return true;
		} else if (voxelShape2.isEmpty()) {
			return false;
		} else {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getDirection();
			VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
			VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
			BooleanBiFunction booleanBiFunction = axisDirection == Direction.AxisDirection.POSITIVE ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
			return DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)
				&& DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)
				&& !method_1074(new SliceVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape4, axis, 0), booleanBiFunction);
		}
	}

	public static VoxelShape method_16344(VoxelShape voxelShape, Direction direction) {
		if (voxelShape == method_1077()) {
			return method_1077();
		} else {
			Direction.Axis axis = direction.getAxis();
			boolean bl;
			int i;
			if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
				bl = DoubleMath.fuzzyEquals(voxelShape.getMaximum(axis), 1.0, 1.0E-7);
				i = voxelShape.voxels.getSize(axis) - 1;
			} else {
				bl = DoubleMath.fuzzyEquals(voxelShape.getMinimum(axis), 0.0, 1.0E-7);
				i = 0;
			}

			return (VoxelShape)(!bl ? method_1073() : new SliceVoxelShape(voxelShape, axis, i));
		}
	}

	public static boolean method_1080(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape != method_1077() && voxelShape2 != method_1077()) {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getDirection();
			VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
			VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
			if (!DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)) {
				voxelShape3 = method_1073();
			}

			if (!DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)) {
				voxelShape4 = method_1073();
			}

			return !method_1074(
				method_1077(),
				method_1082(new SliceVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape4, axis, 0), BooleanBiFunction.OR),
				BooleanBiFunction.ONLY_FIRST
			);
		} else {
			return true;
		}
	}

	public static boolean method_20713(VoxelShape voxelShape, VoxelShape voxelShape2) {
		if (voxelShape == method_1077() || voxelShape2 == method_1077()) {
			return true;
		} else {
			return voxelShape.isEmpty() && voxelShape2.isEmpty()
				? false
				: !method_1074(method_1077(), method_1082(voxelShape, voxelShape2, BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
		}
	}

	@VisibleForTesting
	protected static DoubleListPair createListPair(int i, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		int j = doubleList.size() - 1;
		int k = doubleList2.size() - 1;
		if (doubleList instanceof FractionalDoubleList && doubleList2 instanceof FractionalDoubleList) {
			long l = lcm(j, k);
			if ((long)i * l <= 256L) {
				return new FractionalDoubleListPair(j, k);
			}
		}

		if (doubleList.getDouble(j) < doubleList2.getDouble(0) - 1.0E-7) {
			return new DisjointDoubleListPair(doubleList, doubleList2, false);
		} else if (doubleList2.getDouble(k) < doubleList.getDouble(0) - 1.0E-7) {
			return new DisjointDoubleListPair(doubleList2, doubleList, true);
		} else if (j != k || !Objects.equals(doubleList, doubleList2)) {
			return new SimpleDoubleListPair(doubleList, doubleList2, bl, bl2);
		} else if (doubleList instanceof IdentityListMerger) {
			return (DoubleListPair)doubleList;
		} else {
			return (DoubleListPair)(doubleList2 instanceof IdentityListMerger ? (DoubleListPair)doubleList2 : new IdentityListMerger(doubleList));
		}
	}

	public interface BoxConsumer {
		void consume(double d, double e, double f, double g, double h, double i);
	}
}
