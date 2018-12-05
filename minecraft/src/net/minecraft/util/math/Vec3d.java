package net.minecraft.util.math;

import java.util.EnumSet;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

public class Vec3d {
	public static final Vec3d ZERO = new Vec3d(0.0, 0.0, 0.0);
	public final double x;
	public final double y;
	public final double z;

	public Vec3d(double d, double e, double f) {
		this.x = d;
		this.y = e;
		this.z = f;
	}

	public Vec3d(Vec3i vec3i) {
		this((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ());
	}

	public Vec3d reverseSubtract(Vec3d vec3d) {
		return new Vec3d(vec3d.x - this.x, vec3d.y - this.y, vec3d.z - this.z);
	}

	public Vec3d normalize() {
		double d = (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
		return d < 1.0E-4 ? ZERO : new Vec3d(this.x / d, this.y / d, this.z / d);
	}

	public double dotProduct(Vec3d vec3d) {
		return this.x * vec3d.x + this.y * vec3d.y + this.z * vec3d.z;
	}

	public Vec3d crossProduct(Vec3d vec3d) {
		return new Vec3d(this.y * vec3d.z - this.z * vec3d.y, this.z * vec3d.x - this.x * vec3d.z, this.x * vec3d.y - this.y * vec3d.x);
	}

	public Vec3d subtract(Vec3d vec3d) {
		return this.subtract(vec3d.x, vec3d.y, vec3d.z);
	}

	public Vec3d subtract(double d, double e, double f) {
		return this.add(-d, -e, -f);
	}

	public Vec3d add(Vec3d vec3d) {
		return this.add(vec3d.x, vec3d.y, vec3d.z);
	}

	public Vec3d add(double d, double e, double f) {
		return new Vec3d(this.x + d, this.y + e, this.z + f);
	}

	public double distanceTo(Vec3d vec3d) {
		double d = vec3d.x - this.x;
		double e = vec3d.y - this.y;
		double f = vec3d.z - this.z;
		return (double)MathHelper.sqrt(d * d + e * e + f * f);
	}

	public double squaredDistanceTo(Vec3d vec3d) {
		double d = vec3d.x - this.x;
		double e = vec3d.y - this.y;
		double f = vec3d.z - this.z;
		return d * d + e * e + f * f;
	}

	public double squaredDistanceTo(double d, double e, double f) {
		double g = d - this.x;
		double h = e - this.y;
		double i = f - this.z;
		return g * g + h * h + i * i;
	}

	public Vec3d multiply(double d) {
		return new Vec3d(this.x * d, this.y * d, this.z * d);
	}

	public double length() {
		return (double)MathHelper.sqrt(this.x * this.x + this.y * this.y + this.z * this.z);
	}

	public double lengthSquared() {
		return this.x * this.x + this.y * this.y + this.z * this.z;
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Vec3d)) {
			return false;
		} else {
			Vec3d vec3d = (Vec3d)object;
			if (Double.compare(vec3d.x, this.x) != 0) {
				return false;
			} else {
				return Double.compare(vec3d.y, this.y) != 0 ? false : Double.compare(vec3d.z, this.z) == 0;
			}
		}
	}

	public int hashCode() {
		long l = Double.doubleToLongBits(this.x);
		int i = (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.y);
		i = 31 * i + (int)(l ^ l >>> 32);
		l = Double.doubleToLongBits(this.z);
		return 31 * i + (int)(l ^ l >>> 32);
	}

	public String toString() {
		return "(" + this.x + ", " + this.y + ", " + this.z + ")";
	}

	public Vec3d rotateX(float f) {
		float g = MathHelper.cos(f);
		float h = MathHelper.sin(f);
		double d = this.x;
		double e = this.y * (double)g + this.z * (double)h;
		double i = this.z * (double)g - this.y * (double)h;
		return new Vec3d(d, e, i);
	}

	public Vec3d rotateY(float f) {
		float g = MathHelper.cos(f);
		float h = MathHelper.sin(f);
		double d = this.x * (double)g + this.z * (double)h;
		double e = this.y;
		double i = this.z * (double)g - this.x * (double)h;
		return new Vec3d(d, e, i);
	}

	@Environment(EnvType.CLIENT)
	public static Vec3d fromPolar(Vec2f vec2f) {
		return fromPolar(vec2f.x, vec2f.y);
	}

	@Environment(EnvType.CLIENT)
	public static Vec3d fromPolar(float f, float g) {
		float h = MathHelper.cos(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float i = MathHelper.sin(-g * (float) (Math.PI / 180.0) - (float) Math.PI);
		float j = -MathHelper.cos(-f * (float) (Math.PI / 180.0));
		float k = MathHelper.sin(-f * (float) (Math.PI / 180.0));
		return new Vec3d((double)(i * j), (double)k, (double)(h * j));
	}

	public Vec3d method_1032(EnumSet<Direction.Axis> enumSet) {
		double d = enumSet.contains(Direction.Axis.X) ? (double)MathHelper.floor(this.x) : this.x;
		double e = enumSet.contains(Direction.Axis.Y) ? (double)MathHelper.floor(this.y) : this.y;
		double f = enumSet.contains(Direction.Axis.Z) ? (double)MathHelper.floor(this.z) : this.z;
		return new Vec3d(d, e, f);
	}
}
