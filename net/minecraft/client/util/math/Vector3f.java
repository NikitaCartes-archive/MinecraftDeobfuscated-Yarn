/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;

public final class Vector3f {
    public static Vector3f NEGATIVE_X = new Vector3f(-1.0f, 0.0f, 0.0f);
    public static Vector3f POSITIVE_X = new Vector3f(1.0f, 0.0f, 0.0f);
    public static Vector3f NEGATIVE_Y = new Vector3f(0.0f, -1.0f, 0.0f);
    public static Vector3f POSITIVE_Y = new Vector3f(0.0f, 1.0f, 0.0f);
    public static Vector3f NEGATIVE_Z = new Vector3f(0.0f, 0.0f, -1.0f);
    public static Vector3f POSITIVE_Z = new Vector3f(0.0f, 0.0f, 1.0f);
    private final float[] components;

    @Environment(value=EnvType.CLIENT)
    public Vector3f(Vector3f vector3f) {
        this.components = Arrays.copyOf(vector3f.components, 3);
    }

    public Vector3f() {
        this.components = new float[3];
    }

    public Vector3f(float f, float g, float h) {
        this.components = new float[]{f, g, h};
    }

    public Vector3f(Vec3d vec3d) {
        this.components = new float[]{(float)vec3d.x, (float)vec3d.y, (float)vec3d.z};
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Vector3f vector3f = (Vector3f)object;
        return Arrays.equals(this.components, vector3f.components);
    }

    public int hashCode() {
        return Arrays.hashCode(this.components);
    }

    public float getX() {
        return this.components[0];
    }

    public float getY() {
        return this.components[1];
    }

    public float getZ() {
        return this.components[2];
    }

    @Environment(value=EnvType.CLIENT)
    public void scale(float f) {
        int i = 0;
        while (i < 3) {
            int n = i++;
            this.components[n] = this.components[n] * f;
        }
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
        this.components[0] = Vector3f.clampFloat(this.components[0], f, g);
        this.components[1] = Vector3f.clampFloat(this.components[1], f, g);
        this.components[2] = Vector3f.clampFloat(this.components[2], f, g);
    }

    public void set(float f, float g, float h) {
        this.components[0] = f;
        this.components[1] = g;
        this.components[2] = h;
    }

    @Environment(value=EnvType.CLIENT)
    public void add(float f, float g, float h) {
        this.components[0] = this.components[0] + f;
        this.components[1] = this.components[1] + g;
        this.components[2] = this.components[2] + h;
    }

    @Environment(value=EnvType.CLIENT)
    public void subtract(Vector3f vector3f) {
        for (int i = 0; i < 3; ++i) {
            int n = i;
            this.components[n] = this.components[n] - vector3f.components[i];
        }
    }

    @Environment(value=EnvType.CLIENT)
    public float dot(Vector3f vector3f) {
        float f = 0.0f;
        for (int i = 0; i < 3; ++i) {
            f += this.components[i] * vector3f.components[i];
        }
        return f;
    }

    @Environment(value=EnvType.CLIENT)
    public boolean reciprocal() {
        float f = 0.0f;
        for (int i = 0; i < 3; ++i) {
            f += this.components[i] * this.components[i];
        }
        if ((double)f < 1.0E-5) {
            return false;
        }
        float g = MathHelper.fastInverseSqrt(f);
        int j = 0;
        while (j < 3) {
            int n = j++;
            this.components[n] = this.components[n] * g;
        }
        return true;
    }

    @Environment(value=EnvType.CLIENT)
    public void cross(Vector3f vector3f) {
        float f = this.components[0];
        float g = this.components[1];
        float h = this.components[2];
        float i = vector3f.getX();
        float j = vector3f.getY();
        float k = vector3f.getZ();
        this.components[0] = g * k - h * j;
        this.components[1] = h * i - f * k;
        this.components[2] = f * j - g * i;
    }

    @Environment(value=EnvType.CLIENT)
    public void multiply(Matrix3f matrix3f) {
        float f = this.components[0];
        float g = this.components[1];
        float h = this.components[2];
        for (int i = 0; i < 3; ++i) {
            float j = 0.0f;
            j += matrix3f.get(i, 0) * f;
            j += matrix3f.get(i, 1) * g;
            this.components[i] = j += matrix3f.get(i, 2) * h;
        }
    }

    public void method_19262(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.copyFrom(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0f));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.reverse();
        quaternion2.copyFrom(quaternion3);
        this.set(quaternion2.getX(), quaternion2.getY(), quaternion2.getZ());
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

