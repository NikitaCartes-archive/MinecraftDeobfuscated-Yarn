package net.minecraft.util.shape;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.math.DoubleMath;
import com.google.common.math.IntMath;
import it.unimi.dsi.fastutil.doubles.DoubleArrayList;
import it.unimi.dsi.fastutil.doubles.DoubleList;
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
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.SystemUtil;
import net.minecraft.util.math.AxisCycle;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;

public final class VoxelShapes {
	private static final VoxelShape EMPTY = new ArrayVoxelShape(
		new BitSetVoxelShapeContainer(0, 0, 0),
		new DoubleArrayList(new double[]{0.0}),
		new DoubleArrayList(new double[]{0.0}),
		new DoubleArrayList(new double[]{0.0})
	);
	private static final VoxelShape FULL_CUBE = SystemUtil.get(() -> {
		AbstractVoxelShapeContainer abstractVoxelShapeContainer = new BitSetVoxelShapeContainer(1, 1, 1);
		abstractVoxelShapeContainer.modify(0, 0, 0, true, true);
		return new SimpleVoxelShape(abstractVoxelShapeContainer);
	});

	public static VoxelShape empty() {
		return EMPTY;
	}

	public static VoxelShape fullCube() {
		return FULL_CUBE;
	}

	public static VoxelShape cube(double d, double e, double f, double g, double h, double i) {
		return cube(new BoundingBox(d, e, f, g, h, i));
	}

	public static VoxelShape cube(BoundingBox boundingBox) {
		int i = findRequiredBitResolution(boundingBox.minX, boundingBox.maxX);
		int j = findRequiredBitResolution(boundingBox.minY, boundingBox.maxY);
		int k = findRequiredBitResolution(boundingBox.minZ, boundingBox.maxZ);
		if (i >= 0 && j >= 0 && k >= 0) {
			if (i == 0 && j == 0 && k == 0) {
				return boundingBox.contains(0.5, 0.5, 0.5) ? fullCube() : empty();
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
				FULL_CUBE.shape,
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

	public static VoxelShape union(VoxelShape voxelShape, VoxelShape voxelShape2) {
		return combine(voxelShape, voxelShape2, BooleanBiFunction.OR);
	}

	public static VoxelShape combine(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		return method_1082(voxelShape, voxelShape2, booleanBiFunction).method_1097();
	}

	public static VoxelShape method_1082(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
		if (booleanBiFunction.apply(false, false)) {
			throw new IllegalArgumentException();
		} else if (voxelShape == voxelShape2) {
			return booleanBiFunction.apply(true, true) ? voxelShape : empty();
		} else {
			boolean bl = booleanBiFunction.apply(true, false);
			boolean bl2 = booleanBiFunction.apply(false, true);
			if (voxelShape.isEmpty()) {
				return bl2 ? voxelShape2 : empty();
			} else if (voxelShape2.isEmpty()) {
				return bl ? voxelShape : empty();
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

	public static boolean compareShapes(VoxelShape voxelShape, VoxelShape voxelShape2, BooleanBiFunction booleanBiFunction) {
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
				&& !compareShapes(new class_263(voxelShape3, axis, voxelShape3.shape.getSize(axis) - 1), new class_263(voxelShape4, axis, 0), booleanBiFunction);
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

		return (VoxelShape)(!bl ? empty() : new class_263(voxelShape, axis, i));
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

			return !compareShapes(
				fullCube(),
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
