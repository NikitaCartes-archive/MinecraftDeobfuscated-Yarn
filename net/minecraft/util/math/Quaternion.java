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
    private float field_21582;
    private float field_21583;
    private float field_21584;
    private float a;

    public Quaternion(float b, float c, float d, float a) {
        this.field_21582 = b;
        this.field_21583 = c;
        this.field_21584 = d;
        this.a = a;
    }

    public Quaternion(Vector3f axis, float rotationAngle, boolean degrees) {
        if (degrees) {
            rotationAngle *= (float)Math.PI / 180;
        }
        float f = Quaternion.sin(rotationAngle / 2.0f);
        this.field_21582 = axis.getX() * f;
        this.field_21583 = axis.getY() * f;
        this.field_21584 = axis.getZ() * f;
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
        this.field_21582 = f * i * k + g * h * j;
        this.field_21583 = g * h * k - f * i * j;
        this.field_21584 = f * h * k + g * i * j;
        this.a = g * i * k - f * h * j;
    }

    public Quaternion(Quaternion other) {
        this.field_21582 = other.field_21582;
        this.field_21583 = other.field_21583;
        this.field_21584 = other.field_21584;
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
        if (Float.compare(quaternion.field_21582, this.field_21582) != 0) {
            return false;
        }
        if (Float.compare(quaternion.field_21583, this.field_21583) != 0) {
            return false;
        }
        if (Float.compare(quaternion.field_21584, this.field_21584) != 0) {
            return false;
        }
        return Float.compare(quaternion.a, this.a) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.field_21582);
        i = 31 * i + Float.floatToIntBits(this.field_21583);
        i = 31 * i + Float.floatToIntBits(this.field_21584);
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
        return this.field_21582;
    }

    public float getC() {
        return this.field_21583;
    }

    public float getD() {
        return this.field_21584;
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
        this.field_21582 = i * j + f * m + g * l - h * k;
        this.field_21583 = i * k - f * l + g * m + h * j;
        this.field_21584 = i * l + f * k - g * j + h * m;
        this.a = i * m - f * j - g * k - h * l;
    }

    @Environment(value=EnvType.CLIENT)
    public void scale(float scale) {
        this.field_21582 *= scale;
        this.field_21583 *= scale;
        this.field_21584 *= scale;
        this.a *= scale;
    }

    public void conjugate() {
        this.field_21582 = -this.field_21582;
        this.field_21583 = -this.field_21583;
        this.field_21584 = -this.field_21584;
    }

    @Environment(value=EnvType.CLIENT)
    public void set(float a, float b, float c, float d) {
        this.field_21582 = a;
        this.field_21583 = b;
        this.field_21584 = c;
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
            this.field_21582 *= g;
            this.field_21583 *= g;
            this.field_21584 *= g;
            this.a *= g;
        } else {
            this.field_21582 = 0.0f;
            this.field_21583 = 0.0f;
            this.field_21584 = 0.0f;
            this.a = 0.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion copy() {
        return new Quaternion(this);
    }
}

