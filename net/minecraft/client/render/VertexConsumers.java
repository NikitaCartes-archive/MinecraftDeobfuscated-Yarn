/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft.client.render;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;

@Environment(value=EnvType.CLIENT)
public class VertexConsumers {
    public static VertexConsumer dual(VertexConsumer vertexConsumer, VertexConsumer vertexConsumer2) {
        return new Dual(vertexConsumer, vertexConsumer2);
    }

    @Environment(value=EnvType.CLIENT)
    static class Dual
    implements VertexConsumer {
        private final VertexConsumer first;
        private final VertexConsumer second;

        public Dual(VertexConsumer vertexConsumer, VertexConsumer vertexConsumer2) {
            if (vertexConsumer == vertexConsumer2) {
                throw new IllegalArgumentException("Duplicate delegates");
            }
            this.first = vertexConsumer;
            this.second = vertexConsumer2;
        }

        @Override
        public VertexConsumer vertex(double d, double e, double f) {
            this.first.vertex(d, e, f);
            this.second.vertex(d, e, f);
            return this;
        }

        @Override
        public VertexConsumer color(int i, int j, int k, int l) {
            this.first.color(i, j, k, l);
            this.second.color(i, j, k, l);
            return this;
        }

        @Override
        public VertexConsumer texture(float f, float g) {
            this.first.texture(f, g);
            this.second.texture(f, g);
            return this;
        }

        @Override
        public VertexConsumer overlay(int i, int j) {
            this.first.overlay(i, j);
            this.second.overlay(i, j);
            return this;
        }

        @Override
        public VertexConsumer light(int i, int j) {
            this.first.light(i, j);
            this.second.light(i, j);
            return this;
        }

        @Override
        public VertexConsumer normal(float f, float g, float h) {
            this.first.normal(f, g, h);
            this.second.normal(f, g, h);
            return this;
        }

        @Override
        public void vertex(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
            this.first.vertex(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
            this.second.vertex(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
        }

        @Override
        public void next() {
            this.first.next();
            this.second.next();
        }
    }
}

