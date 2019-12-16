/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.util.math;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;

public final class Quaternion {
    public static final Quaternion IDENTITY = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
    private float b;
    private float c;
    private float d;
    private float a;

    public Quaternion(float b, float c, float d, float a) {
        this.b = b;
        this.c = c;
        this.d = d;
        this.a = a;
    }

    public Quaternion(Vector3f axis, float rotationAngle, boolean degrees) {
        if (degrees) {
            rotationAngle *= (float)Math.PI / 180;
        }
        float f = Quaternion.sin(rotationAngle / 2.0f);
        this.b = axis.getX() * f;
        this.c = axis.getY() * f;
        this.d = axis.getZ() * f;
        this.a = Quaternion.cos(rotationAngle / 2.0f);
    }

    @Environment(value=EnvType.CLIENT)
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
        this.b = f * i * k + g * h * j;
        this.c = g * h * k - f * i * j;
        this.d = f * h * k + g * i * j;
        this.a = g * i * k - f * h * j;
    }

    public Quaternion(Quaternion other) {
        this.b = other.b;
        this.c = other.c;
        this.d = other.d;
        this.a = other.a;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Quaternion quaternion = (Quaternion)o;
        if (Float.compare(quaternion.b, this.b) != 0) {
            return false;
        }
        if (Float.compare(quaternion.c, this.c) != 0) {
            return false;
        }
        if (Float.compare(quaternion.d, this.d) != 0) {
            return false;
        }
        return Float.compare(quaternion.a, this.a) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.b);
        i = 31 * i + Float.floatToIntBits(this.c);
        i = 31 * i + Float.floatToIntBits(this.d);
        i = 31 * i + Float.floatToIntBits(this.a);
        return i;
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Quaternion[").append(this.getA()).append(" + ");
        stringBuilder.append(this.getB()).append("i + ");
        stringBuilder.append(this.getC()).append("j + ");
        stringBuilder.append(this.getD()).append("k]");
        return stringBuilder.toString();
    }

    public float getB() {
        return this.b;
    }

    public float getC() {
        return this.c;
    }

    public float getD() {
        return this.d;
    }

    public float getA() {
        return this.a;
    }

    public void hamiltonProduct(Quaternion other) {
        float f = this.getB();
        float g = this.getC();
        float h = this.getD();
        float i = this.getA();
        float j = other.getB();
        float k = other.getC();
        float l = other.getD();
        float m = other.getA();
        this.b = i * j + f * m + g * l - h * k;
        this.c = i * k - f * l + g * m + h * j;
        this.d = i * l + f * k - g * j + h * m;
        this.a = i * m - f * j - g * k - h * l;
    }

    @Environment(value=EnvType.CLIENT)
    public void scale(float scale) {
        this.b *= scale;
        this.c *= scale;
        this.d *= scale;
        this.a *= scale;
    }

    public void conjugate() {
        this.b = -this.b;
        this.c = -this.c;
        this.d = -this.d;
    }

    @Environment(value=EnvType.CLIENT)
    public void set(float a, float b, float c, float d) {
        this.b = a;
        this.c = b;
        this.d = c;
        this.a = d;
    }

    private static float cos(float value) {
        return (float)Math.cos(value);
    }

    private static float sin(float value) {
        return (float)Math.sin(value);
    }

    @Environment(value=EnvType.CLIENT)
    public void normalize() {
        float f = this.getB() * this.getB() + this.getC() * this.getC() + this.getD() * this.getD() + this.getA() * this.getA();
        if (f > 1.0E-6f) {
            float g = MathHelper.fastInverseSqrt(f);
            this.b *= g;
            this.c *= g;
            this.d *= g;
            this.a *= g;
        } else {
            this.b = 0.0f;
            this.c = 0.0f;
            this.d = 0.0f;
            this.a = 0.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion copy() {
        return new Quaternion(this);
    }
}

