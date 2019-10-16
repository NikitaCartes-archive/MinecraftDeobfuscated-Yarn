/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class Vector3f {
    public static Vector3f NEGATIVE_X = new Vector3f(-1.0f, 0.0f, 0.0f);
    public static Vector3f POSITIVE_X = new Vector3f(1.0f, 0.0f, 0.0f);
    public static Vector3f NEGATIVE_Y = new Vector3f(0.0f, -1.0f, 0.0f);
    public static Vector3f POSITIVE_Y = new Vector3f(0.0f, 1.0f, 0.0f);
    public static Vector3f NEGATIVE_Z = new Vector3f(0.0f, 0.0f, -1.0f);
    public static Vector3f POSITIVE_Z = new Vector3f(0.0f, 0.0f, 1.0f);
    private float x;
    private float y;
    private float z;

    public Vector3f() {
    }

    public Vector3f(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    @Environment(value=EnvType.CLIENT)
    public Vector3f(Vector3f vector3f) {
        this(vector3f.x, vector3f.y, vector3f.z);
    }

    public Vector3f(Vec3d vec3d) {
        this((float)vec3d.x, (float)vec3d.y, (float)vec3d.z);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Vector3f vector3f = (Vector3f)object;
        if (Float.compare(vector3f.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(vector3f.y, this.y) != 0) {
            return false;
        }
        return Float.compare(vector3f.z, this.z) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
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

    @Environment(value=EnvType.CLIENT)
    public void scale(float f) {
        this.x *= f;
        this.y *= f;
        this.z *= f;
    }

    @Environment(value=EnvType.CLIENT)
    private static float clampFloat(float f, float g, float h) {
        if (f < g) {
            return g;
        }
        if (f > h) {
            return h;
        }
        return f;
    }

    @Environment(value=EnvType.CLIENT)
    public void clamp(float f, float g) {
        this.x = Vector3f.clampFloat(this.x, f, g);
        this.y = Vector3f.clampFloat(this.y, f, g);
        this.z = Vector3f.clampFloat(this.z, f, g);
    }

    public void set(float f, float g, float h) {
        this.x = f;
        this.y = g;
        this.z = h;
    }

    @Environment(value=EnvType.CLIENT)
    public void add(float f, float g, float h) {
        this.x += f;
        this.y += g;
        this.z += h;
    }

    @Environment(value=EnvType.CLIENT)
    public void subtract(Vector3f vector3f) {
        this.x -= vector3f.x;
        this.y -= vector3f.y;
        this.z -= vector3f.z;
    }

    @Environment(value=EnvType.CLIENT)
    public float dot(Vector3f vector3f) {
        return this.x * vector3f.x + this.y * vector3f.y + this.z * vector3f.z;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean reciprocal() {
        float f = this.x * this.x + this.y * this.y + this.z * this.z;
        if ((double)f < 1.0E-5) {
            return false;
        }
        float g = MathHelper.fastInverseSqrt(f);
        this.x *= g;
        this.y *= g;
        this.z *= g;
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public void cross(Vector3f vector3f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        float i = vector3f.getX();
        float j = vector3f.getY();
        float k = vector3f.getZ();
        this.x = g * k - h * j;
        this.y = h * i - f * k;
        this.z = f * j - g * i;
    }

    @Environment(value=EnvType.CLIENT)
    public void multiply(Matrix3f matrix3f) {
        float f = this.x;
        float g = this.y;
        float h = this.z;
        this.x = Vector3f.method_23691(0, matrix3f, f, g, h);
        this.y = Vector3f.method_23691(1, matrix3f, f, g, h);
        this.z = Vector3f.method_23691(2, matrix3f, f, g, h);
    }

    @Environment(value=EnvType.CLIENT)
    private static float method_23691(int i, Matrix3f matrix3f, float f, float g, float h) {
        return matrix3f.get(i, 0) * f + matrix3f.get(i, 1) * g + matrix3f.get(i, 2) * h;
    }

    public void method_19262(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0f));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.conjugate();
        quaternion2.hamiltonProduct(quaternion3);
        this.set(quaternion2.getB(), quaternion2.getC(), quaternion2.getD());
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion method_23626(float f) {
        return new Quaternion(this, f, false);
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion getRotationQuaternion(float f) {
        return new Quaternion(this, f, true);
    }
}

