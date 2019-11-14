/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import it.unimi.dsi.fastutil.objects.ObjectList;
import java.util.Random;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Model;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

@Environment(value=EnvType.CLIENT)
public class ModelPart {
    private float textureWidth = 64.0f;
    private float textureHeight = 32.0f;
    private int textureOffsetU;
    private int textureOffsetV;
    public float pivotX;
    public float pivotY;
    public float pivotZ;
    public float pitch;
    public float yaw;
    public float roll;
    public boolean mirror;
    public boolean visible = true;
    private final ObjectList<Cuboid> cuboids = new ObjectArrayList<Cuboid>();
    private final ObjectList<ModelPart> children = new ObjectArrayList<ModelPart>();

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

    public void copyPositionAndRotation(ModelPart modelPart) {
        this.pitch = modelPart.pitch;
        this.yaw = modelPart.yaw;
        this.roll = modelPart.roll;
        this.pivotX = modelPart.pivotX;
        this.pivotY = modelPart.pivotY;
        this.pivotZ = modelPart.pivotZ;
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
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, this.mirror, false);
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, float i, float j, float k) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f, 0.0f, 0.0f, this.mirror, false);
        return this;
    }

    public ModelPart addCuboid(float f, float g, float h, float i, float j, float k, boolean bl) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, 0.0f, 0.0f, 0.0f, bl, false);
        return this;
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, this.mirror, false);
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l, float m, float n) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, m, n, this.mirror, false);
    }

    public void addCuboid(float f, float g, float h, float i, float j, float k, float l, boolean bl) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, f, g, h, i, j, k, l, l, l, bl, false);
    }

    private void addCuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, boolean bl2) {
        this.cuboids.add(new Cuboid(i, j, f, g, h, k, l, m, n, o, p, bl, this.textureWidth, this.textureHeight));
    }

    public void setPivot(float f, float g, float h) {
        this.pivotX = f;
        this.pivotY = g;
        this.pivotZ = h;
    }

    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, @Nullable Sprite sprite) {
        this.render(matrixStack, vertexConsumer, i, j, sprite, 1.0f, 1.0f, 1.0f);
    }

    public void render(MatrixStack matrixStack, VertexConsumer vertexConsumer, int i, int j, @Nullable Sprite sprite, float f, float g, float h) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrixStack.push();
        this.rotate(matrixStack);
        this.renderCuboids(matrixStack.peek(), vertexConsumer, i, j, sprite, f, g, h);
        for (ModelPart modelPart : this.children) {
            modelPart.render(matrixStack, vertexConsumer, i, j, sprite, f, g, h);
        }
        matrixStack.pop();
    }

    public void rotate(MatrixStack matrixStack) {
        matrixStack.translate(this.pivotX / 16.0f, this.pivotY / 16.0f, this.pivotZ / 16.0f);
        if (this.roll != 0.0f) {
            matrixStack.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(this.roll));
        }
        if (this.yaw != 0.0f) {
            matrixStack.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(this.yaw));
        }
        if (this.pitch != 0.0f) {
            matrixStack.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(this.pitch));
        }
    }

    private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int i, int j, @Nullable Sprite sprite, float f, float g, float h) {
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        for (Cuboid cuboid : this.cuboids) {
            for (Quad quad : cuboid.sides) {
                Vector3f vector3f = quad.direction.copy();
                vector3f.multiply(matrix3f);
                float k = vector3f.getX();
                float l = vector3f.getY();
                float m = vector3f.getZ();
                for (int n = 0; n < 4; ++n) {
                    float s;
                    float r;
                    Vertex vertex = quad.vertices[n];
                    float o = vertex.pos.getX() / 16.0f;
                    float p = vertex.pos.getY() / 16.0f;
                    float q = vertex.pos.getZ() / 16.0f;
                    Vector4f vector4f = new Vector4f(o, p, q, 1.0f);
                    vector4f.multiply(matrix4f);
                    if (sprite == null) {
                        r = vertex.u;
                        s = vertex.v;
                    } else {
                        r = sprite.getFrameU(vertex.u * 16.0f);
                        s = sprite.getFrameV(vertex.v * 16.0f);
                    }
                    vertexConsumer.method_23919(vector4f.getX(), vector4f.getY(), vector4f.getZ(), f, g, h, 1.0f, r, s, j, i, k, l, m);
                }
            }
        }
    }

    public ModelPart setTextureSize(int i, int j) {
        this.textureWidth = i;
        this.textureHeight = j;
        return this;
    }

    public Cuboid getRandomCuboid(Random random) {
        return (Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    @Environment(value=EnvType.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float f, float g, float h, float i, float j) {
            this(new Vector3f(f, g, h), i, j);
        }

        public Vertex remap(float f, float g) {
            return new Vertex(this.pos, f, g);
        }

        public Vertex(Vector3f vector3f, float f, float g) {
            this.pos = vector3f;
            this.u = f;
            this.v = g;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Quad {
        public final Vertex[] vertices;
        public final Vector3f direction;

        public Quad(Vertex[] vertexs, float f, float g, float h, float i, float j, float k, boolean bl, Direction direction) {
            this.vertices = vertexs;
            float l = 0.0f / j;
            float m = 0.0f / k;
            vertexs[0] = vertexs[0].remap(h / j - l, g / k + m);
            vertexs[1] = vertexs[1].remap(f / j + l, g / k + m);
            vertexs[2] = vertexs[2].remap(f / j + l, i / k - m);
            vertexs[3] = vertexs[3].remap(h / j - l, i / k - m);
            if (bl) {
                int n = vertexs.length;
                for (int o = 0; o < n / 2; ++o) {
                    Vertex vertex = vertexs[o];
                    vertexs[o] = vertexs[n - 1 - o];
                    vertexs[n - 1 - o] = vertex;
                }
            }
            this.direction = direction.getUnitVector();
            if (bl) {
                this.direction.piecewiseMultiply(-1.0f, 1.0f, 1.0f);
            }
        }
    }

    @Environment(value=EnvType.CLIENT)
    public static class Cuboid {
        private final Quad[] sides;
        public final float minX;
        public final float minY;
        public final float minZ;
        public final float maxX;
        public final float maxY;
        public final float maxZ;

        public Cuboid(int i, int j, float f, float g, float h, float k, float l, float m, float n, float o, float p, boolean bl, float q, float r) {
            this.minX = f;
            this.minY = g;
            this.minZ = h;
            this.maxX = f + k;
            this.maxY = g + l;
            this.maxZ = h + m;
            this.sides = new Quad[6];
            float s = f + k;
            float t = g + l;
            float u = h + m;
            f -= n;
            g -= o;
            h -= p;
            s += n;
            t += o;
            u += p;
            if (bl) {
                float v = s;
                s = f;
                f = v;
            }
            Vertex vertex = new Vertex(f, g, h, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(s, g, h, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(s, t, h, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(f, t, h, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(f, g, u, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(s, g, u, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(s, t, u, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(f, t, u, 8.0f, 0.0f);
            float w = i;
            float x = (float)i + m;
            float y = (float)i + m + k;
            float z = (float)i + m + k + k;
            float aa = (float)i + m + k + m;
            float ab = (float)i + m + k + m + k;
            float ac = j;
            float ad = (float)j + m;
            float ae = (float)j + m + l;
            this.sides[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, x, ac, y, ad, q, r, bl, Direction.DOWN);
            this.sides[3] = new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, y, ad, z, ac, q, r, bl, Direction.UP);
            this.sides[1] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, w, ad, x, ae, q, r, bl, Direction.WEST);
            this.sides[4] = new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, x, ad, y, ae, q, r, bl, Direction.NORTH);
            this.sides[0] = new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, y, ad, aa, ae, q, r, bl, Direction.EAST);
            this.sides[5] = new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, aa, ad, ab, ae, q, r, bl, Direction.SOUTH);
        }
    }
}

