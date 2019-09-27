/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import com.mojang.datafixers.util.Pair;
import java.util.Arrays;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import org.apache.commons.lang3.tuple.Triple;

@Environment(value=EnvType.CLIENT)
public final class class_4581 {
    private static final float field_20860 = 3.0f + 2.0f * (float)Math.sqrt(2.0);
    private static final float field_20861 = (float)Math.cos(0.39269908169872414);
    private static final float field_20862 = (float)Math.sin(0.39269908169872414);
    private static final float field_20863 = 1.0f / (float)Math.sqrt(2.0);
    private final float[] field_20864;

    public class_4581() {
        this.field_20864 = new float[9];
    }

    public class_4581(Quaternion quaternion) {
        this();
        float f = quaternion.getX();
        float g = quaternion.getY();
        float h = quaternion.getZ();
        float i = quaternion.getW();
        float j = 2.0f * f * f;
        float k = 2.0f * g * g;
        float l = 2.0f * h * h;
        this.field_20864[0] = 1.0f - k - l;
        this.field_20864[4] = 1.0f - l - j;
        this.field_20864[8] = 1.0f - j - k;
        float m = f * g;
        float n = g * h;
        float o = h * f;
        float p = f * i;
        float q = g * i;
        float r = h * i;
        this.field_20864[1] = 2.0f * (m + r);
        this.field_20864[3] = 2.0f * (m - r);
        this.field_20864[2] = 2.0f * (o - q);
        this.field_20864[6] = 2.0f * (o + q);
        this.field_20864[5] = 2.0f * (n + p);
        this.field_20864[7] = 2.0f * (n - p);
    }

    public class_4581(class_4581 arg, boolean bl) {
        this(arg.field_20864, true);
    }

    public class_4581(Matrix4f matrix4f) {
        this();
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.field_20864[j + i * 3] = matrix4f.method_22669(j, i);
            }
        }
    }

    public class_4581(float[] fs, boolean bl) {
        if (bl) {
            this.field_20864 = new float[9];
            for (int i = 0; i < 3; ++i) {
                for (int j = 0; j < 3; ++j) {
                    this.field_20864[j + i * 3] = fs[i + j * 3];
                }
            }
        } else {
            this.field_20864 = Arrays.copyOf(fs, fs.length);
        }
    }

    public class_4581(class_4581 arg) {
        this.field_20864 = Arrays.copyOf(arg.field_20864, 9);
    }

    private static Pair<Float, Float> method_22849(float f, float g, float h) {
        float j = g;
        float i = 2.0f * (f - h);
        if (field_20860 * j * j < i * i) {
            float k = MathHelper.method_22858(j * j + i * i);
            return Pair.of(Float.valueOf(k * j), Float.valueOf(k * i));
        }
        return Pair.of(Float.valueOf(field_20862), Float.valueOf(field_20861));
    }

    private static Pair<Float, Float> method_22848(float f, float g) {
        float k;
        float h = (float)Math.hypot(f, g);
        float i = h > 1.0E-6f ? g : 0.0f;
        float j = Math.abs(f) + Math.max(h, 1.0E-6f);
        if (f < 0.0f) {
            k = i;
            i = j;
            j = k;
        }
        k = MathHelper.method_22858(j * j + i * i);
        return Pair.of(Float.valueOf(i *= k), Float.valueOf(j *= k));
    }

    private static Quaternion method_22857(class_4581 arg) {
        float h;
        float g;
        float f;
        Quaternion quaternion2;
        Float float2;
        Float float_;
        Pair<Float, Float> pair;
        class_4581 lv = new class_4581();
        Quaternion quaternion = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        if (arg.method_22850(0, 1) * arg.method_22850(0, 1) + arg.method_22850(1, 0) * arg.method_22850(1, 0) > 1.0E-6f) {
            pair = class_4581.method_22849(arg.method_22850(0, 0), 0.5f * (arg.method_22850(0, 1) + arg.method_22850(1, 0)), arg.method_22850(1, 1));
            float_ = pair.getFirst();
            float2 = pair.getSecond();
            quaternion2 = new Quaternion(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternion.copyFrom(quaternion2);
            lv.method_22856();
            lv.method_22851(0, 0, f);
            lv.method_22851(1, 1, f);
            lv.method_22851(1, 0, -g);
            lv.method_22851(0, 1, g);
            lv.method_22851(2, 2, h);
            arg.method_22855(lv);
            lv.method_22847();
            lv.method_22855(arg);
            arg.method_22852(lv);
        }
        if (arg.method_22850(0, 2) * arg.method_22850(0, 2) + arg.method_22850(2, 0) * arg.method_22850(2, 0) > 1.0E-6f) {
            pair = class_4581.method_22849(arg.method_22850(0, 0), 0.5f * (arg.method_22850(0, 2) + arg.method_22850(2, 0)), arg.method_22850(2, 2));
            float i = -pair.getFirst().floatValue();
            float2 = pair.getSecond();
            quaternion2 = new Quaternion(0.0f, i, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - i * i;
            g = -2.0f * i * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + i * i;
            quaternion.copyFrom(quaternion2);
            lv.method_22856();
            lv.method_22851(0, 0, f);
            lv.method_22851(2, 2, f);
            lv.method_22851(2, 0, g);
            lv.method_22851(0, 2, -g);
            lv.method_22851(1, 1, h);
            arg.method_22855(lv);
            lv.method_22847();
            lv.method_22855(arg);
            arg.method_22852(lv);
        }
        if (arg.method_22850(1, 2) * arg.method_22850(1, 2) + arg.method_22850(2, 1) * arg.method_22850(2, 1) > 1.0E-6f) {
            pair = class_4581.method_22849(arg.method_22850(1, 1), 0.5f * (arg.method_22850(1, 2) + arg.method_22850(2, 1)), arg.method_22850(2, 2));
            float_ = pair.getFirst();
            float2 = pair.getSecond();
            quaternion2 = new Quaternion(float_.floatValue(), 0.0f, 0.0f, float2.floatValue());
            f = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
            g = -2.0f * float_.floatValue() * float2.floatValue();
            h = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
            quaternion.copyFrom(quaternion2);
            lv.method_22856();
            lv.method_22851(1, 1, f);
            lv.method_22851(2, 2, f);
            lv.method_22851(2, 1, -g);
            lv.method_22851(1, 2, g);
            lv.method_22851(0, 0, h);
            arg.method_22855(lv);
            lv.method_22847();
            lv.method_22855(arg);
            arg.method_22852(lv);
        }
        return quaternion;
    }

    public void method_22847() {
        this.method_22854(0, 1);
        this.method_22854(0, 2);
        this.method_22854(1, 2);
    }

    private void method_22854(int i, int j) {
        float f = this.field_20864[i + 3 * j];
        this.field_20864[i + 3 * j] = this.field_20864[j + 3 * i];
        this.field_20864[j + 3 * i] = f;
    }

    public Triple<Quaternion, Vector3f, Quaternion> method_22853() {
        Quaternion quaternion = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        Quaternion quaternion2 = new Quaternion(0.0f, 0.0f, 0.0f, 1.0f);
        class_4581 lv = new class_4581(this, true);
        lv.method_22855(this);
        for (int i = 0; i < 5; ++i) {
            quaternion2.copyFrom(class_4581.method_22857(lv));
        }
        quaternion2.method_22873();
        class_4581 lv2 = new class_4581(this);
        lv2.method_22855(new class_4581(quaternion2));
        float f = 1.0f;
        Pair<Float, Float> pair = class_4581.method_22848(lv2.method_22850(0, 0), lv2.method_22850(1, 0));
        Float float_ = pair.getFirst();
        Float float2 = pair.getSecond();
        float g = float2.floatValue() * float2.floatValue() - float_.floatValue() * float_.floatValue();
        float h = -2.0f * float_.floatValue() * float2.floatValue();
        float j = float2.floatValue() * float2.floatValue() + float_.floatValue() * float_.floatValue();
        Quaternion quaternion3 = new Quaternion(0.0f, 0.0f, float_.floatValue(), float2.floatValue());
        quaternion.copyFrom(quaternion3);
        class_4581 lv3 = new class_4581();
        lv3.method_22856();
        lv3.method_22851(0, 0, g);
        lv3.method_22851(1, 1, g);
        lv3.method_22851(1, 0, h);
        lv3.method_22851(0, 1, -h);
        lv3.method_22851(2, 2, j);
        f *= j;
        lv3.method_22855(lv2);
        pair = class_4581.method_22848(lv3.method_22850(0, 0), lv3.method_22850(2, 0));
        float k = -pair.getFirst().floatValue();
        Float float3 = pair.getSecond();
        float l = float3.floatValue() * float3.floatValue() - k * k;
        float m = -2.0f * k * float3.floatValue();
        float n = float3.floatValue() * float3.floatValue() + k * k;
        Quaternion quaternion4 = new Quaternion(0.0f, k, 0.0f, float3.floatValue());
        quaternion.copyFrom(quaternion4);
        class_4581 lv4 = new class_4581();
        lv4.method_22856();
        lv4.method_22851(0, 0, l);
        lv4.method_22851(2, 2, l);
        lv4.method_22851(2, 0, -m);
        lv4.method_22851(0, 2, m);
        lv4.method_22851(1, 1, n);
        f *= n;
        lv4.method_22855(lv3);
        pair = class_4581.method_22848(lv4.method_22850(1, 1), lv4.method_22850(2, 1));
        Float float4 = pair.getFirst();
        Float float5 = pair.getSecond();
        float o = float5.floatValue() * float5.floatValue() - float4.floatValue() * float4.floatValue();
        float p = -2.0f * float4.floatValue() * float5.floatValue();
        float q = float5.floatValue() * float5.floatValue() + float4.floatValue() * float4.floatValue();
        Quaternion quaternion5 = new Quaternion(float4.floatValue(), 0.0f, 0.0f, float5.floatValue());
        quaternion.copyFrom(quaternion5);
        class_4581 lv5 = new class_4581();
        lv5.method_22856();
        lv5.method_22851(1, 1, o);
        lv5.method_22851(2, 2, o);
        lv5.method_22851(2, 1, p);
        lv5.method_22851(1, 2, -p);
        lv5.method_22851(0, 0, q);
        f *= q;
        lv5.method_22855(lv4);
        f = 1.0f / f;
        quaternion.method_22872((float)Math.sqrt(f));
        Vector3f vector3f = new Vector3f(lv5.method_22850(0, 0) * f, lv5.method_22850(1, 1) * f, lv5.method_22850(2, 2) * f);
        return Triple.of(quaternion, vector3f, quaternion2);
    }

    public boolean equals(Object object) {
        if (this == object) {
            return true;
        }
        if (object == null || this.getClass() != object.getClass()) {
            return false;
        }
        class_4581 lv = (class_4581)object;
        return Arrays.equals(this.field_20864, lv.field_20864);
    }

    public int hashCode() {
        return Arrays.hashCode(this.field_20864);
    }

    public void method_22852(class_4581 arg) {
        System.arraycopy(arg.field_20864, 0, this.field_20864, 0, 9);
    }

    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Matrix3f:\n");
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                stringBuilder.append(this.field_20864[i + j * 3]);
                if (j == 2) continue;
                stringBuilder.append(" ");
            }
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }

    public void method_22856() {
        this.field_20864[0] = 1.0f;
        this.field_20864[1] = 0.0f;
        this.field_20864[2] = 0.0f;
        this.field_20864[3] = 0.0f;
        this.field_20864[4] = 1.0f;
        this.field_20864[5] = 0.0f;
        this.field_20864[6] = 0.0f;
        this.field_20864[7] = 0.0f;
        this.field_20864[8] = 1.0f;
    }

    public float method_22850(int i, int j) {
        return this.field_20864[3 * j + i];
    }

    public void method_22851(int i, int j, float f) {
        this.field_20864[3 * j + i] = f;
    }

    public void method_22855(class_4581 arg) {
        float[] fs = Arrays.copyOf(this.field_20864, 9);
        for (int i = 0; i < 3; ++i) {
            for (int j = 0; j < 3; ++j) {
                this.field_20864[i + j * 3] = 0.0f;
                for (int k = 0; k < 3; ++k) {
                    int n = i + j * 3;
                    this.field_20864[n] = this.field_20864[n] + fs[i + k * 3] * arg.field_20864[k + j * 3];
                }
            }
        }
    }
}

