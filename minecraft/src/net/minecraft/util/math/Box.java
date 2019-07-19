package net.minecraft.util.math;

import java.util.Optional;
import javax.annotation.Nullable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.hit.BlockHitResult;

public class Box {
	public final double x1;
	public final double y1;
	public final double z1;
	public final double x2;
	public final double y2;
	public final double z2;

	public Box(double x1, double y1, double z1, double x2, double y2, double z2) {
		this.x1 = Math.min(x1, x2);
		this.y1 = Math.min(y1, y2);
		this.z1 = Math.min(z1, z2);
		this.x2 = Math.max(x1, x2);
		this.y2 = Math.max(y1, y2);
		this.z2 = Math.max(z1, z2);
	}

	public Box(BlockPos blockPos) {
		this(
			(double)blockPos.getX(),
			(double)blockPos.getY(),
			(double)blockPos.getZ(),
			(double)(blockPos.getX() + 1),
			(double)(blockPos.getY() + 1),
			(double)(blockPos.getZ() + 1)
		);
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
		return axis.choose(this.x1, this.y1, this.z1);
	}

	public double getMax(Direction.Axis axis) {
		return axis.choose(this.x2, this.y2, this.z2);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Box)) {
			return false;
		} else {
			Box box = (Box)o;
			if (Double.compare(box.x1, this.x1) != 0) {
				return false;
			} else if (Double.compare(box.y1, this.y1) != 0) {
				return false;
			} else if (Double.compare(box.z1, this.z1) != 0) {
				return false;
			} else if (Double.compare(box.x2, this.x2) != 0) {
				return false;
			} else {
				return Double.compare(box.y2, this.y2) != 0 ? false : Double.compare(box.z2, this.z2) == 0;
			}
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.x1);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y1);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.z1);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.x2);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y2);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.z2);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public Box shrink(double x, double y, double z) {
		double d = this.x1;
		double e = this.y1;
		double f = this.z1;
		double g = this.x2;
		double h = this.y2;
		double i = this.z2;
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
		double d = this.x1;
		double e = this.y1;
		double f = this.z1;
		double g = this.x2;
		double h = this.y2;
		double i = this.z2;
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
		double d = this.x1 - x;
		double e = this.y1 - y;
		double f = this.z1 - z;
		double g = this.x2 + x;
		double h = this.y2 + y;
		double i = this.z2 + z;
		return new Box(d, e, f, g, h, i);
	}

	public Box expand(double value) {
		return this.expand(value, value, value);
	}

	public Box intersection(Box box) {
		double d = Math.max(this.x1, box.x1);
		double e = Math.max(this.y1, box.y1);
		double f = Math.max(this.z1, box.z1);
		double g = Math.min(this.x2, box.x2);
		double h = Math.min(this.y2, box.y2);
		double i = Math.min(this.z2, box.z2);
		return new Box(d, e, f, g, h, i);
	}

	public Box union(Box box) {
		double d = Math.min(this.x1, box.x1);
		double e = Math.min(this.y1, box.y1);
		double f = Math.min(this.z1, box.z1);
		double g = Math.max(this.x2, box.x2);
		double h = Math.max(this.y2, box.y2);
		double i = Math.max(this.z2, box.z2);
		return new Box(d, e, f, g, h, i);
	}

	public Box offset(double x, double y, double z) {
		return new Box(this.x1 + x, this.y1 + y, this.z1 + z, this.x2 + x, this.y2 + y, this.z2 + z);
	}

	public Box offset(BlockPos blockPos) {
		return new Box(
			this.x1 + (double)blockPos.getX(),
			this.y1 + (double)blockPos.getY(),
			this.z1 + (double)blockPos.getZ(),
			this.x2 + (double)blockPos.getX(),
			this.y2 + (double)blockPos.getY(),
			this.z2 + (double)blockPos.getZ()
		);
	}

	public Box offset(Vec3d vec3d) {
		return this.offset(vec3d.x, vec3d.y, vec3d.z);
	}

	public boolean intersects(Box box) {
		return this.intersects(box.x1, box.y1, box.z1, box.x2, box.y2, box.z2);
	}

	public boolean intersects(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
		return this.x1 < maxX && this.x2 > minX && this.y1 < maxY && this.y2 > minY && this.z1 < maxZ && this.z2 > minZ;
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
		return x >= this.x1 && x < this.x2 && y >= this.y1 && y < this.y2 && z >= this.z1 && z < this.z2;
	}

	public double getAverageSideLength() {
		double d = this.getXLength();
		double e = this.getYLength();
		double f = this.getZLength();
		return (d + e + f) / 3.0;
	}

	public double getXLength() {
		return this.x2 - this.x1;
	}

	public double getYLength() {
		return this.y2 - this.y1;
	}

	public double getZLength() {
		return this.z2 - this.z1;
	}

	public Box contract(double value) {
		return this.expand(-value);
	}

	public Optional<Vec3d> rayTrace(Vec3d min, Vec3d max) {
		double[] ds = new double[]{1.0};
		double d = max.x - min.x;
		double e = max.y - min.y;
		double f = max.z - min.z;
		Direction direction = method_1007(this, min, ds, null, d, e, f);
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
			direction = method_1007(box.offset(pos), from, ds, direction, d, e, f);
		}

		if (direction == null) {
			return null;
		} else {
			double g = ds[0];
			return new BlockHitResult(from.add(g * d, g * e, g * f), direction, pos, false);
		}
	}

	@Nullable
	private static Direction method_1007(Box box, Vec3d vec3d, double[] ds, @Nullable Direction direction, double d, double e, double f) {
		if (d > 1.0E-7) {
			direction = traceCollisionSide(ds, direction, d, e, f, box.x1, box.y1, box.y2, box.z1, box.z2, Direction.WEST, vec3d.x, vec3d.y, vec3d.z);
		} else if (d < -1.0E-7) {
			direction = traceCollisionSide(ds, direction, d, e, f, box.x2, box.y1, box.y2, box.z1, box.z2, Direction.EAST, vec3d.x, vec3d.y, vec3d.z);
		}

		if (e > 1.0E-7) {
			direction = traceCollisionSide(ds, direction, e, f, d, box.y1, box.z1, box.z2, box.x1, box.x2, Direction.DOWN, vec3d.y, vec3d.z, vec3d.x);
		} else if (e < -1.0E-7) {
			direction = traceCollisionSide(ds, direction, e, f, d, box.y2, box.z1, box.z2, box.x1, box.x2, Direction.UP, vec3d.y, vec3d.z, vec3d.x);
		}

		if (f > 1.0E-7) {
			direction = traceCollisionSide(ds, direction, f, d, e, box.z1, box.x1, box.x2, box.y1, box.y2, Direction.NORTH, vec3d.z, vec3d.x, vec3d.y);
		} else if (f < -1.0E-7) {
			direction = traceCollisionSide(ds, direction, f, d, e, box.z2, box.x1, box.x2, box.y1, box.y2, Direction.SOUTH, vec3d.z, vec3d.x, vec3d.y);
		}

		return direction;
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
		return "box[" + this.x1 + ", " + this.y1 + ", " + this.z1 + "] -> [" + this.x2 + ", " + this.y2 + ", " + this.z2 + "]";
	}

	@Environment(EnvType.CLIENT)
	public boolean isValid() {
		return Double.isNaN(this.x1) || Double.isNaN(this.y1) || Double.isNaN(this.z1) || Double.isNaN(this.x2) || Double.isNaN(this.y2) || Double.isNaN(this.z2);
	}

	public Vec3d getCenter() {
		return new Vec3d(MathHelper.lerp(0.5, this.x1, this.x2), MathHelper.lerp(0.5, this.y1, this.y2), MathHelper.lerp(0.5, this.z1, this.z2));
	}
}
