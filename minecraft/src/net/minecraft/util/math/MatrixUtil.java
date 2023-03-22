package net.minecraft.util.math;

import org.apache.commons.lang3.tuple.Triple;
import org.joml.Math;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
	private static final float COT_PI_OVER_8 = 3.0F + 2.0F * Math.sqrt(2.0F);
	private static final GivensPair SIN_COS_PI_OVER_8 = GivensPair.fromAngle((float) (java.lang.Math.PI / 4));

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
	 * Computes the approximate Givens rotation factors {@code (c, s) = (cos(phi), sin(phi))} for a 2×2 matrix.
	 * See Algorithm 4 of <a href="https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf">
	 * https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf</a>.
	 * 
	 * @param a11 the top-left element of the matrix
	 * @param a12 the average of the two elements on the minor diagonal
	 * @param a22 the bottom-right element of the matrix
	 */
	private static GivensPair approximateGivensQuaternion(float a11, float a12, float a22) {
		float f = 2.0F * (a11 - a22);
		return COT_PI_OVER_8 * a12 * a12 < f * f ? GivensPair.normalize(a12, f) : SIN_COS_PI_OVER_8;
	}

	/**
	 * Computes the Givens quaternion for a QR factorization.
	 * See Algorithm 4 of <a href="https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf">
	 * https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf</a>.
	 */
	private static GivensPair qrGivensQuaternion(float a1, float a2) {
		float f = (float)java.lang.Math.hypot((double)a1, (double)a2);
		float g = f > 1.0E-6F ? a2 : 0.0F;
		float h = Math.abs(a1) + Math.max(f, 1.0E-6F);
		if (a1 < 0.0F) {
			float i = g;
			g = h;
			h = i;
		}

		return GivensPair.normalize(g, h);
	}

	private static void method_49742(Matrix3f matrix3f, Matrix3f matrix3f2) {
		matrix3f.mul(matrix3f2);
		matrix3f2.transpose();
		matrix3f2.mul(matrix3f);
		matrix3f.set(matrix3f2);
	}

	private static void applyJacobiIteration(Matrix3f matrix3f, Matrix3f matrix3f2, Quaternionf quaternionf, Quaternionf quaternionf2) {
		if (matrix3f.m01 * matrix3f.m01 + matrix3f.m10 * matrix3f.m10 > 1.0E-6F) {
			GivensPair givensPair = approximateGivensQuaternion(matrix3f.m00, 0.5F * (matrix3f.m01 + matrix3f.m10), matrix3f.m11);
			Quaternionf quaternionf3 = givensPair.method_49735(quaternionf);
			quaternionf2.mul(quaternionf3);
			givensPair.method_49734(matrix3f2);
			method_49742(matrix3f, matrix3f2);
		}

		if (matrix3f.m02 * matrix3f.m02 + matrix3f.m20 * matrix3f.m20 > 1.0E-6F) {
			GivensPair givensPair = approximateGivensQuaternion(matrix3f.m00, 0.5F * (matrix3f.m02 + matrix3f.m20), matrix3f.m22).negateSin();
			Quaternionf quaternionf3 = givensPair.method_49732(quaternionf);
			quaternionf2.mul(quaternionf3);
			givensPair.method_49731(matrix3f2);
			method_49742(matrix3f, matrix3f2);
		}

		if (matrix3f.m12 * matrix3f.m12 + matrix3f.m21 * matrix3f.m21 > 1.0E-6F) {
			GivensPair givensPair = approximateGivensQuaternion(matrix3f.m11, 0.5F * (matrix3f.m12 + matrix3f.m21), matrix3f.m22);
			Quaternionf quaternionf3 = givensPair.method_49729(quaternionf);
			quaternionf2.mul(quaternionf3);
			givensPair.method_49728(matrix3f2);
			method_49742(matrix3f, matrix3f2);
		}
	}

	public static Quaternionf method_49741(Matrix3f matrix3f, int i) {
		Quaternionf quaternionf = new Quaternionf();
		Matrix3f matrix3f2 = new Matrix3f();
		Quaternionf quaternionf2 = new Quaternionf();

		for (int j = 0; j < i; j++) {
			applyJacobiIteration(matrix3f, matrix3f2, quaternionf2, quaternionf);
		}

		quaternionf.normalize();
		return quaternionf;
	}

	/**
	 * Performs an approximate singular value decomposition on a 3×3 matrix.
	 * See Algorithm 4 of <a href="https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf">
	 * https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf</a>.
	 */
	public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f A) {
		Matrix3f matrix3f = new Matrix3f(A);
		matrix3f.transpose();
		matrix3f.mul(A);
		Quaternionf quaternionf = method_49741(matrix3f, 5);
		float f = matrix3f.m00;
		float g = matrix3f.m11;
		boolean bl = (double)f < 1.0E-6;
		boolean bl2 = (double)g < 1.0E-6;
		Matrix3f matrix3f3 = A.rotate(quaternionf);
		Quaternionf quaternionf2 = new Quaternionf();
		Quaternionf quaternionf3 = new Quaternionf();
		GivensPair givensPair;
		if (bl) {
			givensPair = qrGivensQuaternion(matrix3f3.m11, -matrix3f3.m10);
		} else {
			givensPair = qrGivensQuaternion(matrix3f3.m00, matrix3f3.m01);
		}

		Quaternionf quaternionf4 = givensPair.method_49735(quaternionf3);
		Matrix3f matrix3f4 = givensPair.method_49734(matrix3f);
		quaternionf2.mul(quaternionf4);
		matrix3f4.transpose().mul(matrix3f3);
		if (bl) {
			givensPair = qrGivensQuaternion(matrix3f4.m22, -matrix3f4.m20);
		} else {
			givensPair = qrGivensQuaternion(matrix3f4.m00, matrix3f4.m02);
		}

		givensPair = givensPair.negateSin();
		Quaternionf quaternionf5 = givensPair.method_49732(quaternionf3);
		Matrix3f matrix3f5 = givensPair.method_49731(matrix3f3);
		quaternionf2.mul(quaternionf5);
		matrix3f5.transpose().mul(matrix3f4);
		if (bl2) {
			givensPair = qrGivensQuaternion(matrix3f5.m22, -matrix3f5.m21);
		} else {
			givensPair = qrGivensQuaternion(matrix3f5.m11, matrix3f5.m12);
		}

		Quaternionf quaternionf6 = givensPair.method_49729(quaternionf3);
		Matrix3f matrix3f6 = givensPair.method_49728(matrix3f4);
		quaternionf2.mul(quaternionf6);
		matrix3f6.transpose().mul(matrix3f5);
		Vector3f vector3f = new Vector3f(matrix3f6.m00, matrix3f6.m11, matrix3f6.m22);
		return Triple.of(quaternionf2, vector3f, quaternionf.conjugate());
	}
}
