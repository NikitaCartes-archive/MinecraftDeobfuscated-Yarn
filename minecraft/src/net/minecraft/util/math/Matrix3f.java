package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import org.apache.commons.lang3.tuple.Triple;

public final class Matrix3f {
	private static final float THREE_PLUS_TWO_SQRT_TWO = 3.0F + 2.0F * (float)Math.sqrt(2.0);
	private static final float COS_PI_OVER_EIGHT = (float)Math.cos(Math.PI / 8);
	private static final float SIN_PI_OVER_EIGHT = (float)Math.sin(Math.PI / 8);
	private static final float SQRT_HALF = 1.0F / (float)Math.sqrt(2.0);
	protected float a00;
	protected float a01;
	protected float a02;
	protected float a10;
	protected float a11;
	protected float a12;
	protected float a20;
	protected float a21;
	protected float a22;

	public Matrix3f() {
	}

	public Matrix3f(Quaternion quaternion) {
		float f = quaternion.getX();
		float g = quaternion.getY();
		float h = quaternion.getZ();
		float i = quaternion.getW();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.a00 = 1.0F - k - l;
		this.a11 = 1.0F - l - j;
		this.a22 = 1.0F - j - k;
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.a10 = 2.0F * (m + r);
		this.a01 = 2.0F * (m - r);
		this.a20 = 2.0F * (o - q);
		this.a02 = 2.0F * (o + q);
		this.a21 = 2.0F * (n + p);
		this.a12 = 2.0F * (n - p);
	}

	@Environment(EnvType.CLIENT)
	public static Matrix3f scale(float x, float y, float z) {
		Matrix3f matrix3f = new Matrix3f();
		matrix3f.a00 = x;
		matrix3f.a11 = y;
		matrix3f.a22 = z;
		return matrix3f;
	}

	public Matrix3f(Matrix4f matrix) {
		this.a00 = matrix.a00;
		this.a01 = matrix.a01;
		this.a02 = matrix.a02;
		this.a10 = matrix.a10;
		this.a11 = matrix.a11;
		this.a12 = matrix.a12;
		this.a20 = matrix.a20;
		this.a21 = matrix.a21;
		this.a22 = matrix.a22;
	}

	public Matrix3f(Matrix3f source) {
		this.a00 = source.a00;
		this.a01 = source.a01;
		this.a02 = source.a02;
		this.a10 = source.a10;
		this.a11 = source.a11;
		this.a12 = source.a12;
		this.a20 = source.a20;
		this.a21 = source.a21;
		this.a22 = source.a22;
	}

	@Environment(EnvType.CLIENT)
	private static Pair<Float, Float> getSinAndCosOfRotation(float upperLeft, float diagonalAverage, float lowerRight) {
		float f = 2.0F * (upperLeft - lowerRight);
		if (THREE_PLUS_TWO_SQRT_TWO * diagonalAverage * diagonalAverage < f * f) {
			float h = MathHelper.fastInverseSqrt(diagonalAverage * diagonalAverage + f * f);
			return Pair.of(h * diagonalAverage, h * f);
		} else {
			return Pair.of(SIN_PI_OVER_EIGHT, COS_PI_OVER_EIGHT);
		}
	}

	@Environment(EnvType.CLIENT)
	private static Pair<Float, Float> method_22848(float f, float g) {
		float h = (float)Math.hypot((double)f, (double)g);
		float i = h > 1.0E-6F ? g : 0.0F;
		float j = Math.abs(f) + Math.max(h, 1.0E-6F);
		if (f < 0.0F) {
			float k = i;
			i = j;
			j = k;
		}

		float k = MathHelper.fastInverseSqrt(j * j + i * i);
		j *= k;
		i *= k;
		return Pair.of(i, j);
	}

	@Environment(EnvType.CLIENT)
	private static Quaternion method_22857(Matrix3f matrix3f) {
		Matrix3f matrix3f2 = new Matrix3f();
		Quaternion quaternion = Quaternion.IDENTITY.copy();
		if (matrix3f.a01 * matrix3f.a01 + matrix3f.a10 * matrix3f.a10 > 1.0E-6F) {
			Pair<Float, Float> pair = getSinAndCosOfRotation(matrix3f.a00, 0.5F * (matrix3f.a01 + matrix3f.a10), matrix3f.a11);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, 0.0F, float_, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.a00 = f;
			matrix3f2.a11 = f;
			matrix3f2.a10 = -g;
			matrix3f2.a01 = g;
			matrix3f2.a22 = h;
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		if (matrix3f.a02 * matrix3f.a02 + matrix3f.a20 * matrix3f.a20 > 1.0E-6F) {
			Pair<Float, Float> pair = getSinAndCosOfRotation(matrix3f.a00, 0.5F * (matrix3f.a02 + matrix3f.a20), matrix3f.a22);
			float i = -pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, i, 0.0F, float2);
			float f = float2 * float2 - i * i;
			float g = -2.0F * i * float2;
			float h = float2 * float2 + i * i;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.a00 = f;
			matrix3f2.a22 = f;
			matrix3f2.a20 = g;
			matrix3f2.a02 = -g;
			matrix3f2.a11 = h;
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		if (matrix3f.a12 * matrix3f.a12 + matrix3f.a21 * matrix3f.a21 > 1.0E-6F) {
			Pair<Float, Float> pair = getSinAndCosOfRotation(matrix3f.a11, 0.5F * (matrix3f.a12 + matrix3f.a21), matrix3f.a22);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(float_, 0.0F, 0.0F, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.a11 = f;
			matrix3f2.a22 = f;
			matrix3f2.a21 = -g;
			matrix3f2.a12 = g;
			matrix3f2.a00 = h;
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		return quaternion;
	}

	@Environment(EnvType.CLIENT)
	public void transpose() {
		float f = this.a01;
		this.a01 = this.a10;
		this.a10 = f;
		f = this.a02;
		this.a02 = this.a20;
		this.a20 = f;
		f = this.a12;
		this.a12 = this.a21;
		this.a21 = f;
	}

	@Environment(EnvType.CLIENT)
	public Triple<Quaternion, Vec3f, Quaternion> decomposeLinearTransformation() {
		Quaternion quaternion = Quaternion.IDENTITY.copy();
		Quaternion quaternion2 = Quaternion.IDENTITY.copy();
		Matrix3f matrix3f = this.copy();
		matrix3f.transpose();
		matrix3f.multiply(this);

		for (int i = 0; i < 5; i++) {
			quaternion2.hamiltonProduct(method_22857(matrix3f));
		}

		quaternion2.normalize();
		Matrix3f matrix3f2 = new Matrix3f(this);
		matrix3f2.multiply(new Matrix3f(quaternion2));
		float f = 1.0F;
		Pair<Float, Float> pair = method_22848(matrix3f2.a00, matrix3f2.a10);
		Float float_ = pair.getFirst();
		Float float2 = pair.getSecond();
		float g = float2 * float2 - float_ * float_;
		float h = -2.0F * float_ * float2;
		float j = float2 * float2 + float_ * float_;
		Quaternion quaternion3 = new Quaternion(0.0F, 0.0F, float_, float2);
		quaternion.hamiltonProduct(quaternion3);
		Matrix3f matrix3f3 = new Matrix3f();
		matrix3f3.loadIdentity();
		matrix3f3.a00 = g;
		matrix3f3.a11 = g;
		matrix3f3.a10 = h;
		matrix3f3.a01 = -h;
		matrix3f3.a22 = j;
		f *= j;
		matrix3f3.multiply(matrix3f2);
		pair = method_22848(matrix3f3.a00, matrix3f3.a20);
		float k = -pair.getFirst();
		Float float3 = pair.getSecond();
		float l = float3 * float3 - k * k;
		float m = -2.0F * k * float3;
		float n = float3 * float3 + k * k;
		Quaternion quaternion4 = new Quaternion(0.0F, k, 0.0F, float3);
		quaternion.hamiltonProduct(quaternion4);
		Matrix3f matrix3f4 = new Matrix3f();
		matrix3f4.loadIdentity();
		matrix3f4.a00 = l;
		matrix3f4.a22 = l;
		matrix3f4.a20 = -m;
		matrix3f4.a02 = m;
		matrix3f4.a11 = n;
		f *= n;
		matrix3f4.multiply(matrix3f3);
		pair = method_22848(matrix3f4.a11, matrix3f4.a21);
		Float float4 = pair.getFirst();
		Float float5 = pair.getSecond();
		float o = float5 * float5 - float4 * float4;
		float p = -2.0F * float4 * float5;
		float q = float5 * float5 + float4 * float4;
		Quaternion quaternion5 = new Quaternion(float4, 0.0F, 0.0F, float5);
		quaternion.hamiltonProduct(quaternion5);
		Matrix3f matrix3f5 = new Matrix3f();
		matrix3f5.loadIdentity();
		matrix3f5.a11 = o;
		matrix3f5.a22 = o;
		matrix3f5.a21 = p;
		matrix3f5.a12 = -p;
		matrix3f5.a00 = q;
		f *= q;
		matrix3f5.multiply(matrix3f4);
		f = 1.0F / f;
		quaternion.scale((float)Math.sqrt((double)f));
		Vec3f vec3f = new Vec3f(matrix3f5.a00 * f, matrix3f5.a11 * f, matrix3f5.a22 * f);
		return Triple.of(quaternion, vec3f, quaternion2);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Matrix3f matrix3f = (Matrix3f)object;
			return Float.compare(matrix3f.a00, this.a00) == 0
				&& Float.compare(matrix3f.a01, this.a01) == 0
				&& Float.compare(matrix3f.a02, this.a02) == 0
				&& Float.compare(matrix3f.a10, this.a10) == 0
				&& Float.compare(matrix3f.a11, this.a11) == 0
				&& Float.compare(matrix3f.a12, this.a12) == 0
				&& Float.compare(matrix3f.a20, this.a20) == 0
				&& Float.compare(matrix3f.a21, this.a21) == 0
				&& Float.compare(matrix3f.a22, this.a22) == 0;
		} else {
			return false;
		}
	}

	public int hashCode() {
		int i = this.a00 != 0.0F ? Float.floatToIntBits(this.a00) : 0;
		i = 31 * i + (this.a01 != 0.0F ? Float.floatToIntBits(this.a01) : 0);
		i = 31 * i + (this.a02 != 0.0F ? Float.floatToIntBits(this.a02) : 0);
		i = 31 * i + (this.a10 != 0.0F ? Float.floatToIntBits(this.a10) : 0);
		i = 31 * i + (this.a11 != 0.0F ? Float.floatToIntBits(this.a11) : 0);
		i = 31 * i + (this.a12 != 0.0F ? Float.floatToIntBits(this.a12) : 0);
		i = 31 * i + (this.a20 != 0.0F ? Float.floatToIntBits(this.a20) : 0);
		i = 31 * i + (this.a21 != 0.0F ? Float.floatToIntBits(this.a21) : 0);
		return 31 * i + (this.a22 != 0.0F ? Float.floatToIntBits(this.a22) : 0);
	}

	@Environment(EnvType.CLIENT)
	public void load(Matrix3f source) {
		this.a00 = source.a00;
		this.a01 = source.a01;
		this.a02 = source.a02;
		this.a10 = source.a10;
		this.a11 = source.a11;
		this.a12 = source.a12;
		this.a20 = source.a20;
		this.a21 = source.a21;
		this.a22 = source.a22;
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix3f:\n");
		stringBuilder.append(this.a00);
		stringBuilder.append(" ");
		stringBuilder.append(this.a01);
		stringBuilder.append(" ");
		stringBuilder.append(this.a02);
		stringBuilder.append("\n");
		stringBuilder.append(this.a10);
		stringBuilder.append(" ");
		stringBuilder.append(this.a11);
		stringBuilder.append(" ");
		stringBuilder.append(this.a12);
		stringBuilder.append("\n");
		stringBuilder.append(this.a20);
		stringBuilder.append(" ");
		stringBuilder.append(this.a21);
		stringBuilder.append(" ");
		stringBuilder.append(this.a22);
		stringBuilder.append("\n");
		return stringBuilder.toString();
	}

	@Environment(EnvType.CLIENT)
	public void loadIdentity() {
		this.a00 = 1.0F;
		this.a01 = 0.0F;
		this.a02 = 0.0F;
		this.a10 = 0.0F;
		this.a11 = 1.0F;
		this.a12 = 0.0F;
		this.a20 = 0.0F;
		this.a21 = 0.0F;
		this.a22 = 1.0F;
	}

	@Environment(EnvType.CLIENT)
	public float determinantAndAdjugate() {
		float f = this.a11 * this.a22 - this.a12 * this.a21;
		float g = -(this.a10 * this.a22 - this.a12 * this.a20);
		float h = this.a10 * this.a21 - this.a11 * this.a20;
		float i = -(this.a01 * this.a22 - this.a02 * this.a21);
		float j = this.a00 * this.a22 - this.a02 * this.a20;
		float k = -(this.a00 * this.a21 - this.a01 * this.a20);
		float l = this.a01 * this.a12 - this.a02 * this.a11;
		float m = -(this.a00 * this.a12 - this.a02 * this.a10);
		float n = this.a00 * this.a11 - this.a01 * this.a10;
		float o = this.a00 * f + this.a01 * g + this.a02 * h;
		this.a00 = f;
		this.a10 = g;
		this.a20 = h;
		this.a01 = i;
		this.a11 = j;
		this.a21 = k;
		this.a02 = l;
		this.a12 = m;
		this.a22 = n;
		return o;
	}

	@Environment(EnvType.CLIENT)
	public boolean invert() {
		float f = this.determinantAndAdjugate();
		if (Math.abs(f) > 1.0E-6F) {
			this.multiply(f);
			return true;
		} else {
			return false;
		}
	}

	public void set(int x, int y, float value) {
		if (x == 0) {
			if (y == 0) {
				this.a00 = value;
			} else if (y == 1) {
				this.a01 = value;
			} else {
				this.a02 = value;
			}
		} else if (x == 1) {
			if (y == 0) {
				this.a10 = value;
			} else if (y == 1) {
				this.a11 = value;
			} else {
				this.a12 = value;
			}
		} else if (y == 0) {
			this.a20 = value;
		} else if (y == 1) {
			this.a21 = value;
		} else {
			this.a22 = value;
		}
	}

	public void multiply(Matrix3f other) {
		float f = this.a00 * other.a00 + this.a01 * other.a10 + this.a02 * other.a20;
		float g = this.a00 * other.a01 + this.a01 * other.a11 + this.a02 * other.a21;
		float h = this.a00 * other.a02 + this.a01 * other.a12 + this.a02 * other.a22;
		float i = this.a10 * other.a00 + this.a11 * other.a10 + this.a12 * other.a20;
		float j = this.a10 * other.a01 + this.a11 * other.a11 + this.a12 * other.a21;
		float k = this.a10 * other.a02 + this.a11 * other.a12 + this.a12 * other.a22;
		float l = this.a20 * other.a00 + this.a21 * other.a10 + this.a22 * other.a20;
		float m = this.a20 * other.a01 + this.a21 * other.a11 + this.a22 * other.a21;
		float n = this.a20 * other.a02 + this.a21 * other.a12 + this.a22 * other.a22;
		this.a00 = f;
		this.a01 = g;
		this.a02 = h;
		this.a10 = i;
		this.a11 = j;
		this.a12 = k;
		this.a20 = l;
		this.a21 = m;
		this.a22 = n;
	}

	@Environment(EnvType.CLIENT)
	public void multiply(Quaternion quaternion) {
		this.multiply(new Matrix3f(quaternion));
	}

	@Environment(EnvType.CLIENT)
	public void multiply(float scalar) {
		this.a00 *= scalar;
		this.a01 *= scalar;
		this.a02 *= scalar;
		this.a10 *= scalar;
		this.a11 *= scalar;
		this.a12 *= scalar;
		this.a20 *= scalar;
		this.a21 *= scalar;
		this.a22 *= scalar;
	}

	@Environment(EnvType.CLIENT)
	public Matrix3f copy() {
		return new Matrix3f(this);
	}
}
