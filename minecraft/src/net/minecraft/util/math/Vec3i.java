package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
	public static final Vec3i ZERO = new Vec3i(0, 0, 0);
	private final int x;
	private final int y;
	private final int z;

	public Vec3i(int i, int j, int k) {
		this.x = i;
		this.y = j;
		this.z = k;
	}

	public Vec3i(double d, double e, double f) {
		this(MathHelper.floor(d), MathHelper.floor(e), MathHelper.floor(f));
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (!(object instanceof Vec3i)) {
			return false;
		} else {
			Vec3i vec3i = (Vec3i)object;
			if (this.getX() != vec3i.getX()) {
				return false;
			} else {
				return this.getY() != vec3i.getY() ? false : this.getZ() == vec3i.getZ();
			}
		}
	}

	public int hashCode() {
		return (this.getY() + this.getZ() * 31) * 31 + this.getX();
	}

	public int method_10265(Vec3i vec3i) {
		if (this.getY() == vec3i.getY()) {
			return this.getZ() == vec3i.getZ() ? this.getX() - vec3i.getX() : this.getZ() - vec3i.getZ();
		} else {
			return this.getY() - vec3i.getY();
		}
	}

	public int getX() {
		return this.x;
	}

	public int getY() {
		return this.y;
	}

	public int getZ() {
		return this.z;
	}

	public Vec3i crossProduct(Vec3i vec3i) {
		return new Vec3i(
			this.getY() * vec3i.getZ() - this.getZ() * vec3i.getY(),
			this.getZ() * vec3i.getX() - this.getX() * vec3i.getZ(),
			this.getX() * vec3i.getY() - this.getY() * vec3i.getX()
		);
	}

	public double distanceTo(int i, int j, int k) {
		double d = (double)(this.getX() - i);
		double e = (double)(this.getY() - j);
		double f = (double)(this.getZ() - k);
		return Math.sqrt(d * d + e * e + f * f);
	}

	public double distanceTo(Vec3i vec3i) {
		return this.distanceTo(vec3i.getX(), vec3i.getY(), vec3i.getZ());
	}

	public double squaredDistanceTo(double d, double e, double f) {
		double g = (double)this.getX() - d;
		double h = (double)this.getY() - e;
		double i = (double)this.getZ() - f;
		return g * g + h * h + i * i;
	}

	public double squaredDistanceToCenter(double d, double e, double f) {
		double g = (double)this.getX() + 0.5 - d;
		double h = (double)this.getY() + 0.5 - e;
		double i = (double)this.getZ() + 0.5 - f;
		return g * g + h * h + i * i;
	}

	public double squaredDistanceTo(Vec3i vec3i) {
		return this.squaredDistanceTo((double)vec3i.getX(), (double)vec3i.getY(), (double)vec3i.getZ());
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
	}
}
