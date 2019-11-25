/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;

@Environment(value=EnvType.CLIENT)
public class Vector4f {
    private float x;
    private float y;
    private float z;
    private float w;

    public Vector4f() {
    }

    public Vector4f(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public Vector4f(Vector3f vector3f) {
        this(vector3f.getX(), vector3f.getY(), vector3f.getZ(), 1.0f);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Vector4f vector4f = (Vector4f)object;
        if (Float.compare(vector4f.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(vector4f.y, this.y) != 0) {
            return false;
        }
        if (Float.compare(vector4f.z, this.z) != 0) {
            return false;
        }
        return Float.compare(vector4f.w, this.w) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
        return i;
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

    public void multiplyComponentwise(Vector3f vector3f) {
        this.x *= vector3f.getX();
        this.y *= vector3f.getY();
        this.z *= vector3f.getZ();
    }

    public void set(float f, float g, float h, float i) {
        this.x = f;
        this.y = g;
        this.z = h;
        this.w = i;
    }

    public float dotProduct(Vector4f vector4f) {
        return this.x * vector4f.x + this.y * vector4f.y + this.z * vector4f.z + this.w * vector4f.w;
    }

    public boolean normalize() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z + this.w * this.w;
        if ((double)f < 1.0E-5) {
            return false;
        }
        float g = MathHelper.fastInverseSqrt(f);
        this.x *= g;
        this.y *= g;
        this.z *= g;
        this.w *= g;
        return true;
    }

    public void transform(Matrix4f matrix4f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = this.w;
        this.x = matrix4f.a00 * f + matrix4f.a01 * g + matrix4f.a02 * h + matrix4f.a03 * i;
        this.y = matrix4f.a10 * f + matrix4f.a11 * g + matrix4f.a12 * h + matrix4f.a13 * i;
        this.z = matrix4f.a20 * f + matrix4f.a21 * g + matrix4f.a22 * h + matrix4f.a23 * i;
        this.w = matrix4f.a30 * f + matrix4f.a31 * g + matrix4f.a32 * h + matrix4f.a33 * i;
    }

    public void setQuarternion(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0f));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.conjugate();
        quaternion2.hamiltonProduct(quaternion3);
        this.set(quaternion2.getB(), quaternion2.getC(), quaternion2.getD(), this.getW());
    }

    public void normalizeProjectiveCoordinates() {
        this.x /= this.w;
        this.y /= this.w;
        this.z /= this.w;
        this.w = 1.0f;
    }

    public String toString() {
        return "[" + this.x + ", " + this.y + ", " + this.z + ", " + this.w + "]";
    }
}

