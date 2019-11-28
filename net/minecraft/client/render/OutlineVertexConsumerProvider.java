/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import java.util.Optional;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.BufferBuilder;
import net.minecraft.client.render.FixedColorVertexConsumer;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.VertexConsumers;

@Environment(value=EnvType.CLIENT)
public class OutlineVertexConsumerProvider
implements VertexConsumerProvider {
    private final VertexConsumerProvider.Immediate parent;
    private final VertexConsumerProvider.Immediate plainDrawer = VertexConsumerProvider.immediate(new BufferBuilder(256));
    private int red = 255;
    private int green = 255;
    private int blue = 255;
    private int alpha = 255;

    public OutlineVertexConsumerProvider(VertexConsumerProvider.Immediate immediate) {
        this.parent = immediate;
    }

    @Override
    public net.minecraft.client.render.VertexConsumer getBuffer(RenderLayer renderLayer) {
        net.minecraft.client.render.VertexConsumer vertexConsumer = this.parent.getBuffer(renderLayer);
        Optional<RenderLayer> optional = renderLayer.getTexture();
        if (optional.isPresent()) {
            net.minecraft.client.render.VertexConsumer vertexConsumer2 = this.plainDrawer.getBuffer(optional.get());
            VertexConsumer vertexConsumer3 = new VertexConsumer(vertexConsumer2, this.red, this.green, this.blue, this.alpha);
            return VertexConsumers.dual(vertexConsumer3, vertexConsumer);
        }
        return vertexConsumer;
    }

    public void setColor(int i, int j, int k, int l) {
        this.red = i;
        this.green = j;
        this.blue = k;
        this.alpha = l;
    }

    public void draw() {
        this.plainDrawer.draw();
    }

    @Environment(value=EnvType.CLIENT)
    static class VertexConsumer
    extends FixedColorVertexConsumer {
        private final net.minecraft.client.render.VertexConsumer delegate;
        private double x;
        private double y;
        private double z;
        private float u;
        private float v;

        private VertexConsumer(net.minecraft.client.render.VertexConsumer vertexConsumer, int i, int j, int k, int l) {
            this.delegate = vertexConsumer;
            super.fixedColor(i, j, k, l);
        }

        @Override
        public void fixedColor(int i, int j, int k, int l) {
        }

        @Override
        public net.minecraft.client.render.VertexConsumer vertex(double d, double e, double f) {
            this.x = d;
            this.y = e;
            this.z = f;
            return this;
        }

        @Override
        public net.minecraft.client.render.VertexConsumer color(int i, int j, int k, int l) {
            return this;
        }

        @Override
        public net.minecraft.client.render.VertexConsumer texture(float f, float g) {
            this.u = f;
            this.v = g;
            return this;
        }

        @Override
        public net.minecraft.client.render.VertexConsumer overlay(int i, int j) {
            return this;
        }

        @Override
        public net.minecraft.client.render.VertexConsumer light(int i, int j) {
            return this;
        }

        @Override
        public net.minecraft.client.render.VertexConsumer normal(float f, float g, float h) {
            return this;
        }

        @Override
        public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
            this.delegate.vertex(f, g, h).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(m, n).next();
        }

        @Override
        public void next() {
            this.delegate.vertex(this.x, this.y, this.z).color(this.fixedRed, this.fixedGreen, this.fixedBlue, this.fixedAlpha).texture(this.u, this.v).next();
        }
    }
}

