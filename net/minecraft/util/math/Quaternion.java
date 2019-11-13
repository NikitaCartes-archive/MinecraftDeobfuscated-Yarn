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
    private float field_21585;

    public Quaternion(float f, float g, float h, float i) {
        this.field_21582 = f;
        this.field_21583 = g;
        this.field_21584 = h;
        this.field_21585 = i;
    }

    public Quaternion(Vector3f vector3f, float f, boolean bl) {
        if (bl) {
            f *= (float)Math.PI / 180;
        }
        float g = Quaternion.sin(f / 2.0f);
        this.field_21582 = vector3f.getX() * g;
        this.field_21583 = vector3f.getY() * g;
        this.field_21584 = vector3f.getZ() * g;
        this.field_21585 = Quaternion.cos(f / 2.0f);
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
        this.field_21582 = i * l * n + j * k * m;
        this.field_21583 = j * k * n - i * l * m;
        this.field_21584 = i * k * n + j * l * m;
        this.field_21585 = j * l * n - i * k * m;
    }

    public Quaternion(Quaternion quaternion) {
        this.field_21582 = quaternion.field_21582;
        this.field_21583 = quaternion.field_21583;
        this.field_21584 = quaternion.field_21584;
        this.field_21585 = quaternion.field_21585;
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Quaternion quaternion = (Quaternion)object;
        if (Float.compare(quaternion.field_21582, this.field_21582) != 0) {
            return false;
        }
        if (Float.compare(quaternion.field_21583, this.field_21583) != 0) {
            return false;
        }
        if (Float.compare(quaternion.field_21584, this.field_21584) != 0) {
            return false;
        }
        return Float.compare(quaternion.field_21585, this.field_21585) == 0;
    }

    public int hashCode() {
        int i = Float.floatToIntBits(this.field_21582);
        i = 31 * i + Float.floatToIntBits(this.field_21583);
        i = 31 * i + Float.floatToIntBits(this.field_21584);
        i = 31 * i + Float.floatToIntBits(this.field_21585);
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
        return this.field_21585;
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
        this.field_21582 = i * j + f * m + g * l - h * k;
        this.field_21583 = i * k - f * l + g * m + h * j;
        this.field_21584 = i * l + f * k - g * j + h * m;
        this.field_21585 = i * m - f * j - g * k - h * l;
    }

    @Environment(value=EnvType.CLIENT)
    public void scale(float f) {
        this.field_21582 *= f;
        this.field_21583 *= f;
        this.field_21584 *= f;
        this.field_21585 *= f;
    }

    public void conjugate() {
        this.field_21582 = -this.field_21582;
        this.field_21583 = -this.field_21583;
        this.field_21584 = -this.field_21584;
    }

    @Environment(value=EnvType.CLIENT)
    public void method_23758(float f, float g, float h, float i) {
        this.field_21582 = f;
        this.field_21583 = g;
        this.field_21584 = h;
        this.field_21585 = i;
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
            this.field_21582 *= g;
            this.field_21583 *= g;
            this.field_21584 *= g;
            this.field_21585 *= g;
        } else {
            this.field_21582 = 0.0f;
            this.field_21583 = 0.0f;
            this.field_21584 = 0.0f;
            this.field_21585 = 0.0f;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public Quaternion copy() {
        return new Quaternion(this);
    }
}

