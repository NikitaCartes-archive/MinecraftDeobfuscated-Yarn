/*
 * Decompiled with CFR 0.2.0 (FabricMC d28b102d).
 */
package net.minecraft;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.VertexConsumer;

@Environment(value=EnvType.CLIENT)
public class class_4720 {
    public static VertexConsumer method_24037(VertexConsumer vertexConsumer, VertexConsumer vertexConsumer2) {
        return new class_4589(vertexConsumer, vertexConsumer2);
    }

    @Environment(value=EnvType.CLIENT)
    static class class_4589
    implements VertexConsumer {
        private final VertexConsumer field_21685;
        private final VertexConsumer field_21686;

        public class_4589(VertexConsumer vertexConsumer, VertexConsumer vertexConsumer2) {
            if (vertexConsumer == vertexConsumer2) {
                throw new IllegalArgumentException("Duplicate delegates");
            }
            this.field_21685 = vertexConsumer;
            this.field_21686 = vertexConsumer2;
        }

        @Override
        public VertexConsumer vertex(double d, double e, double f) {
            this.field_21685.vertex(d, e, f);
            this.field_21686.vertex(d, e, f);
            return this;
        }

        @Override
        public VertexConsumer color(int i, int j, int k, int l) {
            this.field_21685.color(i, j, k, l);
            this.field_21686.color(i, j, k, l);
            return this;
        }

        @Override
        public VertexConsumer texture(float f, float g) {
            this.field_21685.texture(f, g);
            this.field_21686.texture(f, g);
            return this;
        }

        @Override
        public VertexConsumer overlay(int i, int j) {
            this.field_21685.overlay(i, j);
            this.field_21686.overlay(i, j);
            return this;
        }

        @Override
        public VertexConsumer light(int i, int j) {
            this.field_21685.light(i, j);
            this.field_21686.light(i, j);
            return this;
        }

        @Override
        public VertexConsumer normal(float f, float g, float h) {
            this.field_21685.normal(f, g, h);
            this.field_21686.normal(f, g, h);
            return this;
        }

        @Override
        public void method_23919(float f, float g, float h, float i, float j, float k, float l, float m, float n, int o, int p, float q, float r, float s) {
            this.field_21685.method_23919(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
            this.field_21686.method_23919(f, g, h, i, j, k, l, m, n, o, p, q, r, s);
        }

        @Override
        public void next() {
            this.field_21685.next();
            this.field_21686.next();
        }
    }
}

