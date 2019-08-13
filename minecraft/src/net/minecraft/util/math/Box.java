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

	public Box(double d, double e, double f, double g, double h, double i) {
		this.minX = Math.min(d, g);
		this.minY = Math.min(e, h);
		this.minZ = Math.min(f, i);
		this.maxX = Math.max(d, g);
		this.maxY = Math.max(e, h);
		this.maxZ = Math.max(f, i);
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

	public Box(BlockPos blockPos, BlockPos blockPos2) {
		this((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ(), (double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ());
	}

	public Box(Vec3d vec3d, Vec3d vec3d2) {
		this(vec3d.x, vec3d.y, vec3d.z, vec3d2.x, vec3d2.y, vec3d2.z);
	}

	public static Box from(MutableIntBoundingBox mutableIntBoundingBox) {
		return new Box(
			(double)mutableIntBoundingBox.minX,
			(double)mutableIntBoundingBox.minY,
			(double)mutableIntBoundingBox.minZ,
			(double)(mutableIntBoundingBox.maxX + 1),
			(double)(mutableIntBoundingBox.maxY + 1),
			(double)(mutableIntBoundingBox.maxZ + 1)
		);
	}

	public double getMin(Direction.Axis axis) {
		return axis.choose(this.minX, this.minY, this.minZ);
	}

	public double getMax(Direction.Axis axis) {
		return axis.choose(this.maxX, this.maxY, this.maxZ);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Box)) {
			return false;
		} else {
			Box box = (Box)object;
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

	public Box shrink(double d, double e, double f) {
		double g = this.minX;
		double h = this.minY;
		double i = this.minZ;
		double j = this.maxX;
		double k = this.maxY;
		double l = this.maxZ;
		if (d < 0.0) {
			g -= d;
		} else if (d > 0.0) {
			j -= d;
		}

		if (e < 0.0) {
			h -= e;
		} else if (e > 0.0) {
			k -= e;
		}

		if (f < 0.0) {
			i -= f;
		} else if (f > 0.0) {
			l -= f;
		}

		return new Box(g, h, i, j, k, l);
	}

	public Box stretch(Vec3d vec3d) {
		return this.stretch(vec3d.x, vec3d.y, vec3d.z);
	}

	public Box stretch(double d, double e, double f) {
		double g = this.minX;
		double h = this.minY;
		double i = this.minZ;
		double j = this.maxX;
		double k = this.maxY;
		double l = this.maxZ;
		if (d < 0.0) {
			g += d;
		} else if (d > 0.0) {
			j += d;
		}

		if (e < 0.0) {
			h += e;
		} else if (e > 0.0) {
			k += e;
		}

		if (f < 0.0) {
			i += f;
		} else if (f > 0.0) {
			l += f;
		}

		return new Box(g, h, i, j, k, l);
	}

	public Box expand(double d, double e, double f) {
		double g = this.minX - d;
		double h = this.minY - e;
		double i = this.minZ - f;
		double j = this.maxX + d;
		double k = this.maxY + e;
		double l = this.maxZ + f;
		return new Box(g, h, i, j, k, l);
	}

	public Box expand(double d) {
		return this.expand(d, d, d);
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

	public Box offset(double d, double e, double f) {
		return new Box(this.minX + d, this.minY + e, this.minZ + f, this.maxX + d, this.maxY + e, this.maxZ + f);
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

	public boolean intersects(double d, double e, double f, double g, double h, double i) {
		return this.minX < g && this.maxX > d && this.minY < h && this.maxY > e && this.minZ < i && this.maxZ > f;
	}

	@Environment(EnvType.CLIENT)
	public boolean intersects(Vec3d vec3d, Vec3d vec3d2) {
		return this.intersects(
			Math.min(vec3d.x, vec3d2.x),
			Math.min(vec3d.y, vec3d2.y),
			Math.min(vec3d.z, vec3d2.z),
			Math.max(vec3d.x, vec3d2.x),
			Math.max(vec3d.y, vec3d2.y),
			Math.max(vec3d.z, vec3d2.z)
		);
	}

	public boolean contains(Vec3d vec3d) {
		return this.contains(vec3d.x, vec3d.y, vec3d.z);
	}

	public boolean contains(double d, double e, double f) {
		return d >= this.minX && d < this.maxX && e >= this.minY && e < this.maxY && f >= this.minZ && f < this.maxZ;
	}

	public double averageDimension() {
		double d = this.getXSize();
		double e = this.getYSize();
		double f = this.getZSize();
		return (d + e + f) / 3.0;
	}

	public double getXSize() {
		return this.maxX - this.minX;
	}

	public double getYSize() {
		return this.maxY - this.minY;
	}

	public double getZSize() {
		return this.maxZ - this.minZ;
	}

	public Box contract(double d) {
		return this.expand(-d);
	}

	public Optional<Vec3d> rayTrace(Vec3d vec3d, Vec3d vec3d2) {
		double[] ds = new double[]{1.0};
		double d = vec3d2.x - vec3d.x;
		double e = vec3d2.y - vec3d.y;
		double f = vec3d2.z - vec3d.z;
		Direction direction = method_1007(this, vec3d, ds, null, d, e, f);
		if (direction == null) {
			return Optional.empty();
		} else {
			double g = ds[0];
			return Optional.of(vec3d.add(g * d, g * e, g * f));
		}
	}

	@Nullable
	public static BlockHitResult rayTrace(Iterable<Box> iterable, Vec3d vec3d, Vec3d vec3d2, BlockPos blockPos) {
		double[] ds = new double[]{1.0};
		Direction direction = null;
		double d = vec3d2.x - vec3d.x;
		double e = vec3d2.y - vec3d.y;
		double f = vec3d2.z - vec3d.z;

		for (Box box : iterable) {
			direction = method_1007(box.offset(blockPos), vec3d, ds, direction, d, e, f);
		}

		if (direction == null) {
			return null;
		} else {
			double g = ds[0];
			return new BlockHitResult(vec3d.add(g * d, g * e, g * f), direction, blockPos, false);
		}
	}

	@Nullable
	private static Direction method_1007(Box box, Vec3d vec3d, double[] ds, @Nullable Direction direction, double d, double e, double f) {
		if (d > 1.0E-7) {
			direction = method_998(ds, direction, d, e, f, box.minX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.field_11039, vec3d.x, vec3d.y, vec3d.z);
		} else if (d < -1.0E-7) {
			direction = method_998(ds, direction, d, e, f, box.maxX, box.minY, box.maxY, box.minZ, box.maxZ, Direction.field_11034, vec3d.x, vec3d.y, vec3d.z);
		}

		if (e > 1.0E-7) {
			direction = method_998(ds, direction, e, f, d, box.minY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.field_11033, vec3d.y, vec3d.z, vec3d.x);
		} else if (e < -1.0E-7) {
			direction = method_998(ds, direction, e, f, d, box.maxY, box.minZ, box.maxZ, box.minX, box.maxX, Direction.field_11036, vec3d.y, vec3d.z, vec3d.x);
		}

		if (f > 1.0E-7) {
			direction = method_998(ds, direction, f, d, e, box.minZ, box.minX, box.maxX, box.minY, box.maxY, Direction.field_11043, vec3d.z, vec3d.x, vec3d.y);
		} else if (f < -1.0E-7) {
			direction = method_998(ds, direction, f, d, e, box.maxZ, box.minX, box.maxX, box.minY, box.maxY, Direction.field_11035, vec3d.z, vec3d.x, vec3d.y);
		}

		return direction;
	}

	@Nullable
	private static Direction method_998(
		double[] ds,
		@Nullable Direction direction,
		double d,
		double e,
		double f,
		double g,
		double h,
		double i,
		double j,
		double k,
		Direction direction2,
		double l,
		double m,
		double n
	) {
		double o = (g - l) / d;
		double p = m + o * e;
		double q = n + o * f;
		if (0.0 < o && o < ds[0] && h - 1.0E-7 < p && p < i + 1.0E-7 && j - 1.0E-7 < q && q < k + 1.0E-7) {
			ds[0] = o;
			return direction2;
		} else {
			return direction;
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
