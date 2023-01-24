/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import com.mojang.datafixers.util.Pair;
import net.minecraft.util.math.MathHelper;
import org.apache.commons.lang3.tuple.Triple;
import org.joml.Matrix3f;
import org.joml.Matrix4f;
import org.joml.Matrix4x3f;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class MatrixUtil {
    private static final float COT_PI_OVER_8 = 3.0f + 2.0f * (float)Math.sqrt(2.0);
    private static final float COS_PI_OVER_8 = (float)Math.cos(0.39269908169872414);
    private static final float SIN_PI_OVER_8 = (float)Math.sin(0.39269908169872414);

    private MatrixUtil() {
    }

    public static Matrix4f scale(Matrix4f matrix, float scalar) {
        return matrix.set(matrix.m00() * scalar, matrix.m01() * scalar, matrix.m02() * scalar, matrix.m03() * scalar, matrix.m10() * scalar, matrix.m11() * scalar, matrix.m12() * scalar, matrix.m13() * scalar, matrix.m20() * scalar, matrix.m21() * scalar, matrix.m22() * scalar, matrix.m23() * scalar, matrix.m30() * scalar, matrix.m31() * scalar, matrix.m32() * scalar, matrix.m33() * scalar);
    }

    /**
     * Compute the approximate Givens rotation factors (c, s) = (cos(phi), sin(phi)) for a 2\u00d72 matrix.
     * 
     * @return a pair (c, s) = (cos(phi), sin(phi))
     * 
     * @see Algorithm 2 of https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf
     * the top-left element of the matrix
     * the average of the two elements on the minor diagonal
     * the bottom-right element of the matrix
     */
    private static Pair<Float, Float> approximateGivensQuaternion(float a11, float a12, float a22) {
        float g = a12;
        float f = 2.0f * (a11 - a22);
        if (COT_PI_OVER_8 * g * g < f * f) {
            float h = MathHelper.inverseSqrt(g * g + f * f);
            return Pair.of(Float.valueOf(h * g), Float.valueOf(h * f));
        }
        return Pair.of(Float.valueOf(SIN_PI_OVER_8), Float.valueOf(COS_PI_OVER_8));
    }

    /**
     * Compute the Givens quaternion for a QR factorization.
     * 
     * @return a pair (c, s) = (cos(theta), sin(theta))
     * @see Algorithm 4 of https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf
     */
    private static Pair<Float, Float> qrGivensQuaternion(float a1, float a2) {
        float i;
        float f = (float)Math.hypot(a1, a2);
        float g = f > 1.0E-6f ? a2 : 0.0f;
        float h = Math.abs(a1) + Math.max(f, 1.0E-6f);
        if (a1 < 0.0f) {
            i = g;
            g = h;
            h = i;
        }
        i = MathHelper.inverseSqrt(h * h + g * g);
        return Pair.of(Float.valueOf(g *= i), Float.valueOf(h *= i));
    }

    private static Quaternionf applyJacobiIteration(Matrix3f S) {
        float h;
        float g;
        float f;
        Quaternionf quaternionf2;
        Float float2;
        Float float_;
        Pair<Float, Float> pair;
        Matrix3f matrix3f = new Matrix3f();
        Quaternionf quaternionf = new Quaternionf();
        if (S.m01 * S.m01 + S.m10 * S.m10 > 1.0E-6f) {
            pair = MatrixUtil.approximateGivensQuaternion(S.m00, 0.5f * (S.m01 + S.m10), S.m11);
            float_ = pair.getFirst();
            float2 = pair.getSecond();
            quaternionf2 = new Quaternionf(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
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
        if (S.m02 * S.m02 + S.m20 * S.m20 > 1.0E-6f) {
            pair = MatrixUtil.approximateGivensQuaternion(S.m00, 0.5f * (S.m02 + S.m20), S.m22);
            float i = -pair.getFirst().floatValue();
            float2 = pair.getSecond();
            quaternionf2 = new Quaternionf(0.0f, i, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - i * i;
            g = -2.0f * i * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + i * i;
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
        if (S.m12 * S.m12 + S.m21 * S.m21 > 1.0E-6f) {
            pair = MatrixUtil.approximateGivensQuaternion(S.m11, 0.5f * (S.m12 + S.m21), S.m22);
            float_ = pair.getFirst();
            float2 = pair.getSecond();
            quaternionf2 = new Quaternionf(float_.floatValue(), 0.0f, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
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
     * Performs an approximate singular value decomposition on a 3\u00d73 matrix.
     * 
     * @see https://pages.cs.wisc.edu/~sifakis/papers/SVD_TR1690.pdf
     */
    public static Triple<Quaternionf, Vector3f, Quaternionf> svdDecompose(Matrix3f A) {
        Quaternionf quaternionf = new Quaternionf();
        Quaternionf quaternionf2 = new Quaternionf();
        Matrix3f matrix3f = new Matrix3f(A);
        matrix3f.transpose();
        matrix3f.mul(A);
        for (int i = 0; i < 5; ++i) {
            quaternionf2.mul(MatrixUtil.applyJacobiIteration(matrix3f));
        }
        quaternionf2.normalize();
        Matrix3f matrix3f2 = new Matrix3f(A);
        matrix3f2.rotate(quaternionf2);
        float f = 1.0f;
        Pair<Float, Float> pair = MatrixUtil.qrGivensQuaternion(matrix3f2.m00, matrix3f2.m01);
        Float float_ = pair.getFirst();
        Float float2 = pair.getSecond();
        float g = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
        float h = -2.0f * float_.floatValue() * float2.floatValue();
        float j = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
        Quaternionf quaternionf3 = new Quaternionf(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
        quaternionf.mul(quaternionf3);
        Matrix3f matrix3f3 = new Matrix3f();
        matrix3f3.m00 = g;
        matrix3f3.m11 = g;
        matrix3f3.m01 = h;
        matrix3f3.m10 = -h;
        matrix3f3.m22 = j;
        f *= j;
        matrix3f3.mul(matrix3f2);
        pair = MatrixUtil.qrGivensQuaternion(matrix3f3.m00, matrix3f3.m02);
        float k = -pair.getFirst().floatValue();
        Float float3 = pair.getSecond();
        float l = float3.floatValue() * float3.floatValue() - k * k;
        float m = -2.0f * k * float3.floatValue();
        float n = float3.floatValue() * float3.floatValue() + k * k;
        Quaternionf quaternionf4 = new Quaternionf(0.0f, k, 0.0f, float3.floatValue());
        quaternionf.mul(quaternionf4);
        Matrix3f matrix3f4 = new Matrix3f();
        matrix3f4.m00 = l;
        matrix3f4.m22 = l;
        matrix3f4.m02 = -m;
        matrix3f4.m20 = m;
        matrix3f4.m11 = n;
        f *= n;
        matrix3f4.mul(matrix3f3);
        pair = MatrixUtil.qrGivensQuaternion(matrix3f4.m11, matrix3f4.m12);
        Float float4 = pair.getFirst();
        Float float5 = pair.getSecond();
        float o = float5.floatValue() * float5.floatValue() - float4.floatValue() * float4.floatValue();
        float p = -2.0f * float4.floatValue() * float5.floatValue();
        float q = float5.floatValue() * float5.floatValue() + float4.floatValue() * float4.floatValue();
        Quaternionf quaternionf5 = new Quaternionf(float4.floatValue(), 0.0f, 0.0f, float5.floatValue());
        quaternionf.mul(quaternionf5);
        Matrix3f matrix3f5 = new Matrix3f();
        matrix3f5.m11 = o;
        matrix3f5.m22 = o;
        matrix3f5.m12 = p;
        matrix3f5.m21 = -p;
        matrix3f5.m00 = q;
        f *= q;
        matrix3f5.mul(matrix3f4);
        f = 1.0f / f;
        quaternionf.mul((float)Math.sqrt(f));
        Vector3f vector3f = new Vector3f(matrix3f5.m00 * f, matrix3f5.m11 * f, matrix3f5.m22 * f);
        return Triple.of(quaternionf, vector3f, quaternionf2);
    }

    public static Matrix4x3f affineTransform(Matrix4f matrix) {
        float f = 1.0f / matrix.m33();
        return new Matrix4x3f().set(matrix).scaleLocal(f, f, f);
    }
}

