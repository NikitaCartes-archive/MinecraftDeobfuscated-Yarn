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
import net.minecraft.client.util.math.MatrixStack;
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

    default public void method_23919(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
        this.vertex(f, g, h);
        this.color(i, j, k, l);
        this.texture(m, n);
        this.overlay(o);
        this.light(p);
        this.normal(q, r, s);
        this.next();
    }

    default public VertexConsumer color(float f, float g, float h, float i) {
        return this.color((int)(f * 255.0f), (int)(g * 255.0f), (int)(h * 255.0f), (int)(i * 255.0f));
    }

    default public VertexConsumer light(int i) {
        return this.light(i & 0xFFFF, i >> 16 & 0xFFFF);
    }

    default public VertexConsumer overlay(int i) {
        return this.overlay(i & 0xFFFF, i >> 16 & 0xFFFF);
    }

    default public void quad(MatrixStack.Entry entry, BakedQuad bakedQuad, float f, float g, float h, int i, int j) {
        this.quad(entry, bakedQuad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, f, g, h, new int[]{i, i, i, i}, j, false);
    }

    default public void quad(MatrixStack.Entry entry, BakedQuad bakedQuad, float[] fs, float f, float g, float h, int[] is, int i, boolean bl) {
        int[] js = bakedQuad.getVertexData();
        Vec3i vec3i = bakedQuad.getFace().getVector();
        Vector3f vector3f = new Vector3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        Matrix4f matrix4f = entry.getModel();
        vector3f.multiply(entry.getNormal());
        int j = 8;
        int k = js.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSize());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int l = 0; l < k; ++l) {
                float u;
                float t;
                float s;
                float r;
                float q;
                intBuffer.clear();
                intBuffer.put(js, l * 8, 8);
                float m = byteBuffer.getFloat(0);
                float n = byteBuffer.getFloat(4);
                float o = byteBuffer.getFloat(8);
                if (bl) {
                    float p = (float)(byteBuffer.get(12) & 0xFF) / 255.0f;
                    q = (float)(byteBuffer.get(13) & 0xFF) / 255.0f;
                    r = (float)(byteBuffer.get(14) & 0xFF) / 255.0f;
                    s = p * fs[l] * f;
                    t = q * fs[l] * g;
                    u = r * fs[l] * h;
                } else {
                    s = fs[l] * f;
                    t = fs[l] * g;
                    u = fs[l] * h;
                }
                int v = is[l];
                q = byteBuffer.getFloat(16);
                r = byteBuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(m, n, o, 1.0f);
                vector4f.multiply(matrix4f);
                this.method_23919(vector4f.getX(), vector4f.getY(), vector4f.getZ(), s, t, u, 1.0f, q, r, i, v, vector3f.getX(), vector3f.getY(), vector3f.getZ());
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f matrix4f, float f, float g, float h) {
        Vector4f vector4f = new Vector4f(f, g, h, 1.0f);
        vector4f.multiply(matrix4f);
        return this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }

    default public VertexConsumer method_23763(Matrix3f matrix3f, float f, float g, float h) {
        Vector3f vector3f = new Vector3f(f, g, h);
        vector3f.multiply(matrix3f);
        return this.normal(vector3f.getX(), vector3f.getY(), vector3f.getZ());
    }
}

