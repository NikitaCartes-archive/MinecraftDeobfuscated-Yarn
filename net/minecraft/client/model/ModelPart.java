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
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.math.Vector4f;

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
        model.accept(this);
        this.setTextureSize(model.textureWidth, model.textureHeight);
    }

    public ModelPart(Model model, int textureOffsetU, int textureOffsetV) {
        this(model.textureWidth, model.textureHeight, textureOffsetU, textureOffsetV);
        model.accept(this);
    }

    public ModelPart(int textureWidth, int textureHeight, int textureOffsetU, int textureOffsetV) {
        this.setTextureSize(textureWidth, textureHeight);
        this.setTextureOffset(textureOffsetU, textureOffsetV);
    }

    private ModelPart() {
    }

    public ModelPart method_29991() {
        ModelPart modelPart = new ModelPart();
        modelPart.copyTransform(this);
        return modelPart;
    }

    public void copyTransform(ModelPart part) {
        this.pitch = part.pitch;
        this.yaw = part.yaw;
        this.roll = part.roll;
        this.pivotX = part.pivotX;
        this.pivotY = part.pivotY;
        this.pivotZ = part.pivotZ;
    }

    public void addChild(ModelPart part) {
        this.children.add(part);
    }

    public ModelPart setTextureOffset(int textureOffsetU, int textureOffsetV) {
        this.textureOffsetU = textureOffsetU;
        this.textureOffsetV = textureOffsetV;
        return this;
    }

    public ModelPart addCuboid(String name, float x, float y, float z, int sizeX, int sizeY, int sizeZ, float extra, int textureOffsetU, int textureOffsetV) {
        this.setTextureOffset(textureOffsetU, textureOffsetV);
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, extra, extra, this.mirror, false);
        return this;
    }

    public ModelPart addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0f, 0.0f, 0.0f, this.mirror, false);
        return this;
    }

    public ModelPart addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, boolean mirror) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, 0.0f, 0.0f, 0.0f, mirror, false);
        return this;
    }

    public void addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extra) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, extra, extra, this.mirror, false);
    }

    public void addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, this.mirror, false);
    }

    public void addCuboid(float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extra, boolean mirror) {
        this.addCuboid(this.textureOffsetU, this.textureOffsetV, x, y, z, sizeX, sizeY, sizeZ, extra, extra, extra, mirror, false);
    }

    private void addCuboid(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, boolean bl) {
        this.cuboids.add(new Cuboid(u, v, x, y, z, sizeX, sizeY, sizeZ, extraX, extraY, extraZ, mirror, this.textureWidth, this.textureHeight));
    }

    public void setPivot(float x, float y, float z) {
        this.pivotX = x;
        this.pivotY = y;
        this.pivotZ = z;
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay) {
        this.render(matrices, vertices, light, overlay, 1.0f, 1.0f, 1.0f, 1.0f);
    }

    public void render(MatrixStack matrices, VertexConsumer vertices, int light, int overlay, float red, float green, float blue, float alpha) {
        if (!this.visible) {
            return;
        }
        if (this.cuboids.isEmpty() && this.children.isEmpty()) {
            return;
        }
        matrices.push();
        this.rotate(matrices);
        this.renderCuboids(matrices.peek(), vertices, light, overlay, red, green, blue, alpha);
        for (ModelPart modelPart : this.children) {
            modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
        matrices.pop();
    }

    public void rotate(MatrixStack matrix) {
        matrix.translate(this.pivotX / 16.0f, this.pivotY / 16.0f, this.pivotZ / 16.0f);
        if (this.roll != 0.0f) {
            matrix.multiply(Vec3f.POSITIVE_Z.getRadialQuaternion(this.roll));
        }
        if (this.yaw != 0.0f) {
            matrix.multiply(Vec3f.POSITIVE_Y.getRadialQuaternion(this.yaw));
        }
        if (this.pitch != 0.0f) {
            matrix.multiply(Vec3f.POSITIVE_X.getRadialQuaternion(this.pitch));
        }
    }

    private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        Matrix4f matrix4f = entry.getModel();
        Matrix3f matrix3f = entry.getNormal();
        for (Cuboid cuboid : this.cuboids) {
            for (Quad quad : cuboid.sides) {
                Vec3f vec3f = quad.direction.copy();
                vec3f.transform(matrix3f);
                float f = vec3f.getX();
                float g = vec3f.getY();
                float h = vec3f.getZ();
                for (int i = 0; i < 4; ++i) {
                    Vertex vertex = quad.vertices[i];
                    float j = vertex.pos.getX() / 16.0f;
                    float k = vertex.pos.getY() / 16.0f;
                    float l = vertex.pos.getZ() / 16.0f;
                    Vector4f vector4f = new Vector4f(j, k, l, 1.0f);
                    vector4f.transform(matrix4f);
                    vertexConsumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), red, green, blue, alpha, vertex.u, vertex.v, overlay, light, f, g, h);
                }
            }
        }
    }

    public ModelPart setTextureSize(int width, int height) {
        this.textureWidth = width;
        this.textureHeight = height;
        return this;
    }

    public Cuboid getRandomCuboid(Random random) {
        return (Cuboid)this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    @Environment(value=EnvType.CLIENT)
    static class Vertex {
        public final Vec3f pos;
        public final float u;
        public final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vec3f(x, y, z), u, v);
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        public Vertex(Vec3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Quad {
        public final Vertex[] vertices;
        public final Vec3f direction;

        public Quad(Vertex[] vertices, float u1, float v1, float u2, float v2, float squishU, float squishV, boolean flip, Direction direction) {
            this.vertices = vertices;
            float f = 0.0f / squishU;
            float g = 0.0f / squishV;
            vertices[0] = vertices[0].remap(u2 / squishU - f, v1 / squishV + g);
            vertices[1] = vertices[1].remap(u1 / squishU + f, v1 / squishV + g);
            vertices[2] = vertices[2].remap(u1 / squishU + f, v2 / squishV - g);
            vertices[3] = vertices[3].remap(u2 / squishU - f, v2 / squishV - g);
            if (flip) {
                int i = vertices.length;
                for (int j = 0; j < i / 2; ++j) {
                    Vertex vertex = vertices[j];
                    vertices[j] = vertices[i - 1 - j];
                    vertices[i - 1 - j] = vertex;
                }
            }
            this.direction = direction.getUnitVector();
            if (flip) {
                this.direction.multiplyComponentwise(-1.0f, 1.0f, 1.0f);
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

        public Cuboid(int u, int v, float x, float y, float z, float sizeX, float sizeY, float sizeZ, float extraX, float extraY, float extraZ, boolean mirror, float textureWidth, float textureHeight) {
            this.minX = x;
            this.minY = y;
            this.minZ = z;
            this.maxX = x + sizeX;
            this.maxY = y + sizeY;
            this.maxZ = z + sizeZ;
            this.sides = new Quad[6];
            float f = x + sizeX;
            float g = y + sizeY;
            float h = z + sizeZ;
            x -= extraX;
            y -= extraY;
            z -= extraZ;
            f += extraX;
            g += extraY;
            h += extraZ;
            if (mirror) {
                float i = f;
                f = x;
                x = i;
            }
            Vertex vertex = new Vertex(x, y, z, 0.0f, 0.0f);
            Vertex vertex2 = new Vertex(f, y, z, 0.0f, 8.0f);
            Vertex vertex3 = new Vertex(f, g, z, 8.0f, 8.0f);
            Vertex vertex4 = new Vertex(x, g, z, 8.0f, 0.0f);
            Vertex vertex5 = new Vertex(x, y, h, 0.0f, 0.0f);
            Vertex vertex6 = new Vertex(f, y, h, 0.0f, 8.0f);
            Vertex vertex7 = new Vertex(f, g, h, 8.0f, 8.0f);
            Vertex vertex8 = new Vertex(x, g, h, 8.0f, 0.0f);
            float j = u;
            float k = (float)u + sizeZ;
            float l = (float)u + sizeZ + sizeX;
            float m = (float)u + sizeZ + sizeX + sizeX;
            float n = (float)u + sizeZ + sizeX + sizeZ;
            float o = (float)u + sizeZ + sizeX + sizeZ + sizeX;
            float p = v;
            float q = (float)v + sizeZ;
            float r = (float)v + sizeZ + sizeY;
            this.sides[2] = new Quad(new Vertex[]{vertex6, vertex5, vertex, vertex2}, k, p, l, q, textureWidth, textureHeight, mirror, Direction.DOWN);
            this.sides[3] = new Quad(new Vertex[]{vertex3, vertex4, vertex8, vertex7}, l, q, m, p, textureWidth, textureHeight, mirror, Direction.UP);
            this.sides[1] = new Quad(new Vertex[]{vertex, vertex5, vertex8, vertex4}, j, q, k, r, textureWidth, textureHeight, mirror, Direction.WEST);
            this.sides[4] = new Quad(new Vertex[]{vertex2, vertex, vertex4, vertex3}, k, q, l, r, textureWidth, textureHeight, mirror, Direction.NORTH);
            this.sides[0] = new Quad(new Vertex[]{vertex6, vertex2, vertex3, vertex7}, l, q, n, r, textureWidth, textureHeight, mirror, Direction.EAST);
            this.sides[5] = new Quad(new Vertex[]{vertex5, vertex6, vertex7, vertex8}, n, q, o, r, textureWidth, textureHeight, mirror, Direction.SOUTH);
        }
    }
}

