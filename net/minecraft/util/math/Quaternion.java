/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3f;

public final class Quaternion {
    public static final Quaternion IDENTITY = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private float x;
    private float y;
    private float z;
    private float w;

    public Quaternion(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    public Quaternion(Vec3f axis, float rotationAngle, boolean degrees) {
        if (degrees) {
            rotationAngle *= (float)Math.PI / 180;
        }
        float f = Quaternion.sin(rotationAngle / 2.0f);
        this.x = axis.getX() * f;
        this.y = axis.getY() * f;
        this.z = axis.getZ() * f;
        this.w = Quaternion.cos(rotationAngle / 2.0f);
    }

    public Quaternion(float x, float y, float z, boolean degrees) {
        if (degrees) {
            x *= (float)Math.PI / 180;
            y *= (float)Math.PI / 180;
            z *= (float)Math.PI / 180;
        }
        float f = Quaternion.sin(0.5f * x);
        float g = Quaternion.cos(0.5f * x);
        float h = Quaternion.sin(0.5f * y);
        float i = Quaternion.cos(0.5f * y);
        float j = Quaternion.sin(0.5f * z);
        float k = Quaternion.cos(0.5f * z);
        this.x = f * i * k + g * h * j;
        this.y = g * h * k - f * i * j;
        this.z = f * h * k + g * i * j;
        this.w = g * i * k - f * h * j;
    }

    public Quaternion(Quaternion other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
        this.w = other.w;
    }

    public static Quaternion method_35821(float f, float g, float h) {
        Quaternion quaternion = IDENTITY.copy();
        quaternion.hamiltonProduct(new Quaternion(0.0f, (float)Math.sin(f / 2.0f), 0.0f, (float)Math.cos(f / 2.0f)));
        quaternion.hamiltonProduct(new Quaternion((float)Math.sin(g / 2.0f), 0.0f, 0.0f, (float)Math.cos(g / 2.0f)));
        quaternion.hamiltonProduct(new Quaternion(0.0f, 0.0f, (float)Math.sin(h / 2.0f), (float)Math.cos(h / 2.0f)));
        return quaternion;
    }

    public static Quaternion method_35823(Vec3f vec3f) {
        return Quaternion.method_35825((float)Math.toRadians(vec3f.getX()), (float)Math.toRadians(vec3f.getY()), (float)Math.toRadians(vec3f.getZ()));
    }

    public static Quaternion method_35826(Vec3f vec3f) {
        return Quaternion.method_35825(vec3f.getX(), vec3f.getY(), vec3f.getZ());
    }

    public static Quaternion method_35825(float f, float g, float h) {
        Quaternion quaternion = IDENTITY.copy();
        quaternion.hamiltonProduct(new Quaternion((float)Math.sin(f / 2.0f), 0.0f, 0.0f, (float)Math.cos(f / 2.0f)));
        quaternion.hamiltonProduct(new Quaternion(0.0f, (float)Math.sin(g / 2.0f), 0.0f, (float)Math.cos(g / 2.0f)));
        quaternion.hamiltonProduct(new Quaternion(0.0f, 0.0f, (float)Math.sin(h / 2.0f), (float)Math.cos(h / 2.0f)));
        return quaternion;
    }

    public Vec3f method_35820() {
        float f = this.getW() * this.getW();
        float g = this.getX() * this.getX();
        float h = this.getY() * this.getY();
        float i = this.getZ() * this.getZ();
        float j = f + g + h + i;
        float k = 2.0f * this.getW() * this.getX() - 2.0f * this.getY() * this.getZ();
        float l = (float)Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vec3f(2.0f * (float)Math.atan2(this.getX(), this.getW()), l, 0.0f);
        }
        return new Vec3f((float)Math.atan2(2.0f * this.getY() * this.getZ() + 2.0f * this.getX() * this.getW(), f - g - h + i), l, (float)Math.atan2(2.0f * this.getX() * this.getY() + 2.0f * this.getW() * this.getZ(), f + g - h - i));
    }

    public Vec3f method_35824() {
        Vec3f vec3f = this.method_35820();
        return new Vec3f((float)Math.toDegrees(vec3f.getX()), (float)Math.toDegrees(vec3f.getY()), (float)Math.toDegrees(vec3f.getZ()));
    }

    public Vec3f method_35827() {
        float f = this.getW() * this.getW();
        float g = this.getX() * this.getX();
        float h = this.getY() * this.getY();
        float i = this.getZ() * this.getZ();
        float j = f + g + h + i;
        float k = 2.0f * this.getW() * this.getX() - 2.0f * this.getY() * this.getZ();
        float l = (float)Math.asin(k / j);
        if (Math.abs(k) > 0.999f * j) {
            return new Vec3f(l, 2.0f * (float)Math.atan2(this.getY(), this.getW()), 0.0f);
        }
        return new Vec3f(l, (float)Math.atan2(2.0f * this.getX() * this.getZ() + 2.0f * this.getY() * this.getW(), f - g - h + i), (float)Math.atan2(2.0f * this.getX() * this.getY() + 2.0f * this.getW() * this.getZ(), f - g + h - i));
    }

    public Vec3f method_35828() {
        Vec3f vec3f = this.method_35827();
        return new Vec3f((float)Math.toDegrees(vec3f.getX()), (float)Math.toDegrees(vec3f.getY()), (float)Math.toDegrees(vec3f.getZ()));
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Quaternion quaternion = (Quaternion)o;
        if (Float.compare(quaternion.x, this.x) != 0) {
            return false;
        }
        if (Float.compare(quaternion.y, this.y) != 0) {
            return false;
        }
        if (Float.compare(quaternion.z, this.z) != 0) {
            return false;
        }
        return Float.compare(quaternion.w, this.w) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.x);
        i = 31 * i + Float.floatToIntBits(this.y);
        i = 31 * i + Float.floatToIntBits(this.z);
        i = 31 * i + Float.floatToIntBits(this.w);
        return i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternion[").append(this.getW()).append(" + ");
        stringBuilder.append(this.getX()).append("i + ");
        stringBuilder.append(this.getY()).append("j + ");
        stringBuilder.append(this.getZ()).append("k]");
        return stringBuilder.toString();
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

    public void hamiltonProduct(Quaternion other) {
        float f = this.getX();
        float g = this.getY();
        float h = this.getZ();
        float i = this.getW();
        float j = other.getX();
        float k = other.getY();
        float l = other.getZ();
        float m = other.getW();
        this.x = i * j + f * m + g * l - h * k;
        this.y = i * k - f * l + g * m + h * j;
        this.z = i * l + f * k - g * j + h * m;
        this.w = i * m - f * j - g * k - h * l;
    }

    public void scale(float scale) {
        this.x *= scale;
        this.y *= scale;
        this.z *= scale;
        this.w *= scale;
    }

    public void conjugate() {
        this.x = -this.x;
        this.y = -this.y;
        this.z = -this.z;
    }

    public void set(float x, float y, float z, float w) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.w = w;
    }

    private static float cos(float value) {
        return (float)Math.cos(value);
    }

    private static float sin(float value) {
        return (float)Math.sin(value);
    }

    public void normalize() {
        float f = this.getX() * this.getX() + this.getY() * this.getY() + this.getZ() * this.getZ() + this.getW() * this.getW();
        if (f > 1.0E-6f) {
            float g = MathHelper.fastInverseSqrt(f);
            this.x *= g;
            this.y *= g;
            this.z *= g;
            this.w *= g;
        } else {
            this.x = 0.0f;
            this.y = 0.0f;
            this.z = 0.0f;
            this.w = 0.0f;
        }
    }

    public void method_35822(Quaternion quaternion, float f) {
        throw new UnsupportedOperationException();
    }

    public Quaternion copy() {
        return new Quaternion(this);
    }
}

