/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.model;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.stream.Stream;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.class_5603;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Matrix3f;
import net.minecraft.util.math.Matrix4f;

@Environment(value=EnvType.CLIENT)
public final class ModelPart {
    public float pivotX;
    public float pivotY;
    public float pivotZ;
    public float pitch;
    public float yaw;
    public float roll;
    public boolean visible = true;
    private final List<Cuboid> cuboids;
    private final Map<String, ModelPart> children;

    public ModelPart(List<Cuboid> list, Map<String, ModelPart> map) {
        this.cuboids = list;
        this.children = map;
    }

    public class_5603 method_32084() {
        return class_5603.method_32091(this.pivotX, this.pivotY, this.pivotZ, this.pitch, this.yaw, this.roll);
    }

    public void method_32085(class_5603 arg) {
        this.pivotX = arg.field_27702;
        this.pivotY = arg.field_27703;
        this.pivotZ = arg.field_27704;
        this.pitch = arg.field_27705;
        this.yaw = arg.field_27706;
        this.roll = arg.field_27707;
    }

    public void copyPositionAndRotation(ModelPart modelPart) {
        this.pitch = modelPart.pitch;
        this.yaw = modelPart.yaw;
        this.roll = modelPart.roll;
        this.pivotX = modelPart.pivotX;
        this.pivotY = modelPart.pivotY;
        this.pivotZ = modelPart.pivotZ;
    }

    public ModelPart method_32086(String string) {
        ModelPart modelPart = this.children.get(string);
        if (modelPart == null) {
            throw new NoSuchElementException("Can't find part " + string);
        }
        return modelPart;
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
        for (ModelPart modelPart : this.children.values()) {
            modelPart.render(matrices, vertices, light, overlay, red, green, blue, alpha);
        }
        matrices.pop();
    }

    public void rotate(MatrixStack matrix) {
        matrix.translate(this.pivotX / 16.0f, this.pivotY / 16.0f, this.pivotZ / 16.0f);
        if (this.roll != 0.0f) {
            matrix.multiply(Vector3f.POSITIVE_Z.getRadialQuaternion(this.roll));
        }
        if (this.yaw != 0.0f) {
            matrix.multiply(Vector3f.POSITIVE_Y.getRadialQuaternion(this.yaw));
        }
        if (this.pitch != 0.0f) {
            matrix.multiply(Vector3f.POSITIVE_X.getRadialQuaternion(this.pitch));
        }
    }

    private void renderCuboids(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int light, int overlay, float red, float green, float blue, float alpha) {
        for (Cuboid cuboid : this.cuboids) {
            cuboid.method_32089(entry, vertexConsumer, light, overlay, red, green, blue, alpha);
        }
    }

    public Cuboid getRandomCuboid(Random random) {
        return this.cuboids.get(random.nextInt(this.cuboids.size()));
    }

    public boolean method_32087() {
        return this.cuboids.isEmpty();
    }

    public Stream<ModelPart> method_32088() {
        return Stream.concat(Stream.of(this), this.children.values().stream().flatMap(ModelPart::method_32088));
    }

    @Environment(value=EnvType.CLIENT)
    static class Vertex {
        public final Vector3f pos;
        public final float u;
        public final float v;

        public Vertex(float x, float y, float z, float u, float v) {
            this(new Vector3f(x, y, z), u, v);
        }

        public Vertex remap(float u, float v) {
            return new Vertex(this.pos, u, v);
        }

        public Vertex(Vector3f pos, float u, float v) {
            this.pos = pos;
            this.u = u;
            this.v = v;
        }
    }

    @Environment(value=EnvType.CLIENT)
    static class Quad {
        public final Vertex[] vertices;
        public final Vector3f direction;

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

        public void method_32089(MatrixStack.Entry entry, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
            Matrix4f matrix4f = entry.getModel();
            Matrix3f matrix3f = entry.getNormal();
            for (Quad quad : this.sides) {
                Vector3f vector3f = quad.direction.copy();
                vector3f.transform(matrix3f);
                float l = vector3f.getX();
                float m = vector3f.getY();
                float n = vector3f.getZ();
                for (Vertex vertex : quad.vertices) {
                    float o = vertex.pos.getX() / 16.0f;
                    float p = vertex.pos.getY() / 16.0f;
                    float q = vertex.pos.getZ() / 16.0f;
                    Vector4f vector4f = new Vector4f(o, p, q, 1.0f);
                    vector4f.transform(matrix4f);
                    vertexConsumer.vertex(vector4f.getX(), vector4f.getY(), vector4f.getZ(), f, g, h, k, vertex.u, vertex.v, j, i, l, m, n);
                }
            }
        }
    }
}

