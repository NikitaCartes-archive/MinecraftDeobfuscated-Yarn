package net.minecraft.util.shape;

import com.google.common.collect.Lists;
import com.google.common.math.DoubleMath;
import it.unimi.dsi.fastutil.doubles.DoubleList;
import java.util.List;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.BooleanBiFunction;
import net.minecraft.util.OffsetDoubleList;
import net.minecraft.util.Util;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.AxisCycleDirection;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public abstract class VoxelShape {
	protected final VoxelSet voxels;
	@Nullable
	private VoxelShape[] shapeCache;

	VoxelShape(VoxelSet voxels) {
		this.voxels = voxels;
	}

	public double getMinimum(Direction.Axis axis) {
		int i = this.voxels.getMin(axis);
		return i >= this.voxels.getSize(axis) ? Double.POSITIVE_INFINITY : this.getPointPosition(axis, i);
	}

	public double getMaximum(Direction.Axis axis) {
		int i = this.voxels.getMax(axis);
		return i <= 0 ? Double.NEGATIVE_INFINITY : this.getPointPosition(axis, i);
	}

	public Box getBoundingBox() {
		if (this.isEmpty()) {
			throw (UnsupportedOperationException)Util.throwOrPause(new UnsupportedOperationException("No bounds for empty shape."));
		} else {
			return new Box(
				this.getMinimum(Direction.Axis.X),
				this.getMinimum(Direction.Axis.Y),
				this.getMinimum(Direction.Axis.Z),
				this.getMaximum(Direction.Axis.X),
				this.getMaximum(Direction.Axis.Y),
				this.getMaximum(Direction.Axis.Z)
			);
		}
	}

	protected double getPointPosition(Direction.Axis axis, int index) {
		return this.getPointPositions(axis).getDouble(index);
	}

	protected abstract DoubleList getPointPositions(Direction.Axis axis);

	public boolean isEmpty() {
		return this.voxels.isEmpty();
	}

	public VoxelShape offset(double x, double y, double z) {
		return (VoxelShape)(this.isEmpty()
			? VoxelShapes.empty()
			: new ArrayVoxelShape(
				this.voxels,
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.X), x),
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.Y), y),
				new OffsetDoubleList(this.getPointPositions(Direction.Axis.Z), z)
			));
	}

	public VoxelShape simplify() {
		VoxelShape[] voxelShapes = new VoxelShape[]{VoxelShapes.empty()};
		this.forEachBox((d, e, f, g, h, i) -> voxelShapes[0] = VoxelShapes.combine(voxelShapes[0], VoxelShapes.cuboid(d, e, f, g, h, i), BooleanBiFunction.OR));
		return voxelShapes[0];
	}

	@Environment(EnvType.CLIENT)
	public void forEachEdge(VoxelShapes.BoxConsumer boxConsumer) {
		this.voxels
			.forEachEdge(
				(i, j, k, l, m, n) -> boxConsumer.consume(
						this.getPointPosition(Direction.Axis.X, i),
						this.getPointPosition(Direction.Axis.Y, j),
						this.getPointPosition(Direction.Axis.Z, k),
						this.getPointPosition(Direction.Axis.X, l),
						this.getPointPosition(Direction.Axis.Y, m),
						this.getPointPosition(Direction.Axis.Z, n)
					),
				true
			);
	}

	public void forEachBox(VoxelShapes.BoxConsumer boxConsumer) {
		DoubleList doubleList = this.getPointPositions(Direction.Axis.X);
		DoubleList doubleList2 = this.getPointPositions(Direction.Axis.Y);
		DoubleList doubleList3 = this.getPointPositions(Direction.Axis.Z);
		this.voxels
			.forEachBox(
				(i, j, k, l, m, n) -> boxConsumer.consume(
						doubleList.getDouble(i), doubleList2.getDouble(j), doubleList3.getDouble(k), doubleList.getDouble(l), doubleList2.getDouble(m), doubleList3.getDouble(n)
					),
				true
			);
	}

	public List<Box> getBoundingBoxes() {
		List<Box> list = Lists.<Box>newArrayList();
		this.forEachBox((d, e, f, g, h, i) -> list.add(new Box(d, e, f, g, h, i)));
		return list;
	}

	@Environment(EnvType.CLIENT)
	public double getBeginningCoord(Direction.Axis axis, double from, double to) {
		Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
		Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
		int i = this.getCoordIndex(axis2, from);
		int j = this.getCoordIndex(axis3, to);
		int k = this.voxels.getBeginningAxisCoord(axis, i, j);
		return k >= this.voxels.getSize(axis) ? Double.POSITIVE_INFINITY : this.getPointPosition(axis, k);
	}

	@Environment(EnvType.CLIENT)
	public double getEndingCoord(Direction.Axis axis, double from, double to) {
		Direction.Axis axis2 = AxisCycleDirection.FORWARD.cycle(axis);
		Direction.Axis axis3 = AxisCycleDirection.BACKWARD.cycle(axis);
		int i = this.getCoordIndex(axis2, from);
		int j = this.getCoordIndex(axis3, to);
		int k = this.voxels.getEndingAxisCoord(axis, i, j);
		return k <= 0 ? Double.NEGATIVE_INFINITY : this.getPointPosition(axis, k);
	}

	protected int getCoordIndex(Direction.Axis axis, double coord) {
		return MathHelper.binarySearch(0, this.voxels.getSize(axis) + 1, i -> {
			if (i < 0) {
				return false;
			} else {
				return i > this.voxels.getSize(axis) ? true : coord < this.getPointPosition(axis, i);
			}
		}) - 1;
	}

	protected boolean contains(double x, double y, double z) {
		return this.voxels
			.inBoundsAndContains(this.getCoordIndex(Direction.Axis.X, x), this.getCoordIndex(Direction.Axis.Y, y), this.getCoordIndex(Direction.Axis.Z, z));
	}

	@Nullable
	public BlockHitResult rayTrace(Vec3d start, Vec3d end, BlockPos pos) {
		if (this.isEmpty()) {
			return null;
		} else {
			Vec3d vec3d = end.subtract(start);
			if (vec3d.lengthSquared() < 1.0E-7) {
				return null;
			} else {
				Vec3d vec3d2 = start.add(vec3d.multiply(0.001));
				return this.contains(vec3d2.x - (double)pos.getX(), vec3d2.y - (double)pos.getY(), vec3d2.z - (double)pos.getZ())
					? new BlockHitResult(vec3d2, Direction.getFacing(vec3d.x, vec3d.y, vec3d.z).getOpposite(), pos, true)
					: Box.rayTrace(this.getBoundingBoxes(), start, end, pos);
			}
		}
	}

	public VoxelShape getFace(Direction facing) {
		if (!this.isEmpty() && this != VoxelShapes.fullCube()) {
			if (this.shapeCache != null) {
				VoxelShape voxelShape = this.shapeCache[facing.ordinal()];
				if (voxelShape != null) {
					return voxelShape;
				}
			} else {
				this.shapeCache = new VoxelShape[6];
			}

			VoxelShape voxelShape = this.getUncachedFace(facing);
			this.shapeCache[facing.ordinal()] = voxelShape;
			return voxelShape;
		} else {
			return this;
		}
	}

	private VoxelShape getUncachedFace(Direction facing) {
		Direction.Axis axis = facing.getAxis();
		Direction.AxisDirection axisDirection = facing.getDirection();
		DoubleList doubleList = this.getPointPositions(axis);
		if (doubleList.size() == 2 && DoubleMath.fuzzyEquals(doubleList.getDouble(0), 0.0, 1.0E-7) && DoubleMath.fuzzyEquals(doubleList.getDouble(1), 1.0, 1.0E-7)) {
			return this;
		} else {
			int i = this.getCoordIndex(axis, axisDirection == Direction.AxisDirection.POSITIVE ? 0.9999999 : 1.0E-7);
			return new SlicedVoxelShape(this, axis, i);
		}
	}

	public double calculateMaxDistance(Direction.Axis axis, Box box, double maxDist) {
		return this.calculateMaxDistance(AxisCycleDirection.between(axis, Direction.Axis.X), box, maxDist);
	}

	protected double calculateMaxDistance(AxisCycleDirection axisCycle, Box box, double maxDist) {
		if (this.isEmpty()) {
			return maxDist;
		} else if (Math.abs(maxDist) < 1.0E-7) {
			return 0.0;
		} else {
			AxisCycleDirection axisCycleDirection = axisCycle.opposite();
			Direction.Axis axis = axisCycleDirection.cycle(Direction.Axis.X);
			Direction.Axis axis2 = axisCycleDirection.cycle(Direction.Axis.Y);
			Direction.Axis axis3 = axisCycleDirection.cycle(Direction.Axis.Z);
			double d = box.getMax(axis);
			double e = box.getMin(axis);
			int i = this.getCoordIndex(axis, e + 1.0E-7);
			int j = this.getCoordIndex(axis, d - 1.0E-7);
			int k = Math.max(0, this.getCoordIndex(axis2, box.getMin(axis2) + 1.0E-7));
			int l = Math.min(this.voxels.getSize(axis2), this.getCoordIndex(axis2, box.getMax(axis2) - 1.0E-7) + 1);
			int m = Math.max(0, this.getCoordIndex(axis3, box.getMin(axis3) + 1.0E-7));
			int n = Math.min(this.voxels.getSize(axis3), this.getCoordIndex(axis3, box.getMax(axis3) - 1.0E-7) + 1);
			int o = this.voxels.getSize(axis);
			if (maxDist > 0.0) {
				for (int p = j + 1; p < o; p++) {
					for (int q = k; q < l; q++) {
						for (int r = m; r < n; r++) {
							if (this.voxels.inBoundsAndContains(axisCycleDirection, p, q, r)) {
								double f = this.getPointPosition(axis, p) - d;
								if (f >= -1.0E-7) {
									maxDist = Math.min(maxDist, f);
								}

								return maxDist;
							}
						}
					}
				}
			} else if (maxDist < 0.0) {
				for (int p = i - 1; p >= 0; p--) {
					for (int q = k; q < l; q++) {
						for (int rx = m; rx < n; rx++) {
							if (this.voxels.inBoundsAndContains(axisCycleDirection, p, q, rx)) {
								double f = this.getPointPosition(axis, p + 1) - e;
								if (f <= 1.0E-7) {
									maxDist = Math.max(maxDist, f);
								}

								return maxDist;
							}
						}
					}
				}
			}

			return maxDist;
		}
	}

	public String toString() {
		return this.isEmpty() ? "EMPTY" : "VoxelShape[" + this.getBoundingBox() + "]";
	}
}
