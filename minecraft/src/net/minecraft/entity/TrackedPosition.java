package net.minecraft.entity;

import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.VisibleForTesting;

public class TrackedPosition {
	private static final double COORDINATE_SCALE = 4096.0;
	private Vec3d pos = Vec3d.ZERO;

	@VisibleForTesting
	static long pack(double value) {
		return Math.round(value * 4096.0);
	}

	@VisibleForTesting
	static double unpack(long value) {
		return (double)value / 4096.0;
	}

	public Vec3d withDelta(long x, long y, long z) {
		if (x == 0L && y == 0L && z == 0L) {
			return this.pos;
		} else {
			double d = x == 0L ? this.pos.x : unpack(pack(this.pos.x) + x);
			double e = y == 0L ? this.pos.y : unpack(pack(this.pos.y) + y);
			double f = z == 0L ? this.pos.z : unpack(pack(this.pos.z) + z);
			return new Vec3d(d, e, f);
		}
	}

	public long getDeltaX(Vec3d pos) {
		return pack(pos.x) - pack(this.pos.x);
	}

	public long getDeltaY(Vec3d pos) {
		return pack(pos.y) - pack(this.pos.y);
	}

	public long getDeltaZ(Vec3d pos) {
		return pack(pos.z) - pack(this.pos.z);
	}

	public Vec3d subtract(Vec3d pos) {
		return pos.subtract(this.pos);
	}

	public void setPos(Vec3d pos) {
		this.pos = pos;
	}
}
