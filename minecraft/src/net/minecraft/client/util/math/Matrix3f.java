package net.minecraft.client.util.math;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(EnvType.CLIENT)
public final class Matrix3f {
	private static final float THREE_PLUS_TWO_SQRT_TWO = 3.0F + 2.0F * (float)Math.sqrt(2.0);
	private static final float COS_PI_OVER_EIGHT = (float)Math.cos(Math.PI / 8);
	private static final float SIN_PI_OVER_EIGHT = (float)Math.sin(Math.PI / 8);
	private static final float SQRT_HALF = 1.0F / (float)Math.sqrt(2.0);
	private final float[] components;

	public Matrix3f() {
		this(new float[9]);
	}

	private Matrix3f(float[] fs) {
		this.components = fs;
	}

	public Matrix3f(Quaternion quaternion) {
		this();
		float f = quaternion.getB();
		float g = quaternion.getC();
		float h = quaternion.getD();
		float i = quaternion.getA();
		float j = 2.0F * f * f;
		float k = 2.0F * g * g;
		float l = 2.0F * h * h;
		this.set(0, 0, 1.0F - k - l);
		this.set(1, 1, 1.0F - l - j);
		this.set(2, 2, 1.0F - j - k);
		float m = f * g;
		float n = g * h;
		float o = h * f;
		float p = f * i;
		float q = g * i;
		float r = h * i;
		this.set(1, 0, 2.0F * (m + r));
		this.set(0, 1, 2.0F * (m - r));
		this.set(2, 0, 2.0F * (o - q));
		this.set(0, 2, 2.0F * (o + q));
		this.set(2, 1, 2.0F * (n + p));
		this.set(1, 2, 2.0F * (n - p));
	}

	public Matrix3f(Matrix4f matrix) {
		this();

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.components[j + i * 3] = matrix.get(j, i);
			}
		}
	}

	public Matrix3f(Matrix3f matrix) {
		this(Arrays.copyOf(matrix.components, 9));
	}

	private static Pair<Float, Float> method_22849(float f, float g, float h) {
		float i = 2.0F * (f - h);
		if (THREE_PLUS_TWO_SQRT_TWO * g * g < i * i) {
			float k = MathHelper.fastInverseSqrt(g * g + i * i);
			return Pair.of(k * g, k * i);
		} else {
			return Pair.of(SIN_PI_OVER_EIGHT, COS_PI_OVER_EIGHT);
		}
	}

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

	private static Quaternion method_22857(Matrix3f matrix3f) {
		Matrix3f matrix3f2 = new Matrix3f();
		Quaternion quaternion = Quaternion.IDENTITY.copy();
		if (matrix3f.get(0, 1) * matrix3f.get(0, 1) + matrix3f.get(1, 0) * matrix3f.get(1, 0) > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(matrix3f.get(0, 0), 0.5F * (matrix3f.get(0, 1) + matrix3f.get(1, 0)), matrix3f.get(1, 1));
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, 0.0F, float_, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.set(0, 0, f);
			matrix3f2.set(1, 1, f);
			matrix3f2.set(1, 0, -g);
			matrix3f2.set(0, 1, g);
			matrix3f2.set(2, 2, h);
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		if (matrix3f.get(0, 2) * matrix3f.get(0, 2) + matrix3f.get(2, 0) * matrix3f.get(2, 0) > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(matrix3f.get(0, 0), 0.5F * (matrix3f.get(0, 2) + matrix3f.get(2, 0)), matrix3f.get(2, 2));
			float i = -pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(0.0F, i, 0.0F, float2);
			float f = float2 * float2 - i * i;
			float g = -2.0F * i * float2;
			float h = float2 * float2 + i * i;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.set(0, 0, f);
			matrix3f2.set(2, 2, f);
			matrix3f2.set(2, 0, g);
			matrix3f2.set(0, 2, -g);
			matrix3f2.set(1, 1, h);
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		if (matrix3f.get(1, 2) * matrix3f.get(1, 2) + matrix3f.get(2, 1) * matrix3f.get(2, 1) > 1.0E-6F) {
			Pair<Float, Float> pair = method_22849(matrix3f.get(1, 1), 0.5F * (matrix3f.get(1, 2) + matrix3f.get(2, 1)), matrix3f.get(2, 2));
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternion quaternion2 = new Quaternion(float_, 0.0F, 0.0F, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternion.hamiltonProduct(quaternion2);
			matrix3f2.loadIdentity();
			matrix3f2.set(1, 1, f);
			matrix3f2.set(2, 2, f);
			matrix3f2.set(2, 1, -g);
			matrix3f2.set(1, 2, g);
			matrix3f2.set(0, 0, h);
			matrix3f.multiply(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.multiply(matrix3f);
			matrix3f.load(matrix3f2);
		}

		return quaternion;
	}

	public void transpose() {
		this.transpose(0, 1);
		this.transpose(0, 2);
		this.transpose(1, 2);
	}

	private void transpose(int row, int column) {
		float f = this.components[row + 3 * column];
		this.components[row + 3 * column] = this.components[column + 3 * row];
		this.components[column + 3 * row] = f;
	}

	public Triple<Quaternion, Vector3f, Quaternion> method_22853() {
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
		Pair<Float, Float> pair = method_22848(matrix3f2.get(0, 0), matrix3f2.get(1, 0));
		Float float_ = pair.getFirst();
		Float float2 = pair.getSecond();
		float g = float2 * float2 - float_ * float_;
		float h = -2.0F * float_ * float2;
		float j = float2 * float2 + float_ * float_;
		Quaternion quaternion3 = new Quaternion(0.0F, 0.0F, float_, float2);
		quaternion.hamiltonProduct(quaternion3);
		Matrix3f matrix3f3 = new Matrix3f();
		matrix3f3.loadIdentity();
		matrix3f3.set(0, 0, g);
		matrix3f3.set(1, 1, g);
		matrix3f3.set(1, 0, h);
		matrix3f3.set(0, 1, -h);
		matrix3f3.set(2, 2, j);
		f *= j;
		matrix3f3.multiply(matrix3f2);
		pair = method_22848(matrix3f3.get(0, 0), matrix3f3.get(2, 0));
		float k = -pair.getFirst();
		Float float3 = pair.getSecond();
		float l = float3 * float3 - k * k;
		float m = -2.0F * k * float3;
		float n = float3 * float3 + k * k;
		Quaternion quaternion4 = new Quaternion(0.0F, k, 0.0F, float3);
		quaternion.hamiltonProduct(quaternion4);
		Matrix3f matrix3f4 = new Matrix3f();
		matrix3f4.loadIdentity();
		matrix3f4.set(0, 0, l);
		matrix3f4.set(2, 2, l);
		matrix3f4.set(2, 0, -m);
		matrix3f4.set(0, 2, m);
		matrix3f4.set(1, 1, n);
		f *= n;
		matrix3f4.multiply(matrix3f3);
		pair = method_22848(matrix3f4.get(1, 1), matrix3f4.get(2, 1));
		Float float4 = pair.getFirst();
		Float float5 = pair.getSecond();
		float o = float5 * float5 - float4 * float4;
		float p = -2.0F * float4 * float5;
		float q = float5 * float5 + float4 * float4;
		Quaternion quaternion5 = new Quaternion(float4, 0.0F, 0.0F, float5);
		quaternion.hamiltonProduct(quaternion5);
		Matrix3f matrix3f5 = new Matrix3f();
		matrix3f5.loadIdentity();
		matrix3f5.set(1, 1, o);
		matrix3f5.set(2, 2, o);
		matrix3f5.set(2, 1, p);
		matrix3f5.set(1, 2, -p);
		matrix3f5.set(0, 0, q);
		f *= q;
		matrix3f5.multiply(matrix3f4);
		f = 1.0F / f;
		quaternion.scale((float)Math.sqrt((double)f));
		Vector3f vector3f = new Vector3f(matrix3f5.get(0, 0) * f, matrix3f5.get(1, 1) * f, matrix3f5.get(2, 2) * f);
		return Triple.of(quaternion, vector3f, quaternion2);
	}

	public boolean equals(Object object) {
		if (this == object) {
			return true;
		} else if (object != null && this.getClass() == object.getClass()) {
			Matrix3f matrix3f = (Matrix3f)object;
			return Arrays.equals(this.components, matrix3f.components);
		} else {
			return false;
		}
	}

	public int hashCode() {
		return Arrays.hashCode(this.components);
	}

	public void load(Matrix3f matrix) {
		System.arraycopy(matrix.components, 0, this.components, 0, 9);
	}

	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("Matrix3f:\n");

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				stringBuilder.append(this.components[i + j * 3]);
				if (j != 2) {
					stringBuilder.append(" ");
				}
			}

			stringBuilder.append("\n");
		}

		return stringBuilder.toString();
	}

	public void loadIdentity() {
		this.components[0] = 1.0F;
		this.components[1] = 0.0F;
		this.components[2] = 0.0F;
		this.components[3] = 0.0F;
		this.components[4] = 1.0F;
		this.components[5] = 0.0F;
		this.components[6] = 0.0F;
		this.components[7] = 0.0F;
		this.components[8] = 1.0F;
	}

	public float method_23731() {
		float f = this.method_23730(1, 2, 1, 2);
		float g = -this.method_23730(1, 2, 0, 2);
		float h = this.method_23730(1, 2, 0, 1);
		float i = -this.method_23730(0, 2, 1, 2);
		float j = this.method_23730(0, 2, 0, 2);
		float k = -this.method_23730(0, 2, 0, 1);
		float l = this.method_23730(0, 1, 1, 2);
		float m = -this.method_23730(0, 1, 0, 2);
		float n = this.method_23730(0, 1, 0, 1);
		float o = this.get(0, 0) * f + this.get(0, 1) * g + this.get(0, 2) * h;
		this.set(0, 0, f);
		this.set(1, 0, g);
		this.set(2, 0, h);
		this.set(0, 1, i);
		this.set(1, 1, j);
		this.set(2, 1, k);
		this.set(0, 2, l);
		this.set(1, 2, m);
		this.set(2, 2, n);
		return o;
	}

	public boolean method_23732() {
		float f = this.method_23731();
		if (Math.abs(f) > 1.0E-6F) {
			this.method_23729(f);
			return true;
		} else {
			return false;
		}
	}

	private float method_23730(int i, int j, int k, int l) {
		return this.get(i, k) * this.get(j, l) - this.get(i, l) * this.get(j, k);
	}

	public float get(int row, int column) {
		return this.components[3 * column + row];
	}

	public void set(int row, int column, float value) {
		this.components[3 * column + row] = value;
	}

	public void multiply(Matrix3f matrix) {
		float[] fs = Arrays.copyOf(this.components, 9);

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				this.components[i + j * 3] = 0.0F;

				for (int k = 0; k < 3; k++) {
					this.components[i + j * 3] = this.components[i + j * 3] + fs[i + k * 3] * matrix.components[k + j * 3];
				}
			}
		}
	}

	public void multiply(Quaternion quaternion) {
		this.multiply(new Matrix3f(quaternion));
	}

	public void method_23729(float f) {
		for (int i = 0; i < 9; i++) {
			this.components[i] = this.components[i] * f;
		}
	}

	public Matrix3f copy() {
		return new Matrix3f((float[])this.components.clone());
	}
}
