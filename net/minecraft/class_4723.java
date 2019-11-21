/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
public class class_4723
implements VertexConsumer {
    private final VertexConsumer field_21730;
    private final Sprite field_21731;

    public class_4723(VertexConsumer vertexConsumer, Sprite sprite) {
        this.field_21730 = vertexConsumer;
        this.field_21731 = sprite;
    }

    @Override
    public VertexConsumer vertex(double d, double e, double f) {
        return this.field_21730.vertex(d, e, f);
    }

    @Override
    public VertexConsumer color(int i, int j, int k, int l) {
        return this.field_21730.color(i, j, k, l);
    }

    @Override
    public VertexConsumer texture(float f, float g) {
        return this.field_21730.texture(this.field_21731.getFrameU(f * 16.0f), this.field_21731.getFrameV(g * 16.0f));
    }

    @Override
    public VertexConsumer overlay(int i, int j) {
        return this.field_21730.overlay(i, j);
    }

    @Override
    public VertexConsumer light(int i, int j) {
        return this.field_21730.light(i, j);
    }

    @Override
    public VertexConsumer normal(float f, float g, float h) {
        return this.field_21730.normal(f, g, h);
    }

    @Override
    public void next() {
        this.field_21730.next();
    }

    @Override
    public void method_23919(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
        this.field_21730.method_23919(f, g, h, i, j, k, l, this.field_21731.getFrameU(m * 16.0f), this.field_21731.getFrameV(n * 16.0f), o, p, q, r, s);
    }
}

