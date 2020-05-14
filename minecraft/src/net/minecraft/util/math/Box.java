package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.hit.BlockHitResult;

public class Box {
	public final double minX;
	public final double minY;
	public final double minZ;
	public final double maxX;
	public final double maxY;
	public final double maxZ;

	public Box(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.minX = Math.min(x1, x2);
		this.minY = Math.min(y1, y2);
		this.minZ = Math.min(z1, z2);
		this.maxX = Math.max(x1, x2);
		this.maxY = Math.max(y1, y2);
		this.maxZ = Math.max(z1, z2);
	}

	public Box(BlockPos pos) {
		this((double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), (double)(pos.getX() + 1), (double)(pos.getY() + 1), (double)(pos.getZ() + 1));
	}

	public Box(BlockPos pos1, BlockPos pos2) {
		this((double)pos1.getX(), (double)pos1.getY(), (double)pos1.getZ(), (double)pos2.getX(), (double)pos2.getY(), (double)pos2.getZ());
	}

	public Box(Vec3d pos1, Vec3d pos2) {
		this(pos1.x, pos1.y, pos1.z, pos2.x, pos2.y, pos2.z);
	}

	public static Box from(BlockBox mutable) {
		return new Box(
			(double)mutable.minX, (double)mutable.minY, (double)mutable.minZ, (double)(mutable.maxX + 1), (double)(mutable.maxY + 1), (double)(mutable.maxZ + 1)
		);
	}

	public double getMin(Direction.Axis axis) {
		return axis.choose(this.minX, this.minY, this.minZ);
	}

	public double getMax(Direction.Axis axis) {
		return axis.choose(this.maxX, this.maxY, this.maxZ);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Box)) {
			return false;
		} else {
			Box box = (Box)o;
			if (Double.compare(box.minX, this.minX) != 0) {
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

	public Box expand(double x, double y, double z) {
		double d = this.minX - x;
		double e = this.minY - y;
		double f = this.minZ - z;
		double g = this.maxX + x;
		double h = this.maxY + y;
		double i = this.maxZ + z;
		return new Box(d, e, f, g, h, i);
	}

	public Box expand(double value) {
		return this.expand(value, value, value);
	}

	public Box intersection(Box box) {
		double d = Math.max(this.minX, box.minX);
		double e = Math.max(this.minY, box.minY);
		double f = Math.max(this.minZ, box.minZ);
		double g = Math.min(this.maxX, box.maxX);
		double h = Math.min(this.maxY, box.maxY);
		double i = Math.min(this.maxZ, box.maxZ);
		return new Box(d, e, f, g, h, i);
	}

	public Box union(Box box) {
		double d = Math.min(this.minX, box.minX);
		double e = Math.min(this.minY, box.minY);
		double f = Math.min(this.minZ, box.minZ);
		double g = Math.max(this.maxX, box.maxX);
		double h = Math.max(this.maxY, box.maxY);
		double i = Math.max(this.maxZ, box.maxZ);
		return new Box(d, e, f, g, h, i);
	}

	public Box offset(double x, double y, double z) {
		return new Box(this.minX + x, this.minY + y, this.minZ + z, this.maxX + x, this.maxY + y, this.maxZ + z);
	}

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

	public Box offset(Vec3d vec3d) {
		return this.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	public boolean intersects(Box box) {
		return this.intersects(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
	}

	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.minX < maxX && this.maxX > minX && this.minY < maxY && this.maxY > minY && this.minZ < maxZ && this.maxZ > minZ;
	}

	@Environment(EnvType.CLIENT)
	public boolean intersects(Vec3d from, Vec3d to) {
		return this.intersects(
			Math.min(from.x, to.x), Math.min(from.y, to.y), Math.min(from.z, to.z), Math.max(from.x, to.x), Math.max(from.y, to.y), Math.max(from.z, to.z)
		);
	}

	public boolean contains(Vec3d vec) {
		return this.contains(vec.x, vec.y, vec.z);
	}

	public boolean contains(double x, double y, double z) {
		return x >= this.minX && x < this.maxX && y >= this.minY && y < this.maxY && z >= this.minZ && z < this.maxZ;
	}

	public double getAverageSideLength() {
		double d = this.getXLength();
		double e = this.getYLength();
		double f = this.getZLength();
		return (d + e + f) / 3.0;
	}

	public double getXLength() {
		return this.maxX - this.minX;
	}

	public double getYLength() {
		return this.maxY - this.minY;
	}

	public double getZLength() {
		return this.maxZ - this.minZ;
	}

	public Box contract(double value) {
		return this.expand(-value);
	}

	public Optional<Vec3d> rayTrace(Vec3d min, Vec3d max) {
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
	public static BlockHitResult rayTrace(Iterable<Box> boxes, Vec3d from, Vec3d to, BlockPos pos) {
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
		Box box, Vec3d intersectingVector, double[] traceDistanceResult, @Nullable Direction approachDirection, double xDelta, double yDelta, double zDelta
	) {
		if (xDelta > 1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				xDelta,
				yDelta,
				zDelta,
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
		} else if (xDelta < -1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				xDelta,
				yDelta,
				zDelta,
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

		if (yDelta > 1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				yDelta,
				zDelta,
				xDelta,
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
		} else if (yDelta < -1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				yDelta,
				zDelta,
				xDelta,
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

		if (zDelta > 1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				zDelta,
				xDelta,
				yDelta,
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
		} else if (zDelta < -1.0E-7) {
			approachDirection = traceCollisionSide(
				traceDistanceResult,
				approachDirection,
				zDelta,
				xDelta,
				yDelta,
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
		double xDelta,
		double yDelta,
		double zDelta,
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
		double d = (begin - startX) / xDelta;
		double e = startY + d * yDelta;
		double f = startZ + d * zDelta;
		if (0.0 < d && d < traceDistanceResult[0] && minX - 1.0E-7 < e && e < maxX + 1.0E-7 && minZ - 1.0E-7 < f && f < maxZ + 1.0E-7) {
			traceDistanceResult[0] = d;
			return resultDirection;
		} else {
			return approachDirection;
		}
	}

	public String toString() {
		return "box[" + this.minX + ", " + this.minY + ", " + this.minZ + "] -> [" + this.maxX + ", " + this.maxY + ", " + this.maxZ + "]";
	}

	@Environment(EnvType.CLIENT)
	public boolean isValid() {
		return Double.isNaN(this.minX)
			|| Double.isNaN(this.minY)
			|| Double.isNaN(this.minZ)
			|| Double.isNaN(this.maxX)
			|| Double.isNaN(this.maxY)
			|| Double.isNaN(this.maxZ);
	}

	public Vec3d getCenter() {
		return new Vec3d(MathHelper.lerp(0.5, this.minX, this.maxX), MathHelper.lerp(0.5, this.minY, this.maxY), MathHelper.lerp(0.5, this.minZ, this.maxZ));
	}
}
