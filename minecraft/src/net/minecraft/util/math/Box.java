package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.hit.BlockHitResult;

/**
 * An immutable box with double-valued coordinates. The box is axis-aligned
 * and the coordinates are minimum inclusive and maximum exclusive.
 * 
 * <p>This box has proper {@link #hashCode()} and {@link #equals(Object)}
 * implementations and can be used as a map key.
 * 
 * @see BlockBox
 */
public class Box {
	private static final double EPSILON = 1.0E-7;
	public final double minX;
	public final double minY;
	public final double minZ;
	public final double maxX;
	public final double maxY;
	public final double maxZ;

	/**
	 * Creates a box of the given positions as corners.
	 */
	public Box(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	/**
	 * Creates a box that only contains the given block position.
	 */
	public Box(BlockPos pos) {
		this((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
	}

	/**
	 * Creates a box of the given positions as corners.
	 */
	public Box(BlockPos pos1, BlockPos pos2) {
		this((double)pos1.getX(), (double)pos1.getY(), (double)pos1.getZ(), (double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
	}

	/**
	 * Creates a box of the given positions as corners.
	 */
	public Box(Vec3d pos1, Vec3d pos2) {
		this(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
	}

	public static Box from(BlockBox mutable) {
		return new Box(
			(double)mutable.getMinX(),
			(double)mutable.getMinY(),
			(double)mutable.getMinZ(),
			(double)(mutable.getMaxX() + 1),
			(double)(mutable.getMaxY() + 1),
			(double)(mutable.getMaxZ() + 1)
		);
	}

	public static Box from(Vec3d pos) {
		return new Box(pos.x, pos.y, pos.z, pos.x + 1.0, pos.y + 1.0, pos.z + 1.0);
	}

	/**
	 * {@return a new box with the minimum X provided and all other coordinates
	 * of this box}
	 */
	public Box withMinX(double minX) {
		return new Box(minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * {@return a new box with the minimum Y provided and all other coordinates
	 * of this box}
	 */
	public Box withMinY(double minY) {
		return new Box(this.minX, minY, this.minZ, this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * {@return a new box with the minimum Z provided and all other coordinates
	 * of this box}
	 */
	public Box withMinZ(double minZ) {
		return new Box(this.minX, this.minY, minZ, this.maxX, this.maxY, this.maxZ);
	}

	/**
	 * {@return a new box with the maximum X provided and all other coordinates
	 * of this box}
	 */
	public Box withMaxX(double maxX) {
		return new Box(this.minX, this.minY, this.minZ, maxX, this.maxY, this.maxZ);
	}

	/**
	 * {@return a new box with the maximum Y provided and all other coordinates
	 * of this box}
	 */
	public Box withMaxY(double maxY) {
		return new Box(this.minX, this.minY, this.minZ, this.maxX, maxY, this.maxZ);
	}

	/**
	 * {@return a new box with the maximum Z provided and all other coordinates
	 * of this box}
	 */
	public Box withMaxZ(double maxZ) {
		return new Box(this.minX, this.minY, this.minZ, this.maxX, this.maxY, maxZ);
	}

	/**
	 * {@return the minimum coordinate for the given {@code axis} of this box}
	 */
	public double getMin(Direction.Axis axis) {
		return axis.choose(this.minX, this.minY, this.minZ);
	}

	/**
	 * {@return the maximum coordinate for the given {@code axis} of this box}
	 */
	public double getMax(Direction.Axis axis) {
		return axis.choose(this.maxX, this.maxY, this.maxZ);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Box box)) {
			return false;
		} else if (Double.compare(box.minX, this.minX) != 0) {
			return false;
		} else if (Double.compare(box.minY, this.minY) != 0) {
			return false;
		} else if (Double.compare(box.minZ, this.minZ) != 0) {
			return false;
		} else if (Double.compare(box.maxX, this.maxX) != 0) {
			return false;
		} else {
			return Double.compare(box.maxY, this.maxY) != 0 ? false : Double.compare(box.maxZ, this.maxZ) == 0;
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.minX);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minY);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.minZ);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxX);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxY);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.maxZ);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public Box shrink(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d -= x;
		} else if (x > 0.0) {
			g -= x;
		}

		if (y < 0.0) {
			e -= y;
		} else if (y > 0.0) {
			h -= y;
		}

		if (z < 0.0) {
			f -= z;
		} else if (z > 0.0) {
			i -= z;
		}

		return new Box(d, e, f, g, h, i);
	}

	public Box stretch(Vec3d scale) {
		return this.stretch(scale.x, scale.y, scale.z);
	}

	public Box stretch(double x, double y, double z) {
		double d = this.minX;
		double e = this.minY;
		double f = this.minZ;
		double g = this.maxX;
		double h = this.maxY;
		double i = this.maxZ;
		if (x < 0.0) {
			d += x;
		} else if (x > 0.0) {
			g += x;
		}

		if (y < 0.0) {
			e += y;
		} else if (y > 0.0) {
			h += y;
		}

		if (z < 0.0) {
			f += z;
		} else if (z > 0.0) {
			i += z;
		}

		return new Box(d, e, f, g, h, i);
	}

	/**
	 * @see #contract(double, double, double)
	 */
	public Box expand(double x, double y, double z) {
		double d = this.minX - x;
		double e = this.minY - y;
		double f = this.minZ - z;
		double g = this.maxX + x;
		double h = this.maxY + y;
		double i = this.maxZ + z;
		return new Box(d, e, f, g, h, i);
	}

	/**
	 * @see #contract(double)
	 */
	public Box expand(double value) {
		return this.expand(value, value, value);
	}

	/**
	 * Creates the maximum box that this box and the given box contain.
	 */
	public Box intersection(Box box) {
		double d = Math.max(this.minX, box.minX);
		double e = Math.max(this.minY, box.minY);
		double f = Math.max(this.minZ, box.minZ);
		double g = Math.min(this.maxX, box.maxX);
		double h = Math.min(this.maxY, box.maxY);
		double i = Math.min(this.maxZ, box.maxZ);
		return new Box(d, e, f, g, h, i);
	}

	/**
	 * Creates the minimum box that contains this box and the given box.
	 */
	public Box union(Box box) {
		double d = Math.min(this.minX, box.minX);
		double e = Math.min(this.minY, box.minY);
		double f = Math.min(this.minZ, box.minZ);
		double g = Math.max(this.maxX, box.maxX);
		double h = Math.max(this.maxY, box.maxY);
		double i = Math.max(this.maxZ, box.maxZ);
		return new Box(d, e, f, g, h, i);
	}

	/**
	 * Creates a box that is translated by {@code x}, {@code y}, {@code z} on
	 * each axis from this box.
	 */
	public Box offset(double x, double y, double z) {
		return new Box(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

	/**
	 * Creates a box that is translated by {@code blockPos.getX()}, {@code
	 * blockPos.getY()}, {@code blockPos.getZ()} on each axis from this box.
	 * 
	 * @see #offset(double, double, double)
	 */
	public Box offset(BlockPos blockPos) {
		return new Box(
			this.minX + (double)blockPos.getX(),
			this.minY + (double)blockPos.getY(),
			this.minZ + (double)blockPos.getZ(),
			this.maxX + (double)blockPos.getX(),
			this.maxY + (double)blockPos.getY(),
			this.maxZ + (double)blockPos.getZ()
		);
	}

	/**
	 * Creates a box that is translated by {@code vec.x}, {@code vec.y}, {@code
	 * vec.z} on each axis from this box.
	 * 
	 * @see #offset(double, double, double)
	 */
	public Box offset(Vec3d vec) {
		return this.offset(vec.x, vec.y, vec.z);
	}

	/**
	 * Checks if this box intersects the given box.
	 */
	public boolean intersects(Box box) {
		return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	/**
	 * Checks if this box intersects the box of the given coordinates.
	 */
	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
	}

	/**
	 * Checks if this box intersects the box of the given positions as
	 * corners.
	 */
	public boolean intersects(Vec3d pos1, Vec3d pos2) {
		return this.intersects(
			Math.min(pos1.x, pos2.x), Math.min(pos1.y, pos2.y), Math.min(pos1.z, pos2.z), Math.max(pos1.x, pos2.x), Math.max(pos1.y, pos2.y), Math.max(pos1.z, pos2.z)
		);
	}

	/**
	 * Checks if the given position is in this box.
	 */
	public boolean contains(Vec3d pos) {
		return this.contains(pos.x, pos.y, pos.z);
	}

	/**
	 * Checks if the given position is in this box.
	 */
	public boolean contains(double x, double y, double z) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
	}

	public double getAverageSideLength() {
		double d = this.getLengthX();
		double e = this.getLengthY();
		double f = this.getLengthZ();
		return (d + e + f) / 3.0;
	}

	/**
	 * {@return the length of this box on the X axis}
	 */
	public double getLengthX() {
		return this.maxX - this.minX;
	}

	/**
	 * {@return the length of this box on the Y axis}
	 */
	public double getLengthY() {
		return this.maxY - this.minY;
	}

	/**
	 * {@return the length of this box on the Z axis}
	 */
	public double getLengthZ() {
		return this.maxZ - this.minZ;
	}

	/**
	 * @see #expand(double, double, double)
	 */
	public Box contract(double x, double y, double z) {
		return this.expand(-x, -y, -z);
	}

	/**
	 * @see #expand(double)
	 */
	public Box contract(double value) {
		return this.expand(-value);
	}

	public Optional<Vec3d> raycast(Vec3d min, Vec3d max) {
		double[] ds = new double[]{1.0};
		double d = max.x - min.x;
		double e = max.y - min.y;
		double f = max.z - min.z;
		Direction direction = traceCollisionSide(this, min, ds, null, d, e, f);
		if (direction == null) {
			return Optional.empty();
		} else {
			double g = ds[0];
			return Optional.of(min.add(g * d, g * e, g * f));
		}
	}

	@Nullable
	public static BlockHitResult raycast(Iterable<Box> boxes, Vec3d from, Vec3d to, BlockPos pos) {
		double[] ds = new double[]{1.0};
		Direction direction = null;
		double d = to.x - from.x;
		double e = to.y - from.y;
		double f = to.z - from.z;

		for (Box box : boxes) {
			direction = traceCollisionSide(box.offset(pos), from, ds, direction, d, e, f);
		}

		if (direction == null) {
			return null;
		} else {
			double g = ds[0];
			return new BlockHitResult(from.add(g * d, g * e, g * f), direction, pos, false);
		}
	}

	@Nullable
	private static Direction traceCollisionSide(
		Box box, Vec3d intersectingVector, double[] traceDistanceResult, @Nullable Direction approachDirection, double deltaX, double deltaY, double deltaZ
	) {
		if (deltaX > 1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				deltaX,
				deltaY,
				deltaZ,
				box.minX,
				box.minY,
				box.maxY,
				box.minZ,
				box.maxZ,
				Direction.WEST,
				intersectingVector.x,
				intersectingVector.y,
				intersectingVector.z
			);
		} else if (deltaX < -1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				deltaX,
				deltaY,
				deltaZ,
				box.maxX,
				box.minY,
				box.maxY,
				box.minZ,
				box.maxZ,
				Direction.EAST,
				intersectingVector.x,
				intersectingVector.y,
				intersectingVector.z
			);
		}

		if (deltaY > 1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				deltaY,
				deltaZ,
				deltaX,
				box.minY,
				box.minZ,
				box.maxZ,
				box.minX,
				box.maxX,
				Direction.DOWN,
				intersectingVector.y,
				intersectingVector.z,
				intersectingVector.x
			);
		} else if (deltaY < -1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				deltaY,
				deltaZ,
				deltaX,
				box.maxY,
				box.minZ,
				box.maxZ,
				box.minX,
				box.maxX,
				Direction.UP,
				intersectingVector.y,
				intersectingVector.z,
				intersectingVector.x
			);
		}

		if (deltaZ > 1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				deltaZ,
				deltaX,
				deltaY,
				box.minZ,
				box.minX,
				box.maxX,
				box.minY,
				box.maxY,
				Direction.NORTH,
				intersectingVector.z,
				intersectingVector.x,
				intersectingVector.y
			);
		} else if (deltaZ < -1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				deltaZ,
				deltaX,
				deltaY,
				box.maxZ,
				box.minX,
				box.maxX,
				box.minY,
				box.maxY,
				Direction.SOUTH,
				intersectingVector.z,
				intersectingVector.x,
				intersectingVector.y
			);
		}

		return approachDirection;
	}

	@Nullable
	private static Direction traceCollisionSide(
		double[] traceDistanceResult,
		@Nullable Direction approachDirection,
		double deltaX,
		double deltaY,
		double deltaZ,
		double begin,
		double minX,
		double maxX,
		double minZ,
		double maxZ,
		Direction resultDirection,
		double startX,
		double startY,
		double startZ
	) {
		double d = (begin - startX) / deltaX;
		double e = startY + d * deltaY;
		double f = startZ + d * deltaZ;
		if (0.0 < d && d < traceDistanceResult[0] && minX - 1.0E-7 < e && e < maxX + 1.0E-7 && minZ - 1.0E-7 < f && f < maxZ + 1.0E-7) {
			traceDistanceResult[0] = d;
			return resultDirection;
		} else {
			return approachDirection;
		}
	}

	public double squaredMagnitude(Vec3d pos) {
		double d = Math.max(Math.max(this.minX - pos.x, pos.x - this.maxX), 0.0);
		double e = Math.max(Math.max(this.minY - pos.y, pos.y - this.maxY), 0.0);
		double f = Math.max(Math.max(this.minZ - pos.z, pos.z - this.maxZ), 0.0);
		return MathHelper.squaredMagnitude(d, e, f);
	}

	public String toString() {
		return "AABB[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}

	/**
	 * Checks if any of the coordinates of this box are {@linkplain
	 * Double#isNaN(double) not a number}.
	 */
	public boolean isNaN() {
		return Double.isNaN(this.minX)
			|| Double.isNaN(this.minY)
			|| Double.isNaN(this.minZ)
			|| Double.isNaN(this.maxX)
			|| Double.isNaN(this.maxY)
			|| Double.isNaN(this.maxZ);
	}

	/**
	 * Returns the center position of this box.
	 */
	public Vec3d getCenter() {
		return new Vec3d(MathHelper.lerp(0.5, this.minX, this.maxX), MathHelper.lerp(0.5, this.minY, this.maxY), MathHelper.lerp(0.5, this.minZ, this.maxZ));
	}

	public static Box of(Vec3d center, double dx, double dy, double dz) {
		return new Box(center.x - dx / 2.0, center.y - dy / 2.0, center.z - dz / 2.0, center.x + dx / 2.0, center.y + dy / 2.0, center.z + dz / 2.0);
	}
}
