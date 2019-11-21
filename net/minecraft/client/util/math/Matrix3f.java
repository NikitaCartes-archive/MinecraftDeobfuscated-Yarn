/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import com.mojang.datafixers.util.Pair;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(value=EnvType.CLIENT)
public final class Matrix3f {
    private static final float THREE_PLUS_TWO_SQRT_TWO = 3.0f + 2.0f * (float)Math.sqrt(2.0);
    private static final float COS_PI_OVER_EIGHT = (float)Math.cos(0.39269908169872414);
    private static final float SIN_PI_OVER_EIGHT = (float)Math.sin(0.39269908169872414);
    private static final float SQRT_HALF = 1.0f / (float)Math.sqrt(2.0);
    protected float field_21633;
    protected float field_21634;
    protected float field_21635;
    protected float field_21636;
    protected float field_21637;
    protected float field_21638;
    protected float field_21639;
    protected float field_21640;
    protected float field_21641;

    public Matrix3f() {
    }

    public Matrix3f(Quaternion quaternion) {
        float f = quaternion.getB();
        float g = quaternion.getC();
        float h = quaternion.getD();
        float i = quaternion.getA();
        float j = 2.0f * f * f;
        float k = 2.0f * g * g;
        float l = 2.0f * h * h;
        this.field_21633 = 1.0f - k - l;
        this.field_21637 = 1.0f - l - j;
        this.field_21641 = 1.0f - j - k;
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
        this.field_21636 = 2.0f * (m + r);
        this.field_21634 = 2.0f * (m - r);
        this.field_21639 = 2.0f * (o - q);
        this.field_21635 = 2.0f * (o + q);
        this.field_21640 = 2.0f * (n + p);
        this.field_21638 = 2.0f * (n - p);
    }

    public static Matrix3f method_23963(float f, float g, float h) {
        Matrix3f matrix3f = new Matrix3f();
        matrix3f.field_21633 = f;
        matrix3f.field_21637 = g;
        matrix3f.field_21641 = h;
        return matrix3f;
    }

    public Matrix3f(Matrix4f matrix4f) {
        this.field_21633 = matrix4f.field_21652;
        this.field_21634 = matrix4f.field_21653;
        this.field_21635 = matrix4f.field_21654;
        this.field_21636 = matrix4f.field_21656;
        this.field_21637 = matrix4f.field_21657;
        this.field_21638 = matrix4f.field_21658;
        this.field_21639 = matrix4f.field_21660;
        this.field_21640 = matrix4f.field_21661;
        this.field_21641 = matrix4f.field_21662;
    }

    public Matrix3f(Matrix3f matrix3f) {
        this.field_21633 = matrix3f.field_21633;
        this.field_21634 = matrix3f.field_21634;
        this.field_21635 = matrix3f.field_21635;
        this.field_21636 = matrix3f.field_21636;
        this.field_21637 = matrix3f.field_21637;
        this.field_21638 = matrix3f.field_21638;
        this.field_21639 = matrix3f.field_21639;
        this.field_21640 = matrix3f.field_21640;
        this.field_21641 = matrix3f.field_21641;
    }

    private static Pair<Float, Float> method_22849(float f, float g, float h) {
        float j = g;
        float i = 2.0f * (f - h);
        if (THREE_PLUS_TWO_SQRT_TWO * j * j < i * i) {
            float k = MathHelper.fastInverseSqrt(j * j + i * i);
            return Pair.of(Float.valueOf(k * j), Float.valueOf(k * i));
        }
        return Pair.of(Float.valueOf(SIN_PI_OVER_EIGHT), Float.valueOf(COS_PI_OVER_EIGHT));
    }

    private static Pair<Float, Float> method_22848(float f, float g) {
        float k;
        float h = (float)Math.hypot(f, g);
        float i = h > 1.0E-6f ? g : 0.0f;
        float j = Math.abs(f) + Math.max(h, 1.0E-6f);
        if (f < 0.0f) {
            k = i;
            i = j;
            j = k;
        }
        k = MathHelper.fastInverseSqrt(j * j + i * i);
        return Pair.of(Float.valueOf(i *= k), Float.valueOf(j *= k));
    }

    private static Quaternion method_22857(Matrix3f matrix3f) {
        float h;
        float g;
        float f;
        Quaternion quaternion2;
        Float float2;
        Float float_;
        Pair<Float, Float> pair;
        Matrix3f matrix3f2 = new Matrix3f();
        Quaternion quaternion = Quaternion.IDENTITY.copy();
        if (matrix3f.field_21634 * matrix3f.field_21634 + matrix3f.field_21636 * matrix3f.field_21636 > 1.0E-6f) {
            pair = Matrix3f.method_22849(matrix3f.field_21633, 0.5f * (matrix3f.field_21634 + matrix3f.field_21636), matrix3f.field_21637);
            float_ = pair.getFirst();
            float2 = pair.getSecond();
            quaternion2 = new Quaternion(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternion.hamiltonProduct(quaternion2);
            matrix3f2.loadIdentity();
            matrix3f2.field_21633 = f;
            matrix3f2.field_21637 = f;
            matrix3f2.field_21636 = -g;
            matrix3f2.field_21634 = g;
            matrix3f2.field_21641 = h;
            matrix3f.multiply(matrix3f2);
            matrix3f2.transpose();
            matrix3f2.multiply(matrix3f);
            matrix3f.load(matrix3f2);
        }
        if (matrix3f.field_21635 * matrix3f.field_21635 + matrix3f.field_21639 * matrix3f.field_21639 > 1.0E-6f) {
            pair = Matrix3f.method_22849(matrix3f.field_21633, 0.5f * (matrix3f.field_21635 + matrix3f.field_21639), matrix3f.field_21641);
            float i = -pair.getFirst().floatValue();
            float2 = pair.getSecond();
            quaternion2 = new Quaternion(0.0f, i, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - i * i;
            g = -2.0f * i * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + i * i;
            quaternion.hamiltonProduct(quaternion2);
            matrix3f2.loadIdentity();
            matrix3f2.field_21633 = f;
            matrix3f2.field_21641 = f;
            matrix3f2.field_21639 = g;
            matrix3f2.field_21635 = -g;
            matrix3f2.field_21637 = h;
            matrix3f.multiply(matrix3f2);
            matrix3f2.transpose();
            matrix3f2.multiply(matrix3f);
            matrix3f.load(matrix3f2);
        }
        if (matrix3f.field_21638 * matrix3f.field_21638 + matrix3f.field_21640 * matrix3f.field_21640 > 1.0E-6f) {
            pair = Matrix3f.method_22849(matrix3f.field_21637, 0.5f * (matrix3f.field_21638 + matrix3f.field_21640), matrix3f.field_21641);
            float_ = pair.getFirst();
            float2 = pair.getSecond();
            quaternion2 = new Quaternion(float_.floatValue(), 0.0f, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternion.hamiltonProduct(quaternion2);
            matrix3f2.loadIdentity();
            matrix3f2.field_21637 = f;
            matrix3f2.field_21641 = f;
            matrix3f2.field_21640 = -g;
            matrix3f2.field_21638 = g;
            matrix3f2.field_21633 = h;
            matrix3f.multiply(matrix3f2);
            matrix3f2.transpose();
            matrix3f2.multiply(matrix3f);
            matrix3f.load(matrix3f2);
        }
        return quaternion;
    }

    public void transpose() {
        float f = this.field_21634;
        this.field_21634 = this.field_21636;
        this.field_21636 = f;
        f = this.field_21635;
        this.field_21635 = this.field_21639;
        this.field_21639 = f;
        f = this.field_21638;
        this.field_21638 = this.field_21640;
        this.field_21640 = f;
    }

    public Triple<Quaternion, Vector3f, Quaternion> method_22853() {
        Quaternion quaternion = Quaternion.IDENTITY.copy();
        Quaternion quaternion2 = Quaternion.IDENTITY.copy();
        Matrix3f matrix3f = this.copy();
        matrix3f.transpose();
        matrix3f.multiply(this);
        for (int i = 0; i < 5; ++i) {
            quaternion2.hamiltonProduct(Matrix3f.method_22857(matrix3f));
        }
        quaternion2.normalize();
        Matrix3f matrix3f2 = new Matrix3f(this);
        matrix3f2.multiply(new Matrix3f(quaternion2));
        float f = 1.0f;
        Pair<Float, Float> pair = Matrix3f.method_22848(matrix3f2.field_21633, matrix3f2.field_21636);
        Float float_ = pair.getFirst();
        Float float2 = pair.getSecond();
        float g = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
        float h = -2.0f * float_.floatValue() * float2.floatValue();
        float j = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
        Quaternion quaternion3 = new Quaternion(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
        quaternion.hamiltonProduct(quaternion3);
        Matrix3f matrix3f3 = new Matrix3f();
        matrix3f3.loadIdentity();
        matrix3f3.field_21633 = g;
        matrix3f3.field_21637 = g;
        matrix3f3.field_21636 = h;
        matrix3f3.field_21634 = -h;
        matrix3f3.field_21641 = j;
        f *= j;
        matrix3f3.multiply(matrix3f2);
        pair = Matrix3f.method_22848(matrix3f3.field_21633, matrix3f3.field_21639);
        float k = -pair.getFirst().floatValue();
        Float float3 = pair.getSecond();
        float l = float3.floatValue() * float3.floatValue() - k * k;
        float m = -2.0f * k * float3.floatValue();
        float n = float3.floatValue() * float3.floatValue() + k * k;
        Quaternion quaternion4 = new Quaternion(0.0f, k, 0.0f, float3.floatValue());
        quaternion.hamiltonProduct(quaternion4);
        Matrix3f matrix3f4 = new Matrix3f();
        matrix3f4.loadIdentity();
        matrix3f4.field_21633 = l;
        matrix3f4.field_21641 = l;
        matrix3f4.field_21639 = -m;
        matrix3f4.field_21635 = m;
        matrix3f4.field_21637 = n;
        f *= n;
        matrix3f4.multiply(matrix3f3);
        pair = Matrix3f.method_22848(matrix3f4.field_21637, matrix3f4.field_21640);
        Float float4 = pair.getFirst();
        Float float5 = pair.getSecond();
        float o = float5.floatValue() * float5.floatValue() - float4.floatValue() * float4.floatValue();
        float p = -2.0f * float4.floatValue() * float5.floatValue();
        float q = float5.floatValue() * float5.floatValue() + float4.floatValue() * float4.floatValue();
        Quaternion quaternion5 = new Quaternion(float4.floatValue(), 0.0f, 0.0f, float5.floatValue());
        quaternion.hamiltonProduct(quaternion5);
        Matrix3f matrix3f5 = new Matrix3f();
        matrix3f5.loadIdentity();
        matrix3f5.field_21637 = o;
        matrix3f5.field_21641 = o;
        matrix3f5.field_21640 = p;
        matrix3f5.field_21638 = -p;
        matrix3f5.field_21633 = q;
        f *= q;
        matrix3f5.multiply(matrix3f4);
        f = 1.0f / f;
        quaternion.scale((float)Math.sqrt(f));
        Vector3f vector3f = new Vector3f(matrix3f5.field_21633 * f, matrix3f5.field_21637 * f, matrix3f5.field_21641 * f);
        return Triple.of(quaternion, vector3f, quaternion2);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Matrix3f matrix3f = (Matrix3f)object;
        return Float.compare(matrix3f.field_21633, this.field_21633) == 0 && Float.compare(matrix3f.field_21634, this.field_21634) == 0 && Float.compare(matrix3f.field_21635, this.field_21635) == 0 && Float.compare(matrix3f.field_21636, this.field_21636) == 0 && Float.compare(matrix3f.field_21637, this.field_21637) == 0 && Float.compare(matrix3f.field_21638, this.field_21638) == 0 && Float.compare(matrix3f.field_21639, this.field_21639) == 0 && Float.compare(matrix3f.field_21640, this.field_21640) == 0 && Float.compare(matrix3f.field_21641, this.field_21641) == 0;
    }

    public int hashCode() {
        int i = this.field_21633 != 0.0f ? Float.floatToIntBits(this.field_21633) : 0;
        i = 31 * i + (this.field_21634 != 0.0f ? Float.floatToIntBits(this.field_21634) : 0);
        i = 31 * i + (this.field_21635 != 0.0f ? Float.floatToIntBits(this.field_21635) : 0);
        i = 31 * i + (this.field_21636 != 0.0f ? Float.floatToIntBits(this.field_21636) : 0);
        i = 31 * i + (this.field_21637 != 0.0f ? Float.floatToIntBits(this.field_21637) : 0);
        i = 31 * i + (this.field_21638 != 0.0f ? Float.floatToIntBits(this.field_21638) : 0);
        i = 31 * i + (this.field_21639 != 0.0f ? Float.floatToIntBits(this.field_21639) : 0);
        i = 31 * i + (this.field_21640 != 0.0f ? Float.floatToIntBits(this.field_21640) : 0);
        i = 31 * i + (this.field_21641 != 0.0f ? Float.floatToIntBits(this.field_21641) : 0);
        return i;
    }

    public void load(Matrix3f matrix3f) {
        this.field_21633 = matrix3f.field_21633;
        this.field_21634 = matrix3f.field_21634;
        this.field_21635 = matrix3f.field_21635;
        this.field_21636 = matrix3f.field_21636;
        this.field_21637 = matrix3f.field_21637;
        this.field_21638 = matrix3f.field_21638;
        this.field_21639 = matrix3f.field_21639;
        this.field_21640 = matrix3f.field_21640;
        this.field_21641 = matrix3f.field_21641;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Matrix3f:\n");
        stringBuilder.append(this.field_21633);
        stringBuilder.append(" ");
        stringBuilder.append(this.field_21634);
        stringBuilder.append(" ");
        stringBuilder.append(this.field_21635);
        stringBuilder.append("\n");
        stringBuilder.append(this.field_21636);
        stringBuilder.append(" ");
        stringBuilder.append(this.field_21637);
        stringBuilder.append(" ");
        stringBuilder.append(this.field_21638);
        stringBuilder.append("\n");
        stringBuilder.append(this.field_21639);
        stringBuilder.append(" ");
        stringBuilder.append(this.field_21640);
        stringBuilder.append(" ");
        stringBuilder.append(this.field_21641);
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public void loadIdentity() {
        this.field_21633 = 1.0f;
        this.field_21634 = 0.0f;
        this.field_21635 = 0.0f;
        this.field_21636 = 0.0f;
        this.field_21637 = 1.0f;
        this.field_21638 = 0.0f;
        this.field_21639 = 0.0f;
        this.field_21640 = 0.0f;
        this.field_21641 = 1.0f;
    }

    public float method_23731() {
        float f = this.field_21637 * this.field_21641 - this.field_21638 * this.field_21640;
        float g = -(this.field_21636 * this.field_21641 - this.field_21638 * this.field_21639);
        float h = this.field_21636 * this.field_21640 - this.field_21637 * this.field_21639;
        float i = -(this.field_21634 * this.field_21641 - this.field_21635 * this.field_21640);
        float j = this.field_21633 * this.field_21641 - this.field_21635 * this.field_21639;
        float k = -(this.field_21633 * this.field_21640 - this.field_21634 * this.field_21639);
        float l = this.field_21634 * this.field_21638 - this.field_21635 * this.field_21637;
        float m = -(this.field_21633 * this.field_21638 - this.field_21635 * this.field_21636);
        float n = this.field_21633 * this.field_21637 - this.field_21634 * this.field_21636;
        float o = this.field_21633 * f + this.field_21634 * g + this.field_21635 * h;
        this.field_21633 = f;
        this.field_21636 = g;
        this.field_21639 = h;
        this.field_21634 = i;
        this.field_21637 = j;
        this.field_21640 = k;
        this.field_21635 = l;
        this.field_21638 = m;
        this.field_21641 = n;
        return o;
    }

    public boolean method_23732() {
        float f = this.method_23731();
        if (Math.abs(f) > 1.0E-6f) {
            this.method_23729(f);
            return true;
        }
        return false;
    }

    public void multiply(Matrix3f matrix3f) {
        float f = this.field_21633 * matrix3f.field_21633 + this.field_21634 * matrix3f.field_21636 + this.field_21635 * matrix3f.field_21639;
        float g = this.field_21633 * matrix3f.field_21634 + this.field_21634 * matrix3f.field_21637 + this.field_21635 * matrix3f.field_21640;
        float h = this.field_21633 * matrix3f.field_21635 + this.field_21634 * matrix3f.field_21638 + this.field_21635 * matrix3f.field_21641;
        float i = this.field_21636 * matrix3f.field_21633 + this.field_21637 * matrix3f.field_21636 + this.field_21638 * matrix3f.field_21639;
        float j = this.field_21636 * matrix3f.field_21634 + this.field_21637 * matrix3f.field_21637 + this.field_21638 * matrix3f.field_21640;
        float k = this.field_21636 * matrix3f.field_21635 + this.field_21637 * matrix3f.field_21638 + this.field_21638 * matrix3f.field_21641;
        float l = this.field_21639 * matrix3f.field_21633 + this.field_21640 * matrix3f.field_21636 + this.field_21641 * matrix3f.field_21639;
        float m = this.field_21639 * matrix3f.field_21634 + this.field_21640 * matrix3f.field_21637 + this.field_21641 * matrix3f.field_21640;
        float n = this.field_21639 * matrix3f.field_21635 + this.field_21640 * matrix3f.field_21638 + this.field_21641 * matrix3f.field_21641;
        this.field_21633 = f;
        this.field_21634 = g;
        this.field_21635 = h;
        this.field_21636 = i;
        this.field_21637 = j;
        this.field_21638 = k;
        this.field_21639 = l;
        this.field_21640 = m;
        this.field_21641 = n;
    }

    public void multiply(Quaternion quaternion) {
        this.multiply(new Matrix3f(quaternion));
    }

    public void method_23729(float f) {
        this.field_21633 *= f;
        this.field_21634 *= f;
        this.field_21635 *= f;
        this.field_21636 *= f;
        this.field_21637 *= f;
        this.field_21638 *= f;
        this.field_21639 *= f;
        this.field_21640 *= f;
        this.field_21641 *= f;
    }

    public Matrix3f copy() {
        return new Matrix3f(this);
    }
}

