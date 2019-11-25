package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import javax.annotation.concurrent.Immutable;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

@Immutable
public class Vec3i implements Comparable<Vec3i> {
	public static final Vec3i ZERO = new Vec3i(0, 0, 0);
	@Deprecated
	private final int x;
	@Deprecated
	private final int y;
	@Deprecated
	private final int z;

	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3i(double x, double y, double z) {
		this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
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

	public int compareTo(Vec3i vec3i) {
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

	public Vec3i down() {
		return this.down(1);
	}

	public Vec3i down(int i) {
		return this.offset(Direction.DOWN, i);
	}

	public Vec3i offset(Direction direction, int i) {
		return i == 0
			? this
			: new Vec3i(this.getX() + direction.getOffsetX() * i, this.getY() + direction.getOffsetY() * i, this.getZ() + direction.getOffsetZ() * i);
	}

	public Vec3i crossProduct(Vec3i vec) {
		return new Vec3i(
			this.getY() * vec.getZ() - this.getZ() * vec.getY(),
			this.getZ() * vec.getX() - this.getX() * vec.getZ(),
			this.getX() * vec.getY() - this.getY() * vec.getX()
		);
	}

	public boolean isWithinDistance(Vec3i vec, double distance) {
		return this.getSquaredDistance((double)vec.getX(), (double)vec.getY(), (double)vec.getZ(), false) < distance * distance;
	}

	public boolean isWithinDistance(Position pos, double distance) {
		return this.getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), true) < distance * distance;
	}

	public double getSquaredDistance(Vec3i vec) {
		return this.getSquaredDistance((double)vec.getX(), (double)vec.getY(), (double)vec.getZ(), true);
	}

	public double getSquaredDistance(Position pos, boolean treatAsBlockPos) {
		return this.getSquaredDistance(pos.getX(), pos.getY(), pos.getZ(), treatAsBlockPos);
	}

	public double getSquaredDistance(double x, double y, double z, boolean treatAsBlockPos) {
		double d = treatAsBlockPos ? 0.5 : 0.0;
		double e = (double)this.getX() + d - x;
		double f = (double)this.getY() + d - y;
		double g = (double)this.getZ() + d - z;
		return e * e + f * f + g * g;
	}

	public int getManhattanDistance(Vec3i vec) {
		float f = (float)Math.abs(vec.getX() - this.getX());
		float g = (float)Math.abs(vec.getY() - this.getY());
		float h = (float)Math.abs(vec.getZ() - this.getZ());
		return (int)(f + g + h);
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
	}

	@Environment(EnvType.CLIENT)
	public String toShortString() {
		return "" + this.getX() + ", " + this.getY() + ", " + this.getZ();
	}
}
