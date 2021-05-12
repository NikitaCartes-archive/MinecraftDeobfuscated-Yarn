package net.minecraft.util.math;

import com.google.common.base.MoreObjects;
import com.mojang.serialization.Codec;
import java.util.stream.IntStream;
import javax.annotation.concurrent.Immutable;
import net.minecraft.util.Util;

/**
 * A publicly read-only but mutable vector composed of 3 integers.
 */
@Immutable
public class Vec3i implements Comparable<Vec3i> {
	public static final Codec<Vec3i> CODEC = Codec.INT_STREAM
		.comapFlatMap(
			intStream -> Util.toArray(intStream, 3).map(is -> new Vec3i(is[0], is[1], is[2])),
			vec3i -> IntStream.of(new int[]{vec3i.getX(), vec3i.getY(), vec3i.getZ()})
		);
	public static final Vec3i ZERO = new Vec3i(0, 0, 0);
	private int x;
	private int y;
	private int z;

	public Vec3i(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;
	}

	public Vec3i(double x, double y, double z) {
		this(MathHelper.floor(x), MathHelper.floor(y), MathHelper.floor(z));
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (!(o instanceof Vec3i vec3i)) {
			return false;
		} else if (this.getX() != vec3i.getX()) {
			return false;
		} else {
			return this.getY() != vec3i.getY() ? false : this.getZ() == vec3i.getZ();
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

	protected Vec3i setX(int x) {
		this.x = x;
		return this;
	}

	protected Vec3i setY(int y) {
		this.y = y;
		return this;
	}

	protected Vec3i setZ(int z) {
		this.z = z;
		return this;
	}

	public Vec3i add(double x, double y, double z) {
		return x == 0.0 && y == 0.0 && z == 0.0 ? this : new Vec3i((double)this.getX() + x, (double)this.getY() + y, (double)this.getZ() + z);
	}

	/**
	 * Returns another Vec3i whose coordinates have the parameter x, y, and z
	 * added to the coordinates of this vector.
	 * 
	 * <p>This method always returns an immutable object.
	 */
	public Vec3i add(int x, int y, int z) {
		return x == 0 && y == 0 && z == 0 ? this : new Vec3i(this.getX() + x, this.getY() + y, this.getZ() + z);
	}

	public Vec3i add(Vec3i vec) {
		return this.add(vec.getX(), vec.getY(), vec.getZ());
	}

	public Vec3i subtract(Vec3i vec) {
		return this.add(-vec.getX(), -vec.getY(), -vec.getZ());
	}

	public Vec3i multiply(int scale) {
		if (scale == 1) {
			return this;
		} else {
			return scale == 0 ? ZERO : new Vec3i(this.getX() * scale, this.getY() * scale, this.getZ() * scale);
		}
	}

	public Vec3i up() {
		return this.up(1);
	}

	public Vec3i up(int distance) {
		return this.offset(Direction.UP, distance);
	}

	public Vec3i down() {
		return this.down(1);
	}

	public Vec3i down(int distance) {
		return this.offset(Direction.DOWN, distance);
	}

	public Vec3i north() {
		return this.north(1);
	}

	public Vec3i north(int distance) {
		return this.offset(Direction.NORTH, distance);
	}

	public Vec3i south() {
		return this.south(1);
	}

	public Vec3i south(int distance) {
		return this.offset(Direction.SOUTH, distance);
	}

	public Vec3i west() {
		return this.west(1);
	}

	public Vec3i west(int distance) {
		return this.offset(Direction.WEST, distance);
	}

	public Vec3i east() {
		return this.east(1);
	}

	public Vec3i east(int distance) {
		return this.offset(Direction.EAST, distance);
	}

	public Vec3i offset(Direction direction) {
		return this.offset(direction, 1);
	}

	public Vec3i offset(Direction direction, int distance) {
		return distance == 0
			? this
			: new Vec3i(
				this.getX() + direction.getOffsetX() * distance, this.getY() + direction.getOffsetY() * distance, this.getZ() + direction.getOffsetZ() * distance
			);
	}

	public Vec3i offset(Direction.Axis axis, int distance) {
		if (distance == 0) {
			return this;
		} else {
			int i = axis == Direction.Axis.X ? distance : 0;
			int j = axis == Direction.Axis.Y ? distance : 0;
			int k = axis == Direction.Axis.Z ? distance : 0;
			return new Vec3i(this.getX() + i, this.getY() + j, this.getZ() + k);
		}
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

	public double getSquaredDistance(Vec3i vec, boolean treatAsBlockPos) {
		return this.getSquaredDistance((double)vec.x, (double)vec.y, (double)vec.z, treatAsBlockPos);
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

	public int getComponentAlongAxis(Direction.Axis axis) {
		return axis.choose(this.x, this.y, this.z);
	}

	public String toString() {
		return MoreObjects.toStringHelper(this).add("x", this.getX()).add("y", this.getY()).add("z", this.getZ()).toString();
	}

	public String toShortString() {
		return this.getX() + ", " + this.getY() + ", " + this.getZ();
	}
}
