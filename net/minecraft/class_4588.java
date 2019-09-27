/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public interface class_4588 {
    public class_4588 vertex(double var1, double var3, double var5);

    public class_4588 color(int var1, int var2, int var3, int var4);

    public class_4588 texture(float var1, float var2);

    public class_4588 method_22917(int var1, int var2);

    public class_4588 method_22921(int var1, int var2);

    public class_4588 method_22914(float var1, float var2, float var3);

    public void next();

    public void method_22922(int var1, int var2);

    public void method_22923();

    default public class_4588 method_22915(float f, float g, float h, float i) {
        return this.color((int)(f * 255.0f), (int)(g * 255.0f), (int)(h * 255.0f), (int)(i * 255.0f));
    }

    default public class_4588 method_22916(int i) {
        return this.method_22921(i & 0xFFFF, i >> 16 & 0xFFFF);
    }

    default public void method_22919(Matrix4f matrix4f, BakedQuad bakedQuad, float f, float g, float h, int i) {
        this.method_22920(matrix4f, bakedQuad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, f, g, h, new int[]{i, i, i, i}, false);
    }

    default public void method_22920(Matrix4f matrix4f, BakedQuad bakedQuad, float[] fs, float f, float g, float h, int[] is, boolean bl) {
        int[] js = bakedQuad.getVertexData();
        Vec3i vec3i = bakedQuad.getFace().getVector();
        int i = 8;
        int j = js.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_UV_NORMAL.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int k = 0; k < j; ++k) {
                byte d;
                byte c;
                byte b;
                int o;
                intBuffer.clear();
                intBuffer.put(js, k * 8, 8);
                float l = byteBuffer.getFloat(0);
                float m = byteBuffer.getFloat(4);
                float n = byteBuffer.getFloat(8);
                if (bl) {
                    o = byteBuffer.get(12) & 0xFF;
                    int p = byteBuffer.get(13) & 0xFF;
                    int q = byteBuffer.get(14) & 0xFF;
                    b = (byte)((float)o * fs[k] * f);
                    c = (byte)((float)p * fs[k] * g);
                    d = (byte)((float)q * fs[k] * h);
                } else {
                    b = (byte)(255.0f * fs[k] * f);
                    c = (byte)(255.0f * fs[k] * g);
                    d = (byte)(255.0f * fs[k] * h);
                }
                o = is[k];
                float r = byteBuffer.getFloat(16);
                float s = byteBuffer.getFloat(20);
                this.method_22918(matrix4f, l, m, n);
                this.color(b, c, d, 255);
                this.texture(r, s);
                this.method_22916(o);
                this.method_22914(vec3i.getX(), vec3i.getY(), vec3i.getZ());
                this.next();
            }
        }
    }

    default public class_4588 method_22918(Matrix4f matrix4f, float f, float g, float h) {
        Vector4f vector4f = new Vector4f(f, g, h, 1.0f);
        vector4f.method_22674(matrix4f);
        return this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }
}

