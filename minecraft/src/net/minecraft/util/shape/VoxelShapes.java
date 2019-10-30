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
import net.minecraft.util.Util;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.WorldView;

public final class VoxelShapes {
	private static final VoxelShape FULL_CUBE = Util.create(() -> {
		VoxelSet voxelSet = new BitSetVoxelSet(1, 1, 1);
		voxelSet.set(0, 0, 0, true, true);
		return new SimpleVoxelShape(voxelSet);
	});
	public static final VoxelShape UNBOUNDED = cuboid(
		Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
	);
	private static final VoxelShape EMPTY = new ArrayVoxelShape(
		new BitSetVoxelSet(0, 0, 0), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0}), new DoubleArrayList(new double[]{0.0})
	);

	public static VoxelShape empty() {
		return EMPTY;
	}

	public static VoxelShape fullCube() {
		return FULL_CUBE;
	}

	public static VoxelShape cuboid(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
		return cuboid(new Box(xMin, yMin, zMin, xMax, yMax, zMax));
	}

	public static VoxelShape cuboid(Box box) {
		int i = findRequiredBitResolution(box.x1, box.x2);
		int j = findRequiredBitResolution(box.y1, box.y2);
		int k = findRequiredBitResolution(box.z1, box.z2);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return box.contains(0.5, 0.5, 0.5) ? fullCube() : empty();
			} else {
				int l = 1 << i;
				int m = 1 << j;
				int n = 1 << k;
				int o = (int)Math.round(box.x1 * (double)l);
				int p = (int)Math.round(box.x2 * (double)l);
				int q = (int)Math.round(box.y1 * (double)m);
				int r = (int)Math.round(box.y2 * (double)m);
				int s = (int)Math.round(box.z1 * (double)n);
				int t = (int)Math.round(box.z2 * (double)n);
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
			return new ArrayVoxelShape(FULL_CUBE.voxels, new double[]{box.x1, box.x2}, new double[]{box.y1, box.y2}, new double[]{box.z1, box.z2});
		}
	}

	private static int findRequiredBitResolution(double min, double max) {
		if (!(min < -1.0E-7) && !(max > 1.0000001)) {
			for (int i = 0; i <= 3; i++) {
				double d = min * (double)(1 << i);
				double e = max * (double)(1 << i);
				boolean bl = Math.abs(d - Math.floor(d)) < 1.0E-7;
				boolean bl2 = Math.abs(e - Math.floor(e)) < 1.0E-7;
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

	public static VoxelShape union(VoxelShape first, VoxelShape second) {
		return combineAndSimplify(first, second, BooleanBiFunction.OR);
	}

	public static VoxelShape union(VoxelShape first, VoxelShape... others) {
		return (VoxelShape)Arrays.stream(others).reduce(first, VoxelShapes::union);
	}

	public static VoxelShape combineAndSimplify(VoxelShape first, VoxelShape second, BooleanBiFunction function) {
		return combine(first, second, function).simplify();
	}

	public static VoxelShape combine(VoxelShape shape1, VoxelShape shape2, BooleanBiFunction function) {
		if (function.apply(false, false)) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException());
		} else if (shape1 == shape2) {
			return function.apply(true, true) ? shape1 : empty();
		} else {
			boolean bl = function.apply(true, false);
			boolean bl2 = function.apply(false, true);
			if (shape1.isEmpty()) {
				return bl2 ? shape2 : empty();
			} else if (shape2.isEmpty()) {
				return bl ? shape1 : empty();
			} else {
				DoubleListPair doubleListPair = createListPair(1, shape1.getPointPositions(Direction.Axis.X), shape2.getPointPositions(Direction.Axis.X), bl, bl2);
				DoubleListPair doubleListPair2 = createListPair(
					doubleListPair.getMergedList().size() - 1, shape1.getPointPositions(Direction.Axis.Y), shape2.getPointPositions(Direction.Axis.Y), bl, bl2
				);
				DoubleListPair doubleListPair3 = createListPair(
					(doubleListPair.getMergedList().size() - 1) * (doubleListPair2.getMergedList().size() - 1),
					shape1.getPointPositions(Direction.Axis.Z),
					shape2.getPointPositions(Direction.Axis.Z),
					bl,
					bl2
				);
				BitSetVoxelSet bitSetVoxelSet = BitSetVoxelSet.combine(shape1.voxels, shape2.voxels, doubleListPair, doubleListPair2, doubleListPair3, function);
				return (VoxelShape)(doubleListPair instanceof FractionalDoubleListPair
						&& doubleListPair2 instanceof FractionalDoubleListPair
						&& doubleListPair3 instanceof FractionalDoubleListPair
					? new SimpleVoxelShape(bitSetVoxelSet)
					: new ArrayVoxelShape(bitSetVoxelSet, doubleListPair.getMergedList(), doubleListPair2.getMergedList(), doubleListPair3.getMergedList()));
			}
		}
	}

	public static boolean matchesAnywhere(VoxelShape shape1, VoxelShape shape2, BooleanBiFunction predicate) {
		if (predicate.apply(false, false)) {
			throw (IllegalArgumentException)Util.throwOrPause(new IllegalArgumentException());
		} else if (shape1 == shape2) {
			return predicate.apply(true, true);
		} else if (shape1.isEmpty()) {
			return predicate.apply(false, !shape2.isEmpty());
		} else if (shape2.isEmpty()) {
			return predicate.apply(!shape1.isEmpty(), false);
		} else {
			boolean bl = predicate.apply(true, false);
			boolean bl2 = predicate.apply(false, true);

			for (Direction.Axis axis : AxisCycleDirection.AXES) {
				if (shape1.getMaximum(axis) < shape2.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}

				if (shape2.getMaximum(axis) < shape1.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}
			}

			DoubleListPair doubleListPair = createListPair(1, shape1.getPointPositions(Direction.Axis.X), shape2.getPointPositions(Direction.Axis.X), bl, bl2);
			DoubleListPair doubleListPair2 = createListPair(
				doubleListPair.getMergedList().size() - 1, shape1.getPointPositions(Direction.Axis.Y), shape2.getPointPositions(Direction.Axis.Y), bl, bl2
			);
			DoubleListPair doubleListPair3 = createListPair(
				(doubleListPair.getMergedList().size() - 1) * (doubleListPair2.getMergedList().size() - 1),
				shape1.getPointPositions(Direction.Axis.Z),
				shape2.getPointPositions(Direction.Axis.Z),
				bl,
				bl2
			);
			return matchesAnywhere(doubleListPair, doubleListPair2, doubleListPair3, shape1.voxels, shape2.voxels, predicate);
		}
	}

	private static boolean matchesAnywhere(
		DoubleListPair mergedX, DoubleListPair mergedY, DoubleListPair mergedZ, VoxelSet shape1, VoxelSet shape2, BooleanBiFunction predicate
	) {
		return !mergedX.forAllOverlappingSections(
			(i, j, k) -> mergedY.forAllOverlappingSections(
					(kx, l, m) -> mergedZ.forAllOverlappingSections((mx, n, o) -> !predicate.apply(shape1.inBoundsAndContains(i, kx, mx), shape2.inBoundsAndContains(j, l, n)))
				)
		);
	}

	public static double calculateMaxOffset(Direction.Axis axis, Box box, Stream<VoxelShape> shapes, double d) {
		Iterator<VoxelShape> iterator = shapes.iterator();

		while (iterator.hasNext()) {
			if (Math.abs(d) < 1.0E-7) {
				return 0.0;
			}

			d = ((VoxelShape)iterator.next()).method_1108(axis, box, d);
		}

		return d;
	}

	public static double method_17945(Direction.Axis axis, Box box, WorldView worldView, double d, EntityContext entityContext, Stream<VoxelShape> stream) {
		return method_17944(box, worldView, d, entityContext, AxisCycleDirection.between(axis, Direction.Axis.Z), stream);
	}

	private static double method_17944(
		Box box, WorldView worldView, double d, EntityContext entityContext, AxisCycleDirection axisCycleDirection, Stream<VoxelShape> stream
	) {
		if (box.getXLength() < 1.0E-6 || box.getYLength() < 1.0E-6 || box.getZLength() < 1.0E-6) {
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
							mutable.set(axisCycleDirection2, q, r, p);
							BlockState blockState = worldView.getBlockState(mutable);
							if ((s != 1 || blockState.method_17900()) && (s != 2 || blockState.getBlock() == Blocks.MOVING_PISTON)) {
								d = blockState.getCollisionShape(worldView, mutable, entityContext)
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
	public static boolean isSideCovered(VoxelShape shape, VoxelShape neighbor, Direction side) {
		if (shape == fullCube() && neighbor == fullCube()) {
			return true;
		} else if (neighbor.isEmpty()) {
			return false;
		} else {
			Direction.Axis axis = side.getAxis();
			Direction.AxisDirection axisDirection = side.getDirection();
			VoxelShape voxelShape = axisDirection == Direction.AxisDirection.POSITIVE ? shape : neighbor;
			VoxelShape voxelShape2 = axisDirection == Direction.AxisDirection.POSITIVE ? neighbor : shape;
			BooleanBiFunction booleanBiFunction = axisDirection == Direction.AxisDirection.POSITIVE ? BooleanBiFunction.ONLY_FIRST : BooleanBiFunction.ONLY_SECOND;
			return DoubleMath.fuzzyEquals(voxelShape.getMaximum(axis), 1.0, 1.0E-7)
				&& DoubleMath.fuzzyEquals(voxelShape2.getMinimum(axis), 0.0, 1.0E-7)
				&& !matchesAnywhere(
					new SliceVoxelShape(voxelShape, axis, voxelShape.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape2, axis, 0), booleanBiFunction
				);
		}
	}

	public static VoxelShape slice(VoxelShape shape, Direction direction) {
		if (shape == fullCube()) {
			return fullCube();
		} else {
			Direction.Axis axis = direction.getAxis();
			boolean bl;
			int i;
			if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
				bl = DoubleMath.fuzzyEquals(shape.getMaximum(axis), 1.0, 1.0E-7);
				i = shape.voxels.getSize(axis) - 1;
			} else {
				bl = DoubleMath.fuzzyEquals(shape.getMinimum(axis), 0.0, 1.0E-7);
				i = 0;
			}

			return (VoxelShape)(!bl ? empty() : new SliceVoxelShape(shape, axis, i));
		}
	}

	public static boolean adjacentSidesCoverSquare(VoxelShape shape1, VoxelShape shape2, Direction direction) {
		if (shape1 != fullCube() && shape2 != fullCube()) {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getDirection();
			VoxelShape voxelShape = axisDirection == Direction.AxisDirection.POSITIVE ? shape1 : shape2;
			VoxelShape voxelShape2 = axisDirection == Direction.AxisDirection.POSITIVE ? shape2 : shape1;
			if (!DoubleMath.fuzzyEquals(voxelShape.getMaximum(axis), 1.0, 1.0E-7)) {
				voxelShape = empty();
			}

			if (!DoubleMath.fuzzyEquals(voxelShape2.getMinimum(axis), 0.0, 1.0E-7)) {
				voxelShape2 = empty();
			}

			return !matchesAnywhere(
				fullCube(),
				combine(new SliceVoxelShape(voxelShape, axis, voxelShape.voxels.getSize(axis) - 1), new SliceVoxelShape(voxelShape2, axis, 0), BooleanBiFunction.OR),
				BooleanBiFunction.ONLY_FIRST
			);
		} else {
			return true;
		}
	}

	public static boolean unionCoversFullCube(VoxelShape shape1, VoxelShape shape2) {
		if (shape1 == fullCube() || shape2 == fullCube()) {
			return true;
		} else {
			return shape1.isEmpty() && shape2.isEmpty()
				? false
				: !matchesAnywhere(fullCube(), combine(shape1, shape2, BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
		}
	}

	@VisibleForTesting
	protected static DoubleListPair createListPair(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond) {
		int i = first.size() - 1;
		int j = second.size() - 1;
		if (first instanceof FractionalDoubleList && second instanceof FractionalDoubleList) {
			long l = lcm(i, j);
			if ((long)size * l <= 256L) {
				return new FractionalDoubleListPair(i, j);
			}
		}

		if (first.getDouble(i) < second.getDouble(0) - 1.0E-7) {
			return new DisjointDoubleListPair(first, second, false);
		} else if (second.getDouble(j) < first.getDouble(0) - 1.0E-7) {
			return new DisjointDoubleListPair(second, first, true);
		} else if (i != j || !Objects.equals(first, second)) {
			return new SimpleDoubleListPair(first, second, includeFirst, includeSecond);
		} else if (first instanceof IdentityListMerger) {
			return (DoubleListPair)first;
		} else {
			return (DoubleListPair)(second instanceof IdentityListMerger ? (DoubleListPair)second : new IdentityListMerger(first));
		}
	}

	public interface BoxConsumer {
		void consume(double minX, double d, double e, double f, double g, double h);
	}
}
