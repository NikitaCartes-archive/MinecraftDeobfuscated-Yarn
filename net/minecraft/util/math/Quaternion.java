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

    public Quaternion(float f, float g, float h, float i) {
        this.b = f;
        this.c = g;
        this.d = h;
        this.a = i;
    }

    public Quaternion(Vector3f vector3f, float f, boolean bl) {
        if (bl) {
            f *= (float)Math.PI / 180;
        }
        float g = Quaternion.sin(f / 2.0f);
        this.b = vector3f.getX() * g;
        this.c = vector3f.getY() * g;
        this.d = vector3f.getZ() * g;
        this.a = Quaternion.cos(f / 2.0f);
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion(float f, float g, float h, boolean bl) {
        if (bl) {
            f *= (float)Math.PI / 180;
            g *= (float)Math.PI / 180;
            h *= (float)Math.PI / 180;
        }
        float i = Quaternion.sin(0.5f * f);
        float j = Quaternion.cos(0.5f * f);
        float k = Quaternion.sin(0.5f * g);
        float l = Quaternion.cos(0.5f * g);
        float m = Quaternion.sin(0.5f * h);
        float n = Quaternion.cos(0.5f * h);
        this.b = i * l * n + j * k * m;
        this.c = j * k * n - i * l * m;
        this.d = i * k * n + j * l * m;
        this.a = j * l * n - i * k * m;
    }

    public Quaternion(Quaternion quaternion) {
        this.b = quaternion.b;
        this.c = quaternion.c;
        this.d = quaternion.d;
        this.a = quaternion.a;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Quaternion quaternion = (Quaternion)object;
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

    public void hamiltonProduct(Quaternion quaternion) {
        float f = this.getB();
        float g = this.getC();
        float h = this.getD();
        float i = this.getA();
        float j = quaternion.getB();
        float k = quaternion.getC();
        float l = quaternion.getD();
        float m = quaternion.getA();
        this.b = i * j + f * m + g * l - h * k;
        this.c = i * k - f * l + g * m + h * j;
        this.d = i * l + f * k - g * j + h * m;
        this.a = i * m - f * j - g * k - h * l;
    }

    @Environment(value=EnvType.CLIENT)
    public void scale(float f) {
        this.b *= f;
        this.c *= f;
        this.d *= f;
        this.a *= f;
    }

    public void conjugate() {
        this.b = -this.b;
        this.c = -this.c;
        this.d = -this.d;
    }

    @Environment(value=EnvType.CLIENT)
    public void set(float f, float g, float h, float i) {
        this.b = f;
        this.c = g;
        this.d = h;
        this.a = i;
    }

    private static float cos(float f) {
        return (float)Math.cos(f);
    }

    private static float sin(float f) {
        return (float)Math.sin(f);
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

