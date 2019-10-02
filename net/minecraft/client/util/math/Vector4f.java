/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;

@Environment(value=EnvType.CLIENT)
public class Vector4f {
    private final float[] components;

    public Vector4f() {
        this.components = new float[4];
    }

    public Vector4f(float f, float g, float h, float i) {
        this.components = new float[]{f, g, h, i};
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        Vector4f vector4f = (Vector4f)object;
        return Arrays.equals(this.components, vector4f.components);
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

    public void multiply(Vector3f vector3f) {
        this.components[0] = this.components[0] * vector3f.getX();
        this.components[1] = this.components[1] * vector3f.getY();
        this.components[2] = this.components[2] * vector3f.getZ();
    }

    public float method_23217(Vector4f vector4f) {
        float f = 0.0f;
        for (int i = 0; i < 4; ++i) {
            f += this.components[i] * vector4f.components[i];
        }
        return f;
    }

    public boolean method_23218() {
        float f = 0.0f;
        for (int i = 0; i < 4; ++i) {
            f += this.components[i] * this.components[i];
        }
        if ((double)f < 1.0E-5) {
            return false;
        }
        float g = MathHelper.fastInverseSqrt(f);
        int j = 0;
        while (j < 4) {
            int n = j++;
            this.components[n] = this.components[n] * g;
        }
        return true;
    }

    public void multiply(Matrix4f matrix4f) {
        float[] fs = Arrays.copyOf(this.components, 4);
        for (int i = 0; i < 4; ++i) {
            this.components[i] = 0.0f;
            for (int j = 0; j < 4; ++j) {
                int n = i;
                this.components[n] = this.components[n] + matrix4f.get(i, j) * fs[j];
            }
        }
    }

    public void method_23219() {
        this.components[0] = this.components[0] / this.components[3];
        this.components[1] = this.components[1] / this.components[3];
        this.components[2] = this.components[2] / this.components[3];
        this.components[3] = 1.0f;
    }
}

