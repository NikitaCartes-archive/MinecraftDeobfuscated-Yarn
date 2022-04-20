package net.minecraft;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class class_7422 {
	private static final double field_39015 = 4096.0;
	private Vec3d field_39016 = Vec3d.ZERO;

	private static long method_43487(double d) {
		return MathHelper.lfloor(d * 4096.0);
	}

	private static double method_43488(long l) {
		return (double)l / 4096.0;
	}

	public Vec3d method_43489(long l, long m, long n) {
		if (l == 0L && m == 0L && n == 0L) {
			return this.field_39016;
		} else {
			double d = l == 0L ? this.field_39016.x : method_43488(method_43487(this.field_39016.x) + l);
			double e = m == 0L ? this.field_39016.y : method_43488(method_43487(this.field_39016.y) + m);
			double f = n == 0L ? this.field_39016.z : method_43488(method_43487(this.field_39016.z) + n);
			return new Vec3d(d, e, f);
		}
	}

	public long method_43490(Vec3d vec3d) {
		return method_43487(vec3d.x - this.field_39016.x);
	}

	public long method_43491(Vec3d vec3d) {
		return method_43487(vec3d.y - this.field_39016.y);
	}

	public long method_43492(Vec3d vec3d) {
		return method_43487(vec3d.z - this.field_39016.z);
	}

	public Vec3d method_43493(Vec3d vec3d) {
		return vec3d.subtract(this.field_39016);
	}

	public void method_43494(Vec3d vec3d) {
		this.field_39016 = vec3d;
	}
}
