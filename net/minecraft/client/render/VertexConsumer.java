/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Vec3i;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public interface VertexConsumer {
    public static final Logger LOGGER = LogManager.getLogger();

    public VertexConsumer vertex(double var1, double var3, double var5);

    public VertexConsumer color(int var1, int var2, int var3, int var4);

    public VertexConsumer texture(float var1, float var2);

    public VertexConsumer overlay(int var1, int var2);

    public VertexConsumer light(int var1, int var2);

    public VertexConsumer normal(float var1, float var2, float var3);

    public void next();

    default public VertexConsumer color(float f, float g, float h, float i) {
        return this.color((int)(f * 255.0f), (int)(g * 255.0f), (int)(h * 255.0f), (int)(i * 255.0f));
    }

    default public VertexConsumer light(int i) {
        return this.light(i & 0xFFFF, i >> 16 & 0xFFFF);
    }

    default public VertexConsumer defaultOverlay(int i) {
        return this.overlay(i & 0xFFFF, i >> 16 & 0xFFFF);
    }

    default public void quad(Matrix4f matrix4f, Matrix3f matrix3f, BakedQuad bakedQuad, float f, float g, float h, int i, int j) {
        this.quad(matrix4f, matrix3f, bakedQuad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, f, g, h, new int[]{i, i, i, i}, j, false);
    }

    default public void quad(Matrix4f matrix4f, Matrix3f matrix3f, BakedQuad bakedQuad, float[] fs, float f, float g, float h, int[] is, int i, boolean bl) {
        int[] js = bakedQuad.getVertexData();
        Vec3i vec3i = bakedQuad.getFace().getVector();
        Vector3f vector3f = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        vector3f.multiply(matrix3f);
        int j = 8;
        int k = js.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_UV_NORMAL.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int l = 0; l < k; ++l) {
                byte d;
                byte c;
                byte b;
                int p;
                intBuffer.clear();
                intBuffer.put(js, l * 8, 8);
                float m = byteBuffer.getFloat(0);
                float n = byteBuffer.getFloat(4);
                float o = byteBuffer.getFloat(8);
                if (bl) {
                    p = byteBuffer.get(12) & 0xFF;
                    int q = byteBuffer.get(13) & 0xFF;
                    int r = byteBuffer.get(14) & 0xFF;
                    b = (byte)((float)p * fs[l] * f);
                    c = (byte)((float)q * fs[l] * g);
                    d = (byte)((float)r * fs[l] * h);
                } else {
                    b = (byte)(255.0f * fs[l] * f);
                    c = (byte)(255.0f * fs[l] * g);
                    d = (byte)(255.0f * fs[l] * h);
                }
                p = is[l];
                float s = byteBuffer.getFloat(16);
                float t = byteBuffer.getFloat(20);
                this.vertex(matrix4f, m, n, o);
                this.color(b, c, d, 255);
                this.texture(s, t);
                this.defaultOverlay(i);
                this.light(p);
                this.normal(vector3f.getX(), vector3f.getY(), vector3f.getZ());
                this.next();
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f matrix4f, float f, float g, float h) {
        Vector4f vector4f = new Vector4f(f, g, h, 1.0f);
        vector4f.multiply(matrix4f);
        return this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }
}

