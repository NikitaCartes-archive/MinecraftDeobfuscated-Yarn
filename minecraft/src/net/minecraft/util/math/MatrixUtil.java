package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
	private static final float field_40746 = 3.0F + 2.0F * (float)Math.sqrt(2.0);
	private static final float COS_PI_OVER_8 = (float)Math.cos(Math.PI / 8);
	private static final float SIN_PI_OVER_8 = (float)Math.sin(Math.PI / 8);

	private MatrixUtil() {
	}

	public static Matrix4f scale(Matrix4f matrix, float scalar) {
		return matrix.set(
			matrix.m00() * scalar,
			matrix.m01() * scalar,
			matrix.m02() * scalar,
			matrix.m03() * scalar,
			matrix.m10() * scalar,
			matrix.m11() * scalar,
			matrix.m12() * scalar,
			matrix.m13() * scalar,
			matrix.m20() * scalar,
			matrix.m21() * scalar,
			matrix.m22() * scalar,
			matrix.m23() * scalar,
			matrix.m30() * scalar,
			matrix.m31() * scalar,
			matrix.m32() * scalar,
			matrix.m33() * scalar
		);
	}

	private static Pair<Float, Float> method_46411(float f, float g, float h) {
		float i = 2.0F * (f - h);
		if (field_40746 * g * g < i * i) {
			float k = MathHelper.inverseSqrt(g * g + i * i);
			return Pair.of(k * g, k * i);
		} else {
			return Pair.of(SIN_PI_OVER_8, COS_PI_OVER_8);
		}
	}

	private static Pair<Float, Float> method_46410(float f, float g) {
		float h = (float)Math.hypot((double)f, (double)g);
		float i = h > 1.0E-6F ? g : 0.0F;
		float j = Math.abs(f) + Math.max(h, 1.0E-6F);
		if (f < 0.0F) {
			float k = i;
			i = j;
			j = k;
		}

		float k = MathHelper.inverseSqrt(j * j + i * i);
		j *= k;
		i *= k;
		return Pair.of(i, j);
	}

	private static Quaternionf method_46415(Matrix3f matrix3f) {
		Matrix3f matrix3f2 = new Matrix3f();
		Quaternionf quaternionf = new Quaternionf();
		if (matrix3f.m01 * matrix3f.m01 + matrix3f.m10 * matrix3f.m10 > 1.0E-6F) {
			Pair<Float, Float> pair = method_46411(matrix3f.m00, 0.5F * (matrix3f.m01 + matrix3f.m10), matrix3f.m11);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternionf quaternionf2 = new Quaternionf(0.0F, 0.0F, float_, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternionf.mul(quaternionf2);
			matrix3f2.m00 = f;
			matrix3f2.m11 = f;
			matrix3f2.m01 = -g;
			matrix3f2.m10 = g;
			matrix3f2.m22 = h;
			matrix3f.mul(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.mul(matrix3f);
			matrix3f.set(matrix3f2);
		}

		if (matrix3f.m02 * matrix3f.m02 + matrix3f.m20 * matrix3f.m20 > 1.0E-6F) {
			Pair<Float, Float> pair = method_46411(matrix3f.m00, 0.5F * (matrix3f.m02 + matrix3f.m20), matrix3f.m22);
			float i = -pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternionf quaternionf2 = new Quaternionf(0.0F, i, 0.0F, float2);
			float f = float2 * float2 - i * i;
			float g = -2.0F * i * float2;
			float h = float2 * float2 + i * i;
			quaternionf.mul(quaternionf2);
			matrix3f2.m00 = f;
			matrix3f2.m22 = f;
			matrix3f2.m02 = g;
			matrix3f2.m20 = -g;
			matrix3f2.m11 = h;
			matrix3f.mul(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.mul(matrix3f);
			matrix3f.set(matrix3f2);
		}

		if (matrix3f.m12 * matrix3f.m12 + matrix3f.m21 * matrix3f.m21 > 1.0E-6F) {
			Pair<Float, Float> pair = method_46411(matrix3f.m11, 0.5F * (matrix3f.m12 + matrix3f.m21), matrix3f.m22);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternionf quaternionf2 = new Quaternionf(float_, 0.0F, 0.0F, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternionf.mul(quaternionf2);
			matrix3f2.m11 = f;
			matrix3f2.m22 = f;
			matrix3f2.m12 = -g;
			matrix3f2.m21 = g;
			matrix3f2.m00 = h;
			matrix3f.mul(matrix3f2);
			matrix3f2.transpose();
			matrix3f2.mul(matrix3f);
			matrix3f.set(matrix3f2);
		}

		return quaternionf;
	}

	public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f matrix3f) {
		Quaternionf quaternionf = new Quaternionf();
		Quaternionf quaternionf2 = new Quaternionf();
		Matrix3f matrix3f2 = new Matrix3f(matrix3f);
		matrix3f2.transpose();
		matrix3f2.mul(matrix3f);

		for (int i = 0; i < 5; i++) {
			quaternionf2.mul(method_46415(matrix3f2));
		}

		quaternionf2.normalize();
		Matrix3f matrix3f3 = new Matrix3f(matrix3f);
		matrix3f3.rotate(quaternionf2);
		float f = 1.0F;
		Pair<Float, Float> pair = method_46410(matrix3f3.m00, matrix3f3.m01);
		Float float_ = pair.getFirst();
		Float float2 = pair.getSecond();
		float g = float2 * float2 - float_ * float_;
		float h = -2.0F * float_ * float2;
		float j = float2 * float2 + float_ * float_;
		Quaternionf quaternionf3 = new Quaternionf(0.0F, 0.0F, float_, float2);
		quaternionf.mul(quaternionf3);
		Matrix3f matrix3f4 = new Matrix3f();
		matrix3f4.m00 = g;
		matrix3f4.m11 = g;
		matrix3f4.m01 = h;
		matrix3f4.m10 = -h;
		matrix3f4.m22 = j;
		f *= j;
		matrix3f4.mul(matrix3f3);
		pair = method_46410(matrix3f4.m00, matrix3f4.m02);
		float k = -pair.getFirst();
		Float float3 = pair.getSecond();
		float l = float3 * float3 - k * k;
		float m = -2.0F * k * float3;
		float n = float3 * float3 + k * k;
		Quaternionf quaternionf4 = new Quaternionf(0.0F, k, 0.0F, float3);
		quaternionf.mul(quaternionf4);
		Matrix3f matrix3f5 = new Matrix3f();
		matrix3f5.m00 = l;
		matrix3f5.m22 = l;
		matrix3f5.m02 = -m;
		matrix3f5.m20 = m;
		matrix3f5.m11 = n;
		f *= n;
		matrix3f5.mul(matrix3f4);
		pair = method_46410(matrix3f5.m11, matrix3f5.m12);
		Float float4 = pair.getFirst();
		Float float5 = pair.getSecond();
		float o = float5 * float5 - float4 * float4;
		float p = -2.0F * float4 * float5;
		float q = float5 * float5 + float4 * float4;
		Quaternionf quaternionf5 = new Quaternionf(float4, 0.0F, 0.0F, float5);
		quaternionf.mul(quaternionf5);
		Matrix3f matrix3f6 = new Matrix3f();
		matrix3f6.m11 = o;
		matrix3f6.m22 = o;
		matrix3f6.m12 = p;
		matrix3f6.m21 = -p;
		matrix3f6.m00 = q;
		f *= q;
		matrix3f6.mul(matrix3f5);
		f = 1.0F / f;
		quaternionf.mul((float)Math.sqrt((double)f));
		Vector3f vector3f = new Vector3f(matrix3f6.m00 * f, matrix3f6.m11 * f, matrix3f6.m22 * f);
		return Triple.of(quaternionf, vector3f, quaternionf2);
	}

	public static Matrix4x3f affineTransform(Matrix4f matrix) {
		float f = 1.0F / matrix.m33();
		return new Matrix4x3f().set(matrix).scaleLocal(f, f, f);
	}
}
