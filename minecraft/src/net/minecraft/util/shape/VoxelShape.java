package net.minecraft.util.shape;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_251;
import net.minecraft.class_261;
import net.minecraft.class_263;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.HitResult;
import net.minecraft.util.math.AxisCycle;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BoundingBox;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class VoxelShape {
	protected final class_251 shape;

	public VoxelShape(class_251 arg) {
		this.shape = arg;
	}

	public double method_1091(Direction.Axis axis) {
		int i = this.shape.method_1055(axis);
		return i >= this.shape.method_1051(axis) ? Double.POSITIVE_INFINITY : this.method_1099(axis, i);
	}

	public double method_1105(Direction.Axis axis) {
		int i = this.shape.method_1045(axis);
		return i <= 0 ? Double.NEGATIVE_INFINITY : this.method_1099(axis, i);
	}

	public BoundingBox getBoundingBox() {
		if (this.isEmpty()) {
			throw new UnsupportedOperationException("No bounds for empty shape.");
		} else {
			return new BoundingBox(
				this.method_1091(Direction.Axis.X),
				this.method_1091(Direction.Axis.Y),
				this.method_1091(Direction.Axis.Z),
				this.method_1105(Direction.Axis.X),
				this.method_1105(Direction.Axis.Y),
				this.method_1105(Direction.Axis.Z)
			);
		}
	}

	protected double method_1099(Direction.Axis axis, int i) {
		return this.method_1109(axis).getDouble(i);
	}

	protected abstract DoubleList method_1109(Direction.Axis axis);

	public boolean isEmpty() {
		return this.shape.method_1056();
	}

	public VoxelShape method_1096(double d, double e, double f) {
		return (VoxelShape)(this.isEmpty()
			? VoxelShapes.empty()
			: new ArrayVoxelShape(
				this.shape,
				new class_261(this.method_1109(Direction.Axis.X), d),
				new class_261(this.method_1109(Direction.Axis.Y), e),
				new class_261(this.method_1109(Direction.Axis.Z), f)
			));
	}

	public VoxelShape method_1097() {
		VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty()};
		this.method_1089((d, e, f, g, h, i) -> voxelShapes[0] = VoxelShapes.method_1082(voxelShapes[0], VoxelShapes.cube(d, e, f, g, h, i), BooleanBiFunction.OR));
		return voxelShapes[0];
	}

	@Environment(EnvType.CLIENT)
	public void method_1104(VoxelShapes.class_260 arg) {
		this.shape
			.method_1064(
				(i, j, k, l, m, n) -> arg.consume(
						this.method_1099(Direction.Axis.X, i),
						this.method_1099(Direction.Axis.Y, j),
						this.method_1099(Direction.Axis.Z, k),
						this.method_1099(Direction.Axis.X, l),
						this.method_1099(Direction.Axis.Y, m),
						this.method_1099(Direction.Axis.Z, n)
					),
				true
			);
	}

	public void method_1089(VoxelShapes.class_260 arg) {
		this.shape
			.method_1053(
				(i, j, k, l, m, n) -> arg.consume(
						this.method_1099(Direction.Axis.X, i),
						this.method_1099(Direction.Axis.Y, j),
						this.method_1099(Direction.Axis.Z, k),
						this.method_1099(Direction.Axis.X, l),
						this.method_1099(Direction.Axis.Y, m),
						this.method_1099(Direction.Axis.Z, n)
					),
				true
			);
	}

	public List<BoundingBox> getBoundingBoxList() {
		List<BoundingBox> list = Lists.<BoundingBox>newArrayList();
		this.method_1089((d, e, f, g, h, i) -> list.add(new BoundingBox(d, e, f, g, h, i)));
		return list;
	}

	@Environment(EnvType.CLIENT)
	public double method_1093(Direction.Axis axis, double d, double e) {
		Direction.Axis axis2 = AxisCycle.field_10963.method_10058(axis);
		Direction.Axis axis3 = AxisCycle.field_10965.method_10058(axis);
		int i = this.method_1100(axis2, d);
		int j = this.method_1100(axis3, e);
		int k = this.shape.method_1043(axis, i, j);
		return k >= this.shape.method_1051(axis) ? Double.POSITIVE_INFINITY : this.method_1099(axis, k);
	}

	@Environment(EnvType.CLIENT)
	public double method_1102(Direction.Axis axis, double d, double e) {
		Direction.Axis axis2 = AxisCycle.field_10963.method_10058(axis);
		Direction.Axis axis3 = AxisCycle.field_10965.method_10058(axis);
		int i = this.method_1100(axis2, d);
		int j = this.method_1100(axis3, e);
		int k = this.shape.method_1058(axis, i, j);
		return k <= 0 ? Double.NEGATIVE_INFINITY : this.method_1099(axis, k);
	}

	protected int method_1100(Direction.Axis axis, double d) {
		return MathHelper.method_15360(0, this.shape.method_1051(axis) + 1, i -> {
			if (i < 0) {
				return false;
			} else {
				return i > this.shape.method_1051(axis) ? true : d < this.method_1099(axis, i);
			}
		}) - 1;
	}

	protected boolean method_1095(double d, double e, double f) {
		return this.shape.method_1044(this.method_1100(Direction.Axis.X, d), this.method_1100(Direction.Axis.Y, e), this.method_1100(Direction.Axis.Z, f));
	}

	@Nullable
	public HitResult rayTrace(Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos) {
		if (this.isEmpty()) {
			return null;
		} else {
			Vec3d vec3d3 = vec3d2.subtract(vec3d);
			if (vec3d3.lengthSquared() < 1.0E-7) {
				return null;
			} else {
				Vec3d vec3d4 = vec3d.add(vec3d3.multiply(0.001));
				Vec3d vec3d5 = vec3d.add(vec3d3.multiply(0.001)).subtract((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ());
				return this.method_1095(vec3d5.x, vec3d5.y, vec3d5.z)
					? new HitResult(vec3d4, Direction.getFacing(vec3d3.x, vec3d3.y, vec3d3.z), blockPos)
					: BoundingBox.method_1010(this.getBoundingBoxList(), vec3d, vec3d2, blockPos);
			}
		}
	}

	public VoxelShape method_1098(Direction direction) {
		if (!this.isEmpty() && this != VoxelShapes.fullCube()) {
			Direction.Axis axis = direction.getAxis();
			Direction.AxisDirection axisDirection = direction.getDirection();
			DoubleList doubleList = this.method_1109(axis);
			if (doubleList.size() == 2 && DoubleMath.fuzzyEquals(doubleList.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(doubleList.getDouble(1), 1.0, 1.0E-7)) {
				return this;
			} else {
				int i = this.method_1100(axis, axisDirection == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
				return new class_263(this, axis, i);
			}
		} else {
			return this;
		}
	}

	public double method_1108(Direction.Axis axis, BoundingBox boundingBox, double d) {
		return this.method_1103(AxisCycle.method_10057(axis, Direction.Axis.X), boundingBox, d);
	}

	protected double method_1103(AxisCycle axisCycle, BoundingBox boundingBox, double d) {
		if (this.isEmpty()) {
			return d;
		} else if (Math.abs(d) < 1.0E-7) {
			return 0.0;
		} else {
			AxisCycle axisCycle2 = axisCycle.opposite();
			Direction.Axis axis = axisCycle2.method_10058(Direction.Axis.X);
			Direction.Axis axis2 = axisCycle2.method_10058(Direction.Axis.Y);
			Direction.Axis axis3 = axisCycle2.method_10058(Direction.Axis.Z);
			double e = boundingBox.method_990(axis);
			double f = boundingBox.method_1001(axis);
			int i = this.method_1100(axis, f + 1.0E-7);
			int j = this.method_1100(axis, e - 1.0E-7);
			int k = Math.max(0, this.method_1100(axis2, boundingBox.method_1001(axis2) + 1.0E-7));
			int l = Math.min(this.shape.method_1051(axis2), this.method_1100(axis2, boundingBox.method_990(axis2) - 1.0E-7) + 1);
			int m = Math.max(0, this.method_1100(axis3, boundingBox.method_1001(axis3) + 1.0E-7));
			int n = Math.min(this.shape.method_1051(axis3), this.method_1100(axis3, boundingBox.method_990(axis3) - 1.0E-7) + 1);
			int o = this.shape.method_1051(axis);
			if (d > 0.0) {
				for (int p = j + 1; p < o; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							if (this.shape.method_1062(axisCycle2, p, q, r)) {
								double g = this.method_1099(axis, p) - e;
								if (g >= -1.0E-7) {
									d = Math.min(d, g);
								}

								return d;
							}
						}
					}
				}
			} else if (d < 0.0) {
				for (int p = i - 1; p >= 0; p--) {
					for (int q = k; q < l; q++) {
						for (int rx = m; rx < n; rx++) {
							if (this.shape.method_1062(axisCycle2, p, q, rx)) {
								double g = this.method_1099(axis, p + 1) - f;
								if (g <= 1.0E-7) {
									d = Math.max(d, g);
								}

								return d;
							}
						}
					}
				}
			}

			return d;
		}
	}

	public String toString() {
		return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
	}
}
