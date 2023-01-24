package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
	private static final float COT_PI_OVER_8 = 3.0F + 2.0F * (float)Math.sqrt(2.0);
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

	/**
	 * Compute the approximate Givens rotation factors (c, s) = (cos(phi), sin(phi)) for a 2×2 matrix.
	 * 
	 * @return a pair (c, s) = (cos(phi), sin(phi))
	 * 
	 * @see Algorithm 2 of https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf
	 * the top-left element of the matrix
	 * the average of the two elements on the minor diagonal
	 * the bottom-right element of the matrix
	 */
	private static Pair<Float, Float> approximateGivensQuaternion(float a11, float a12, float a22) {
		float f = 2.0F * (a11 - a22);
		if (COT_PI_OVER_8 * a12 * a12 < f * f) {
			float h = MathHelper.inverseSqrt(a12 * a12 + f * f);
			return Pair.of(h * a12, h * f);
		} else {
			return Pair.of(SIN_PI_OVER_8, COS_PI_OVER_8);
		}
	}

	/**
	 * Compute the Givens quaternion for a QR factorization.
	 * 
	 * @return a pair (c, s) = (cos(theta), sin(theta))
	 * @see Algorithm 4 of https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf
	 */
	private static Pair<Float, Float> qrGivensQuaternion(float a1, float a2) {
		float f = (float)Math.hypot((double)a1, (double)a2);
		float g = f > 1.0E-6F ? a2 : 0.0F;
		float h = Math.abs(a1) + Math.max(f, 1.0E-6F);
		if (a1 < 0.0F) {
			float i = g;
			g = h;
			h = i;
		}

		float i = MathHelper.inverseSqrt(h * h + g * g);
		h *= i;
		g *= i;
		return Pair.of(g, h);
	}

	private static Quaternionf applyJacobiIteration(Matrix3f S) {
		Matrix3f matrix3f = new Matrix3f();
		Quaternionf quaternionf = new Quaternionf();
		if (S.m01 * S.m01 + S.m10 * S.m10 > 1.0E-6F) {
			Pair<Float, Float> pair = approximateGivensQuaternion(S.m00, 0.5F * (S.m01 + S.m10), S.m11);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternionf quaternionf2 = new Quaternionf(0.0F, 0.0F, float_, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternionf.mul(quaternionf2);
			matrix3f.m00 = f;
			matrix3f.m11 = f;
			matrix3f.m01 = -g;
			matrix3f.m10 = g;
			matrix3f.m22 = h;
			S.mul(matrix3f);
			matrix3f.transpose();
			matrix3f.mul(S);
			S.set(matrix3f);
		}

		if (S.m02 * S.m02 + S.m20 * S.m20 > 1.0E-6F) {
			Pair<Float, Float> pair = approximateGivensQuaternion(S.m00, 0.5F * (S.m02 + S.m20), S.m22);
			float i = -pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternionf quaternionf2 = new Quaternionf(0.0F, i, 0.0F, float2);
			float f = float2 * float2 - i * i;
			float g = -2.0F * i * float2;
			float h = float2 * float2 + i * i;
			quaternionf.mul(quaternionf2);
			matrix3f.m00 = f;
			matrix3f.m22 = f;
			matrix3f.m02 = g;
			matrix3f.m20 = -g;
			matrix3f.m11 = h;
			S.mul(matrix3f);
			matrix3f.transpose();
			matrix3f.mul(S);
			S.set(matrix3f);
		}

		if (S.m12 * S.m12 + S.m21 * S.m21 > 1.0E-6F) {
			Pair<Float, Float> pair = approximateGivensQuaternion(S.m11, 0.5F * (S.m12 + S.m21), S.m22);
			Float float_ = pair.getFirst();
			Float float2 = pair.getSecond();
			Quaternionf quaternionf2 = new Quaternionf(float_, 0.0F, 0.0F, float2);
			float f = float2 * float2 - float_ * float_;
			float g = -2.0F * float_ * float2;
			float h = float2 * float2 + float_ * float_;
			quaternionf.mul(quaternionf2);
			matrix3f.m11 = f;
			matrix3f.m22 = f;
			matrix3f.m12 = -g;
			matrix3f.m21 = g;
			matrix3f.m00 = h;
			S.mul(matrix3f);
			matrix3f.transpose();
			matrix3f.mul(S);
			S.set(matrix3f);
		}

		return quaternionf;
	}

	/**
	 * Performs an approximate singular value decomposition on a 3×3 matrix.
	 * 
	 * @see https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf
	 */
	public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f A) {
		Quaternionf quaternionf = new Quaternionf();
		Quaternionf quaternionf2 = new Quaternionf();
		Matrix3f matrix3f = new Matrix3f(A);
		matrix3f.transpose();
		matrix3f.mul(A);

		for (int i = 0; i < 5; i++) {
			quaternionf2.mul(applyJacobiIteration(matrix3f));
		}

		quaternionf2.normalize();
		Matrix3f matrix3f2 = new Matrix3f(A);
		matrix3f2.rotate(quaternionf2);
		float f = 1.0F;
		Pair<Float, Float> pair = qrGivensQuaternion(matrix3f2.m00, matrix3f2.m01);
		Float float_ = pair.getFirst();
		Float float2 = pair.getSecond();
		float g = float2 * float2 - float_ * float_;
		float h = -2.0F * float_ * float2;
		float j = float2 * float2 + float_ * float_;
		Quaternionf quaternionf3 = new Quaternionf(0.0F, 0.0F, float_, float2);
		quaternionf.mul(quaternionf3);
		Matrix3f matrix3f3 = new Matrix3f();
		matrix3f3.m00 = g;
		matrix3f3.m11 = g;
		matrix3f3.m01 = h;
		matrix3f3.m10 = -h;
		matrix3f3.m22 = j;
		f *= j;
		matrix3f3.mul(matrix3f2);
		pair = qrGivensQuaternion(matrix3f3.m00, matrix3f3.m02);
		float k = -pair.getFirst();
		Float float3 = pair.getSecond();
		float l = float3 * float3 - k * k;
		float m = -2.0F * k * float3;
		float n = float3 * float3 + k * k;
		Quaternionf quaternionf4 = new Quaternionf(0.0F, k, 0.0F, float3);
		quaternionf.mul(quaternionf4);
		Matrix3f matrix3f4 = new Matrix3f();
		matrix3f4.m00 = l;
		matrix3f4.m22 = l;
		matrix3f4.m02 = -m;
		matrix3f4.m20 = m;
		matrix3f4.m11 = n;
		f *= n;
		matrix3f4.mul(matrix3f3);
		pair = qrGivensQuaternion(matrix3f4.m11, matrix3f4.m12);
		Float float4 = pair.getFirst();
		Float float5 = pair.getSecond();
		float o = float5 * float5 - float4 * float4;
		float p = -2.0F * float4 * float5;
		float q = float5 * float5 + float4 * float4;
		Quaternionf quaternionf5 = new Quaternionf(float4, 0.0F, 0.0F, float5);
		quaternionf.mul(quaternionf5);
		Matrix3f matrix3f5 = new Matrix3f();
		matrix3f5.m11 = o;
		matrix3f5.m22 = o;
		matrix3f5.m12 = p;
		matrix3f5.m21 = -p;
		matrix3f5.m00 = q;
		f *= q;
		matrix3f5.mul(matrix3f4);
		f = 1.0F / f;
		quaternionf.mul((float)Math.sqrt((double)f));
		Vector3f vector3f = new Vector3f(matrix3f5.m00 * f, matrix3f5.m11 * f, matrix3f5.m22 * f);
		return Triple.of(quaternionf, vector3f, quaternionf2);
	}

	public static Matrix4x3f affineTransform(Matrix4f matrix) {
		float f = 1.0F / matrix.m33();
		return new Matrix4x3f().set(matrix).scaleLocal(f, f, f);
	}
}
