/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.datafixers.util.Pair;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.BufferRenderer;
import net.minecraft.client.render.VertexFormat;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.GlAllocationUtils;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Quaternion;
import net.minecraft.util.math.Vec3d;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelPart {
    private static final BufferBuilder field_20790 = new BufferBuilder(256);
    private float textureWidth = 64.0f;
    private float textureHeight = 32.0f;
    private int textureOffsetU;
    private int textureOffsetV;
    public float rotationPointX;
    public float rotationPointY;
    public float rotationPointZ;
    public float pitch;
    public float yaw;
    public float roll;
    @Nullable
    private ByteBuffer compiled;
    public boolean mirror;
    public boolean visible = true;
    private final List<Cuboid> cuboids = Lists.newArrayList();
    private final List<ModelPart> children = Lists.newArrayList();

    public ModelPart(Model model) {
        model.method_22696(this);
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }

    public ModelPart(Model model, int i, int j) {
        this(model.textureWidth, model.textureHeight, i, j);
        model.method_22696(this);
    }

    public ModelPart(int i, int j, int k, int l) {
        this.setTextureSize(i, j);
        this.setTextureOffset(k, l);
    }

    public void copyRotation(ModelPart modelPart) {
        this.pitch = modelPart.pitch;
        this.yaw = modelPart.yaw;
        this.roll = modelPart.roll;
        this.rotationPointX = modelPart.rotationPointX;
        this.rotationPointY = modelPart.rotationPointY;
        this.rotationPointZ = modelPart.rotationPointZ;
    }

    public void addChild(ModelPart modelPart) {
        this.children.add(modelPart);
    }

    public ModelPart setTextureOffset(int i, int j) {
        this.textureOffsetU = i;
        this.textureOffsetV = j;
        return this;
    }

    public ModelPart addCuboid(String string, float f, float g, float h, int i, int j, int k, float l, int m, int n) {
        this.setTextureOffset(m, n);
        this.cuboids.add(new Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, this.mirror, this.textureWidth, this.textureHeight));
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, float i, float j, float k) {
        this.cuboids.add(new Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f, this.mirror, this.textureWidth, this.textureHeight));
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, float i, float j, float k, boolean bl) {
        this.cuboids.add(new Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f, bl, this.textureWidth, this.textureHeight));
        return this;
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l) {
        this.cuboids.add(new Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, this.mirror, this.textureWidth, this.textureHeight));
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l, boolean bl) {
        this.cuboids.add(new Cuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, bl, this.textureWidth, this.textureHeight));
    }

    public void setRotationPoint(float f, float g, float h) {
        this.rotationPointX = f;
        this.rotationPointY = g;
        this.rotationPointZ = h;
    }

    public void render(float f) {
        if (!this.visible) {
            return;
        }
        this.compile(f);
        if (this.compiled == null) {
            return;
        }
        RenderSystem.pushMatrix();
        this.method_22703(f);
        this.compiled.clear();
        int i = this.compiled.remaining() / VertexFormats.POSITION_UV_NORMAL_2.getVertexSize();
        BufferRenderer.method_22637(this.compiled, 7, VertexFormats.POSITION_UV_NORMAL_2, i);
        for (ModelPart modelPart : this.children) {
            modelPart.render(f);
        }
        RenderSystem.popMatrix();
    }

    public void method_22698(BufferBuilder bufferBuilder, float f, int i, int j, Sprite sprite) {
        this.method_22699(bufferBuilder, f, i, j, sprite, 1.0f, 1.0f, 1.0f);
    }

    public void method_22699(BufferBuilder bufferBuilder, float f, int i, int j, Sprite sprite, float g, float h, float k) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        bufferBuilder.method_22629();
        bufferBuilder.method_22626(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
        if (this.roll != 0.0f) {
            bufferBuilder.method_22622(new Quaternion(Vector3f.field_20707, this.roll, false));
        }
        if (this.yaw != 0.0f) {
            bufferBuilder.method_22622(new Quaternion(Vector3f.field_20705, this.yaw, false));
        }
        if (this.pitch != 0.0f) {
            bufferBuilder.method_22622(new Quaternion(Vector3f.field_20703, this.pitch, false));
        }
        this.method_22702(bufferBuilder, f, i, j, sprite, g, h, k);
        for (ModelPart modelPart : this.children) {
            modelPart.method_22698(bufferBuilder, f, i, j, sprite);
        }
        bufferBuilder.method_22630();
    }

    private void compile(float f) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        if (this.compiled == null) {
            field_20790.begin(7, VertexFormats.POSITION_UV_NORMAL_2);
            this.method_22701(field_20790, f, 240, 240, null);
            field_20790.end();
            Pair<BufferBuilder.class_4574, ByteBuffer> pair = field_20790.method_22632();
            ByteBuffer byteBuffer = pair.getSecond();
            this.compiled = GlAllocationUtils.allocateByteBuffer(byteBuffer.remaining());
            this.compiled.put(byteBuffer);
        }
    }

    public void applyTransform(float f) {
        if (!this.visible) {
            return;
        }
        this.method_22703(f);
    }

    private void method_22703(float f) {
        RenderSystem.translatef(this.rotationPointX * f, this.rotationPointY * f, this.rotationPointZ * f);
        if (this.roll != 0.0f) {
            RenderSystem.rotatef(this.roll * 57.295776f, 0.0f, 0.0f, 1.0f);
        }
        if (this.yaw != 0.0f) {
            RenderSystem.rotatef(this.yaw * 57.295776f, 0.0f, 1.0f, 0.0f);
        }
        if (this.pitch != 0.0f) {
            RenderSystem.rotatef(this.pitch * 57.295776f, 1.0f, 0.0f, 0.0f);
        }
    }

    private void method_22701(BufferBuilder bufferBuilder, float f, int i, int j, @Nullable Sprite sprite) {
        this.method_22702(bufferBuilder, f, i, j, sprite, 1.0f, 1.0f, 1.0f);
    }

    private void method_22702(BufferBuilder bufferBuilder, float f, int i, int j, @Nullable Sprite sprite, float g, float h, float k) {
        Matrix4f matrix4f = bufferBuilder.method_22631();
        VertexFormat vertexFormat = bufferBuilder.getVertexFormat();
        for (Cuboid cuboid : this.cuboids) {
            for (Quad quad : cuboid.polygons) {
                Vec3d vec3d = quad.vertices[1].pos.reverseSubtract(quad.vertices[0].pos);
                Vec3d vec3d2 = quad.vertices[1].pos.reverseSubtract(quad.vertices[2].pos);
                Vec3d vec3d3 = vec3d2.crossProduct(vec3d).normalize();
                float l = (float)vec3d3.x;
                float m = (float)vec3d3.y;
                float n = (float)vec3d3.z;
                for (int o = 0; o < 4; ++o) {
                    Vertex vertex = quad.vertices[o];
                    Vector4f vector4f = new Vector4f((float)vertex.pos.x * f, (float)vertex.pos.y * f, (float)vertex.pos.z * f, 1.0f);
                    vector4f.method_22674(matrix4f);
                    bufferBuilder.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ());
                    if (vertexFormat.hasColorElement()) {
                        float p = MathHelper.method_22451(l, m, n);
                        bufferBuilder.color(p * g, p * h, p * k, 1.0f);
                    }
                    if (sprite == null) {
                        bufferBuilder.texture(vertex.u, vertex.v);
                    } else {
                        bufferBuilder.texture(sprite.getU(vertex.u * 16.0f), sprite.getV(vertex.v * 16.0f));
                    }
                    if (vertexFormat.hasUvElement(1)) {
                        bufferBuilder.texture(i, j);
                    }
                    bufferBuilder.normal(l, m, n).next();
                }
            }
        }
    }

    public ModelPart setTextureSize(int i, int j) {
        this.textureWidth = i;
        this.textureHeight = j;
        return this;
    }

    public Cuboid method_22700(Random random) {
        return this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    @Environment(value=EnvType.CLIENT)
    static class Vertex {
        public final Vec3d pos;
        public final float u;
        public final float v;

        public Vertex(float f, float g, float h, float i, float j) {
            this(new Vec3d(f, g, h), i, j);
        }

        public Vertex remap(float f, float g) {
            return new Vertex(this.pos, f, g);
        }

        public Vertex(Vec3d vec3d, float f, float g) {
            this.pos = vec3d;
            this.u = f;
            this.v = g;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Quad {
        public Vertex[] vertices;

        public Quad(Vertex[] vertexs) {
            this.vertices = vertexs;
        }

        public Quad(Vertex[] vertexs, float f, float g, float h, float i, float j, float k) {
            this(vertexs);
            float l = 0.0f / j;
            float m = 0.0f / k;
            vertexs[0] = vertexs[0].remap(h / j - l, g / k + m);
            vertexs[1] = vertexs[1].remap(f / j + l, g / k + m);
            vertexs[2] = vertexs[2].remap(f / j + l, i / k - m);
            vertexs[3] = vertexs[3].remap(h / j - l, i / k - m);
        }

        public void flip() {
            Vertex[] vertexs = new Vertex[this.vertices.length];
            for (int i = 0; i < this.vertices.length; ++i) {
                vertexs[i] = this.vertices[this.vertices.length - i - 1];
            }
            this.vertices = vertexs;
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Cuboid {
        private final Quad[] polygons;
        public final float xMin;
        public final float yMin;
        public final float zMin;
        public final float xMax;
        public final float yMax;
        public final float zMax;

        public Cuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, boolean bl, float o, float p) {
            this.xMin = f;
            this.yMin = g;
            this.zMin = h;
            this.xMax = f + k;
            this.yMax = g + l;
            this.zMax = h + m;
            this.polygons = new Quad[6];
            float q = f + k;
            float r = g + l;
            float s = h + m;
            f -= n;
            g -= n;
            h -= n;
            q += n;
            r += n;
            s += n;
            if (bl) {
                float t = q;
                q = f;
                f = t;
            }
            Vertex vertex = new Vertex(f, g, h, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(q, g, h, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(q, r, h, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(f, r, h, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(f, g, s, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(q, g, s, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(q, r, s, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(f, r, s, 8.0f, 0.0f);
            this.polygons[0] = new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, (float)i + m + k, (float)j + m, (float)i + m + k + m, (float)j + m + l, o, p);
            this.polygons[1] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, i, (float)j + m, (float)i + m, (float)j + m + l, o, p);
            this.polygons[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, (float)i + m, j, (float)i + m + k, (float)j + m, o, p);
            this.polygons[3] = new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, (float)i + m + k, (float)j + m, (float)i + m + k + k, j, o, p);
            this.polygons[4] = new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, (float)i + m, (float)j + m, (float)i + m + k, (float)j + m + l, o, p);
            this.polygons[5] = new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, (float)i + m + k + m, (float)j + m, (float)i + m + k + m + k, (float)j + m + l, o, p);
            if (bl) {
                for (Quad quad : this.polygons) {
                    quad.flip();
                }
            }
        }
    }
}

