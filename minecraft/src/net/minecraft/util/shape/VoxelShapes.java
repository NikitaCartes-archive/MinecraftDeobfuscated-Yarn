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
import net.minecraft.class_248;
import net.minecraft.class_250;
import net.minecraft.class_254;
import net.minecraft.class_255;
import net.minecraft.class_257;
import net.minecraft.class_263;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.VerticalEntityPosition;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.AxisCycle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.ViewableWorld;
import net.minecraft.world.chunk.Chunk;

public final class VoxelShapes {
	private static final VoxelShape field_1385 = SystemUtil.get(() -> {
		AbstractVoxelShapeContainer abstractVoxelShapeContainer = new BitSetVoxelShapeContainer(1, 1, 1);
		abstractVoxelShapeContainer.modify(0, 0, 0, true, true);
		return new SimpleVoxelShape(abstractVoxelShapeContainer);
	});
	public static final VoxelShape field_17669 = method_1081(
		Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY
	);
	private static final VoxelShape field_1384 = new ArrayVoxelShape(
		new BitSetVoxelShapeContainer(0, 0, 0),
		new DoubleArrayList(new double[]{0.0}),
		new DoubleArrayList(new double[]{0.0}),
		new DoubleArrayList(new double[]{0.0})
	);

	public static VoxelShape method_1073() {
		return field_1384;
	}

	public static VoxelShape method_1077() {
		return field_1385;
	}

	public static VoxelShape method_1081(double d, double e, double f, double g, double h, double i) {
		return method_1078(new BoundingBox(d, e, f, g, h, i));
	}

	public static VoxelShape method_1078(BoundingBox boundingBox) {
		int i = findRequiredBitResolution(boundingBox.minX, boundingBox.maxX);
		int j = findRequiredBitResolution(boundingBox.minY, boundingBox.maxY);
		int k = findRequiredBitResolution(boundingBox.minZ, boundingBox.maxZ);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return boundingBox.contains(0.5, 0.5, 0.5) ? method_1077() : method_1073();
			} else {
				int l = 1 << i;
				int m = 1 << j;
				int n = 1 << k;
				int o = (int)Math.round(boundingBox.minX * (double)l);
				int p = (int)Math.round(boundingBox.maxX * (double)l);
				int q = (int)Math.round(boundingBox.minY * (double)m);
				int r = (int)Math.round(boundingBox.maxY * (double)m);
				int s = (int)Math.round(boundingBox.minZ * (double)n);
				int t = (int)Math.round(boundingBox.maxZ * (double)n);
				BitSetVoxelShapeContainer bitSetVoxelShapeContainer = new BitSetVoxelShapeContainer(l, m, n, o, q, s, p, r, t);

				for (long u = (long)o; u < (long)p; u++) {
					for (long v = (long)q; v < (long)r; v++) {
						for (long w = (long)s; w < (long)t; w++) {
							bitSetVoxelShapeContainer.modify((int)u, (int)v, (int)w, false, true);
						}
					}
				}

				return new SimpleVoxelShape(bitSetVoxelShapeContainer);
			}
		} else {
			return new ArrayVoxelShape(
				field_1385.shape,
				new double[]{boundingBox.minX, boundingBox.maxX},
				new double[]{boundingBox.minY, boundingBox.maxY},
				new double[]{boundingBox.minZ, boundingBox.maxZ}
			);
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

	public static long lcm(int i, int j) {
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
				class_255 lv = method_1069(1, voxelShape.getIncludedPoints(Direction.Axis.X), voxelShape2.getIncludedPoints(Direction.Axis.X), bl, bl2);
				class_255 lv2 = method_1069(
					lv.method_1066().size() - 1, voxelShape.getIncludedPoints(Direction.Axis.Y), voxelShape2.getIncludedPoints(Direction.Axis.Y), bl, bl2
				);
				class_255 lv3 = method_1069(
					(lv.method_1066().size() - 1) * (lv2.method_1066().size() - 1),
					voxelShape.getIncludedPoints(Direction.Axis.Z),
					voxelShape2.getIncludedPoints(Direction.Axis.Z),
					bl,
					bl2
				);
				BitSetVoxelShapeContainer bitSetVoxelShapeContainer = BitSetVoxelShapeContainer.method_1040(
					voxelShape.shape, voxelShape2.shape, lv, lv2, lv3, booleanBiFunction
				);
				return (VoxelShape)(lv instanceof class_248 && lv2 instanceof class_248 && lv3 instanceof class_248
					? new SimpleVoxelShape(bitSetVoxelShapeContainer)
					: new ArrayVoxelShape(bitSetVoxelShapeContainer, lv.method_1066(), lv2.method_1066(), lv3.method_1066()));
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

			for (Direction.Axis axis : AxisCycle.AXES) {
				if (voxelShape.getMaximum(axis) < voxelShape2.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}

				if (voxelShape2.getMaximum(axis) < voxelShape.getMinimum(axis) - 1.0E-7) {
					return bl || bl2;
				}
			}

			class_255 lv = method_1069(1, voxelShape.getIncludedPoints(Direction.Axis.X), voxelShape2.getIncludedPoints(Direction.Axis.X), bl, bl2);
			class_255 lv2 = method_1069(
				lv.method_1066().size() - 1, voxelShape.getIncludedPoints(Direction.Axis.Y), voxelShape2.getIncludedPoints(Direction.Axis.Y), bl, bl2
			);
			class_255 lv3 = method_1069(
				(lv.method_1066().size() - 1) * (lv2.method_1066().size() - 1),
				voxelShape.getIncludedPoints(Direction.Axis.Z),
				voxelShape2.getIncludedPoints(Direction.Axis.Z),
				bl,
				bl2
			);
			return method_1071(lv, lv2, lv3, voxelShape.shape, voxelShape2.shape, booleanBiFunction);
		}
	}

	private static boolean method_1071(
		class_255 arg,
		class_255 arg2,
		class_255 arg3,
		AbstractVoxelShapeContainer abstractVoxelShapeContainer,
		AbstractVoxelShapeContainer abstractVoxelShapeContainer2,
		BooleanBiFunction booleanBiFunction
	) {
		return !arg.method_1065(
			(i, j, k) -> arg2.method_1065(
					(kx, l, m) -> arg3.method_1065(
							(mx, n, o) -> !booleanBiFunction.apply(abstractVoxelShapeContainer.method_1044(i, kx, mx), abstractVoxelShapeContainer2.method_1044(j, l, n))
						)
				)
		);
	}

	public static double calculateMaxOffset(Direction.Axis axis, BoundingBox boundingBox, Stream<VoxelShape> stream, double d) {
		Iterator<VoxelShape> iterator = stream.iterator();

		while (iterator.hasNext()) {
			if (Math.abs(d) < 1.0E-7) {
				return 0.0;
			}

			d = ((VoxelShape)iterator.next()).method_1108(axis, boundingBox, d);
		}

		return d;
	}

	public static double method_17945(
		Direction.Axis axis, BoundingBox boundingBox, ViewableWorld viewableWorld, double d, VerticalEntityPosition verticalEntityPosition, Stream<VoxelShape> stream
	) {
		return method_17944(boundingBox, viewableWorld, d, verticalEntityPosition, AxisCycle.between(axis, Direction.Axis.Z), stream);
	}

	private static double method_17944(
		BoundingBox boundingBox, ViewableWorld viewableWorld, double d, VerticalEntityPosition verticalEntityPosition, AxisCycle axisCycle, Stream<VoxelShape> stream
	) {
		if (boundingBox.getXSize() < 1.0E-6 || boundingBox.getYSize() < 1.0E-6 || boundingBox.getZSize() < 1.0E-6) {
			return d;
		} else if (Math.abs(d) < 1.0E-7) {
			return 0.0;
		} else {
			AxisCycle axisCycle2 = axisCycle.opposite();
			Direction.Axis axis = axisCycle2.cycle(Direction.Axis.X);
			Direction.Axis axis2 = axisCycle2.cycle(Direction.Axis.Y);
			Direction.Axis axis3 = axisCycle2.cycle(Direction.Axis.Z);
			BlockPos.Mutable mutable = new BlockPos.Mutable();
			int i = MathHelper.floor(boundingBox.method_1001(axis) - 1.0E-7) - 1;
			int j = MathHelper.floor(boundingBox.method_990(axis) + 1.0E-7) + 1;
			int k = MathHelper.floor(boundingBox.method_1001(axis2) - 1.0E-7) - 1;
			int l = MathHelper.floor(boundingBox.method_990(axis2) + 1.0E-7) + 1;
			double e = boundingBox.method_1001(axis3) - 1.0E-7;
			double f = boundingBox.method_990(axis3) + 1.0E-7;
			boolean bl = d > 0.0;
			int m = bl ? MathHelper.floor(boundingBox.method_990(axis3) - 1.0E-7) - 1 : MathHelper.floor(boundingBox.method_1001(axis3) + 1.0E-7) + 1;
			int n = method_17943(d, e, f);
			int o = bl ? 1 : -1;
			int p = Integer.MAX_VALUE;
			int q = Integer.MAX_VALUE;
			Chunk chunk = null;

			for (int r = m; bl ? r <= n : r >= n; r += o) {
				for (int s = i; s <= j; s++) {
					for (int t = k; t <= l; t++) {
						int u = 0;
						if (s == i || s == j) {
							u++;
						}

						if (t == k || t == l) {
							u++;
						}

						if (r == m || r == n) {
							u++;
						}

						if (u < 3) {
							mutable.method_17965(axisCycle2, s, t, r);
							int v = mutable.getX() >> 4;
							int w = mutable.getZ() >> 4;
							if (v != p || w != q) {
								chunk = viewableWorld.method_8392(v, w);
								p = v;
								q = w;
							}

							BlockState blockState = chunk.method_8320(mutable);
							if ((u != 1 || blockState.method_17900()) && (u != 2 || blockState.getBlock() == Blocks.field_10008)) {
								d = blockState.method_16337(viewableWorld, mutable, verticalEntityPosition)
									.method_1108(axis3, boundingBox.offset((double)(-mutable.getX()), (double)(-mutable.getY()), (double)(-mutable.getZ())), d);
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
			stream.forEach(voxelShape -> ds[0] = voxelShape.method_1108(axis3, boundingBox, ds[0]));
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
				&& !method_1074(new class_263(voxelShape3, axis, voxelShape3.shape.getSize(axis) - 1), new class_263(voxelShape4, axis, 0), booleanBiFunction);
		}
	}

	public static VoxelShape method_16344(VoxelShape voxelShape, Direction direction) {
		Direction.Axis axis = direction.getAxis();
		boolean bl;
		int i;
		if (direction.getDirection() == Direction.AxisDirection.POSITIVE) {
			bl = DoubleMath.fuzzyEquals(voxelShape.getMaximum(axis), 1.0, 1.0E-7);
			i = voxelShape.shape.getSize(axis) - 1;
		} else {
			bl = DoubleMath.fuzzyEquals(voxelShape.getMinimum(axis), 0.0, 1.0E-7);
			i = 0;
		}

		return (VoxelShape)(!bl ? method_1073() : new class_263(voxelShape, axis, i));
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
				method_1082(new class_263(voxelShape3, axis, voxelShape3.shape.getSize(axis) - 1), new class_263(voxelShape4, axis, 0), BooleanBiFunction.OR),
				BooleanBiFunction.ONLY_FIRST
			);
		} else {
			return true;
		}
	}

	@VisibleForTesting
	protected static class_255 method_1069(int i, DoubleList doubleList, DoubleList doubleList2, boolean bl, boolean bl2) {
		int j = doubleList.size() - 1;
		int k = doubleList2.size() - 1;
		if (doubleList instanceof FractionalDoubleList && doubleList2 instanceof FractionalDoubleList) {
			long l = lcm(j, k);
			if ((long)i * l <= 256L) {
				return new class_248(j, k);
			}
		}

		if (doubleList.getDouble(j) < doubleList2.getDouble(0) - 1.0E-7) {
			return new class_257(doubleList, doubleList2, false);
		} else if (doubleList2.getDouble(k) < doubleList.getDouble(0) - 1.0E-7) {
			return new class_257(doubleList2, doubleList, true);
		} else if (j != k || !Objects.equals(doubleList, doubleList2)) {
			return new class_254(doubleList, doubleList2, bl, bl2);
		} else if (doubleList instanceof class_250) {
			return (class_255)doubleList;
		} else {
			return (class_255)(doubleList2 instanceof class_250 ? (class_255)doubleList2 : new class_250(doubleList));
		}
	}

	public interface ShapeConsumer {
		void consume(double d, double e, double f, double g, double h, double i);
	}
}
