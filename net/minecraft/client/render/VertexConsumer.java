/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.LightmapTextureManager;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.model.BakedQuad;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.ColorHelper;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.math.Vector4f;
import org.lwjgl.system.MemoryStack;

@Environment(value=EnvType.CLIENT)
public interface VertexConsumer {
    public VertexConsumer vertex(double var1, double var3, double var5);

    public VertexConsumer color(int var1, int var2, int var3, int var4);

    public VertexConsumer texture(float var1, float var2);

    public VertexConsumer overlay(int var1, int var2);

    public VertexConsumer light(int var1, int var2);

    public VertexConsumer normal(float var1, float var2, float var3);

    public void next();

    default public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.vertex(x, y, z);
        this.color(red, green, blue, alpha);
        this.texture(u, v);
        this.overlay(overlay);
        this.light(light);
        this.normal(normalX, normalY, normalZ);
        this.next();
    }

    public void fixedColor(int var1, int var2, int var3, int var4);

    public void unfixColor();

    default public VertexConsumer color(float red, float green, float blue, float alpha) {
        return this.color((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), (int)(alpha * 255.0f));
    }

    default public VertexConsumer color(int argb) {
        return this.color(ColorHelper.Argb.getRed(argb), ColorHelper.Argb.getGreen(argb), ColorHelper.Argb.getBlue(argb), ColorHelper.Argb.getAlpha(argb));
    }

    default public VertexConsumer light(int uv) {
        return this.light(uv & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F), uv >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F));
    }

    default public VertexConsumer overlay(int uv) {
        return this.overlay(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
        this.quad(matrixEntry, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, red, green, blue, new int[]{light, light, light, light}, overlay, false);
    }

    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float[] brightnesses, float red, float green, float blue, int[] lights, int overlay, boolean useQuadColorData) {
        float[] fs = new float[]{brightnesses[0], brightnesses[1], brightnesses[2], brightnesses[3]};
        int[] is = new int[]{lights[0], lights[1], lights[2], lights[3]};
        int[] js = quad.getVertexData();
        Vec3i vec3i = quad.getFace().getVector();
        Vec3f vec3f = new Vec3f(vec3i.getX(), vec3i.getY(), vec3i.getZ());
        Matrix4f matrix4f = matrixEntry.getPositionMatrix();
        vec3f.transform(matrixEntry.getNormalMatrix());
        int i = 8;
        int j = js.length / 8;
        try (MemoryStack memoryStack = MemoryStack.stackPush();){
            ByteBuffer byteBuffer = memoryStack.malloc(VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL.getVertexSizeByte());
            IntBuffer intBuffer = byteBuffer.asIntBuffer();
            for (int k = 0; k < j; ++k) {
                float q;
                float p;
                float o;
                float n;
                float m;
                intBuffer.clear();
                intBuffer.put(js, k * 8, 8);
                float f = byteBuffer.getFloat(0);
                float g = byteBuffer.getFloat(4);
                float h = byteBuffer.getFloat(8);
                if (useQuadColorData) {
                    float l = (float)(byteBuffer.get(12) & 0xFF) / 255.0f;
                    m = (float)(byteBuffer.get(13) & 0xFF) / 255.0f;
                    n = (float)(byteBuffer.get(14) & 0xFF) / 255.0f;
                    o = l * fs[k] * red;
                    p = m * fs[k] * green;
                    q = n * fs[k] * blue;
                } else {
                    o = fs[k] * red;
                    p = fs[k] * green;
                    q = fs[k] * blue;
                }
                int r = is[k];
                m = byteBuffer.getFloat(16);
                n = byteBuffer.getFloat(20);
                Vector4f vector4f = new Vector4f(f, g, h, 1.0f);
                vector4f.transform(matrix4f);
                this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), o, p, q, 1.0f, m, n, overlay, r, vec3f.getX(), vec3f.getY(), vec3f.getZ());
            }
        }
    }

    default public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        Vector4f vector4f = new Vector4f(x, y, z, 1.0f);
        vector4f.transform(matrix);
        return this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }

    default public VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
        Vec3f vec3f = new Vec3f(x, y, z);
        vec3f.transform(matrix);
        return this.normal(vec3f.getX(), vec3f.getY(), vec3f.getZ());
    }
}

