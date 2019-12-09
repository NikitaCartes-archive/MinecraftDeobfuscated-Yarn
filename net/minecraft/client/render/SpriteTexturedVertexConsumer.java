/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.texture.Sprite;

@Environment(value=EnvType.CLIENT)
public class SpriteTexturedVertexConsumer
implements VertexConsumer {
    private final VertexConsumer parent;
    private final Sprite sprite;

    public SpriteTexturedVertexConsumer(VertexConsumer parent, Sprite sprite) {
        this.parent = parent;
        this.sprite = sprite;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z) {
        return this.parent.vertex(x, y, z);
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha) {
        return this.parent.color(red, green, blue, alpha);
    }

    @Override
    public VertexConsumer texture(float u, float v) {
        return this.parent.texture(this.sprite.getFrameU(u * 16.0f), this.sprite.getFrameV(v * 16.0f));
    }

    @Override
    public VertexConsumer overlay(int u, int v) {
        return this.parent.overlay(u, v);
    }

    @Override
    public VertexConsumer light(int u, int v) {
        return this.parent.light(u, v);
    }

    @Override
    public VertexConsumer normal(float x, float y, float z) {
        return this.parent.normal(x, y, z);
    }

    @Override
    public void next() {
        this.parent.next();
    }

    @Override
    public void vertex(float x, float y, float z, float red, float green, float blue, float alpha, float u, float v, int overlay, int light, float normalX, float normalY, float normalZ) {
        this.parent.vertex(x, y, z, red, green, blue, alpha, this.sprite.getFrameU(u * 16.0f), this.sprite.getFrameV(v * 16.0f), overlay, light, normalX, normalY, normalZ);
    }
}

