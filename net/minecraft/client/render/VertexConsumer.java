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

/**
 * An interface that consumes vertices in a certain {@linkplain
 * VertexFormat vertex format}.
 * 
 * <p>The vertex elements must be specified in the same order as defined in
 * the format the vertices being consumed are in.
 */
@Environment(value=EnvType.CLIENT)
public interface VertexConsumer {
    /**
     * Specifies the {@linkplain VertexFormats#POSITION_ELEMENT
     * position element} of the current vertex.
     * 
     * <p>This is typically the first element in a vertex, hence the name.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a position element.
     * 
     * @return this consumer, for chaining
     */
    public VertexConsumer vertex(double var1, double var3, double var5);

    /**
     * Specifies the {@linkplain VertexFormats#COLOR_ELEMENT
     * color element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a color element or if a color has been set in {@link
     * #fixedColor}.
     * 
     * @return this consumer, for chaining
     */
    public VertexConsumer color(int var1, int var2, int var3, int var4);

    /**
     * Specifies the {@linkplain VertexFormats#TEXTURE_ELEMENT
     * texture element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a texture element.
     * 
     * @return this consumer, for chaining
     */
    public VertexConsumer texture(float var1, float var2);

    /**
     * Specifies the {@linkplain VertexFormats#OVERLAY_ELEMENT
     * overlay element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting an overlay element.
     * 
     * @return this consumer, for chaining
     */
    public VertexConsumer overlay(int var1, int var2);

    /**
     * Specifies the {@linkplain VertexFormats#LIGHT_ELEMENT
     * light element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a light element.
     * 
     * @return this consumer, for chaining
     */
    public VertexConsumer light(int var1, int var2);

    /**
     * Specifies the {@linkplain VertexFormats#NORMAL_ELEMENT
     * normal element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a normal element.
     * 
     * @return this consumer, for chaining
     */
    public VertexConsumer normal(float var1, float var2, float var3);

    /**
     * Starts consuming the next vertex.
     * 
     * <p>This method must be called after specifying all elements in a vertex.
     */
    public void next();

    /**
     * Specifies the
     * {@linkplain VertexFormats#POSITION_ELEMENT position},
     * {@linkplain VertexFormats#COLOR_ELEMENT color},
     * {@linkplain VertexFormats#TEXTURE_ELEMENT texture},
     * {@linkplain VertexFormats#OVERLAY_ELEMENT overlay},
     * {@linkplain VertexFormats#LIGHT_ELEMENT light}, and
     * {@linkplain VertexFormats#NORMAL_ELEMENT normal} elements of the
     * current vertex and starts consuming the next vertex.
     * 
     * @throws IllegalStateException if a color has been set in {@link
     * #fixedColor}.
     */
    default public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.vertex(x, y, z);
        this.color(red, green, blue, alpha);
        this.texture(u, v);
        this.overlay(overlay);
        this.light(light);
        this.normal(normalX, normalY, normalZ);
        this.next();
    }

    /**
     * Makes this consumer always use the same color for subsequent vertices
     * until {@link #unfixColor} is called.
     * 
     * <p>The color will be automatically supplied when the color element is
     * requested. Make sure not to specify the color yourself when using this
     * method.
     */
    public void fixedColor(int var1, int var2, int var3, int var4);

    /**
     * Makes this consumer no longer use the color set in {@link #fixedColor}.
     */
    public void unfixColor();

    /**
     * Specifies the {@linkplain VertexFormats#COLOR_ELEMENT
     * color element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a color element or if a color has been set in {@link
     * #fixedColor}.
     * 
     * @return this consumer, for chaining
     */
    default public VertexConsumer color(float red, float green, float blue, float alpha) {
        return this.color((int)(red * 255.0f), (int)(green * 255.0f), (int)(blue * 255.0f), (int)(alpha * 255.0f));
    }

    /**
     * Specifies the {@linkplain VertexFormats#COLOR_ELEMENT
     * color element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a color element or if a color has been set in {@link
     * #fixedColor}.
     * 
     * @return this consumer, for chaining
     */
    default public VertexConsumer color(int argb) {
        return this.color(ColorHelper.Argb.getRed(argb), ColorHelper.Argb.getGreen(argb), ColorHelper.Argb.getBlue(argb), ColorHelper.Argb.getAlpha(argb));
    }

    /**
     * Specifies the {@linkplain VertexFormats#LIGHT_ELEMENT
     * light element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a light element.
     * 
     * @return this consumer, for chaining
     */
    default public VertexConsumer light(int uv) {
        return this.light(uv & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F), uv >> 16 & (LightmapTextureManager.MAX_BLOCK_LIGHT_COORDINATE | 0xFF0F));
    }

    /**
     * Specifies the {@linkplain VertexFormats#OVERLAY_ELEMENT
     * overlay element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting an overlay element.
     * 
     * @return this consumer, for chaining
     */
    default public VertexConsumer overlay(int uv) {
        return this.overlay(uv & 0xFFFF, uv >> 16 & 0xFFFF);
    }

    /**
     * Specifies the vertex elements from {@code quad} and starts consuming
     * the next vertex.
     * 
     * @throws IllegalStateException if a color has been set in {@link
     * #fixedColor}.
     */
    default public void quad(MatrixStack.Entry matrixEntry, BakedQuad quad, float red, float green, float blue, int light, int overlay) {
        this.quad(matrixEntry, quad, new float[]{1.0f, 1.0f, 1.0f, 1.0f}, red, green, blue, new int[]{light, light, light, light}, overlay, false);
    }

    /**
     * Specifies the vertex elements from {@code quad} and starts consuming
     * the next vertex.
     * 
     * @throws IllegalStateException if a color has been set in {@link
     * #fixedColor}.
     */
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

    /**
     * Specifies the {@linkplain VertexFormats#POSITION_ELEMENT
     * position element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a position element.
     * 
     * @return this consumer, for chaining
     * 
     * @param matrix the matrix that will be applied to the vertex position, typically {@link
     * net.minecraft.client.util.math.MatrixStack.Entry#getPositionMatrix
     * MatrixStack.Entry#getPositionMatrix}
     */
    default public VertexConsumer vertex(Matrix4f matrix, float x, float y, float z) {
        Vector4f vector4f = new Vector4f(x, y, z, 1.0f);
        vector4f.transform(matrix);
        return this.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ());
    }

    /**
     * Specifies the {@linkplain VertexFormats#NORMAL_ELEMENT
     * normal element} of the current vertex.
     * 
     * @throws IllegalStateException if this consumer is not currently
     * accepting a normal element.
     * 
     * @return this consumer, for chaining
     * 
     * @param matrix the matrix that will be applied to the normal vector, typically {@link
     * net.minecraft.client.util.math.MatrixStack.Entry#getNormalMatrix
     * MatrixStack.Entry#getNormalMatrix}
     */
    default public VertexConsumer normal(Matrix3f matrix, float x, float y, float z) {
        Vec3f vec3f = new Vec3f(x, y, z);
        vec3f.transform(matrix);
        return this.normal(vec3f.getX(), vec3f.getY(), vec3f.getZ());
    }
}

