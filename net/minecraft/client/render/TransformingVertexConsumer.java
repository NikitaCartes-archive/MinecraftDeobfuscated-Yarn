/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.Matrix3f;
import net.minecraft.client.util.math.Matrix4f;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.client.util.math.Vector4f;
import net.minecraft.util.math.Direction;

@Environment(value=EnvType.CLIENT)
public class TransformingVertexConsumer
extends FixedColorVertexConsumer {
    private final VertexConsumer vertexConsumer;
    private final Matrix4f textureMatrix;
    private final Matrix3f normalMatrix;
    private float x;
    private float y;
    private float z;
    private int red;
    private int green;
    private int blue;
    private int alpha;
    private int u1;
    private int v1;
    private int light;
    private float normalX;
    private float normalY;
    private float normalZ;

    public TransformingVertexConsumer(VertexConsumer vertexConsumer, MatrixStack.Entry entry) {
        this.vertexConsumer = vertexConsumer;
        this.textureMatrix = entry.method_23761().copy();
        this.textureMatrix.invert();
        this.normalMatrix = entry.method_23762().copy();
        this.normalMatrix.method_23732();
        this.init();
    }

    private void init() {
        this.x = 0.0f;
        this.y = 0.0f;
        this.z = 0.0f;
        this.red = this.fixedRed;
        this.green = this.fixedGreen;
        this.blue = this.fixedBlue;
        this.alpha = this.fixedAlpha;
        this.u1 = 0;
        this.v1 = 10;
        this.light = 0xF000F0;
        this.normalX = 0.0f;
        this.normalY = 1.0f;
        this.normalZ = 0.0f;
    }

    @Override
    public void next() {
        Vector3f vector3f = new Vector3f(this.normalX, this.normalY, this.normalZ);
        vector3f.multiply(this.normalMatrix);
        Direction direction = Direction.getFacing(vector3f.getX(), vector3f.getY(), vector3f.getZ());
        Vector4f vector4f = new Vector4f(this.x, this.y, this.z, 1.0f);
        vector4f.multiply(this.textureMatrix);
        vector4f.method_23852(Vector3f.POSITIVE_Y.getRotationQuaternion(180.0f));
        vector4f.method_23852(Vector3f.POSITIVE_X.getRotationQuaternion(-90.0f));
        vector4f.method_23852(direction.getRotationQuaternion());
        float f = -vector4f.getX();
        float g = -vector4f.getY();
        this.vertexConsumer.vertex(this.x, this.y, this.z).color(this.red, this.green, this.blue, this.alpha).texture(f, g).overlay(this.u1, this.v1).light(this.light).normal(this.normalX, this.normalY, this.normalZ).next();
        this.init();
    }

    @Override
    public VertexConsumer vertex(double d, double e, double f) {
        this.x = (float)d;
        this.y = (float)e;
        this.z = (float)f;
        return this;
    }

    @Override
    public VertexConsumer color(int i, int j, int k, int l) {
        if (this.colorFixed) {
            throw new IllegalStateException();
        }
        this.red = i;
        this.green = j;
        this.blue = k;
        this.alpha = l;
        return this;
    }

    @Override
    public VertexConsumer texture(float f, float g) {
        return this;
    }

    @Override
    public VertexConsumer overlay(int i, int j) {
        this.u1 = i;
        this.v1 = j;
        return this;
    }

    @Override
    public VertexConsumer light(int i, int j) {
        this.light = i | j << 16;
        return this;
    }

    @Override
    public VertexConsumer normal(float f, float g, float h) {
        this.normalX = f;
        this.normalY = g;
        this.normalZ = h;
        return this;
    }
}

