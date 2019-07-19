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
import net.minecraft.world.CollisionView;

public final class VoxelShapes {
	private static final VoxelShape FULL_CUBE = Util.make(() -> {
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

	protected static long lcm(int a, int b) {
		return (long)a * (long)(b / IntMath.gcd(a, b));
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

	public static VoxelShape combine(VoxelShape one, VoxelShape two, BooleanBiFunction function) {
		if (function.apply(false, false)) {
			throw new IllegalArgumentException();
		} else if (one == two) {
			return function.apply(true, true) ? one : empty();
		} else {
			boolean bl = function.apply(true, false);
			boolean bl2 = function.apply(false, true);
			if (one.isEmpty()) {
				return bl2 ? two : empty();
			} else if (two.isEmpty()) {
				return bl ? one : empty();
			} else {
				PairList pairList = createListPair(1, one.getPointPositions(Direction.Axis.X), two.getPointPositions(Direction.Axis.X), bl, bl2);
				PairList pairList2 = createListPair(
					pairList.getPairs().size() - 1, one.getPointPositions(Direction.Axis.Y), two.getPointPositions(Direction.Axis.Y), bl, bl2
				);
				PairList pairList3 = createListPair(
					(pairList.getPairs().size() - 1) * (pairList2.getPairs().size() - 1),
					one.getPointPositions(Direction.Axis.Z),
					two.getPointPositions(Direction.Axis.Z),
					bl,
					bl2
				);
				BitSetVoxelSet bitSetVoxelSet = BitSetVoxelSet.combine(one.voxels, two.voxels, pairList, pairList2, pairList3, function);
				return (VoxelShape)(pairList instanceof FractionalPairList && pairList2 instanceof FractionalPairList && pairList3 instanceof FractionalPairList
					? new SimpleVoxelShape(bitSetVoxelSet)
					: new ArrayVoxelShape(bitSetVoxelSet, pairList.getPairs(), pairList2.getPairs(), pairList3.getPairs()));
			}
		}
	}

	public static boolean matchesAnywhere(VoxelShape shape1, VoxelShape shape2, BooleanBiFunction predicate) {
		if (predicate.apply(false, false)) {
			throw new IllegalArgumentException();
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

			PairList pairList = createListPair(1, shape1.getPointPositions(Direction.Axis.X), shape2.getPointPositions(Direction.Axis.X), bl, bl2);
			PairList pairList2 = createListPair(
				pairList.getPairs().size() - 1, shape1.getPointPositions(Direction.Axis.Y), shape2.getPointPositions(Direction.Axis.Y), bl, bl2
			);
			PairList pairList3 = createListPair(
				(pairList.getPairs().size() - 1) * (pairList2.getPairs().size() - 1),
				shape1.getPointPositions(Direction.Axis.Z),
				shape2.getPointPositions(Direction.Axis.Z),
				bl,
				bl2
			);
			return matchesAnywhere(pairList, pairList2, pairList3, shape1.voxels, shape2.voxels, predicate);
		}
	}

	private static boolean matchesAnywhere(PairList mergedX, PairList mergedY, PairList mergedZ, VoxelSet shape1, VoxelSet shape2, BooleanBiFunction predicate) {
		return !mergedX.forEachPair(
			(i, j, k) -> mergedY.forEachPair(
					(kx, l, m) -> mergedZ.forEachPair((mx, n, o) -> !predicate.apply(shape1.inBoundsAndContains(i, kx, mx), shape2.inBoundsAndContains(j, l, n)))
				)
		);
	}

	public static double calculateMaxOffset(Direction.Axis axis, Box box, Stream<VoxelShape> shapes, double maxDist) {
		Iterator<VoxelShape> iterator = shapes.iterator();

		while (iterator.hasNext()) {
			if (Math.abs(maxDist) < 1.0E-7) {
				return 0.0;
			}

			maxDist = ((VoxelShape)iterator.next()).calculateMaxDistance(axis, box, maxDist);
		}

		return maxDist;
	}

	public static double calculateSoftOffset(
		Direction.Axis axis, Box box, CollisionView collisionView, double d, EntityContext entityContext, Stream<VoxelShape> stream
	) {
		return method_17944(box, collisionView, d, entityContext, AxisCycleDirection.between(axis, Direction.Axis.Z), stream);
	}

	private static double method_17944(
		Box box, CollisionView collisionView, double d, EntityContext entityContext, AxisCycleDirection axisCycleDirection, Stream<VoxelShape> stream
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
							BlockState blockState = collisionView.getBlockState(mutable);
							if ((s != 1 || blockState.method_17900()) && (s != 2 || blockState.getBlock() == Blocks.MOVING_PISTON)) {
								d = blockState.getCollisionShape(collisionView, mutable, entityContext)
									.calculateMaxDistance(axis3, box.offset((double)(-mutable.getX()), (double)(-mutable.getY()), (double)(-mutable.getZ())), d);
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
			stream.forEach(voxelShape -> ds[0] = voxelShape.calculateMaxDistance(axis3, box, ds[0]));
			return ds[0];
		}
	}

	private static int method_17943(double d, double e, double f) {
		return d > 0.0 ? MathHelper.floor(f + d) + 1 : MathHelper.floor(e + d) - 1;
	}

	@Environment(EnvType.CLIENT)
	public static boolean method_1083(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape == fullCube() && voxelShape2 == fullCube()) {
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
				&& !matchesAnywhere(
					new SlicedVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SlicedVoxelShape(voxelShape4, axis, 0), booleanBiFunction
				);
		}
	}

	public static VoxelShape method_16344(VoxelShape voxelShape, Direction direction) {
		if (voxelShape == fullCube()) {
			return fullCube();
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

			return (VoxelShape)(!bl ? empty() : new SlicedVoxelShape(voxelShape, axis, i));
		}
	}

	public static boolean method_1080(VoxelShape voxelShape, VoxelShape voxelShape2, Direction direction) {
		if (voxelShape != fullCube() && voxelShape2 != fullCube()) {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getDirection();
			VoxelShape voxelShape3 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape : voxelShape2;
			VoxelShape voxelShape4 = axisDirection == Direction.AxisDirection.POSITIVE ? voxelShape2 : voxelShape;
			if (!DoubleMath.fuzzyEquals(voxelShape3.getMaximum(axis), 1.0, 1.0E-7)) {
				voxelShape3 = empty();
			}

			if (!DoubleMath.fuzzyEquals(voxelShape4.getMinimum(axis), 0.0, 1.0E-7)) {
				voxelShape4 = empty();
			}

			return !matchesAnywhere(
				fullCube(),
				combine(new SlicedVoxelShape(voxelShape3, axis, voxelShape3.voxels.getSize(axis) - 1), new SlicedVoxelShape(voxelShape4, axis, 0), BooleanBiFunction.OR),
				BooleanBiFunction.ONLY_FIRST
			);
		} else {
			return true;
		}
	}

	public static boolean method_20713(VoxelShape voxelShape, VoxelShape voxelShape2) {
		if (voxelShape == fullCube() || voxelShape2 == fullCube()) {
			return true;
		} else {
			return voxelShape.isEmpty() && voxelShape2.isEmpty()
				? false
				: !matchesAnywhere(fullCube(), combine(voxelShape, voxelShape2, BooleanBiFunction.OR), BooleanBiFunction.ONLY_FIRST);
		}
	}

	@VisibleForTesting
	protected static PairList createListPair(int size, DoubleList first, DoubleList second, boolean includeFirst, boolean includeSecond) {
		int i = first.size() - 1;
		int j = second.size() - 1;
		if (first instanceof FractionalDoubleList && second instanceof FractionalDoubleList) {
			long l = lcm(i, j);
			if ((long)size * l <= 256L) {
				return new FractionalPairList(i, j);
			}
		}

		if (first.getDouble(i) < second.getDouble(0) - 1.0E-7) {
			return new DisjointPairList(first, second, false);
		} else if (second.getDouble(j) < first.getDouble(0) - 1.0E-7) {
			return new DisjointPairList(second, first, true);
		} else if (i != j || !Objects.equals(first, second)) {
			return new SimplePairList(first, second, includeFirst, includeSecond);
		} else if (first instanceof IdentityPairList) {
			return (PairList)first;
		} else {
			return (PairList)(second instanceof IdentityPairList ? (PairList)second : new IdentityPairList(first));
		}
	}

	public interface BoxConsumer {
		void consume(double minX, double d, double e, double f, double g, double h);
	}
}
