package net.minecraft.util.math;

public final class Quaternion {
	public static final Quaternion IDENTITY = new Quaternion(0.0F, 0.0F, 0.0F, 1.0F);
	private float x;
	private float y;
	private float z;
	private float w;

	public Quaternion(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Quaternion(Vec3f axis, float rotationAngle, boolean degrees) {
		if (degrees) {
			rotationAngle *= (float) (Math.PI / 180.0);
		}

		float f = sin(rotationAngle / 2.0F);
		this.x = axis.getX() * f;
		this.y = axis.getY() * f;
		this.z = axis.getZ() * f;
		this.w = cos(rotationAngle / 2.0F);
	}

	public Quaternion(float x, float y, float z, boolean degrees) {
		if (degrees) {
			x *= (float) (Math.PI / 180.0);
			y *= (float) (Math.PI / 180.0);
			z *= (float) (Math.PI / 180.0);
		}

		float f = sin(0.5F * x);
		float g = cos(0.5F * x);
		float h = sin(0.5F * y);
		float i = cos(0.5F * y);
		float j = sin(0.5F * z);
		float k = cos(0.5F * z);
		this.x = f * i * k + g * h * j;
		this.y = g * h * k - f * i * j;
		this.z = f * h * k + g * i * j;
		this.w = g * i * k - f * h * j;
	}

	public Quaternion(Quaternion other) {
		this.x = other.x;
		this.y = other.y;
		this.z = other.z;
		this.w = other.w;
	}

	public static Quaternion method_35821(float f, float g, float h) {
		Quaternion quaternion = IDENTITY.copy();
		quaternion.hamiltonProduct(new Quaternion(0.0F, (float)Math.sin((double)(f / 2.0F)), 0.0F, (float)Math.cos((double)(f / 2.0F))));
		quaternion.hamiltonProduct(new Quaternion((float)Math.sin((double)(g / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(g / 2.0F))));
		quaternion.hamiltonProduct(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(h / 2.0F)), (float)Math.cos((double)(h / 2.0F))));
		return quaternion;
	}

	public static Quaternion method_35823(Vec3f vec3f) {
		return method_35825((float)Math.toRadians((double)vec3f.getX()), (float)Math.toRadians((double)vec3f.getY()), (float)Math.toRadians((double)vec3f.getZ()));
	}

	public static Quaternion method_35826(Vec3f vec3f) {
		return method_35825(vec3f.getX(), vec3f.getY(), vec3f.getZ());
	}

	public static Quaternion method_35825(float f, float g, float h) {
		Quaternion quaternion = IDENTITY.copy();
		quaternion.hamiltonProduct(new Quaternion((float)Math.sin((double)(f / 2.0F)), 0.0F, 0.0F, (float)Math.cos((double)(f / 2.0F))));
		quaternion.hamiltonProduct(new Quaternion(0.0F, (float)Math.sin((double)(g / 2.0F)), 0.0F, (float)Math.cos((double)(g / 2.0F))));
		quaternion.hamiltonProduct(new Quaternion(0.0F, 0.0F, (float)Math.sin((double)(h / 2.0F)), (float)Math.cos((double)(h / 2.0F))));
		return quaternion;
	}

	public Vec3f method_35820() {
		float f = this.getW() * this.getW();
		float g = this.getX() * this.getX();
		float h = this.getY() * this.getY();
		float i = this.getZ() * this.getZ();
		float j = f + g + h + i;
		float k = 2.0F * this.getW() * this.getX() - 2.0F * this.getY() * this.getZ();
		float l = (float)Math.asin((double)(k / j));
		return Math.abs(k) > 0.999F * j
			? new Vec3f(2.0F * (float)Math.atan2((double)this.getX(), (double)this.getW()), l, 0.0F)
			: new Vec3f(
				(float)Math.atan2((double)(2.0F * this.getY() * this.getZ() + 2.0F * this.getX() * this.getW()), (double)(f - g - h + i)),
				l,
				(float)Math.atan2((double)(2.0F * this.getX() * this.getY() + 2.0F * this.getW() * this.getZ()), (double)(f + g - h - i))
			);
	}

	public Vec3f method_35824() {
		Vec3f vec3f = this.method_35820();
		return new Vec3f((float)Math.toDegrees((double)vec3f.getX()), (float)Math.toDegrees((double)vec3f.getY()), (float)Math.toDegrees((double)vec3f.getZ()));
	}

	public Vec3f method_35827() {
		float f = this.getW() * this.getW();
		float g = this.getX() * this.getX();
		float h = this.getY() * this.getY();
		float i = this.getZ() * this.getZ();
		float j = f + g + h + i;
		float k = 2.0F * this.getW() * this.getX() - 2.0F * this.getY() * this.getZ();
		float l = (float)Math.asin((double)(k / j));
		return Math.abs(k) > 0.999F * j
			? new Vec3f(l, 2.0F * (float)Math.atan2((double)this.getY(), (double)this.getW()), 0.0F)
			: new Vec3f(
				l,
				(float)Math.atan2((double)(2.0F * this.getX() * this.getZ() + 2.0F * this.getY() * this.getW()), (double)(f - g - h + i)),
				(float)Math.atan2((double)(2.0F * this.getX() * this.getY() + 2.0F * this.getW() * this.getZ()), (double)(f - g + h - i))
			);
	}

	public Vec3f method_35828() {
		Vec3f vec3f = this.method_35827();
		return new Vec3f((float)Math.toDegrees((double)vec3f.getX()), (float)Math.toDegrees((double)vec3f.getY()), (float)Math.toDegrees((double)vec3f.getZ()));
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Quaternion quaternion = (Quaternion)o;
			if (Float.compare(quaternion.x, this.x) != 0) {
				return false;
			} else if (Float.compare(quaternion.y, this.y) != 0) {
				return false;
			} else {
				return Float.compare(quaternion.z, this.z) != 0 ? false : Float.compare(quaternion.w, this.w) == 0;
			}
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = Float.floatToIntBits(this.x);
		i = 31 * i + Float.floatToIntBits(this.y);
		i = 31 * i + Float.floatToIntBits(this.z);
		return 31 * i + Float.floatToIntBits(this.w);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Quaternion[").append(this.getW()).append(" + ");
		stringBuilder.append(this.getX()).append("i + ");
		stringBuilder.append(this.getY()).append("j + ");
		stringBuilder.append(this.getZ()).append("k]");
		return stringBuilder.toString();
	}

	public float getX() {
		return this.x;
	}

	public float getY() {
		return this.y;
	}

	public float getZ() {
		return this.z;
	}

	public float getW() {
		return this.w;
	}

	public void hamiltonProduct(Quaternion other) {
		float f = this.getX();
		float g = this.getY();
		float h = this.getZ();
		float i = this.getW();
		float j = other.getX();
		float k = other.getY();
		float l = other.getZ();
		float m = other.getW();
		this.x = i * j + f * m + g * l - h * k;
		this.y = i * k - f * l + g * m + h * j;
		this.z = i * l + f * k - g * j + h * m;
		this.w = i * m - f * j - g * k - h * l;
	}

	public void scale(float scale) {
		this.x *= scale;
		this.y *= scale;
		this.z *= scale;
		this.w *= scale;
	}

	public void conjugate() {
		this.x = -this.x;
		this.y = -this.y;
		this.z = -this.z;
	}

	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	private static float cos(float value) {
		return (float)Math.cos((double)value);
	}

	private static float sin(float value) {
		return (float)Math.sin((double)value);
	}

	public void normalize() {
		float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
		if (f > 1.0E-6F) {
			float g = MathHelper.fastInverseSqrt(f);
			this.x *= g;
			this.y *= g;
			this.z *= g;
			this.w *= g;
		} else {
			this.x = 0.0F;
			this.y = 0.0F;
			this.z = 0.0F;
			this.w = 0.0F;
		}
	}

	public void method_35822(Quaternion quaternion, float f) {
		throw new UnsupportedOperationException();
	}

	public Quaternion copy() {
		return new Quaternion(this);
	}
}
