/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.util.math;

import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Quaternion;

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

    public float getW() {
        return this.components[3];
    }

    public void multiplyComponentwise(Vector3f vector3f) {
        this.components[0] = this.components[0] * vector3f.getX();
        this.components[1] = this.components[1] * vector3f.getY();
        this.components[2] = this.components[2] * vector3f.getZ();
    }

    public void set(float f, float g, float h, float i) {
        this.components[0] = f;
        this.components[1] = g;
        this.components[2] = h;
        this.components[3] = i;
    }

    public void method_4959(Quaternion quaternion) {
        Quaternion quaternion2 = new Quaternion(quaternion);
        quaternion2.hamiltonProduct(new Quaternion(this.getX(), this.getY(), this.getZ(), 0.0f));
        Quaternion quaternion3 = new Quaternion(quaternion);
        quaternion3.conjugate();
        quaternion2.hamiltonProduct(quaternion3);
        this.set(quaternion2.getB(), quaternion2.getC(), quaternion2.getD(), this.getW());
    }
}

