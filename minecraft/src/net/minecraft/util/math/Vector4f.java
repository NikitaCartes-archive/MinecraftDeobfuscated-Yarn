package net.minecraft.util.math;

/**
 * @implNote Even though this is used only on the client, it accesses protected
 * fields from {@link Matrix4f}, which requires them to be in the same package.
 */
public class Vector4f {
	private float x;
	private float y;
	private float z;
	private float w;

	public Vector4f() {
	}

	public Vector4f(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public Vector4f(Vec3f vector) {
		this(vector.getX(), vector.getY(), vector.getZ(), 1.0F);
	}

	public boolean equals(Object o) {
		if (this == o) {
			return true;
		} else if (o != null && this.getClass() == o.getClass()) {
			Vector4f vector4f = (Vector4f)o;
			if (Float.compare(vector4f.x, this.x) != 0) {
				return false;
			} else if (Float.compare(vector4f.y, this.y) != 0) {
				return false;
			} else {
				return Float.compare(vector4f.z, this.z) != 0 ? false : Float.compare(vector4f.w, this.w) == 0;
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

	public void method_35927(float f) {
		this.x *= f;
		this.y *= f;
		this.z *= f;
		this.w *= f;
	}

	public void multiplyComponentwise(Vec3f vector) {
		this.x = this.x * vector.getX();
		this.y = this.y * vector.getY();
		this.z = this.z * vector.getZ();
	}

	public void set(float x, float y, float z, float w) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.w = w;
	}

	public void method_35929(float f, float g, float h, float i) {
		this.x += f;
		this.y += g;
		this.z += h;
		this.w += i;
	}

	public float dotProduct(Vector4f other) {
		return this.x * other.x + this.y * other.y + this.z * other.z + this.w * other.w;
	}

	public boolean normalize() {
		float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
		if ((double)f < 1.0E-5) {
			return false;
		} else {
			float g = MathHelper.fastInverseSqrt(f);
			this.x *= g;
			this.y *= g;
			this.z *= g;
			this.w *= g;
			return true;
		}
	}

	public void transform(Matrix4f matrix) {
		float f = this.x;
		float g = this.y;
		float h = this.z;
		float i = this.w;
		this.x = matrix.a00 * f + matrix.a01 * g + matrix.a02 * h + matrix.a03 * i;
		this.y = matrix.a10 * f + matrix.a11 * g + matrix.a12 * h + matrix.a13 * i;
		this.z = matrix.a20 * f + matrix.a21 * g + matrix.a22 * h + matrix.a23 * i;
		this.w = matrix.a30 * f + matrix.a31 * g + matrix.a32 * h + matrix.a33 * i;
	}

	public void rotate(Quaternion rotation) {
		Quaternion quaternion = new Quaternion(rotation);
		quaternion.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0F));
		Quaternion quaternion2 = new Quaternion(rotation);
		quaternion2.conjugate();
		quaternion.hamiltonProduct(quaternion2);
		this.set(quaternion.getX(), quaternion.getY(), quaternion.getZ(), this.getW());
	}

	public void normalizeProjectiveCoordinates() {
		this.x = this.x / this.w;
		this.y = this.y / this.w;
		this.z = this.z / this.w;
		this.w = 1.0F;
	}

	public void method_35928(Vector4f vector4f, float f) {
		float g = 1.0F - f;
		this.x = this.x * g + vector4f.x * f;
		this.y = this.y * g + vector4f.y * f;
		this.z = this.z * g + vector4f.z * f;
		this.w = this.w * g + vector4f.w * f;
	}

	public String toString() {
		return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
	}
}
